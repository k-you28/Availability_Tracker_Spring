package notifier.slot.main;
import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CampResortsController {

    private final CampResortsService service;
    private final EmailService emailService;

    public CampResortsController(CampResortsService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    @GetMapping("/check-slots")
    public List<SlotAvailability> checkSlots() throws IOException {
        List<SlotAvailability> slots = service.checkAvailability();
        if (!slots.isEmpty()) {
            String message = slots.stream()
                    .map(slot -> slot.getDate() + ": " + slot.getRemainingSlots() + " slots")
                    .reduce("", (a, b) -> a + "\n" + b);
            emailService.sendNotification(message);
        }
        return slots;
    }
}

