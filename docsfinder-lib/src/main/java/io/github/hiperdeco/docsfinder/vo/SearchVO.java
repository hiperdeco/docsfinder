package io.github.hiperdeco.docsfinder.vo;

import java.io.Serializable;

public class SearchVO implements Serializable{
	
	private static final long serialVersionUID = 1192445477401488962L;

	private String Term;
	
	private boolean findPath = false;
	
	private String[] types;
	
	public String getTerm() {
		return Term;
	}

	public void setTerm(String term) {
		Term = term;
	}

	public boolean isFindPath() {
		return findPath;
	}

	public void setFindPath(boolean findPath) {
		this.findPath = findPath;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}	

}
