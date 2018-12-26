package io.github.hiperdeco.docsfinder.ui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import io.github.hiperdeco.docsfinder.controller.JPAUtil;
import io.github.hiperdeco.docsfinder.ui.ComboListItem;
import io.github.hiperdeco.docsfinder.ui.UIConstants;

//TODO: documentar classe
public class UIUtil {

	@SuppressWarnings("rawtypes")
	public static List<ComboListItem> getComboList(Class clazz){
		List<Object[]> list = JPAUtil.getListIdAndDescription(clazz);
		List<ComboListItem> result = new ArrayList<ComboListItem>();
		for(Object[] item : list) {
			ComboListItem listItem = new ComboListItem();
			listItem.setId((long) item[0]); 
			listItem.setDescription((String) item[1]);
			result.add(listItem);
		}
		return result;
	}
	
	public static void putMessage(Severity type, String title, String message) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.addMessage("msgReturn", 
				new FacesMessage(type, getMessageBundle(null,title), getMessageBundle(null,message)));
	}
	
	public static void putMessage(String bundle, Severity type, String title, String message) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.addMessage("msgReturn", 
				new FacesMessage(type, getMessageBundle(bundle,title), getMessageBundle(bundle,message)));
	}
	
	public static String getMessageBundle(String bundleName, String key) {
		
		if (bundleName == null) bundleName = UIConstants.BUNDLE_GENERAL;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		//String messageBundleName = facesContext.getApplication().getMessageBundle();
		Locale locale = facesContext.getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
		return bundle.getString(key);
	}

}
