package io.github.hiperdeco.docsfinder.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.ExpandedTitleContentHandler;

import io.github.hiperdeco.docsfinder.entity.Repository;

public class FileUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9177011636181185928L;
	
	private static Logger log = Logger.getLogger(FileUtil.class);

	//future release
	public static String getContentsHTML(String path) {
		File file = new File(path);
		InputStream input = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String result = "";
		try {
			input = TikaInputStream.get(new FileInputStream(file));

			SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			TransformerHandler handler = factory.newTransformerHandler();
			handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
			handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
			handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			handler.setResult(new StreamResult(out));
			ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);

			Parser parser = new AutoDetectParser();
			parser.parse(input, handler1, new Metadata(), new ParseContext());
			result = new String(out.toByteArray(), "UTF-8");
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.debug(e.getMessage(),e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.debug(e.getMessage(),e);
				}
			}
		}
		return result;
	}

	public static String getContentsHTML(long repositoryId, String path) {
		Repository repo = (Repository) JPAUtil.findById(Repository.class, repositoryId);
		String newPath = repo.getLocalDirectory() + path;
		return getContentsHTML(newPath);
	}

}
