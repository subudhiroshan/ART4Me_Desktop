package com.data;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class is a JTable MOdel for the Active Prescriptions list.
 */
public class JTableModelPresA {

	static Vector<Vector<String>> data=new Vector<Vector<String>>();
	static Vector<String> headers=new Vector<String>();
	static Vector<String> types=new Vector<String>();
	public static TableModel model;
	public static JTable table;
	static int col;
	
	public static JScrollPane jTableModel() {
		
		model = new DefaultTableModel(data, headers) {
			private static final long serialVersionUID = 1L;
				@Override
				public boolean isCellEditable(int row, int col){
					return false;
				}
	           };
		
	           Comparator<Object> objComp = new Comparator<Object>() {
	               public int compare(Object o1, Object o2) {
	                   return ((String) o1).compareTo((String) o2);
	               }
	           };
	           
		table=new JTable(model);
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
			      if (e.getClickCount() == 2) {
			    	 JOptionPane.showMessageDialog(null, "You cannot edit the values directly. Use the buttons below.");  
			      }
			}
		});
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		sorter.setComparator(0, objComp);
	    table.setRowSorter(sorter);
		table.setAutoCreateRowSorter(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setCellSelectionEnabled(true);
		
		col=0;
		int width;
		Enumeration<String> enumHeaders = types.elements();
		while (enumHeaders.hasMoreElements()){
			String colType = enumHeaders.nextElement();
			if (colType.equalsIgnoreCase("datetime")){
				width = 20;
			}
			else{
				int size = Integer.parseInt(colType.substring(colType.indexOf("(") +1, colType.indexOf(")")));
				if (size <5){
					width = 10;
				}else if (size > 40){
					width = 40;
				}else{
					width = size;
				}
			}
			table.getColumnModel().getColumn(col).setPreferredWidth(width*11);
			col++;
		}
		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		return scrollTable;
	}

	public static void retrieveHeaders(String table) throws ClassNotFoundException
	{
		Connection connection;
		ResultSet rsHeaders;
		Statement stmt;
		try {
			Class.forName(InitDatabase.JDBC_DRIVER);
			connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmt= connection.createStatement();
			rsHeaders = stmt.executeQuery("show columns from " + table);
			headers.clear();
			types.clear();
			while (rsHeaders.next()){
				if (!rsHeaders.getString("Field").equalsIgnoreCase("status")){
					headers.add(PrescriptionTab.presHeaders.findByKey(rsHeaders.getString("Field")));
					types.add(rsHeaders.getString("Type"));
				}
			}
			rsHeaders.close();
		connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void retrieveData(String table) throws ClassNotFoundException
	{
		Connection connection;
		ResultSet rsData;
		Statement stmt;
		try {
			Class.forName(InitDatabase.JDBC_DRIVER);
			connection=DriverManager.getConnection(InitDatabase.DB_URL+InitDatabase.DB, InitDatabase.USER, InitDatabase.PASS);
			stmt= connection.createStatement();
			rsData= stmt.executeQuery("select prescription_id, contact_id, generic_name, trade_name, purpose, preference, dosage, hours, minutes, frequency from " + table + " where status='A';");
			ArrayList<String> presKeys = (ArrayList<String>) PrescriptionTab.presHeaders.getKeys();
			data.clear();
			while(rsData.next()){
				Vector <String> rowData=new Vector<String>();
				for (int i =0; i< presKeys.size(); i++){
					rowData.add(rsData.getString(presKeys.get(i)));
				}
			data.add(rowData);
			}
		rsData.close();
		connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
