package ru.diasoft.tasklesson23;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import ru.diasoft.tasklesson23.domain.Order;
import ru.diasoft.tasklesson23.domain.OrderStatus;
import ru.diasoft.tasklesson23.dto.OrderItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class Tasklesson23ApplicationTests {

	@Autowired
	private DirectChannel orderInputChannel;

	@Test
	void contextLoads() {
		assertThat(orderInputChannel).isNotNull();
	}

	@Test
	void sendTestOrder() {
		// Создаем OrderItemDto (как будто получили от OrderItem Service)
		OrderItemDto item = OrderItemDto.builder()
				.id(UUID.randomUUID().toString())
				.productId("prod-123")
				.productName("Test Product")
				.quantity(2)
				.price(50.0)
				.totalPrice(100.0)
				.build();

		Order order = Order.builder()
				.orderId(UUID.randomUUID().toString())
				.customerId("cust-001")
				.items(List.of(item))
				.totalAmount(100.0)
				.status(OrderStatus.NEW)
				.shippingAddress("123 Main St, City, Country")
				.createdDate(LocalDateTime.now())
				.build();

		Message<Order> message = MessageBuilder.withPayload(order).build();
		boolean sent = orderInputChannel.send(message);

		assertThat(sent).isTrue();

		// Даем время на асинхронную обработку
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}