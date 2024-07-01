package es.kiwi.drinksdispenser.integration.event.manager;


import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProductStockZeroEventHandler {
    @Resource
    private StreamBridge streamBridge;

    public void handle(ProductStockZeroEvent event) {
        Message<ProductStockZeroEvent> msg = MessageBuilder.withPayload(event)
                .setHeader("machineId", event.getMachines().getId())
                .setHeader("productId", event.getProducts().getId())
                .build();
        streamBridge.send("zeroStockNotifierChannel-out-0", msg);
    }
}