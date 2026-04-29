package ru.diasoft.orderitemservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
@Configuration
@EnableCircuitBreaker
public class HystrixConfig {
}