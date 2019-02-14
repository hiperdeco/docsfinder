package io.github.hiperdeco.docsfinder.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParsingReader;

import io.github.hiperdeco.docsfinder.constants.Properties;
import io.github.hiperdeco.docsfinder.entity.FileType;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryStatus;

//TODO: documentar classe
public class RepositoryIndexer implements Serializable {

	private static Logger log = Logger.getLogger(RepositoryIndexer.class);

	private static final long serialVersionUID = -64136026102778742L;

	private Tika tika = new Tika();
	private TikaConfig tikaConfig = null;
	private Repository repository;
	private IndexWriter writer;
	private String localDirectory = null;
	
	private Set<FileType> filesType = new HashSet<FileType>();

	public RepositoryIndexer(Repository repository) {
		this.repository = repository;
		this.localDirectory = repository.getLocalDirectory();
		try {
			this.tikaConfig = new TikaConfig();
		}catch(Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public Repository getRepository() {
		return repository;
	}

	
	public Reader parse(Path path) throws Exception {
		try {
			Metadata metadata = new Metadata();
			InputStream stream = TikaInputStream.get(path, metadata);
			ParseContext context = new ParseContext();
			context.set(Parser.class, tikaConfig.getParser());
			return new ParsingReader(tikaConfig.getParser(), stream, metadata, context,
					tikaConfig.getExecutorService());
		} catch (Exception e) {
			throw e;
		}
	}

	private void addFileIndex(Object path) {

		try {
			Document doc = new Document();
			String extension ="";
			String mimeType = tika.detect((Path) path);
			doc.add(new StringField("path", path.toString(), Store.YES));
			doc.add(new StringField("mimeType", mimeType, Store.YES));
			doc.add(new StringField("fileName", ((Path) path).getFileName().toString(), Store.YES));
			String[] pathAux = path.toString().split("\\.");
			
			if (pathAux.length > 1) {
				extension = pathAux[pathAux.length - 1].toLowerCase();
				doc.add(new StringField("extension", extension , Store.YES));
			}
			doc.add(new TextField("content", tika.parseToString((Path) path), Store.YES));
			//filePaths.add(doc);
			if (writer != null)	writer.addDocument(doc);
			FileType type = new FileType(mimeType, extension);
			filesType.add(type);
			log.debug("Document " + path.toString() + " parsed.");
		} catch (Exception e) {
			log.info("Document " + path.toString() + " discarded");
			log.debug("Indexed document " + path.toString() + " error", e);
		}

	}

	public void execute() {

		try {
			changeIndexStatus(RepositoryStatus.INDEXING);
		} catch (Exception e) {
			throw e;
		}
		// varrer o diretorio local informado.
		File localDirectory = new File(this.localDirectory);
		if (!localDirectory.exists() || !localDirectory.isDirectory()) {
			changeIndexStatusError();
			throw new RuntimeException("Directory not exists");
		}

		Directory dir = null;
		Analyzer analyzer = null;
		
		try {
			analyzer = new StandardAnalyzer();
			// armazenar
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setOpenMode(OpenMode.CREATE);
			config.setCommitOnClose(true);

			dir = NIOFSDirectory.open(new File(
					getIndexPath(this.getRepository()) + this.getRepository().getId() + "_" + this.getRepository().getNextIndexSequence())
							.toPath());
			writer = new IndexWriter(dir, config);
			
			Files.walk(Paths.get(localDirectory.toURI())).filter(Files::isRegularFile).forEach(this::addFileIndex);
			writer.flush();
		} catch (IOException e) {
			log.error("Read Directory Error", e);
			changeIndexStatusError();
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			changeIndexStatusError();
			throw e;
		} finally {
			if (writer != null)
				try {
					writer.close();
					writer = null;
				} catch (Exception e) {
				}
			if (dir != null)
				try {
					dir.close();
				} catch (Exception e) {
				}
			if (analyzer != null)
				try {
					analyzer.close();
				} catch (Exception e) {
				}
		}

		
		try {
			persistFileTypes();
		}catch (Exception e) {
			log.error("Persist FileType Error", e);
		}
		
		// process success status
		try {
			changeFinishStatus();
		} catch (Exception e) {
			log.error("Index Error", e);
			changeIndexStatusError();
		}

	}

	public synchronized void changeIndexStatus(RepositoryStatus status) {
		if (!isIndexRunningConflict(status)) {
			try {
				this.repository = (Repository) JPAUtil.findById(Repository.class, this.getRepository().getId());
				this.getRepository().setStatus(status);
				JPAUtil.update(this.getRepository());
			} catch (Exception e) {
				throw e;
			}
		} else {
			log.error("Repository Already Index Running" + this.getRepository().getName());
			throw new RuntimeException("");
		}
	}
	
	public synchronized void changeFinishStatus() {
	
		try {
			
			this.repository = (Repository) JPAUtil.findById(Repository.class, this.getRepository().getId());
			this.getRepository().setStatus(RepositoryStatus.INDEXED);
			this.getRepository().setLastExecution(new Date());
			this.getRepository().setIndexSequence(this.getRepository().getNextIndexSequence());
			JPAUtil.update(this.getRepository());
			removeLastIndexedSequence(this.getRepository());
		} catch (Exception e) {
			throw e;
		}
	
	}

	private boolean isIndexRunningConflict(RepositoryStatus status) {
		boolean ret = false;
		if (status.equals(RepositoryStatus.INDEXING)) {
			EntityManager em = null;
			try {
				em = JPAUtil.getEntityManager();
				Query query = em
						.createQuery("select repo.status, repo.lastStatus from Repository repo where repo.id = ?");
				query.setParameter(1, this.getRepository().getId());
				Object[] line = (Object[]) query.getSingleResult();
				RepositoryStatus st = (RepositoryStatus) line[0];
				Date lastStatus = (Date) line[1];
				if (st.equals(status)) {
					if (lastStatus != null) {

						long hourDuration = Duration.between(Instant.ofEpochMilli(lastStatus.getTime()),
								Instant.ofEpochMilli((new Date()).getTime())).toHours();
						log.info("Check conflict. Time spent from " + this.getRepository().getName()
								+ " for indexRunning " + hourDuration + "h");
						if (hourDuration < Long.valueOf(System.getProperty("maxRepoIndexing", "24"))) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				JPAUtil.closeEntityManager(em);
			}
		}
		return ret;
	}

	public void changeIndexStatusError() {
		this.repository = (Repository) JPAUtil.findById(Repository.class, this.getRepository().getId());
		if (this.getRepository().getLastExecution() != null) {
			changeIndexStatus(RepositoryStatus.INDEXED_ERROR);
		} else {
			changeIndexStatus(RepositoryStatus.ERROR);
		}
	}

	public static void removeLastIndexedSequence(Repository repository) {
		try {
			FileUtils.deleteDirectory(new File(getIndexPath(repository)
					+ repository.getId() + "_" + (repository.getIndexSequence() - 1) ));
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
			for (FileType type: this.filesType) {
				try {
					em.persist(type);
				}catch(Exception e) {
					log.debug("Error Persisting FileType:" + type);
				}
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			log.debug("Error persisting filetypes");
			em.getTransaction().rollback();
		} finally {
			JPAUtil.closeEntityManager(em);
		}

	}

	private static  String getIndexPath(Repository repository) {
		String result = ConfigurationManager.getValue(repository.getId(), "INDEX_PATH");
		if (result == null || result.isEmpty()) {
			result = Properties.get("indexPath", System.getProperty("java.io.tmpdir"));
			if (!result.endsWith("/")) {
				result += "/";
			}
		}
		return result;
	}

}
