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

package heroesgrave.paint.main;

import heroesgrave.paint.gui.Menu;
import heroesgrave.paint.tools.Tool;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class Input implements KeyListener
{
	public static boolean CTRL, SHIFT, ALT;
	public static Robot robot;
	
	private static HashMap<Integer, String> keyCodeToStr =
			new HashMap<Integer, String>();
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			CTRL = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			SHIFT = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_ALT)
		{
			ALT = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_F5)
		{
			Paint.main.gui.chooser.toggle();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_F6)
		{
			Paint.main.gui.layers.toggle();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			//Canvas selection = Paint.main.gui.canvas.selection.getSelection();
			//Paint.main.history.addChange(new DeleteSelectionOp(selection, Paint.main.gui.canvas.getParentOf(selection)));
		}
		
		int MOVE = 1;
		
		if(e.isControlDown())
		{
			MOVE = 8;
			if(e.isShiftDown())
			{
				if(keyCodeToStr.containsKey(e.getKeyCode()))
				{
					/*
					ImageOp op = Paint.getImageOp(keyCodeToStr.get(e.getKeyCode()).toLowerCase());
					if(op != null)
					{
						op.operation();
					}
					*/
				}
			}
			else
			{
				if(e.getKeyCode() == KeyEvent.VK_Z)
				{
					Paint.getDocument().getHistory().revertChange();
					Paint.main.gui.repaint();
				}
				else if(e.getKeyCode() == KeyEvent.VK_Y)
				{
					Paint.getDocument().getHistory().repeatChange();
					Paint.main.gui.repaint();
				}
				else if(e.getKeyCode() == KeyEvent.VK_EQUALS)
				{
					Paint.main.gui.canvasPanel.incZoom();
				}
				else if(e.getKeyCode() == KeyEvent.VK_MINUS)
				{
					Paint.main.gui.canvasPanel.decZoom();
				}
				else if(e.getKeyCode() == KeyEvent.VK_S)
				{
					Paint.save();
				}
				else if(e.getKeyCode() == KeyEvent.VK_N)
				{
					Menu.showNewMenu();
				}
				else if(e.getKeyCode() == KeyEvent.VK_O)
				{
					Menu.showOpenMenu();
				}
				else if(e.getKeyCode() == KeyEvent.VK_G)
				{
					Menu.GRID_ENABLED = !Menu.GRID_ENABLED;
					Paint.main.gui.repaint();
				}
				else if(e.getKeyCode() == KeyEvent.VK_B)
				{
					Menu.DARK_BACKGROUND = !Menu.DARK_BACKGROUND;
					Paint.main.gui.frame.repaint();
					Paint.main.gui.revalidateBG();
				}
				else if(e.getKeyCode() == KeyEvent.VK_D)
				{
					//Paint.main.gui.canvas.selection.drop();
				}
				else if(e.getKeyCode() == KeyEvent.VK_A)
				{
					/*
					Paint.main.gui.canvas.selection.create(
							new Rectangle2D.Float(0, 0, Paint.main.gui.canvas
									.getWidth(), Paint.main.gui.canvas
									.getHeight()), CombineMode.REPLACE);
					*/
				}
				else if(e.getKeyCode() == KeyEvent.VK_C)
				{
					/*
					SelectionCanvas sel =
							Paint.main.gui.canvas.selection.getSelection();
					if(sel != null)
						ClipboardHandler.copy(sel.getBoundedSelection());
					*/
				}
				else if(e.getKeyCode() == KeyEvent.VK_X)
				{
					/*
					SelectionCanvas sel =
							Paint.main.gui.canvas.selection.getSelection();
					if(sel != null)
					{
						ClipboardHandler.copy(sel.getBoundedSelection());
						Paint.main.history.addChange(new DeleteSelectionOp(sel,
								Paint.main.gui.canvas.getParentOf(sel)));
					}
					*/
				}
				else if(e.getKeyCode() == KeyEvent.VK_V)
				{
					/*
					BufferedImage image = ClipboardHandler.paste();
					if(image != null)
						Paint.main.gui.canvas.selection.paste(image);
					*/
				}
			}
		}
		else
		{
			if(keyCodeToStr.containsKey(e.getKeyCode()))
			{
				Tool tool =
						Paint.getTool(keyCodeToStr.get(e.getKeyCode())
								.toLowerCase());
				if(tool != null)
				{
					Paint.setTool(tool);
				}
			}
		}
		Point p = MouseInfo.getPointerInfo().getLocation();
		
		//MOVE = (int) (MOVE * Paint.main.gui.canvas.getScale());
		
		if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			robot.mouseMove(p.x, p.y - MOVE);
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			robot.mouseMove(p.x, p.y + MOVE);
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			robot.mouseMove(p.x - MOVE, p.y);
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			robot.mouseMove(p.x + MOVE, p.y);
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			CTRL = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			SHIFT = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_ALT)
		{
			ALT = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	static
	{
		keyCodeToStr.put(KeyEvent.VK_A, "A");
		keyCodeToStr.put(KeyEvent.VK_B, "B");
		keyCodeToStr.put(KeyEvent.VK_C, "C");
		keyCodeToStr.put(KeyEvent.VK_D, "D");
		keyCodeToStr.put(KeyEvent.VK_E, "E");
		keyCodeToStr.put(KeyEvent.VK_F, "F");
		keyCodeToStr.put(KeyEvent.VK_G, "G");
		keyCodeToStr.put(KeyEvent.VK_H, "H");
		keyCodeToStr.put(KeyEvent.VK_I, "I");
		keyCodeToStr.put(KeyEvent.VK_J, "J");
		keyCodeToStr.put(KeyEvent.VK_K, "K");
		keyCodeToStr.put(KeyEvent.VK_L, "L");
		keyCodeToStr.put(KeyEvent.VK_M, "M");
		keyCodeToStr.put(KeyEvent.VK_N, "N");
		keyCodeToStr.put(KeyEvent.VK_O, "O");
		keyCodeToStr.put(KeyEvent.VK_P, "P");
		keyCodeToStr.put(KeyEvent.VK_Q, "Q");
		keyCodeToStr.put(KeyEvent.VK_R, "R");
		keyCodeToStr.put(KeyEvent.VK_S, "S");
		keyCodeToStr.put(KeyEvent.VK_T, "T");
		keyCodeToStr.put(KeyEvent.VK_U, "U");
		keyCodeToStr.put(KeyEvent.VK_V, "V");
		keyCodeToStr.put(KeyEvent.VK_W, "W");
		keyCodeToStr.put(KeyEvent.VK_X, "X");
		keyCodeToStr.put(KeyEvent.VK_Y, "Y");
		keyCodeToStr.put(KeyEvent.VK_Z, "Z");
		
		try
		{
			robot = new Robot();
		}
		catch(AWTException e)
		{
			e.printStackTrace();
		}
	}
}