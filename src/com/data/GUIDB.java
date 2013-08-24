package com.data;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;

import com.sms.SMSClient;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class initializes the GUI, and running environment.
 */
public class GUIDB{
	
	public static boolean trial = false; // true-> production, false->development or testing
	
	public static JLabel modemStatus = new JLabel();
	static JLabel unreadMessageCount = new JLabel();
	static long lastViewed = System.currentTimeMillis();
	static boolean notifyFlag = true;
	static int notDur = 5;
	static boolean followUp = true;
	static int followUpDur = 5;
	static int combineDur = 1;
	static String lead = "Please take";
	static String separator = "and";
	static String trail = "now. Have a good day!";
	public static String csca = "+12063130004";
	public static String defaultPort = "COM";
	
	static Vector<Vector<String>> data = new Vector<Vector<String>>();
	static Vector<String> headers = new Vector<String>();
	static Vector<String> types = new Vector<String>();
	static TableModel model;
	static int col;
	static JTable table;
	static String visibleTable;
	public static JFrame baseFrame;
	static String[] msgCategories = {"  ", "Educational", "Motivational", "Patient-Generated"};
	static String infoMsg = "If this is an error, please contact us immediately on XXX-YYY-ZZZ.";
	static Connection connection;
	static Statement stmt;
	static ResultSet recipientRS;
	final static JComboBox<String> recipient = new JComboBox<String>();
	static String cellNumber;
	static SMSClient SMSC = new SMSClient(0);
	public static MyKVPair messages = new MyKVPair();
	
	public static JButton sendButton, fwdButton;
	
	public static void loadContacts(){
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		try {
			Class.forName(InitDatabase.JDBC_DRIVER);
			connection = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmt = connection.createStatement();
			recipientRS = stmt.executeQuery("select contact_id from " + ContactTab.currentTable + " where not contact_id = '" + InitDatabase.dumCId + "' and status='A';");
			recipient.removeAllItems();
			model.addElement("");
			while (recipientRS.next()){
				model.addElement(recipientRS.getString("contact_id"));
			}
			recipient.setModel(model);
			stmt.close();
			connection.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void createGUI() throws ClassNotFoundException
	{		
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Messages", new MessageTab().messagePanel());
		tabbedPane.addTab("Contacts", new ContactTab().conPanel());
		tabbedPane.addTab("Prescriptions", new PrescriptionTab().prePanel());
		tabbedPane.addTab("Sent Logs", new SentTab().sentPanel());
		tabbedPane.addTab("Received Logs", new ReceivedTab().recdPanel());		
		
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (tabbedPane.getSelectedIndex() == 4){ // Viewing Received tab
					lastViewed = System.currentTimeMillis();
					MessageHandler.count = 1;
					unreadMessageCount.setIcon(new ImageIcon(ClassLoader.getSystemResource("")));
					unreadMessageCount.setToolTipText("0");
				}
			}
		});
		
		JPanel msgSend = new JPanel();
		JPanel msgRecv = new JPanel();
		
		/*Message Sender panel starts*/
		JPanel messagePanel=new JPanel();
		JPanel buttonPanel=new JPanel();
		
		final JComboBox<String> textCat = new JComboBox<String>(GUIDB.msgCategories);
		final JComboBox<String> textMsgBody = new JComboBox<>();
		
		loadContacts();
		
		textCat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					Class.forName(InitDatabase.JDBC_DRIVER);
					connection = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
					stmt = connection.createStatement();
					ResultSet msgCatRS = stmt.executeQuery("select message_contents from " + MessageTab.currentTable + " where message_category='" + textCat.getSelectedItem() + "' and status='A';");
					textMsgBody.removeAllItems();
					while (msgCatRS.next()){
						StringBuilder tempMsg = new StringBuilder(msgCatRS.getString("message_contents"));
						String viewableMsgText;
						if (tempMsg.length() > 25){
							viewableMsgText = tempMsg.substring(0, 22).concat("...");
						}else{
							viewableMsgText = tempMsg.toString();
						}
						messages.put(viewableMsgText, tempMsg.toString());
						textMsgBody.addItem(viewableMsgText);
					}
					stmt.close();
					connection.close();
				}catch (NullPointerException npe){
					JOptionPane.showMessageDialog(null, "Please contact the programmer, as the message strings are too similiar.");
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		sendButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("Send.png")));
		fwdButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("Forward.png")));
		JButton clsButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("Clear.png")));
		
		messagePanel.add(recipient, BorderLayout.EAST);
		messagePanel.add(textCat, BorderLayout.CENTER);
		messagePanel.add(textMsgBody, BorderLayout.WEST);

		buttonPanel.add(sendButton,BorderLayout.EAST);
		buttonPanel.add(fwdButton,BorderLayout.CENTER);
		buttonPanel.add(clsButton,BorderLayout.WEST);
		
		msgSend.add(messagePanel, BorderLayout.CENTER);
		msgSend.add(buttonPanel, BorderLayout.WEST);
		clsButton.setToolTipText("Clear");
		clsButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				recipient.setSelectedIndex(-1);
				textCat.setSelectedIndex(-1);
				textMsgBody.setSelectedIndex(-1);
			}
		});
		sendButton.setToolTipText("Send Message");
		sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae) {
				
				if (recipient.getSelectedItem().toString().equals("")){
					JOptionPane.showMessageDialog(recipient, "Please select a recipient.");	
				}else if (textCat.getSelectedIndex() == -1){
					JOptionPane.showMessageDialog(textCat, "Please select a Message Category");	
				}else if (textMsgBody.getSelectedIndex() == -1){
					JOptionPane.showMessageDialog(textMsgBody, "Please select a Message Body");	
				}else {
					MessageHandler.sendManualMessage(recipient.getSelectedItem().toString(), messages.findByKey(textMsgBody.getSelectedItem().toString()), InitDatabase.dumPId);
				}
			}
		});
		fwdButton.setToolTipText("Forward Message");
		fwdButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae) {
				if (textCat.getSelectedIndex() == -1){
					JOptionPane.showMessageDialog(textCat, "Please select a Message Category");	
				}else if (textMsgBody.getSelectedIndex() == -1){
					JOptionPane.showMessageDialog(textMsgBody, "Please select a Message Body");	
				}else {
					recipient.setSelectedItem("");
					if(JOptionPane.showConfirmDialog(baseFrame, "Are you sure you want to send this message to ALL subjects now?") == JOptionPane.YES_OPTION){
						//Starting a new Thread to forward messages
						Thread forwardMessagesThread = new Thread() {
							public void run() {
								MessageHandler.forwardMessage(messages.findByKey(textMsgBody.getSelectedItem().toString()));
							}
						};
						forwardMessagesThread.start();	
					}
				}
			}
		});
		/*Message Sender panel ends*/
		
		/*Message Receiver panel starts*/
		final JLabel notStatus = new JLabel(new ImageIcon(ClassLoader.getSystemResource("Shown.png")));
		
		JButton toggleButton=new JButton("Notifications");
		
		JPanel msgRecvPanel = new JPanel();
		msgRecvPanel.add(toggleButton, BorderLayout.WEST);
		
		msgRecv.add(msgRecvPanel, BorderLayout.CENTER);
		msgRecv.add(notStatus, BorderLayout.SOUTH);
		
		toggleButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				notifyFlag = !notifyFlag;
				if (notifyFlag){
					notStatus.setIcon(new ImageIcon(ClassLoader.getSystemResource("Shown.png")));
					notStatus.setToolTipText("Notifications shown");
				}else {
					notStatus.setIcon(new ImageIcon(ClassLoader.getSystemResource("Hidden.png")));
					notStatus.setToolTipText("Notifications hidden");
				}
			}
		});
		
		/*Message Receiver panel ends*/
		
		modemStatus.setIcon(new ImageIcon(ClassLoader.getSystemResource("NotReady.png")));
		modemStatus.setToolTipText("Modem uninitialised");
		unreadMessageCount.setToolTipText("0");
		
		JPanel tempPanel = new JPanel();
		tempPanel.add(msgSend);
		tempPanel.add(msgRecv);
		
		JPanel msg = new JPanel();
		msg.setLayout(new BorderLayout());
		msg.add(unreadMessageCount, BorderLayout.WEST);
		msg.add(tempPanel, BorderLayout.CENTER);
		msg.add(modemStatus, BorderLayout.EAST);
		
		baseFrame = new JFrame("ART4Me");
		baseFrame.setUndecorated(true);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = baseFrame.getContentPane();
		
		content.setLayout(new BorderLayout());
		content.add(msg, BorderLayout.PAGE_END);
		content.add(tabbedPane, BorderLayout.CENTER);
		
		baseFrame.setJMenuBar(generateMenuBar());
		baseFrame.pack();
		baseFrame.setSize(1100, 400);
		baseFrame.setLocation(300, 200);
		baseFrame.setResizable(true);
		baseFrame.setVisible(true);

		if (trial){
			OptionsPanel optPanel = new OptionsPanel(GUIDB.baseFrame, true);
			optPanel.setTitle("Options");
			optPanel.setSize(450, 465);
			optPanel.setLocationRelativeTo(baseFrame);
			optPanel.setResizable(false);
			optPanel.setVisible(true);
		}
		
	}

	private static JMenuBar generateMenuBar()
	{
		JMenu jmenuFile, jmenuPref, jmenuHelp;

		JMenuItem jmenuitemOpenCmdPrmpt, jmenuitemTestMessage, jmenuitemExit;
		JMenuItem jmenuitemOpt, jmenuitemClear;
		JMenuItem jmenuitemHelp, jmenuitemAbt;
		
		/*'File' menu starts*/
		jmenuFile = new JMenu("File");
		jmenuFile.setMnemonic(KeyEvent.VK_F);

		jmenuitemOpenCmdPrmpt = new JMenuItem("Open Command prompt..");
		jmenuitemOpenCmdPrmpt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try{
					Runtime.getRuntime().exec("cmd.exe /c start");
				}
				catch(Exception ee){
					log(ee.getMessage());
				}
			}
		});

		jmenuitemTestMessage = new JMenuItem("Test Message..");
		jmenuitemTestMessage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				try{
					String testNumber = JOptionPane.showInputDialog(baseFrame, "Please enter a mobile number");
					if (testNumber.length() == 10 ){
						MessageHandler.sendMessage(testNumber, "This is a Test Message sent from the ART4Me server. If you received this message, this means that the ART4Me server is properly configured.");
					}else{
						JOptionPane.showMessageDialog(baseFrame, "Invalid mobile number");
					}
				}
				catch(Exception ee){
					log(ee.getMessage());
				}
			}
		});
		
		jmenuitemExit = new JMenuItem("Quit");
		jmenuitemExit.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		jmenuitemExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		jmenuFile.add(jmenuitemOpenCmdPrmpt);
		jmenuFile.add(jmenuitemTestMessage);
		jmenuFile.addSeparator();
		jmenuFile.add(jmenuitemExit);
		/*'File' menu ends*/

		/*'Preferences' menu starts*/
		jmenuPref = new JMenu("Preferences");
		jmenuPref.setMnemonic(KeyEvent.VK_P);

		jmenuitemOpt = new JMenuItem("Options..");
		jmenuitemOpt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				OptionsPanel optPanel = new OptionsPanel(baseFrame, true);
				optPanel.setTitle("Options");
				optPanel.setSize(450, 465);
				optPanel.setLocationRelativeTo(baseFrame);
				optPanel.setResizable(true);
				optPanel.setVisible(true);
			}
		});
		jmenuPref.add(jmenuitemOpt);
		
		jmenuitemClear = new JMenuItem("Clear Preferences");
		jmenuitemClear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				OptionsPanel.prefs.remove(OptionsPanel.notDurPref);
				OptionsPanel.prefs.remove(OptionsPanel.followUpStatePref);
				OptionsPanel.prefs.remove(OptionsPanel.followUpDurPref);
				OptionsPanel.prefs.remove(OptionsPanel.infoMsgPref);
				OptionsPanel.prefs.remove(OptionsPanel.combineDurPref);
				OptionsPanel.prefs.remove(OptionsPanel.leadTextPref);
				OptionsPanel.prefs.remove(OptionsPanel.separatorTextPref);
				OptionsPanel.prefs.remove(OptionsPanel.trailTextPref);
			}
		});
		jmenuPref.add(jmenuitemClear);
		/*'Preferences' menu ends*/

		/*'Help' menu starts*/
		jmenuHelp = new JMenu("Help");
		jmenuHelp.setMnemonic(KeyEvent.VK_H);

		jmenuitemHelp = new JMenuItem("Help Contents");
		jmenuitemHelp.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F1,ActionEvent.ALT_MASK));
		jmenuitemHelp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(ClassLoader.getSystemResource("help.html").toURI());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});

		jmenuitemAbt = new JMenuItem("About..");
		jmenuitemAbt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				JLabel version = new JLabel("<html><center>ART4Me Server Prototype <br> Version 1.3</center></html>", new ImageIcon(ClassLoader.getSystemResource("Ribbon.png")), SwingConstants.CENTER);
				JOptionPane.showMessageDialog(baseFrame, version, "About", JOptionPane.PLAIN_MESSAGE);
			}
		});
		jmenuHelp.add(jmenuitemHelp);
		jmenuHelp.add(jmenuitemAbt);
		/*'Help' menu ends*/

		JMenuBar topMenuBar = new JMenuBar();
		topMenuBar.add(jmenuFile);
		topMenuBar.add(jmenuPref);
		topMenuBar.add(jmenuHelp);
		
		return topMenuBar;
	}
	
	public static void log(String logEvent){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println(sdf.format(System.currentTimeMillis()) + "-> " + logEvent);
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		InitDatabase initDB = new InitDatabase();
		initDB.createTables();
		try {
		    UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (Exception e) {
		    e.printStackTrace();
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run() {
				try {
					createGUI();
				} catch (ClassNotFoundException e) {
					log("The GUI could not be rendered!");
					e.printStackTrace();
				}
			}
		});
	}
}