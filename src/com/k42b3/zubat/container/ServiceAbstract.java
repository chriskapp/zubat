/**
 * zubat
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011-2013 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of zubat. zubat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * zubat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with zubat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.zubat.container;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.k42b3.neodym.ServiceItem;

/**
 * ServiceAbstract
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
abstract public class ServiceAbstract extends ModuleAbstract
{
	protected ServiceItem item;
	
	public ServiceAbstract(ServiceItem item) throws Exception
	{
		this.item = item;

		// check whether we have an data type
		if(item.hasType("http://ns.amun-project.org/2011/amun/data/1.0"))
		{
			//throw new Exception("Service is not an data type");
		}

		// loading panel
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Loading ..."));

		this.add(panel, BorderLayout.CENTER);
	}
	
	public ServiceItem getItem()
	{
		return item;
	}
}
