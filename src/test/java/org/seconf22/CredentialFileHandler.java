package org.seconf22;

import com.google.gson.Gson;
import org.openqa.selenium.virtualauthenticator.Credential;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CredentialFileHandler {
    public static void saveCredentialsToFile(Credential credential) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(credential);
            FileWriter writer = new FileWriter("credentials.json");
            writer.write(json);
            writer.close();
            System.out.println("Credentials saved to file successfully.");
        } catch (IOException e) {
            System.err.println("Error saving credentials to file: " + e.getMessage());
        }
    }

    public static Credential readCredentialsFromFile() {
        try {
            Gson gson = new Gson();
            BufferedReader reader = new BufferedReader(new FileReader("credentials.json"));
            Credential credential = gson.fromJson(reader, Credential.class);
            reader.close();
            return credential;
        } catch (IOException e) {
            System.err.println("Error reading credentials from file: " + e.getMessage());
            return null;
        }
    }
}
