package org.absi.sandbox.seed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.absi.sandbox.download.CSVWriter;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class CSVReader {

	private SalesforceUtil salesforceUtil;
	private static final Logger LOGGER = Logger.getLogger(CSVReader.class.getName());
	
	/**
	 * 
	 * Make the salesforce connection and prepare
	 * 
	 * @param username
	 * @param password
	 * @param securityToken
	 * @throws SeedException
	 */
	public CSVReader(String username, String password, String securityToken) throws SeedException {
		try {
			salesforceUtil = new SalesforceUtil(username, password + securityToken);
		} catch (ConnectionException e) {
			throw new SeedException("Error while connecting to Target Salesforce.", e);
		}
	}
	
	/**
	 * 
	 * Start uploading data from CSV to target salesforce org.
	 * Start with parent and move to children
	 * 
	 * @param sObjectType
	 * @param lIds
	 * @throws SeedException
	 */
	public void seed(String sObjectType, List<String> lIds, String filePath)  throws SeedException {
		LOGGER.info("Start seeding root object " + sObjectType);
		try {
			//Store child details to process later
			List<ChildDetails> lChildDetails = new ArrayList<ChildDetails>();
			//Add parent object to the target org
			lChildDetails.add(createSobjects(sObjectType, lIds, null, null, filePath));
			
			//Process children until it reach the lowest level
			while (!lChildDetails.isEmpty()) {
				LOGGER.info(" <-- Nexted level started from here --> ");
				//Store temporary children of current processing child objects
				List<ChildDetails> lChildDetailsTmp = new ArrayList<ChildDetails>();
				for (ChildDetails childDetail : lChildDetails) {
					Map<String, String> mChildDetails = childDetail.getmChildred();
					for (String childObjectType : mChildDetails.keySet()) {
						LOGGER.info("Start seeding chile object " + childObjectType + " For parent " + childDetail.getParentSobjectType());
						ChildDetails grandChildDetails = createSobjects(childObjectType, childDetail.getParentIds(),
								mChildDetails.get(childObjectType), childDetail.getSourceIds(), filePath);
						if (grandChildDetails != null) {
							lChildDetailsTmp.add(grandChildDetails);
						}
					}
				}
				lChildDetails = lChildDetailsTmp;
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while seeding " + sObjectType, e);
			throw new SeedException("Error while seeding ", e);
		}
	}
	
	/**
	 * 
	 * Insert the given sobjects and find available child objects
	 * 
	 * @param sSobjectType
	 * @param lIdsSource - Source ids in the archived to be filtered
	 * @param parentColumn - When inserting children filter records based on parent column
	 * @param lIdsTarget - Map of achived ids to target ids to maintain the relashahonships in target org
	 * @return
	 */
	private ChildDetails createSobjects(String sSobjectType, List<String> lIdsSource, String parentColumn, Map<String,String>lIdsTarget, String filePath) {
		List<String>parentIds = new ArrayList<String>();
		Map<String,String>sourceIds = new HashMap<String,String>();
		SobjectDetails sobjectDTO = getSobjects(sSobjectType, lIdsSource, parentColumn, lIdsTarget, filePath);
		if (sobjectDTO == null) {
			return null;
		}
		List<SObject> lSObjects = sobjectDTO.getlSobjects();
		if (lSObjects == null || lSObjects.isEmpty()) {
			return null;
		}
		//Retive the child sobjects
		Map<String, String>mChildred = salesforceUtil.getChildren(sSobjectType);
		//Insert data to salesforce
		SaveResult[]saveResults = salesforceUtil.createObjects(lSObjects.toArray(new SObject[0]));		
		
		int i = 0;
		for (SaveResult saveResult:saveResults) {
			if (saveResult.isSuccess()) {
				parentIds.add(saveResult.getId());
				sourceIds.put(sobjectDTO.getlIds().get(i), saveResult.getId());
			}
			i++;
		}		
		return new ChildDetails(mChildred, parentIds, sourceIds, sSobjectType);
	}
	
	/**
	 * 
	 * Find the sobject archive in the given location and get Sobjects to be inserted with source ids
	 * 
	 * @param sSobjectType
	 * @param lIdsSource - Source ids in the archived to be filtered
	 * @param parentColumn - When inserting children filter records based on parent column
	 * @param lIdsTarget - Map of achived ids to target ids to maintain the relashahonships in target org
	 * @return
	 */
	private SobjectDetails getSobjects(String sSobjectType, List<String> lIds, String parentColumn, Map<String,String>lIdsTarget, String filePath) {
		File file = new File(filePath + sSobjectType + ".csv");
		List<SObject> lSobjects = new ArrayList<SObject>();
		List<String> Ids = new ArrayList<String>();
		
		Map<String, Field>mFields = salesforceUtil.getFields(sSobjectType);
		
		//Null if filtering is not needed and take all the records
		Integer idColumn = null;
		//Filter records based on object id of archive
		if (lIds != null) {
			idColumn = 0;
		}
			
		BufferedReader br = null;
		try {
			//First check if the achive is available
			if (file.exists() && file.isFile()) {
				br = new BufferedReader(new FileReader(file));
				String sLine = null;
				String[]columns = null;
				whileLoop:
				while((sLine = br.readLine()) != null){
					//Get the header raw first
					if (columns == null) {
						columns = sLine.split(CSVWriter.SPLITTER_CHAR);						
						for(int i = 0;i < columns.length;i++){
							//remote " from data
							columns[i] = getData(columns[i]);
							//If filtering only needs to done based on parent column of the child set the index
							if (parentColumn != null && columns[i].equals(parentColumn)) {
								idColumn = i;
							}
						}
					} else {
						//Start processing data line by line
						String [] raws = sLine.split(CSVWriter.SPLITTER_CHAR);
						
						SObject sObject = new SObject();
						sObject.setType(sSobjectType);
						int i = 0,j = -1;
						
						//Read Field by field and update sobject
						forloop:
						for (String column:columns) {														
							Field field = mFields.get(column);							
							String raw = raws[i++];		
							//Sometimes there can be data with commas. Need to check that cases manually
							while ((raw.lastIndexOf(CSVWriter.QUOTE) + 1) != raw.length()) {
								raw += raws[i++];
							}							
							raw = getData(raw);
							j++;

							//Check the filter based on source ids
							if (idColumn != null && idColumn.equals(0) && idColumn.equals(j) && lIds != null && !lIds.contains(raw)) {
								continue whileLoop;
							}
																		
							if (idColumn != null && idColumn.equals(j) && lIdsTarget != null && !lIdsTarget.keySet().contains(raw)) {
								//Check the filter based on parent ids
								continue whileLoop;
							} else if (lIdsTarget != null && lIdsTarget.keySet().contains(raw)) {
								//If on the correct column need to get the parent id of target system for matching source archive.
								raw = lIdsTarget.get(raw);
							}					

							//Add the source (archive) ids for later reference
							if (j==0) {
								Ids.add(raw);	
							}								
							
							Object oFieldValue = null;
							//Ignore the fields with no create permission or diabled
							if (!field.getCreateable()) {
								continue forloop;
							}							
							 
							// Ignore the null fields
							if (raw == null || raw.equals("null")) {
								continue forloop;
							}
							
						
			
							//Handle different date types
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
								//oFieldValue = raw;
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
			LOGGER.log(Level.SEVERE, "Error file not found ", e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error reading file ", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {				}
			}
		}
		return new SobjectDetails(lSobjects, Ids);
	}

	/**
	 * Remove the surrpunding double quates
	 * eg "test" -> test
	 * 
	 * @param sRaw
	 * @return
	 */
	private String getData(String sRaw){
		if (sRaw != null) {
			sRaw = sRaw.substring(1);
			sRaw = sRaw.substring(0,(sRaw.length() - 1));
		}
		return sRaw;
	}
	
	private Date getDateTime(String sRaw) {
		//11/12/2015 6:10:06 AM
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
		try {
			return dateFormat.parse(sRaw);
		} catch (ParseException e) {
			LOGGER.log(Level.WARNING, "Error converting Date " + sRaw, e);
		}
		return null;	
	}

	private Date getDate(String sRaw) {
		//11/12/2015
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		try {
			return dateFormat.parse(sRaw);
		} catch (ParseException e) {
			LOGGER.log(Level.WARNING, "Error converting Date " + sRaw, e);
		}
		return null;		
	}
	
}

