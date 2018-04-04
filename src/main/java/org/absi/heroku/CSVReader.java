package org.absi.heroku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.absi.sandbox.fill.ChildFinder;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class CSVReader {

	ChildFinder childFinder;
	
	public CSVReader(String username, String password, String securityToken) throws SyncException {
		try {
			childFinder = new ChildFinder(username, password + securityToken);
		} catch (ConnectionException e) {
			throw new SyncException("Error while connecting to Salesforce.", e);
		}
	}
	
	public void createSobjects(String sSobjectType, List<String> lIds) {
		List<SObject> lSObjects = getSobjects(sSobjectType, lIds);
		childFinder.createObjects(lSObjects.toArray(new SObject[0]));
	}
	
	private List<SObject> getSobjects(String sSobjectType, List<String> lIds) {
		File file = new File(CSVWriter.FILE_PATH + sSobjectType + ".csv");
		List<SObject> lSobjects = new ArrayList<SObject>();
		
		Map<String, Field>mFields = childFinder.getFields(sSobjectType);
		
		try {
			if (file.exists() && file.isFile()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String sLine = null;
				String[]columns = null;
				while((sLine = br.readLine()) != null){
					//Get the header raw first
					if (columns == null) {
						columns = sLine.split(CSVWriter.SPLITTER_CHAR);
						for(int i = 0;i < columns.length;i++){
							columns[i] = getData(columns[i]);
						}
					} else {
						String [] raws = sLine.split(CSVWriter.SPLITTER_CHAR);
						SObject sObject = new SObject();
						sObject.setType(sSobjectType);
						int i = 0;
						for (String column:columns) {	
							

							
							Field field = mFields.get(column);					
							String raw = raws[i++];							
							while ((raw.lastIndexOf(CSVWriter.QUOTE) + 1) != raw.length()) {
								raw += raws[i++];
							}							
							raw = getData(raw);
							
							Object oFieldValue = null;
							System.out.println(column + " - " + raw);

							if (column.equals("CreatedById") || column.equals("PhotoUrl") || column.equals("IsDeleted") || column.equals("LastModifiedById")) {
								continue;
							}							
							
							if (raw == null || raw.equals("null")) {
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
								oFieldValue = raw;
							} else if (field.getType().toString().equals("date")) {
								//oFieldValue = new Date(raw);
							} else if (field.getType().toString().equals("datetime")) {
								//oFieldValue = raw;
							} else if (field.getType().toString().equals("double") 
									|| field.getType().toString().equals("currency")
									|| field.getType().toString().equals("percent")) { 
								oFieldValue = new Double(raw);
							} else if (field.getType().toString().equals("int")) {   
								oFieldValue = new Integer(raw);		
							} else if (field.getType().toString().equals("boolean")) {
								oFieldValue = new Boolean(raw);		
							}							
							sObject.addField(column, oFieldValue);
						}
						lSobjects.add(sObject);
					}					
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lSobjects;
	}

	private String getData(String sRaw){
		if (sRaw != null) {
			sRaw = sRaw.substring(1);
			sRaw = sRaw.substring(0,(sRaw.length() - 1));
		}
		return sRaw;
	}
	
}
