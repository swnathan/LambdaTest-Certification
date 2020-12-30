package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import helper.GlobalVariables;
import net.serenitybdd.core.annotations.findby.By;
import pageobjects.LoginPage;
import pageobjects.MainPage;

public class ShoppingTest {

	private static WebDriver driver;
	Logs logs;
	LogEntries logEntries;
	String destinationFile = "";
	String destinationFileName = "";
	URL server;

	@BeforeTest
	public void beforeTest() throws MalformedURLException {

		server = new URL(GlobalVariables.remoteURL);
	}

	@Test(dataProvider = "Environment")
	public void testChrome(Platform platform, String browserName, String browserVersion)
			throws InterruptedException, IOException {
		try {

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("build", "build");
			capabilities.setCapability("name", "test");
			capabilities.setCapability("platform", platform);
			capabilities.setCapability("browserName", browserName);
			capabilities.setCapability("version", browserVersion);
			capabilities.setCapability("console", true);
			capabilities.setCapability("network", true);
			driver = new RemoteWebDriver(server, capabilities);
			((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
			driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			driver.get(GlobalVariables.applicationURL);
			LoginPage loginpage = new LoginPage(driver);
			loginpage.waitFor(loginpage.getUserName());
			loginpage.getUserName().sendKeys(GlobalVariables.userName);
			loginpage.waitFor(loginpage.getPassword());
			loginpage.getPassword().sendKeys(GlobalVariables.password);
			loginpage.getCookiesLabel("I ACCEPT").waitUntilVisible();
			loginpage.getCookiesLabel("I ACCEPT").click();
			loginpage.getSubmit().waitUntilVisible();
			loginpage.getSubmit().click();
			waitForPageToLoad(loginpage.getDriver());
			MainPage mainPage = new MainPage(driver);
			mainPage.waitFor(mainPage.getEmail());
			mainPage.getEmail().sendKeys(GlobalVariables.email);
			mainPage.getPopulate().waitUntilVisible();
			mainPage.getPopulate().click();
			mainPage.getDriver().switchTo().alert().accept();
			mainPage.getFrequencyRadio("Every month").waitUntilVisible();
			mainPage.getFrequencyRadio("Every month").click();
			Actions actions = new Actions(driver);
			actions.moveToElement(mainPage.getDecisiveFactors("customer-service"));
			actions.clickAndHold();
			actions.moveToElement(mainPage.getDecisiveFactors("customer-service"));
			actions.release().perform();
			actions.moveToElement(mainPage.getPaymentMode("Wallets"));
			actions.clickAndHold();
			actions.moveToElement(mainPage.getPaymentMode("Wallets"));
			JavascriptExecutor js = (JavascriptExecutor) mainPage.getDriver();
			js.executeScript("window.scrollBy(0,100)");
			mainPage.getECommercePurchaseCheckbox().waitUntilVisible();
			mainPage.getECommercePurchaseCheckbox().click();
			js.executeScript("window.scrollBy(0,100)");
			new Actions(driver).dragAndDropBy(mainPage.getSlider(), 500, 0).build().perform();
			System.out.println(mainPage.getSlider().getAttribute("style"));
			Assert.assertTrue(mainPage.getSlider().getAttribute("style").equalsIgnoreCase("left: 88.8889%;"),
					"Rating 9 scale is not selected");
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.open()");
			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
			mainPage.getDriver().switchTo().window(tabs.get(1));
			mainPage.getDriver().get(GlobalVariables.seleniumAutomationURL);
			waitForPageToLoad(mainPage.getDriver());
			js.executeScript("arguments[0].scrollIntoView();", mainPage.getJenkinsLogo());
			String logo = mainPage.getJenkinsLogo().getAttribute("src");
			URL imageURL = new URL(logo);
			saveImage(imageURL);
			mainPage.getDriver().switchTo().window(tabs.get(0));
			mainPage.getComments().waitUntilVisible();
			mainPage.getComments().sendKeys("feedback");
			js.executeScript("window.scrollBy(0,500)");
			File file = new File(destinationFile);
			String absolute = file.getAbsolutePath();
			WebElement uploadElement = driver.findElement(By.xpath("//input[@id='file']"));
			((JavascriptExecutor) mainPage.getDriver()).executeScript("arguments[0].removeAttribute('style')",
					uploadElement);
			uploadElement.sendKeys(absolute);
			Alert alert = driver.switchTo().alert();
			alert.getText();
			Assert.assertTrue(alert.getText().contains("your image upload sucessfully!!"),
					"Image not uploaded successfully");
			Assert.assertTrue(file.getName().equalsIgnoreCase(destinationFileName),
					"Image Name uploaded/Download is different");
			alert.accept();
			mainPage.getSubmit().waitUntilVisible();
			mainPage.getSubmit().click();
			mainPage.waitForTextToAppear("You have successfully submitted the form");
		} catch (IOException IOException) {
			IOException.printStackTrace();
		}

	}

	public void saveImage(URL url) {
		destinationFile = "driver/jenkins.svg";
		File file = new File(destinationFile);
		destinationFileName = file.getName();
		file.getAbsolutePath();
		InputStream is;
		try {
			is = url.openStream();
			OutputStream os = new FileOutputStream(destinationFile);
			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void waitForPageToLoad(WebDriver driver) {
		ExpectedCondition<Boolean> javascriptDone = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				} catch (Exception e) {
					return Boolean.FALSE;
				}
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(javascriptDone);
	}

	@DataProvider(name = "Environment", parallel = false)
	public Object[][] getData() {

		Object[][] Browser_Property = new Object[][] {

				{ Platform.WIN10, "chrome", "88.0" }, { Platform.HIGH_SIERRA, "Firefox", "84.0" } };
		return Browser_Property;

	}

	@AfterTest
	public void afterTest() {
		driver.quit();

	}

}