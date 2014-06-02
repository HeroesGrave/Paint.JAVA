/*
 *	Copyright 2013 HeroesGrave and other Paint.JAVA developers.
 *
 *	This file is part of Paint.JAVA
 *
 *	Paint.JAVA is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package heroesgrave.paint.image.old.doc;

import heroesgrave.paint.gui.LayerManager.LayerNode;

public class MergeLayerOp extends DocumentChange
{
	private LayerNode src, dest;
	
	public MergeLayerOp(LayerNode src, LayerNode dest)
	{
		this.src = src;
		this.dest = dest;
	}
	
	public void apply()
	{
		dest.mergeNoChange(src);
	}
	
	public void revert()
	{
		dest.revertMerge(src);
	}
}