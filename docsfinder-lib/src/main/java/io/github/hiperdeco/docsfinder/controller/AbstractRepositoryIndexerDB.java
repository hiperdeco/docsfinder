package io.github.hiperdeco.docsfinder.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import io.github.hiperdeco.docsfinder.constants.Constants;
import io.github.hiperdeco.docsfinder.entity.Configuration;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryFile;
import io.github.hiperdeco.docsfinder.entity.RepositoryStatus;

public abstract class AbstractRepositoryIndexerDB implements Serializable{
	
	private static Logger log = Logger.getLogger(AbstractRepositoryIndexerDB.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -64136026102778742L;
	
	
	private Repository repository;
	
	private List<RepositoryFile> filePaths = new ArrayList<RepositoryFile>();

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public void addFilePath(Object path) {
		String[] pathAux = path.toString().split("\\."); 
		String[] fileName = path.toString().split(File.separator);
		if (pathAux.length > 1 && Constants.FILE_EXTENSIONS.contains(pathAux[pathAux.length - 1].toLowerCase())) {		
			
			RepositoryFile rf = new RepositoryFile();
			rf.setFileName(fileName[fileName.length -1]);
			rf.setPath(path.toString());
			rf.setRepository(this.repository);
			
			filePaths.add(rf);
		}
	}
	
	public void execute() {
		
		if (!this.getRepository().getStatus().equals(RepositoryStatus.EMPTY) 
				&& this.getRepository().getLastExecution() == null) cleanRepositoryFilesIndex();
	
		changeIndexStatus(RepositoryStatus.INDEXING);
		//varrer o diretorio local informado.	
		File localDirectory = new File(this.getRepository().getLocalDirectory());
		if (!localDirectory.exists() || !localDirectory.isDirectory()) {
			changeIndexStatusError();
			throw new RuntimeException("Directory not exists");
		}

		try {
			Files.walk(Paths.get(localDirectory.toURI()))
	        .filter(Files::isRegularFile)
			        .forEach(this::addFilePath);
		} catch (IOException e) {
			log.error("Read Directory Error", e);
			changeIndexStatusError();
			throw new RuntimeException(e);
		}
		
		if (filePaths.isEmpty()) {
			changeIndexStatus(RepositoryStatus.EMPTY);
			throw new RuntimeException("Directory  is empty");
		}
		
		persistFilePaths();
	
		try {
			processIndex();
			this.getRepository().setLastExecution(new Date());
			changeIndexStatus(RepositoryStatus.INDEXED);
		}catch(Exception e) {
			changeIndexStatusError();
		}
		
		
	}
	
	
	private void processIndex() {
		
		List<Configuration> confs = ConfigurationManager.getConfiguration(this.getRepository().getId());
		for (RepositoryFile rf: filePaths) {
			String[] fileAux = rf.getFileName().split("\\.");
			if (fileAux[fileAux.length -1].equals("doc")) {
				new DocIndexer(rf, confs).execute();
			}
		}
		
		
	}
	
	

	private void persistFilePaths() {
		EntityManager em = null;
		try {
			em = JPAUtil.getEntityManager();
			em.getTransaction().begin();
			for (int i = 0; i < filePaths.size(); i++) {
				if (i % 20 == 0) {
					em.flush();
					em.clear();
				}
				em.persist(filePaths.get(i));
			}
			em.flush();
			em.clear();
			em.getTransaction().commit();
		}catch(Exception e) {
			if (em != null) { em.getTransaction().rollback();}
			changeIndexStatusError();
			throw new RuntimeException(e);
		}finally {
			JPAUtil.closeEntityManager(em);
		}

		
	}

	public void cleanRepositoryFilesIndex() {
			EntityManager em  = null;
			try {		
				em  = JPAUtil.getEntityManager();
				em.getTransaction().begin();
				Query repoFileQueryIndex = em.createQuery("delete from RepositoryFileIndex rp where rp.repository.id = :id");
				repoFileQueryIndex.setParameter(":id", this.repository.getId()).executeUpdate();	
				Query repoFileQuery = em.createQuery("delete from RepositoryFile rp where rp.repository.id = :id");
				repoFileQuery.setParameter(":id", this.repository.getId()).executeUpdate();	
				changeIndexStatus(RepositoryStatus.EMPTY);
				em.getTransaction().commit();
			}catch(Exception e) {
				if (em != null) em.getTransaction().rollback();
				throw e;
			}finally {
				JPAUtil.closeEntityManager(em);
			}		
	}
	
	public void changeIndexStatus(RepositoryStatus status) {
		try {
			this.getRepository().setStatus(status);
			JPAUtil.update(this.getRepository());
		}catch(Exception e) {
			throw e;
		}finally {
		}		
	}
	
	public void changeIndexStatusError() {
		if (this.getRepository().getLastExecution() != null) {
			changeIndexStatus(RepositoryStatus.INDEXED_ERROR);
		}else {
			changeIndexStatus(RepositoryStatus.ERROR);
		}
	}
	
	
	

}
