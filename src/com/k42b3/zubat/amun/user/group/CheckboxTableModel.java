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

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.k42b3.neodym.Service;
import com.k42b3.neodym.data.Endpoint;
import com.k42b3.neodym.data.Record;
import com.k42b3.neodym.data.ResultSet;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.basic.ViewTableModel;

/**
 * CheckboxTableModel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class CheckboxTableModel extends ViewTableModel
{
	public CheckboxTableModel(Endpoint api, List<String> fields) throws Exception
	{
		super(api, fields);
	}

	public int getColumnCount()
	{
		return super.getColumnCount() + 1;
	}

	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return "Checked";

			default:
				return super.getColumnName(columnIndex - 1);
		}
	}

	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return Boolean.class;

			default:
				return super.getColumnClass(columnIndex - 1);
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 0;
	}
	
	protected void request(int startIndex, int count, String filterBy, String filterOp, String filterValue) throws Exception
	{
		// request
		ResultSet results = api.getAll(fields, startIndex, count, filterBy, filterOp, filterValue);

		this.totalResults = results.getTotalResults();
		this.startIndex = results.getStartIndex();
		this.itemsPerPage = results.getItemsPerPage();

		// build row
		int size;
		if(fields == null)
		{
			if(results.size() > 0)
			{
				size = results.get(0).size() + 1;
			}
			else
			{
				throw new Exception("Can not determin row size of empty response");
			}
		}
		else
		{
			size = getColumnCount();
		}

		rows = new Object[results.size()][size];

		// get entries
		for(int i = 0; i < results.size(); i++)
		{
			Record record = results.get(i);
			Iterator<Entry<String, String>> it = record.entrySet().iterator();
			int j = 1;
			rows[i][0] = false;

			while(it.hasNext())
			{
				Entry<String, String> entry = it.next();

				rows[i][j] = entry.getValue();

				j++;
			}
		}

		logger.info("Received " + results.size() + " rows");

		// fire data changed
		this.fireTableDataChanged();
	}
}
