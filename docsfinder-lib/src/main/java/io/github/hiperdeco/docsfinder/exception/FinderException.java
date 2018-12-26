package io.github.hiperdeco.docsfinder.exception;

public class FinderException extends Exception {

	private static final long serialVersionUID = -783283335313493615L;
	
	public enum Type{
		ERROR,
		REPOSITORY_EMPTY,
		REPOSITORY_ERROR,
		NOT_FOUND
		;
	}
	
	private Type type = Type.ERROR;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public FinderException(Throwable e, Type type) {
		super(e);
		this.type = type;
	}
	
	public FinderException(String description, Type type) {
		super(description);
		this.type = type;
	}

}
