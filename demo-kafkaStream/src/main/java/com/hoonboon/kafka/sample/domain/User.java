package com.hoonboon.kafka.sample.domain;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.ReflectData;

public class User {
	
	final static Schema schema = ReflectData.get().getSchema(User.class);
	
	String username;
	@Nullable String dobStr;
	@Nullable String lastModifiedDateStr;
	
	public User(String username, String dobStr, String lastModifiedDateStr) {
		super();
		this.username = username;
		this.dobStr = dobStr;
		this.lastModifiedDateStr = lastModifiedDateStr;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDobStr() {
		return dobStr;
	}

	public void setDobStr(String dobStr) {
		this.dobStr = dobStr;
	}
	
	public String getLastModifiedDateStr() {
		return lastModifiedDateStr;
	}

	public void setLastModifiedDateStr(String lastModifiedDateStr) {
		this.lastModifiedDateStr = lastModifiedDateStr;
	}

	public static GenericRecord toGenericRecord(User user) {
		
		GenericRecord avroRecord = new GenericData.Record(schema);
		avroRecord.put("username", user.getUsername());
		avroRecord.put("dobStr", user.getDobStr());
		avroRecord.put("lastModifiedDateStr", user.getLastModifiedDateStr());
		
		return avroRecord;
	}
	
//	public static User fromGenericRecord(GenericRecord avroRecord) {
//		
//		User user = 
//				new User(
//						(String) avroRecord.get("username"), 
//						(String) avroRecord.get("dobStr"), 
//						(String) avroRecord.get("lastModifiedDateStr"));
//		
//		return user;
//	}

	@Override
	public String toString() {
		return "User [username=" + username + ", dobStr=" + dobStr + ", lastModifiedDateStr=" + lastModifiedDateStr
				+ "]";
	}
	
}
