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

package com.k42b3.zubat.form;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * CheckboxList
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class CheckboxList extends JPanel implements FormElementInterface
{
	private static final long serialVersionUID = 1L;

	private String url;

	private CheckboxListTableModel tm;
	private JTable table;

	public CheckboxList(String url)
	{
		this.setLayout(new BorderLayout());

		this.url = url;

		try
		{
			tm = new CheckboxListTableModel(url);

			table = new JTable(tm);

			table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			table.getColumnModel().getColumn(0).setMinWidth(60);
			table.getColumnModel().getColumn(0).setMaxWidth(60);
			
			this.add(new JScrollPane(table), BorderLayout.CENTER);
		}
		catch(Exception e)
		{
		}
	}

	public String getValue() 
	{
		StringBuilder values = new StringBuilder();

		for(int i = 0; i < tm.getRowCount(); i++)
		{
			boolean selected = (Boolean) tm.getValueAt(i, 0);

			if(selected)
			{
				values.append(tm.getValueAt(i, 2).toString());

				if(i < tm.getRowCount() - 2)
				{
					values.append(",");
				}
			}
		}

		return values.toString();
	}
}
