package org.absi.heroku;

import java.util.List;
import java.util.Map;

public class ChildDetail {

	private Map<String, String>mChildred;
	private List<String>parentIds;
	
	public ChildDetail(Map<String, String>mChildred, List<String>parentIds) {
		this.mChildred = mChildred;
		this.parentIds = parentIds;
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
	
	
	
}
