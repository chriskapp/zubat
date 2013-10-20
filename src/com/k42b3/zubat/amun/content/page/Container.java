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
