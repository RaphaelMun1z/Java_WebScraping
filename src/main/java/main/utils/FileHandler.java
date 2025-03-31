package main.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileHandler {
	public static void moveFilesToZip(String standardBasePath, List<String> interactedFilesNames, String zipPath) {
		// Cria uma cópia de targetFileNames
		List<String> notFoundFiles = new ArrayList<>(interactedFilesNames);

		// Enquanto houver arquivos a serem encontrados, continua monitorando
		while (!notFoundFiles.isEmpty()) {
			Iterator<String> iterator = notFoundFiles.iterator();

			while (iterator.hasNext()) {
				String filename = iterator.next();
				if (verifyIfFileWasDownloaded(filename, standardBasePath)) {
					iterator.remove();
					System.out.println("\nBaixou arquivo: " + filename);
					File file = new File(standardBasePath, filename);
					Compactor.addFileToZipFolder(file, zipPath);
				}
			}
			sleep(2);
		}
	}

	private static boolean verifyIfFileWasDownloaded(String targetFileName, String basePath) {
		File targetFile = new File(basePath, targetFileName);

		if (targetFile.exists() && !hasRelatedPartFile(targetFileName, basePath))
			return true;

		return false;
	}

	private static boolean hasRelatedPartFile(String filename, String bPath) {
		File basePath = new File(bPath);
		String filenameBase = filename.split("\\.")[0];

		File[] partFiles = basePath
				.listFiles((dir, name) -> name.endsWith(".part") && name.split("\\.")[0].equals(filenameBase));

		return partFiles != null && partFiles.length > 0;
	}
	
	private static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
