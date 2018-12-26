package io.github.hiperdeco.docsfinder.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.usermodel.Range;

public class TesteLerDoc {

	public static void main(String[] args) {
		try {
			long time = System.currentTimeMillis();
			File file = new File("/home/andre/Documentos/UC-ManterAgendamento.doc");
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			HWPFDocument doc = new HWPFDocument(fis);
			
			//WordExtractor extract = new WordExtractor(doc);
			//String text = doc.getDocumentText();
			
			StyleSheet stl = doc.getStyleSheet();
			
			Range range = doc.getRange();
			System.out.println(range.getParagraph(119).text());
			System.out.println(range.getParagraph(119).getLvl());
			System.out.println(stl.getStyleDescription(range.getParagraph(119).getStyleIndex()).getName());
			
			
			
			doc.close();
			System.out.println("Tempo: " + (System.currentTimeMillis() - time));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);

	}

}
