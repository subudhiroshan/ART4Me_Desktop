package com.sms;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import javax.swing.ImageIcon;

import gnu.io.*;

import com.data.GUIDB;
import com.data.MessageHandler;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class receives a message from a modem, listening continuously. 
 */
public class Receiver implements SerialPortEventListener{
	static SerialConnection mySerial =null;
	static final private char cntrlZ=(char)26;
	private SerialParameters defaultParameters= new SerialParameters (SMSClient.defaultPort,19200,0,0,8,1,0);
	static InputStream inputStream;
	static SerialPort serialPort;
	
	public Receiver(){
		try{
			SerialParameters params = defaultParameters;
			mySerial =new SerialConnection (params);
			mySerial.openConnection();
			if (mySerial.isOpen()){
				mySerial.send(""+cntrlZ);
				mySerial.send("atz");
				Thread.sleep(2000);
				mySerial.send("at+cnmi=1,2,0,0,0");
				Thread.sleep(1000);
				mySerial.send("at+cmgf=1");
				Thread.sleep(1000);
				}
			mySerial.closeConnection() ;
			//Message receiving begins here...
			GUIDB.sendButton.setEnabled(true);
			GUIDB.fwdButton.setEnabled(true);
			GUIDB.modemStatus.setIcon(new ImageIcon(ClassLoader.getSystemResource("Listening.png")));
			GUIDB.modemStatus.setToolTipText("Modem listening...");
			GUIDB.log("Listening...\n");
			try {
				serialPort = (SerialPort) SMSClient.portId.open("SMSReceiver", 20000);
			} catch (PortInUseException e) {
				GUIDB.log("Exception: The port is in use (Receiver).");
				GUIDB.log(e.getMessage());
			}
			//try{
			try {
				inputStream = serialPort.getInputStream();
			} catch (Exception e) {GUIDB.log(e.getMessage());}

			try {
				serialPort.addEventListener(this);
			} catch (TooManyListenersException e) {GUIDB.log(e.getMessage());}

			try{
				serialPort.notifyOnDataAvailable(true);
			}catch (Exception e) {GUIDB.log(e.getMessage());}

			try {
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {e.getMessage();}
			//}catch (Exception e){
			//	e.getMessage();
			//}
		}
		catch (Exception e){
			GUIDB.log(e.getMessage());
		}
	}

	
	@Override
	public void serialEvent(SerialPortEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEventType()) {
			case SerialPortEvent.BI:
			case SerialPortEvent.OE:
			case SerialPortEvent.FE:
			case SerialPortEvent.PE:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.RI:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				System.out.print("Buffer empty!!!"); 
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				byte[] readBuffer = new byte[200];
				try {
					while (inputStream.available() > 0) {
						GUIDB.log("Data received on port...");
						inputStream.read(readBuffer);
					} 
					String rawInput = new String(readBuffer); 
					if ( rawInput.indexOf("+CMT:") > -1){
						String from = rawInput.substring(rawInput.indexOf("+CMT:")+7,rawInput.indexOf("+CMT:")+19);
						String body = rawInput.substring(rawInput.indexOf("+CMT:")+46);
						String time = rawInput.substring(rawInput.indexOf("+CMT:")+23,rawInput.indexOf("+CMT:")+40);
						GUIDB.log(body + " received from " + from + " at " + time + " --- in Receiver");// send this to anyone...
						MessageHandler.msgReceived(from, body);
					}
				} catch (IOException e) {
					GUIDB.log(e.getMessage());
				}
				break;
		}
	}
}