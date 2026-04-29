package ru.diasoft.orderitemservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.diasoft.orderitemservice.dto.OrderItemRequest;
import ru.diasoft.orderitemservice.service.OrderItemService;

/**
 * Инициализация тестовых данных при старте приложения
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final OrderItemService orderItemService;

    @Override
    public void run(String... args) {
        log.info("🚀 Initializing OrderItem Service with sample data...");

        // Электроника
        createOrderItem("LAPTOP-001", "Ноутбук Dell XPS 15", 5, 89999.99);
        createOrderItem("PHONE-001", "iPhone 15 Pro", 10, 119999.99);
        createOrderItem("TABLET-001", "iPad Air", 8, 54999.99);
        createOrderItem("WATCH-001", "Apple Watch Series 9", 15, 39999.99);
        createOrderItem("HEADPHONES-001", "AirPods Pro", 20, 24999.99);

        // Компьютерные комплектующие
        createOrderItem("GPU-001", "NVIDIA RTX 4090", 3, 149999.99);
        createOrderItem("CPU-001", "Intel Core i9-14900K", 7, 54999.99);
        createOrderItem("RAM-001", "Kingston DDR5 32GB", 25, 12999.99);
        createOrderItem("SSD-001", "Samsung 990 PRO 2TB", 30, 18999.99);
        createOrderItem("MONITOR-001", "LG UltraGear 27\" 4K", 12, 44999.99);

        // Периферия
        createOrderItem("KEYBOARD-001", "Logitech MX Keys", 18, 9999.99);
        createOrderItem("MOUSE-001", "Logitech MX Master 3S", 22, 7999.99);
        createOrderItem("WEBCAM-001", "Logitech Brio 4K", 15, 14999.99);
        createOrderItem("MICROPHONE-001", "Blue Yeti", 10, 12999.99);

        // Бытовая техника
        createOrderItem("COFFEE-001", "Кофемашина DeLonghi", 6, 34999.99);
        createOrderItem("VACUUM-001", "Робот-пылесос Xiaomi", 8, 24999.99);
        createOrderItem("AIR-001", "Увлажнитель воздуха Dyson", 5, 29999.99);
        createOrderItem("KETTLE-001", "Чайник Xiaomi Smart", 20, 3999.99);

        // Игровые консоли и аксессуары
        createOrderItem("PS5-001", "PlayStation 5", 4, 54999.99);
        createOrderItem("XBOX-001", "Xbox Series X", 6, 49999.99);
        createOrderItem("SWITCH-001", "Nintendo Switch OLED", 10, 34999.99);
        createOrderItem("CONTROLLER-001", "DualSense Controller", 25, 6999.99);

        // Аудио техника
        createOrderItem("SPEAKER-001", "JBL Charge 5", 15, 12999.99);
        createOrderItem("SOUNDBAR-001", "Samsung HW-Q990C", 5, 89999.99);
        createOrderItem("EARBUDS-001", "Sony WF-1000XM5", 12, 24999.99);

        // Умный дом
        createOrderItem("SMART-LIGHT-001", "Philips Hue Starter Kit", 10, 14999.99);
        createOrderItem("SMART-LOCK-001", "Yale Smart Lock", 8, 19999.99);
        createOrderItem("SMART-CAMERA-001", "Ring Video Doorbell", 12, 12999.99);
        createOrderItem("SMART-SPEAKER-001", "Amazon Echo Dot", 30, 4999.99);

        // Офисная техника
        createOrderItem("PRINTER-001", "HP LaserJet Pro", 7, 24999.99);
        createOrderItem("SCANNER-001", "Epson Perfection V600", 5, 29999.99);
        createOrderItem("PROJECTOR-001", "BenQ TH685P", 4, 54999.99);

        // Сетевое оборудование
        createOrderItem("ROUTER-001", "ASUS RT-AX88U", 10, 24999.99);
        createOrderItem("MESH-001", "TP-Link Deco X60", 8, 19999.99);
        createOrderItem("SWITCH-NET-001", "Netgear GS308", 15, 4999.99);

        // Хранение данных
        createOrderItem("NAS-001", "Synology DS923+", 3, 64999.99);
        createOrderItem("HDD-001", "Seagate IronWolf 8TB", 20, 14999.99);
        createOrderItem("EXTERNAL-SSD-001", "Samsung T7 1TB", 25, 9999.99);

        // Фото и видео
        createOrderItem("CAMERA-001", "Canon EOS R6 Mark II", 2, 249999.99);
        createOrderItem("LENS-001", "Canon RF 24-70mm f/2.8", 3, 149999.99);
        createOrderItem("GIMBAL-001", "DJI RS 3 Pro", 5, 54999.99);
        createOrderItem("DRONE-001", "DJI Mini 3 Pro", 6, 64999.99);

        // Аксессуары
        createOrderItem("CABLE-USB-C-001", "Кабель USB-C 2м", 100, 999.99);
        createOrderItem("CHARGER-001", "Зарядное устройство 65W", 50, 2999.99);
        createOrderItem("POWERBANK-001", "Внешний аккумулятор 20000mAh", 40, 3999.99);
        createOrderItem("CASE-001", "Чехол для ноутбука 15\"", 35, 1999.99);

        log.info("✅ Successfully initialized {} OrderItems", orderItemService.getAllOrderItems().size());
    }

    private void createOrderItem(String productId, String productName, Integer quantity, Double price) {
        OrderItemRequest request = new OrderItemRequest(productId, productName, quantity, price);
        var created = orderItemService.createOrderItem(request);
        log.debug("Created OrderItem: {} - {} (qty: {}, price: {}, total: {})",
                created.getId(),
                created.getProductName(),
                created.getQuantity(),
                created.getPrice(),
                created.getTotalPrice());
    }
}
