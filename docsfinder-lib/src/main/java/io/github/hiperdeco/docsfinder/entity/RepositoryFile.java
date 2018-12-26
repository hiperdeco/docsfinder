package io.github.hiperdeco.docsfinder.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Deprecated
@Entity
@Table(name="REPO_FILE",indexes= { @Index(name="IDX_FILENAME",columnList="FILENAME")})
public class RepositoryFile implements IEntityBase,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1819479956188517314L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@ManyToOne
	@JoinColumn(name="REPO_ID", foreignKey = @ForeignKey(name = "FK_REPO_FILE_REPO"))
	private Repository repository;
	
	@Column(name="PATH", nullable=false, length=10000)
	private String path;
	
	@Column(name="FILENAME", nullable=false, length=255)
	private String fileName;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
}
