package com.k42b3.zubat.amun.page;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.commons.lang3.StringEscapeUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.neodym.Http;
import com.k42b3.zubat.Configuration;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.basic.FormPanel;
import com.k42b3.zubat.form.FormElementInterface;
import com.k42b3.zubat.model.Page;

public class EditorPanel extends com.k42b3.zubat.basic.EditorPanel
{
	protected LinkedHashMap<String, FormElementInterface> fields;
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
		FormPanel form = new FormPanel(item.getUri() + "/form?method=update&id=" + getRecordId());
		fields = form.getRequestFields();

		JPanel panel = new JPanel(new BorderLayout());
		
		// textarea
		textarea = new RSyntaxTextArea();
		textarea.setText(fields.get("content").getValue());
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
		
	}
}
