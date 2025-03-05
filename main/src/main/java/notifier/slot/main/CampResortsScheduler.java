package notifier.slot.main;
import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CampResortsScheduler {

    private final CampResortsService service;
    private final EmailService emailService;

    public CampResortsScheduler(CampResortsService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void scheduledCheck() throws IOException {
        String result = service.checkAvailability().toString();
        if (!result.isBlank()) {
            emailService.sendNotification(result);
        }
    }
}

