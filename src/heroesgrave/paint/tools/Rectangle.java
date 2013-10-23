/*
 *	Copyright 2013 HeroesGrave
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

package heroesgrave.paint.tools;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import heroesgrave.paint.main.Paint;
import heroesgrave.paint.main.ShapeChange;

public class Rectangle extends Tool
{
	private int sx, sy;
	private Rectangle2D.Float rectangle;
	private ShapeChange shapeChange;

	public Rectangle(String name)
	{
		super(name);
	}

	public void onPressed(int x, int y, int button)
	{
		sx = x;
		sy = y;
		rectangle = new Rectangle2D.Float(x, y, 1, 1);
        if(button == MouseEvent.BUTTON1) {
            shapeChange = new ShapeChange(rectangle, Paint.main.getLeftColour());
        }
        else if(button == MouseEvent.BUTTON3) {
            shapeChange = new ShapeChange(rectangle, Paint.main.getRightColour());
        }
        Paint.main.gui.canvas.preview(shapeChange);
	}

	public void onReleased(int x, int y, int button)
	{
        adjustRectangle(x, y);

        Paint.main.gui.canvas.applyPreview();
	}

	public void whilePressed(int x, int y, int button)
	{
		adjustRectangle(x, y);
        
        Paint.main.gui.canvas.preview(shapeChange);
	}

	public void whileReleased(int x, int y, int button)
	{

	}

	private void adjustRectangle(int x, int y) {
	    rectangle.width = Math.abs(x - sx);
	    rectangle.height = Math.abs(y - sy);
        if(x < sx) {
            rectangle.x = x;
        }
        else {
            rectangle.x = sx;
        }
        if(y < sy) {
            rectangle.y = y;
        }
        else {
            rectangle.y = sy;
        }
    }
}