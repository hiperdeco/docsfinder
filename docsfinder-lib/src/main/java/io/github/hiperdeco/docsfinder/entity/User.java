package io.github.hiperdeco.docsfinder.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="USER", indexes={@Index(name="IDX_USERNAME",columnList="USERNAME")}
, uniqueConstraints= {@UniqueConstraint(name="UQ_USERNAME",columnNames= {"USERNAME"})})
public class User implements IEntityBase,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6877459785726621897L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@Column(name="USERNAME", nullable=false)
	private String userName;
	
	@Column(name="NAME", nullable=false)
	private String name;
	
	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="ROLE")
	private RoleType role = RoleType.USER;
	
	@Column(name="REMOTE_AUTH", nullable=false)
	private boolean remoteAuth = false;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RoleType getRole() {
		return role;
	}

	public void setRole(RoleType role) {
		this.role = role;
	}

	public boolean isRemoteAuth() {
		return remoteAuth;
	}

	public void setRemoteAuth(boolean remoteAuth) {
		this.remoteAuth = remoteAuth;
	}

	
	
}
