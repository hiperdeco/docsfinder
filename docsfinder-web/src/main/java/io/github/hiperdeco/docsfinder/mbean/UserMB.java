package io.github.hiperdeco.docsfinder.mbean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import io.github.hiperdeco.docsfinder.controller.CryptoUtil;
import io.github.hiperdeco.docsfinder.controller.JPAUtil;
import io.github.hiperdeco.docsfinder.entity.RoleType;
import io.github.hiperdeco.docsfinder.entity.User;
import io.github.hiperdeco.docsfinder.ui.UIConstants;
import io.github.hiperdeco.docsfinder.ui.util.UIUtil;

//TODO: Documentar classe
@ManagedBean(name = "UserMB")
@ViewScoped
public class UserMB extends AbstractCRUDMB<User> {

	private static Logger log = Logger.getLogger(UserMB.class);
	
	private Map<Long,String> passwords = new HashMap<Long,String>();
	
	@Override
	public User newInstance() {
		return new User();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getList() {
		try {
			this.list = JPAUtil.findAll(User.class);
			passwords.clear();
			for (User repo: list) {
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
			if (this.getObject().getPassword()!= null && !this.getObject().getPassword().isEmpty()) {
				try {
					this.getObject().setPassword(CryptoUtil.encodeBase64(this.getObject().getPassword()));
					log.debug("PWD Encrypted:" + this.getObject().getPassword());
				} catch (Exception e) {
				   	log.error("Crypto Error", e);
				   	UIUtil.putMessage(FacesMessage.SEVERITY_ERROR, "error.title", "error.message");
				}
			}
			super.create();
		}catch (Exception e) {
			if (e.getMessage().contains("UQ_USERNAME") || e.getCause().getMessage().contains("UQ_USERNAME")) {
				UIUtil.putMessage(UIConstants.BUNDLE_USER, FacesMessage.SEVERITY_ERROR, "unique.title", "unique.message");
			}
		}
		return "";
	}
	
	public String update() {
		if (this.getObject().getPassword()!= null && !this.getObject().getPassword().isEmpty()) {
			try {
				this.getObject().setPassword(CryptoUtil.encodeBase64(this.getObject().getPassword()));
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
		this.getObject().setPassword("");
		return "";
	}
	
	public String delete(User object) {
		super.delete(object);
		passwords.remove(object.getId());
		return "";
	}
	

	@Override
	public List<User> filter(User object) {
		// TODO Auto-generated method stub
		return null;
	}

	public RoleType[] getRoleList() {
		return RoleType.values();
	}
	
	public void onRowSelect(SelectEvent event) {
		super.onRowSelect(event);
	}
	
}
