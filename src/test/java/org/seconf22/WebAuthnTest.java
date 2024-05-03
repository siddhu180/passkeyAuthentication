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

import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebAuthnTest {

    private static WebDriver driver;
    private static VirtualAuthenticator virtualAuthenticator;
    private static List<Credential> registeredCredentials;

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

        registeredCredentials = virtualAuthenticator.getCredentials();
        System.out.println("Registered Credentials:");
        for (Credential credential : registeredCredentials) {
// Fetch the PKCS8EncodedKeySpec object representing the private key
            PKCS8EncodedKeySpec privateKeySpec = credential.getPrivateKey();

// Decode the encoded private key bytes
            byte[] privateKeyBytes = privateKeySpec.getEncoded();

// Encode the private key bytes using Base64
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKeyBytes);

            System.out.println("Private Key (Base64): " + privateKeyBase64);

            System.out.println("RpId: " +credential.getRpId());

            byte[] idBytes = credential.getId();

            // Convert the byte array to a Base64-encoded string for printing
            String idBase64 = Base64.getEncoder().encodeToString(idBytes);

            System.out.println("Credential Id (Base64): " + idBase64);

            System.out.println("Sign Count " + credential.getSignCount());

        }
    }

//    @Test
//    @DisplayName("WebAuthn reg and auth flow should work")
    void testLogin() throws InterruptedException {

        // Assigned values
        String privateKeyBase64 = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgLqcsr4uSKl+nOQAkeW1WgJr2pA8A9FGaEGBL5lfD1y6hRANCAATxUX5rK4r3gZrtKEyuU3kfTSKDW05C8m814E0QhjemE3OqV+7wGf6uMj6zTW9J+Bq2l1NEeD4lwEeXRuNluJhZ";
        String rpId = "webauthn.io";
        String credentialIdBase64 = "luObIryUsTl0r7IAY6/gnr7T8LdzTq9tDdxz7RSQDo4=";
        int signCount = 0; // Assuming the sign count is an integer

//// Convert the Base64-encoded private key string to a byte array
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

        // Create a new Credential instance with the provided string values
        Credential c = Credential.createNonResidentCredential(
                credentialIdBase64.getBytes(),
                rpId,
                new PKCS8EncodedKeySpec(privateKeyBytes),
                signCount
        );
        // set up the virtual authenticator for webauthn
        virtualAuthenticator = setupVirtualAuthenticator();

        // Assuming authenticator is initialized
        virtualAuthenticator = ((HasVirtualAuthenticator) driver).addVirtualAuthenticator(new VirtualAuthenticatorOptions());
        // Add the new Credential instance to the authenticator

        virtualAuthenticator.addCredential(c);

        // Login to WebAuthn.io

        driver.get("https://webauthn.io");
        driver.findElement(By.id("input-email")).sendKeys("seconf22");

        // start authentication
        driver.findElement(By.id("login-button")).click();

        // authentication should be successful
        Thread.sleep(3000);


        registeredCredentials = virtualAuthenticator.getCredentials();
        System.out.println("Registered Credentials:");
        for (Credential credential : registeredCredentials) {
// Fetch the PKCS8EncodedKeySpec object representing the private key
            PKCS8EncodedKeySpec privateKeySpec = credential.getPrivateKey();

// Decode the encoded private key bytes
            byte[] privateKeyBytes01 = privateKeySpec.getEncoded();

// Encode the private key bytes using Base64
            String privateKeyBase6401 = Base64.getEncoder().encodeToString(privateKeyBytes01);

            System.out.println("Private Key (Base64): " + privateKeyBase6401);


        }
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