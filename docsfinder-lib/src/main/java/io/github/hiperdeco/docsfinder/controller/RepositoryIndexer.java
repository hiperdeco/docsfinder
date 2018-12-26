package io.github.hiperdeco.docsfinder.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.tika.Tika;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import io.github.hiperdeco.docsfinder.constants.Properties;
import io.github.hiperdeco.docsfinder.entity.FileType;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryStatus;

//TODO: documentar classe
public class RepositoryIndexer implements Serializable {

	private static Logger log = Logger.getLogger(RepositoryIndexer.class);

	private static final long serialVersionUID = -64136026102778742L;

	private static FieldType contentFieldType = new FieldType();
	static {
		contentFieldType.setStored(false);
		contentFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
	}

	private Tika tika = new Tika();
	private Repository repository;

	private List<Document> filePaths = new ArrayList<Document>();

	public RepositoryIndexer(Repository repository) {
		this.repository = repository;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	

	private void addFilePath(Object path) {

		try {
			Document doc = new Document();
			doc.add(new StringField("path", path.toString(), Store.YES));
			doc.add(new StringField("mimeType", tika.detect((Path) path), Store.YES));
			String[] pathAux = path.toString().split("\\."); 
			if (pathAux.length > 1)
				doc.add(new StringField("extension", pathAux[pathAux.length - 1].toLowerCase(), Store.YES));
			doc.add(new Field("content", tika.parse((Path) path), contentFieldType));
			filePaths.add(doc);
		} catch (IOException e) {
			log.info("Document " + path.toString() + " discarded");
			log.debug("Indexed document " + path.toString() + " error", e);
		}

	}

	public void execute() {

		changeIndexStatus(RepositoryStatus.INDEXING);
		// varrer o diretorio local informado.
		File localDirectory = new File(this.getRepository().getLocalDirectory());
		if (!localDirectory.exists() || !localDirectory.isDirectory()) {
			changeIndexStatusError();
			throw new RuntimeException("Directory not exists");
		}

		try {
			Files.walk(Paths.get(localDirectory.toURI())).filter(Files::isRegularFile).forEach(this::addFilePath);
		} catch (IOException e) {
			log.error("Read Directory Error", e);
			changeIndexStatusError();
			throw new RuntimeException(e);
		}

		if (filePaths.isEmpty()) {
			changeIndexStatus(RepositoryStatus.EMPTY);
			log.warn("Directory " + localDirectory.getAbsolutePath() + " is empty.");
			throw new RuntimeException("Directory  is empty");
		}

		// process index
		try {
			processIndex();
			persistFileTypes();
			this.getRepository().setLastExecution(new Date());
			this.getRepository().setIndexSequence(this.getRepository().getNextIndexSequence());
			changeIndexStatus(RepositoryStatus.INDEXED);
		} catch (Exception e) {
			log.error("Index Error", e);
			changeIndexStatusError();
		}

	}

	private void processIndex() throws IOException {

		IndexWriter writer = null;
		Directory dir = null;
		Analyzer analyzer = null;
		try {
			analyzer = new StandardAnalyzer();
			// armazenar
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setOpenMode(OpenMode.CREATE);

			dir = NIOFSDirectory.open(new File(	getIndexPath()
					+ this.getRepository().getId() + "_" + this.getRepository().getNextIndexSequence())
					.toPath());
			writer = new IndexWriter(dir, config);
			writer.addDocuments(filePaths);
		} catch (Exception e) {
			throw e;
		} finally {
			if (writer != null)
				try{writer.close();}catch(Exception e) {}
			if (dir != null)
				try{dir.close();}catch(Exception e) {}
			if (analyzer != null)
				try{analyzer.close();}catch(Exception e) {}
		}

	}

	public void changeIndexStatus(RepositoryStatus status) {
		try {
			this.getRepository().setStatus(status);
			JPAUtil.update(this.getRepository());
		} catch (Exception e) {
			throw e;
		} 
	}

	public void changeIndexStatusError() {
		if (this.getRepository().getLastExecution() != null) {
			changeIndexStatus(RepositoryStatus.INDEXED_ERROR);
		} else {
			changeIndexStatus(RepositoryStatus.ERROR);
		}
	}
	
	public static void removeIndexedSequence(Repository repository) {
		try {
			FileUtils.deleteDirectory(new File(
					Properties.get("indexPath", System.getProperty("java.io.tmpdir"))
					+ repository.getId() + "_" + repository.getIndexSequence())
					); 
		} catch (Exception e) {
			log.error("Deleteting Directory Error", e);
			throw new RuntimeException(e);
		}
	}
	
	private void persistFileTypes() {
		EntityManager em = null;
		try {
			em = JPAUtil.getEntityManager();
			em.getTransaction().begin();
			for(Document doc: filePaths) {
				try {
					FileType type = new FileType();
					type.setMimeType(doc.getField("mimeType").stringValue());
					type.setExtension(doc.getField("extension").stringValue());
					em.persist(type);
				}catch(Exception e) {
					log.debug("Error Persisting FileType",e);
				}
			}
			em.getTransaction().commit();
		}catch(Exception e) {
			log.debug("Error Persisting FileType",e);
			em.getTransaction().rollback();
		}finally {
			JPAUtil.closeEntityManager(em);
		}
		
	}
	
	private String getIndexPath() {
		String result = ConfigurationManager.getValue(this.repository.getId(), "INDEX_PATH");
		if (result == null || result.isEmpty()) {
			result = Properties.get("indexPath", System.getProperty("java.io.tmpdir"));
		}
		return 	result;
	}
	
}
