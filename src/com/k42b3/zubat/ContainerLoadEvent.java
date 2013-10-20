package com.k42b3.zubat;

import com.k42b3.neodym.ServiceItem;

public class ContainerLoadEvent implements ContainerEvent
{
	protected ServiceItem item;
	
	public ContainerLoadEvent(ServiceItem item)
	{
		this.item = item;
	}
	
	public ServiceItem getItem()
	{
		return item;
	}
}
