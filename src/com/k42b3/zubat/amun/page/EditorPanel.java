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

package com.k42b3.zubat.amun.page;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.StringEscapeUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.Message;
import com.k42b3.zubat.Configuration;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.basic.FormPanel;
import com.k42b3.zubat.model.Page;

/**
 * EditorPanel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class EditorPanel extends com.k42b3.zubat.basic.EditorPanel
{
	protected FormPanel form;
	protected RSyntaxTextArea textarea;
	protected JFXPanel webpanel;
	protected WebView webview;
	protected WebEngine webengine;
	protected JSplitPane sp;
	
	public EditorPanel(Page page) throws Exception
	{
		super(page);
	}
	
	public JComponent getComponent() throws Exception
	{
		JPanel panel = new JPanel(new BorderLayout());
		
		// get form
		form = new FormPanel(item.getUri() + "/form?method=update&id=" + getRecordId());
		
		// menu
		panel.add(getMenuBar(), BorderLayout.NORTH);
		
		// textarea
		textarea = new RSyntaxTextArea();
		textarea.setText(form.getRequestFields().get("content").getValue());
		textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);

		// webview
		webpanel = new JFXPanel();

		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new RTextScrollPane(textarea), webpanel);
		sp.addComponentListener(new ComponentAdapter(){

			public void componentResized(ComponentEvent e) 
			{
				JSplitPane sp = (JSplitPane) e.getSource();
				//textarea.setSize(new Dimension(sp.getWidth() / 2, sp.getHeight()));
				//webpanel.setSize(new Dimension(sp.getWidth() / 2, sp.getHeight()));
				
				if(webview != null)
				{
					webview.setPrefSize(sp.getWidth() / 2 - 6, sp.getHeight() - 6);
				}
				
				sp.setDividerLocation(0.50);
			}

		});
		sp.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent e)
			{
				if(webview != null)
				{
					webview.setPrefSize(sp.getWidth() - sp.getDividerLocation(), sp.getHeight() - 6);
				}
			}

		});

		panel.add(sp, BorderLayout.CENTER);
		
		// buttons
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JButton btnPreview = new JButton("Preview");
		btnPreview.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				preview();
			}

		});

		JButton btnPublish = new JButton("Publish");
		btnPublish.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				publish();
			}

		});

		buttons.add(btnPreview);
		buttons.add(btnPublish);

		panel.add(buttons, BorderLayout.SOUTH);
		
		// load webview
		Platform.runLater(new Runnable(){

            public void run() 
            {
            	initWebview(webpanel);
            }
            
        });

		return panel;
	}
	
	protected void loadFinished(JComponent component, Exception lastException)
	{
		super.loadFinished(component, lastException);
		
		sp.setDividerLocation(0.50);
	}
	
	protected JFXPanel initWebview(JFXPanel fxPanel)
	{
		Group group = new Group();
        Scene scene = new Scene(group);
        fxPanel.setScene(scene);

        webview = new WebView();

        group.getChildren().add(webview);

        webengine = webview.getEngine();
        webengine.load(Configuration.getInstance().getBaseUrl() + "/index.php/" + page.getPath());

		return fxPanel;
	}
	
	protected int getRecordId() throws Exception
	{
		Document response = Zubat.getHttp().requestXml(Http.GET, item.getUri() + "?format=xml&fields=id&filterBy=pageId&filterOp=equals&filterValue=" + page.getId());
		NodeList entries = response.getElementsByTagName("entry");
		
		if(entries.getLength() > 0)
		{
			Element item = (Element) entries.item(0);
			NodeList id = item.getElementsByTagName("id");
			
			if(id.getLength() > 0)
			{
				return Integer.parseInt(id.item(0).getTextContent());
			}
			else
			{
				throw new Exception("Found no id column");
			}
		}
		else
		{
			throw new Exception("Found no entry for page");
		}
	}
	
	protected JMenuBar getMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		
		JMenuItem itemPreview = new JMenuItem("Preview");
		itemPreview.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemPreview.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				preview();
			}
			
		});
		menu.add(itemPreview);

		JMenuItem itemPublish= new JMenuItem("Publish");
		itemPublish.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		itemPublish.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				publish();
			}
			
		});
		menu.add(itemPublish);

		menuBar.add(menu);
		
		return menuBar;
	}

	protected void preview()
	{
		Platform.runLater(new Runnable(){

            public void run() 
            {
            	String js = "";
            	js+= "var html = '" + StringEscapeUtils.escapeEcmaScript(textarea.getText()) + "';";
            	js+= "$('.amun-service-page-content').html(html);";

            	webengine.executeScript(js);
            }
            
        });
	}

	protected void publish()
	{
		try
		{
			form.getRequestFields().get("content").setValue(textarea.getText());
			
			Document doc = form.sendRequest();
			Element rootElement = (Element) doc.getDocumentElement();

			// get message
			Message msg = Message.parseMessage(rootElement);

			if(msg.hasSuccess())
			{
				JOptionPane.showMessageDialog(null, msg.getText(), "Response", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				throw new Exception(msg.getText());
			}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Response", JOptionPane.ERROR_MESSAGE);

			Zubat.handleException(ex);
		}
	}
}
