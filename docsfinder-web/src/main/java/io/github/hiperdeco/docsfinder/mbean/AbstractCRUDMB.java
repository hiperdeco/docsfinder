package io.github.hiperdeco.docsfinder.mbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import io.github.hiperdeco.docsfinder.controller.JPAUtil;
import io.github.hiperdeco.docsfinder.entity.IEntityBase;
import io.github.hiperdeco.docsfinder.ui.util.UIUtil;

//TODO: documentar a classe
public abstract class AbstractCRUDMB<T> {

	protected boolean editing = false;
	
	protected List<T> list;
	protected T objectSelected =  null;
	
	public abstract T newInstance();
	public abstract List<T> getList() ;
	public abstract List<T> filter(T object);
	
	public void setObject(T object) {
		this.objectSelected = object;
	}
	
	public T getObject() {
		if (this.objectSelected == null) this.clear();
		return	this.objectSelected;
	}
	
	public boolean isEditing() {
		return editing;
	}
	
	
	public String create() {
		try {
			JPAUtil.insert(this.objectSelected);
			if (this.list == null){ this.list = new ArrayList<T>(); }
			this.list.add(this.objectSelected);
			UIUtil.putMessage(FacesMessage.SEVERITY_INFO, "crud.add.title", "crud.add.msg.sucess");
		}catch (Exception e) {
			UIUtil.putMessage(FacesMessage.SEVERITY_ERROR,"crud.add.title", "crud.add.msg.error");
			throw e;
		}
		this.clear();
		return "";
	}

	
	public String update() {
		try {
			JPAUtil.update(this.objectSelected);
			UIUtil.putMessage(FacesMessage.SEVERITY_INFO, "crud.update.title", "crud.update.msg.sucess");
		}catch (Exception e) {
			UIUtil.putMessage(FacesMessage.SEVERITY_ERROR,"crud.update.title", "crud.update.msg.error");
		}
		this.clear();
		return "";
	}

	
	public String delete(T object) {
		try {
			JPAUtil.delete(object);
			UIUtil.putMessage(FacesMessage.SEVERITY_INFO, "crud.delete.title", "crud.delete.msg.sucess");
			removeListView((IEntityBase) object);
		}catch (Exception e) {
			UIUtil.putMessage(FacesMessage.SEVERITY_ERROR,"crud.delete.title", "crud.delete.msg.error");
		}	
		this.clear();
		return "";
	}


	public void clear() {
		this.objectSelected = this.newInstance();
		this.editing = false;
	}

	@SuppressWarnings("unchecked")
	public void onRowSelect(SelectEvent event) {
		 this.objectSelected = (T) event.getObject();
		 this.editing = true;

	}
	
	public void onRowUnSelect(UnselectEvent event) {
		//nothing at moment
	}

	public void editAction(ActionEvent event) {
		//nothing at moment
	}
	
	public void removeListView(IEntityBase object){
		
		for(int i=0; i< list.size(); i++){
			IEntityBase it = (IEntityBase) list.get(i);
			if (it.getId() == object.getId()){
				list.remove(i);
				return;
			}
		}
		
	}
		
	public int getListSize() {
		int size = 0;
		if (this.list != null ) size = 	this.list.size();
		return size;
	}

}
