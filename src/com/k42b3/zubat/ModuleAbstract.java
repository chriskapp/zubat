
package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import com.k42b3.neodym.ServiceItem;

abstract public class ModuleAbstract extends JPanel implements com.k42b3.zubat.Container
{
	protected ArrayList<ContainerEventListener> listeners = new ArrayList<ContainerEventListener>();
	protected ServiceItem item;
	protected ArrayList<String> fields = new ArrayList<String>();

	protected Logger logger = Logger.getLogger("com.k42b3.zubat");
	
	public ModuleAbstract(ServiceItem item) throws Exception
	{
		super(new BorderLayout());

		this.item = item;

		// check whether we have an data type
		if(item.hasType("http://ns.amun-project.org/2011/amun/data/1.0"))
		{
			//throw new Exception("Service is not an data type");
		}

		// loading panel
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Loading ..."));

		this.add(panel, BorderLayout.CENTER);
	}

	public void onLoad(ArrayList<String> fields)
	{
		this.fields = fields;

		ModuleWorker worker = new ModuleWorker();
		worker.execute();
	}

	public void addContainerListener(ContainerEventListener listener)
	{
		listeners.add(listener);
	}

	public void fireContainer(com.k42b3.zubat.ContainerEvent event)
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).containerEvent(event);
		}
	}

	public ServiceItem getItem()
	{
		return item;
	}
	
	protected void loadFinished(JComponent component, Exception lastException)
	{
    	removeAll();
    	
    	if(component != null)
    	{
    		add(component, BorderLayout.CENTER);
    	}
    	else if(lastException != null)
    	{
    		add(getExceptionPanel(lastException), BorderLayout.CENTER);
    	}

    	validate();
	}

	protected JComponent getExceptionPanel(Exception e)
	{
		// get stacktrace as string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("<html>" + e.getMessage() + "<br /><pre>" + sw.toString() + "</pre></html>"));

		return new JScrollPane(panel);
	}

	/**
	 * Returns the title of the module
	 * 
	 * @return String
	 */
	abstract public String getTitle();

	/**
	 * Returns the main component of the service. Because of network io this
	 * could take longer because of this the work is done in the background
	 * 
	 * @return JComponent
	 */
	abstract public JComponent getComponent() throws Exception;

	private class ModuleWorker extends SwingWorker<Void, Exception>
	{
		protected JComponent component;
		protected Exception lastException;

        protected Void doInBackground()
        {
        	try
        	{
        		component = getComponent();
        	}
        	catch(Exception e)
        	{
        		publish(e);
        	}

            return null;
        }

        protected void process(List<Exception> chunks) 
        {
        	lastException = chunks.get(chunks.size() - 1);
        }

        protected void done() 
        {
        	loadFinished(component, lastException);
        }
	}
}
