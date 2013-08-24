package com.data;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class  provides a prompt to modify a Prescription from the system.
 */
public class ModPrescription extends JDialog {

	private static final long serialVersionUID = 1L;
	
	static String oldContId;
	static String oldPresId;
	static String oldPresGN;
	static String oldPresTN;
	static String oldPresPur;
	static String oldPref;
	static String oldPresDos;
	static String oldPresHour;
	static String oldPresMin;
	static String oldPresFreq;
	
	Connection connection;
	Statement stmtOld, stmtNew, stmtSent;
		
	JPanel presIdPnl = new JPanel();
	JLabel presIdLbl = new JLabel("Prescription Id");
	JTextField presId = new JTextField(5);
	JLabel enter = new JLabel(new ImageIcon(ClassLoader.getSystemResource("Enter.png")));
	
	JPanel contIdPnl = new JPanel();
	JLabel contIdLbl = new JLabel("Contact Id");
	JTextField contId = new JTextField(5);
	
	JPanel presGNPnl = new JPanel();
	JLabel presGNLbl = new JLabel("Generic Name");
	JTextField presGN = new JTextField(20);
	
	JPanel presTNPnl = new JPanel();
	JLabel presTNLbl = new JLabel("Trade Name");
	JTextField presTN = new JTextField(20);
	
	JRadioButton gnRB = new JRadioButton("", true);
	JRadioButton tnRB = new JRadioButton("", false);
	JRadioButton pRB = new JRadioButton("", false);
	ButtonGroup prefBG = new ButtonGroup();
	
	JPanel presPurPnl = new JPanel();
	JLabel presPurLbl = new JLabel("Purpose");
	JTextField presPur = new JTextField(20);
	
	JPanel presDosPnl = new JPanel();
	JLabel presDosLbl = new JLabel("Dosage");
	JComboBox<String> presDos = new JComboBox<String>(PrescriptionTab.dosageAmts);
	
	JPanel presHourPnl = new JPanel();
	JLabel presHourLbl = new JLabel("Hour");
	JComboBox<String> presHour = new JComboBox<String>(PrescriptionTab.hours);
	
	JPanel presMinPnl = new JPanel();
	JLabel presMinLbl = new JLabel("Mins");
	JComboBox<String> presMin = new JComboBox<String>(PrescriptionTab.minutes);
	
	JPanel presFreqPnl = new JPanel();
	JLabel presFreqLbl = new JLabel("Frequency");
	JComboBox<String> presFreq = new JComboBox<String>(PrescriptionTab.freqCycles);
	
	JPanel btns=new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Modify.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));

	ModPrescription(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		presDos.setSelectedIndex(-1);
		presFreq.setSelectedIndex(-1);
		
		presIdPnl.add(presIdLbl);
		presIdPnl.add(presId);
		presIdPnl.add(enter);
		contIdPnl.add(contIdLbl);
		contIdPnl.add(contId);
		prefBG.add(gnRB);
		prefBG.add(tnRB);
		prefBG.add(pRB);
		presGNPnl.add(presGNLbl);
		presGNPnl.add(presGN);
		presGNPnl.add(gnRB);
		presTNPnl.add(presTNLbl);
		presTNPnl.add(presTN);
		presTNPnl.add(tnRB);
		presPurPnl.add(presPurLbl);
		presPurPnl.add(presPur);
		presPurPnl.add(pRB);
		presDosPnl.add(presDosLbl);
		presDosPnl.add(presDos);
		presHourPnl.add(presHourLbl);
		presHourPnl.add(presHour);
		presMinPnl.add(presMinLbl);
		presMinPnl.add(presMin);
		presFreqPnl.add(presFreqLbl);
		presFreqPnl.add(presFreq);
		
		btns.add(confirm);
		btns.add(cancel);
		btns.add(clear);
		
		confirm.setToolTipText("Modify Prescription");
		cancel.setToolTipText("Cancel");
		clear.setToolTipText("Clear");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(presIdPnl);
		getContentPane().add(contIdPnl);
		getContentPane().add(presGNPnl);
		getContentPane().add(presTNPnl);
		getContentPane().add(presPurPnl);
		getContentPane().add(presDosPnl);
		getContentPane().add(presHourPnl);
		getContentPane().add(presMinPnl);
		getContentPane().add(presFreqPnl);
		getContentPane().add(btns);
		
		presId.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (e.getKeyCode() == KeyEvent.VK_ENTER){
						Class.forName(InitDatabase.JDBC_DRIVER);
						connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
						stmtOld= connection.createStatement();
						if (Validation.isAlphaNumeric(presId.getText().trim())){
							ResultSet oldDetailsRS=stmtOld.executeQuery("select * from " + PrescriptionTab.currentTable + " where prescription_id='" + presId.getText() + "' and not prescription_id='" + InitDatabase.dumPId + "';");
							if (!oldDetailsRS.isBeforeFirst()){
								JOptionPane.showMessageDialog(null,"ID not found. Try again.");
								presId.setText(null);
							}
							while(oldDetailsRS.next()){
								oldContId = oldDetailsRS.getString("contact_id");
								contId.setText(oldContId);
								oldPresGN = oldDetailsRS.getString("generic_name");
								presGN.setText(oldPresGN);
								oldPresTN = oldDetailsRS.getString("trade_name");
								presTN.setText(oldPresTN);
								oldPresPur = oldDetailsRS.getString("purpose");
								presPur.setText(oldPresPur);
								oldPref = oldDetailsRS.getString("preference");
								switch(oldPref){
								case "Generic" : gnRB.setSelected(true);break;
								case "Trade" : tnRB.setSelected(true);break;
								case "Purpose" : pRB.setSelected(true);break;
								}
								oldPresDos = oldDetailsRS.getString("dosage");
								presDos.setSelectedItem(oldPresDos);
								oldPresHour = oldDetailsRS.getString("hours");
								presHour.setSelectedItem(oldPresHour);
								oldPresMin = oldDetailsRS.getString("minutes");
								presMin.setSelectedItem(oldPresMin);
								oldPresFreq = oldDetailsRS.getString("frequency");
								presFreq.setSelectedItem(oldPresFreq);
							}
							oldDetailsRS.close();
						}else{
							JOptionPane.showMessageDialog(null, "Prescription ID can contain only alphabets and numbers.");
						}
						stmtOld.close();
						connection.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				contId.setText("");
				presId.setText("");
				presGN.setText("");
				presTN.setText("");
				presPur.setText("");
				presDos.setSelectedIndex(-1);
				presHour.setSelectedIndex(-1);
				presMin.setSelectedIndex(-1);
				presFreq.setSelectedIndex(-1);
				setVisible(false);
			}
		});

		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				contId.setText("");
				presId.setText("");
				presGN.setText("");
				presTN.setText("");
				presPur.setText("");
				presDos.setSelectedIndex(-1);
				presHour.setSelectedIndex(-1);
				presMin.setSelectedIndex(-1);
				presFreq.setSelectedIndex(-1);
				setVisible(true);
			}
		});

		confirm.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				try{
					String newPref = "Generic";
					if (validateFields()){
						Class.forName(InitDatabase.JDBC_DRIVER);
						connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
						stmtNew=connection.createStatement();
						stmtSent=connection.createStatement();
						if (gnRB.isSelected()){
							newPref = "Generic";
						}else if (tnRB.isSelected()){
							newPref = "Trade";
						}else if (pRB.isSelected()){
							newPref = "Purpose";
						}
						if (contId.getText().equals(oldContId) && presGN.getText().equalsIgnoreCase(oldPresGN) && presTN.getText().equalsIgnoreCase(oldPresTN) && presPur.getText().equalsIgnoreCase(oldPresPur) && oldPref.equalsIgnoreCase(newPref) && presDos.getSelectedItem().equals(oldPresDos) && presHour.getSelectedItem().equals(oldPresHour) && presMin.getSelectedItem().equals(oldPresMin) && presFreq.getSelectedItem().equals(oldPresFreq)){
							JOptionPane.showMessageDialog(null,"No changes were made.");
							setVisible(false);
						}
						else{
							String modPres="update " + PrescriptionTab.currentTable + " set generic_name='" + presGN.getText() + "',trade_name='" + presTN.getText() + "',purpose='" + presPur.getText() + "',preference='" + newPref + "',dosage='" + presDos.getSelectedItem() + "',hours='" + presHour.getSelectedItem() + "',minutes='" + presMin.getSelectedItem() + "',frequency='" + presFreq.getSelectedItem() + "' where prescription_id = '" + presId.getText() + "';";
							if (stmtNew.executeUpdate(modPres) == 1){

								stmtSent.executeUpdate("delete from " + SentTab.currentTable + " where status='Scheduled' and prescription_id='" + presId.getText() + "';");
								
								JOptionPane.showMessageDialog(null,"Prescription modified! Modifications will take effect from tomorrow.");
								setVisible(false);
								MessageHandler.sendInfoMessage(contId.getText(), "One of your prescriptions has been modified in the ART4Me system." + GUIDB.infoMsg);
								contId.setText("");
								presId.setText("");
								presGN.setText("");
								presTN.setText("");
								presPur.setText("");
								presDos.setSelectedIndex(-1);
								presHour.setSelectedIndex(-1);
								presMin.setSelectedIndex(-1);
								presFreq.setSelectedIndex(-1);
								PrescriptionTab.refreshTableA();
							}
							else{
								JOptionPane.showMessageDialog(null,"Prescription not found. Please try again.");
								setVisible(true);
							}
						}
						stmtNew.close();
						connection.close();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	boolean validateFields(){
		boolean retValue = true;
		
		if (presId.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Prescription Id is required!");
		}else if (presId.getText().equals(InitDatabase.dumPId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Prescription ID reserved for system use. Cannot modify!");
		}else if (!Validation.isAlphaNumeric(contId.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID can contain only alphabets and numbers.");
		}else if (presGN.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Generic Name is required!");
		}else if (!Validation.isWord(presGN.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Generic Name can contain only words!");
		}else if (presTN.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Trade Name is required!");
		}else if (!Validation.isWord(presTN.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Trade Name can contain only words!");
		}else if(presPur.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Purpose is required!");
		}else if (!Validation.isWord(presPur.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Purpose can contain only words!");
		}else if (presDos.getSelectedIndex() == -1 || presDos.getSelectedItem().toString().length()>1){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a Dosage Amount!");
		}else if (presHour.getSelectedIndex() == -1 || presHour.getSelectedItem().toString().length()>2){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a time (Hours)!");
		}else if (presMin.getSelectedIndex() == -1 || presMin.getSelectedItem().toString().length()>2){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a time (Minutes)!");
		}else if (presFreq.getSelectedIndex() == -1 || presFreq.getSelectedItem().toString().length()<3){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a Frequency Cycle!");
		}else if (presFreq.getSelectedItem().equals("Hourly") && Integer.parseInt((String) presHour.getSelectedItem())>23){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Cannot schedule reminders for given start time.");
		}else if (presFreq.getSelectedItem().equals("Twice Daily") && Integer.parseInt((String) presHour.getSelectedItem())>12){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Only ONE reminder can be scheduled for given start time.");
		}else if (presFreq.getSelectedItem().equals("Thrice Daily") && Integer.parseInt((String) presHour.getSelectedItem())>8){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "All the reminders cannot be scheduled for given start time.");
		}
		return retValue;
	}
	
}
