package main;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import main.config.BrowserConfig;

public class Main {
	public static void main(String[] args) {
		System.out.println("Bot iniciou suas tarefas.");

		final String baseDir = "C:\\PDFs";
		final String zipDir = "C:\\PDFs\\Anexos.zip";

		BrowserConfig config = new BrowserConfig(baseDir);
		FirefoxOptions options = config.getFirefoxOptions();

		WebDriver driver = new FirefoxDriver(options);
		String siteUrl = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
		Bot downloaderBot = new Bot(driver, baseDir, zipDir);

		final String xpathReference = "/html/body/div[2]/div[1]/main/div[2]/div/div/div/div/div[2]/div/ol/";

		// Abre uma janela no navegador
		driver.get(siteUrl);

		downloaderBot.downloadByXpath(String.format("%sli[1]/a[1]", xpathReference));
		downloaderBot.downloadByXpath(String.format("%sli[2]/a", xpathReference));

		try {
			downloaderBot.verifyIfAllFilesHasBeenDownloaded();
		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}

		driver.quit();

		System.out.println("Bot finalizou suas tarefas.");
	}
}
