package com.mySpringProject.OnlineBookShop.order.application;

import com.mySpringProject.OnlineBookShop.order.application.price.OrderPrice;
import com.mySpringProject.OnlineBookShop.order.domain.OrderItem;
import com.mySpringProject.OnlineBookShop.order.domain.OrderStatus;
import com.mySpringProject.OnlineBookShop.order.domain.Recipient;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Value
public
class RichOrder {

    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;
    OrderPrice orderPrice;
    BigDecimal finalPrice;
}
