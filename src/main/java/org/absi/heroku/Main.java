package org.absi.heroku;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.absi.sandbox.download.DownloadException;
import org.absi.sandbox.download.DownloaderMain;
import org.absi.sandbox.seed.CSVReader;


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
		} catch (DownloadException e) {
			LOGGER.log(Level.SEVERE, "Error while setting up", e);
			return;
		}
		for(String sObjectType:sObjects.split(",")) {
			try {
				downloaderMain.generateCSV(sObjectType, "c:/work/tmp/");
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
		try {
			//Can only handle one sobject type for a run
			String sObjectType = "Account";
			List<String>lIds = new ArrayList<String>();
			lIds.add("00128000009h2S1AAI");
			csvReader.seed(sObjectType, lIds, "c:/work/tmp/");	
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while seeding ", e);
			return;
		}		
	}
	
}
