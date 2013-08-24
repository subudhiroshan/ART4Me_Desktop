package com.sms;

import java.util.Enumeration;

import javax.swing.ImageIcon;

import com.data.GUIDB;

import gnu.io.*;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class configures the modem, to establish serial connections and submits messages to be sent. 
 */
public class SMSClient implements Runnable{
	
	public final static int SYNCHRONOUS=0;
	public final static int ASYNCHRONOUS=1;
	private Thread myThread=null;
	private int mode=-1;
	private String recipient=null;
	private String message=null;
	public int status=-1;
	public long messageNo=-1;

	static String defaultPort = "COM";
	static String csca="+12063130004";
	Enumeration<?> portList;
	static CommPortIdentifier portId;
	
	public SMSClient(int mode) {
		this.mode=mode;
	}

	public int sendMessage(String recipient, String message){
		this.recipient=recipient;
		this.message=message;
		GUIDB.log("Recipient: " + recipient + " Message: " + message);
		myThread = new Thread(this);
		myThread.start();
		return status;
	}
	
	public void run(){
		//Message sending starts here...
		GUIDB.modemStatus.setIcon(new ImageIcon(ClassLoader.getSystemResource("Sending.png")));
		GUIDB.modemStatus.setToolTipText("Modem sending");
		GUIDB.sendButton.setEnabled(false);
		GUIDB.fwdButton.setEnabled(false);
		
		Sender aSender = new Sender(recipient,message);
		try{
			aSender.send ();
			if (mode==SYNCHRONOUS) {
				while (aSender.status == -1){
					Thread.sleep (1000);
				}
			}
			if (aSender.status == 0) messageNo=aSender.messageNo ;
		}catch (Exception e){
			e.printStackTrace();
		}
		this.status=aSender.status ;
		aSender=null;
	}
	
	public void configureModem(String comPort, String csca){
		defaultPort = comPort;
		SMSClient.csca = csca;
		
		boolean portFound = false;

		portList = CommPortIdentifier.getPortIdentifiers();
		//Verifying the Port and identifying if Serial or not!!!
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(defaultPort)) {
					GUIDB.log("Found port: "+defaultPort);
					portFound = true;
					break;
				} 
			} 
		}
		if (!portFound) {
			GUIDB.log("Port " + defaultPort + " not found.");
		}
	}
	
}