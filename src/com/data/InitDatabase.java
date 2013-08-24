package com.data;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class initializes the MySQL database, ready to be accessed.
 */
public class InitDatabase {
	
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	public static final String DB_URL = "jdbc:mysql://localhost:3306/";
	public static final String DB = "ServerDB";
	public static final String USER = "root";
	public static final String PASS = "lighthouse";
	public static final String dumMId = "M101";
	public static final String dumCId = "ABCD";
	public static final String dumPId = "P1001";
	public static final long startSentMsgId = 100001;
	public static final long startRecvMsgId = 10001;
	
	public InitDatabase() {
		Connection connection = null;
		Statement stmt = null;

		if (GUIDB.trial) new ScheduleGenerator();
		if (GUIDB.trial) new ScheduleSender();		
		
		try{
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt= connection.createStatement();
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB + ";");
			stmt.close();
			connection.close();
		}
		catch (SQLException se) {
			GUIDB.log("Exception Caught: Cannot establish a connection to database.");
			se.printStackTrace();
		}catch (Exception e){
			GUIDB.log("Exception Caught: Failed to load MySQL driver.");
			e.printStackTrace();
		}
		finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se){
			GUIDB.log("Exception Caught: Query failed to execute finally.");
			se.printStackTrace();
			}
			try{
				if(connection!=null)
					connection.close();
			}catch(SQLException se){
			GUIDB.log("Exception Caught: Cannot establish a connection to database finally.");
			se.printStackTrace();
			}
		}
	}
	
	public void createTables(){
		Connection connection = null;
		Statement stmt = null;

		try{
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL+DB, USER, PASS);
			stmt= connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS message_db(message_id VARCHAR(5) NOT NULL, PRIMARY KEY (message_id), message_category VARCHAR(20) NOT NULL, message_contents VARCHAR(150), status VARCHAR(1) NOT NULL)ENGINE=InnoDB;");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS contacts_db(contact_id VARCHAR(5) NOT NULL, PRIMARY KEY (contact_id), first_name VARCHAR(20) NOT NULL, last_name VARCHAR(20) NOT NULL, gender VARCHAR(1), primary_phone VARCHAR(15) NOT NULL UNIQUE, smart VARCHAR(2) NOT NULL, home_address VARCHAR(30) NOT NULL, home_city VARCHAR(20) NOT NULL, home_state VARCHAR(20) NOT NULL, home_zip VARCHAR(7) NOT NULL, status VARCHAR(1) NOT NULL)ENGINE=InnoDB;");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS prescription_db(prescription_id VARCHAR(6) NOT NULL, PRIMARY KEY (prescription_id), contact_id VARCHAR(5) NOT NULL, FOREIGN KEY(contact_id) REFERENCES contacts_db(contact_id) ON DELETE CASCADE ON UPDATE CASCADE, generic_name VARCHAR(30) NOT NULL, trade_name VARCHAR(30) NOT NULL, purpose VARCHAR(20) NOT NULL, preference VARCHAR(10) NOT NULL, dosage VARCHAR(15), hours VARCHAR(3), minutes VARCHAR(3), frequency VARCHAR(20), status VARCHAR(1) NOT NULL)ENGINE=InnoDB;");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sent_message_log(sent_message_id INT UNSIGNED NOT NULL, PRIMARY KEY(sent_message_id), contact_id VARCHAR(5) NOT NULL, FOREIGN KEY(contact_id) REFERENCES contacts_db(contact_id) ON DELETE CASCADE ON UPDATE CASCADE, message_id VARCHAR(5), FOREIGN KEY (message_id) REFERENCES message_db(message_id) ON DELETE CASCADE ON UPDATE CASCADE, message_category VARCHAR(20) NOT NULL, message_contents VARCHAR(150) NOT NULL,prescription_id VARCHAR(6), FOREIGN KEY(prescription_id) REFERENCES prescription_db(prescription_id) ON DELETE CASCADE ON UPDATE CASCADE, recipient_number VARCHAR(15) NOT NULL, timestamp DATETIME, status VARCHAR(10), msg_delivered DATETIME, msg_read DATETIME, pill_taken VARCHAR(20), reminder VARCHAR(20))ENGINE=InnoDB;");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS received_message_log(received_message_id INT UNSIGNED NOT NULL, PRIMARY KEY(received_message_id), contact_id VARCHAR(5) NOT NULL, FOREIGN KEY(contact_id) REFERENCES contacts_db(contact_id) ON DELETE CASCADE ON UPDATE CASCADE, msg_received DATETIME, message_contents VARCHAR(150))ENGINE=InnoDB;");
			stmt.executeUpdate("INSERT INTO contacts_db(contact_id, first_name, last_name, gender, primary_phone, smart, home_address, home_city, home_state, home_zip, status) SELECT * FROM (SELECT '" + dumCId + "', 'Dummy', 'DumDum', 'X', '1234567890', '-', 'Some Street', 'Some City', 'Some State', '99999', 'A') AS temp WHERE NOT EXISTS (SELECT * FROM contacts_db WHERE contact_id = '" + dumCId + "') LIMIT 1;");
			stmt.executeUpdate("INSERT INTO message_db(message_id, message_category, message_contents, status) SELECT * FROM (SELECT '" + dumMId + "','Pill Reminder', 'Sample Pill Reminder Message', 'A') AS temp WHERE NOT EXISTS (SELECT * FROM message_db WHERE message_id = '" + dumMId + "') LIMIT 1;");
			stmt.executeUpdate("INSERT INTO prescription_db(prescription_id, contact_id, generic_name, trade_name, purpose, preference, dosage, hours, minutes, frequency, status) SELECT * FROM (SELECT '" + dumPId + "', '" + dumCId + "','Generic', 'Trade', 'Purpose', 'No Pref', '1', '--', '00', 'Undefined', 'A') AS temp WHERE NOT EXISTS (SELECT * FROM prescription_db WHERE prescription_id = '" + dumPId + "') LIMIT 1;");
			stmt.close();
			connection.close();
		}
		catch (SQLException se) {
			GUIDB.log("Exception Caught: Error in SQL table creation syntax.");
			se.printStackTrace();
		}catch (Exception e){
			GUIDB.log("Exception Caught: Failed to load MySQL driver.");
			e.printStackTrace();
		}
		finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se){
			GUIDB.log("Exception Caught: Query failed to execute finally.");
			se.printStackTrace();
			}
			try{
				if(connection!=null)
					connection.close();
			}catch(SQLException se){
			GUIDB.log("Exception Caught: Cannot establish a connection to database finally.");
			se.printStackTrace();
			}
		}
	}

}
