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

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

/**
 * ColumnTableModel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class ColumnTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	protected ViewTableModel tm;
	protected Object[][] rows;

	protected Logger logger = Logger.getLogger("com.k42b3.zubat");
	
	public ColumnTableModel(ViewTableModel tm) throws Exception
	{
		this.tm = tm;

		rows = new Object[tm.getSupportedFields().size()][2];

		loadData();
	}

	private void loadData()
	{
		ArrayList<String> supportedFields = tm.getSupportedFields();
		ArrayList<String> fields = tm.getFields();

		for(int i = 0; i < supportedFields.size(); i++)
		{
			Boolean isSelected = false;

			for(int k = 0; k < fields.size(); k++)
			{
				if(fields.get(k).equals(supportedFields.get(i)))
				{
					isSelected = true;

					break;
				}
			}

			rows[i][0] = isSelected;
			rows[i][1] = supportedFields.get(i);
		}
	}

	public int getColumnCount()
	{
		return 2;
	}

	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:

				return Boolean.class;

			case 1:

				return String.class;

			default:

				return null;
		}
	}

	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:

				return "Active";

			case 1:

				return "Column";

			default:

				return null;
		}
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

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(rowIndex > 0 && rowIndex < rows.length)
		{
			if(columnIndex >= 0 && columnIndex < rows[rowIndex].length)
			{
				rows[rowIndex][columnIndex] = aValue;

				this.fireTableDataChanged();
			}
		}
	}
}
