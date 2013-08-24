package com.data;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class causes the sending of Pill Reminder messages, every minute.
 */
public class ScheduleSender extends TimerTask {

	private Timer sender = new Timer();
	
	public ScheduleSender(){
		sender.schedule(this, 10*1000, 60*1000);//Starts initially after 10 seconds and runs every minute
	}
	
	@Override
	public void run() {
		//GUIDB.log(new java.util.Date() + " : Sender invoked.");
		MessageQueue.msgQueueSender();
	}
}
