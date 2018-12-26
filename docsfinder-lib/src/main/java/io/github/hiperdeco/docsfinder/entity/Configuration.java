package io.github.hiperdeco.docsfinder.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="CONFIG",indexes= { @Index(name="IDX_KEY_REPO",columnList="KEYNAME,REPO_ID")})
public class Configuration implements IEntityBase,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1551941148957587001L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REPO_ID", foreignKey = @ForeignKey(name = "FK_CONFIG_REPO"), insertable = false, updatable = false)
	private Repository repository;
	
	@Column(name="REPO_ID")
	private long repositoryId;
	
	
	@Column(name="KEYNAME", nullable=false, length=60)
	private String key;
	
	@Column(name="KEYVALUE")
	private String value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(long repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
			
}
