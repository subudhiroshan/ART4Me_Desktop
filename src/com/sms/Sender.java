package com.sms;

import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.data.GUIDB;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class closes the connection to a modem by the Receiver. It opens a new connection and sends a text message. Hence, it calls the Receiver. 
 */
public class Sender implements Runnable  {

	private static final long STANDARD=500;
	private static final long LONG=2000;
	private static final long  VERYLONG=20000;
	SerialConnection mySerial =null;
	static final private char cntrlZ=(char)26;
	String in, out;
	Thread aThread=null;
	private long delay=STANDARD;
	String recipient=null;
	String message=null;
	
	private SerialParameters defaultParameters= new SerialParameters (SMSClient.defaultPort,19200,0,0,8,1,0);
	public int step;
	public int status=-1;
	public long messageNo=-1;

	public Sender(String recipient, String message){
		this.recipient=recipient;
		this.message=message;
	}

	public int send () throws Exception{
		SerialParameters params = defaultParameters;
		mySerial =new SerialConnection (params);

		Receiver.serialPort.close();
		mySerial.openConnection();
		aThread=new Thread(this);
		aThread.start() ;
		return 0;
	}


	public void run(){
		boolean timeOut=false;
		long startTime=(new Date()).getTime();
		while ((step <7) && (!timeOut)){
			timeOut=((new Date()).getTime() - startTime)>delay;
			if (timeOut && (step==1)) {
				step=-1;
				mySerial.send(""+cntrlZ);
			}
			String result=  mySerial.getIncomingString() ;
			int expectedResult=-1;
			try{
				switch (step){
				case 0:
					mySerial.send("atz");
					Thread.sleep(2000);
					mySerial.send("at+cnmi=1,2,0,0,0");
					delay=LONG;
					startTime=(new Date()).getTime();
					break;
				case 1:
					delay=STANDARD;
					mySerial.send("ath0");
					startTime=(new Date()).getTime();
					break;
				case 2:
					expectedResult=result.indexOf("OK");
					if (expectedResult>-1){
						mySerial.send("at+cmgf=1");
						startTime=(new Date()).getTime();
					}else{
						step=step-1;
					}
					break;
				case 3:
					expectedResult=result.indexOf("OK");

					if (expectedResult>-1){
						mySerial.send("at+csca=\"" + SMSClient.csca + "\"");
						startTime=(new Date()).getTime();
					}else{
						step=step-1;
					}
					break;
				case 4:
					expectedResult=result.indexOf("OK");

					if (expectedResult>-1){
						mySerial.send("at+cmgs=\""+recipient+"\"");
						startTime=(new Date()).getTime();
					}else{
						step=step-1;
					}
					break;
				case 5:
					expectedResult=result.indexOf(">");

					if (expectedResult>-1){
						mySerial.send(message+cntrlZ);
						startTime=(new Date()).getTime();
					}else{
						step=step-1;
					}
					delay=VERYLONG;
					break;
				case 6:
					expectedResult=result.indexOf("OK");
					if (expectedResult>-1){
						int n=result.indexOf("CMGS:");
						result=result.substring(n+5);
						n=result.indexOf("\n");
						status=0;
						messageNo=Long.parseLong(result.substring(0,n).trim() );

						GUIDB.log("sent message #:"+messageNo);
//						JOptionPane.showMessageDialog(null, "Your message is sent!");
						GUIDB.log("Your message is sent!");
						//Message sending ends here...
						GUIDB.sendButton.setEnabled(true);
						GUIDB.fwdButton.setEnabled(true);
						GUIDB.modemStatus.setIcon(new ImageIcon(ClassLoader.getSystemResource("Listening.png")));
						GUIDB.modemStatus.setToolTipText("Modem listening...");
					}else{
						step=step-1;
					}
					break;
				}
				step=step+1;
				Thread.sleep(100);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		mySerial.closeConnection() ;
		new Receiver();
		if (timeOut ) {
			status=-2;
			GUIDB.log("*** time out at step "+step+"***");
			JOptionPane.showMessageDialog(GUIDB.baseFrame, "An error occurred. Please try again. Your message was not sent!", "Message not sent", JOptionPane.ERROR_MESSAGE);
		}
	}
}