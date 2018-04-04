package org.absi.sandbox.fill;

import java.util.HashMap;
import java.util.Map;

import com.sforce.soap.partner.ChildRelationship;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class ChildFinder {
    ConnectorConfig config;
    PartnerConnection connection;
    
	public ChildFinder(String userName, String password) throws ConnectionException {
		config = new ConnectorConfig();
	    config.setUsername(userName);
	    config.setPassword(password);
       	connection = Connector.newConnection(config);
	}

	
	public Map<String, String> getChildren(String sObjectType) {
		Map<String, String>mChildren = new HashMap<String, String>();
		try {
			DescribeSObjectResult describeSObjectResult = connection.describeSObject(sObjectType);
			ChildRelationship [] childRelationships = describeSObjectResult.getChildRelationships();
			for (ChildRelationship childRelationship:childRelationships) {
				if (childRelationship.getDeprecatedAndHidden()){
					mChildren.put(childRelationship.getChildSObject(), childRelationship.getField());
				}
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mChildren;
	}
	
	public Map<String, Field> getFields(String sObjectType) {
		Map<String, Field>mFields = new HashMap<String, Field>();
		try {
			DescribeSObjectResult describeSObjectResult = connection.describeSObject(sObjectType);
			for (Field field:describeSObjectResult.getFields()) {
				mFields.put(field.getName(), field);
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mFields;
	}

	public void createObjects(SObject[]sObjects){
		try {
			SaveResult[]saveResults = connection.create(sObjects);
			for (SaveResult saveResult:saveResults) {
				System.out.println(saveResult.getSuccess() + saveResult.toString());
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
