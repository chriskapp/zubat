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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.alee.laf.WebLookAndFeel;

/**
 * Entry
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class Entry 
{
	public static boolean debugMode = false;

	public static void main(String[] args)
	{
		try
		{
			// parse args
			Options options = new Options();
			options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("path to the configuration xml").create("config"));
			options.addOption("debug", false, "enables debug mode");

			CommandLineParser parser = new GnuParser();
			CommandLine line = parser.parse(options, args);

			// init config file
			File configFile;
			
			if(line.hasOption("config"))
			{
				configFile = new File(line.getOptionValue("config"));
			}
			else
			{
				configFile = new File("zubat.conf.xml");
			}

			if(line.hasOption("debug"))
			{
				debugMode = true;
			}

			Configuration config = Configuration.initInstance(configFile);

			if(!config.hasToken())
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

						Zubat panel = new Zubat(debugMode);
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

	public static void setLookAndFeel()
	{
		try
		{
			WebLookAndFeel.install();
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

			UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
			//UIManager.setLookAndFeel(lookAndFeel);
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}
}
