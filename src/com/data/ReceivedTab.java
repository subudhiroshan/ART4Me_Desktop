package com.data;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class provides all the operations that can be performed on Received Messages.
 */
public class ReceivedTab{
	
	public static String currentTable = "received_message_log";
	public static MyKVPair recdHeaders = new MyKVPair();
	
	public ReceivedTab() throws ClassNotFoundException{
		recdHeaders.put("received_message_id", "Sent Message ID");
		recdHeaders.put("contact_id", "Contact ID");
		recdHeaders.put("msg_received", "Time Received");
		recdHeaders.put("message_contents", "Message Contents");
		JTableModelRecd.retrieveHeaders(currentTable);
		JTableModelRecd.retrieveData(currentTable);
	}

	private static JPanel buttonPanel(){
		JPanel bPanel = new JPanel();
		
		JButton add = new JButton(new ImageIcon(ClassLoader.getSystemResource("Add.png")));
		add.setToolTipText("Add..");
		JButton mod = new JButton(new ImageIcon(ClassLoader.getSystemResource("Modify.png")));
		mod.setToolTipText("Modify..");
		JButton del = new JButton(new ImageIcon(ClassLoader.getSystemResource("Delete.png")));
		del.setToolTipText("Delete..");
		JButton ref = new JButton(new ImageIcon(ClassLoader.getSystemResource("Refresh.png")));
		ref.setToolTipText("Refresh");
		add.setEnabled(false);
		mod.setEnabled(false);
		del.setEnabled(false);
		
		bPanel.add(add);
		bPanel.add(mod);
		bPanel.add(del);
		bPanel.add(ref);
		
		add.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTable();
			}
		});
		
		mod.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTable();
			}
		});
		
		del.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTable();
			}
		});
		
		ref.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTable();
			}
		});
		
		return bPanel;
	}
	
	public static void refreshTable(){
		try {
			JTableModelRecd.retrieveData(currentTable);
			((AbstractTableModel) JTableModelRecd.model).fireTableDataChanged();
			JTableModelRecd.table.repaint();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public JPanel recdPanel(){
		JPanel rPanel = new JPanel();
		rPanel.setLayout(new BorderLayout());
		rPanel.add(JTableModelRecd.jTableModel(),BorderLayout.CENTER);
		rPanel.add(buttonPanel(), BorderLayout.SOUTH);
		return rPanel;
	}
}