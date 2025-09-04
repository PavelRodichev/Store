package com.pavel.store.entity;

public enum OrderStatus {
    PENDING,            // Заказ создан, ожидает подтверждения или обработки
    CONFIRMED,          // Заказ подтвержден менеджером/системой
    PROCESSING,         // Заказ собран и готов к отправке
    SHIPPED,            // Заказ передан в службу доставки
    IN_TRANSIT,         // Заказ в пути
    OUT_FOR_DELIVERY,   // Курьер выехал к клиенту
    DELIVERED,
}

