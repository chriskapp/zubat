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
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.k42b3.neodym.ServiceItem;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.container.ContainerEvent;
import com.k42b3.zubat.container.ContainerEventListener;
import com.k42b3.zubat.container.ContainerLoadFinishedEvent;
import com.k42b3.zubat.container.ContainerRequestEditorEvent;
import com.k42b3.zubat.container.EditorAbstract;
import com.k42b3.zubat.container.ServiceAbstract;
import com.k42b3.zubat.model.Page;

/**
 * Container
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Container extends ServiceAbstract
{
	private static final long serialVersionUID = 1L;

	public final static int RECEIVE = 0x0;
	public final static int CREATE = 0x1;
	public final static int UPDATE = 0x2;
	public final static int DELETE = 0x3;

	protected JTabbedPane tp;
	protected ViewPanel view;
	protected JFrame editorFrame;
	protected int selectedId = 0;

	public Container(ServiceItem item) throws Exception
	{
		super(item);
		
		// event handler
		addContainerListener(new ContainerSelfListener());
	}

	public String getTitle()
	{
		String title = item.getUri().substring(item.getUri().lastIndexOf('/') + 1);
		
		if(title.isEmpty())
		{
			title = "Unknown";
		}

		return Character.toUpperCase(title.charAt(0)) + title.substring(1);
	}

	public JComponent getComponent() throws Exception
	{
		tp = new JTabbedPane();

		tp.addTab("View", this.getViewPanel());
		tp.addTab("Create", null);
		tp.addTab("Update", null);
		tp.addTab("Delete", null);

		tp.addChangeListener(this.getChangeListener());

		tp.setEnabledAt(UPDATE, false);
		tp.setEnabledAt(DELETE, false);
		
		return tp;
	}

	public ViewPanel getView()
	{
		return view;
	}
	
	protected void setSelectedId(int selectedId)
	{
		if(selectedId > 0)
		{
			tp.setEnabledAt(2, true);
			tp.setEnabledAt(3, true);

			this.selectedId = selectedId;
		}
		else
		{
			tp.setEnabledAt(2, false);
			tp.setEnabledAt(3, false);

			this.selectedId = 0;
		}
	}

	protected int getSelectedId()
	{
		return this.selectedId;
	}

	protected void renderTabs()
	{
		// loading panel
		if(tp.getSelectedIndex() == DELETE || tp.getSelectedIndex() == UPDATE || tp.getSelectedIndex() == CREATE)
		{
			JPanel panel = new JPanel(new FlowLayout());
			panel.add(new JLabel("Loading ..."));

			tp.setComponentAt(tp.getSelectedIndex(), panel);
		}

		// worker
		if(tp.getSelectedIndex() == DELETE && getSelectedId() > 0)
		{
			FormWorker worker = new FormWorker(item.getUri() + "/form?method=delete&id=" + getSelectedId(), DELETE);
			worker.execute();
		}
		else if(tp.getSelectedIndex() == UPDATE && getSelectedId() > 0)
		{
			FormWorker worker = new FormWorker(item.getUri() + "/form?method=update&id=" + getSelectedId(), UPDATE);
			worker.execute();
		}
		else if(tp.getSelectedIndex() == CREATE)
		{
			FormWorker worker = new FormWorker(item.getUri() + "/form?method=create", CREATE);
			worker.execute();
		}
		else
		{
			setSelectedId(0);
			
			ViewWorker worker = new ViewWorker();
			worker.execute();
		}
	}
	
	protected ChangeListener getChangeListener()
	{
		return new ContainerChangeListener();
	}

	protected JComponent getViewPanel() throws Exception
	{
		view = new ViewPanel(item, fields);
		JTable table = view.getTable();
		table.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e)
			{
				JTable table = (JTable) e.getSource();
				Object rawId = table.getModel().getValueAt(table.getSelectedRow(), 0);

				if(rawId != null)
				{
					int id = Integer.parseInt(rawId.toString());

					setSelectedId(id);
				}
			}

			public void mouseClicked(MouseEvent e) 
			{
				if(e.getClickCount() == 2)
				{
					try
					{
						JTable table = (JTable) e.getSource();
						Object id = table.getModel().getValueAt(table.getSelectedRow(), 0);
						Object serviceType = table.getModel().getValueAt(table.getSelectedRow(), 2);
						Object path = table.getModel().getValueAt(table.getSelectedRow(), 3);
						Object title = table.getModel().getValueAt(table.getSelectedRow(), 4);
						
						if(id != null && serviceType != null && path != null && title != null)
						{
							Page page = new Page(
								Integer.parseInt(id.toString()),
								title.toString(),
								path.toString(),
								serviceType.toString()
							);

							fireContainer(new ContainerRequestEditorEvent(page));
						}
					}
					catch(Exception ex)
					{
						Zubat.handleException(ex);
					}
				}
			}

		});
		
		return view;
	}

	protected JComponent getFormPanel(String url)
	{
		try
		{
			return new FormPanel(url);
		}
		catch(Exception e)
		{
			return getExceptionPanel(e);
		}
	}

	protected void openEditor(Page page)
	{
		try
		{
			EditorAbstract module;
			String className = Zubat.getClassNameFromType(page.getServiceType(), "EditorPanel");

			try
			{
				Class<?> editor = Class.forName(className);

				module = (EditorAbstract) editor.getConstructor(Page.class).newInstance(page);
			}
			catch(ClassNotFoundException ex)
			{
				module = new com.k42b3.zubat.basic.EditorPanel(page);
			}
			
			logger.info("Load class " + module.getClass().getName());

			// close existing frame
			if(editorFrame != null)
			{
				editorFrame.setVisible(true);
			}

			// frame
			editorFrame = new JFrame();
			editorFrame.setTitle("Edit - /" + page.getPath());
			editorFrame.setLocation(100, 100);
			editorFrame.setSize(800, 600);
			editorFrame.setMinimumSize(this.getSize());
			editorFrame.setLayout(new BorderLayout());
			editorFrame.add(module, BorderLayout.CENTER);
			editorFrame.setVisible(true);

			// event handler
			module.addContainerListener(new ContainerEditorListener());

			// load
			module.onLoad();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	private class ContainerChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if(isShowing())
			{
				renderTabs();
			}
		}
	}
		
	private class ViewWorker extends SwingWorker<Void, Exception>
	{
		protected Void doInBackground()
		{
			if(view != null)
			{
				try
				{
					ViewTableModel tm = (ViewTableModel) view.getTable().getModel();
					tm.loadData(fields);
				}
				catch(Exception e)
				{
					publish(e);
				}
			}

			return null;
		}
	}
	
	private class FormWorker extends SwingWorker<Void, Exception>
	{
		protected Exception lastException;
		protected String url;
		protected int type;
		protected JComponent component;

		public FormWorker(String url, int type)
		{
			this.url = url;
			this.type = type;
		}

        protected Void doInBackground()
        {
        	component = getFormPanel(url);

            return null;
        }

        protected void done() 
        {
        	tp.setComponentAt(type, component);
        }
	}
	
	private class ContainerSelfListener implements ContainerEventListener
	{
		public void containerEvent(ContainerEvent event)
		{
			if(event instanceof ContainerLoadFinishedEvent)
			{
				renderTabs();
			}
			else if(event instanceof ContainerRequestEditorEvent)
			{
				openEditor(((ContainerRequestEditorEvent) event).getPage());
			}
		}
	}
	
	private class ContainerEditorListener implements ContainerEventListener
	{
		public void containerEvent(ContainerEvent event)
		{
			if(event instanceof ContainerLoadFinishedEvent)
			{
			}
		}
	}
}
