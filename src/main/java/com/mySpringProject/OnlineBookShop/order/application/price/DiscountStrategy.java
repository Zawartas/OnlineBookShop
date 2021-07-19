package com.mySpringProject.OnlineBookShop.order.application.price;

import com.mySpringProject.OnlineBookShop.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {

    BigDecimal calculate(Order order);
}
