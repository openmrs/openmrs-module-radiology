import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver



System.out.println("Runnng Tests with: "+System.getProperty('geb.env'))
//baseUrl will be set with the System-Property: 'geb.build.baseUrl'


waiting {
    timeout = 30
    retryInterval = 1.0
    includeCauseInMessage = true
}

environments {

	// when system property 'geb.env' is set to 'chrome' use the Chrome-Webdriver
	'chrome' {
		driver = { new ChromeDriver() }
	}

	// when system property 'geb.env' is set to 'phantomJs' use the phantomJs-Webdriver
	phantomJs {
		driver = { new PhantomJSDriver() }
	}

}
