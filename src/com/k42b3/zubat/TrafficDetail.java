/**
 * $Id: TrafficDetail.java 212 2011-12-20 23:32:51Z k42b3.x@gmail.com $
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

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.http.Header;

import com.k42b3.neodym.TrafficItem;

/**
 * TrafficDetail
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 212 $
 */
public class TrafficDetail extends JFrame
{
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabPane;
	private JTextArea txtRequest;
	private JTextArea txtResponse;

	public TrafficDetail()
	{
		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(600, 400);

		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		tabPane = new JTabbedPane();

		txtRequest = new JTextArea();
		txtResponse = new JTextArea();

		tabPane.addTab("Request", new JScrollPane(txtRequest));
		tabPane.addTab("Response", new JScrollPane(txtResponse));


		this.add(tabPane, BorderLayout.CENTER);

		this.setVisible(true);
	}

	public void setItem(TrafficItem item)
	{
		txtRequest.setText(item.getRequest().getRequestLine() + "\n" + headersToString(item.getRequest().getAllHeaders()) + "\n" + item.getRequestContent());
		txtRequest.setCaretPosition(0);

		txtResponse.setText(item.getResponse().getStatusLine() + "\n" + headersToString(item.getResponse().getAllHeaders()) + "\n" + item.getResponseContent());
		txtResponse.setCaretPosition(0);

		tabPane.setSelectedIndex(1);
	}

	private String headersToString(Header[] headers)
	{
		StringBuilder content = new StringBuilder();

		for(int i = 0; i < headers.length; i++)
		{
			content.append(headers[i].getName() + ": " + headers[i].getValue() + "\n");
		}

		return content.toString();
	}
}
