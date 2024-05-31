package org.seconf22;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.virtualauthenticator.Credential;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions.Protocol;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions.Transport;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class WebAuthNGridTest {

    private static WebDriver driver;
    private static VirtualAuthenticator virtualAuthenticator;
    private static List<Credential> registeredCredentials;

    @BeforeTest
    static void setup() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.setCapability("browserName", "chrome");

        // Initialize WebDriver using RemoteWebDriver to connect to the Selenium Grid hub
        URL gridUrl = new URL("http://localhost:4444/wd/hub");
        driver = new RemoteWebDriver(gridUrl, options);
    }

    @Test(priority = 1)
    void testPasskeyEnrollmentAndLogin() throws InterruptedException {
        // Set up the virtual authenticator for WebAuthn testing
        virtualAuthenticator = setupVirtualAuthenticator();

        // Navigate to the enrollment page
        driver.get("https://webauthn.io");
        driver.findElement(By.id("input-email")).sendKeys("seconf22");

        // Initiate enrollment process
        driver.findElement(By.className("btn-secondary")).click();

        // Select enrollment options
        Select selectAttestationType = new Select(driver.findElement(By.id("attestation")));
        selectAttestationType.selectByVisibleText("Direct");

        Select selectAuthenticatorType = new Select(driver.findElement(By.id("attachment")));
        selectAuthenticatorType.selectByVisibleText("Platform");

        driver.findElement(By.id("register-button")).click();

        // Verify enrollment success
        Thread.sleep(3000);
        assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Success!')]")).isDisplayed());

        // Start authentication process
        driver.findElement(By.id("login-button")).click();

        // Verify authentication success
        Thread.sleep(3000);
        assertTrue(driver.findElement(By.xpath("//h3[text()=\"You're logged in!\"]")).isDisplayed());

        // Retrieve registered credentials
        registeredCredentials = virtualAuthenticator.getCredentials();

        virtualAuthenticator.removeAllCredentials();

        // Retrieve registered credentials
//        registeredCredentials = virtualAuthenticator.getCredentials();

        // Optionally, perform further validation or logging of registered credentials

        // Logout or navigate to the logout page
        // Example: driver.findElement(By.id("logout-button")).click();
    }

    //    @Test(priority = 2)
    void testPasskeyLoginWithPreviousCredentials() throws InterruptedException {

        System.out.println("Registered Credentials:"+registeredCredentials.size());
        // Add the previously enrolled credentials for the same user
        for (Credential credential : registeredCredentials) {
            virtualAuthenticator.addCredential(credential);
        }

        // Navigate to the login page
        driver.get("https://webauthn.io");
        driver.findElement(By.id("input-email")).sendKeys("seconf22");

        // Start authentication process
        driver.findElement(By.id("login-button")).click();

        // Verify authentication success
        Thread.sleep(3000);
        assertTrue(driver.findElement(By.xpath("//h3[text()=\"You're logged in!\"]")).isDisplayed());
    }

    @AfterTest
    static void cleanup() {
        // Remove all credentials from the virtual authenticator and quit the WebDriver
        virtualAuthenticator.removeAllCredentials();
        driver.quit();
    }

    private VirtualAuthenticator setupVirtualAuthenticator() {
        // Configure options for the virtual authenticator
        VirtualAuthenticatorOptions options = new VirtualAuthenticatorOptions()
                .setTransport(Transport.INTERNAL)
                .setProtocol(Protocol.CTAP2)
                .setHasUserVerification(true)
                .setIsUserVerified(true);

        // Add the virtual authenticator with specified options
        return ((HasVirtualAuthenticator) driver).addVirtualAuthenticator(options);
    }
}

