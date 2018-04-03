package org.absi.heroku;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceAPI {

    ConnectorConfig config;
    PartnerConnection connection;
    
	public SalesforceAPI(String userName, String password) throws ConnectionException {
		config = new ConnectorConfig();
	    config.setUsername(userName);
	    config.setPassword(password);
       	connection = Connector.newConnection(config);         
	}
	
	public DescribeSObjectResult describeSobject(String accountName) throws ConnectionException {
		return connection.describeSObject(accountName);
	}

	public QueryResult query(String sSobjectType, Field[] fields) throws ConnectionException {
		return connection.query(getSOQL(sSobjectType, fields));
	}
	
	public QueryResult queryMore(String queryLocator) throws ConnectionException {
		return connection.queryMore(queryLocator);
	}
	
	private String getSOQL(String sSobjectType, Field[] fields) {
		StringBuilder sb = new StringBuilder();
		sb.append("select  ");
		boolean firstField = true;
		for (Field field : fields) {
			if (!firstField) {
				sb.append(",");
			}
			firstField = false;
			sb.append(field.getName());
		}
		sb.append(" from " + sSobjectType);
		sb.append(" order by LastModifiedDate asc");
		return sb.toString();
	}
}
