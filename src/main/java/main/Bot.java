package main;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Bot {
	private final WebDriver driver;
	private final JavascriptExecutor js;
	private List<String> interactedFilesNames = new ArrayList<>();

	public Bot(WebDriver driver) {
		this.driver = driver;
		this.js = (JavascriptExecutor) driver;
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

	public List<String> getInteractedFilesNames() {
		return interactedFilesNames;
	}
}
