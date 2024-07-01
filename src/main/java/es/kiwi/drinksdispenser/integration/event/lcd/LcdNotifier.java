package es.kiwi.drinksdispenser.integration.event.lcd;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LcdNotifier {
    @Resource
    private StreamBridge streamBridge;

    public void notify(String message) {
        streamBridge.send("lcdNotifierChannel-out-0", message);
    }

}
