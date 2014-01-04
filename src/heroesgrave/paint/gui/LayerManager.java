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
import heroesgrave.paint.image.Canvas;
import heroesgrave.paint.main.Paint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class LayerManager
{
	public class LayerManagerTreeCellRenderer implements TreeCellRenderer {
		JLabel label = new JLabel("Node");
		Icon nodeIcon;
		
		LayerManagerTreeCellRenderer()
		{
			BufferedImage img = new BufferedImage(16, 16,BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.getGraphics();
			g.setColor(Color.BLACK);
			g.fillOval(0, 0, 16, 16);
			nodeIcon = new ImageIcon(img);
			label.setMinimumSize(new Dimension(128, 16));
		}
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			
			if(value instanceof LayerNode)
			{
				LayerNode node = (LayerNode) value;
				label.setText(node.canvas.name);
				label.setIcon(nodeIcon);
				label.revalidate();
			}
			
			return label;
		}
		
	}
	
	public JDialog dialog;
	
	protected LayerNode rootNode;
	protected DefaultTreeModel model;
	protected JTree tree;
	protected JPanel controls;
	
	public LayerManager(Canvas root)
	{
		dialog = new CentredJDialog(Paint.main.gui.frame, "Layers");
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setTitle("Layers");
		dialog.getContentPane().setPreferredSize(new Dimension(200, 300));
		
		rootNode = new LayerNode(root);
		model = new DefaultTreeModel(rootNode);
		tree = new JTree(model);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().addTreeSelectionListener(new SelectionListener());
		tree.setVisibleRowCount(10);
		tree.setExpandsSelectedPaths(true);
		tree.setCellRenderer(new LayerManagerTreeCellRenderer());
		
		JScrollPane scroll = new JScrollPane(tree);
		
		controls = new JPanel();
		controls.setLayout(new GridLayout(0, 2));
		
		JButton newLayer = new JButton("New");
		JButton deleteLayer = new JButton("Delete");
		JButton moveUp = new JButton("Move Up");
		JButton moveDown = new JButton("Move Down");
		JButton properties = new JButton("Properties");
		
		newLayer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionModel().getSelectionPath();
				LayerNode n;
				if(path == null)
					n = rootNode;
				else
					n = (LayerNode) path.getLastPathComponent();
				n.createLayer();
			}
		});
		
		deleteLayer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionModel().getSelectionPath();
				LayerNode n;
				if(path == null)
					return;
				else
					n = (LayerNode) path.getLastPathComponent();
				if(n.equals(rootNode))
					return;
				n.delete();
			}
		});
		
		moveUp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionModel().getSelectionPath();
				LayerNode n;
				if(path == null)
					return;
				else
					n = (LayerNode) path.getLastPathComponent();
				if(n.equals(rootNode))
					return;
				LayerNode swap = (LayerNode) n.getPreviousSibling();
				LayerNode parent = (LayerNode) n.getParent();
				if(swap != null)
				{
					parent.canvas.swap(n.canvas, swap.canvas);
					int i = parent.getIndex(n);
					int j = parent.getIndex(swap);
					parent.remove(j);
					parent.insert(n, j);
					parent.insert(swap, i);
					model.reload();
					tree.setSelectionPath(new TreePath(n.getPath()));
				}
			}
		});
		
		moveDown.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionModel().getSelectionPath();
				LayerNode n;
				if(path == null)
					return;
				else
					n = (LayerNode) path.getLastPathComponent();
				if(n.equals(rootNode))
					return;
				LayerNode swap = (LayerNode) n.getNextSibling();
				LayerNode parent = (LayerNode) n.getParent();
				if(swap != null)
				{
					parent.canvas.swap(n.canvas, swap.canvas);
					int i = parent.getIndex(n);
					int j = parent.getIndex(swap);
					parent.remove(i);
					parent.insert(swap, i);
					parent.insert(n, j);
					model.reload();
					tree.setSelectionPath(new TreePath(n.getPath()));
				}
			}
		});
		
		properties.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TreePath path = tree.getSelectionModel().getSelectionPath();
				LayerNode n;
				
				if(path == null)
					n = rootNode;
				else
					n = (LayerNode) path.getLastPathComponent();
				
				LayerManager.this.openPropertiesDialog(n);
			}
		});
		
		controls.add(newLayer);
		controls.add(deleteLayer);
		controls.add(moveUp);
		controls.add(moveDown);
		controls.add(properties);
		controls.setVisible(false);
		
		dialog.setLayout(new BorderLayout());
		dialog.add(controls, BorderLayout.SOUTH);
		dialog.add(scroll, BorderLayout.NORTH);
		
		dialog.pack();
		dialog.setResizable(true);
	}
	
	protected void openPropertiesDialog(LayerNode n) {
		new LayerPropertiesDialog(this, n).show();
	}

	public void show()
	{
		dialog.setVisible(true);
	}
	
	public void hide()
	{
		dialog.setVisible(false);
	}
	
	public void toggle()
	{
		dialog.setVisible(!dialog.isVisible());
	}
	
	public void redrawTree() {
		// Capture the current state of the JTree node expansions.
		Vector<TreePath> paths = new Vector<TreePath>();
		
		Enumeration<TreePath> e = tree.getExpandedDescendants(new TreePath(rootNode));
		TreePath selpath = tree.getSelectionPath();
		
		if(e != null) while(e.hasMoreElements())
		{
			paths.addElement(e.nextElement());
		}
		
		// Force the JTree to rebuild itself.
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.reload();
		
		// Recover the old expanded state of the JTree.
		for (int i=0; i < paths.size(); i++) {
			TreePath path = (TreePath)paths.elementAt(i);
			tree.expandPath(path);
		}
		
		tree.setSelectionPath(selpath);
		
	}
	
	public boolean isVisible()
	{
		return dialog.isVisible();
	}
	
	public class LayerNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = -9016111201661580573L;
		
		public Canvas canvas;
		
		public LayerNode(Canvas canvas)
		{
			super(canvas.name);
			this.canvas = canvas;
			if(canvas.hasChildren())
			{
				for(Canvas c : canvas.getChildren())
				{
					this.add(new LayerNode(c));
				}
			}
		}
		
		public void createLayer()
		{
			Canvas canvas = new Canvas("New Layer", this.canvas.getWidth(), this.canvas.getHeight());
			LayerNode node = new LayerNode(canvas);
			this.add(node);
			this.canvas.addLayer(canvas);
			model.reload();
			tree.setSelectionPath(new TreePath(node.getPath()));
			Paint.main.gui.canvas.getPanel().repaint();
		}
		
		public void delete()
		{
			LayerNode n = (LayerNode) this.getParent();
			if(n != null)
			{
				n.remove(this);
				n.canvas.removeLayer(canvas);
				Paint.main.gui.layers.model.reload();
				Paint.main.gui.canvas.getPanel().repaint();
			}
		}
	}
	
	private class SelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			if(e.getNewLeadSelectionPath() == null)
			{
				Paint.main.gui.canvas.selectRoot();
				controls.setVisible(false);
				return;
			}
			LayerNode n = (LayerNode) e.getNewLeadSelectionPath().getLastPathComponent();
			if(n == null)
			{
				Paint.main.gui.canvas.selectRoot();
				controls.setVisible(false);
			}
			else
			{
				Paint.main.gui.canvas.select(n.canvas);
				controls.setVisible(true);
			}
			
		}
	}
	
	public JDialog getDialog()
	{
		return dialog;
	}
}