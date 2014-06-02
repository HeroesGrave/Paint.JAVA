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

package heroesgrave.paint.tools.old;

import heroesgrave.paint.image.old.accurate.EllipseChange;
import heroesgrave.paint.image.old.change.Frame;
import heroesgrave.paint.image.old.change.ShapeChange;
import heroesgrave.paint.main.Input;
import heroesgrave.paint.main.Paint;

import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

public class Ellipse extends Tool
{
	private int sx, sy;
	private Ellipse2D.Float ellipse;
	private Frame change;
	private JCheckBox fill;
	private JCheckBox antialias;
	
	public Ellipse(String name)
	{
		super(name);
		
		fill = new JCheckBox("Fill Shape");
		antialias = new JCheckBox("Antialiasing");
		
		fill.setFocusable(false);
		antialias.setFocusable(false);
		
		JLabel label = (JLabel) menu.getComponent(0);
		
		SpringLayout layout = new SpringLayout();
		menu.setLayout(layout);
		
		menu.add(label);
		menu.add(fill);
		menu.add(antialias);
		
		// top/bottom
		layout.putConstraint(SpringLayout.NORTH, fill, -2, SpringLayout.NORTH, menu);
		layout.putConstraint(SpringLayout.NORTH, antialias, -2, SpringLayout.NORTH, menu);
		layout.putConstraint(SpringLayout.SOUTH, menu, 0, SpringLayout.SOUTH, label);
		
		// left/right
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, menu);
		layout.putConstraint(SpringLayout.WEST, fill, 20, SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.WEST, antialias, 20, SpringLayout.EAST, fill);
		layout.putConstraint(SpringLayout.EAST, menu, 20, SpringLayout.EAST, antialias);
	}
	
	public void onPressed(int x, int y, int button)
	{
		sx = x;
		sy = y;
		if(fill.isSelected() || antialias.isSelected())
		{
			ellipse = new Ellipse2D.Float(x, y, 0, 0);
			if(button == MouseEvent.BUTTON1)
			{
				change = new ShapeChange(ellipse, Paint.main.getLeftColour()).setFill(fill.isSelected()).setAntialiasing(antialias.isSelected());
			}
			else if(button == MouseEvent.BUTTON3)
			{
				change = new ShapeChange(ellipse, Paint.main.getRightColour()).setFill(fill.isSelected()).setAntialiasing(antialias.isSelected());
			}
		}
		else
		{
			change = new EllipseChange(x, y, button);
		}
		Paint.main.gui.canvas.preview(change);
	}
	
	public void onReleased(int x, int y, int button)
	{
		if(fill.isSelected() || antialias.isSelected())
			adjustEllipse(x, y);
		else
			((EllipseChange) change).change(x, y);
		Paint.main.gui.canvas.applyPreview();
	}
	
	public void whilePressed(int x, int y, int button)
	{
		if(fill.isSelected() || antialias.isSelected())
			adjustEllipse(x, y);
		else
			((EllipseChange) change).change(x, y);
		Paint.main.gui.canvas.getPanel().repaint();
	}
	
	private void adjustEllipse(int x, int y)
	{
		if(Input.CTRL)
		{
			int w = x - sx;
			int h = y - sy;
			if(Math.abs(w) > Math.abs(h))
			{
				int r = Math.abs(w);
				h = sign(h) * r;
			}
			else
			{
				int r = Math.abs(h);
				w = sign(w) * r;
			}
			x = sx + w;
			y = sy + h;
		}
		ellipse.width = Math.abs(x - sx) * 2;
		ellipse.height = Math.abs(y - sy) * 2;
		ellipse.x = sx - ellipse.width / 2;
		ellipse.y = sy - ellipse.height / 2;
	}
	
	private int sign(int i)
	{
		if(i < 0)
			return -1;
		else if(i > 0)
			return 1;
		return 0;
	}
}