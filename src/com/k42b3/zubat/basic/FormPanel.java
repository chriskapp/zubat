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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.data.Endpoint;
import com.k42b3.neodym.data.Message;
import com.k42b3.neodym.data.Record;
import com.k42b3.zubat.Zubat;
import com.k42b3.zubat.container.ContainerEventListener;
import com.k42b3.zubat.container.ContainerExceptionEvent;
import com.k42b3.zubat.container.ContainerSuccessEvent;
import com.k42b3.zubat.form.FileChooser;
import com.k42b3.zubat.form.FormElementInterface;
import com.k42b3.zubat.form.Input;
import com.k42b3.zubat.form.Select;
import com.k42b3.zubat.form.SelectItem;
import com.k42b3.zubat.form.Textarea;

/**
 * FormPanel
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class FormPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static final int CREATE = 0x1;
	public static final int UPDATE = 0x2;
	public static final int DELETE = 0x3;

	protected Endpoint api;
	protected int method;
	protected int id;
	protected Map<String, String> params;

	protected String requestMethod;
	protected String requestUrl;

	protected LinkedHashMap<String, FormElementInterface> requestFields = new LinkedHashMap<String, FormElementInterface>();
	protected ArrayList<ContainerEventListener> listeners = new ArrayList<ContainerEventListener>();

	protected Container body;
	protected JButton btnSend;

	protected SearchPanel searchPanel;
	protected HashMap<String, ReferenceItem> referenceFields = new HashMap<String, ReferenceItem>();
	
	protected Logger logger = Logger.getLogger("com.k42b3.zubat");

	public FormPanel(Endpoint api, int method, int id, Map<String, String> params) throws Exception
	{
		this.api = api;
		this.method = method;
		this.id = id;
		this.params = params;

		this.setLayout(new BorderLayout());

		this.buildComponent();
	}

	public FormPanel(Endpoint api, int method, int id) throws Exception
	{
		this(api, method, id, null);
	}

	public FormPanel(Endpoint api, int method, Map<String, String> params) throws Exception
	{
		this(api, method, 0, params);
	}

	public FormPanel(Endpoint api, int method) throws Exception
	{
		this(api, method, 0);
	}

	public LinkedHashMap<String, FormElementInterface> getRequestFields()
	{
		return requestFields;
	}
	
	public Message sendRequest() throws Exception
	{
		Set<String> keys = requestFields.keySet();

		boolean hasFileUpload = false;
		for(String key : keys)
		{
			if(requestFields.get(key) instanceof FileChooser)
			{
				hasFileUpload = true;
				break;
			}
		}
		
		if(!hasFileUpload)
		{
			Record record = new Record();
			
			for(String key : keys)
			{
				record.put(key, requestFields.get(key).getValue());
			}

			Message message;
			if(method == CREATE)
			{
				message = api.create(record);
			}
			else if(method == UPDATE)
			{
				message = api.update(record);
			}
			else if(method == DELETE)
			{
				message = api.delete(record);
			}
			else
			{
				throw new Exception("Unknown method");
			}

			if(message.hasSuccess())
			{
				fireContainer(new ContainerSuccessEvent());

				JOptionPane.showMessageDialog(null, message.getText(), "Response", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				throw new Exception(message.getText());
			}

			return message;
		}
		else
		{
			// build entity
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
					
			for(String key : keys)
			{
				if(requestFields.get(key) instanceof FileChooser)
				{
					FileChooser fc = (FileChooser) requestFields.get(key);

					entityBuilder.addPart(key, new FileBody(fc.getSelectedFile()));
				}
				else
				{
					entityBuilder.addTextBody(key, requestFields.get(key).getValue());
				}
			}
			
			// send request
			Document response = Zubat.getHttp().requestXml(Http.POST, api.getService().getUri(), null, entityBuilder.build());
			
			// parse response
			Message message = Message.parseMessage(response.getDocumentElement());

			if(message.hasSuccess())
			{
				fireContainer(new ContainerSuccessEvent());

				JOptionPane.showMessageDialog(null, message.getText(), "Response", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				throw new Exception(message.getText());
			}
			
			return message;
		}
	}

	public void addContainerListener(ContainerEventListener listener)
	{
		listeners.add(listener);
	}

	public void fireContainer(com.k42b3.zubat.container.ContainerEvent event)
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).containerEvent(event);
		}
	}
	
	protected void buildComponent() throws Exception
	{
		// form panel
		body = new JPanel();
		body.setLayout(new BorderLayout());
		//body.setLayout(new GridLayout(0, 1));

		// load data
		String method = this.method == CREATE ? "create" : (this.method == UPDATE ? "update" : (this.method == DELETE ? "delete" : "create"));
		String url = api.getService().getUri() + "/form?method=" + method;

		if(id > 0)
		{
			url = Http.appendQuery(url, "id=" + id);
		}

		if(params != null && params.size() > 0)
		{
			Iterator<Entry<String, String>> it = params.entrySet().iterator();
			
			while(it.hasNext())
			{
				Entry<String, String> entry = it.next();

				url = Http.appendQuery(url, entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
		}

		this.request(url);

		// add panel
		this.add(new JScrollPane(body), BorderLayout.CENTER);

		// buttons
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEADING));

		this.btnSend = new JButton("Send");
		this.btnSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					sendRequest();
				}
				catch(Exception ex)
				{
					fireContainer(new ContainerExceptionEvent(ex));
					
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Response", JOptionPane.ERROR_MESSAGE);

					Zubat.handleException(ex);
				}
			}

		});

		buttons.add(this.btnSend);

		this.add(buttons, BorderLayout.SOUTH);		
	}
	
	protected void request(String url) throws Exception
	{
		// request
		Document doc = Zubat.getHttp().requestXml(Http.GET, url);

		// parse response
		try
		{
			Element rootElement = (Element) doc.getDocumentElement();
			rootElement.normalize();

			// get message
			Message msg = Message.parseMessage(rootElement);

			if(msg != null && !msg.hasSuccess())
			{
				JPanel errorPanel = new JPanel();
				errorPanel.setLayout(new FlowLayout());
				errorPanel.add(new JLabel(msg.getText()));

				body.add(errorPanel, BorderLayout.CENTER);
			}
			else
			{
				body.add(this.parse(rootElement), BorderLayout.CENTER);
			}
		}
		catch(SAXException e)
		{
			JLabel error = new JLabel(e.getMessage());

			body.add(error, BorderLayout.CENTER);
		}
	}

	protected Container parse(Node node) throws Exception
	{
		if(node.getNodeType() == Node.ELEMENT_NODE)
		{
			Node nodeClass = this.getChildNode(node, "class");
			String nodeName = nodeClass.getTextContent().toLowerCase();

			if(nodeName.equals("form"))
			{
				return parseForm(node);
			}
			else if(nodeName.equals("input"))
			{
				return parseInput(node);
			}
			else if(nodeName.equals("textarea"))
			{
				return parseTextarea(node);
			}
			else if(nodeName.equals("select"))
			{
				return parseSelect(node);
			}
			else if(nodeName.equals("tabbedpane"))
			{
				return parseTabbedPane(node);
			}
			else if(nodeName.equals("panel"))
			{
				return parsePanel(node);
			}
			else if(nodeName.equals("reference"))
			{
				return parseReference(node);
			}
			/*
			else if(nodeName.equals("checkboxlist"))
			{
				return parseCheckboxList(node);
			}
			*/
			else if(nodeName.equals("captcha"))
			{
				return parseCaptcha(node);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	protected Container parseForm(Node node) throws Exception
	{
		Node nodeMethod = this.getChildNode(node, "method");
		Node nodeAction = this.getChildNode(node, "action");
		Node nodeItem = this.getChildNode(node, "item");

		if(nodeMethod != null)
		{
			requestMethod = nodeMethod.getTextContent();
		}
		else
		{
			throw new Exception("Request method is missing");
		}
		
		if(nodeAction != null)
		{
			requestUrl = nodeAction.getTextContent();
		}
		else
		{
			throw new Exception("Request value is missing");
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		Container con = this.parse(nodeItem); 

		if(con != null)
		{
			panel.add(con, BorderLayout.CENTER);
		}

		return con;
	}

	protected Container parseInput(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");
		Node nodeDisabled = this.getChildNode(node, "disabled");
		Node nodeType = this.getChildNode(node, "type");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		if(nodeType != null && nodeType.getTextContent().equals("file"))
		{
			FileChooser fileSelect = new FileChooser();

			item.add(label);
			item.add(fileSelect);
			
			requestFields.put(nodeRef.getTextContent(), fileSelect);
		}
		else
		{
			Input input = new Input();
			input.setPreferredSize(new Dimension(300, 22));
			
			if(nodeValue != null)
			{
				input.setText(nodeValue.getTextContent());
			}

			if(nodeDisabled != null && nodeDisabled.getTextContent().equals("true"))
			{
				input.setEnabled(false);
			}
			
			item.add(label);
			item.add(input);
			
			requestFields.put(nodeRef.getTextContent(), input);
		}

		if(nodeType != null && nodeType.getTextContent().equals("hidden"))
		{
			return null;
		}
		else
		{
			return item;
		}
	}

	protected Container parseSelect(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");
		Node nodeDisabled = this.getChildNode(node, "disabled");
		Node nodeChildren = this.getChildNode(node, "children");
		
		JPanel item = new JPanel();
		item.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		if(nodeChildren != null)
		{
			SelectItem[] items = this.getSelectOptions(nodeChildren);
			
			if(items != null)
			{
				DefaultComboBoxModel<SelectItem> model = new DefaultComboBoxModel<SelectItem>(items);

				Select input = new Select(model);
				input.setPreferredSize(new Dimension(300, 22));

				// set select if value available
				if(nodeValue != null)
				{
					for(int i = 0; i < model.getSize(); i++)
					{
						SelectItem boxItem = (SelectItem) model.getElementAt(i);

						if(boxItem.getKey().equals(nodeValue.getTextContent()))
						{
							input.setSelectedIndex(i);
						}
					}
				}


				if(nodeDisabled != null && nodeDisabled.getTextContent().equals("true"))
				{
					input.setEnabled(false);
				}

				item.add(label);
				item.add(input);


				requestFields.put(nodeRef.getTextContent(), input);

				return item;
			}
		}

		return null;
	}

	protected Container parseTextarea(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		Textarea input = new Textarea();
		RTextScrollPane scpInput = new RTextScrollPane(input);
		scpInput.setPreferredSize(new Dimension(300, 200));

		if(nodeValue != null)
		{
			input.setText(nodeValue.getTextContent());
		}

		item.add(label);
		item.add(scpInput);

		requestFields.put(nodeRef.getTextContent(), input);

		return item;
	}

	protected Container parseTabbedPane(Node node) throws Exception
	{
		JTabbedPane item = new JTabbedPane();
		Node nodeChildren = this.getChildNode(node, "children");

		if(nodeChildren != null)
		{
			ArrayList<Node> items = this.getChildNodes(nodeChildren, "item");

			for(int i = 0; i < items.size(); i++)
			{
				Node nodeLabel = this.getChildNode(items.get(i), "label");

				if(nodeLabel != null)
				{
					Container con = this.parse(items.get(i)); 

					if(con != null)
					{
						item.addTab(nodeLabel.getTextContent(), con);
					}
				}
			}
		}

		return item;
	}

	protected Container parsePanel(Node node) throws Exception
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel item = new JPanel();
		item.setLayout(new BoxLayout(item, BoxLayout.PAGE_AXIS));

		Node nodeChildren = this.getChildNode(node, "children");

		if(nodeChildren != null)
		{
			ArrayList<Node> items = this.getChildNodes(nodeChildren, "item");

			for(int i = 0; i < items.size(); i++)
			{
				Container con = this.parse(items.get(i)); 

				if(con != null)
				{
					item.add(con);
				}
			}
		}

		panel.add(item);

		return panel;
	}

	protected Container parseReference(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");
		Node nodeDisabled = this.getChildNode(node, "disabled");
		Node nodeValueField = this.getChildNode(node, "valueField");
		Node nodeLabelField = this.getChildNode(node, "labelField");
		Node nodeSrc = this.getChildNode(node, "src");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		Input input = new Input();
		input.setPreferredSize(new Dimension(255, 22));

		JButton button = new JButton(nodeRef.getTextContent());
		button.setPreferredSize(new Dimension(40, 22));
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					JButton source = (JButton) e.getSource();
					String key = source.getText();

					if(referenceFields.containsKey(key))
					{
						ReferenceItem item = referenceFields.get(key);
						SearchPanel panel = item.getPanel();

						if(panel == null)
						{
							panel = new SearchPanel(item);

							item.setPanel(panel);
						}

						panel.setVisible(true);

						panel.toFront();
					}
				}
				catch(Exception ex)
				{
					logger.warning(ex.getMessage());
				}
			}

		});

		if(nodeValue != null)
		{
			input.setText(nodeValue.getTextContent());
		}

		if(nodeDisabled != null && nodeDisabled.getTextContent().equals("true"))
		{
			input.setEnabled(false);
		}

		item.add(label);
		item.add(input);
		item.add(button);


		referenceFields.put(nodeRef.getTextContent(), new ReferenceItem(nodeValueField.getTextContent(), nodeLabelField.getTextContent(), nodeSrc.getTextContent(), input));

		requestFields.put(nodeRef.getTextContent(), input);

		return item;
	}

	/*
	protected Container parseCheckboxList(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		//Node nodeValue = this.getChildNode(node, "value");
		//Node nodeDisabled = this.getChildNode(node, "disabled");
		Node nodeSrc = this.getChildNode(node, "src");

		JPanel item = new JPanel();
		item.setLayout(new BorderLayout());

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		CheckboxList checkboxlist = new CheckboxList(nodeSrc.getTextContent());
		checkboxlist.setPreferredSize(new Dimension(255, 22));

		item.add(checkboxlist, BorderLayout.CENTER);


		requestFields.put(nodeRef.getTextContent(), checkboxlist);

		return item;
	}
	*/

	protected Container parseCaptcha(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");
		Node nodeDisabled = this.getChildNode(node, "disabled");
		Node nodeType = this.getChildNode(node, "type");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		Input input = new Input();
		input.setPreferredSize(new Dimension(300, 22));
		
		if(nodeValue != null)
		{
			input.setText(nodeValue.getTextContent());
		}

		if(nodeDisabled != null && nodeDisabled.getTextContent().equals("true"))
		{
			input.setEnabled(false);
		}

		item.add(label);
		item.add(input);


		requestFields.put(nodeRef.getTextContent(), input);

		if(nodeType != null && nodeType.getTextContent().equals("hidden"))
		{
			return null;
		}
		else
		{
			return item;
		}
	}

	protected SelectItem[] getSelectOptions(Node node)
	{
		ArrayList<Node> options = this.getChildNodes(node, "item");
		SelectItem[] items = new SelectItem[options.size()];

		for(int i = 0; i < options.size(); i++)
		{
			Node nodeLabel = this.getChildNode(options.get(i), "label");
			Node nodeValue = this.getChildNode(options.get(i), "value");

			if(nodeLabel != null && nodeValue != null)
			{
				items[i] = new SelectItem(nodeValue.getTextContent(), nodeLabel.getTextContent());
			}
		}

		return items;
	}
	
	protected ArrayList<Node> getChildNodes(Node node, String nodeName)
	{
		ArrayList<Node> nodes = new ArrayList<Node>();

		NodeList childList = node.getChildNodes();

		for(int i = 0; i < childList.getLength(); i++)
		{
			Node childNode = childList.item(i);

			if(childNode.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			if(childNode.getNodeName().equals(nodeName))
			{
				nodes.add(childNode);
			}
		}

		return nodes;
	}

	protected Node getChildNode(Node node, String nodeName)
	{
		NodeList childList = node.getChildNodes();

		for(int i = 0; i < childList.getLength(); i++)
		{
			Node childNode = childList.item(i);

			if(childNode.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			if(childNode.getNodeName().equals(nodeName))
			{
				return childNode;
			}
		}

		return null;
	}
}
