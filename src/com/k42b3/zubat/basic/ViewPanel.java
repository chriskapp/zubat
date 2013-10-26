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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.k42b3.neodym.Service;
import com.k42b3.neodym.data.Endpoint;
import com.k42b3.zubat.Zubat;

/**
 * ViewPanel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class ViewPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	protected Endpoint api;
	protected ArrayList<String> fields;

	protected ViewTableModel tm;
	protected JTable table;

	protected JPanel search;
	protected ButtonsPanel buttons;

	protected JTextField txtSearch;
	protected JComboBox<String> cboOperator;
	protected JComboBox<String> cboField;

	public ViewPanel(Endpoint api, ArrayList<String> fields) throws Exception
	{
		this.api = api;
		this.fields = fields;

		this.setLayout(new BorderLayout());

		tm = this.getTableModel();

		this.buildComponent();
	}

	public JTable getTable()
	{
		return table;
	}
	
	protected ViewTableModel getTableModel() throws Exception
	{
		ViewTableModel tm = new ViewTableModel(api, fields);

		tm.loadData();

		return tm;
	}

	protected void buildComponent()
	{
		this.add(this.buildTable(), BorderLayout.CENTER);

		this.add(this.buildSearch(), BorderLayout.NORTH);

		this.add(this.buildButtons(), BorderLayout.SOUTH);
	}

	protected Component buildTable()
	{
		table = new JTable(tm);

		return new JScrollPane(table);
	}

	protected Component buildSearch()
	{
		search = new SearchPanel();

		return search;
	}

	protected Component buildButtons()
	{
		buttons = new ButtonsPanel(tm);
		buttons.validate();

		tm.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) 
			{
				buttons.validate();
			}

		});

		return buttons;
	}

	private class SearchPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public SearchPanel()
		{
			this.setLayout(new BorderLayout());

			this.add(this.buildSearch(), BorderLayout.CENTER);
		}

		private Component buildSearch()
		{
			JPanel searchPanel = new JPanel();

			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEFT);

			searchPanel.setLayout(layout);


			JLabel lblSearch = new JLabel("Search:");
			lblSearch.setBorder(new EmptyBorder(5, 5, 5, 5));

			cboField = new JComboBox<String>(new DefaultComboBoxModel(fields.toArray()));
			cboField.setPreferredSize(new Dimension(100, 22));

			String[] operators = {"contains", "equals", "startsWith", "present"};
			cboOperator = new JComboBox<String>(new DefaultComboBoxModel(operators));
			cboOperator.setPreferredSize(new Dimension(75, 22));

			txtSearch = new JTextField();
			txtSearch.setPreferredSize(new Dimension(225, 22));
			txtSearch.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						String filterBy = cboField.getSelectedItem().toString();
						String filterOp = cboOperator.getSelectedItem().toString();
						String filterValue = txtSearch.getText();

						tm.loadData(filterBy, filterOp, filterValue);
					}
					catch(Exception ex)
					{
						Zubat.handleException(ex);
					}
				}

			});

			searchPanel.add(lblSearch);
			searchPanel.add(cboField);
			searchPanel.add(cboOperator);
			searchPanel.add(txtSearch);


			return searchPanel;
		}
	}
}
