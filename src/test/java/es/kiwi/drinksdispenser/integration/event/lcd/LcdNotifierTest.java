package es.kiwi.drinksdispenser.integration.event.lcd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;


import javax.annotation.Resource;

@SpringBootTest
class LcdNotifierTest {

    @Resource(name = "lcdNotifierChannel-in-0")
    private MessageChannel lcdNotifierChannel;


    @Test
    public void testLcdNotifier() {
        String messagePayload = "Test message";
        Message<String> message = new GenericMessage<>(messagePayload);
        lcdNotifierChannel.send(message);
    }

}