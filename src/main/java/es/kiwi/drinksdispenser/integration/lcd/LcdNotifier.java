package es.kiwi.drinksdispenser.integration.lcd;

import org.springframework.stereotype.Component;

@Component
public class LcdNotifier {
    public void notify(String message) {
        // Logic to notify LCD asynchronously
        System.out.println("LCD Notification: " + message);
    }
}
