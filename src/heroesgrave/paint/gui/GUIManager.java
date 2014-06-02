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

package heroesgrave.paint.gui;

import heroesgrave.paint.gui.Menu.CentredJDialog;
import heroesgrave.paint.main.Input;
import heroesgrave.paint.main.Paint;
import heroesgrave.paint.main.UserPreferences;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;

public class GUIManager
{
	public WebFrame frame;
	private JPanel panel;
	private WebPanel menus;
	public BackgroundPanel canvasPanel;
	private JComponent infoBar;
	private WebMenuBar menuBar;
	public WebScrollPane scroll;
	
	public ColourChooser chooser;
	public LayerManager layers;
	public InfoMenuBar info;
	public ToolBox toolBox;
	
	AboutDialog about;
	
	private JPanel toolOptions;
	
	public GUIManager()
	{
		// Check if the DlafClassName-property is avaible, and if so, use it's value as LAF name.
		if(System.getProperty("DlafClassName") != "")
		{
			String LAF_TO_USE = System.getProperty("DlafClassName");
			
			if(LAF_TO_USE.equalsIgnoreCase("system_default"))
			{
				try
				{
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					boolean success = false;
					
					for(LookAndFeelInfo info : UIManager
							.getInstalledLookAndFeels())
					{
						if(info.getName().equals(LAF_TO_USE))
						{
							UIManager.setLookAndFeel(info.getClassName());
							success = true;
							break;
						}
					}
					
					if(!success)
						throw new Exception(
								"Failed to apply LAF! LAF not found: "
										+ LAF_TO_USE);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					
					System.err
							.println("Applying LAF failed. Printing all LAF names for correction:");
					for(LookAndFeelInfo info : UIManager
							.getInstalledLookAndFeels())
					{
						System.err.println("LAF: " + info.getName() + " / "
								+ info.getClassName());
					}
					
				}
			}
		}
		else
		{
			WebLookAndFeel.install();
			WebLookAndFeel.setDecorateAllWindows(true);
		}
	}
	
	public void init()
	{
		initFrame();
		initMenu();
		initBGPanel();
		
		chooser = new ColourChooser(frame);
		layers = new LayerManager();
		about = new AboutDialog(frame);
		Paint.main.tools.toolbox = toolBox = new ToolBox();
		panel.add(toolBox.getToolbar(), BorderLayout.WEST);
		finish();
		
		initInputs();
	}
	
	public void setToolOption(JPanel options)
	{
		if(toolOptions != null)
		{
			info.getSpace().remove(toolOptions);
		}
		toolOptions = options;
		info.getSpace().add(toolOptions);
		menus.revalidate();
		menus.repaint();
	}
	
	public void setTitle(String title)
	{
		frame.setTitle(title);
	}
	
	public void setRenderer(Renderer r)
	{
		this.canvasPanel.setRenderer(r);
	}
	
	public void initBGPanel()
	{
		this.canvasPanel = new BackgroundPanel();
		
		scroll = new WebScrollPane(canvasPanel);
		scroll.removeMouseWheelListener(scroll.getMouseWheelListeners()[0]);
		scroll.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(final MouseWheelEvent e)
			{
				if(e.isControlDown())
				{
					if(e.getUnitsToScroll() > 0)
					{
						canvasPanel.decZoom();
					}
					else if(e.getUnitsToScroll() < 0)
					{
						canvasPanel.incZoom();
					}
				}
				else
				{
					if(e.isShiftDown())
					{
						// Horizontal scrolling
						Adjustable adj = scroll.getHorizontalScrollBar();
						int scroll =
								e.getUnitsToScroll() * adj.getBlockIncrement();
						adj.setValue(adj.getValue() + scroll);
					}
					else
					{
						// Vertical scrolling
						Adjustable adj = scroll.getVerticalScrollBar();
						int scroll =
								e.getUnitsToScroll() * adj.getBlockIncrement();
						adj.setValue(adj.getValue() + scroll);
					}
				}
			}
		});
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.getHorizontalScrollBar().setUnitIncrement(16);
		this.panel.add(scroll, BorderLayout.CENTER);
	}
	
	public void initMenu()
	{
		info = new InfoMenuBar();
		
		menus = new WebPanel();
		menus.setLayout(new BorderLayout());
		
		menuBar = Menu.createMenuBar();
		infoBar = info.createInfoMenuBar();
		infoBar.setVisible(false);
		
		menus.add(menuBar, BorderLayout.NORTH);
		menus.add(infoBar, BorderLayout.CENTER);
		
		panel.add(menus, BorderLayout.NORTH);
	}
	
	public void setFile(File file)
	{
		frame.setTitle((file == null ? "Untitled" : file.getAbsolutePath())
				+ " - Paint.JAVA");
	}
	
	public void initFrame()
	{
		// Create the Frame
		frame = new WebFrame("Untitled - Paint.JAVA");
		//ComponentMoveAdapter.install(frame.getRootPane(), frame);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				displayCloseDialogue();
			}
		});
		frame.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				Input.CTRL = false;
				Input.ALT = false;
				Input.SHIFT = false;
			}
			
			public void focusLost(FocusEvent e)
			{
				Input.CTRL = false;
				Input.ALT = false;
				Input.SHIFT = false;
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		// Load the frames Icon. It looks a lot nicer with an actual logo. Remove if inappropriate.
		try
		{
			frame.setIconImage(ImageIO.read(this.getClass().getResource(
					"/heroesgrave/paint/res/favicon.png")));
		}
		catch(IOException e1)
		{
			// Ignore the error if there is one! The logo doesn't matter so much as to crash the application.
		}
		
		heroesgrave.paint.plugin.PluginManager.instance
				.frameCreationEvent(frame);
		
		panel = (JPanel) frame.getContentPane();
		
		panel.setLayout(new BorderLayout());
	}
	
	public void displayCloseDialogue()
	{
		if(Paint.getDocument().saved)
		{
			UserPreferences.savePrefs(frame, chooser, layers, toolBox);
			Paint.main.terminate = true;
			return;
		}
		
		// dialogue creation
		final JDialog close =
				new CentredJDialog(frame, "Save before you quit?");
		close.setAlwaysOnTop(true);
		close.setAutoRequestFocus(true);
		close.setLayout(new BorderLayout());
		
		JButton save = new JButton("Save & Quit");
		JButton dispose = new JButton("Quit without saving");
		JButton cancel = new JButton("Don't Quit");
		
		// Init all the actions
		save.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Paint.save();
				UserPreferences.savePrefs(frame, chooser, layers, toolBox);
				Paint.main.terminate = true;
				close.dispose();
			}
		});
		dispose.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				UserPreferences.savePrefs(frame, chooser, layers, toolBox);
				Paint.main.terminate = true;
				close.dispose();
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			@Override
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
	 * Finishes the GUI building process.
	 **/
	public void finish()
	{
		frame.pack();
		UserPreferences.loadPrefs(frame, chooser, layers);
		frame.setVisible(true);
		frame.setResizable(true);
		frame.requestFocus();
	}
	
	public void initInputs()
	{
		Input in = new Input();
		
		frame.addKeyListener(in);
		chooser.getDialog().addKeyListener(in);
		layers.getDialog().addKeyListener(in);
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
				throw new RuntimeException(
						"FATAL ERROR WHILE LOADING ICONIMAGE: " + name);
			}
		}
	}
	
	public void revalidateBG()
	{
		canvasPanel.renderer.revalidateBG();
	}
	
	public void repaint()
	{
		canvasPanel.renderer.repaint();
	}
}