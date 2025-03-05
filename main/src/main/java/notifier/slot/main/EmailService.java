package notifier.slot.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String FORM_URL = "https://formspree.io/YOUR-EMAIL";  // Replace with your actual Formspree URL

    public void sendNotification(String message) throws IOException {
        if (message.isBlank()) {
            return;
        }

        String payload = "name=AutoChecker&email=youremail@example.com"
                + "&message=" + message
                + "&sub=Open Slots Found";

        HttpURLConnection form = (HttpURLConnection) new URL(FORM_URL).openConnection();
        form.setRequestMethod("POST");
        form.setDoOutput(true);
        form.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = form.getOutputStream()) {
            os.write(payload.getBytes());
        }

        form.getResponseCode();  // triggers the request
    }
}

