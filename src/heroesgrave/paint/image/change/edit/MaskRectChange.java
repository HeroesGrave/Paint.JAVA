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

package heroesgrave.paint.image.change.edit;

import heroesgrave.paint.image.RawImage;
import heroesgrave.paint.image.RawImage.MaskMode;
import heroesgrave.paint.image.change.IMaskChange;
import heroesgrave.paint.io.Serialised;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MaskRectChange implements IMaskChange, Serialised
{
	private short x1, y1, x2, y2;
	private MaskMode mode;
	
	public MaskRectChange(short x1, short y1, short x2, short y2, MaskMode mode)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.mode = mode;
	}
	
	public boolean moveTo(short x, short y)
	{
		if(x2 == x && y2 == y)
			return false;
		this.x2 = x;
		this.y2 = y;
		return true;
	}
	
	@Override
	public void apply(RawImage image)
	{
		image.setMaskEnabled(true);
		image.maskRect(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), mode);
	}
	
	@Override
	public MaskRectChange encode()
	{
		return this;
	}
	
	@Override
	public MaskRectChange decode()
	{
		return this;
	}
	
	@Override
	public void write(DataOutputStream out) throws IOException
	{
		out.writeInt(mode.ordinal());
		out.writeShort(x1);
		out.writeShort(y1);
		out.writeShort(x2);
		out.writeShort(y2);
	}
	
	@Override
	public void read(DataInputStream in) throws IOException
	{
		int mode = in.readInt();
		for(MaskMode m : MaskMode.values())
		{
			if(m.ordinal() == mode)
			{
				this.mode = m;
				break;
			}
		}
		x1 = in.readShort();
		y1 = in.readShort();
		x2 = in.readShort();
		y2 = in.readShort();
	}
	
	@Override
	public boolean isMarker()
	{
		return false;
	}
}