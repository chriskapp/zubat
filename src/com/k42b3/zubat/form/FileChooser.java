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

package com.k42b3.zubat.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 * FileChooser
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class FileChooser extends JButton implements FormElementInterface
{
	private static final long serialVersionUID = 1L;

	protected File selectedFile;
	
	public FileChooser()
	{
		super("Browse");

		this.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);

				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					selectedFile = fc.getSelectedFile();

					setText("Browse (" + selectedFile.getName() + ")");
				}
			}

		});
	}

	public String getValue() 
	{
		return null;
	}
	
	public void setValue(String text)
	{
	}
	
	public File getSelectedFile()
	{
		return selectedFile;
	}
}
