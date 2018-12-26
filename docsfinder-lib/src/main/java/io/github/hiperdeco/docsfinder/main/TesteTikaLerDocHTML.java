package io.github.hiperdeco.docsfinder.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ExpandedTitleContentHandler;
import org.apache.tika.parser.Parser;
public class TesteTikaLerDocHTML {

	public static void main(String[] args) {

		
		long time = System.currentTimeMillis();
		File file = new File("/home/andre/Documentos/UC-ManterAgendamento.doc");
		InputStream input = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
		input = TikaInputStream.get(new FileInputStream(file));
		
		
		SAXTransformerFactory factory = (SAXTransformerFactory)
		 SAXTransformerFactory.newInstance();
		TransformerHandler handler = factory.newTransformerHandler();
		handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
		handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
		handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		handler.setResult(new StreamResult(out));
		ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
		 
		Parser parser = new AutoDetectParser();
		
		
		
			//String content = new Tika().parseToString(file);
			//System.out.println(content);
			parser.parse(input, handler1, new Metadata(), new ParseContext());
			System.out.println(new String(out.toByteArray(), "UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("Tempo: " + (System.currentTimeMillis() - time));
		System.exit(0);
	}

}
