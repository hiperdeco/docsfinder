package io.github.hiperdeco.docsfinder.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="USER_REPO")
public class UserRepository implements IEntityBase,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8607625870702043408L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@ManyToOne
	@JoinColumn(name="REPO_ID", foreignKey = @ForeignKey(name = "FK_USER_REPO_REPO"))
	private Repository repository;
	
	@ManyToOne
	@JoinColumn(name="USER_ID",  foreignKey = @ForeignKey(name = "FK_USER_REPO_USER"))
	private User user;
	
	@Column(name="ROLE")
	private RoleType role;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public RoleType getRole() {
		return role;
	}

	public void setRole(RoleType role) {
		this.role = role;
	}

	

}
