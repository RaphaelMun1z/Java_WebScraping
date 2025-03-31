package main;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import main.config.BrowserConfig;

public class Main {
	public static void main(String[] args) {
		System.out.println("Bot iniciou suas tarefas.");

		final String standardBasePath = "C:\\PDFs";
		final String zipPath = standardBasePath + "\\Anexos.zip";

		BrowserConfig config = new BrowserConfig(standardBasePath);
		FirefoxOptions options = config.getFirefoxOptions();

		String siteUrl = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";

		WebDriver driver = new FirefoxDriver(options);
		Bot downloaderBot = new Bot(driver, standardBasePath);

		final String xpathReference = "/html/body/div[2]/div[1]/main/div[2]/div/div/div/div/div[2]/div/ol/";

		// Abre uma janela no navegador
		driver.get(siteUrl);

		// Baixa anexo
		downloaderBot.downloadByXpath(String.format("%sli[1]/a[1]", xpathReference));

		// Baixa anexo
		downloaderBot.downloadByXpath(String.format("%sli[2]/a", xpathReference));

		// Move arquivos para o ZIP
		// downloaderBot.moveFilesToZip(zipPath);

		try {
			List<String> filenames = new ArrayList<>();
			filenames.add("Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf");
			filenames.add("Anexo_II_DUT_2021_RN_465.2021_RN628.2025_RN629.2025.pdf");
			downloaderBot.targetAllFiles(filenames, zipPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		driver.quit();

		System.out.println("Bot finalizou suas tarefas.");
	}
}
