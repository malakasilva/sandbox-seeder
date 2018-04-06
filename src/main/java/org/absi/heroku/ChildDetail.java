package org.absi.heroku;

import java.util.List;
import java.util.Map;

public class ChildDetail {

	private Map<String, String>mChildred;
	private List<String>parentIds;
	private Map<String,String>sourceIds;
	
	public ChildDetail(Map<String, String>mChildred, List<String>parentIds, Map<String,String>sourceIds) {
		this.mChildred = mChildred;
		this.parentIds = parentIds;
		this.sourceIds = sourceIds;
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
	
	
	
}
