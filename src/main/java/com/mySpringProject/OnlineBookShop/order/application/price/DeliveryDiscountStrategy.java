package com.mySpringProject.OnlineBookShop.order.application.price;

import com.mySpringProject.OnlineBookShop.order.domain.Order;

import java.math.BigDecimal;

public class DeliveryDiscountStrategy implements DiscountStrategy {

    public static final BigDecimal THRESHOLD = BigDecimal.valueOf(100);

    @Override
    public BigDecimal calculate(Order order) {
        if(order.getItemsPrice().compareTo(THRESHOLD) >= 0) {
            return order.getDeliveryPrice();
        }
        return BigDecimal.ZERO;
    }
}
