package main;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.utils.Compactor;

public class Bot {
	private final WebDriver driver;
	private final String standardBasePath;
	private final JavascriptExecutor js;
	private List<String> interactedFilesNames = new ArrayList<>();

	public Bot(WebDriver driver, String standardBasePath) {
		this.driver = driver;
		this.standardBasePath = standardBasePath;
		this.js = (JavascriptExecutor) driver;
	}

	private void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	private void scrollToElement(WebElement element) {
		js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'});",
				element);
	}

	private void clickElement(WebElement element) {
		js.executeScript("arguments[0].click();", element);
	}

	public void downloadByXpath(String elementXpath) {
		try {
			// Espera durante o período máximo de 20 segundos
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

			// Durante esse período tenta encontrar o elemento com base em seu Xpath
			WebElement downloadButtonElement = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath(elementXpath)));

			// Realiza o scroll até o elemento
			scrollToElement(downloadButtonElement);

			// Clica no elemento
			clickElement(downloadButtonElement);

			// Adiciona o nome do arquivo a lista de downloads
			String href = downloadButtonElement.getAttribute("href");
			String fileName = href.substring(href.lastIndexOf("/") + 1);
			interactedFilesNames.add(fileName);
		} catch (Exception e) {
			System.err.println("Ocorreu o seguinte erro: " + e.getMessage());
		}
	}

	public void moveFilesToZip(String zipPath) {
		// Cria uma cópia de targetFileNames
		List<String> notFoundFiles = new ArrayList<>(interactedFilesNames);

		// Enquanto houver arquivos a serem encontrados, continua monitorando
		while (!notFoundFiles.isEmpty()) {
			Iterator<String> iterator = notFoundFiles.iterator();

			while (iterator.hasNext()) {
				String filename = iterator.next();
				if (verifyIfFileWasDownloaded(filename)) {
					iterator.remove();
					System.out.println("\nBaixou arquivo: " + filename);
					File file = new File(standardBasePath, filename);
					Compactor.addFileToZipFolder(file, zipPath);
				}
			}
			sleep(2);
		}
	}

	private boolean verifyIfFileWasDownloaded(String targetFileName) {
		File targetFile = new File(standardBasePath, targetFileName);

		if (targetFile.exists() && !hasRelatedPartFile(targetFileName))
			return true;

		return false;
	}

	private boolean hasRelatedPartFile(String filename) {
		File basePath = new File(standardBasePath);
		String filenameBase = filename.split("\\.")[0];

		File[] partFiles = basePath
				.listFiles((dir, name) -> name.endsWith(".part") && name.split("\\.")[0].equals(filenameBase));

		return partFiles != null && partFiles.length > 0;
	}
}
