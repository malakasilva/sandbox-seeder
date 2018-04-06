package org.absi.heroku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.absi.sandbox.fill.ChildFinder;

import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.SaveResult;
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
	
	public ChildDetail createSobjects(String sSobjectType, List<String> lIds, String parentColumn, Map<String,String>lIdsSource) {
		SobjectDTO sobjectDTO = getSobjects(sSobjectType, lIds, parentColumn, lIdsSource);
		if (sobjectDTO == null) {
			return null;
		}
		List<SObject> lSObjects = sobjectDTO.getlSobjects();
		if (lSObjects == null || lSObjects.isEmpty()) {
			return null;
		}
		Map<String, String>mChildred = childFinder.getChildren(sSobjectType);
		List<String>parentIds = new ArrayList<String>();
		SaveResult[]saveResults = childFinder.createObjects(lSObjects.toArray(new SObject[0]));		
		
		Map<String,String>sourceIds = new HashMap<String,String>();
		int i = 0;
		for (SaveResult saveResult:saveResults) {
			if (saveResult.isSuccess()) {
				parentIds.add(saveResult.getId());
				sourceIds.put(sobjectDTO.getlIds().get(i), saveResult.getId());
			}
			i++;
		}		
		return new ChildDetail(mChildred, parentIds, sourceIds);
	}
	
	private SobjectDTO getSobjects(String sSobjectType, List<String> lIds, String parentColumn, Map<String,String>sourceIds) {
		File file = new File(CSVWriter.FILE_PATH + sSobjectType + ".csv");
		List<SObject> lSobjects = new ArrayList<SObject>();
		List<String> Ids = new ArrayList<String>();
		
		Map<String, Field>mFields = childFinder.getFields(sSobjectType);
		
		Integer idColumn = null;
		
		if (lIds != null) {
			idColumn = 0;
		}
		
		if (sSobjectType.equals("Contact")) {
			System.out.println("ok");
		}
		
		try {
			if (file.exists() && file.isFile()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String sLine = null;
				String[]columns = null;
				whileLoop:
				while((sLine = br.readLine()) != null){
					//Get the header raw first
					if (columns == null) {
						columns = sLine.split(CSVWriter.SPLITTER_CHAR);						
						for(int i = 0;i < columns.length;i++){
							columns[i] = getData(columns[i]);
							if (parentColumn != null && columns[i].equals(parentColumn)) {
								idColumn = i;
							}
						}
					} else {
						String [] raws = sLine.split(CSVWriter.SPLITTER_CHAR);
						
						SObject sObject = new SObject();
						sObject.setType(sSobjectType);
						int i = 0;
						int j = 0;
						forloop:
						for (String column:columns) {	
													
							Field field = mFields.get(column);							
							String raw = raws[i++];							
							while ((raw.lastIndexOf(CSVWriter.QUOTE) + 1) != raw.length()) {
								raw += raws[i++];
							}							
							raw = getData(raw);
							j++;
							if (raw.equals("00328000008T8imAAC")) {
								System.out.println("fdf");
							}
							
							

							

							if (idColumn != null && idColumn.equals(0) && idColumn.equals((j-1)) && lIds != null && !lIds.contains(raw)) {
								continue whileLoop;
							}
											
							if (idColumn != null && idColumn.equals((j-1)) && sourceIds != null && !sourceIds.keySet().contains(raw)) {
								continue whileLoop;
							} else if (sourceIds != null && sourceIds.keySet().contains(raw)) {
								raw = sourceIds.get(raw);
							}					
														
							Object oFieldValue = null;

							if (column.equals("CreatedById") || column.equals("PhotoUrl") || column.equals("IsDeleted") || column.equals("LastModifiedById")) {
								continue forloop;
							}							
			
							if (sSobjectType.equals("Contact") && (column.equals("IsEmailBounced") || column.equals("Name") )) {
								continue forloop;
							}
							
							 
							
							if (raw == null || raw.equals("null")) {
								continue forloop;
							}
							
							if (j==1) {
								Ids.add(raw);	
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
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new SobjectDTO(lSobjects, Ids);
	}

	private String getData(String sRaw){
		if (sRaw != null) {
			sRaw = sRaw.substring(1);
			sRaw = sRaw.substring(0,(sRaw.length() - 1));
		}
		return sRaw;
	}
	
}
