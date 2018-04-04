package org.absi.heroku;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws Exception {

		LOGGER.info("Starting the sync job.");
		
		/*String userName = System.getenv("SALESFORCE_USERNAME");
		String password = System.getenv("SALESFORCE_PASSWORD");
		String securityToken = System.getenv("SALESFORCE_SECURITYTOKEN");
		String sObjects = System.getenv("SOBJECTS");*/

		String userName = "malaka@wso2.com";
		String password = "METs1@met";
		String securityToken = "pr8IwOxxe6ICixa0w0ihOD7mZ";
		String sObjects = "Account";		
				
		/*DownloaderMain downloaderMain;
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
		}*/
		
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
				lIds.add("00128000009h2RvAAI");
				csvReader.createSobjects("Account", lIds);	
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error while sync " + sObjectType, e);
			}	
		}
		LOGGER.info("Completed the sync job sucessfully.");
	}
}
