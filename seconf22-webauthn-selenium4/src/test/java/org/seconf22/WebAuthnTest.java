package org.seconf22;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.virtualauthenticator.Credential;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions.Protocol;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions.Transport;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebAuthnTest {

    private static WebDriver driver;
    private static VirtualAuthenticator virtualAuthenticator;

    @BeforeAll
    static void setup() {
//        driver = WebDriverManager.chromedriver().create();

        driver = new ChromeDriver();

    }

    @Test
    @DisplayName("WebAuthn reg and auth flow should work")
    void sampleTest() throws InterruptedException {

        // set up the virtual authenticator for webauthn
        virtualAuthenticator = setupVirtualAuthenticator();
        System.out.println(virtualAuthenticator.getCredentials());
//        List<Credential> credentials = virtualAuthenticator.getCredentials();
//
//
//// Iterate through the list and do something with each credential
//        for (Credential credential : credentials) {
//            // Do something with each credential, such as printing its details
//            System.out.println("Credentials: "+credential.toString());
//        }

        // start registration
        driver.get("https://webauthn.io");
        driver.findElement(By.id("input-email")).sendKeys("seconf22");

        WebElement ele1 = driver.findElement(By.className("btn-secondary"));
        ele1.click();


        Select selectAttestationType = new Select(driver.findElement(By.id("attestation")));
        selectAttestationType.selectByVisibleText("Direct");

        Select selectAuthenticatorType = new Select(driver.findElement(By.id("attachment")));
        selectAuthenticatorType.selectByVisibleText("Platform");

        driver.findElement(By.id("register-button")).click();

        // registration should be successful
        Thread.sleep(3000);
        assertTrue(driver.findElement(By.xpath("//div[contains(text(),'Success!')]")).isDisplayed());

        // start authentication
        driver.findElement(By.id("login-button")).click();

        // authentication should be successful
        Thread.sleep(3000);
        assertTrue(driver.findElement(By.xpath("//h3[text()=\"You're logged in!\"]")).isDisplayed());
    }

    private VirtualAuthenticator setupVirtualAuthenticator() {
        VirtualAuthenticatorOptions options = new VirtualAuthenticatorOptions();
        options.setTransport(Transport.INTERNAL)
                .setProtocol(Protocol.CTAP2)
                .setHasUserVerification(true)
                .setIsUserVerified(true);
        return ((HasVirtualAuthenticator) driver).addVirtualAuthenticator(options);
    }

    @AfterAll
    static void cleanup() {
        virtualAuthenticator.removeAllCredentials();
        driver.quit();
    }
}