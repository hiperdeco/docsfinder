package io.github.hiperdeco.docsfinder.entity;

public enum RoleType {
	ADMIN,
	USER;
	
	public static RoleType fromInt(int value){
		return RoleType.values()[value];
	}

}
