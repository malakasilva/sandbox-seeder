package org.absi.heroku;

import java.util.List;

import com.sforce.soap.partner.sobject.SObject;

public class SobjectDTO {

	private List<SObject> lSobjects;
	private List<String> lIds;
	
	public SobjectDTO(List<SObject> lSobjects, List<String> lIds) {
		this.lSobjects = lSobjects;
		this.lIds = lIds;
	}
	
	public List<SObject> getlSobjects() {
		return lSobjects;
	}
	public void setlSobjects(List<SObject> lSobjects) {
		this.lSobjects = lSobjects;
	}
	public List<String> getlIds() {
		return lIds;
	}
	public void setlIds(List<String> lIds) {
		this.lIds = lIds;
	}
	
	
	
}
