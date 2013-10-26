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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.k42b3.neodym.data.Endpoint;
import com.k42b3.zubat.basic.ViewTableModel;
import com.k42b3.zubat.form.FormElementInterface;

/**
 * CheckboxList
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class CheckboxPanel extends JPanel implements FormElementInterface
{
	private static final long serialVersionUID = 1L;

	protected Endpoint api;
	protected List<String> fields;

	private ViewTableModel tm;
	private JTable table;

	public CheckboxPanel(Endpoint api, List<String> fields) throws Exception
	{
		this.setLayout(new BorderLayout());

		this.api = api;
		this.fields = fields;

		// table
		tm = new CheckboxTableModel(api, fields);
		tm.loadData(0, 1024);

		table = new JTable(tm);
		table.getColumnModel().removeColumn(table.getColumnModel().getColumn(1));
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		JScrollPane scp = new JScrollPane(table);
		scp.setPreferredSize(new Dimension(400, 300));

		this.add(scp, BorderLayout.CENTER);
		
		// buttons
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JButton btnToggle = new JButton("Toggle");
		btnToggle.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				for(int i = 0; i < tm.getRowCount(); i++)
				{
					boolean checked = Boolean.parseBoolean(tm.getValueAt(i, 0).toString());

					tm.setValueAt(!checked, i, 0);
				}

				tm.fireTableRowsUpdated(0, tm.getRowCount() - 1);
			}
			
		});

		buttons.add(btnToggle);

		this.add(buttons, BorderLayout.SOUTH);
	}

	public List<Integer> getSelectedValues() 
	{
		ArrayList<Integer> values = new ArrayList<Integer>();

		for(int i = 0; i < tm.getRowCount(); i++)
		{
			boolean selected = (Boolean) tm.getValueAt(i, 0);

			if(selected)
			{
				int id = Integer.parseInt(tm.getValueAt(i, 1).toString());

				values.add(id);
			}
		}

		return values;
	}
	
	public void setSelectedValues(List<Integer> values)
	{
		for(int i = 0; i < tm.getRowCount(); i++)
		{
			int id = Integer.parseInt(tm.getValueAt(i, 1).toString());

			for(int j = 0; j < values.size(); j++)
			{
				if(id == values.get(j))
				{
					tm.setValueAt(true, i, 0);
					break;
				}
			}
		}

		tm.fireTableRowsUpdated(0, tm.getRowCount() - 1);
	}

	public String getValue()
	{
		StringBuilder value = new StringBuilder();
		List<Integer> selected = getSelectedValues();

		for(int i = 0; i < selected.size(); i++)
		{
			value.append(selected.get(i));
			
			if(i < selected.size() - 1)
			{
				value.append(',');
			}
		}

		return value.toString();
	}

	public void setValue(String text)
	{
		String[] ids = text.split(",");
		List<Integer> selected = new ArrayList<Integer>();

		for(int i = 0; i < ids.length; i++)
		{
			int id = Integer.parseInt(ids[i]);

			selected.add(id);
		}

		setSelectedValues(selected);
	}
}
