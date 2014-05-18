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
package experimental.colorchooser;

import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author BurntPizza
 *
 */
public class ColorChooser extends JDialog {
	
	PalletPanel palletPanel;
	ColorWheel colorWheel;
	
	
	public ColorChooser() {
		super();
		
	}
	
	
	/**
	 * Completely for testing
	 */
	public static void main(String[] a) {
		JFrame j = new JFrame();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		j.add(panel);
		
		PalletPanel pp = new PalletPanel(Pallet.defaultPallet());
		ColorWheel wheel = new ColorWheel();
		ColorSlider slider = new ColorSlider(Channel.Hue);
		
		slider.addColorListener(wheel);
		
		panel.add(pp);
		panel.add(wheel);
		panel.add(slider);
		
		j.pack();
		j.setLocationRelativeTo(null);
		j.setVisible(true);
	}
}
