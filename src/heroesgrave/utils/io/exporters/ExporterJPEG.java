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

package heroesgrave.utils.io.exporters;

import heroesgrave.utils.io.ImageExporter;
import heroesgrave.utils.io.ImageLoader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * JPEG Exporter.<br>
 * This exporter was made, because there was some kind of 'corruption' effect going on,
 * if the image had transparency. This special exporter fixes the corruption issue.
 * 
 * @author Longor1996
 **/
public class ExporterJPEG extends ImageExporter
{
	@Override
	public String getFileExtension()
	{
		return "jpeg";
	}
	
	@Override
	public void exportImage(BufferedImage imageIn, File destination)
	{
		// 'Color Corruption' Fix
		BufferedImage imageOut = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = imageOut.getGraphics();
		g.drawImage(imageIn, 0, 0, null);
		
		ImageLoader.writeImage(imageOut, getFileExtension().toUpperCase(), destination.getAbsolutePath());
	}
	
	@Override
	public String getFileExtensionDescription()
	{
		return "JPEG - JPEG File Interchange Format";
	}
	
}