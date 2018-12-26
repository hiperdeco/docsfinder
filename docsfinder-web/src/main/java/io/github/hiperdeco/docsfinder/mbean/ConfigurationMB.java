package io.github.hiperdeco.docsfinder.mbean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import io.github.hiperdeco.docsfinder.controller.JPAUtil;
import io.github.hiperdeco.docsfinder.entity.Configuration;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.ui.ComboListItem;
import io.github.hiperdeco.docsfinder.ui.util.UIUtil;

@ManagedBean(name="ConfigurationMB")
@ViewScoped
public class ConfigurationMB extends AbstractCRUDMB<Configuration>  {

	@Override
	public Configuration newInstance() {
		return new Configuration();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Configuration> getList() {
		try {
			this.list = JPAUtil.findAll(Configuration.class);
		}catch (Exception e) {
			//putMessage(FacesMessage.SEVERITY_INFO, "crud.list.title", "crud.list.norecords");
		}
		return this.list;
	}

	@Override
	public List<Configuration> filter(Configuration object) {
		// TODO implementar filter
		return null;
	}

	public List<ComboListItem> getRepositoryList(){
			
		return UIUtil.getComboList(Repository.class);
	}
	
}
