package io.github.hiperdeco.docsfinder.entity;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="REPO",uniqueConstraints= {@UniqueConstraint(name="UQ_NAME",columnNames= {"NAME"})})
@NamedQueries({
	@NamedQuery(name="Repository.findAllIdAndDescription", query="select r.id, r.name from Repository r"),
	@NamedQuery(name="Repository.findByName", query="select r from Repository r where r.name = ?")
})
public class Repository implements IEntityBase,Serializable {

	
	private static final long serialVersionUID = -4605713286175063920L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@Column(name="NAME", nullable=false)
	private String name;
	
	@Column(name="LOCAL_DIRECTORY", nullable=false)
	private String localDirectory;
	
	@Column(name="REMOTE_URL")
	private String remoteURL;
	
	@Column(name="USER")
	private String user;
	
	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="TYPE",nullable=false)
	private RepositoryType type = RepositoryType.LOCAL;
	
	@Column(name="REVISION")
	private String revision = "0";
	
	@Column(name="LAST_EXECUTION")
	private Date lastExecution;
	
	@Column(name="STATUS")
	private RepositoryStatus status = RepositoryStatus.EMPTY;
	
	@Column(name="INDEX_SEQUENCE")
	private long indexSequence;
	
	@Column(name="CRON_SCHEDULE")
	private String cronSchedule ="0 0 0 * * ?"; //default Daily 00:00 AM
	
	@Column(name="LAST_STATUS")
	private Date lastStatus;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalDirectory() {
		return localDirectory;
	}

	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	public String getRemoteURL() {
		return remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RepositoryType getType() {
		return type;
	}

	public void setType(RepositoryType type) {
		this.type = type;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public Date getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	public RepositoryStatus getStatus() {
		return status;
	}

	public void setStatus(RepositoryStatus status) {
		this.status = status;
		this.lastStatus = new Date();
	}

	public long getIndexSequence() {
		return indexSequence;
	}

	public void setIndexSequence(long indexSequence) {
		this.indexSequence = indexSequence;
	}

	public long getNextIndexSequence() {
		// TODO Auto-generated method stub
		return this.indexSequence + 1;
	}

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}
	public Date getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(Date lastStatus) {
		this.lastStatus = lastStatus;
	}
	public void clearEssentials() {
		this.status = null;
		this.cronSchedule = null;
		this.password = null;
		this.type = null;
		this.remoteURL = null;
		this.revision = null;
		this.localDirectory = null;
		this.remoteURL = null;
		
	}
	
}
