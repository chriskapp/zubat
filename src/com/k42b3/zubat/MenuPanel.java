/**
 * $Id: MenuPanel.java 212 2011-12-20 23:32:51Z k42b3.x@gmail.com $
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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import com.k42b3.neodym.ServiceItem;

/**
 * MenuPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 212 $
 */
public class MenuPanel extends JMenuBar
{
	private static final long serialVersionUID = 1L;

	private Zubat zubat;

	public MenuPanel(Zubat zubatInstance)
	{
		this.zubat = zubatInstance;

		this.add(this.buildContentMenu());
		this.add(this.buildUserMenu());
		this.add(this.buildSystemMenu());
		this.add(this.buildHelpMenu());
		this.add(Box.createHorizontalGlue());
		this.add(this.buildInfo());
	}

	protected JMenu buildContentMenu()
	{
		JMenu menu = new JMenu("Content");
		ServiceItem item;

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/content/page");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Page");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/content/page"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/content/gadget");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Gadget");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/content/gadget"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/media");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Media");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/media"));
				}

	        });
			
			menu.add(menuItem);
		}
		
		return menu;
	}

	protected JMenu buildUserMenu()
	{
		JMenu menu = new JMenu("User");
		ServiceItem item;
		
		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/account");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Account");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/account"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/group");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Group");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/group"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/activity");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Activity");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/activity"));
				}

	        });
			
			menu.add(menuItem);
		}
		
		return menu;
	}

	protected JMenu buildSystemMenu()
	{
		JMenu menu = new JMenu("System");
		ServiceItem item;
		
		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/core/service");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Service");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/core/service"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/core/registry");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Registry");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/core/registry"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/mail");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Mail");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/mail"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/oauth");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Oauth");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/oauth"));
				}

	        });
			
			menu.add(menuItem);
		}
		
		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/country");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Country");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/country"));
				}

	        });
			
			menu.add(menuItem);
		}

		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/phpinfo");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Phpinfo");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/phpinfo"));
				}

	        });
			
			menu.add(menuItem);
		}
		
		
		return menu;
	}
	
	protected JMenu buildServiceMenu()
	{
		JMenu menu = new JMenu("Service");
		ServiceItem item;
		
		item = Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/account");
		if(item != null)
		{
			JMenuItem menuItem = new JMenuItem("Account");
			menuItem.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					zubat.loadContainer(Zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/user/account"));
				}

	        });
			
			menu.add(menuItem);
		}
		
		return menu;
	}
	
	protected JMenu buildHelpMenu()
	{
		JMenu menu = new JMenu("Help");

		JMenuItem websiteItem = new JMenuItem("Website");
		websiteItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				String websiteUrl = "http://amun.phpsx.org";

				try
				{
					URI websiteUri = new URI(websiteUrl);

					if(Desktop.isDesktopSupported())
					{
						Desktop desktop = Desktop.getDesktop();

						if(desktop.isSupported(Desktop.Action.BROWSE))
						{
							desktop.browse(websiteUri);
						}
						else
						{
							JOptionPane.showMessageDialog(null, websiteUrl);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, websiteUrl);
					}
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);

					JOptionPane.showMessageDialog(null, websiteUrl);
				}
			}

		});

		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				StringBuilder out = new StringBuilder();

				out.append("Version: zubat (version: " + Zubat.version + ")\n");
				out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
				out.append("Website: http://code.google.com/p/delta-quadrant" + "\n");
				out.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
				out.append("\n");
				out.append("An java application to access the API of Amun (amun.phpsx.org). It is" + "\n");
				out.append("used to debug and control a website based on Amun. This is the reference" + "\n");
				out.append("implementation howto access the API so feel free to hack and extend." + "\n");

				JOptionPane.showMessageDialog(null, out, "About", JOptionPane.INFORMATION_MESSAGE);
			}

		});

		menu.add(websiteItem);
		menu.add(aboutItem);

		return menu;
	}

	protected JLabel buildInfo()
	{
		JLabel lblInfo = new JLabel(Zubat.getAccount().getName() + " (" + Zubat.getAccount().getGroup() + ")");
		lblInfo.setBorder(new EmptyBorder(4, 4, 4, 8));

		return lblInfo;
	}
}
