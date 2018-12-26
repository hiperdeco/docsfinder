package io.github.hiperdeco.docsfinder.mbean;

import java.io.InputStream;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import io.github.hiperdeco.docsfinder.controller.FileUtil;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.ui.ComboListItem;
import io.github.hiperdeco.docsfinder.ui.util.UIUtil;
import io.github.hiperdeco.docsfinder.vo.ContentFoundVO;
import io.github.hiperdeco.docsfinder.vo.SearchVO;

@ManagedBean(name = "SearchMB")
@SessionScoped
public class SearchMB {

	private SearchVO form = new SearchVO();

	private ContentFoundVO contentFoundSelected;
	private ContentFoundVO fileFoundSelected;
	private ContentFoundVO filePreviewSelected;


	private List<ContentFoundVO> filesFound;
	private List<ContentFoundVO> contentsFound;
	
	private boolean listFilesFoundVisible = false;
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

	public List<ContentFoundVO> findFiles() {

		return null;
	}

	public List<ContentFoundVO> find() {

		return null;
	}

	public List<ComboListItem> getRepositoryList() {

		return UIUtil.getComboList(Repository.class);
	}

	public void clear() {
		this.form = new SearchVO();

	}

	public void fileToDownload(ContentFoundVO file) {

	}

	// TODO: rewrite
	public StreamedContent getFileDownload() {
		InputStream stream = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream("/resources/demo/images/boromir.jpg");
		return new DefaultStreamedContent(stream, "image/jpg", "downloaded_boromir.jpg");
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

	public int getContentFoundSize() {
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

	public boolean isListFilesFoundVisible() {
		return listFilesFoundVisible;
	}

	public void setListFilesFoundVisible(boolean listFilesFoundVisible) {
		this.listFilesFoundVisible = listFilesFoundVisible;
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

}
