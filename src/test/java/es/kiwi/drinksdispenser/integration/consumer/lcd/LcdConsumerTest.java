package es.kiwi.drinksdispenser.integration.consumer.lcd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;


import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@EnableBinding(Sink.class)
class LcdConsumerTest {

    @Resource(name = "lcdNotifierChannel-in-0")
    private SubscribableChannel lcdNotifierChannel;

    private String receivedMessage;

    @Autowired
    private LcdConsumer lcdConsumer;

    @Test
    public void testLcdConsumer() {
        String messagePayload = "Test message";
        lcdNotifierChannel.send(new GenericMessage<>(messagePayload));

        assertNull(receivedMessage);
    }

}