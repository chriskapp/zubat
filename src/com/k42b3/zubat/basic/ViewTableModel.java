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

package com.k42b3.zubat.basic;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import com.k42b3.neodym.data.Endpoint;
import com.k42b3.neodym.data.Record;
import com.k42b3.neodym.data.ResultSet;

/**
 * ViewTableModel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class ViewTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	protected int totalResults;
	protected int startIndex;
	protected int itemsPerPage;
	
	protected Endpoint api;
	protected List<String> fields;

	protected Object[][] rows;
	protected Logger logger = Logger.getLogger("com.k42b3.zubat");
	
	public ViewTableModel(Endpoint api, List<String> fields) throws Exception
	{
		this.api = api;
		this.fields = fields;
	}
	
	public void loadData(int startIndex, int count, String filterBy, String filterOp, String filterValue) throws Exception
	{
		this.request(startIndex, count, filterBy, filterOp, filterValue);
	}
	
	public void loadData(String filterBy, String filterOp, String filterValue) throws Exception
	{
		this.loadData(startIndex, itemsPerPage, filterBy, filterOp, filterValue);
	}
	
	public void loadData(int startIndex, int count) throws Exception
	{
		this.loadData(startIndex, count, null, null, null);
	}
	
	public void loadData() throws Exception
	{
		this.loadData(0, 0);
	}

	public void nextPage() throws Exception
	{
		int index = startIndex + itemsPerPage;
	
		this.loadData(index, itemsPerPage);
	}

	public void prevPage() throws Exception
	{
		int index = startIndex - itemsPerPage;
		index = index < 0 ? 0 : index;

		this.loadData(index, itemsPerPage);
	}

	public List<String> getFields()
	{
		return fields;
	}

	public int getTotalResults()
	{
		return totalResults;
	}

	public int getStartIndex()
	{
		return startIndex;
	}

	public int getItemsPerPage()
	{
		return itemsPerPage;
	}

	public int getColumnCount()
	{
		return fields.size();
	}

	public String getColumnName(int columnIndex)
	{
		String columnName = fields.get(columnIndex);
		columnName = Character.toUpperCase(columnName.charAt(0)) + columnName.substring(1);

		return columnName;
	}

	public int getRowCount() 
	{
		return rows.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex >= 0 && rowIndex < rows.length)
		{
			if(columnIndex >= 0 && columnIndex < rows[rowIndex].length)
			{
				return rows[rowIndex][columnIndex];
			}
		}

		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(rowIndex >= 0 && rowIndex < rows.length)
		{
			if(columnIndex >= 0 && columnIndex < rows[rowIndex].length)
			{
				rows[rowIndex][columnIndex] = aValue;
			}
		}
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
				size = results.get(0).size();
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
			int j = 0;
			
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
