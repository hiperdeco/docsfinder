package io.github.hiperdeco.docsfinder.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

public class TesteTikaLerDoc {

	public static void main(String[] args) {
		Tika tika = new Tika();
		long time = System.currentTimeMillis();
		File file = new File("/home/andre/Documentos/UC-ManterAgendamento.doc");
		
		
		try {
			//Parser method parameters
		      Parser parser = new AutoDetectParser();
		      BodyContentHandler handler = new BodyContentHandler();
		      Metadata metadata = new Metadata();
		      FileInputStream inputstream = new FileInputStream(file);
		      ParseContext context = new ParseContext();
		      
		      parser.parse(inputstream, handler, metadata, context);
		      //System.out.println(handler.toString());

		      //getting the list of all meta data elements 
		      String[] metadataNames = metadata.names();

		      for(String name : metadataNames) {		        
		         System.out.println(name + ": " + metadata.get(name));
		      }
		      
		      //Last-Author: Lucas Gouveia Bacelar
		      //modified: 2018-04-12T15:29:00Z
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Tempo: " + (System.currentTimeMillis() - time));
		System.exit(0);
	}

}
