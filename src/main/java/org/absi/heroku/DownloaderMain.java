package org.absi.heroku;



import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class DownloaderMain {

	private SalesforceAPI salesforceAPI;
	private CSVWriter csvWriter;
	
	public DownloaderMain(String username, String password, String securityToken) throws SyncException {
		try {
			salesforceAPI = new SalesforceAPI(username, password + securityToken);
		} catch (ConnectionException e) {
			throw new SyncException("Error while connecting to Salesforce.", e);
		}
		csvWriter = new CSVWriter();		
	}
	
	/**
	 * 
	 * Sync the object based on timestamp
	 * 
	 * @param sObjectType
	 * @throws SyncException
	 */
	public void generateCSV(String sObjectType) throws SyncException {
		try {			
			DescribeSObjectResult describeSObjectResult = salesforceAPI.describeSobject(sObjectType);
			
			QueryResult qr = salesforceAPI.query(sObjectType, describeSObjectResult.getFields());
			boolean queryMore = false;
			do {
				SObject[] sObjects = qr.getRecords();
				csvWriter.writeFile(sObjectType, sObjects, describeSObjectResult.getFields());
				if (!qr.isDone()) {
					queryMore = true;
					qr = salesforceAPI.queryMore(qr.getQueryLocator());
				} else {
					queryMore = false;
				}
			} while (queryMore);
			
		} catch (ConnectionException e) {
			throw new SyncException("Error while querying or updateing DB", e);
		}		
	}

}
