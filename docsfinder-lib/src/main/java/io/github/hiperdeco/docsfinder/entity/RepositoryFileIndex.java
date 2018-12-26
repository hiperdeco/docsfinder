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


@Deprecated
@Entity
@Table(name="REPO_FILE_INDEX"
	//indices criados para efetivar configuracao
	,indexes= { @Index(name="IDX_STYLE_NAME",columnList="STYLE_NAME"), @Index(name="IDX_HEADER_LEVEL",columnList="HEADER_LEVEL")})

public class RepositoryFileIndex implements IEntityBase,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2794814405316090772L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@ManyToOne()
	@JoinColumn(name="REPO_FILE_ID", foreignKey = @ForeignKey(name = "FK_INDEX_REPOFILE"))
	private RepositoryFile repositoryFile;
	
	@Column(name="CONTENT",length=1024)
	private String content;
	
	@Column(name="HEADER_LEVEL")
	private int headerLevel = 9;
	
	@Column(name="STYLE_NAME")
	private String styleName;
	
	@Column(name="SEQUENCE")
	private long sequence = 1;
	
	@Column(name="REPO_ID")
	private long repositoryId;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public RepositoryFile getRepositoryFile() {
		return repositoryFile;
	}

	public void setRepositoryFile(RepositoryFile repositoryFile) {
		this.repositoryFile = repositoryFile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getHeaderLevel() {
		return headerLevel;
	}

	public void setHeaderLevel(int headerLevel) {
		this.headerLevel = headerLevel;
	}

	public String getStyleName() {
		return styleName;
	}

	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(long repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	
	

}
