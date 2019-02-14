package io.github.hiperdeco.docsfinder.vo;

import java.io.Serializable;
import java.util.List;

public class ContentFoundVO implements Serializable {

	private static final long serialVersionUID = 6579104065180933007L;

	private long id;
	
	private String path;
	
	private String pathDescription;
	
	private String type;

	private List<String> content;
	
	private String fileName;
	
	private String extension;

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
		if (this.content == null || this.content.isEmpty()) {
			return "";
		}
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
		if (this.extension == null || this.extension.isEmpty()) {
		String[] pathAux = this.fileName.split("\\."); 
		if (pathAux.length > 1)
			this.extension =  pathAux[pathAux.length - 1].toLowerCase();
		}
		return this.extension;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public String getPathDescription() {
		return pathDescription;
	}

	public void setPathDescription(String pathDescription) {
		this.pathDescription = pathDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContentFoundVO other = (ContentFoundVO) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}


}
