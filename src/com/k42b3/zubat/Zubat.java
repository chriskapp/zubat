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

package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.Service;
import com.k42b3.neodym.Services;
import com.k42b3.neodym.TrafficItem;
import com.k42b3.neodym.TrafficListenerInterface;
import com.k42b3.neodym.data.Endpoint;
import com.k42b3.neodym.oauth.Oauth;
import com.k42b3.neodym.oauth.OauthProvider;
import com.k42b3.zubat.container.ContainerEvent;
import com.k42b3.zubat.container.ContainerEventListener;
import com.k42b3.zubat.container.ContainerRequestLoadEvent;
import com.k42b3.zubat.container.ServiceAbstract;

/**
 * Zubat
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Zubat extends JFrame
{
	private static final long serialVersionUID = 1L;

	public static String version = "1.0.0";
	public static boolean debugMode;

	private static Http http;
	private static Account account;
	private static Services services;
	
	protected MenuPanel menuPanel;
	protected ContainerPanel containerPanel;

	protected TrafficPanel trafficPanel;
	protected TrafficTableModel trafficTm;

	protected Logger logger = Logger.getLogger("com.k42b3.zubat");

	public Zubat(boolean debugMode)
	{
		this.setTitle("zubat (version: " + Zubat.version + ")");
		this.setPreferredSize(new Dimension(820, 600));
		this.setMinimumSize(this.getSize());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		Zubat.debugMode = debugMode;

		try
		{
			this.doInitialize();

			// do authentication
			this.doAuthentication();

			// get account infos
			this.fetchAccount();

			// menu
			menuPanel = new MenuPanel(this);

			this.add(menuPanel, BorderLayout.NORTH);

			// container
			containerPanel = new ContainerPanel();
			containerPanel.setMinimumSize(new Dimension(400, 100));

			this.add(containerPanel, BorderLayout.CENTER);

			// traffic
			if(debugMode)
			{
				trafficPanel = new TrafficPanel(trafficTm);
				trafficPanel.setPreferredSize(new Dimension(600, 200));
				trafficPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

				this.add(trafficPanel, BorderLayout.SOUTH);
			}

			if(http.getOauth().isAuthed())
			{
				Service item = services.getService("http://ns.amun-project.org/2011/amun/service/content/page");

				if(item != null)
				{
					loadContainer(item);
				}
				else
				{
					throw new Exception("Could not find page service");
				}
			}
			else
			{
				throw new Exception("Not authenticated");
			}
		}
		catch(Exception e)
		{
			JPanel panel = new JPanel(new FlowLayout());
			panel.add(new JLabel(e.getMessage()));
			
			this.add(panel, BorderLayout.CENTER);

			Zubat.handleException(e);
		}
	}

	public Zubat()
	{
		this(false);
	}

	private void doInitialize()
	{
		if(debugMode)
		{
			// traffic model
			trafficTm = new TrafficTableModel();
			
			// http
			http = new Http(new TrafficListenerInterface(){

				public void handleRequest(TrafficItem item)
				{
					trafficTm.addTraffic(item);
				}

			});
		}
		else
		{
			http = new Http();
		}
	}

	private void fetchAccount() throws Exception
	{
		Service item = getServices().getService("http://ns.amun-project.org/2011/amun/service/my/verifyCredentials");

		if(item != null)
		{
			account = Account.buildAccount(http.requestXml(Http.GET, item.getUri()));
		}
		else
		{
			throw new Exception("Could not discover user informations");
		}
	}

	private void doAuthentication() throws Exception
	{
		// fetch services
		services = new Services(http, Configuration.getInstance().getBaseUrl());
		services.discover();

		// authentication
		String requestUrl = services.getService("http://oauth.net/core/1.0/endpoint/request").getUri();
		String authorizationUrl = services.getService("http://oauth.net/core/1.0/endpoint/authorize").getUri();
		String accessUrl = services.getService("http://oauth.net/core/1.0/endpoint/access").getUri();

		OauthProvider provider = new OauthProvider(requestUrl, authorizationUrl, accessUrl, Configuration.getInstance().getConsumerKey(), Configuration.getInstance().getConsumerSecret());
		Oauth oauth = new Oauth(http, provider);

		if(!Configuration.getInstance().getToken().isEmpty() && !Configuration.getInstance().getTokenSecret().isEmpty())
		{
			oauth.auth(Configuration.getInstance().getToken(), Configuration.getInstance().getTokenSecret());
		}
		else
		{
			throw new Exception("No token set");
		}

		http.setOauth(oauth);
	}

	public void loadContainer(Service service)
	{
		try
		{
			// get fields
			ArrayList<String> fields = Configuration.getFieldsForService(service);

			// load container
			ServiceAbstract module;
			String className = getClassNameFromType(service.getTypeStartsWith("http://ns.amun-project.org/2011/amun/service/"));

			// add component
			boolean found = false;

			for(int i = 0; i < containerPanel.getTabCount(); i++)
			{
				module = (ServiceAbstract) containerPanel.getComponent(i);
				
				if(module.getApi().getService().equals(service))
				{
					containerPanel.setSelectedIndex(i);

					found = true;
					break;
				}
			}

			if(!found)
			{
				// load class
				Endpoint api = new Endpoint(http, service);

				try
				{
					Class<?> container = Class.forName(className);

					module = (ServiceAbstract) container.getConstructor(Endpoint.class).newInstance(api);
				}
				catch(ClassNotFoundException e)
				{
					module = new com.k42b3.zubat.basic.Container(api);
				}

				logger.info("Load class " + module.getClass().getName());

				// add tab
				containerPanel.addTab(module.getTitle(), module);
				containerPanel.setSelectedIndex(containerPanel.getTabCount() - 1);
				
				// event handler
				module.addContainerListener(new ContainerEventListener() {
		
					public void containerEvent(ContainerEvent event)
					{
						if(event instanceof ContainerRequestLoadEvent)
						{
							loadContainer(((ContainerRequestLoadEvent) event).getItem());
						}
					}

				});
				
				// call onload
				module.onLoad(fields);
			}
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	public static Http getHttp()
	{
		return http;
	}

	public static Account getAccount()
	{
		return account;
	}

	public static Services getServices()
	{
		return services;
	}

	public static void handleException(Exception e)
	{
		Logger.getLogger("com.k42b3.zubat").warning(e.getMessage());

		if(debugMode)
		{
			e.printStackTrace();
		}
	}

	public static String getClassNameFromType(String type, String subType) throws Exception
	{
		String baseNs = "http://ns.amun-project.org/2011/amun/service/";

		if(!type.startsWith(baseNs))
		{
			throw new Exception("Type must be in amun namespace");
		}

		type = type.substring(baseNs.length());


		String[] parts = type.split("/");
		String className = "";

		for(int i = 0; i < parts.length; i++)
		{
			className+= parts[i] + ".";
		}

		if(className.isEmpty())
		{
			throw new Exception("Invalid type");
		}

		className = "com.k42b3.zubat.amun." + className + subType;

		return className;
	}
	
	public static String getClassNameFromType(String type) throws Exception
	{
		return getClassNameFromType(type, "Container");
	}
}
