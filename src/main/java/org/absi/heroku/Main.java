package org.absi.heroku;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	static String userName = "malaka@wso2.com";
	static String password = "METs1@met";
	static String securityToken = "pr8IwOxxe6ICixa0w0ihOD7mZ";
	static String sObjects = "Account,contact";	
	
	public static void main(String[] args) throws Exception {

		LOGGER.info("Starting the sync job.");
		
		/*String userName = System.getenv("SALESFORCE_USERNAME");
Enrico 		String password = System.getenv("SALESFORCE_PASSWORD");
		String securityToken = System.getenv("SALESFORCE_SECURITYTOKEN");
		String sObjects = System.getenv("SOBJECTS");*/

	
				
		//write();
		read();
		LOGGER.info("Completed the sync job sucessfully.");
	}

	private static void write() {
		DownloaderMain downloaderMain;
		try {
			downloaderMain = new DownloaderMain(userName, password, securityToken);
		} catch (SyncException e) {
			LOGGER.log(Level.SEVERE, "Error while setting up", e);
			return;
		}
		for(String sObjectType:sObjects.split(",")) {
			try {
				downloaderMain.generateCSV(sObjectType);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error while sync " + sObjectType, e);
			}	
		}		
	}
	
	private static void read() {
		CSVReader csvReader;
		try {
			csvReader = new CSVReader(userName, password, securityToken);			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while setting up", e);
			return;
		}		
		
		for(String sObjectType:sObjects.split(",")) {
			try {
				LOGGER.info("Start Syncing object " + sObjectType);
				List<String>lIds = new ArrayList<String>();
				lIds.add("00128000009h2S1AAI");
				List<ChildDetail>lChildDetails = new ArrayList<ChildDetail>();
				lChildDetails.add(csvReader.createSobjects("Account", lIds, null, null));
				while (!lChildDetails.isEmpty()) {
					List<ChildDetail>lChildDetailsTmp = new ArrayList<ChildDetail>();
					for (ChildDetail childDetail:lChildDetails) {
						Map<String, String> mChildDetails = childDetail.getmChildred();
						for (String sObjectType1:mChildDetails.keySet()) {
							LOGGER.info("Start Syncing object " + sObjectType1);
							ChildDetail childDetai2 = csvReader.createSobjects(sObjectType1, childDetail.getParentIds(), mChildDetails.get(sObjectType1), childDetail.getSourceIds());
							if (childDetai2 != null) {
								lChildDetailsTmp.add(childDetai2);
							}
						}
						
					}
					lChildDetails = lChildDetailsTmp;
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error while sync " + sObjectType, e);
			}	
		}		
	}
	
}
