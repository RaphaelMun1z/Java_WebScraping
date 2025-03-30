package main.config;

import org.openqa.selenium.firefox.FirefoxOptions;

public class BrowserConfig {
	private final String downloadDir;

	public BrowserConfig(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	public FirefoxOptions getFirefoxOptions() {
		FirefoxOptions options = new FirefoxOptions();
		options.addPreference("browser.download.folderList", 2);
		options.addPreference("browser.download.dir", downloadDir);
		options.addPreference("browser.download.useDownloadDir", true);
		options.addPreference("browser.download.viewableInternally.enabledTypes", "");
		options.addPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/pdf;text/plain;application/text;text/xml;application/xml");
		options.addPreference("pdfjs.disabled", true);
		return options;
	}
}