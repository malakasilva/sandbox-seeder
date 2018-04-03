package org.absi.heroku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sforce.soap.partner.sobject.SObject;

public class CSVReader {

	public List<SObject> getSobjects(String sSobjectType, List<String> lIds) {
		File file = new File(CSVWriter.FILE_PATH + sSobjectType + ".csv");
		List<SObject> lSobjects = new ArrayList<SObject>();
		try {
			if (file.exists() && file.isFile()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String sLine = null;
				String[]columns = null;
				while((sLine = br.readLine()) != null){
					if (columns == null) {
						columns = sLine.split(CSVWriter.SPLITTER_CHAR);
					} else {
						String [] raw = sLine.split(CSVWriter.SPLITTER_CHAR);
						SObject sObject = new SObject();
						int i = 0;
						for (String column:columns) {
							sObject.addField(column, raw[i++]);
					
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

}
