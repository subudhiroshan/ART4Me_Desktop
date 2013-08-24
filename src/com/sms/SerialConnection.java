package com.sms;

import gnu.io.*;
//import javax.comm.*;
import java.io.*;
import java.awt.event.*;
import java.util.TooManyListenersException;

import com.data.GUIDB;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class sets up a Serial Connection to a modem connected to the PC.
 */
public class SerialConnection implements SerialPortEventListener, CommPortOwnershipListener {
	
	private SerialParameters parameters;
	private OutputStream os;
	private InputStream is;
	private CommPortIdentifier portId;
	private SerialPort sPort;
	private boolean open = false;
	private String receptionString="";

	public String getIncomingString(){
		byte[] bVal= receptionString.getBytes();
		receptionString="";
		return new String (bVal);
	}

	public SerialConnection(SerialParameters parameters) {
		this.parameters = parameters;
		open = false;
	}

	public void openConnection() throws SerialConnectionException {
		try {
			portId = CommPortIdentifier.getPortIdentifier(parameters.getPortName());
		}catch (NoSuchPortException e) {
			e.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			sPort = (SerialPort)portId.open("SMSSender", 30000);
		}catch (PortInUseException e) {
			GUIDB.log("Exception: The port is in use (SerialConnection.open ).");
//			throw new SerialConnectionException(e.getMessage());
			e.printStackTrace();
		}
		sPort.sendBreak(1000);
		try {
			setConnectionParameters();
		}catch (SerialConnectionException e) {
			sPort.close();
			throw e;
		}
		try {
			os = sPort.getOutputStream();
			is = sPort.getInputStream();
		} catch (IOException e) {
			sPort.close();
			throw new SerialConnectionException("Error opening I/O streams");
		}
		try {
			sPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			sPort.close();
			throw new SerialConnectionException("Too many listeners added");
		}
		sPort.notifyOnDataAvailable(true);
		sPort.notifyOnBreakInterrupt(true);
		try {
			sPort.enableReceiveTimeout(30);
		} catch (UnsupportedCommOperationException e) {
		}
		portId.addPortOwnershipListener(this);
		open = true;
	}

	public void setConnectionParameters() throws SerialConnectionException {
		int oldBaudRate = sPort.getBaudRate();
		int oldDatabits = sPort.getDataBits();
		int oldStopbits = sPort.getStopBits();
		int oldParity   = sPort.getParity();
		sPort.getFlowControlMode();
		try {
			sPort.setSerialPortParams(parameters.getBaudRate(),
					parameters.getDatabits(),
					parameters.getStopbits(),
					parameters.getParity());
		} catch (UnsupportedCommOperationException e) {
			parameters.setBaudRate(oldBaudRate);
			parameters.setDatabits(oldDatabits);
			parameters.setStopbits(oldStopbits);
			parameters.setParity(oldParity);
			throw new SerialConnectionException("Unsupported parameter");
		}
		try {
			sPort.setFlowControlMode(parameters.getFlowControlIn()
					| parameters.getFlowControlOut());
		} catch (UnsupportedCommOperationException e) {
			throw new SerialConnectionException("Unsupported flow control");
		}
	}
	
	public void closeConnection() {
		if (!open) {
			return;
		}
		if (sPort != null) {
			try {
				os.close();
				is.close();
			} catch (IOException e) {
				System.err.println(e);
			}
			sPort.close();
			portId.removePortOwnershipListener(this);
		}
		open = false;
	}

	public void sendBreak() {
		sPort.sendBreak(1000);
	}

	public boolean isOpen() {
		return open;
	}

	public void serialEvent(SerialPortEvent e) {
		StringBuffer inputBuffer = new StringBuffer();
		int newData = 0;
		switch (e.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			while (newData != -1) {
				try {
					newData = is.read();
					if (newData == -1) {
						break;
					}
					if ('\r' == (char)newData) {
						inputBuffer.append('\n');
					} else {
						inputBuffer.append((char)newData);
					}
				} catch (IOException ex) {
					System.err.println(ex);
					return;
				}
			}
			receptionString=receptionString+ (new String(inputBuffer));        
			break;
		case SerialPortEvent.BI:
			receptionString=receptionString+("\n--- BREAK RECEIVED ---\n");
		}
	}

	public void ownershipChange(int type) {
	}

	class KeyHandler extends KeyAdapter {
		OutputStream os;

		public KeyHandler(OutputStream os) {
			super();
			this.os = os;
		}
		public void keyTyped(KeyEvent evt) {
			char newCharacter = evt.getKeyChar();
			if ((int)newCharacter==10) newCharacter = '\r';
			System.out.println((int)newCharacter);
			try {
				os.write((int)newCharacter);
			} catch (IOException e) {
				System.err.println("OutputStream write error: " + e);
			}
		}
	}
	
	public void send(String message) {
		byte[] theBytes= (message+"\n").getBytes();
		for (int i=0; i<theBytes.length;i++){
			char newCharacter = (char)theBytes[i];
			if ((int)newCharacter==10) newCharacter = '\r';
			try {
				os.write((int)newCharacter);
			} catch (IOException e) {
				System.err.println("OutputStream write error: " + e);
			}
		}
	}
}
