package io.github.hiperdeco.docsfinder.main;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class TesteTikaLerDocMetadata {

	public static void main(String[] args) {
		Tika tika = new Tika();
		long time = System.currentTimeMillis();
		File file = new File("/home/andre/Documentos/UC-ManterAgendamento.doc");
		try {
			String content = tika.parseToString(file);
			System.out.println(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Tempo: " + (System.currentTimeMillis() - time));
		System.exit(0);
	}

}
