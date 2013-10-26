package com.k42b3.zubat.basic;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.k42b3.zubat.Zubat;

public class ButtonsPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private ViewTableModel tm;
	private JButton btnPrev;
	private JButton btnNext;
	private JLabel lblPagination;

	public ButtonsPanel(ViewTableModel model)
	{
		this.tm = model;
		btnPrev = new JButton("Prev");
		btnNext = new JButton("Next");
		lblPagination = new JLabel();

		btnPrev.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ex) 
			{
				try
				{
					tm.prevPage();
				}
				catch(Exception e)
				{
					Zubat.handleException(e);
				}
			}

		});

		btnNext.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ex) 
			{
				try
				{
					tm.nextPage();
				}
				catch(Exception e)
				{
					Zubat.handleException(e);
				}
			}

		});

		this.setLayout(new FlowLayout(FlowLayout.CENTER));

		this.add(btnPrev);
		this.add(lblPagination);
		this.add(btnNext);
	}

	public void validate()
	{
		int pageResults = tm.getStartIndex() + tm.getItemsPerPage();
		int count = pageResults > tm.getTotalResults() ? tm.getTotalResults() : pageResults;


		lblPagination.setText(tm.getStartIndex() + " - " + count + " of " + tm.getTotalResults());

		if(tm.getStartIndex() == 0)
		{
			btnPrev.setEnabled(false);
		}
		else
		{
			btnPrev.setEnabled(true);
		}

		if(tm.getTotalResults() < tm.getItemsPerPage() || pageResults >= tm.getTotalResults())
		{
			btnNext.setEnabled(false);
		}
		else
		{
			btnNext.setEnabled(true);
		}

		super.validate();
	}
}
