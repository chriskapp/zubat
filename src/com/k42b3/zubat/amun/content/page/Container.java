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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.k42b3.neodym.Service;
import com.k42b3.neodym.data.Endpoint;
import com.k42b3.zubat.basic.ViewPanel;
import com.k42b3.zubat.container.ContainerEvent;
import com.k42b3.zubat.container.ContainerEventListener;
import com.k42b3.zubat.container.ContainerLoadFinishedEvent;
import com.k42b3.zubat.container.ContainerRequestEditorEvent;
import com.k42b3.zubat.container.ContainerRequestLoadEvent;
import com.k42b3.zubat.container.ServiceAbstract;
import com.k42b3.zubat.model.Page;

/**
 * Container
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Container extends ServiceAbstract
{
	protected TreePanel treePanel;
	protected com.k42b3.zubat.basic.Container container;

	public Container(Endpoint api) throws Exception
	{
		super(api);

		container = new com.k42b3.zubat.basic.Container(api);

		// event handler
		addContainerListener(new ContainerSelfListener());

		container.addContainerListener(new ContainerChildListener());
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
		treePanel.getTree().addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					JTree tree = (JTree) e.getSource();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
					
					if(node != null)
					{
						Page page = (Page) node.getUserObject();

						if(page != null)
						{
							container.fireContainer(new ContainerRequestEditorEvent(page));
						}
					}
				}
			}

		});

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
	
	private class ContainerSelfListener implements ContainerEventListener
	{
		public void containerEvent(ContainerEvent event)
		{
			if(event instanceof ContainerLoadFinishedEvent)
			{
				// load tree
				treePanel.reload();
				
				// load container
				container.onLoad(fields);
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
				// hide specific columns
				JTabbedPane tp = (JTabbedPane) ((ContainerLoadFinishedEvent) event).getComponent();
				ViewPanel panel = (ViewPanel) tp.getComponent(0);

				JTable table = panel.getTable();
				table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
				table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
				table.getColumnModel().removeColumn(table.getColumnModel().getColumn(0));
			}
		}
	}
}
