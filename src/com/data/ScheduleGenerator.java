package com.data;


import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class causes the generation of Pill Reminder messages every night.
 */
public class ScheduleGenerator extends TimerTask {

	private Timer generator = new Timer();
	
	public ScheduleGenerator(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.add(Calendar.DAY_OF_MONTH, 1); // Next day from 00:00 AM
		long startTime = c.getTimeInMillis() - System.currentTimeMillis();
		if (GUIDB.trial){
			generator.schedule(this, startTime, 24*60*60*1000); // Starts initially at midnight and runs every midnight
		}else{
			generator.schedule(this, 60*1000, 24*60*60*1000);
		}
	}
	
	@Override
	public void run() {
		//GUIDB.log(new java.util.Date() + " : Generator invoked.");
		MessageQueue.msgQueueGenerator();
	}
}
