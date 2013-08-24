package com.data;


import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class provides all the operations that can be performed on Sent Messages.
 */
public class SentTab{
	public static String currentTable = "sent_message_log";
	public static MyKVPair sentHeaders = new MyKVPair();
	
	public SentTab() throws ClassNotFoundException{
		sentHeaders.put("sent_message_id", "Sent Message ID");
		sentHeaders.put("contact_id", "Contact ID");
		sentHeaders.put("message_id", "Message ID");
		sentHeaders.put("message_category", "Message Category");
		sentHeaders.put("message_contents", "Message Contents");
		sentHeaders.put("prescription_id", "Prescription ID");
		sentHeaders.put("recipient_number", "Recipient Number");
		sentHeaders.put("timestamp", "Time Stamp");
		sentHeaders.put("status", "Status");
		sentHeaders.put("msg_delivered", "Time Delivered");
		sentHeaders.put("msg_read", "Time Read");
		sentHeaders.put("pill_taken", "Pill Taken?");
		sentHeaders.put("reminder", "Reminder");
		JTableModelSentA.retrieveHeaders(currentTable);
		JTableModelSentA.retrieveData(currentTable);
		JTableModelSentI.retrieveHeaders(currentTable);
		JTableModelSentI.retrieveData(currentTable);
	}

	private static JPanel buttonPanelA(){
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
		
		ref.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTableA();
			}
		});
		
		return bPanel;
	}
	
	public static void refreshTableA(){
		try {
			JTableModelSentA.retrieveData(currentTable);
			((AbstractTableModel) JTableModelSentA.model).fireTableDataChanged();
			JTableModelSentA.table.repaint();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static JPanel buttonPanelI(){
		JPanel bPanel = new JPanel();
		
		JButton ret = new JButton(new ImageIcon(ClassLoader.getSystemResource("Retrieve.png")));
		ret.setToolTipText("Retrieve..");
		JButton ref = new JButton(new ImageIcon(ClassLoader.getSystemResource("Refresh.png")));
		ref.setToolTipText("Refresh");
		ret.setEnabled(false);
		
		bPanel.add(ret);
		bPanel.add(ref);
		
		ref.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTableI();
			}
		});
		
		return bPanel;
	}
	
	public static void refreshTableI(){
		try {
			JTableModelSentI.retrieveData(currentTable);
			((AbstractTableModel) JTableModelSentI.model).fireTableDataChanged();
			JTableModelSentI.table.repaint();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public JTabbedPane sentPanel(){
		JTabbedPane sentTabs = new JTabbedPane(JTabbedPane.BOTTOM);
		sentTabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JPanel sentPanelA = new JPanel();
		sentPanelA.setLayout(new BorderLayout());
		sentPanelA.add(JTableModelSentA.jTableModel(),BorderLayout.CENTER);
		sentPanelA.add(buttonPanelA(), BorderLayout.SOUTH);
		
		JPanel sentPanelI = new JPanel();
		sentPanelI.setLayout(new BorderLayout());
		sentPanelI.add(JTableModelSentI.jTableModel(),BorderLayout.CENTER);
		sentPanelI.add(buttonPanelI(), BorderLayout.SOUTH);
		
		sentTabs.addTab("Inactive", sentPanelI);
		sentTabs.addTab("Active", sentPanelA);
		sentTabs.setSelectedComponent(sentPanelA);
		
		return sentTabs;
	}
	
}