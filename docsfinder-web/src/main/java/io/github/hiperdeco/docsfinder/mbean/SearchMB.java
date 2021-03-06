package io.github.hiperdeco.docsfinder.mbean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import io.github.hiperdeco.docsfinder.controller.FileUtil;
import io.github.hiperdeco.docsfinder.controller.JPAUtil;
import io.github.hiperdeco.docsfinder.controller.RepositoryFinder;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.ui.ComboListItem;
import io.github.hiperdeco.docsfinder.ui.util.UIUtil;
import io.github.hiperdeco.docsfinder.vo.ContentFoundVO;
import io.github.hiperdeco.docsfinder.vo.SearchVO;

@ManagedBean(name = "SearchMB")
@ViewScoped
public class SearchMB {
	
	private static Logger log = Logger.getLogger(SearchMB.class);

	private SearchVO form = new SearchVO();

	private ContentFoundVO contentFoundSelected;
	private ContentFoundVO fileFoundSelected;
	private ContentFoundVO filePreviewSelected;
	private StreamedContent fileToDownload = null;

	private List<ContentFoundVO> filesFound;
	private List<ContentFoundVO> contentsFound;

	private boolean listContentsFoundVisible = false;

	private long repositoryId;

	public SearchVO getForm() {
		return form;
	}

	public void setForm(SearchVO form) {
		this.form = form;
	}

	public String search() {

		return "";
	}

	public String find() {
		List<ContentFoundVO> list = new ArrayList<ContentFoundVO>();
		try {
			Repository repo = (Repository) JPAUtil.findById(Repository.class, this.getRepositoryId());
			RepositoryFinder finder = new RepositoryFinder(repo);
			list = finder.find(this.getForm());
		}catch(Exception e) {
			log.error(e.getMessage(),e);
		}
		this.contentsFound = list;
		if (!list.isEmpty()) {
			listContentsFoundVisible = true;
		}else {
			 UIUtil.putMessage(FacesMessage.SEVERITY_INFO, "crud.list.search", "crud.list.notfound");
			listContentsFoundVisible = false;
		}
		return "";
	}

	public List<ComboListItem> getRepositoryList() {

		return UIUtil.getComboList(Repository.class);
	}

	public void clear() {
		this.form = new SearchVO();
		this.fileFoundSelected = null;
		this.contentFoundSelected = null;
		this.listContentsFoundVisible = false;

	}

	public void fileToDownload(ContentFoundVO file) {
		try {
			this.fileToDownload.getStream().close();
		}catch(Exception e) {
			
		}
		this.fileFoundSelected = file;
		String localPath = ((Repository) JPAUtil.findById(Repository.class, this.getRepositoryId())).getLocalDirectory();
		InputStream stream = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream(localPath + this.contentFoundSelected.getPath());
		this.fileToDownload = new DefaultStreamedContent(stream, this.contentFoundSelected.getType(), this.contentFoundSelected.getFileName());

	}

	public StreamedContent getFileDownload() {
		try {
			this.fileToDownload.getStream().close();
		}catch(Exception e) {
			
		}
		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String fileName = (String) map.get("fileName");
		String filePath = (String) map.get("filePath");
		String fileType = (String) map.get("fileType");
		
		//String localPath = ((Repository) JPAUtil.findById(Repository.class, this.getRepositoryId())).getLocalDirectory();
		InputStream stream = null;
		try {
			stream = new  FileInputStream(filePath);
			log.debug("Download file: " + filePath);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(),e);
		}
		
		this.fileToDownload = new DefaultStreamedContent(stream, fileType, fileName);
		return this.fileToDownload;
	}
	public void setFileDownload(StreamedContent file) {
		this.fileToDownload = file;
	}

	public ContentFoundVO getFileFoundSelected() {
		return fileFoundSelected;
	}

	public void setFileFoundSelected(ContentFoundVO fileFoundSelected) {
		this.fileFoundSelected = fileFoundSelected;
	}

	public ContentFoundVO getContentFoundSelected() {
		return contentFoundSelected;
	}

	public void setContentFoundSelected(ContentFoundVO contentFoundSelected) {
		this.contentFoundSelected = contentFoundSelected;
	}

	public List<ContentFoundVO> getFilesFound() {
		return filesFound;
	}

	public void setFilesFound(List<ContentFoundVO> filesFound) {
		this.filesFound = filesFound;
	}

	public List<ContentFoundVO> getContentsFound() {
		return contentsFound;
	}

	public void setContentsFound(List<ContentFoundVO> contentsFound) {
		this.contentsFound = contentsFound;
	}

	public int getFilesFoundSize() {
		int size = 0;
		if (this.filesFound != null)
			size = this.filesFound.size();
		return size;
	}

	public int getContentsFoundSize() {
		int size = 0;
		if (this.contentsFound != null)
			size = this.contentsFound.size();
		return size;
	}

	public long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(long repositoryId) {
		this.repositoryId = repositoryId;
	}

	public ContentFoundVO getFilePreviewSelected() {
		return filePreviewSelected;
	}

	public void setFilePreviewSelected(ContentFoundVO filePreviewSelected) {
		this.filePreviewSelected = filePreviewSelected;
	}

	public String getAllContentHTML() {
		return FileUtil.getContentsHTML(this.repositoryId, this.filePreviewSelected.getPath());

	}

	public boolean isListContentsFoundVisible() {
		return listContentsFoundVisible;
	}

	public void setListContentsFoundVisible(boolean listContentsFoundVisible) {
		this.listContentsFoundVisible = listContentsFoundVisible;
	}

	public boolean isFilePreViewDialogVisible() {
		return this.filePreviewSelected != null;
	}
	
	@PostConstruct
	@PreDestroy
	public void clearOpenFile() {
		try {
			this.fileToDownload.getStream().close();
			this.fileToDownload = null;
		}catch(Exception e) {
			
		}
	}
	

}
