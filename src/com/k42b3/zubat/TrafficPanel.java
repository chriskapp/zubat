/**
 * $Id: TrafficPanel.java 212 2011-12-20 23:32:51Z k42b3.x@gmail.com $
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

package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.k42b3.neodym.TrafficItem;

/**
 * TrafficPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 212 $
 */
public class TrafficPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JTable trafficTable;
	private TrafficDetail trafficDetailFrame;
	private TrafficTableModel trafficTm;

	public TrafficPanel(TrafficTableModel trafficTmModel)
	{
		this.setLayout(new BorderLayout());


		this.trafficTm = trafficTmModel;

		trafficTable = new JTable(trafficTm);

		trafficTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		trafficTable.getColumnModel().getColumn(0).setMinWidth(60); 
		trafficTable.getColumnModel().getColumn(0).setMaxWidth(60); 
		trafficTable.getColumnModel().getColumn(1).setMinWidth(120); 
		trafficTable.getColumnModel().getColumn(1).setMaxWidth(120); 
		//trafficTable.getColumnModel().getColumn(2).setMinWidth(600); 

		trafficTable.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) 
			{
				TrafficItem item = trafficTm.getRow(trafficTable.getSelectedRow());

				if(item != null)
				{
					if(trafficDetailFrame == null)
					{
						trafficDetailFrame = new TrafficDetail();
					}

					trafficDetailFrame.setItem(item);

					trafficDetailFrame.setVisible(true);

					trafficDetailFrame.toFront();
				}
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
			}

		});

		JScrollPane trafficPane = new JScrollPane(trafficTable);

		this.add(trafficPane, BorderLayout.CENTER);
	}
}
