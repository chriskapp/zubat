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

package com.k42b3.zubat.basic;

import java.util.ArrayList;

import javax.swing.JComponent;

import com.k42b3.neodym.Service;
import com.k42b3.neodym.data.Endpoint;
import com.k42b3.zubat.Configuration;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.container.ContainerEvent;
import com.k42b3.zubat.container.ContainerEventListener;
import com.k42b3.zubat.container.ContainerLoadFinishedEvent;
import com.k42b3.zubat.container.ContainerRequestLoadEvent;
import com.k42b3.zubat.container.EditorAbstract;
import com.k42b3.zubat.model.Page;

/**
 * EditorPanel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class EditorPanel extends EditorAbstract
{
	protected Endpoint api;
	protected com.k42b3.zubat.basic.Container container;
	
	public EditorPanel(Page page) throws Exception
	{
		super(page);

		// container
		container = new com.k42b3.zubat.basic.Container(page.getApi());
		
		// event handler
		addContainerListener(new ContainerSelfListener());

		container.addContainerListener(new ContainerChildListener());
	}

	public JComponent getComponent() throws Exception
	{
		return container;
	}

	private class ContainerSelfListener implements ContainerEventListener
	{
		public void containerEvent(ContainerEvent event)
		{
			if(event instanceof ContainerLoadFinishedEvent)
			{
				try
				{
					ArrayList<String> fields = Configuration.getFieldsForService(page.getApi().getService());

					container.onLoad(fields);
				}
				catch(Exception e)
				{
					Zubat.handleException(e);
				}
			}
		}
	}
	
	private class ContainerChildListener implements ContainerEventListener
	{
		public void containerEvent(ContainerEvent event)
		{
			if(event instanceof ContainerRequestLoadEvent)
			{
				fireContainer(event);
			}
			else if(event instanceof ContainerLoadFinishedEvent)
			{
			}
		}
	}
}
