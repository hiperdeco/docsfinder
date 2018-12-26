package io.github.hiperdeco.docsfinder.entity;

public enum RepositoryType {
	LOCAL,
	SVN,
	GIT;
	
	public static RepositoryType fromInt(int value){
		return RepositoryType.values()[value];
	}
}
