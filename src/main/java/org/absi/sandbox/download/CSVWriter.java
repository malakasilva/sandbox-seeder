package org.absi.sandbox.download;

import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObjectWrapper;

public class CSVWriter {

	private static final Logger LOGGER = Logger.getLogger(CSVWriter.class.getName());
	private static final String NEWLINE_CHAR = "\n";
	public static final String SPLITTER_CHAR = ",";
	public static final String QUOTE = "\"";
	
	public void writeFile(String sObjectType, SObject[]sObjects, Field[]fields, String filePath){

		StringBuilder sb = new StringBuilder();
			
		File file = new File(filePath + sObjectType + ".csv");
		
		if (!(file.exists() && file.isFile())) {
			sb.append(QUOTE + "Id" + QUOTE);
			for (Field field : fields) {
				
				if (field.getType().toString().equals("id")) {
					continue;
				}			
				
				if (field.getType().toString().equals("string") 
						|| field.getType().toString().equals("email")
						|| field.getType().toString().equals("url")
						|| field.getType().toString().equals("textarea")
						|| field.getType().toString().equals("phone")
						|| field.getType().toString().equals("reference")
						|| field.getType().toString().equals("picklist")
						|| field.getType().toString().equals("address")
						|| field.getType().toString().equals("date")
						|| field.getType().toString().equals("datetime")
						|| field.getType().toString().equals("double")
						|| field.getType().toString().equals("boolean")
						|| field.getType().toString().equals("int")
						|| field.getType().toString().equals("percent")
						|| field.getType().toString().equals("multipicklist")
						|| field.getType().toString().equals("encryptedstring")
						|| field.getType().toString().equals("currency")) {   
					sb.append(SPLITTER_CHAR + QUOTE + field.getName() + QUOTE);
				} else {
					LOGGER.log(Level.WARNING, "Field type not supported when inserting data. Field Type : " + field.getType().toString());
					continue;
				}
			}		
			sb.append(NEWLINE_CHAR);
		} 
		
		for (SObject sObject:sObjects) {
			sb.append(QUOTE + sObject.getId() + QUOTE);
			for (Field field : fields) {
				
				if (field.getType().toString().equals("id")) {
					continue;
				}			
				
				if (field.getType().toString().equals("string") 
						|| field.getType().toString().equals("email")
						|| field.getType().toString().equals("url")
						|| field.getType().toString().equals("textarea")
						|| field.getType().toString().equals("phone")
						|| field.getType().toString().equals("reference")
						|| field.getType().toString().equals("picklist")
						|| field.getType().toString().equals("multipicklist")
						|| field.getType().toString().equals("encryptedstring")
						|| field.getType().toString().equals("address")) {
					sb.append(SPLITTER_CHAR + QUOTE + getString(sObject.getField(field.getName())) + QUOTE);
				} else if (field.getType().toString().equals("date")) {
					sb.append(SPLITTER_CHAR + QUOTE + getDate(sObject.getField(field.getName())) + QUOTE);
				} else if (field.getType().toString().equals("datetime")) {
					sb.append(SPLITTER_CHAR + QUOTE + getDateTime(sObject.getField(field.getName())) + QUOTE);
				} else if (field.getType().toString().equals("double") 
						|| field.getType().toString().equals("currency")
						|| field.getType().toString().equals("percent")) { 
					sb.append(SPLITTER_CHAR + QUOTE + getDouble(sObject.getField(field.getName())) + QUOTE);
				} else if (field.getType().toString().equals("int")) {   
					sb.append(SPLITTER_CHAR + QUOTE + getInt(sObject.getField(field.getName())) + QUOTE);		
				} else if (field.getType().toString().equals("boolean")) {
					sb.append(SPLITTER_CHAR + QUOTE + getBoolean(sObject.getField(field.getName())) + QUOTE);				
				} else {
					LOGGER.log(Level.WARNING, "Field type not supported when inserting data. Field Type : " + field.getType().toString());
				}
			}
			sb.append(NEWLINE_CHAR);
		}
		
		try{
			
			FileWriter fileWriter = new FileWriter(file, true);
			fileWriter.write(sb.toString());
			fileWriter.flush();
		}catch (Exception e) {
			// TODO close the streams
			e.printStackTrace();
		} finally {
			// TODO close the streams
		}	
	}
	
	private Date getDate(Object strObject) {
		if (strObject == null) {
			return null;
		}
		java.util.Date inputDate = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			inputDate = dateFormat.parse(strObject.toString());
		} catch (ParseException e) {
			LOGGER.log(Level.WARNING, "Error parsing Date for string " + strObject, e);
			return null;
		}
		return new Date(inputDate.getTime());
	}
	private Timestamp getDateTime(Object strObject) {
		if (strObject == null) {
			return null;
		}
		java.util.Date inputDate = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
			inputDate = dateFormat.parse(strObject.toString());
		} catch (ParseException e) {
			LOGGER.log(Level.WARNING, "Error parsing Timestamp for string " + strObject, e);
			return null;
		}
		return new Timestamp(inputDate.getTime());
	}	
	private Boolean getBoolean(Object strObject) {
		if (strObject == null) {
			return null;
		}
		return Boolean.valueOf(strObject.toString()); 
	}
	private int getInt(Object strObject) {
		if (strObject == null) {
			return 0;
		}
		return Integer.valueOf(strObject.toString()); 
	}	
	private Double getDouble(Object strObject) {
		if (strObject == null) {
			return 0d;
		}
		return Double.valueOf(strObject.toString()); 
	}	
	private String getString(Object strObject) {
		if (strObject instanceof XmlObjectWrapper) {
			return null;
		} 
		if (strObject == null) {
			return null;
		}
		return (strObject.toString()).replaceAll(NEWLINE_CHAR, " ").replaceAll(QUOTE, "'");
	}

	

}
