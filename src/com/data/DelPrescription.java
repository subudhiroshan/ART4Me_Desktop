package com.data;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
 * This class provides a prompt to delete a Prescription from the system.
 */
public class DelPrescription extends JDialog {

	private static final long serialVersionUID = 1L;

	Connection connection;
	Statement stmtPres, stmtCont;
	
	JPanel delIdPnl=new JPanel();
	JLabel delIdLbl = new JLabel("Prescription ID");
	JTextField delId = new JTextField(5);
	
	JPanel btns=new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Delete.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));

	DelPrescription(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		delIdPnl.add(delIdLbl);
		delIdPnl.add(delId);
		btns.add(confirm);
		btns.add(cancel);
		btns.add(clear);
		
		confirm.setToolTipText("Delete Prescription");
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
					String contId = null;
					if (validateFields()){
						Class.forName(InitDatabase.JDBC_DRIVER);
						connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
						stmtPres=connection.createStatement();
						stmtCont=connection.createStatement();
						String delMsg="update " + PrescriptionTab.currentTable + " set status='I' where prescription_id='" + delId.getText() + "';";
						if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this Prescription?") == JOptionPane.YES_OPTION){
							ResultSet contIdRS = stmtCont.executeQuery("select contact_id from " + PrescriptionTab.currentTable + " where prescription_id='" + delId.getText() + "';");
							while (contIdRS.next()){
								contId = contIdRS.getString("contact_id");
							}
							if (stmtPres.executeUpdate(delMsg) == 1){
								MessageHandler.sendInfoMessage(contId, "One of your prescriptions has been deleted in the ART4Me system." + GUIDB.infoMsg);
								JOptionPane.showMessageDialog(null,"Prescription deleted! Effective immediately.");
							}
							else{
								JOptionPane.showMessageDialog(null,"Prescription not found. Please try again.");
							}
							stmtCont.close();
							stmtPres.close();
							connection.close();
							setVisible(false);
							PrescriptionTab.refreshTableA();
							PrescriptionTab.refreshTableI();
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
			JOptionPane.showMessageDialog(null, "Please enter a Prescription ID!");
		}else if (!Validation.isAlphaNumeric(delId.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(null, "Prescription ID can contain only alphabets and numbers.");
		}else if (delId.getText().equals(InitDatabase.dumPId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Prescription ID reserved for system use. Cannot delete!");
		}
		return retValue;
	}	
}
