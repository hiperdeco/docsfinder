package io.github.hiperdeco.docsfinder.mbean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import io.github.hiperdeco.docsfinder.controller.CryptoUtil;
import io.github.hiperdeco.docsfinder.controller.JPAUtil;
import io.github.hiperdeco.docsfinder.entity.Configuration;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryType;
import io.github.hiperdeco.docsfinder.ui.UIConstants;
import io.github.hiperdeco.docsfinder.ui.util.UIUtil;

//TODO: Documentar classe
@ManagedBean(name = "RepositoryMB")
@ViewScoped
public class RepositoryMB extends AbstractCRUDMB<Repository> {

	private static Logger log = Logger.getLogger(RepositoryMB.class);
	
	private boolean remoteRepository = false;

	private Map<Long,String> passwords = new HashMap<Long,String>();
	
	@Override
	public Repository newInstance() {
		return new Repository();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Repository> getList() {
		try {
			this.list = JPAUtil.findAll(Repository.class);
			passwords.clear();
			for (Repository repo: list) {
				passwords.put(repo.getId(),repo.getPassword());
				repo.setPassword("");
			}
		} catch (Exception e) {
			 UIUtil.putMessage(FacesMessage.SEVERITY_INFO, "crud.list.title", "crud.list.norecords");
		}
		return this.list;
	}

	
	public String create() {
		try {
			if ( this.getObject().getType().equals(RepositoryType.LOCAL)) { 
				this.getObject().setPassword("");
				this.getObject().setUser("");
				this.getObject().setRemoteURL("");
				
			}else if (this.getObject().getPassword()!= null && !this.getObject().getPassword().isEmpty()) {
				try {
					this.getObject().setPassword(CryptoUtil.encode(this.getObject().getPassword()));
					log.debug("PWD Encrypted:" + this.getObject().getPassword());
				} catch (Exception e) {
				   	log.error("Crypto Error", e);
				   	UIUtil.putMessage(FacesMessage.SEVERITY_ERROR, "error.title", "error.message");
				}
			}
			super.create();
			//create default configuration
			try {
				Configuration conf = new Configuration();
				conf.setKey("INDEX_PATH");
				conf.setRepository(this.getObject());
				JPAUtil.insert(conf);
			}catch(Exception e) {
				log.error("Error creating configuration for " + this.getObject().getName());
			}
		}catch (Exception e) {
			if (e.getMessage().contains("UQ_NAME") || e.getCause().getMessage().contains("UQ_NAME")) {
				UIUtil.putMessage(UIConstants.BUNDLE_REPO, FacesMessage.SEVERITY_ERROR, "unique.title", "unique.message");
			}
		}
		refreshFields();
		return "";
	}
	
	public String update() {
		if ( this.getObject().getType().equals(RepositoryType.LOCAL)) { 
			this.getObject().setPassword("");
			this.getObject().setUser("");
			this.getObject().setRemoteURL("");
			
		}else if (this.getObject().getPassword()!= null && !this.getObject().getPassword().isEmpty()) {
			try {
				this.getObject().setPassword(CryptoUtil.encode(this.getObject().getPassword()));
				log.debug("PWD Encrypted:" + this.getObject().getPassword());
			} catch (Exception e) {
			   	log.error("Crypto Error", e);
			   	UIUtil.putMessage(FacesMessage.SEVERITY_ERROR, "error.title", "error.message");
			}
		}else{
			
				this.getObject().setPassword(passwords.get(this.getObject().getId()));
			
		}
		super.update();
		passwords.put(this.getObject().getId(), this.getObject().getPassword());
		refreshFields();
		
		return "";
	}
	
	public String delete(Repository object) {
		String hql = "delete from Configuration c where c.repository.id =  " + object.getId();
		JPAUtil.executeUpdate(hql);
		super.delete(object);
		passwords.remove(object.getId());
		refreshFields();
		return "";
	}


	public void onTypeChange(AjaxBehaviorEvent event) {
		RepositoryType type = (RepositoryType) ((javax.faces.component.html.HtmlSelectOneMenu) event
                .getSource()).getValue();
		refreshFields();

	}

	public boolean isRemoteRepository() {
		return remoteRepository;
	}

	@Override
	public List<Repository> filter(Repository object) {
		// TODO Auto-generated method stub
		return null;
	}

	public RepositoryType[] getTypeList() {
		return RepositoryType.values();
	}
	
	public void onRowSelect(SelectEvent event) {
		super.onRowSelect(event);
		refreshFields();
	}
	
	public void clear() {
		super.clear();
		refreshFields();
	}
	
	public void refreshFields() {
		if (this.getObject().getType().equals(RepositoryType.LOCAL)) {
			this.remoteRepository = false;
			this.getObject().setPassword("");
		}else {
			this.remoteRepository = true;
		}
	}
	
}
