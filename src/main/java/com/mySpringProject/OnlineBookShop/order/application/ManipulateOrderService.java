package com.mySpringProject.OnlineBookShop.order.application;

import com.mySpringProject.OnlineBookShop.catalog.db.BookJpaRepository;
import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.db.OrderJpaRepository;
import com.mySpringProject.OnlineBookShop.order.db.RecipientJpaRepository;
import com.mySpringProject.OnlineBookShop.order.domain.*;
import com.mySpringProject.OnlineBookShop.security.UserSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
class ManipulateOrderService implements ManipulateOrderUseCase {

    private final OrderJpaRepository orderJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final RecipientJpaRepository recipientJpaRepository;
    private final UserSecurity userSecurity;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> items = command
                .getItems()
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());
        Order order = Order
                .builder()
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .delivery(command.getDelivery()==null?Delivery.COURIER:command.getDelivery())
                .items(items)
                .build();
        Order saveOrder = orderJpaRepository.save(order);
        bookJpaRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(saveOrder.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientJpaRepository
                .findByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
        return items
                .stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                })
                .collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = bookJpaRepository.getById(command.getBookId());
        int quantity = command.getQuantity();
        if (book.getAvailable() >= quantity) {
            return new OrderItem(book, quantity);
        }
        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + quantity + " of " + book.getAvailable() + " available ");
    }

    @Override
    public void deleteOrderById(Long id) {
        orderJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command) {
        return orderJpaRepository
                .findById(command.getOrderId())
                .map(order -> {
                            if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), command.getUser())) {
                                UpdateStatusResult result = order.updateStatus(command.getStatus());
                                if (result.isRevoked()) {
                                    bookJpaRepository.saveAll(revokeBooks(order.getItems()));
                                }
                                orderJpaRepository.save(order);
                                return UpdateStatusResponse.success(order.getStatus());
                            }
                            return UpdateStatusResponse.failure(Error.FORBIDDEN);
                })
                .orElse(UpdateStatusResponse.failure(Error.NOT_FOUND));
    }

    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items
                .stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                })
                .collect(Collectors.toSet());
    }
}
