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

package com.k42b3.zubat;

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Entry 
{
	public static void main(String[] args)
	{
		try
		{
			// init config file
			File configFile = getConfigFile(args);

			if(configFile == null)
			{
				configFile = new File("zubat.conf.xml");
			}

			Configuration.initInstance(configFile);


			// start
			if(isAuth(args))
			{
				SwingUtilities.invokeLater(new Runnable() {

					public void run()
					{
						Entry.setLookAndFeel();

						Auth panel = new Auth();
						panel.setVisible(true);
					}

				});
			}
			else
			{
				SwingUtilities.invokeLater(new Runnable() {

					public void run()
					{
						Entry.setLookAndFeel();

						Zubat panel = new Zubat();
						panel.setVisible(true);
					}

				});
			}
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	public static File getConfigFile(String[] args)
	{
		boolean foundConfig = false;

		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equals("--config"))
			{
				foundConfig = true;

				continue;
			}

			if(foundConfig)
			{
				return new File(args[i]);
			}
		}

		return null;
	}

	public static boolean isAuth(String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equals("--auth"))
			{
				return true;
			}
		}

		return false;
	}

	public static void setLookAndFeel()
	{
		try
		{
			//WebLookAndFeel.install();
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

			//UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
			//UIManager.setLookAndFeel(lookAndFeel);
		}
		catch(Exception e)
		{
		}
	}
}
