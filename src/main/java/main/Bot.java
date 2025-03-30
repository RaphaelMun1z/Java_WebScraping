package main;

import java.io.File;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.utils.Compactor;

public class Bot {
	private final WebDriver driver;
	private final String downloadDir;
	private final String zipDir;
	private final JavascriptExecutor js;

	public Bot(WebDriver driver, String downloadDir, String zipDir) {
		this.driver = driver;
		this.downloadDir = downloadDir;
		this.zipDir = zipDir;
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

	public void verifyIfAllFilesHasBeenDownloaded() throws Exception {
		// Diretório onde se encontram os arquivos
		File dir = new File(downloadDir);

		// Tempo limite de espera pelo download
		long waitLimitTime = System.currentTimeMillis() + 25000;

		Compactor compactor = new Compactor(zipDir);

		// Enquanto estiver dentro do limite de tempo, realizar verificações
		while (System.currentTimeMillis() < waitLimitTime) {
			// Armazena os arquivos sendo baixados (contém extensão '.part', no caso do
			// firefox)
			File[] downloadingFiles = dir.listFiles((dir1, name) -> name.endsWith(".part"));

			// Armazena o nome formatado dos arquivos sendo baixados
			Set<String> downloadingSet = new HashSet<>();

			for (File file : downloadingFiles) {
				// Divide o nome do arquivo onde tiver '.', pois é criado uma espécie de hash no
				// nome do arquivo que contém extensão '.part'
				String[] parts = file.getName().split("\\.");
				if (parts.length > 2) {
					StringBuilder formattedFileName = new StringBuilder(parts[0]);
					// Formata o nome do arquivo
					for (int ii = 2; ii < (parts.length - 1); ii++)
						formattedFileName.append(".").append(parts[ii]);

					downloadingSet.add(formattedFileName.toString());
				}
			}

			File[] downloadedFiles = dir
					.listFiles((dir1, name) -> name.endsWith(".pdf") && !downloadingSet.contains(name));

			// Ao finalizar o download do arquivo, já realizo a transferência para a pasta
			// compactada
			for (File f : downloadedFiles) {
				compactor.addFileToZipFolder(f);
			}

			System.out.println("Há " + downloadingFiles.length + " arquivos sendo baixados...\n");
			if (downloadingSet.size() == 0 && downloadedFiles != null && downloadedFiles.length > 0) {
				System.out.println("Todos os arquivos foram baixados com sucesso!");
				return;
			}

			sleep(1);
		}

		throw new Exception("Tempo máximo atingido!");
	}
}
