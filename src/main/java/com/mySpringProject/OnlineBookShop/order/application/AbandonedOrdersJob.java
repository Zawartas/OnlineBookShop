package com.mySpringProject.OnlineBookShop.order.application;

import com.mySpringProject.OnlineBookShop.clock.Clock;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.db.OrderJpaRepository;
import com.mySpringProject.OnlineBookShop.order.domain.Order;
import com.mySpringProject.OnlineBookShop.order.domain.OrderStatus;
import com.mySpringProject.OnlineBookShop.security.UserSecurity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.*;

@Slf4j
@Component
@AllArgsConstructor
class AbandonedOrdersJob {
    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;
    private final OrdersProperties properties;
    private final Clock clock;
    private final User systemUser; //searches for "User" Bean;

    @Transactional
    @Scheduled(cron = "${app.orders.abandon-cron}")
    public void run() {

        log.info("______ " + properties.getAbandonCron() + " ______ " + properties.getPaymentPeriod().toString());
        Duration paymentPeriod = properties.getPaymentPeriod();
        LocalDateTime olderThan = clock.now().minus(paymentPeriod);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, olderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> {
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, systemUser);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
