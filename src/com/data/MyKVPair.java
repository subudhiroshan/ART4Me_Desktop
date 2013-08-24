package com.data;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class creates a Java Collection which is a 2-way hash on both the keys and values.
 */
public class MyKVPair{
	
	private ArrayList<String> keys ;
	private ArrayList<String> values;
	public MyKVPair()
	{
		keys = new ArrayList<String>(10);
		values = new ArrayList<String>(10);
	}
	public synchronized void put(String key , String value)
	{
		if (key == null || value ==null ){
			throw new NullPointerException("Null key or value is not allowed");
		}

		if ((keys.indexOf(key)) !=-1){
//			GUIDB.log("Key " + key + " already taken!");
		}
		keys.add(key);
		values.add(value);
	}

	public String findByKey(String key){
		int index = keys.indexOf(key);
		return index==-1?null : values.get(index);
	}

	public String findByValue(String value){
		int index = values.indexOf(value);
		return index==-1?null : keys.get(index);
	}

	public Collection<String> getKeys(){
		return new ArrayList<String>(keys);
	}

	public Collection<String> getValues(){
		return new ArrayList<String>(values);
	}
}