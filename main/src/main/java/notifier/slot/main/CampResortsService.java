package notifier.slot.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Service;

@Service
public class CampResortsService {

    private static final String REQUESTED_MONTH = "2024-07";
    private static final String REQUEST_URL = "https://www.recreation.gov/api/permits/249991/availability/month?start_date=" 
            + REQUESTED_MONTH + "-01T00:00:00.000Z";

    private final SlotAvailabilityRepository repository;

    public CampResortsService(SlotAvailabilityRepository repository) {
        this.repository = repository;
    }

    public List<SlotAvailability> checkAvailability() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(REQUEST_URL).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed: HTTP error code " + connection.getResponseCode());
        }

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        return parseAndSaveResponse(response.toString());
    }

    private List<SlotAvailability> parseAndSaveResponse(String json) {
        JSONObject payload = (JSONObject) ((JSONObject) JSONValue.parse(json)).get("payload");
        JSONObject available = (JSONObject) payload.get("availability");
        JSONObject num279 = (JSONObject) available.get("279");
        JSONObject quotaTypeMaps = (JSONObject) num279.get("quota_type_maps");
        JSONObject quotaUsage = (JSONObject) quotaTypeMaps.get("QuotaUsageByMemberDaily");

        List<SlotAvailability> slots = new ArrayList<>();

        for (int i = 1; i <= 31; i++) {
            String date = String.format("%s-%02d", REQUESTED_MONTH, i);
            JSONObject dayData = (JSONObject) quotaUsage.get(date + "T00:00:00Z");

            if (dayData != null && !"0".equals(dayData.get("remaining").toString())) {
                SlotAvailability slot = new SlotAvailability(date, Integer.parseInt(dayData.get("remaining").toString()));
                slots.add(slot);
                repository.save(slot);  // Save directly to database
            }
        }

        return slots;
    }
}