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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.k42b3.neodym.Service;

/**
 * Configuration
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Configuration 
{
	private static Configuration instance;
	private static File file;

	private String baseUrl;
	private String consumerKey;
	private String consumerSecret;
	private String token;
	private String tokenSecret;
	private HashMap<String, ArrayList<String>> services;

	private Configuration()
	{
	}

	public String getBaseUrl() 
	{
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) 
	{
		this.baseUrl = baseUrl;
	}

	public String getConsumerKey() 
	{
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) 
	{
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() 
	{
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) 
	{
		this.consumerSecret = consumerSecret;
	}

	public String getToken() 
	{
		return token;
	}

	public void setToken(String token) 
	{
		this.token = token;
	}

	public String getTokenSecret() 
	{
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) 
	{
		this.tokenSecret = tokenSecret;
	}

	public HashMap<String, ArrayList<String>> getServices() 
	{
		return services;
	}

	public void setServices(HashMap<String, ArrayList<String>> services) 
	{
		this.services = services;
	}

	public boolean hasToken()
	{
		return !getToken().isEmpty() && !getTokenSecret().isEmpty();
	}

	public static Configuration initInstance(File configFile) throws Exception
	{
		instance = new Configuration();
		file = configFile;

		// load dom
		Document doc = Configuration.loadDocument();

		// parse config elements
		String baseUrl = "";
		String consumerKey = "";
		String consumerSecret = "";
		String token = "";
		String tokenSecret = "";

		Element baseUrlElement = (Element) doc.getElementsByTagName("baseUrl").item(0);
		Element consumerKeyElement = (Element) doc.getElementsByTagName("consumerKey").item(0);
		Element consumerSecretElement = (Element) doc.getElementsByTagName("consumerSecret").item(0);
		Element tokenElement = (Element) doc.getElementsByTagName("token").item(0);
		Element tokenSecretElement = (Element) doc.getElementsByTagName("tokenSecret").item(0);

		if(baseUrlElement != null)
		{
			baseUrl = baseUrlElement.getTextContent();
		}
		else
		{
			throw new Exception("baseUrl not set in config");
		}
		
		if(consumerKeyElement != null)
		{
			consumerKey = consumerKeyElement.getTextContent();
		}
		else
		{
			throw new Exception("consumerKey not set in config");
		}
		
		if(consumerSecretElement != null)
		{
			consumerSecret = consumerSecretElement.getTextContent();
		}
		else
		{
			throw new Exception("consumerSecret not set in config");
		}

		if(tokenElement != null)
		{
			token = tokenElement.getTextContent();
		}

		if(tokenSecretElement != null)
		{
			tokenSecret = tokenSecretElement.getTextContent();
		}

		// set values
		instance.setBaseUrl(baseUrl);
		instance.setConsumerKey(consumerKey);
		instance.setConsumerSecret(consumerSecret);
		instance.setToken(token);
		instance.setTokenSecret(tokenSecret);
		instance.setServices(getServices(doc));

		return instance;
	}

	public static Configuration getInstance()
	{
		return instance;
	}

	public static void reload() throws Exception
	{
		if(instance != null)
		{
			instance.setServices(getServices(loadDocument()));
		}
	}

	public static ArrayList<String> getFieldsForService(Service item)
	{
		ArrayList<String> types = item.getTypes();

		for(int i = 0; i < types.size(); i++)
		{
			if(Configuration.getInstance().getServices().containsKey(types.get(i)))
			{
				return Configuration.getInstance().getServices().get(types.get(i));
			}
		}
		
		return null;
	}

	public static HashMap<String, ArrayList<String>> getServices(Document doc)
	{
		return ColumnConfig.getColumns();
	}

	public static Document loadDocument() throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		return doc;
	}

	public static void saveDocument(Document doc) throws Exception
	{
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");

		LSSerializer writer = impl.createLSSerializer();
		LSOutput output = impl.createLSOutput();

		output.setByteStream(new FileOutputStream(file));

		writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
		writer.write(doc, output);
	}
}
