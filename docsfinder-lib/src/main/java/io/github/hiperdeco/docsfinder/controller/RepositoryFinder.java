package io.github.hiperdeco.docsfinder.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.tika.Tika;

import io.github.hiperdeco.docsfinder.constants.Properties;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryStatus;
import io.github.hiperdeco.docsfinder.exception.FinderException;
import io.github.hiperdeco.docsfinder.exception.FinderException.Type;
import io.github.hiperdeco.docsfinder.vo.ContentFoundVO;
import io.github.hiperdeco.docsfinder.vo.SearchVO;

public class RepositoryFinder implements Serializable {

	private static Logger log = Logger.getLogger(RepositoryFinder.class);
	
	private static final long serialVersionUID = -8496004951943233241L;

	private Repository repository;
	
	private Tika tika = new Tika();
	
	public Repository getRepository() {
		return repository;
	}
	
	public RepositoryFinder() {
		
	}
	public RepositoryFinder(Repository repository) {
		this.setRepository(repository);
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public List<ContentFoundVO> find(SearchVO search) throws FinderException, Exception  {
		
		if (this.getRepository().getStatus().equals(RepositoryStatus.EMPTY)) {
			log.error("Repository " + this.getRepository().toString() + " is empty");
			throw new FinderException("Repository not Indexed", Type.REPOSITORY_EMPTY);
		}
			
		List<ContentFoundVO> result = new ArrayList<ContentFoundVO>();
		
		
		//searchEngine
		DirectoryReader reader = null;
		Directory dir = null;
		Analyzer analyzer = null;
		try {
			
			try {
				dir = NIOFSDirectory.open(new File(getIndexPath()+this.getRepository().getId()+"_"+this.getRepository().getIndexSequence()).toPath());
				reader = DirectoryReader.open(dir);
			}catch (Exception e) {
				if (this.getRepository().getStatus().equals(RepositoryStatus.INDEXING)) {
					log.error("Repository " + this.getRepository().toString() + " is indexing");
					throw new FinderException("Repository is Indexing", Type.REPOSITORY_EMPTY);
				}
				throw new FinderException(e,Type.REPOSITORY_ERROR);
			}
			
			analyzer = new StandardAnalyzer();
			//replace first character ? or *
			search.setTerm(search.getTerm().replaceFirst("^[*|?]+", ""));
			TopDocs hitsFile = null;
			if(search.isFindPath()) {
				IndexSearcher searchIndexFile = new IndexSearcher(reader);
				Query queryFile = new TermQuery(new Term("path",search.getTerm()));
				int count = reader.getDocCount("path");
				
				String termRegexFile = search.getTerm().replaceAll("[*,?,+,-]", "|").replaceAll(" ", "|");
				String pattern = "^.*(" + termRegexFile + ").*$";
				 // Create a Pattern object
	            Pattern rf = Pattern.compile(pattern,Pattern.MULTILINE| Pattern.CASE_INSENSITIVE);
				for (int i = 0; i < count; i++)
		        {
		            Document docx = searchIndexFile.doc(i);
		            String path = docx.get("path"); 
		            //get the matches
		            Matcher mf = rf.matcher(path);
		            if(mf.find()) {

			            String type = docx.get("mimeType");
			            String extension = docx.get("extension");
			            String fileName = docx.get("fileName");
			            
			            ContentFoundVO found = new ContentFoundVO();
			            found.setPath(path);
			            found.setPathDescription(path.replace(repository.getLocalDirectory(), "").replaceAll("(?i)("+termRegexFile+")", "<b>$1</b>"));
			            found.setType(type);
			            found.setExtension(extension);
			            found.setFileName(fileName);
			            result.add(found);
		            }
		            
		        }
			}
			
			QueryParser queryParser = new QueryParser("content", analyzer);
			IndexSearcher searchIndex = new IndexSearcher(reader);
			Query query = queryParser.parse(search.getTerm());
			
			TopDocs hits = searchIndex.search(query, Integer.MAX_VALUE);
			
			//if no hits, ends here
			if (hits.scoreDocs.length == 0 &&result.isEmpty()) throw new FinderException("Not Found", Type.NOT_FOUND);
			
			//org.apache.lucene.search.PhraseQuery (unica frase)
			//org.apache.lucene.search.TermQuery (uma palavra)
			//org.apache.lucene.search.PrefixQuery (uma palavra*)
			//org.apache.lucene.search.WildcardQuery (uma palavra*outra)
			//org.apache.lucene.search.BooleanQuery (mais de uma palavra)
			
			String termRegex = search.getTerm();
			switch (query.getClass().getName()) {
			case "org.apache.lucene.search.WildcardQuery":
				termRegex = search.getTerm().replaceAll("[*,?,+,-]", "|").replaceAll(" ", "|");
				break;
			case "org.apache.lucene.search.BooleanQuery":
				termRegex = search.getTerm().replaceAll("[*,?,+,-]", "|").replaceAll(" ", "|");
				break;
			case "org.apache.lucene.search.PrefixQuery":
				termRegex = search.getTerm().replaceAll("[*,?,+,-]", "|").replaceAll(" ", "|");
				break;
			}
			
			String pattern = "^.*(" + termRegex + ").*$";
			 // Create a Pattern object
            Pattern r = Pattern.compile(pattern,Pattern.MULTILINE| Pattern.CASE_INSENSITIVE);

	
			for (int i = 0; i < hits.scoreDocs.length; i++)
	        {
	            int docid = hits.scoreDocs[i].doc;
	            Document docx = searchIndex.doc(docid);
	            String path = docx.get("path");
	            
	            //Get stored text from found document
	            String text = docx.get("content");//tika.parseToString(file);
	            String type = docx.get("mimeType");
	            String extension = docx.get("extension");
	            String fileName = docx.get("fileName");
	            
	            //get the matches
	            Matcher m = r.matcher(text);
	            List<String> textMatchs = new ArrayList<String>();
	            int maxLines = Integer.parseInt(System.getProperty("maxLines","10"));
	            int count = 0;
	            while (m.find( )) {
	            	try {
	            		textMatchs.add(m.group().replaceAll("(?i)("+termRegex+")", "<b>$1</b>"));
	            	}catch(Exception e) {
	            		textMatchs.add(m.group());
	            	}
	            	count++;
	            	if (count >= maxLines) {
	            		textMatchs.add("<b>...</b>");
	            		break;
	            	}
	            		
	            }
	            
	            ContentFoundVO found = new ContentFoundVO();
	            found.setPath(path);
	            found.setPathDescription(path.replace(repository.getLocalDirectory(), "").replaceAll("(?i)("+termRegex+")", "<b>$1</b>"));
	            found.setType(type);
	            found.setContent(textMatchs);
	            found.setExtension(extension);
	            found.setFileName(fileName);
 
	            if (search.isFindPath() && result.contains(found)) {
	            	result.remove(found);
	            }
	            
	            result.add(found);
	            
	        }
			log.info(result.size() + " results found.");
		}catch(Exception e) {
			throw e;
		}finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {	}
			}
			if (dir != null){
				try {
					dir.close();
				} catch (Exception e) { }
			}
			if (analyzer != null){
				try {
					analyzer.close();
				} catch (Exception e) {	}
			}
		}
		return result;
	}
	
	private List<String> filesPathFound = null;
	
	private void addFilePath(Object path) {
		if (filesPathFound == null) filesPathFound =  new ArrayList<String>();
		filesPathFound.add(path.toString());
	}
	
	public List<ContentFoundVO> findFilesPath(SearchVO search) throws FinderException, Exception  {
		
		List<ContentFoundVO> result = new ArrayList<ContentFoundVO>();
		filesPathFound =  new ArrayList<String>();
		try {
			File localDirectory = new File(this.getRepository().getLocalDirectory());
			Files.walk(Paths.get(localDirectory.toURI()))
	        .filter(Files::isRegularFile)
			        .forEach(this::addFilePath);			
		} catch (IOException e) {
			throw e;
		}
		String term = removeWildCards(search.getTerm());
		
		long count = 1;
		for (String path: filesPathFound) {
			if (path.toLowerCase().contains(term.toLowerCase())) {
				//File f = new File(path);
				ContentFoundVO item = new ContentFoundVO();
				item.setId(count);
				item.setPath(path);
				item.setType(tika.detect(path));
				result.add(item);
				count++;
			}
		}
		filesPathFound = null;
		return result;
	}
	
	
	private String removeWildCards(String term) {
		
		return term.replaceAll(".[*,?,+,-]","");
		
	}
	
	private String getIndexPath() {
		String result = ConfigurationManager.getValue(this.repository.getId(), "INDEX_PATH");
		if (result == null || result.isEmpty()) {
			result = Properties.get("indexPath", System.getProperty("java.io.tmpdir"));
			if (!result.endsWith("/")) {
				result += "/";
			}
		}
		return 	result;
	}
}
