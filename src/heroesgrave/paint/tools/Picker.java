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

import heroesgrave.paint.gui.ToolMenu;
import heroesgrave.paint.main.Paint;

import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;

public class Picker extends Tool
{
	private JCheckBox switchPencil;
	
	public Picker(String name)
	{
		super(name);
		
		this.switchPencil = new JCheckBox("Switch to Pencil");
		menu.add(switchPencil);
		
		switchPencil.setFocusable(false);
	}
	
	public void onPressed(int x, int y, int button)
	{
		if(x < 0 || y < 0 || x >= Paint.main.gui.canvas.getImage().getWidth() || y >= Paint.main.gui.canvas.getImage().getHeight())
			return;
		
		if(button == MouseEvent.BUTTON1)
		{
			Paint.main.setLeftColour(Paint.main.gui.canvas.getImage().getRGB(x, y));
		}
		else if(button == MouseEvent.BUTTON3)
		{
			Paint.main.setRightColour(Paint.main.gui.canvas.getImage().getRGB(x, y));
		}
	}
	
	public void onReleased(int x, int y, int button)
	{
		if(x < 0 || y < 0 || x >= Paint.main.gui.canvas.getImage().getWidth() || y >= Paint.main.gui.canvas.getImage().getHeight())
			return;
		
		if(button == MouseEvent.BUTTON1)
		{
			Paint.main.setLeftColour(Paint.main.gui.canvas.getImage().getRGB(x, y));
		}
		else if(button == MouseEvent.BUTTON3)
		{
			Paint.main.setRightColour(Paint.main.gui.canvas.getImage().getRGB(x, y));
		}
		
		if(switchPencil.isSelected())
		{
			Paint.setTool(ToolMenu.DEF);
		}
	}
	
	public void whilePressed(int x, int y, int button)
	{
		if(x < 0 || y < 0 || x >= Paint.main.gui.canvas.getImage().getWidth() || y >= Paint.main.gui.canvas.getImage().getHeight())
			return;
		
		if(button == MouseEvent.BUTTON1)
		{
			Paint.main.setLeftColour(Paint.main.gui.canvas.getImage().getRGB(x, y));
		}
		else if(button == MouseEvent.BUTTON3)
		{
			Paint.main.setRightColour(Paint.main.gui.canvas.getImage().getRGB(x, y));
		}
	}
	
	public void whileReleased(int x, int y, int button)
	{
		
	}
}