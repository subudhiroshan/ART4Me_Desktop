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
 * This class provides all the operations that can be performed on Prescriptions. 
 */
public class PrescriptionTab{
	
	static String[] freqCycles = {"  ", "Hourly", "Once Daily", "Twice Daily", "Thrice Daily"};
	static String[] dosageAmts = {"  ", "1", "2", "3", "4", "5"};
	static String[] hours = {"   ", "01", "02", "03", "04", "05", "06" ,"07", "08", "09", "10", "11", "12", "13", "14", "15", "16" ,"17", "18", "19", "20", "21", "22", "23"};
	static String[] minutes = {"   ", "00", "15", "30", "45"};
	public static String currentTable = "prescription_db";
	public static MyKVPair presHeaders = new MyKVPair();
	
	public PrescriptionTab() throws ClassNotFoundException{
		presHeaders.put("prescription_id", "Prescription ID");
		presHeaders.put("contact_id", "Contact ID");
		presHeaders.put("generic_name", "Generic Name");
		presHeaders.put("trade_name", "Trade Name");
		presHeaders.put("purpose", "Purpose");
		presHeaders.put("preference", "Preference");
		presHeaders.put("dosage", "Dosage");
		presHeaders.put("hours", "Time(HH)");
		presHeaders.put("minutes", "Time(MM)");
		presHeaders.put("frequency", "Frequency");
		JTableModelPresA.retrieveHeaders(currentTable);
		JTableModelPresA.retrieveData(currentTable);
		JTableModelPresI.retrieveHeaders(currentTable);
		JTableModelPresI.retrieveData(currentTable);
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
				AddPrescription addPresPrompt = new AddPrescription(GUIDB.baseFrame, true);
				addPresPrompt.setTitle("Add a Prescription");
				addPresPrompt.setSize(360,350);
				addPresPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				addPresPrompt.setResizable(true);
				addPresPrompt.setVisible(true);
				refreshTableA();
			}
		});
		
		mod.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ModPrescription modPresPrompt = new ModPrescription(GUIDB.baseFrame, true);
				modPresPrompt.setTitle("Modify a Presription");
				modPresPrompt.setSize(360,390);
				modPresPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				modPresPrompt.setResizable(true);
				modPresPrompt.setVisible(true);
				refreshTableA();
			}
		});
		
		del.addActionListener(new ActionListener()
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DelPrescription delPresPrompt = new DelPrescription(GUIDB.baseFrame, true);
				delPresPrompt.setTitle("Delete a Prescription");
				delPresPrompt.setSize(280,125);
				delPresPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				delPresPrompt.setResizable(false);
				delPresPrompt.setVisible(true);
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
			JTableModelPresA.retrieveData(currentTable);
			((AbstractTableModel) JTableModelPresA.model).fireTableDataChanged();
			JTableModelPresA.table.repaint();
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
				RetPrescription retPresPrompt = new RetPrescription(GUIDB.baseFrame, true);
				retPresPrompt.setTitle("Retrieve a Prescription");
				retPresPrompt.setSize(280,125);
				retPresPrompt.setLocationRelativeTo(GUIDB.baseFrame);
				retPresPrompt.setResizable(false);
				retPresPrompt.setVisible(true);
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
			JTableModelPresI.retrieveData(currentTable);
			((AbstractTableModel) JTableModelPresI.model).fireTableDataChanged();
			JTableModelPresA.table.repaint();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public JTabbedPane prePanel(){
		JTabbedPane prescriptionTabs = new JTabbedPane(JTabbedPane.BOTTOM);
		prescriptionTabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		JPanel presPanelA = new JPanel();
		presPanelA.setLayout(new BorderLayout());
		presPanelA.add(JTableModelPresA.jTableModel(),BorderLayout.CENTER);
		presPanelA.add(buttonPanelA(), BorderLayout.SOUTH);
		
		JPanel presPanelI = new JPanel();
		presPanelI.setLayout(new BorderLayout());
		presPanelI.add(JTableModelPresI.jTableModel(),BorderLayout.CENTER);
		presPanelI.add(buttonPanelI(), BorderLayout.SOUTH);
		
		prescriptionTabs.addTab("Inactive", presPanelI);
		prescriptionTabs.addTab("Active", presPanelA);
		prescriptionTabs.setSelectedComponent(presPanelA);
		
		return prescriptionTabs;
	}
	
}