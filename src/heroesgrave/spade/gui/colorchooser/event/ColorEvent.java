// {LICENSE}
/*
 * Copyright 2013-2015 HeroesGrave and other Spade developers.
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

package heroesgrave.spade.gui.colorchooser.event;

import heroesgrave.spade.gui.colorchooser.Channel;

import java.util.EventObject;

/**
 * @author BurntPizza
 * 
 */
@SuppressWarnings("serial")
public class ColorEvent extends EventObject {
	
	public final int val;
	public final Channel channel;
	
	public ColorEvent(Object source, Channel channel, int val) {
		super(source);
		this.channel = channel;
		this.val = val;
	}
}
