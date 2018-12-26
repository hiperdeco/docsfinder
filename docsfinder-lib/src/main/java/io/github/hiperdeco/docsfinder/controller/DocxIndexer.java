package io.github.hiperdeco.docsfinder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;

import io.github.hiperdeco.docsfinder.constants.Properties;
import io.github.hiperdeco.docsfinder.entity.Configuration;
import io.github.hiperdeco.docsfinder.entity.RepositoryFile;
import io.github.hiperdeco.docsfinder.entity.RepositoryFileIndex;

public class DocxIndexer {
	
	private static Logger log = Logger.getLogger(DocxIndexer.class);
	private RepositoryFile repoFile;
	private List<Configuration> listConf;
	
	public DocxIndexer(RepositoryFile repoFile) {
		this.setRepoFile(repoFile);
	}
	
	public DocxIndexer(RepositoryFile repoFile, List<Configuration> listConf) {
		this.setRepoFile(repoFile);
		this.setListConf(listConf);
	}
	
	
	public void execute() {
		
		List<String> styleList = getConfList("STYLE");
		XWPFDocument doc  = null;
		try {
			File file = new File(repoFile.getPath());
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			doc = new XWPFDocument(fis);
			
			List<XWPFParagraph> range = doc.getParagraphs();
			
			List<RepositoryFileIndex> rfiList = new ArrayList<RepositoryFileIndex>();
			int sequence = 0;
			for (XWPFParagraph p: range) {
				sequence++;

				if (styleList.contains(p.getStyle().toUpperCase())) {
					RepositoryFileIndex rfi  = new RepositoryFileIndex();
					rfi.setRepositoryFile(repoFile);
					rfi.setRepositoryId(repoFile.getRepository().getId());
					rfi.setStyleName(p.getStyle());
					rfi.setSequence(sequence);
					rfi.setContent(p.getText().substring(0, 1024));
					rfiList.add(rfi);
					continue;
				}				
				
			}
			
			persistFileIndex(rfiList);
			
		
		}catch(Exception e) {
			log.error("File index error " + repoFile.getPath());
			throw new RuntimeException(e);
		}finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					log.debug(e.getMessage(),e);
				}
			}
		}
	}

	private void persistFileIndex(List<RepositoryFileIndex> rfiList) {
		
		EntityManager em = null;
		try {
			em = JPAUtil.getEntityManager();
			em.getTransaction().begin();
			for (int i = 0; i < rfiList.size(); i++) {
				if (i % 20 == 0) {
					em.flush();
					em.clear();
				}
				em.persist(rfiList.get(i));
			}
			em.flush();
			em.clear();
			em.getTransaction().commit();
		}catch(Exception e) {
			if (em != null) { em.getTransaction().rollback();}
			throw new RuntimeException(e);
		}finally {
			JPAUtil.closeEntityManager(em);
		}
		
	}

	private String getConf(String key) {
		String ret = null;
		for (Configuration conf: listConf) {
			if (conf.getKey().toUpperCase().equals(key.toUpperCase())) {
				return conf.getValue();
			}
		}
		return ret;
	}
	
	private List<String> getConfList(String key) {
		List<String> ret = new ArrayList<String>();
		for (Configuration conf: listConf) {
			if (conf.getKey().toUpperCase().equals(key)) {
				ret.add(conf.getValue().toUpperCase());
			}
		}
		return ret;
	}

	public RepositoryFile getRepoFile() {
		return repoFile;
	}


	public void setRepoFile(RepositoryFile repoFile) {
		this.repoFile = repoFile;
	}

	public List<Configuration> getListConf() {
		return listConf;
	}

	public void setListConf(List<Configuration> listConf) {
		this.listConf = listConf;
	}

}
