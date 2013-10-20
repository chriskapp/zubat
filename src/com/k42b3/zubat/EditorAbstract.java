
package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import com.k42b3.zubat.model.Page;

abstract public class EditorAbstract extends JPanel
{
	protected Page page;

	protected Logger logger = Logger.getLogger("com.k42b3.zubat");
	
	public EditorAbstract(Page page)
	{
		super(new BorderLayout());

		this.page = page;

		// loading panel
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Loading ..."));

		this.add(panel, BorderLayout.CENTER);
	}

	public void onLoad()
	{
		EditorWorker worker = new EditorWorker();
		worker.execute();
	}

	public Page getPage()
	{
		return page;
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
	 * Returns the main component of the service. Because of network io this
	 * could take longer because of this the work is done in the background
	 * 
	 * @return JComponent
	 */
	abstract public JComponent getComponent() throws Exception;

	private class EditorWorker extends SwingWorker<Void, Exception>
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
