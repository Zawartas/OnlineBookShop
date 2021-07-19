package com.mySpringProject.OnlineBookShop.order.application;

import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase;
import com.mySpringProject.OnlineBookShop.catalog.db.BookJpaRepository;
import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import com.mySpringProject.OnlineBookShop.clock.Clock;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import com.mySpringProject.OnlineBookShop.order.application.port.QueryOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.domain.OrderStatus;
import com.mySpringProject.OnlineBookShop.order.domain.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        properties = "app.orders.payment-period=1H"
)
@AutoConfigureTestDatabase
public class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Autowired
    ManipulateOrderService manipulateOrderService;

    @Autowired
    Clock.Fake clock;

    @Autowired
    AbandonedOrdersJob ordersJob;

    @Test
    public void shouldMarkOrdersAsAbandoned() {
        // given - orders
        Book book = givenEffectiveJava(50L);
        Long orderId = placedOrder(book.getId(), 15);

        // when - run
        clock.tick(Duration.ofHours(2));
        ordersJob.run();

        // then - status changed
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
        assertEquals(50L, availableCopiesOf(book));
    }

    private Long placedOrder(Long bookId, int copies) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(bookId, copies))
                .build();
        return manipulateOrderService.placeOrder(command).getRight();
    }

    private Recipient recipient() {
        return Recipient.builder().email("marek@example.org").build();
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                .get()
                .getAvailable();
    }
}
