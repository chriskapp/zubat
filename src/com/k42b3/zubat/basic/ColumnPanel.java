/**
 * $Id: ColumnPanel.java 212 2011-12-20 23:32:51Z k42b3.x@gmail.com $
 * 
 * zubat
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.neodym.ServiceItem;
import com.k42b3.zubat.Configuration;
import com.k42b3.zubat.Zubat;

/**
 * ColumnPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 212 $
 */
public class ColumnPanel extends JFrame
{
	private static final long serialVersionUID = 1L;

	protected ViewTableModel viewTm;
	protected ServiceItem item;

	protected JTable table;
	protected ColumnTableModel tm;

	protected JButton btnSave;
	protected JButton btnCancel;

	protected Logger logger = Logger.getLogger("com.k42b3.zubat");

	public ColumnPanel(ViewTableModel viewTm, ServiceItem item) throws Exception
	{
		this.viewTm = viewTm;
		this.item = item;

		this.setTitle("zubat (version: " + Zubat.version + ")");
		this.setLocation(100, 100);
		this.setSize(430, 400);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());

		tm = new ColumnTableModel(viewTm);

		this.buildComponent();
	}

	protected void buildComponent() throws Exception
	{
		this.add(this.buildTable(), BorderLayout.CENTER);

		this.add(this.buildButtons(), BorderLayout.SOUTH);
	}

	protected Component buildTable() throws Exception
	{
		table = new JTable(tm);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.getColumnModel().getColumn(0).setMinWidth(60);
		table.getColumnModel().getColumn(0).setMaxWidth(60);

		table.addMouseListener(new MouseListener() {

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
				int selectedRow = table.getSelectedRow();

				if(selectedRow != -1)
				{
					Boolean val = (Boolean) tm.getValueAt(selectedRow, 0);

					tm.setValueAt(!val, selectedRow, 0);
				}
			}

		});

		return new JScrollPane(table);
	}

	protected Component buildButtons()
	{
		JPanel buttons = new JPanel();

		this.btnSave   = new JButton("Save");
		this.btnCancel = new JButton("Cancel");

		this.btnSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					ArrayList<String> fields = new ArrayList<String>();

					for(int i = 0; i < tm.getRowCount(); i++)
					{
						if((Boolean) tm.getValueAt(i, 0))
						{
							fields.add((String) tm.getValueAt(i, 1));
						}
					}

					if(fields.size() > 0)
					{
						saveConfig(fields, item);

						viewTm.loadData(fields);

						setVisible(false);
					}
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}
			}

		});

		this.btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}

		});

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));

		buttons.add(this.btnSave);
		buttons.add(this.btnCancel);
		
		return buttons;
	}
	
	private void saveConfig(ArrayList<String> fields, ServiceItem item)
	{
		try
		{
			// load dom
			Document doc = Configuration.loadDocument();

			// find service node
			NodeList serviceList = doc.getElementsByTagName("service");
			Element serviceElement = null;
			Element tmpServiceElement = null;

			for(int i = 0; i < serviceList.getLength(); i++)
			{
				tmpServiceElement = (Element) serviceList.item(i);

				if(item.hasType(tmpServiceElement.getAttribute("type")))
				{
					serviceElement = tmpServiceElement;

					break;
				}
			}

			// append new items
			if(serviceElement != null)
			{
				// remove all child items
				NodeList itemList = serviceElement.getChildNodes();

				for(int i = 0; i < itemList.getLength(); i++)
				{
					if(itemList.item(i) instanceof Element)
					{
						serviceElement.removeChild(itemList.item(i));
					}
				}
			}
			else
			{
				serviceElement = doc.createElement("service");
				serviceElement.setAttribute("type", item.getTypes().get(0));

				doc.getDocumentElement().appendChild(serviceElement);
			}

			// add new items
			for(int i = 0; i < fields.size(); i++)
			{
				Element itemElement = doc.createElement("item");
				itemElement.setTextContent(fields.get(i));

				serviceElement.appendChild(itemElement);
			}

			// save dom
			Configuration.saveDocument(doc);

			Configuration.reload();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}
}
