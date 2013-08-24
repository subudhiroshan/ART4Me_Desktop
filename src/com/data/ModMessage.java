package com.data;


import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class  provides a prompt to modify a Message from the system. 
 */
public class ModMessage extends JDialog {

	private static final long serialVersionUID = 1L;
	static String oldMsgCat;
	static String oldMsgContents;
	
	Connection connection;
	Statement stmt;
	
	JPanel msgIdPnl = new JPanel();
	JLabel msgIdLbl = new JLabel("Message ID");
	JTextField msgId = new JTextField(3);
	JLabel enter = new JLabel(new ImageIcon(ClassLoader.getSystemResource("Enter.png")));
	
	JPanel msgCatPnl=new JPanel();
	JLabel msgCatLbl = new JLabel("Message Category");
	JComboBox<String> msgCat = new JComboBox<String>(GUIDB.msgCategories);
		
	JPanel msgContentsPnl=new JPanel();
	final JTextField count = new JTextField(3);
	JLabel msgContentsLbl = new JLabel("Message Contents");
	JTextArea msgContents = new JTextArea("", 4, 20);
	
	JPanel btns=new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Modify.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));

	ModMessage(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		msgCat.setSelectedIndex(-1);
		
		msgContents.setWrapStyleWord(true);
		msgContents.setLineWrap(true);
		
		msgIdPnl.add(msgIdLbl);
		msgIdPnl.add(msgId);
		msgIdPnl.add(enter);
		msgCatPnl.add(msgCatLbl);
		msgCatPnl.add(msgCat);
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
		msgPanel.add(msgContentsLbl, BorderLayout.WEST);
		msgPanel.add(count, BorderLayout.EAST);
		count.setText(msgContents.getText().trim().length() + "");
		msgContentsPnl.setLayout(new BorderLayout());
		msgContentsPnl.add(msgPanel, BorderLayout.NORTH);
		msgContentsPnl.add(msgContents, BorderLayout.CENTER);
		msgContents.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				count.setText(msgContents.getText().trim().length() + "");
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (msgContents.getText().trim().length() > 149){
					try {
						msgContents.setText(msgContents.getText(0, 149));
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		msgContentsPnl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		btns.add(confirm);
		btns.add(cancel);
		btns.add(clear);
		
		confirm.setToolTipText("Modify Message");
		cancel.setToolTipText("Cancel");
		clear.setToolTipText("Clear");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(msgIdPnl);
		getContentPane().add(msgCatPnl);
		getContentPane().add(msgContentsPnl);
		getContentPane().add(btns);
		
		msgId.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (e.getKeyCode() == KeyEvent.VK_ENTER){
						Class.forName(InitDatabase.JDBC_DRIVER);
						connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
						stmt= connection.createStatement();
						if (Validation.isAlphaNumeric(msgId.getText().trim())){
						ResultSet oldDetailsRS = stmt.executeQuery("select * from message_db where message_id='" + msgId.getText() + "' and not message_id='" + InitDatabase.dumMId + "';");
						if (!oldDetailsRS.isBeforeFirst()){
							JOptionPane.showMessageDialog(null,"ID not found. Try again.");
							msgId.setText(null);
						}
						while(oldDetailsRS.next()){
							oldMsgCat = oldDetailsRS.getString("message_category");
							msgCat.setSelectedItem(oldMsgCat);
							oldMsgContents = oldDetailsRS.getString("message_contents");
							msgContents.setText(oldMsgContents);
						}
						oldDetailsRS.close();
						}else{
							JOptionPane.showMessageDialog(null, "Message ID can contain only alphabets and numbers.");
						}
						stmt.close();
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
				msgId.setText(null);
				msgCat.setSelectedIndex(-1);
				msgContents.setText(null);
				setVisible(false);
			}
		});

		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				msgId.setText(null);
				msgCat.setSelectedIndex(-1);
				msgContents.setText("");
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
						if (msgCat.getSelectedItem().equals(oldMsgCat) && msgContents.getText().equals(oldMsgContents)){
							JOptionPane.showMessageDialog(null,"No changes were made.");
							setVisible(false);
						}
						else{
							String modMsg="update " + MessageTab.currentTable + " set message_category='" + msgCat.getSelectedItem() + "',message_contents='" + msgContents.getText().toString().replaceAll("\'", "") + "' where message_id='" + msgId.getText() +"';";
							if (stmt.executeUpdate(modMsg) == 1){
								JOptionPane.showMessageDialog(null,"Message modified!");
								setVisible(false);
								msgId.setText(null);
								msgCat.setSelectedIndex(-1);
								msgContents.setText(null);
								MessageTab.refreshTableA();
							}
							else{
								JOptionPane.showMessageDialog(null,"Message not found. Please try again.");
								setVisible(true);
							}
						}
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
		if (msgId.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please enter a Message ID!");
		}else if (msgCat.getSelectedIndex() == -1 || msgCat.getSelectedItem().toString().length()<3){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a Message Category!");
		}else if (msgContents.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message Contents are required!");
		}else if (msgId.getText().equals(InitDatabase.dumMId)){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message ID reserved for system use. Cannot modify!");
		}
		return retValue;
	}
}
