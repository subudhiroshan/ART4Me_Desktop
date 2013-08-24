package com.data;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class handles all text messages, incoming and outgoing, to/from the modem.
 */
public class MessageHandler {
	
	static Connection connRecd, connRead, connDel, connSent, connUser, connPillS, connPillP, connRepl, connFwd, connSendPill, connInfo, connSendFollowUpPill;
	static Statement stmtPillS, stmtPillP, stmtRead, stmtDel, stmtUser, stmtRecvId, stmtSentId, stmtName, stmtRet, stmtMsg, stmtNum, stmtPres, stmtSend, stmtRepl, stmtFwd, stmtSendPill, stmtSendPillType, stmtSendPillMsg, stmtInfoNum, stmtInfoSend, stmtSendFollowUpPillType, stmtSendFollowUpPill, stmtSendFollowUpPillMsg;
	static String msgId, msgCat, msgRcvdTime;
	static SimpleDateFormat inFormat, sqlFormat;
	static long sentMsgId = InitDatabase.startSentMsgId, recvMsgId = InitDatabase.startRecvMsgId;
	static String smartPhone = "###";
	static int count = 1;
	
	public static void msgReceived(String phoneNumber, String contents){
		try{
		String number = phoneNumber.substring(2);
		String contactId = null;
		Class.forName(InitDatabase.JDBC_DRIVER);
		connRecd = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
		stmtRet = connRecd.createStatement();
		ResultSet contIdRS = stmtRet.executeQuery("select contact_id from contacts_db where primary_phone='" + number +"';");
		if (contIdRS.isBeforeFirst()){
			while(contIdRS.next()){
				contactId = contIdRS.getString("contact_id");
			}
			GUIDB.log("Message Received");
			msgRcvdTime = Calendar.getInstance().getTime().toString();
			inFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			sqlFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			
			if (contents.indexOf(smartPhone) == -1){ //plain-phone
				if (contents.trim().equalsIgnoreCase("Yes")){
					msgPillTakenPlain(number);
				}else if (contents.trim().equalsIgnoreCase("Replay")){
					replayMsg(contactId, number);
				}else{
					if (contents.trim().length() > 150){
						msgUser(contactId, contents.substring(0, 150));
					}else{
						msgUser(contactId, contents.trim());
					}
				}
			}else{ //smart-phone
				String flag = contents.substring(contents.indexOf(smartPhone) + 3, contents.indexOf(smartPhone) + 9);
				if (flag.equalsIgnoreCase("ReadXX")){
					GUIDB.log("Message is read of " + number);
					msgRead(number);
				}else if (flag.equalsIgnoreCase("UserXX")){
					if (contents.substring(0, contents.indexOf(smartPhone)).trim().equalsIgnoreCase("Yes")){
						msgPillTakenSmart(number);
					}else{
						if (contents.substring(0, contents.indexOf(smartPhone)).length() > 150){
							msgUser(contactId, contents.substring(0, 150));
						}else{
							msgUser(contactId, contents.substring(0, contents.indexOf(smartPhone)));
						}
					}
				}else if (flag.equalsIgnoreCase("LastXX")){
					replayMsg(contactId, number);
				}else{
					msgDelivered(Long.parseLong(flag));
				}
			}
		}else {
			GUIDB.log("Spam Message " + number);
		}
		
		contIdRS.close();
		stmtRet.close();
		connRecd.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void msgDelivered(Long msgId){
		try{
			Class.forName(InitDatabase.JDBC_DRIVER);
			connDel = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtDel = connDel.createStatement();
			stmtDel.executeUpdate("update " + SentTab.currentTable + " set msg_delivered='" + sqlFormat.format(inFormat.parse(msgRcvdTime)).toString() +"' where sent_message_id = '" + msgId + "';");
			SentTab.refreshTableA();
			stmtDel.close();
			connDel.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void msgRead(String number){
		try{
			Class.forName(InitDatabase.JDBC_DRIVER);
			connRead = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtRead = connRead.createStatement();
			stmtRead.executeUpdate("update " + SentTab.currentTable + " set msg_read='" + sqlFormat.format(inFormat.parse(msgRcvdTime)).toString() + "' where recipient_number='" + number + "' and msg_read IS NULL and status='Sent';");
			SentTab.refreshTableA();
			stmtRead.close();
			connRead.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void msgPillTakenSmart(String number){
		try{
			Class.forName(InitDatabase.JDBC_DRIVER);
			connPillS = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtPillS = connPillS.createStatement();
			stmtPillS.executeUpdate("update " + SentTab.currentTable + " set pill_taken='" + sqlFormat.format(inFormat.parse(msgRcvdTime)).toString() + "' where msg_read in (select * from (select max(msg_read) from " + SentTab.currentTable + " where not prescription_id='" + InitDatabase.dumPId + "' and recipient_number='" + number + "')smart);");
			SentTab.refreshTableA();
			stmtPillS.close();
			connPillS.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void msgPillTakenPlain(String number){
		try{
			Class.forName(InitDatabase.JDBC_DRIVER);
			connPillP = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtPillP = connPillP.createStatement();
			stmtPillP.executeUpdate("update " + SentTab.currentTable + " set pill_taken='" + sqlFormat.format(inFormat.parse(msgRcvdTime)).toString() + "' where timestamp in (select * from (select max(timestamp) from " + SentTab.currentTable + " where not prescription_id='" + InitDatabase.dumPId + "' and recipient_number='" + number + "' and status='Sent')plain);");
			SentTab.refreshTableA();
			stmtPillP.close();
			connPillP.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void msgUser(String contactId, String contents){
		try {
			
			String temp = null;
			String contName = null;
			Class.forName(InitDatabase.JDBC_DRIVER);
			connUser = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtUser = connUser.createStatement();
			stmtName = connUser.createStatement();
			stmtRecvId = connUser.createStatement();
			ResultSet recvMsgIdRS = stmtRecvId.executeQuery("select max(received_message_id) from received_message_log;");
			while (recvMsgIdRS.next()){
				temp = recvMsgIdRS.getString("max(received_message_id)");
			}
			if (!(temp == null)){
				recvMsgId = Integer.parseInt(temp);
				recvMsgId++;
			}else{
				recvMsgId = InitDatabase.startRecvMsgId;
			}
			recvMsgIdRS.close();
			stmtRecvId.close();
			stmtUser.executeUpdate("insert into " + ReceivedTab.currentTable + " values('" + recvMsgId + "','" + contactId + "','"+ sqlFormat.format(inFormat.parse(msgRcvdTime)).toString() + "','" + contents.replace("'", "") +"');");
			ResultSet nameRS = stmtName.executeQuery("select concat(first_name,' ',last_name) from " + ContactTab.currentTable + " where contact_id='" + contactId +"';");
			while (nameRS.next()){
				contName = nameRS.getString("concat(first_name,' ',last_name)");
			}
			ReceivedTab.refreshTable();
			if (GUIDB.notifyFlag == true) {
				new DisappearingMessage( contName, contents, sqlFormat.format(inFormat.parse(msgRcvdTime)).toString(), GUIDB.notDur).showNotice();
			}
			if (System.currentTimeMillis() > GUIDB.lastViewed){
				GUIDB.unreadMessageCount.setToolTipText(count++ + "");
				if (count>0) GUIDB.unreadMessageCount.setIcon(new ImageIcon(ClassLoader.getSystemResource("Unread.png")));
			}
			stmtUser.close();
			connUser.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void replayMsg(String contactId, String number){
		try{
			Class.forName(InitDatabase.JDBC_DRIVER);
			connRepl = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtRepl = connRepl.createStatement();
			ResultSet msgContentsRS = stmtRepl.executeQuery("select contact_id, message_contents, prescription_id from " + SentTab.currentTable + " where timestamp in (select max(timestamp) from sent_message_log where not prescription_id='" + InitDatabase.dumPId + "' and recipient_number='" + number + "');");
			if (msgContentsRS.isBeforeFirst()){
				while (msgContentsRS.next()){
				sendManualMessage(msgContentsRS.getString("contact_id"), msgContentsRS.getString("message_contents"), msgContentsRS.getString("prescription_id"));
				}
			} else{
				sendManualMessage(contactId, "No recent activity found.", InitDatabase.dumPId);
			}
			SentTab.refreshTableA();
			msgContentsRS.close();
			stmtRepl.close();
			connRepl.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void sendManualMessage(String contactId, String contents, String prescriptionId){
		try{
			String temp = null;
			String number = null;
			String type = null;
			String sendDate = Calendar.getInstance().getTime().toString();
			sqlFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			inFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			
			Class.forName(InitDatabase.JDBC_DRIVER);
			connSent = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtNum = connSent.createStatement();
			stmtMsg = connSent.createStatement();
			stmtSend = connSent.createStatement();
			ResultSet numberRS = stmtNum.executeQuery("select primary_phone, smart from " + ContactTab.currentTable + " where contact_id='" + contactId +"';");
			ResultSet msgIdRS = stmtMsg.executeQuery("select * from " + MessageTab.currentTable + " where message_contents='" + contents +"';");
			if (numberRS.isBeforeFirst()){
				while (numberRS.next()){
					number = numberRS.getString("primary_phone");
					type = numberRS.getString("smart");
				}
				stmtSentId = connSent.createStatement();
				ResultSet sentMsgIdRS = stmtSentId.executeQuery("select max(sent_message_id) from sent_message_log;");
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
				boolean isSmart = type.equalsIgnoreCase("Y") ? true : false;
				
				if (msgIdRS.isBeforeFirst()){
					while (msgIdRS.next()){
						msgId = msgIdRS.getString("message_id");
						msgCat = msgIdRS.getString("message_category");
						stmtSend.executeUpdate("insert into " + SentTab.currentTable + " values('" + sentMsgId + "','" + contactId + "','"+ msgId + "','"+ msgCat + "','" + contents + "','" + prescriptionId + "','"+ number + "','" + sqlFormat.format(inFormat.parse(sendDate)) + "','Sent',null,null,null,null);");
						SentTab.refreshTableA();
					}
				}else{
//					JOptionPane.showMessageDialog(null, "Pill Reminder Message attempted to be sent manually. Really strange!!!");// Due to replay Message
				}
				
				if (isSmart){
					sendMessage("+1" + number, contents + " " + smartPhone + sentMsgId); 
				}else{
					sendMessage("+1" + number, contents);
				}
				
			}else {
				JOptionPane.showMessageDialog(null, "Invalid Number. Really strange!!!");
			}
			
			msgIdRS.close();
			numberRS.close();
			stmtNum.close();
			stmtMsg.close();
			stmtSend.close();
			connSent.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void sendInfoMessage(String contactId, String contents){
		try{
			String temp = null;
			String number = null;
			String type = null;
			String sendDate = Calendar.getInstance().getTime().toString();
			sqlFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			inFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			
			Class.forName(InitDatabase.JDBC_DRIVER);
			connInfo = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtInfoNum = connInfo.createStatement();
			stmtInfoSend = connInfo.createStatement();
			ResultSet numberRS = stmtInfoNum.executeQuery("select primary_phone, smart from " + ContactTab.currentTable + " where contact_id='" + contactId +"';");
			if (numberRS.isBeforeFirst()){
				while (numberRS.next()){
					number = numberRS.getString("primary_phone");
					type = numberRS.getString("smart");
				}
				stmtSentId = connInfo.createStatement();
				ResultSet sentMsgIdRS = stmtSentId.executeQuery("select max(sent_message_id) from sent_message_log;");
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
				boolean isSmart = type.equalsIgnoreCase("Y") ? true : false;

				stmtInfoSend.executeUpdate("insert into " + SentTab.currentTable + " values('" + sentMsgId + "','" + contactId + "','" + InitDatabase.dumMId + "','Informational','" + contents + "','" + InitDatabase.dumPId + "','" + number + "','" + sqlFormat.format(inFormat.parse(sendDate)) + "','Sent',null,null,null,null);");
				SentTab.refreshTableA();
				
				if (isSmart){
					sendMessage("+1" + number, contents + " " + smartPhone + sentMsgId); 
				}else{
					sendMessage("+1" + number, contents);
				}
				
			}else {
				JOptionPane.showMessageDialog(null, "Invalid Number. Really strange!!!");
			}
			
			numberRS.close();
			stmtInfoNum.close();
			stmtInfoSend.close();
			connInfo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void sendPillReminderMessage(String msgId){
		try{
			String phoneNumber = null;
			String contents = null;
			String type = null;
			Class.forName(InitDatabase.JDBC_DRIVER);
			connSendPill = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtSendPillType = connSendPill.createStatement();
			stmtSendPill = connSendPill.createStatement();
			stmtSendPillMsg = connSendPill.createStatement();
			ResultSet messageRS = stmtSendPillMsg.executeQuery("select recipient_number, message_contents from " + SentTab.currentTable + " where sent_message_id = '" + msgId + "';");
			while (messageRS.next()){
				phoneNumber = messageRS.getString("recipient_number");
				contents = messageRS.getString("message_contents");
			}
			ResultSet numberRS = stmtSendPillType.executeQuery("select smart from " + ContactTab.currentTable + " where primary_phone='" + phoneNumber +"';");
			while (numberRS.next()){
				type = numberRS.getString("smart");
			}
			boolean isSmart = type.equalsIgnoreCase("Y") ? true : false;

			if (isSmart){
				sendMessage("+1" + phoneNumber, contents + " " + smartPhone + msgId); 
			}else{
				sendMessage("+1" + phoneNumber, contents);
			}
			stmtSendPill.executeUpdate("update " + SentTab.currentTable + " set status='Sent' where sent_message_id = '" + msgId + "';");

			numberRS.close();
			messageRS.close();
			stmtSendPillMsg.close();
			stmtSendPillType.close();
			stmtSendPill.close();
			connSendPill.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void sendFollowUpReminderMessage(String msgId){
		try{
			String phoneNumber = null;
			String contents = null;
			String type = null;
			Class.forName(InitDatabase.JDBC_DRIVER);
			connSendFollowUpPill = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtSendFollowUpPillType = connSendFollowUpPill.createStatement();
			stmtSendFollowUpPill = connSendFollowUpPill.createStatement();
			stmtSendFollowUpPillMsg = connSendFollowUpPill.createStatement();
			ResultSet messageRS = stmtSendFollowUpPillMsg.executeQuery("select recipient_number, message_contents from " + SentTab.currentTable + " where sent_message_id = '" + msgId + "';");
			while (messageRS.next()){
				phoneNumber = messageRS.getString("recipient_number");
				contents = messageRS.getString("message_contents");
			}
			ResultSet numberRS = stmtSendFollowUpPillType.executeQuery("select smart from " + ContactTab.currentTable + " where primary_phone='" + phoneNumber +"';");
			while (numberRS.next()){
				type = numberRS.getString("smart");
			}
			boolean isSmart = type.equalsIgnoreCase("Y") ? true : false;

			if (isSmart){
				sendMessage("+1" + phoneNumber, contents + "FOLLOW-UP REMINDER " + smartPhone + msgId); 
			}else{
				sendMessage("+1" + phoneNumber, contents + "FOLLOW-UP REMINDER");
			}
			stmtSendFollowUpPill.executeUpdate("update " + SentTab.currentTable + " set reminder=now() where sent_message_id = '" + msgId + "';");

			numberRS.close();
			messageRS.close();
			stmtSendFollowUpPillMsg.close();
			stmtSendFollowUpPillType.close();
			stmtSendFollowUpPill.close();
			connSendFollowUpPill.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void forwardMessage(String message){
		try{
			String temp = null;
			String number = null;
			String type = null;
			String contactId = null;
			String sendDate = Calendar.getInstance().getTime().toString();
			sqlFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			inFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

			Class.forName(InitDatabase.JDBC_DRIVER);
			connFwd = DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmtNum = connFwd.createStatement();
			stmtMsg = connFwd.createStatement();
			stmtSend = connFwd.createStatement();
			ResultSet numberRS = stmtNum.executeQuery("select contact_id, primary_phone, smart from " + ContactTab.currentTable + " where not contact_id='" + InitDatabase.dumCId +"';");
			ResultSet msgIdRS = stmtMsg.executeQuery("select * from " + MessageTab.currentTable + " where message_contents='" + message +"';");
			while (numberRS.next()){
				contactId = numberRS.getString("contact_id");
				number = numberRS.getString("primary_phone");
				type = numberRS.getString("smart");
				GUIDB.log("Forwarded number is " + number);
				if (msgIdRS.isBeforeFirst()){
					while (msgIdRS.next()){
						msgId = msgIdRS.getString("message_id");
						msgCat = msgIdRS.getString("message_category");
					}
				}
				stmtSentId = connFwd.createStatement();
				ResultSet sentMsgIdRS = stmtSentId.executeQuery("select max(sent_message_id) from sent_message_log;");
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
				boolean isSmart = type.equalsIgnoreCase("Y") ? true : false;
				if (isSmart){
					sendMessage("+1" + number, message + " " + smartPhone + sentMsgId);
				}else{
					sendMessage("+1" + number, message);
				}
				stmtSend.executeUpdate("insert into " + SentTab.currentTable + " values('" + sentMsgId + "','" + contactId + "','"+ msgId + "','"+ msgCat + "','" + message + "','" + InitDatabase.dumPId + "','"+ number + "','" + sqlFormat.format(inFormat.parse(sendDate)) + "','Forwarded',null,null,'NA','NA');");
				SentTab.refreshTableA();
				Thread.sleep(12000); // Wait for 15 seconds before sending out next message
			}
			msgIdRS.close();
			numberRS.close();
			stmtNum.close();
			stmtMsg.close();
			stmtSend.close();
			connFwd.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void sendMessage(String number, String message) throws InterruptedException{
		// implement a queue here...
		if (GUIDB.trial){
			GUIDB.SMSC.sendMessage(number, message);
		}else{
			GUIDB.log("Message sent to: " + number + " | " + message + "|");
		}
	}
}
