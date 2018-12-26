package io.github.hiperdeco.docsfinder.vo;

import java.io.Serializable;
import java.util.List;

public class ContentFoundVO implements Serializable {

	private static final long serialVersionUID = 6579104065180933007L;

	private long id;
	
	private String path;
	
	private String type;

	private List<String> content;
	
	private String fileName;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getContent() {
		return content;
	}
	
	public String getContentString() {
		StringBuffer contentString = new StringBuffer("<ul class=\"striped-list\">");
		for (String line: this.content) {
			contentString.append("<li>" + line + "</li>");
		}
		contentString.append("</ul>");
		return contentString.toString();
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtension() {
		String[] pathAux = this.fileName.split("\\."); 
		if (pathAux.length > 1)
			return pathAux[pathAux.length - 1].toLowerCase();
		return "";
	}
	

}
