package com.k42b3.zubat.model;

public class Page
{
	protected int id;
	protected String globalId;
	protected int serviceId;
	protected String title;
	protected String path;
	protected String serviceType;
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getGlobalId()
	{
		return globalId;
	}
	
	public void setGlobalId(String globalId)
	{
		this.globalId = globalId;
	}
	
	public int getServiceId()
	{
		return serviceId;
	}
	
	public void setServiceId(int serviceId)
	{
		this.serviceId = serviceId;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public String getServiceType()
	{
		return serviceType;
	}
	
	public void setServiceType(String serviceType)
	{
		this.serviceType = serviceType;
	}
}
