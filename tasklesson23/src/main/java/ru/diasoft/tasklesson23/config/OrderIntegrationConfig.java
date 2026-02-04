// OrderIntegrationConfig.java
package ru.diasoft.tasklesson23.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderStatus;
import ru.diasoft.tasklesson23.service.InventoryService;
import ru.diasoft.tasklesson23.service.OrderValidator;
import ru.diasoft.tasklesson23.service.PaymentService;
import ru.diasoft.tasklesson23.service.ShippingService;

import java.util.concurrent.Executors;


@Configuration
@EnableIntegration
public class OrderIntegrationConfig {

    // Каналы
    @Bean
    public MessageChannel orderInputChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel validatedOrderChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel paymentResultChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel inventoryResultChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel orderAggregatorChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel errorChannel() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    public MessageChannel orderEventOutputChannel() {
        return MessageChannels.publishSubscribe().getObject();
    }

    // Основной поток обработки заказа
    @Bean
    public IntegrationFlow orderProcessingFlow(OrderValidator orderValidator) {
        return IntegrationFlow
                .from(orderInputChannel())
                .log(message -> "📥 Received order: " +
                        ((Order) message.getPayload()).getOrderId())

                // Валидация заказа
                .handle(orderValidator, "validate")

                // Маршрутизация по результату валидации
                .<Order, Boolean>route(
                        order -> order.getStatus() == OrderStatus.VALIDATED,
                        mapping -> mapping
                                .subFlowMapping(true, sf -> sf
                                        .log(message -> "✅ Order validated: " +
                                                ((Order) message.getPayload()).getOrderId())
                                        .channel(validatedOrderChannel()))
                                .subFlowMapping(false, sf -> sf
                                        .log(message -> "❌ Order failed validation: " +
                                                ((Order) message.getPayload()).getOrderId())
                                        .channel(errorChannel()))
                )
                .get();
    }

    // Параллельная обработка валидных заказов
    @Bean
    public IntegrationFlow validatedOrderProcessingFlow(
            PaymentService paymentService,
            InventoryService inventoryService) {

        return IntegrationFlow
                .from(validatedOrderChannel())
                .log(message -> "🔀 Starting parallel processing for order: " +
                        ((Order) message.getPayload()).getOrderId())

                // Разветвление для параллельной обработки
                .publishSubscribeChannel(
                        Executors.newFixedThreadPool(2),
                        pubsub -> pubsub
                                .subscribe(subflow -> subflow
                                        .log(message -> "💳 Processing payment for order: " +
                                                ((Order) message.getPayload()).getOrderId())
                                        .handle(paymentService, "processPayment")
                                        .channel(paymentResultChannel()))

                                .subscribe(subflow -> subflow
                                        .log(message -> "📦 Reserving inventory for order: " +
                                                ((Order) message.getPayload()).getOrderId())
                                        .handle(inventoryService, "reserveInventory")
                                        .channel(inventoryResultChannel()))
                )
                .get();
    }

    // Упрощенная агрегация результатов (без сложной логики)
    @Bean
    public IntegrationFlow aggregationFlow(ShippingService shippingService) {
        return IntegrationFlow
                .from(orderAggregatorChannel())
                .aggregate(aggregator -> aggregator
                        .correlationStrategy(message -> ((Order) message.getPayload()).getOrderId())
                        .releaseStrategy(group -> group.size() == 2)
                        .groupTimeout(10000L)
                )
                .log(message -> "🚚 Scheduling shipping for aggregated results")
                .handle(shippingService, "scheduleShipping")
                .log(message -> "🎉 Order processing completed")
                .channel(orderEventOutputChannel())
                .get();
    }

    // Направляем результаты платежа в агрегатор
    @Bean
    public IntegrationFlow paymentResultFlow() {
        return IntegrationFlow
                .from(paymentResultChannel())
                .log(message -> "💳 Payment result for order: " +
                        ((Order) message.getPayload()).getOrderId() +
                        " status: " + ((Order) message.getPayload()).getStatus())
                .channel(orderAggregatorChannel())
                .get();
    }

    // Направляем результаты инвентаря в агрегатор
    @Bean
    public IntegrationFlow inventoryResultFlow() {
        return IntegrationFlow
                .from(inventoryResultChannel())
                .log(message -> "📦 Inventory result for order: " +
                        ((Order) message.getPayload()).getOrderId() +
                        " status: " + ((Order) message.getPayload()).getStatus())
                .channel(orderAggregatorChannel())
                .get();
    }

    // Обработка ошибок
    @Bean
    public IntegrationFlow errorHandlingFlow() {
        return IntegrationFlow
                .from(errorChannel())
                .<Order>handle((order, headers) -> {
                    System.err.println("🔥 Error processing order " + order.getOrderId() +
                            ". Final status: " + order.getStatus());
                    order.setStatus(OrderStatus.CANCELLED);
                    return order;
                })
                .channel(orderEventOutputChannel())
                .get();
    }

    // Service Activator для вывода событий
    @Bean
    @ServiceActivator(inputChannel = "orderEventOutputChannel")
    public MessageHandler orderEventSubscriber() {
        return message -> {
            Object payload = message.getPayload();
            if (payload instanceof Order) {
                Order order = (Order) payload;
                System.out.println("📢 Order Event: " + order.getOrderId() +
                        " -> " + order.getStatus());
            } else {
                System.out.println("📢 Received event: " + payload);
            }
        };
    }
}