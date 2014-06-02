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

package heroesgrave.paint.io;

import heroesgrave.paint.image.Document;
import heroesgrave.paint.image.Layer;
import heroesgrave.paint.io.importers.ImporterBIN;
import heroesgrave.paint.io.importers.ImporterZipBIN;
import heroesgrave.utils.misc.Metadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public abstract class ImageImporter extends FileFilter
{
	private static final HashMap<String, ImageImporter> importers =
			new HashMap<String, ImageImporter>();
	
	static
	{
		// Set this to FALSE, because it is NOT faster to use this!
		ImageIO.setUseCache(false);
		
		/// initialize importers?
		
		// Nothing here yet! Feel free to expand by adding new image-importers!
		add(new ImporterBIN());
		add(new ImporterZipBIN());
		//add(new ImporterLBIN());
		//add(new ImporterZipLBIN());
	}
	
	public static void add(ImageImporter exporter)
	{
		importers.put(exporter.getFormat(), exporter);
	}
	
	public static void loadImage(String path, Document doc)
	{
		File file = new File(path);
		
		if(file.exists())
		{
			try
			{
				String fileName = file.getAbsolutePath();
				String extension = "";
				
				int i = fileName.lastIndexOf('.');
				
				if(i > 0)
				{
					extension = fileName.substring(i + 1);
				}
				
				// Get the ImageImporter
				ImageImporter importer = importers.get(extension);
				
				// If there is a custom importer for the given format, use the custom importer...
				if(importer != null)
				{
					importer.read(file, doc);
				}
				else
				{
					BufferedImage image = ImageIO.read(file);
					doc.setDimensions(image.getWidth(), image.getHeight());
					doc.setRoot(new Layer(doc, image, new Metadata()));
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
			throw new RuntimeException("The resource \"" + path
					+ "\" was missing!");
	}
	
	public static void addAllImporters(JFileChooser chooser)
	{
		for(Entry<String, ImageImporter> importer : importers.entrySet())
		{
			chooser.addChoosableFileFilter(importer.getValue());
		}
	}
	
	/**
	 * Reads an Image.
	 * @throws IOException 
	 **/
	public abstract void read(File file, Document doc) throws IOException;
	
	public abstract String getFormat();
	
	@Override
	public boolean accept(File f)
	{
		if(f.isDirectory())
			return true;
		
		if(f.getAbsolutePath().endsWith("." + getFormat()))
			return true;
		
		return false;
	}
	
	@Override
	public abstract String getDescription();
	
}