package io.github.hiperdeco.docsfinder.main;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.tika.Tika;

public class TesteIndexLuceneTika {

	public static void main(String[] args) {

		IndexWriter writer = null;
		DirectoryReader reader = null;
		Directory dir = null;
		try {
			// armazenar
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			config.setOpenMode(OpenMode.CREATE);
			
			//dir = new SimpleFSDirectory(new File("/tmp/ab").toPath());
			dir = NIOFSDirectory.open(new File("/tmp/ab").toPath());
			writer = new IndexWriter(dir,config ); 
			
			
			
			// LuceneIndexer indexer = new LuceneIndexer(new Tika(), writer);
			// for (int i = 1; i < args.length; i++) {
			// indexer.indexDocument(new File(args[i]));
			// }
			Document doc = new Document();
			FieldType ftype = new FieldType();
			ftype.setStored(false);
			ftype.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			
			FieldType ftype2 = new FieldType();
			ftype2.setStored(true);
			ftype2.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			
		    //long cont  = writer.deleteDocuments();
			//writer.flush();
			//writer.commit();
			//System.out.println("deleted: " + cont);
			File file = new File("/home/andre/Documentos/UC-ManterAgendamento.doc");
			Tika tika = new Tika();
			doc.add(new Field("path", file.getAbsolutePath(), ftype2));
			doc.add(new Field("type",tika.detect(file),ftype2));
			
			doc.add(new Field("content",
					tika.parse(file),ftype));
			writer.addDocument(doc);
			writer.close();
			writer = null;
			
			long time = System.currentTimeMillis();
			// pesquisar
			reader = DirectoryReader.open(dir);
			IndexSearcher search = new IndexSearcher(reader);
			//Query query = new TermQuery(new Term("path", "%manter%"));//new TermQuery(new Term("content", "manter"));
			
			Analyzer analyzer = new StandardAnalyzer();
			//QueryParser queryp = new MultiFieldQueryParser((new String[]{"path","content"}), analyzer);
			QueryParser queryp = new QueryParser("content",analyzer);
			Query query = queryp.parse("manter Agendamento ne*");
			
			//org.apache.lucene.search.PhraseQuery (unica frase)
			//org.apache.lucene.search.TermQuery (uma palavra)
			//org.apache.lucene.search.PrefixQuery (uma palavra*)
			//org.apache.lucene.search.WildcardQuery (uma palavra*outra)
			//org.apache.lucene.search.BooleanQuery (mais de uma palavra)

			
			
			
			TopDocs hits = search.search(query, Integer.MAX_VALUE);
			
			System.out.println(hits.totalHits);
			//System.out.println(search.doc(2).getField("content").stringValue());
			System.out.println("Tempo: " + (System.currentTimeMillis() - time));
			  System.out.println(query.getClass().getName());

			
			for (int i = 0; i < hits.scoreDocs.length; i++)
	        {
	            int docid = hits.scoreDocs[i].doc;
	            Document docx = search.doc(docid);
	            String title = docx.get("path");
	             
	            //Printing - to which document result belongs
	            System.out.println("Path " + " : " + title);
	            System.out.println("Type " + " : " + docx.get("type"));
	             
	            //Get stored text from found document
	            String text = tika.parseToString(file);
	            //String text = docx.get("content");
	 
	            //Create token stream
	           // TokenStream stream = TokenSources.getAnyTokenStream(reader, docid, "content", analyzer);
	             
	            //Get highlighted text fragments
	            //String[] frags = highlighter.getBestFragments(stream, text, 100);
	            String pattern = "^.*(Satu|fttx).*$";
	            
	          

	            // Create a Pattern object
	            Pattern r = Pattern.compile(pattern,Pattern.MULTILINE| Pattern.CASE_INSENSITIVE);

	            // Now create matcher object.
	            Matcher m = r.matcher(text);
	            while (m.find( )) {
	            	System.out.println("=======================");
	            	System.out.println(m.group());
	            }
	        
	        }
			
			search.getIndexReader().close();
			reader.close();
			reader = null;
			System.out.println("Tempo: " + (System.currentTimeMillis() - time));
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.exit(0);

	}

}
