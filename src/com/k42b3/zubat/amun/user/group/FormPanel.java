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

package com.k42b3.zubat.amun.user.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.k42b3.neodym.data.Endpoint;
import com.k42b3.neodym.data.ResultSet;
import com.k42b3.zubat.Zubat;

/**
 * FormPanel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class FormPanel extends com.k42b3.zubat.basic.FormPanel
{
	protected CheckboxPanel checkbox;

	public FormPanel(Endpoint api, int method, int id, Map<String, String> params) throws Exception
	{
		super(api, method, id, params);
	}

	protected void buildComponent() throws Exception
	{
		super.buildComponent();
		
		// load rights
		Endpoint api = Zubat.getServices().getEndpoint("http://ns.amun-project.org/2011/amun/service/user/right");

		List<String> fields = new ArrayList<String>();
		fields.add("id");
		fields.add("description");

		// add panel
		JPanel form = (JPanel) body.getComponent(0);
		JPanel panel = (JPanel) form.getComponent(0);

		checkbox = new CheckboxPanel(api, fields);
		
		// load existing rights
		if((method == UPDATE || method == DELETE) && id > 0)
		{
			api = Zubat.getServices().getEndpoint("http://ns.amun-project.org/2011/amun/service/user/group/right");
			fields = new ArrayList<String>();
			fields.add("rightId");

			ResultSet result = api.getAll(fields, 0, 1024, "groupId", "equals", "" + id);
			List<Integer> selected = new ArrayList<Integer>();

			for(int i = 0; i < result.size(); i++)
			{
				selected.add(result.get(i).getIntField("rightId"));
			}

			checkbox.setSelectedValues(selected);
		}

		panel.add(checkbox);

		requestFields.put("rights", checkbox);
	}
}
