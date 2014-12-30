// {LICENSE}
/*
 * Copyright 2013-2014 HeroesGrave and other Spade developers.
 * 
 * This file is part of Spade
 * 
 * Spade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package heroesgrave.spade.io;

import heroesgrave.spade.image.Document;
import heroesgrave.spade.image.Layer;
import heroesgrave.spade.image.RawImage;
import heroesgrave.spade.io.exporters.ExporterGenericImageIO;
import heroesgrave.spade.io.exporters.ImporterGenericImageIO;
import heroesgrave.spade.main.Popup;
import heroesgrave.utils.misc.Metadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

import com.alee.laf.filechooser.WebFileChooser;

public abstract class ImageImporter extends FileFilter
{
	private static final HashMap<String, ImageImporter> importers = new HashMap<String, ImageImporter>();
	
	static
	{
		// Set this to FALSE, because it is NOT faster to use this!
		ImageIO.setUseCache(false);
		
		// Default formats supported by the JRE: jpg, bmp, jpeg, png, gif
		add(new ImporterGenericImageIO("bmp", "BMP - Bitmap Image"));
		add(new ImporterGenericImageIO("gif", "GIF - Graphics Interchange Format"));
		add(new ImporterGenericImageIO("png", "PNG - Portable Network Graphics Image"));
		add(new ImporterGenericImageIO("jpg", "JPG - Joint Photographic Experts Group Image"));
		add(new ImporterGenericImageIO("jpeg", "JPEG - Joint Photographic Experts Group Image"));
	}
	
	/**
	 * Reads an Image.
	 * @throws IOException 
	 **/
	public abstract boolean load(File file, Document doc) throws IOException;
	
	public abstract String getFileExtension();
	
	@Override
	public boolean accept(File f)
	{
		if(f.isDirectory())
			return true;
		
		if(f.getAbsolutePath().endsWith("." + getFileExtension()))
			return true;
		
		return false;
	}
	
	public static void add(ImageImporter importer)
	{
		importers.put(importer.getFileExtension(), importer);
	}
	
	public static ImageImporter get(String extension)
	{
		return importers.get(extension);
	}
	
	public static boolean loadImage(String path, Document doc)
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
				
				// Attempt loading the Image trough ImageIO...
				{
					BufferedImage img = null;
					try
					{
						img = ImageIO.read(file);
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
					
					if(img != null)
					{
						RawImage image = RawImage.fromBufferedImage(img);
						doc.setDimensions(image.width, image.height);
						doc.setRoot(new Layer(doc, image, new Metadata()));
						return true;
					}
				}
				
				// We failed to load the image trough ImageIO, so now attempt to use a custom Importer...
				
				// Get the ImageImporter
				ImageImporter importer = importers.get(extension);
				
				// If there is a custom importer for the given format, use the custom importer...
				if(importer != null)
				{
					return importer.load(file, doc);
				}
				else
				{
					// We are unable to load the image.
					throw new IOException("Unable to load Image: There are no default/custom importers for the given format '"+extension+"'.");
				}
			}
			catch(Exception e)
			{
				Popup.showException("Error Loading Document", e, "This error occured while loading the file " + file
						+ ". It may work if you try again, but if not, we apologise. Report the bug to get it fixed as soon as possible");
				return false;
			}
		}
		else
		{
			Popup.showException("Error Loading Document", new FileNotFoundException(file.getAbsolutePath()), "The file " + file + " could not be found");
			return false;
		}
	}
	
	public static void addAllImporters(WebFileChooser chooser)
	{
		for(ImageImporter importer : importers.values())
		{
			chooser.addChoosableFileFilter(importer);
		}
	}
}
