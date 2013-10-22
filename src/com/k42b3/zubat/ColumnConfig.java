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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ColumnConfig
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class ColumnConfig
{
	public static HashMap<String, ArrayList<String>> getColumns()
	{
		HashMap<String, ArrayList<String>> services = new HashMap<String, ArrayList<String>>();
		ArrayList<String> items;

		items = new ArrayList<String>();
		items.add("id");
		items.add("parentId");
		items.add("serviceType");
		items.add("path");
		items.add("title");
		items.add("template");
		items.add("serviceName");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/content/page", items);

		items = new ArrayList<String>();
		items.add("id");
		items.add("name");
		items.add("title");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/content/gadget", items);

		items = new ArrayList<String>();
		items.add("id");
		items.add("name");
		items.add("mimeType");
		items.add("size");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/media", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("name");
		items.add("email");
		items.add("countryTitle");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/user/account", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("title");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/user/group", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("authorName");
		items.add("verb");
		items.add("summary");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/user/activity", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("name");
		items.add("source");
		items.add("license");
		items.add("version");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/core/service", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("name");
		items.add("type");
		items.add("class");
		items.add("value");
		services.put("http://ns.amun-project.org/2011/amun/service/core/registry", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("name");
		items.add("from");
		items.add("subject");
		services.put("http://ns.amun-project.org/2011/amun/service/mail", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("status");
		items.add("name");
		items.add("email");
		items.add("url");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/oauth", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("title");
		items.add("code");
		items.add("longitude");
		items.add("latitude");
		services.put("http://ns.amun-project.org/2011/amun/service/country", items);
		
		items = new ArrayList<String>();
		items.add("group");
		items.add("key");
		items.add("value");
		services.put("http://ns.amun-project.org/2011/amun/service/phpinfo", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("authorName");
		items.add("pageTitle");
		items.add("text");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/comment", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("pageTitle");
		items.add("contentType");
		items.add("content");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/file", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("authorName");
		items.add("pageTitle");
		items.add("title");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/news", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("authorName");
		items.add("pageTitle");
		items.add("content");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/page", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("authorName");
		items.add("pageTitle");
		items.add("content");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/php", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("authorName");
		items.add("pageTitle");
		items.add("mediaName");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/pipe", items);
		
		items = new ArrayList<String>();
		items.add("id");
		items.add("authorName");
		items.add("pageTitle");
		items.add("href");
		items.add("date");
		services.put("http://ns.amun-project.org/2011/amun/service/redirect", items);
		
		return services;
	}
}
