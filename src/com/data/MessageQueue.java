package com.data;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.PreparedStatement;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class generates and schedules Pill Reminder messages for all patients.
 */
public class MessageQueue {
	
	static String contId;
	static String presId;
	static String presGN;
	static String presTN;
	static String presPur;
	static String presPref;
	static String presDos;
	static String presHour;
	static String presMin;
	static String presFreq;
	static String number;
	static SimpleDateFormat sqlFormat;
	static int n = 0;
	static long sentMsgId = InitDatabase.startSentMsgId;
	static Connection connQueue, connSend;
	static Statement stmtPres, stmtMsg, stmtSentId, stmtSendCId, stmtSendMsg, stmtSendRem, stmtError;
	
	static void msgQueueGenerator(){
		try{
			String temp=null;
			sqlFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			Class.forName(InitDatabase.JDBC_DRIVER);
			connQueue = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtMsg = connQueue.createStatement();
			stmtError = connQueue.createStatement();
			stmtPres = connQueue.createStatement();
			stmtError.executeUpdate("update " + SentTab.currentTable + " set status = 'Error' where status = 'Scheduled';");
			ResultSet prescriptionRS = stmtPres.executeQuery("select * from " + PrescriptionTab.currentTable + " where not prescription_id = '" + InitDatabase.dumPId +"' and status='A';");
			while (prescriptionRS.next()){
				presId = prescriptionRS.getString("prescription_id");
				contId = prescriptionRS.getString("contact_id");
				presGN = prescriptionRS.getString("generic_name");
				presTN = prescriptionRS.getString("trade_name");
				presPur = prescriptionRS.getString("purpose");
				presPref = prescriptionRS.getString("preference");
				presDos = prescriptionRS.getString("dosage");
				presHour = prescriptionRS.getString("hours");
				presMin = prescriptionRS.getString("minutes");
				presFreq = prescriptionRS.getString("frequency");

				ResultSet numberRS = stmtMsg.executeQuery("select primary_phone from " + ContactTab.currentTable + " where contact_id='" + contId +"';");
				while (numberRS.next()){
					number = numberRS.getString("primary_phone");
				}
				
				switch (presFreq){
				case "Customly":
					n=30;break;
				case "Hourly":
					n=24;break;
				case "Thrice Daily":
					n=3;break;
				case "Twice Daily":
					n=2;break;
				case "Once Daily":
					n=1;break;
				default: n=1;
				}
				
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, Integer.parseInt(presHour));
				today.set(Calendar.MINUTE, Integer.parseInt(presMin));
				today.set(Calendar.SECOND, 0);
				long startTime = today.getTimeInMillis();
				
				Calendar midnight = Calendar.getInstance();
				midnight.set(Calendar.HOUR_OF_DAY, 0);
				midnight.set(Calendar.MINUTE, 0);
				midnight.set(Calendar.SECOND, 0);
				midnight.add(Calendar.DAY_OF_MONTH, 1);
				long endTime = midnight.getTimeInMillis();
				long schedTime = startTime;
				
				for(int z=0; z<n; z++){
					stmtSentId = connQueue.createStatement();
					ResultSet sentMsgIdRS = stmtSentId.executeQuery("select max(sent_message_id) from " + SentTab.currentTable + ";");
					while (sentMsgIdRS.next()){
						temp = sentMsgIdRS.getString("max(sent_message_id)");
					}
					if (!(temp == null)){
						sentMsgId = Integer.parseInt(temp);
						sentMsgId++;
					}else{
						sentMsgId = InitDatabase.startSentMsgId;
					}
					sentMsgIdRS.close();
					stmtSentId.close();

					schedTime = startTime + (z)*(24*60*60*1000/n);
					if (schedTime > endTime){
						break;
					}else{
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(schedTime);
						String reminderMsg = null;
						switch (presPref){
						case "Generic":
							if (Integer.parseInt(presDos) == 1){
								reminderMsg = GUIDB.lead + " " + presDos + " pill of " + presGN + " " + GUIDB.trail;
							}else{
								reminderMsg = GUIDB.lead + " " + presDos + " pills of " + presGN + " " + GUIDB.trail;
							}
							break;
						case "Trade":
							if (Integer.parseInt(presDos) == 1){
								reminderMsg = GUIDB.lead + " " + presDos + " pill of " + presTN + " " + GUIDB.trail;
							}else{
								reminderMsg = GUIDB.lead + " " + presDos + " pills of " + presTN + " " + GUIDB.trail;
							}
							break;
						case "Purpose":
							if (Integer.parseInt(presDos) == 1){
								reminderMsg = GUIDB.lead + " " + presDos + " pill for " + presPur + " " + GUIDB.trail;
							}else{
								reminderMsg = GUIDB.lead + " " + presDos + " pills for " + presPur + " " + GUIDB.trail;
							}
							break;	
						}
						stmtMsg.executeUpdate("insert into " + SentTab.currentTable + " values('" + sentMsgId + "','" + contId + "','"+ InitDatabase.dumMId + "','Pill Reminder','" + reminderMsg + "','" + presId + "','" + number + "','" + sqlFormat.format(cal.getTime()) + "','Scheduled',null,null,null,null);");
					}
				}
			}
			prescriptionRS.close();
			stmtError.close();
			stmtMsg.close();
			stmtPres.close();
			connQueue.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void msgQueueSender(){
		try{
			Class.forName(InitDatabase.JDBC_DRIVER);
			connSend = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtSendCId = connSend.createStatement();
			stmtSendMsg = connSend.createStatement();
			String multiMsgQuery = "select * from " + SentTab.currentTable + " where status='Scheduled' and timestamp between Date_sub(now(), INTERVAL " + GUIDB.combineDur + " MINUTE) AND now() and contact_id = ? order by sent_message_id;";
			PreparedStatement multiMsgPS = connSend.prepareStatement(multiMsgQuery);
			ResultSet latestRS = stmtSendCId.executeQuery("select contact_id from " + SentTab.currentTable + " where status='Scheduled' and timestamp between Date_sub(now(), INTERVAL " + GUIDB.combineDur + " MINUTE) AND now() group by contact_id;");
			if (latestRS.isBeforeFirst()){
				while (latestRS.next()){
					multiMsgPS.setString(1, latestRS.getString("contact_id"));
					ResultSet multiMsgRS = multiMsgPS.executeQuery(); 
					multiMsgRS.last();
					if (multiMsgRS.getRow() > 1){
						multiMsgRS.beforeFirst();
						StringBuilder combinedMsg = new StringBuilder(GUIDB.lead + " ");
						String tempId = null;
						String tempCId = null;
						String tempMId = null;
						String tempMCat = null;
						String tempPid = null;
						String tempNum = null;
						String tempTime = null;
						while (multiMsgRS.next()){
							tempId = multiMsgRS.getString("sent_message_id");
							tempCId = multiMsgRS.getString("contact_id");
							tempMId = multiMsgRS.getString("message_id");
							tempMCat = multiMsgRS.getString("message_category");
							tempPid = multiMsgRS.getString("prescription_id");
							tempNum = multiMsgRS.getString("recipient_number");
							tempTime = multiMsgRS.getString("timestamp");
							combinedMsg.append(multiMsgRS.getString("message_contents").replace(GUIDB.lead + " ", "").replace(GUIDB.trail, "")).append(GUIDB.separator + " ");
							stmtSendMsg.executeUpdate("delete from " + SentTab.currentTable + " where sent_message_id = '" + tempId + "';");
						}
						combinedMsg.replace(combinedMsg.length() - GUIDB.separator.length() - 1, combinedMsg.length(), "").append(GUIDB.trail);
						stmtSendMsg.executeUpdate("insert into " + SentTab.currentTable + " values('" + tempId + "','" + tempCId + "','"+ tempMId + "','" + tempMCat + "','" + combinedMsg.toString() + "','" + tempPid + "','" + tempNum + "','" + tempTime + "','Sent',null,null,null,null);");
						MessageHandler.sendPillReminderMessage(tempId);
					}else{
						multiMsgRS.beforeFirst();
						while(multiMsgRS.next()){
							MessageHandler.sendPillReminderMessage(multiMsgRS.getString("sent_message_id"));
						}
					}
				}
			}
			stmtSendMsg.close();
			latestRS.close();
			stmtSendCId.close();
			if (GUIDB.followUp){
				stmtSendRem = connSend.createStatement(); 
				ResultSet reminderRS = stmtSendRem.executeQuery("select sent_message_id from " + SentTab.currentTable + " where message_category='Pill Reminder' and status='Sent' and pill_taken is null and reminder is null and Date_sub(timestamp, INTERVAL -" + GUIDB.followUpDur + " MINUTE) between now() and Date_sub(now(), INTERVAL -1 MINUTE);"); 
				if (reminderRS.isBeforeFirst()){
					while (reminderRS.next()){
						MessageHandler.sendFollowUpReminderMessage(reminderRS.getString("sent_message_id"));
					}
				}
				reminderRS.close();
				stmtSendRem.close();
			}
			connSend.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
