package io.github.hiperdeco.docsfinder.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.github.hiperdeco.docsfinder.constants.Constants;

public class TesteFileWalker {
	
	private static List<String> files = new ArrayList<String>();
	public static void addFilePath(Object path) {
		//String[] pathAux = path.toString().split("\\.");
		//String[] fileName = path.toString().split(File.separator);
		//if (pathAux.length > 1 && Constants.FILE_EXTENSIONS.contains(pathAux[pathAux.length - 1].toLowerCase())) {	
			//System.out.println(fileName[fileName.length -1 ]);
		files.add(path.toString());
		  if (path.toString().contains("README")) {
			
			System.out.println(path.toString());
		}
	}
	
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		try {
		
			File localDirectory = new File("/home/andre/");
			/*Files.find(Paths.get(localDirectory.toURI()),
			           Integer.MAX_VALUE,
			           (filePath, fileAttr) -> fileAttr.isRegularFile()) */
			Files.walk(Paths.get(localDirectory.toURI()))
	        .filter(Files::isRegularFile)
			        .forEach(TesteFileWalker::addFilePath);			
			System.out.println("Total: " + files.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long totalTime = System.currentTimeMillis() - time;
		System.out.println("Tempo: " + totalTime);
		System.exit(0);
	}

}
