package com.data;


import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
 * This class provides a prompt to add a new Message into the system.
 */
public class AddMessage extends JDialog {

	private static final long serialVersionUID = 1L;

	Connection connection;
	Statement stmt, stmtId;
	int messageId;
	JPanel msgCatPnl=new JPanel();
	JLabel msgCatLbl = new JLabel("Message Category");
	JComboBox<String> msgCat = new JComboBox<String>(GUIDB.msgCategories);
		
	JPanel msgContentsPnl=new JPanel();
	final JTextField count = new JTextField(3);
	JLabel msgContentsLbl = new JLabel("Message Contents");
	final JTextArea msgContents = new JTextArea("Enter message contents...", 4, 20);
	
	JPanel btns=new JPanel(new GridLayout(1,3,10,10));
	JButton confirm = new JButton(new ImageIcon(ClassLoader.getSystemResource("Add.png")));
	JButton cancel = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	JButton clear = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));
	
	AddMessage(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		msgCat.setSelectedIndex(-1);
		
		msgContents.setWrapStyleWord(true);
		msgContents.setLineWrap(true);
		
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
		
		confirm.setToolTipText("Add Message");
		cancel.setToolTipText("Cancel");
		clear.setToolTipText("Clear");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		getContentPane().add(msgCatPnl);
		getContentPane().add(msgContentsPnl);
		getContentPane().add(btns);
		
		msgContents.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			msgContents.setText(null);	
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
				msgCat.setSelectedIndex(-1);
				msgContents.setText(null);
				setVisible(false);
			}
		});

		clear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae) {
				msgCat.setSelectedIndex(-1);
				msgContents.setText("Enter message contents...");
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
						stmtId=connection.createStatement();
						ResultSet msgIdRS = stmtId.executeQuery("select max(message_id) from message_db;");
						while (msgIdRS.next()){
							messageId = Integer.parseInt(msgIdRS.getString("max(message_id)").substring(1));
						}
						msgIdRS.close();
						stmtId.close();
						String addMsg="insert into " + MessageTab.currentTable + " values('M" + ++messageId + "','" + msgCat.getSelectedItem() + "','"+ msgContents.getText().toString().replaceAll("\'", "") + "','A');";
						if (stmt.executeUpdate(addMsg) == 1){
							JOptionPane.showMessageDialog(null,"Message added!");
							setVisible(false);
							msgCat.setSelectedIndex(-1);
							msgContents.setText(null);
							MessageTab.refreshTableA();
							}
						else{
							JOptionPane.showMessageDialog(null,"Something went wrong!");
							setVisible(true);
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
		if (msgCat.getSelectedIndex() == -1 || msgCat.getSelectedItem().toString().length()<3){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a Message Category!");
		}else if (msgContents.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message Contents are required!");
		}
		return retValue;
	}
}
