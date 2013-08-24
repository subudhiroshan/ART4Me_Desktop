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
 * This class provides all the operations that can be performed on Contacts.
 */
public class ContactTab{
	
	static String[] genderTypes = {" ", "M", "F"};
	static String[] usStates = {"  ", "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina" ,"North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
	public static String currentTable = "contacts_db";
	public static MyKVPair conHeaders = new MyKVPair();
	
	public ContactTab() throws ClassNotFoundException{
		conHeaders.put("contact_id", "Contact ID");
		conHeaders.put("first_name", "First Name");
		conHeaders.put("last_name", "Last Name");
		conHeaders.put("gender", "Gender");
		conHeaders.put("primary_phone", "Primary Phone");
		conHeaders.put("smart", "Smart?");
		conHeaders.put("home_address", "Home Address");
		conHeaders.put("home_city", "Home City");
		conHeaders.put("home_state", "Home State");
		conHeaders.put("home_zip", "Home ZIP");
		JTableModelConA.retrieveHeaders(currentTable);
		JTableModelConA.retrieveData(currentTable);
		JTableModelConI.retrieveHeaders(currentTable);
		JTableModelConI.retrieveData(currentTable);
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
				AddContact addContPrompt = new AddContact(GUIDB.baseFrame, true);
				addContPrompt.setTitle("Add a Contact");
				addContPrompt.setSize(355,500);
				addContPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				addContPrompt.setResizable(false);
				addContPrompt.setVisible(true);
				refreshTableA();
			}
		});
		
		mod.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ModContact modContPrompt = new ModContact(GUIDB.baseFrame, true);
				modContPrompt.setTitle("Modify a Contact");
				modContPrompt.setSize(355,500);
				modContPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				modContPrompt.setResizable(false);
				modContPrompt.setVisible(true);
				refreshTableA();
			}
		});
		
		del.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DelContact delContPrompt = new DelContact(GUIDB.baseFrame, true);
				delContPrompt.setTitle("Delete a Contact");
				delContPrompt.setSize(270,125);
				delContPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				delContPrompt.setResizable(false);
				delContPrompt.setVisible(true);
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
			GUIDB.loadContacts();
			JTableModelConA.retrieveData(currentTable);
			((AbstractTableModel) JTableModelConA.model).fireTableDataChanged();
			JTableModelConA.table.repaint();
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
				RetContact retContPrompt = new RetContact(GUIDB.baseFrame, true);
				retContPrompt.setTitle("Retrieve a Contact");
				retContPrompt.setSize(270,125);
				retContPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				retContPrompt.setResizable(false);
				retContPrompt.setVisible(true);
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
			GUIDB.loadContacts();
			JTableModelConI.retrieveData(currentTable);
			((AbstractTableModel) JTableModelConI.model).fireTableDataChanged();
			JTableModelConI.table.repaint();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public JTabbedPane conPanel(){
		JTabbedPane contactTabs = new JTabbedPane(JTabbedPane.BOTTOM);
		contactTabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JPanel conPanelA = new JPanel();
		conPanelA.setLayout(new BorderLayout());
		conPanelA.add(JTableModelConA.jTableModel(),BorderLayout.CENTER);
		conPanelA.add(buttonPanelA(), BorderLayout.SOUTH);
		
		JPanel conPanelI = new JPanel();
		conPanelI.setLayout(new BorderLayout());
		conPanelI.add(JTableModelConI.jTableModel(),BorderLayout.CENTER);
		conPanelI.add(buttonPanelI(), BorderLayout.SOUTH);
		
		contactTabs.addTab("Inactive", conPanelI);
		contactTabs.addTab("Active", conPanelA);
		contactTabs.setSelectedComponent(conPanelA);
		
		return contactTabs;
	}
}
