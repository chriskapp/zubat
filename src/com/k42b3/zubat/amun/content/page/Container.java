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

package com.k42b3.zubat.amun.content.page;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.k42b3.neodym.ServiceItem;
import com.k42b3.zubat.ContainerEvent;
import com.k42b3.zubat.ContainerEventListener;
import com.k42b3.zubat.ContainerLoadEvent;
import com.k42b3.zubat.ModuleAbstract;
import com.k42b3.zubat.PageItem;
import com.k42b3.zubat.Zubat;

/**
 * Container
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Container extends ModuleAbstract
{
	protected TreePanel treePanel;
	protected com.k42b3.zubat.basic.Container container;

	public Container(ServiceItem item) throws Exception
	{
		super(item);

		container = new com.k42b3.zubat.basic.Container(item);
		
		// event handler
		container.addContainerListener(new ContainerEventListener() {

			public void containerEvent(ContainerEvent event)
			{
				if(event instanceof ContainerLoadEvent)
				{
					fireContainer(event);
				}
			}

		});
	}

	public String getTitle()
	{
		return "Page";
	}

	public JComponent getComponent()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		// tree
		treePanel = new TreePanel();
		treePanel.setPreferredSize(new Dimension(150, 100));
		treePanel.setMinimumSize(new Dimension(100, 100));

		panel.add(treePanel, BorderLayout.WEST);

		// container
		try
		{
			panel.add(container, BorderLayout.CENTER);
		}
		catch(Exception e)
		{
			panel.add(getExceptionPanel(e), BorderLayout.CENTER);
		}

		return panel;
	}

	protected void loadFinished(JComponent component, Exception lastException)
	{
		super.loadFinished(component, lastException);

		treePanel.reload();
		container.onLoad(fields);
	}

	private class TreeListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e) 
		{
			if(e.getNewLeadSelectionPath() != null)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

				if(node != null)
				{
					PageItem item = (PageItem) node.getUserObject();
					
					fireContainer(new ContainerLoadEvent(Zubat.getAvailableServices().getItem(item.getType())));
				}
			}
		}
	}
}
