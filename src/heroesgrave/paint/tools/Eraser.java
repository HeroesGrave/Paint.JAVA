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

import heroesgrave.paint.image.old.change.ShapeChange;
import heroesgrave.paint.main.Paint;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Eraser extends Tool
{
	private GeneralPath path;
	private ShapeChange change;
	private JSlider slider;
	private JLabel size;
	
	private Ellipse2D.Float previewCircle;
	private Stroke previewStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	public Eraser(String name)
	{
		super(name);
		slider = new JSlider(0, 16, 0);
		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				size.setText("Size: " + slider.getValue());
				previewCircle.width = previewCircle.height = slider.getValue();
			}
		});
		
		JLabel label = (JLabel) menu.getComponent(0);
		
		SpringLayout layout = new SpringLayout();
		menu.setLayout(layout);
		
		slider.setFocusable(false);
		
		size = new JLabel("Size: " + slider.getValue());
		
		menu.add(label);
		menu.add(size);
		menu.add(slider);
		
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, menu);
		layout.putConstraint(SpringLayout.WEST, size, 20, SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.WEST, slider, 20, SpringLayout.EAST, size);
		layout.putConstraint(SpringLayout.EAST, menu, 20, SpringLayout.EAST, slider);
		
		layout.putConstraint(SpringLayout.NORTH, slider, -3, SpringLayout.NORTH, menu);
		
		layout.putConstraint(SpringLayout.SOUTH, menu, 0, SpringLayout.SOUTH, label);
		
		previewCircle = new Ellipse2D.Float(0,0,slider.getValue(),slider.getValue());
	}
	
	@Override
	public void onPressed(int x, int y, int button)
	{
		path = new GeneralPath();
		path.moveTo(x, y);
		change = new ShapeChange(path, 0x000000, new BasicStroke(slider.getValue() + 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Paint.main.gui.canvas.preview(change);
		Paint.main.gui.canvas.getPanel().repaint();
	}
	
	public void onReleased(int x, int y, int button)
	{
		if(path != null)
		{
			path.lineTo(x, y);
		}
		Paint.main.gui.canvas.applyPreview();
		path = null;
		change = null;
	}
	
	public void whilePressed(int x, int y, int button)
	{
		if(path != null)
		{
			path.lineTo(x, y);
		}
		whileReleased(x, y, button);
		Paint.main.gui.canvas.getPanel().repaint();
	}
	
	@Override
	public void whileReleased(int x, int y, int button) {
		previewCircle.x = x - previewCircle.width / 2;
		previewCircle.y = y - previewCircle.width / 2;
		Paint.main.gui.canvas.getPanel().repaint();
	}
	
	@Override
	public void onSelect() {
		Paint.main.gui.canvas.getPanel().setCursorPreview(previewCircle, previewStroke);
	}
}