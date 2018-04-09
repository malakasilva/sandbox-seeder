package org.absi.sandbox.seed;

import java.util.List;
import java.util.Map;

public class ChildDetails {

	private Map<String, String>mChildred;
	private List<String>parentIds;
	private Map<String,String>sourceIds;
	private String parentSobjectType;
	
	public ChildDetails(Map<String, String>mChildred, List<String>parentIds, Map<String,String>sourceIds,String parentSobjectType) {
		this.mChildred = mChildred;
		this.parentIds = parentIds;
		this.sourceIds = sourceIds;
		this.parentSobjectType = parentSobjectType;
	}

	public Map<String, String> getmChildred() {
		return mChildred;
	}

	public void setmChildred(Map<String, String> mChildred) {
		this.mChildred = mChildred;
	}

	public List<String> getParentIds() {
		return parentIds;
	}

	public void setParentIds(List<String> parentIds) {
		this.parentIds = parentIds;
	}

	public Map<String,String> getSourceIds() {
		return sourceIds;
	}

	public void setSourceIds(Map<String,String> sourceIds) {
		this.sourceIds = sourceIds;
	}

	public String getParentSobjectType() {
		return parentSobjectType;
	}

	public void setParentSobjectType(String parentSobjectType) {
		this.parentSobjectType = parentSobjectType;
	}
	
	
	
}
