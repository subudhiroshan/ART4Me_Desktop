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
 * This class  provides a prompt to retrieve a Message from the system.
 */
public class RetMessage extends JDialog {
	
	private static final long serialVersionUID = 1L;

	Connection connection;
	Statement stmt;
	
	JPanel retIdPnl=new JPanel();
	JLabel retIdLbl = new JLabel("Message ID");
	JTextField retId = new JTextField(3);
	
	JPanel btns=new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Retrieve.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));

	RetMessage(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		retIdPnl.add(retIdLbl);
		retIdPnl.add(retId);
		btns.add(confirm);
		btns.add(cancel);
		btns.add(clear);
		
		confirm.setToolTipText("Retrieve Message");
		cancel.setToolTipText("Cancel");
		clear.setToolTipText("Clear");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(retIdPnl);
		getContentPane().add(btns);
		
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				retId.setText(null);
				setVisible(false);
			}
		});

		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				retId.setText(null);
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
						String delMsg="update " + MessageTab.currentTable + " set status='A' where message_id='" + retId.getText() + "';";
						if (JOptionPane.showConfirmDialog(null, "Are you sure you want to retrieve this Message?") == 0){
							if (stmt.executeUpdate(delMsg) == 1){
								JOptionPane.showMessageDialog(null,"Message retrieved!");
							}
							else{
								JOptionPane.showMessageDialog(null,"Message not found. Please try again.");
							}
							stmt.close();
							connection.close();
							setVisible(false);
							MessageTab.refreshTableI();
							MessageTab.refreshTableA();
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
		if (retId.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please enter a Message ID!");
		}else if (!Validation.isAlphaNumeric(retId.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message ID can contain only alphabets and numbers.");
		}else if (retId.getText().equals(InitDatabase.dumMId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message ID reserved for system use. Cannot retrieve!");
		}
		return retValue;
	}
}
