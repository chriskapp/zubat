/**
 * $Id: Account.java 212 2011-12-20 23:32:51Z k42b3.x@gmail.com $
 * 
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

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Account
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 212 $
 */
public class Account extends HashMap<String, String>
{
	private static final long serialVersionUID = 1L;

	private static String[] fields = {"name", "profileUrl", "thumbnailUrl", "loggedIn", "gender", "group", "status", "timezone", "updated", "date"};

	public String getName()
	{
		return this.get("name");
	}
	
	public String getProfileUrl()
	{
		return this.get("profileUrl");
	}
	
	public String getThumbnailUrl()
	{
		return this.get("thumbnailUrl");
	}
	
	public String getLoggedIn()
	{
		return this.get("loggedIn");
	}

	public String getGender()
	{
		return this.get("gender");
	}

	public String getGroup()
	{
		return this.get("group");
	}

	public String getStatus()
	{
		return this.get("status");
	}

	public String getTimezone()
	{
		return this.get("timezone");
	}

	public String getUpdated()
	{
		return this.get("updated");
	}

	public String getDate()
	{
		return this.get("date");
	}

	public static Account buildAccount(Document doc)
	{
		Account account = new Account();

		// parse account
		for(int i = 0; i < fields.length; i++)
		{
			NodeList fieldList = doc.getElementsByTagName(fields[i]);

			if(fieldList.getLength() > 0)
			{
				Node fieldNode = fieldList.item(0);
				Element fieldElement = (Element) fieldNode;

				account.put(fields[i], fieldElement.getTextContent());
			}
		}

		return account;
	}
}
