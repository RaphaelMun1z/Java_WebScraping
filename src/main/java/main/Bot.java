package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
		} catch (Exception e) {
			System.err.println("Ocorreu o seguinte erro: " + e.getMessage());
		}
	}

	public void targetAllFiles(List<String> targetFileNames, String zipPath) {
		List<String> notFoundFiles = new ArrayList<>(targetFileNames);

		while (!notFoundFiles.isEmpty()) {
	        Iterator<String> iterator = notFoundFiles.iterator();
	        
	        while (iterator.hasNext()) {
	            String filename = iterator.next();
	            if (verifyIfFileWasDownloaded(filename, standardBasePath)) {
	                iterator.remove();
	                System.out.println("Baixou arquivo: " + filename);
	                File file = new File(standardBasePath, filename);
					Compactor.addFileToZipFolder(file, zipPath);
	            }
	        }
	        sleep(2);
		}
	}

	public boolean verifyIfFileWasDownloaded(String targetFileName, String targetPath) {
		File targetFile = new File(targetPath, targetFileName);

		if (targetFile.exists() && !hasRelatedPartFile(targetFileName))
			return true;

		return false;
	}
	
	private boolean hasRelatedPartFile(String filename) {
	    File basePath = new File(standardBasePath);
	    String filenameBase = filename.split("\\.")[0];
	    
	    File[] partFiles = basePath.listFiles((dir, name) -> 
	        name.endsWith(".part") && name.split("\\.")[0].equals(filenameBase)
	    );

	    return partFiles != null && partFiles.length > 0;
	}
}
