package com.data;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * This class provides a prompt to add a new Prescription into the system.
 */
public class AddPrescription extends JDialog {

	private static final long serialVersionUID = 1L;

	Connection connection;
	Statement stmt, stmtId, stmtCId;
	int prescriptionId;
	
	JPanel contIdPnl = new JPanel();
	JLabel contIdLbl = new JLabel("Contact Id");
	JTextField contId = new JTextField(5);
	
	JPanel presGNPnl = new JPanel();
	JLabel presGNLbl = new JLabel("Generic Name");
	JTextField presGN = new JTextField(20);
	
	JPanel presTNPnl = new JPanel();
	JLabel presTNLbl = new JLabel("Trade Name");
	JTextField presTN = new JTextField(20);
	
	JPanel presPurPnl = new JPanel();
	JLabel presPurLbl = new JLabel("Purpose");
	JTextField presPur = new JTextField(20);
	
	JRadioButton gnRB = new JRadioButton("", true);
	JRadioButton tnRB = new JRadioButton("", false);
	JRadioButton pRB = new JRadioButton("", false);
	ButtonGroup prefBG = new ButtonGroup();
	
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
		
	JPanel btns = new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Add.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));
	
	AddPrescription(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		presDos.setSelectedIndex(-1);
		presFreq.setSelectedIndex(-1);
		
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
		
		confirm.setToolTipText("Add Prescription");
		cancel.setToolTipText("Cancel");
		clear.setToolTipText("Clear");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(contIdPnl);
		getContentPane().add(presGNPnl);
		getContentPane().add(presTNPnl);
		getContentPane().add(presPurPnl);
		getContentPane().add(presDosPnl);
		getContentPane().add(presHourPnl);
		getContentPane().add(presMinPnl);
		getContentPane().add(presFreqPnl);
		getContentPane().add(btns);
		
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				contId.setText("");
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
					String preference = "Generic";
					if (validateFields()){
						Class.forName(InitDatabase.JDBC_DRIVER);
						connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
						stmt=connection.createStatement();
						stmtId=connection.createStatement();
						stmtCId=connection.createStatement();
						ResultSet contIdRS = stmtCId.executeQuery("select contact_id from contacts_db where contact_id='" + contId.getText() + "';");
						if (!contIdRS.isBeforeFirst()){
							JOptionPane.showMessageDialog(GUIDB.baseFrame,"Please check the Contact ID.");
						}else{
							ResultSet presIdRS = stmtId.executeQuery("select max(prescription_id) from prescription_db;");
							while (presIdRS.next()){
								prescriptionId = Integer.parseInt(presIdRS.getString("max(prescription_id)").substring(1));
							}
							presIdRS.close();
							stmtId.close();
							if (gnRB.isSelected()){
								preference = "Generic";
							}else if (tnRB.isSelected()){
								preference = "Trade";
							}else if (pRB.isSelected()){
								preference = "Purpose";
							}
							String addPres="insert into " + PrescriptionTab.currentTable + " values('P" + ++prescriptionId + "','" + contId.getText()  + "','" + presGN.getText() + "','"+ presTN.getText() + "','" + presPur.getText() + "','" + preference + "','" + presDos.getSelectedItem() + "','" + presHour.getSelectedItem() + "','" + presMin.getSelectedItem() + "','" + presFreq.getSelectedItem() + "','A');";
							if (stmt.executeUpdate(addPres) == 1){
								JOptionPane.showMessageDialog(GUIDB.baseFrame,"Prescription added! Schedule will start from tomorrow.");
								setVisible(false);
								MessageHandler.sendInfoMessage(contId.getText(), "A prescription has been added for you in the ART4Me system." + GUIDB.infoMsg);
								contId.setText("");
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
								JOptionPane.showMessageDialog(GUIDB.baseFrame,"Something went wrong!");
								setVisible(true);
							}
						}
						contIdRS.close();
						stmtCId.close();
						stmt.close();
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
		
		if (contId.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact Id is required!");
		}else if (contId.getText().equals(InitDatabase.dumCId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact Id is for system use. Please check!");
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
