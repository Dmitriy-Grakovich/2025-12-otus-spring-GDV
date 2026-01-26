package ru.diasoft.tasklesson23.domain;

public enum OrderStatus {
    NEW,
    VALIDATED,
    PAYMENT_PENDING,
    PAYMENT_PROCESSED,
    PAYMENT_FAILED,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    SHIPPING_SCHEDULED,
    COMPLETED,
    CANCELLED
}