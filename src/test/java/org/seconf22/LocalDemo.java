package org.seconf22;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.virtualauthenticator.Credential;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

public class LocalDemo {

    private static WebDriver driver;
    private static VirtualAuthenticator virtualAuthenticator;
    private static List<Credential> registeredCredentials, registeredCredentials01;

    @BeforeTest
    static void setup() {
        // Initialize WebDriver

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        // Allow invalid certificates for resources loaded from localhost.
    }

    @Test(priority = 1)
    void testPasskeyEnrollmentAndLogin() throws InterruptedException {
        // Set up the virtual authenticator for WebAuthn testing
        virtualAuthenticator = setupVirtualAuthenticator();

        virtualAuthenticator.getCredentials();

        // Navigate to the enrollment page
        driver.get("http://localhost:63342/identities/local-passkey/register.html?_ijt=vcfpjambpnuhi4jkafqvl2n8hs&_ij_reload=RELOAD_ON_SAVE");

        driver.findElement(By.id("email")).sendKeys("testabc@test.com");

        Thread.sleep(10000);
        driver.findElement(By.xpath("//*[@id=\"registerForm\"]/button")).click();

        Thread.sleep(10000);




    }
    private VirtualAuthenticator setupVirtualAuthenticator() {
        // Configure options for the virtual authenticator
        VirtualAuthenticatorOptions options = new VirtualAuthenticatorOptions()
                .setTransport(VirtualAuthenticatorOptions.Transport.USB)
                .setProtocol(VirtualAuthenticatorOptions.Protocol.CTAP2)
                .setHasUserVerification(true)
                .setIsUserVerified(true);

        // Add the virtual authenticator with specified options
        return ((HasVirtualAuthenticator) driver).addVirtualAuthenticator(options);
    }
}
