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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.k42b3.neodym.data.Message;
import com.k42b3.neodym.data.ResultSet;
import com.k42b3.zubat.Configuration;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.container.ContainerEvent;
import com.k42b3.zubat.container.ContainerEventListener;
import com.k42b3.zubat.container.ContainerLoadFinishedEvent;
import com.k42b3.zubat.model.Page;

/**
 * WebviewEditorPanelAbstract
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
abstract public class WebviewEditorPanelAbstract extends com.k42b3.zubat.basic.EditorPanel
{
	protected int recordId;
	protected FormPanel form;

	protected JFXPanel webpanel;
	protected WebView webview;
	protected WebEngine webengine;

	protected JSplitPane sp;
	
	public WebviewEditorPanelAbstract(Page page) throws Exception
	{
		super(page);
		
		// event handler
		addContainerListener(new ContainerSelfListener());
	}
	
	abstract public JComponent getEditorComponent() throws Exception;

	public JComponent getComponent() throws Exception
	{
		JPanel panel = new JPanel(new BorderLayout());
		
		// get form
		recordId = getRecordId();

		if(recordId > 0)
		{
			form = new FormPanel(page.getApi(), FormPanel.UPDATE, recordId);
		}
		else
		{
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("pageId", "" + page.getId());

			form = new FormPanel(page.getApi(), FormPanel.CREATE, params);
		}
		
		// menu
		JMenuBar menuBar = getMenuBar();
		
		if(menuBar != null)
		{
			panel.add(getMenuBar(), BorderLayout.NORTH);
		}

		// editor
		JComponent editor = getEditorComponent();

		// webview
		webpanel = new JFXPanel();

		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editor, webpanel);
		sp.addComponentListener(new ComponentAdapter(){

			public void componentResized(ComponentEvent e) 
			{
				JSplitPane sp = (JSplitPane) e.getSource();

				if(webview != null)
				{
					webview.setPrefSize(sp.getWidth() / 2 - 4, sp.getHeight() - 4);
				}

				sp.setDividerLocation(0.50);
			}

		});
		sp.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent e)
			{
				if(webview != null)
				{
					webview.setPrefSize(sp.getWidth() - sp.getDividerLocation(), sp.getHeight() - 4);
				}
			}

		});

		panel.add(sp, BorderLayout.CENTER);
		
		// buttons
		JComponent bottomBar = getBottomBar();
		
		if(bottomBar != null)
		{
			panel.add(bottomBar, BorderLayout.SOUTH);
		}
		
		// load webview
		Platform.runLater(new Runnable(){

            public void run() 
            {
            	initWebview(webpanel);
            }
            
        });

		return panel;
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
		webengine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>(){

			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) 
			{
				if(newState == State.SUCCEEDED)
				{
					if(webview != null)
					{
						webview.setPrefSize(sp.getWidth() / 2 - 4, sp.getHeight() - 4);
					}
				}
			}

		});

		return fxPanel;
	}

	protected int getRecordId() throws Exception
	{
		List<String> fields = new ArrayList<String>();
		fields.add("id");

		ResultSet result = page.getApi().getAll(fields, 0, 1, "pageId", "equals", "" + page.getId());
		
		if(result.size() > 0)
		{
			return result.get(0).getIntField("id");
		}
		else
		{
			throw new Exception("Found nor record");
		}
	}
	
	protected JMenuBar getMenuBar()
	{
		return null;
	}
	
	protected JComponent getBottomBar()
	{
		return null;
	}

	protected void setRequestFields()
	{
	}

	protected void save()
	{
		try
		{
			// set fields
			setRequestFields();
			
			// send request
			Message message = form.sendRequest();

			if(message.hasSuccess())
			{
				if(recordId == 0)
				{
					// we have created a new page set to update
					recordId = getRecordId();

					form = new FormPanel(page.getApi(), FormPanel.UPDATE, recordId);
				}

				// reload
				Platform.runLater(new Runnable(){

					public void run() 
					{
						webengine.reload();
					}

				});
			}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Response", JOptionPane.ERROR_MESSAGE);

			Zubat.handleException(ex);
		}
	}
	
	private class ContainerSelfListener implements ContainerEventListener
	{
		public void containerEvent(ContainerEvent event)
		{
			if(event instanceof ContainerLoadFinishedEvent)
			{
				sp.setDividerLocation(0.50);
			}
		}
	}
}
