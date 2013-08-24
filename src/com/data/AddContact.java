package com.data;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class provides a prompt to add a new Contact into the system.
 */
public class AddContact extends JDialog{

	private static final long serialVersionUID = 1L;

	Connection connection;
	Statement stmt, stmtRSCID, stmtRSPP;
	JPanel contIdPnl = new JPanel();
	JLabel contIdLbl = new JLabel("Contact ID");
	JTextField contId = new JTextField(5);
	
	JPanel contFNPnl = new JPanel();
	JLabel contFNLbl = new JLabel("First Name");
	JTextField contFN = new JTextField(20);
	
	JPanel contLNPnl = new JPanel();
	JLabel contLNLbl = new JLabel("Last Name");
	JTextField contLN = new JTextField(20);
	
	JPanel contGPnl = new JPanel();
	JLabel contGLbl = new JLabel("Gender");
	JComboBox<String> contG = new JComboBox<String>(ContactTab.genderTypes);
	
	JPanel contPPPnl = new JPanel();
	JLabel contPPLbl = new JLabel("Phone");
	JTextField contPP = new JTextField(15);
	JLabel space = new JLabel("  ");
	JLabel typeLbl = new JLabel("Android?");
	JCheckBox type = new JCheckBox();
	
	JPanel contHAPnl = new JPanel();
	JLabel contHALbl = new JLabel("Home Address");
	JTextArea contHA = new JTextArea("Enter Home Address...", 2, 15);
		
	JPanel contCityPnl = new JPanel();
	JLabel contCityLbl = new JLabel("Home City");
	JTextField contCity = new JTextField(20);
	
	JPanel contStatePnl = new JPanel();
	JLabel contStateLbl = new JLabel("Home State");
	JComboBox<String> contState = new JComboBox<String>(ContactTab.usStates);
	
	JPanel contZipPnl = new JPanel();
	JLabel contZipLbl = new JLabel("Home Zip");
	JTextField contZip = new JTextField(7);
		
	JPanel btns = new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Add.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));
	
	AddContact(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		contG.setSelectedIndex(-1);
		contState.setSelectedIndex(-1);
		
		contHA.setWrapStyleWord(true);
		contHA.setLineWrap(true);
		
		contIdPnl.add(contIdLbl);
		contIdPnl.add(contId);
		contFNPnl.add(contFNLbl);
		contFNPnl.add(contFN);
		contLNPnl.add(contLNLbl);
		contLNPnl.add(contLN);
		contGPnl.add(contGLbl);
		contGPnl.add(contG);
		contPPPnl.add(contPPLbl);
		contPPPnl.add(contPP);
		contPPPnl.add(space);
		contPPPnl.add(typeLbl);
		contPPPnl.add(type);
		contHAPnl.add(contHALbl);
		contHAPnl.add(contHA);
		contCityPnl.add(contCityLbl);
		contCityPnl.add(contCity);
		contStatePnl.add(contStateLbl);
		contStatePnl.add(contState);
		contZipPnl.add(contZipLbl);
		contZipPnl.add(contZip);
		
		btns.add(confirm);
		btns.add(cancel);
		btns.add(clear);
		
		confirm.setToolTipText("Add Contact");
		cancel.setToolTipText("Cancel");
		clear.setToolTipText("Clear");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(contIdPnl);
		getContentPane().add(contFNPnl);
		getContentPane().add(contLNPnl);
		getContentPane().add(contGPnl);
		getContentPane().add(contPPPnl);
		getContentPane().add(contHAPnl);
		getContentPane().add(contCityPnl);
		getContentPane().add(contStatePnl);
		getContentPane().add(contZipPnl);
		getContentPane().add(btns);
		
		contHA.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			contHA.setText("");	
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
		});
		
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				contId.setText("");
				contFN.setText("");
				contLN.setText("");
				contG.setSelectedIndex(-1);
				contPP.setText("");
				type.setSelected(false);
				contHA.setText("");
				contCity.setText("");
				contState.setSelectedIndex(-1);
				contZip.setText("");
				setVisible(false);
			}
		});

		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				contId.setText("");
				contFN.setText("");
				contLN.setText("");
				contG.setSelectedIndex(-1);
				contPP.setText("");
				type.setSelected(false);
				contHA.setText("");
				contCity.setText("");
				contState.setSelectedIndex(-1);
				contZip.setText("");
				setVisible(true);
			}
		});

		confirm.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				try{
					if (validateFields()){
						Class.forName(InitDatabase.JDBC_DRIVER);
						connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
						stmt=connection.createStatement();
						stmtRSCID=connection.createStatement();
						stmtRSPP=connection.createStatement();
						ResultSet oldDetailsRSCID = stmtRSCID.executeQuery("select contact_id from " + ContactTab.currentTable + " where contact_id='" + contId.getText() + "' and not contact_id='" + InitDatabase.dumCId + "';");
						ResultSet oldDetailsRSPP = stmtRSPP.executeQuery("select primary_phone from " + ContactTab.currentTable + " where primary_phone='" + contPP.getText() + "' and not contact_id='" + InitDatabase.dumCId + "';");
						if (oldDetailsRSCID.isBeforeFirst()){
							JOptionPane.showMessageDialog(GUIDB.baseFrame,"Contact ID already used. Please check inactive logs too. Try again.");
						}else if (oldDetailsRSPP.isBeforeFirst()){
							JOptionPane.showMessageDialog(GUIDB.baseFrame,"Phone Number already used. Please check inactive logs too. Try again.");
						}else{
							String smart = type.isSelected() ? "Y" : "N";
							String addCont="insert into " + ContactTab.currentTable + " values('"+contId.getText() + "','" + contFN.getText() + "','"+ contLN.getText() + "','" + contG.getSelectedItem().toString().substring(0, 1) + "','" + contPP.getText() + "','" + smart + "','" + contHA.getText().replaceAll("\'", "") + "','" + contCity.getText() + "','" + contState.getSelectedItem() + "','" + contZip.getText() + "','A');";
							if (stmt.executeUpdate(addCont) == 1){
								MessageHandler.sendInfoMessage(contId.getText(), "You have been added as a Contact in the ART4Me system." + GUIDB.infoMsg);
								JOptionPane.showMessageDialog(GUIDB.baseFrame,"Contact added!");
								setVisible(false);
								contId.setText("");
								contFN.setText("");
								contLN.setText("");
								contG.setSelectedIndex(-1);
								contPP.setText("");
								type.setSelected(false);
								contHA.setText("");
								contCity.setText("");
								contState.setSelectedIndex(-1);
								contZip.setText("");
								GUIDB.loadContacts();
								ContactTab.refreshTableA();
							}
							else{
								JOptionPane.showMessageDialog(GUIDB.baseFrame,"Something went wrong!");
								setVisible(true);
							}
							stmtRSPP.close();
							stmtRSCID.close();
							stmt.close();
							connection.close();
						}
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
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID is required!");
		}else if (contId.getText().equals(InitDatabase.dumCId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID is invalid!");
		}else if (!Validation.isAlphaNumeric(contId.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID can contain only alphabets and numbers.");
		}else if (contFN.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "First Name is required!");
		}else if (!Validation.isName(contFN.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "First Name can contain only alphabets!");
		}else if (contLN.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Last Name is required!");
		}else if (!Validation.isName(contLN.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Last Name can contain only alphabets!");
		}else if(contPP.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Primary Phone is required!");
		}else if(!Validation.isNumber(contPP.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Primary Phone can contain only numbers!");
		}else if (contCity.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "City is required!");
		}else if (!Validation.isName(contCity.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "City can contain only alphabets!");
		}else if (contState.getSelectedIndex() == -1 || contState.getSelectedItem().toString().length()<3){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a State!");
		}else if (contZip.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Zip Code is required!");
		}else if (!Validation.isNumber(contZip.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Zip Code can contain numbers only!");
		}else if (contZip.getText().trim().length() > 5){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Zip Code has too many numbers!");
		}
		return retValue;
	}
}