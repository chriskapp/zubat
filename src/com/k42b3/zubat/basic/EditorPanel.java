package com.k42b3.zubat.basic;

import javax.swing.JComponent;

import com.k42b3.neodym.ServiceItem;
import com.k42b3.zubat.EditorAbstract;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.model.Page;

public class EditorPanel extends EditorAbstract
{
	protected ServiceItem item;
	protected com.k42b3.zubat.basic.Container container;
	
	public EditorPanel(Page page) throws Exception
	{
		super(page);
		
		// get service
		item = Zubat.getAvailableServices().getItem(page.getServiceType());

		if(item != null)
		{
			container = new com.k42b3.zubat.basic.Container(item);
		}
		else
		{
			throw new Exception("Service " + page.getServiceType() + " not found");
		}
	}

	public JComponent getComponent() throws Exception
	{
		return container.getComponent();
	}
}
