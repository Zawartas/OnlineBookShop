package com.mySpringProject.OnlineBookShop.order.application;

import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase;
import com.mySpringProject.OnlineBookShop.catalog.db.BookJpaRepository;
import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import com.mySpringProject.OnlineBookShop.order.domain.Delivery;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import com.mySpringProject.OnlineBookShop.order.application.port.QueryOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.domain.OrderStatus;
import com.mySpringProject.OnlineBookShop.order.domain.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {
    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void userCanPlaceOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 15))
                .item(new OrderItemCommand(jcip.getId(), 10))
                .build();

        // when
        PlaceOrderResponse response = service.placeOrder(command);

        // then
        assertTrue(response.isSuccess());
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(jcip));
    }

    @Test
    public void userCanRevokeOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrderWithShipping(effectiveJava.getId(), 15, marek);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user(marek));
        service.updateOrderStatus(command);

        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELLED, queryOrderService.findById(orderId).get().getStatus());
    }

    private User user(String email) {
        return new User(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void userCannotRevokePaidOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrderWithShipping(effectiveJava.getId(), 15, marek);
        UpdateStatusCommand paidCommand = new UpdateStatusCommand(orderId, OrderStatus.PAID, user(marek));
        service.updateOrderStatus(paidCommand);

        // when
        final UpdateStatusCommand cancelCommand =
                new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user(marek));
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.updateOrderStatus(cancelCommand));

        // then
        assertTrue(exception.getMessage().contains("Unable to mark PAID order as CANCELLED"));
    }

    @Test
    public void userCannotRevokeShippedOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrderWithShipping(effectiveJava.getId(), 15, marek);
        UpdateStatusCommand paidCommand = new UpdateStatusCommand(orderId, OrderStatus.PAID, user(marek));
        service.updateOrderStatus(paidCommand);
        UpdateStatusCommand shippedCommand = new UpdateStatusCommand(orderId, OrderStatus.SHIPPED, user(marek));
        service.updateOrderStatus(shippedCommand);

        // when
        final UpdateStatusCommand cancelCommand =
                new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user(marek));
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.updateOrderStatus(cancelCommand));

        // then
        assertTrue(exception.getMessage().contains("Unable to mark SHIPPED order as CANCELLED"));
    }

    @Test
    public void userCannotOrderNonExistingBooks() {
        // user nie moze zamowic nieistniejacych ksiazek
        // given
        Book bookNotInRepo = new Book("Effective Java", 2005, new BigDecimal("199.90"), 1L);
        String recipient = "marek@example.org";
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookNotInRepo.getId(), 1))
                .delivery(Delivery.COURIER)
                .build();

        // when
        RuntimeException exception =
                assertThrows(InvalidDataAccessApiUsageException.class, () -> service.placeOrder(command));

        // then
        assertTrue(exception.getMessage().contains("The given id must not be null!"));
    }

    @Test
    public void userCannotOrderNegativeNumberOfBooks() {
        // user nie moze zamowic ujemnej liczby ksiazek
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrderWithShipping(effectiveJava.getId(), -15, recipient);
        // TODO pytanie na forum do Darka
    }

    @Test
    public void userCannotRevokeOtherUsersOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org";
        Long orderId = placedOrderWithShipping(effectiveJava.getId(), 15, adam);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user("marek@example.org"));
        service.updateOrderStatus(command);

        // then
        assertEquals(35, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    // TODO-Darek: poprawic w module security
    public void adminCannotRevokeOtherUsersOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrderWithShipping(effectiveJava.getId(), 15, marek);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, admin());
        service.updateOrderStatus(command);

        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELLED, queryOrderService.findById(orderId).get().getStatus());
    }

    private User admin() {
        return new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void adminCanMarkOrderAsPaid() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrderWithShipping(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, admin());
        service.updateOrderStatus(command);

        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
    }

    private Long placedOrderWithShipping(Long bookId, int copies, String recipient) {
            PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .delivery(Delivery.COURIER)
                .build();
        return service.placeOrder(command).getRight();
    }

    private Long placedOrderWithShipping(Long bookId, int copies) {
        return placedOrderWithShipping(bookId, copies, "john@example.org");
    }

    @Test
    public void userCantOrderMoreBooksThanAvailable() {
        // given
        Book effectiveJava = givenEffectiveJava(5L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .build();

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });

        // then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() + " requested"));
    }

    @Test
    public void shippingCostsAreAddedToTotalOrderPrice() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrderWithShipping(book.getId(), 1);

        // then
        assertEquals("59.80", orderOf(orderId).getFinalPrice().toPlainString());
    }

    @Test
    public void shippingCostsAreDiscountedOver100zlotys() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrderWithShipping(book.getId(), 3);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("149.70", order.getFinalPrice().toPlainString());
        assertEquals("149.70", order.getOrderPrice().getItemsPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsHalfPricedWhenTotalOver200zlotys() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrderWithShipping(book.getId(), 5);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("224.55", order.getFinalPrice().toPlainString());
    }

    @Test
    public void cheapestBookIsFreeWhenTotalOver400zlotys() {
        // given
        Book book = givenBook(50L, "49.90");

        // when
        Long orderId = placedOrderWithShipping(book.getId(), 10);

        // then
        assertEquals("449.10", orderOf(orderId).getFinalPrice().toPlainString());
    }

    private RichOrder orderOf(Long orderId) {
        return queryOrderService.findById(orderId).get();
    }


    private Book givenBook(long available, String price) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal(price), available));
    }

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private Recipient recipient() {
        return recipient("john@example.org");
    }

    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
    }

    private Long availableCopiesOf(Book effectiveJava) {
        return catalogUseCase.findById(effectiveJava.getId())
                .get()
                .getAvailable();
    }
}