package io.github.hiperdeco.docsfinder.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name="FILE_TYPE",uniqueConstraints= {@UniqueConstraint(name="UQ_FILE_TYPE",columnNames= {"MIME_TYPE","EXTENSION"})})
public class FileType implements IEntityBase,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2949034373670657497L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;

	@Column(name="MIME_TYPE")
	private String mimeType;
	
	@Column(name="EXTENSION")
	private String extension;
	
	
	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;

	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public String toString() {
		return this.getMimeType() + " (." + this.extension+")";
	}

}
