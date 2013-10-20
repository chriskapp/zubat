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

import com.k42b3.zubat.form.Input;

/**
 * ReferenceItem
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/zubat
 */
public class ReferenceItem 
{
	private String valueField;
	private String labelField;
	private String src;
	private Input input;
	private SearchPanel panel;

	public ReferenceItem(String valueField, String labelField, String src, Input input)
	{
		this.setValueField(valueField);
		this.setLabelField(labelField);
		this.setSrc(src);
		this.setInput(input);
	}

	public String getValueField() 
	{
		return valueField;
	}

	public void setValueField(String valueField) 
	{
		this.valueField = valueField;
	}

	public String getLabelField() 
	{
		return labelField;
	}

	public void setLabelField(String labelField) 
	{
		this.labelField = labelField;
	}

	public String getSrc() 
	{
		return src;
	}

	public void setSrc(String src) 
	{
		this.src = src;
	}

	public Input getInput() 
	{
		return input;
	}

	public void setInput(Input input) 
	{
		this.input = input;
	}

	public SearchPanel getPanel() 
	{
		return panel;
	}

	public void setPanel(SearchPanel panel) 
	{
		this.panel = panel;
	}
}
