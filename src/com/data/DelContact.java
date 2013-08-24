package com.data;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class provides a prompt to delete a Contact from the system.
 */
public class DelContact extends JDialog {

	private static final long serialVersionUID = 1L;
	Connection connection;
	Statement stmt;
	
	JPanel delIdPnl=new JPanel();
	JLabel delIdLbl = new JLabel("Contact ID");
	JTextField delId = new JTextField(5);
	
	JPanel btns=new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Delete.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));

	DelContact(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		delIdPnl.add(delIdLbl);
		delIdPnl.add(delId);
		btns.add(confirm);
		btns.add(cancel);
		btns.add(clear);
		
		confirm.setToolTipText("Delete Contact");
		cancel.setToolTipText("Cancel");
		clear.setToolTipText("Clear");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(delIdPnl);
		getContentPane().add(btns);
		
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				delId.setText(null);
				setVisible(false);
			}
		});

		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				delId.setText(null);
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
						String delMsg="update " + ContactTab.currentTable + " set status='I' where contact_id='" + delId.getText() + "';";
						if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this Contact?") == 0){
							MessageHandler.sendInfoMessage(delId.getText(), "You have been deleted as a Contact in the ART4Me system." + GUIDB.infoMsg);	
							if (stmt.executeUpdate(delMsg) == 1){
								JOptionPane.showMessageDialog(null,"Contact deleted!");
							}
							else{
								JOptionPane.showMessageDialog(null,"Contact not found. Please try again.");
							}
							stmt.close();
							connection.close();
							setVisible(false);
							GUIDB.loadContacts();
							ContactTab.refreshTableA();
							ContactTab.refreshTableI();
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
		if (delId.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please enter a Contact ID!");
		}else if (!Validation.isAlphaNumeric(delId.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID can contain only alphabets and numbers.");
		}else if (delId.getText().equals(InitDatabase.dumCId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Contact ID reserved for system use. Cannot delete!");
		}
		return retValue;
	}	
}
