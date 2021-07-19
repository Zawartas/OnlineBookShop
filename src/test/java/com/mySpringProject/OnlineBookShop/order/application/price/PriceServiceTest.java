package com.mySpringProject.OnlineBookShop.order.application.price;

import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import com.mySpringProject.OnlineBookShop.order.domain.Order;
import com.mySpringProject.OnlineBookShop.order.domain.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceServiceTest {
    PriceService priceService = new PriceService();

    @Test
    public void calculatesTotalPriceOfEmptyOrder() {
        // given
        Order order = Order
                .builder()
                .build();

        // when
        OrderPrice price = priceService.calculatePrice(order);

        // then
        assertEquals(BigDecimal.ZERO, price.finalPrice());
    }

    @Test
    public void calculatesTotalPrice() {
        // given
        Book book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        Book book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));

        Order order = Order
                .builder()
                .item(new OrderItem(book1, 2))
                .item(new OrderItem(book2, 5))
                .build();

        // when
        OrderPrice price = priceService.calculatePrice(order);

        // then
        assertEquals(new BigDecimal("194.95"), price.finalPrice());
        assertEquals(new BigDecimal("194.95"), price.getItemsPrice());
    }
}
