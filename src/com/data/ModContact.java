package com.data;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.DriverManager;
import java.sql.Connection;
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
 * This class  provides a prompt to modify a Contact from the system.
 */
public class ModContact extends JDialog {

	private static final long serialVersionUID = 1L;
	static String oldContId;
	static String oldContFN;
	static String oldContLN;
	static String oldContG;
	static String oldContPP;
	static String oldContType;
	static boolean smart;
	static String oldContHA;
	static String oldContCity;
	static String oldContState;
	static String oldContZip;

	Connection connection;
	Statement stmtOld, stmtNew;

	JPanel contIdPnl = new JPanel();
	JLabel contIdLbl = new JLabel("Contact ID");
	JTextField contId = new JTextField(5);
	JLabel enter = new JLabel(new ImageIcon(ClassLoader.getSystemResource("Enter.png")));

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
	JTextArea contHA = new JTextArea("", 2, 15);

	JPanel contCityPnl = new JPanel();
	JLabel contCityLbl = new JLabel("Home City");
	JTextField contCity = new JTextField(20);

	JPanel contStatePnl = new JPanel();
	JLabel contStateLbl = new JLabel("Home State");
	JComboBox<String> contState = new JComboBox<String>(ContactTab.usStates);

	JPanel contZipPnl = new JPanel();
	JLabel contZipLbl = new JLabel("Home Zip");
	JTextField contZip = new JTextField(7);

	JPanel btns=new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Modify.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));

	ModContact(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		contG.setSelectedIndex(-1);
		contState.setSelectedIndex(-1);

		contHA.setWrapStyleWord(true);
		contHA.setLineWrap(true);

		contIdPnl.add(contIdLbl);
		contIdPnl.add(contId);
		contIdPnl.add(enter);
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

		confirm.setToolTipText("Modify Contact");
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

		contId.addKeyListener(new KeyListener() {

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
						if (Validation.isAlphaNumeric(contId.getText().trim())){
							ResultSet oldDetailsRS=stmtOld.executeQuery("select * from " + ContactTab.currentTable + " where contact_id='" + contId.getText() + "' and not contact_id='" + InitDatabase.dumCId + "';");
							if (!oldDetailsRS.isBeforeFirst()){
								JOptionPane.showMessageDialog(GUIDB.baseFrame,"ID not found. Try again.");
								contId.setText("");
							}
							while(oldDetailsRS.next()){
								oldContId = oldDetailsRS.getString("contact_id");
								contId.setText(oldContId);
								oldContFN = oldDetailsRS.getString("first_name");
								contFN.setText(oldContFN);
								oldContLN = oldDetailsRS.getString("last_name");
								contLN.setText(oldContLN);
								oldContG = oldDetailsRS.getString("gender");
								contG.setSelectedItem(oldContG);
								oldContPP = oldDetailsRS.getString("primary_phone");
								contPP.setText(oldContPP);
								oldContType = oldDetailsRS.getString("smart");
								smart = oldContType.equalsIgnoreCase("Y") ? true : false;
								type.setSelected(smart);
								oldContHA = oldDetailsRS.getString("home_address");
								contHA.setText(oldContHA);
								oldContCity = oldDetailsRS.getString("home_city");
								contCity.setText(oldContCity);
								oldContState = oldDetailsRS.getString("home_state");
								contState.setSelectedItem(oldContState);
								oldContZip = oldDetailsRS.getString("home_zip");
								contZip.setText(oldContZip);
							}
							oldDetailsRS.close();
						}else{
							JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID can contain only alphabets and numbers.");
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
						stmtNew=connection.createStatement();
						if (contFN.getText().equalsIgnoreCase(oldContFN) && contLN.getText().equalsIgnoreCase(oldContLN) && contG.getSelectedItem().equals(oldContG) && contPP.getText().equals(oldContPP) && type.isSelected() == smart && contHA.getText().equalsIgnoreCase(oldContHA) && contCity.getText().equalsIgnoreCase(oldContCity) && contState.getSelectedItem().equals(oldContState) && contZip.getText().equals(oldContZip)){
							JOptionPane.showMessageDialog(GUIDB.baseFrame,"No changes were made.");
							setVisible(false);
						}
						else if (!contPP.getText().equalsIgnoreCase(oldContPP)){
							Statement stmtRSPP=connection.createStatement();
							ResultSet newDetailsRSPP = stmtRSPP.executeQuery("select primary_phone from " + ContactTab.currentTable + " where primary_phone='" + contPP.getText() + "' and not contact_id='" + InitDatabase.dumCId + "';");
							if (newDetailsRSPP.isBeforeFirst()){
								JOptionPane.showMessageDialog(GUIDB.baseFrame,"Phone Number already used. Please check inactive logs too. Try again.");
							}else{
								String smartNew = type.isSelected() ? "Y" : "N";
								String modCont="update " + ContactTab.currentTable + " set first_name='" + contFN.getText() + "',last_name='" + contLN.getText() + "',gender='" + contG.getSelectedItem().toString().substring(0, 1) + "',primary_phone='" + contPP.getText() + "',smart='" + smartNew + "',home_address='" + contHA.getText().replaceAll("\'", "") + "',home_city='" + contCity.getText() + "',home_state='" + contState.getSelectedItem() + "',home_zip='" + contZip.getText() + "' where contact_id='" + contId.getText() +"';";
								if (stmtNew.executeUpdate(modCont) == 1){
									JOptionPane.showMessageDialog(GUIDB.baseFrame,"Contact modified!");
									MessageHandler.sendInfoMessage(contId.getText(), "Your Contact details have been modified in the ART4Me system." + GUIDB.infoMsg);
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
									JOptionPane.showMessageDialog(GUIDB.baseFrame,"Contact not found. Please try again.");
									setVisible(true);
								}
							}
							stmtRSPP.close();
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

		if (contId.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID is required!");
		}else if (contId.getText().equals(InitDatabase.dumCId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID reserved for system use. Cannot modify!");
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