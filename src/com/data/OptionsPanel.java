package com.data;


import gnu.io.CommPortIdentifier;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import com.sms.Receiver;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class  provides a prompt to set up the preferences of the ART4Me system.
 */
public class OptionsPanel extends JDialog{
	
	private static final long serialVersionUID = 1L;
	private static boolean isSettable = true;
	final static JComboBox<String> portFld = new JComboBox<String>();

	JPanel optionsBasePanel = new JPanel();
	
	static Preferences prefs = Preferences.userRoot();
	static String notDurPref = "notDur";
	static String followUpStatePref = "followUpState";
	static String followUpDurPref = "followUpDur";
	static String infoMsgPref = "infoMsg";
	static String combineDurPref = "combineDur";
	static String leadTextPref = "leadText";
	static String separatorTextPref = "separatorText";
	static String trailTextPref = "trailText";
	
	JLabel modelLbl = new JLabel("Model: ");
	final JTextField modelFld = new JTextField(6);
	JPanel modelPanel = new JPanel();
	
	JLabel portLbl = new JLabel("Serial Port: ");
	JLabel portHelp = new JLabel(new ImageIcon(ClassLoader.getSystemResource("Help.png")));
	JPanel portPanel = new JPanel();
	
	JLabel msgctrLbl = new JLabel("Message Center Number:");
	JLabel plusOne = new JLabel("+1");
	final JTextField msgctrFld = new JTextField(8);
	JPanel msgctrPanel = new JPanel();
	
	JLabel notDurLbl = new JLabel("Show notifications for ");
	final JTextField notDurFld = new JTextField(2);
	JLabel notDurUnitsLbl = new JLabel(" Seconds");
	JPanel notDurPanel = new JPanel();
	
	JLabel followUpDurLbl = new JLabel("Follow Up Reminder: ");
	final JCheckBox followUpState = new JCheckBox();
	final JTextField followUpDurFld = new JTextField(2);
	JLabel followUpDurUnitsLbl = new JLabel("Minutes");
	JPanel followUpDurPanel = new JPanel();
	
	JLabel infoMsgLbl = new JLabel("Informational Text: ");
	final JTextField count = new JTextField(3);
	final JTextArea infoMsgText;
	JPanel infoMsgPanel = new JPanel();
	JPanel infoPanel = new JPanel();
	
	JLabel combineDurLbl = new JLabel("Combine Messages within ");
	final JTextField combineDurFld = new JTextField(2);
	JLabel combineDurUnitsLbl = new JLabel(" Minutes");
	JPanel combineDurPanel = new JPanel();
	
	final JTextArea lead;
	JLabel pres1 = new JLabel(" Prescription1 ");
	final JTextArea separator;
	JLabel pres2 = new JLabel(" Prescription2 ");
	final JTextArea trail;
	JPanel pillRemPanel = new JPanel();
	
	JPanel buttonPanel=new JPanel();
	JButton saveButt = new JButton(new ImageIcon(ClassLoader.getSystemResource("Save.png")));
	JButton cancelButt = new JButton(new ImageIcon(ClassLoader.getSystemResource("Cancel.png")));
	
	JScrollPane optionsPane = new JScrollPane(optionsBasePanel);
	
	public static void enumeratePorts(){
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
		portFld.removeAllItems();
		model.addElement("");
		while(ports.hasMoreElements()){
			CommPortIdentifier port = (CommPortIdentifier)ports.nextElement();
			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL){
				model.addElement(port.getName());
			}
		}
		portFld.setModel(model);
	}
	
	public OptionsPanel(JFrame parentFrame, boolean modality)
	{
		super(parentFrame, modality);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		if (isSettable){
			enumeratePorts();
		}
		
		modelFld.setText("E173");
		modelFld.setEditable(false);
		modelPanel.setLayout(new BorderLayout());
		modelPanel.add(modelLbl, BorderLayout.WEST);
		modelPanel.add(modelFld, BorderLayout.CENTER);

		portFld.setEnabled(isSettable);
		portHelp.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(ClassLoader.getSystemResource("help.html").toURI());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		portPanel.setLayout(new BorderLayout());
		portPanel.add(portLbl, BorderLayout.WEST);
		portPanel.add(portFld, BorderLayout.CENTER);
		portPanel.add(portHelp, BorderLayout.EAST);
		
		msgctrFld.setText(GUIDB.csca.substring(2));
		msgctrFld.setEditable(isSettable);
		msgctrPanel.setLayout(new BorderLayout());
		msgctrPanel.add(plusOne, BorderLayout.WEST);
		msgctrPanel.add(msgctrFld, BorderLayout.CENTER);
		
		notDurFld.setText(prefs.getInt(notDurPref, 5) + "");
		notDurPanel.setLayout(new BorderLayout());
		notDurPanel.add(notDurLbl, BorderLayout.WEST);
		notDurPanel.add(notDurFld, BorderLayout.CENTER);
		notDurPanel.add(notDurUnitsLbl, BorderLayout.EAST);
		
		followUpState.setSelected(prefs.getBoolean(followUpStatePref, true));
		followUpDurFld.setText(prefs.getInt(followUpDurPref, 5) + "");
		followUpDurFld.setEnabled(followUpState.isSelected());
		followUpState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!followUpState.isSelected()){
					followUpDurFld.setEnabled(false);
					GUIDB.followUp = false;
				}
				if (followUpState.isSelected()){
					followUpDurFld.setEnabled(true);
					GUIDB.followUp = true;
				}
			}
		});
		followUpDurPanel.setLayout(new FlowLayout());
		followUpDurPanel.add(followUpDurLbl);
		followUpDurPanel.add(followUpState);
		followUpDurPanel.add(followUpDurFld);
		followUpDurPanel.add(followUpDurUnitsLbl);
		followUpDurPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		infoMsgText = new JTextArea(prefs.get(infoMsgPref, GUIDB.infoMsg), 5, 20);
		infoMsgText.setWrapStyleWord(true);
		infoMsgText.setLineWrap(true);
		count.setText(infoMsgText.getText().trim().length() + "");
		count.setEnabled(false);
		infoMsgPanel.setLayout(new BorderLayout());
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(infoMsgLbl, BorderLayout.WEST);
		infoPanel.add(count, BorderLayout.EAST);
		infoMsgPanel.add(infoPanel, BorderLayout.NORTH);
		infoMsgPanel.add(infoMsgText, BorderLayout.CENTER);
		infoMsgText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				count.setText(infoMsgText.getText().trim().length() + "");
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (infoMsgText.getText().trim().length() > 149){
					try {
						infoMsgText.setText(infoMsgText.getText(0, 149));
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		combineDurFld.setText(prefs.getInt(combineDurPref, 5) + "");
		combineDurPanel.setLayout(new BorderLayout());
		combineDurPanel.add(combineDurLbl, BorderLayout.WEST);
		combineDurPanel.add(combineDurFld, BorderLayout.CENTER);
		combineDurPanel.add(combineDurUnitsLbl, BorderLayout.EAST);

		lead = new JTextArea(prefs.get(leadTextPref, GUIDB.lead), 1, 6);
		separator = new JTextArea(prefs.get(separatorTextPref, GUIDB.separator), 1, 2);
		trail = new JTextArea(prefs.get(trailTextPref, GUIDB.trail), 1, 10);
		
		pillRemPanel.setLayout(new BoxLayout(pillRemPanel, BoxLayout.X_AXIS));
		pillRemPanel.add(lead);
		pillRemPanel.add(pres1);
		pillRemPanel.add(separator);
		pillRemPanel.add(pres2);
		pillRemPanel.add(trail);
		
		buttonPanel.add(cancelButt,BorderLayout.EAST);
		buttonPanel.add(saveButt,BorderLayout.WEST);
		saveButt.setToolTipText("Save");
		saveButt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae) {
				if (validateFields()){
					GUIDB.defaultPort = portFld.getSelectedItem().toString();
					GUIDB.csca = "+1" + msgctrFld.getText();
					
					GUIDB.notDur = Integer.parseInt(notDurFld.getText());
					prefs.putInt(notDurPref, Integer.parseInt(notDurFld.getText()));
					
					prefs.putBoolean(followUpStatePref, followUpState.isSelected());
					if (followUpState.isSelected()) {
						GUIDB.followUpDur = Integer.parseInt(followUpDurFld.getText());
						prefs.putInt(followUpDurPref, Integer.parseInt(followUpDurFld.getText()));
					}
					
					GUIDB.combineDur = Integer.parseInt(combineDurFld.getText());
					prefs.putInt(combineDurPref, Integer.parseInt(combineDurFld.getText()));
					
					GUIDB.infoMsg = infoMsgText.getText().trim().replaceAll("\'", "");
					prefs.put(infoMsgPref, infoMsgText.getText().trim().replaceAll("\'", ""));
					
					GUIDB.lead = lead.getText().trim();
					prefs.put(leadTextPref, lead.getText().trim());
					
					GUIDB.separator = separator.getText().trim();
					prefs.put(separatorTextPref, separator.getText().trim());
					
					GUIDB.trail = trail.getText().trim();
					prefs.put(trailTextPref, trail.getText().trim());
					
					if (isSettable){
						GUIDB.SMSC.configureModem(GUIDB.defaultPort, GUIDB.csca);
						if (GUIDB.trial) new Receiver();
					}
					isSettable = false;
					setVisible(false);
				}
			}
		});
		cancelButt.setToolTipText("Cancel");
		cancelButt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				if (isSettable){
					JOptionPane.showMessageDialog(getContentPane(), "Please provide the settings!", "Settings Required", JOptionPane.ERROR_MESSAGE);
				}else{
					setVisible(false);
				}
			}
		});

		optionsBasePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.CENTER));
		optionsBasePanel.add(modelPanel);
		optionsBasePanel.add(portPanel);
		optionsBasePanel.add(msgctrLbl);
		optionsBasePanel.add(msgctrPanel);
		optionsBasePanel.add(notDurPanel);
		optionsBasePanel.add(followUpDurPanel);
		optionsBasePanel.add(infoMsgPanel);
		optionsBasePanel.add(combineDurPanel);
		optionsBasePanel.add(pillRemPanel);
		optionsBasePanel.add(buttonPanel);
		pack();
		
		optionsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		optionsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		getContentPane().add(optionsPane);
	}

	boolean validateFields() {
		boolean retValue = true;
		
		if(portFld.getSelectedItem().toString().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Please select a Port!");
		}else if(msgctrFld.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message Center Number is required!");
		}else if(!Validation.isNumber(msgctrFld.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message Center Number can contain only numbers!");
		}else if(notDurFld.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Notification duration is required!");
		}else if(!Validation.isNumber(notDurFld.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Notification duration can contain only numbers!");
		}else if(followUpDurFld.getText().isEmpty()){
			if (followUpState.isSelected()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Follow Up Duration is required!");}
		}else if(!Validation.isNumber(followUpDurFld.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Follow Up Duration can contain only numbers!");
		}else if(combineDurFld.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Duration to combine messages is required!");
		}else if(!Validation.isNumber(combineDurFld.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Duration to combine messages can contain only numbers.");
		}else if(lead.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Leading message contents are required!");
		}else if(!Validation.isText(lead.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Leading message contents cannot contain special characters.");
		}else if(separator.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message contents between Prescription names is required!");
		}else if(!Validation.isText(separator.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Message contents between Prescription names cannot contain special characters.");
		}else if(trail.getText().isEmpty()){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Trailing message contents are required!");
		}else if(!Validation.isText(trail.getText().trim())){
			retValue = false;
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "Trailing message contents cannot contain special characters.");
		}
		return retValue;
	}
}