// {LICENSE}
/*
 * Copyright 2013-2014 HeroesGrave and other Paint.JAVA developers.
 * 
 * This file is part of Paint.JAVA
 * 
 * Paint.JAVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package heroesgrave.paint.image;

import heroesgrave.paint.image.change.IChange;
import heroesgrave.paint.image.change.IEditChange;
import heroesgrave.paint.image.change.IImageChange;
import heroesgrave.paint.image.change.IRevEditChange;
import heroesgrave.paint.image.change.Marker;
import heroesgrave.paint.io.Serialised;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class FreezeBuffer
{
	private class OldBuffer
	{
		int order; // Also equals number of markers.
		RawImage image;
		
		OldBuffer(int order, RawImage image)
		{
			this.order = order;
			this.image = image;
		}
	}
	
	public static final int MAXIMUM = 16;
	public static final int MAXIMUM_ORDER = 8;
	
	private LinkedList<OldBuffer> oldBuffers = new LinkedList<OldBuffer>();
	private RawImage front, back;
	private BufferedImage image;
	private LinkedList<IChange> changes = new LinkedList<IChange>();
	private LinkedList<IChange> reverted = new LinkedList<IChange>();
	private LinkedList<Serialised> oldChanges = new LinkedList<Serialised>();
	private Serialised marker;
	private boolean rebuffer;
	
	public FreezeBuffer(RawImage image)
	{
		this.back = image;
		this.marker = new Marker();
		this.image = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB);
		this.front = RawImage.unwrapBufferedImage(this.image);
		this.front.copyFrom(this.back, false);
	}
	
	private void pushOldBuffer(OldBuffer buffer)
	{
		if(this.oldBuffers.isEmpty())
		{
			this.oldBuffers.push(buffer);
			return;
		}
		OldBuffer top = this.oldBuffers.pop();
		if(top.order == buffer.order && top.order != MAXIMUM_ORDER)
		{
			top.order *= 2;
			pushOldBuffer(top);
		}
		else
		{
			this.oldBuffers.push(top);
			this.oldBuffers.push(buffer);
		}
	}
	
	private boolean popOldBuffer()
	{
		if(oldBuffers.isEmpty())
		{
			marker = new Marker();
			return false;
		}
		OldBuffer top = this.oldBuffers.peek();
		if(top.order == 1)
		{
			oldBuffers.pop();
			this.back = top.image;
			Serialised s;
			while((s = oldChanges.poll()) != null)
			{
				if(s.isMarker())
				{
					this.marker = s;
					break;
				}
				else
				{
					this.changes.addFirst(s.decode());
				}
			}
			rebuffer = true;
			return true;
		}
		else
		{
			int i = top.order;
			LinkedList<Serialised> toReturn = new LinkedList<Serialised>();
			Serialised s;
			while(i * 2 > top.order)
			{
				if((s = oldChanges.poll()) == null)
				{
					throw new IllegalStateException("Not enough markers");
				}
				else if(s.isMarker())
				{
					i--;
					if(i * 2 == top.order && !(s instanceof Marker))
					{
						oldChanges.push(s);
						break;
					}
				}
				toReturn.push(s);
			}
			RawImage image = RawImage.copyOf(top.image);
			LinkedList<IChange> toApply = new LinkedList<IChange>();
			while(i > 0)
			{
				if((s = oldChanges.poll()) == null)
				{
					throw new IllegalStateException("Not enough markers");
				}
				else if(s.isMarker())
				{
					i--;
				}
				toReturn.push(s);
				if(!(s instanceof Marker))
				{
					toApply.push(s.decode());
				}
			}
			for(IChange c : toApply)
			{
				if(c instanceof IImageChange)
				{
					image = ((IImageChange) c).apply(image);
				}
				else if(c instanceof IEditChange)
				{
					((IEditChange) c).apply(image);
				}
			}
			top.order /= 2;
			this.oldBuffers.push(new OldBuffer(top.order, image));
			while(!toReturn.isEmpty())
			{
				this.oldChanges.push(toReturn.pop());
			}
			return popOldBuffer();
		}
	}
	
	public void addChange(IChange change)
	{
		if(change instanceof IEditChange)
		{
			changes.addLast(change);
			checkBuffered();
			((IEditChange) change).apply(front);
			if(changes.size() >= MAXIMUM)
			{
				pushOldBuffer(new OldBuffer(1, back));
				
				this.back = RawImage.copyOf(this.front);
				
				oldChanges.push(marker);
				marker = new Marker();
				while(!changes.isEmpty())
					oldChanges.push(changes.removeFirst().encode());
			}
		}
		else if(change instanceof IImageChange)
		{
			checkBuffered();
			pushOldBuffer(new OldBuffer(1, back));
			
			this.back = ((IImageChange) change).apply(RawImage.copyOf(front));
			front.copyFrom(back, true);
			
			oldChanges.push(marker);
			marker = change.encode();
			while(!changes.isEmpty())
				oldChanges.push(changes.removeFirst().encode());
		}
	}
	
	public IChange revertChange()
	{
		if(changes.isEmpty())
		{
			if(!(marker instanceof Marker))
			{
				IChange ret = marker.decode();
				reverted.push(ret);
				popOldBuffer();
				return ret;
			}
			
			this.front.copyFrom(this.back, false);
			rebuffer = true;
			
			if(!popOldBuffer())
				return null;
		}
		IChange change = changes.pollLast();
		reverted.push(change);
		if(change instanceof IEditChange)
		{
			if(change instanceof IRevEditChange)
				((IRevEditChange) change).revert(front);
			else
				rebuffer = true;
		}
		return change;
	}
	
	public IChange repeatChange()
	{
		if(reverted.isEmpty())
		{
			return null;
		}
		IChange change = reverted.pop();
		addChange(change);
		return change;
	}
	
	public void rebuffer()
	{
		front.copyFrom(back, true);
		for(IChange c : changes)
		{
			((IEditChange) c).apply(front);
		}
		rebuffer = false;
	}
	
	public void checkBuffered()
	{
		if(rebuffer)
			rebuffer();
	}
	
	public BufferedImage getFront()
	{
		checkBuffered();
		return image;
	}
	
	public RawImage getImage()
	{
		return front;
	}
}
