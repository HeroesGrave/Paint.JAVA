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

package heroesgrave.paint.gui;

import heroesgrave.paint.gui.Menu.CentredJDialog;
import heroesgrave.paint.main.Input;
import heroesgrave.paint.main.Paint;
import heroesgrave.paint.main.UserPreferences;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

public class GUIManager
{
	public JFrame frame;
	private JPanel panel, menus;
	private JMenuBar menuBar;
	private JScrollPane canvasZone;
	
	public CanvasManager canvas;
	public ColourChooser chooser;
	public InfoMenu info;
	
	private JMenuBar toolOptions;
	
	private Input input = new Input();
	
	public GUIManager()
	{
		/* Remove/Add Slash at the end of this line to switch between Nimbus L&F and the Default */
		String LAF_TO_USE = "Nimbus";
		
		// Check if the DlafClassName-property is avaible, and if so, use it's value as LAF name.
		if(System.getProperty("DlafClassName") != null)
		{
			LAF_TO_USE = System.getProperty("DlafClassName");
		}
		
		if(LAF_TO_USE.equalsIgnoreCase("system_default"))
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("[GUIManager] Trying to apply LAF '" + LAF_TO_USE + "'!");
			
			try
			{
				boolean success = false;
				
				for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				{
					if(info.getName().equals(LAF_TO_USE))
					{
						UIManager.setLookAndFeel(info.getClassName());
						System.out.println("[GUIManager] Successfully applied LAF '" + LAF_TO_USE + "'!");
						success = true;
						break;
					}
				}
				
				if(!success)
					throw new Exception("Failed to apply LAF! LAF not found: " + LAF_TO_USE);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
				System.out.println("Applying LAF failed. Printing all LAF names for correction:");
				for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				{
					System.out.println("LAF: " + info.getName() + " / " + info.getClassName());
				}
				
			}
		}
		
		/**/
		
		initFrame();
		initMenu();
		initInputs();
		createCanvas();
		finish();
		
		chooser = new ColourChooser();
	}
	
	public void setToolOption(JMenuBar options)
	{
		if(toolOptions != null)
		{
			menus.remove(toolOptions);
		}
		toolOptions = options;
		menus.add(toolOptions);
		menus.revalidate();
		menus.repaint();
	}
	
	public void setTitle(String title)
	{
		frame.setTitle(title);
	}
	
	public void createCanvas()
	{
		canvas = new CanvasManager();
		
		JPanel panel = new JPanel();
		panel.add(canvas.getCanvas());
		
		canvasZone = new JScrollPane(panel);
		canvasZone.addMouseWheelListener(input);
		canvasZone.getVerticalScrollBar().setUnitIncrement(16);
		canvasZone.getHorizontalScrollBar().setUnitIncrement(16);
		this.panel.add(canvasZone, BorderLayout.CENTER);
	}
	
	public void initMenu()
	{
		info = new InfoMenu();
		
		menus = new JPanel();
		menus.setLayout(new GridLayout(0, 1));
		
		menuBar = Menu.createMenuBar();
		JMenuBar infoBar = info.createInfoMenuBar();
		
		menus.add(menuBar);
		menus.add(infoBar);
		
		panel.add(menus, BorderLayout.NORTH);
	}
	
	public void setFile(File file)
	{
		frame.setTitle((file == null ? "Untitled" : file.getAbsolutePath()) + " - Paint.JAVA");
	}
	
	public void initFrame()
	{
		// Create the Frame
		frame = new JFrame("Untitled - Paint.JAVA");
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				displayCloseDialogue();
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		// Load the frames Icon. It looks a lot nicer with an actual logo. Remove if inappropriate.
		try
		{
			frame.setIconImage(ImageIO.read(this.getClass().getResource("/heroesgrave/paint/res/favicon.png")));
		}
		catch(IOException e1)
		{
			// Ignore the error if there is one! The logo doesn't matter so much as to crash the application.
		}
		
		heroesgrave.paint.plugin.PluginManager.instance.frameCreationEvent(frame);
		
		panel = (JPanel) frame.getContentPane();
		
		panel.setLayout(new BorderLayout());
	}
	
	public void displayCloseDialogue()
	{
		if(Paint.main.saved)
		{
			UserPreferences.savePrefs(frame);
			Paint.main.terminate = true;
			return;
		}
		
		// dialogue creation
		final JDialog close = new CentredJDialog();
		close.setTitle("Save before you quit?");
		close.setAlwaysOnTop(true);
		close.setAutoRequestFocus(true);
		close.setLayout(new BorderLayout());
		
		JButton save = new JButton("Save & Quit");
		JButton dispose = new JButton("Quit without saving");
		JButton cancel = new JButton("Don't Quit");
		
		// Init all the actions
		save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Paint.save();
				UserPreferences.savePrefs(frame);
				Paint.main.terminate = true;
				close.dispose();
			}
		});
		dispose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				UserPreferences.savePrefs(frame);
				Paint.main.terminate = true;
				close.dispose();
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				close.dispose();
			}
		});
		
		// Add the actions to the dialogue
		close.add(save, BorderLayout.NORTH);
		close.add(dispose, BorderLayout.CENTER);
		close.add(cancel, BorderLayout.SOUTH);
		
		// pack it, show it!
		close.pack();
		close.setResizable(false);
		close.setVisible(true);
	}
	
	/**
	 * Finishe's the GUI building process.
	 **/
	public void finish()
	{
		UserPreferences.loadPrefs(frame);
		frame.setVisible(true);
		frame.setResizable(true);
		frame.requestFocus();
	}
	
	public void initInputs()
	{
		Input in = new Input();
		
		frame.addKeyListener(in);
		frame.addMouseWheelListener(in);
	}
	
	public static final ImageIcon getIcon(String name)
	{
		String fullPath = "/heroesgrave/paint/res/icons/" + name + ".png";
		
		try
		{
			URL url = Paint.class.getResource(fullPath);
			
			if(url == null)
				throw new IOException("ImageIcon Not found: " + fullPath);
			
			return new ImageIcon(ImageIO.read(url));
		}
		catch(IOException e)
		{
			try
			{
				return new ImageIcon(ImageIO.read(Paint.questionMarkURL));
			}
			catch(IOException e1)
			{
				throw new RuntimeException("FATAL ERROR WHILE LOADING ICONIMAGE: " + name);
			}
		}
	}
}