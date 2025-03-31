package main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import main.config.BrowserConfig;
import main.utils.FileHandler;

public class Main {
	public static void main(String[] args) {
		System.out.println("Bot iniciou suas tarefas.");

		final String standardBasePath = "C:\\PDFs";
		final String zipPath = String.format("%s\\Anexos.zip", standardBasePath);

		BrowserConfig config = new BrowserConfig(standardBasePath);
		FirefoxOptions options = config.getFirefoxOptions();

		String siteUrl = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";

		WebDriver driver = new FirefoxDriver(options);
		Bot downloaderBot = new Bot(driver);

		final String xpathReference = "/html/body/div[2]/div[1]/main/div[2]/div/div/div/div/div[2]/div/ol/";

		// Abre uma janela no navegador
		driver.get(siteUrl);

		// Baixa anexo
		downloaderBot.downloadByXpath(String.format("%sli[1]/a[1]", xpathReference));

		// Baixa anexo
		downloaderBot.downloadByXpath(String.format("%sli[2]/a", xpathReference));

		// Move os arquivos, neste caso os anexos, para um ZIP
		FileHandler.moveFilesToZip(standardBasePath, downloaderBot.getInteractedFilesNames(), zipPath);

		driver.quit();

		System.out.println("\nBot finalizou suas tarefas.");
	}
}
