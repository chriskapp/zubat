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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.ServiceItem;
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
	
	protected ServiceItem service;
	protected ArrayList<String> fields;

	protected ViewTableModel tm;
	protected JTable table;

	protected JPanel search;
	protected JPanel buttons;

	protected JTextField txtSearch;
	protected JComboBox<String> cboOperator;
	protected JComboBox<String> cboField;

	public ViewPanel(ServiceItem service, ArrayList<String> fields) throws Exception
	{
		this.service = service;
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
		ViewTableModel tm = new ViewTableModel(service.getUri());

		if(fields == null || fields.size() == 0)
		{
			tm.loadData(tm.getSupportedFields());
		}
		else
		{
			tm.loadData(fields);
		}

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

		private ColumnPanel panel;

		public SearchPanel()
		{
			this.setLayout(new BorderLayout());

			this.add(this.buildSearch(), BorderLayout.CENTER);
			//this.add(this.buildColumn(), BorderLayout.EAST);
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
						String operator = cboOperator.getSelectedItem().toString();
						String selectedField = cboField.getSelectedItem().toString();
						String value = URLEncoder.encode(txtSearch.getText(), "UTF-8");

						String urlFilter = Http.appendQuery(tm.getBaseUrl(), "filterBy=" + selectedField + "&filterOp=" + operator + "&filterValue=" + value);

						tm.setUrl(urlFilter);
						tm.loadData(tm.getFields());
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
		
		private Component buildColumn()
		{
			JPanel columnPanel = new JPanel();

			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.RIGHT);

			columnPanel.setLayout(layout);


			JButton btnColumn = new JButton("Columns");

			btnColumn.addMouseListener(new MouseListener() {

				public void mouseReleased(MouseEvent e) 
				{
				}

				public void mousePressed(MouseEvent e) 
				{
				}

				public void mouseExited(MouseEvent e) 
				{
				}

				public void mouseEntered(MouseEvent e) 
				{
				}

				public void mouseClicked(MouseEvent e) 
				{
					try
					{
						if(panel == null)
						{
							panel = new ColumnPanel(tm, service);
						}

						panel.setVisible(true);
					}
					catch(Exception ex)
					{
						Zubat.handleException(ex);
					}
				}

			});

			columnPanel.add(btnColumn);


			return columnPanel;
		}
	}

	private class ButtonsPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private ViewTableModel tm;
		private JButton btnPrev;
		private JButton btnNext;
		private JLabel lblPagination;

		public ButtonsPanel(ViewTableModel model)
		{
			this.tm = model;
			btnPrev = new JButton("Prev");
			btnNext = new JButton("Next");
			lblPagination = new JLabel();

			btnPrev.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent ex) 
				{
					try
					{
						tm.prevPage();
					}
					catch(Exception e)
					{
						Zubat.handleException(e);
					}
				}

			});

			btnNext.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent ex) 
				{
					try
					{
						tm.nextPage();
					}
					catch(Exception e)
					{
						Zubat.handleException(e);
					}
				}

			});

			this.setLayout(new FlowLayout(FlowLayout.CENTER));

			this.add(btnPrev);
			this.add(lblPagination);
			this.add(btnNext);
		}

		public void validate()
		{
			int pageResults = tm.getStartIndex() + tm.getItemsPerPage();
			int count = pageResults > tm.getTotalResults() ? tm.getTotalResults() : pageResults;


			lblPagination.setText(tm.getStartIndex() + " - " + count + " of " + tm.getTotalResults());

			if(tm.getStartIndex() == 0)
			{
				btnPrev.setEnabled(false);
			}
			else
			{
				btnPrev.setEnabled(true);
			}

			if(tm.getTotalResults() < tm.getItemsPerPage() || pageResults >= tm.getTotalResults())
			{
				btnNext.setEnabled(false);
			}
			else
			{
				btnNext.setEnabled(true);
			}

			super.validate();
		}
	}
}
