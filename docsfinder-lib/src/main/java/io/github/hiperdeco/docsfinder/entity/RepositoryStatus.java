package io.github.hiperdeco.docsfinder.entity;

public enum RepositoryStatus {
	EMPTY,
	INDEXING,
	INDEXED,
	ERROR,
	INDEXED_ERROR;
	
	public static RepositoryStatus fromInt(int value){
		return RepositoryStatus.values()[value];
	}
}
