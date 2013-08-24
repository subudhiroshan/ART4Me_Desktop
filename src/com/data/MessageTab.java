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
 * This class provides all the operations that can be performed on Messages.
 */
public class MessageTab{
	
	public static String currentTable = "message_db";
	public static MyKVPair msgHeaders = new MyKVPair();
	
	public MessageTab() throws ClassNotFoundException{
		msgHeaders.put("message_id", "Message ID");
		msgHeaders.put("message_category", "Message Category");
		msgHeaders.put("message_contents", "Message Contents");
		JTableModelMsgA.retrieveHeaders(currentTable);
		JTableModelMsgA.retrieveData(currentTable);
		JTableModelMsgI.retrieveHeaders(currentTable);
		JTableModelMsgI.retrieveData(currentTable);
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
		
		bPanel.add(add);
		bPanel.add(mod);
		bPanel.add(del);
		bPanel.add(ref);
		
		add.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AddMessage addMsgPrompt = new AddMessage(GUIDB.baseFrame, true);
				addMsgPrompt.setTitle("Add a Message");
				addMsgPrompt.setSize(320,230);
				addMsgPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				addMsgPrompt.setResizable(false);
				addMsgPrompt.setVisible(true);
				refreshTableA();
			}
		});
		
		mod.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ModMessage modMsgPrompt = new ModMessage(GUIDB.baseFrame, true);
				modMsgPrompt.setTitle("Modify a Message");
				modMsgPrompt.setSize(350,275);
				modMsgPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				modMsgPrompt.setResizable(false);
				modMsgPrompt.setVisible(true);
				refreshTableA();
			}
		});
		
		del.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DelMessage delMsgPrompt = new DelMessage(GUIDB.baseFrame, true);
				delMsgPrompt.setTitle("Delete a Message");
				delMsgPrompt.setSize(270,120);
				delMsgPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				delMsgPrompt.setResizable(false);
				delMsgPrompt.setVisible(true);
				refreshTableA();
			}
		});
		
		ref.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTableA();
			}
		});

		return bPanel;
	}
	
	static void refreshTableA(){
		try {
			JTableModelMsgA.retrieveData(currentTable);
			((AbstractTableModel) JTableModelMsgA.model).fireTableDataChanged();
			JTableModelMsgA.table.repaint();
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
		
		bPanel.add(ret);
		bPanel.add(ref);
		
		ret.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				RetMessage retMsgPrompt = new RetMessage(GUIDB.baseFrame, true);
				retMsgPrompt.setTitle("Retrieve a Message");
				retMsgPrompt.setSize(270,120);
				retMsgPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				retMsgPrompt.setResizable(false);
				retMsgPrompt.setVisible(true);
				refreshTableI();
			}
		});
		
		ref.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshTableI();
			}
		});

		return bPanel;
	}
	
	static void refreshTableI(){
		try {
			JTableModelMsgI.retrieveData(currentTable);
			((AbstractTableModel) JTableModelMsgI.model).fireTableDataChanged();
			JTableModelMsgI.table.repaint();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public JTabbedPane messagePanel(){
		JTabbedPane messageTabs = new JTabbedPane(JTabbedPane.BOTTOM);
		messageTabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JPanel msgPanelA = new JPanel();
		msgPanelA.setLayout(new BorderLayout());
		msgPanelA.add(JTableModelMsgA.jTableModel(),BorderLayout.CENTER);
		msgPanelA.add(buttonPanelA(), BorderLayout.SOUTH);
		
		JPanel msgPanelI = new JPanel();
		msgPanelI.setLayout(new BorderLayout());
		msgPanelI.add(JTableModelMsgI.jTableModel(),BorderLayout.CENTER);
		msgPanelI.add(buttonPanelI(), BorderLayout.SOUTH);
		
		messageTabs.addTab("Inactive", msgPanelI);
		messageTabs.addTab("Active", msgPanelA);
		messageTabs.setSelectedComponent(msgPanelA);
		
		return messageTabs;
	}
}
