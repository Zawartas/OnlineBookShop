package com.mySpringProject.OnlineBookShop.order.application.port;

import com.mySpringProject.OnlineBookShop.order.domain.Delivery;
import com.mySpringProject.OnlineBookShop.commons.Either;
import com.mySpringProject.OnlineBookShop.order.domain.OrderStatus;
import com.mySpringProject.OnlineBookShop.order.domain.Recipient;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

public interface ManipulateOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void deleteOrderById(Long id);

    UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        @Valid
        List<OrderItemCommand> items;
        Recipient recipient;
        Delivery delivery;
    }

    @Value
    class OrderItemCommand {
        Long bookId;
        @NotNull
        @PositiveOrZero
        int quantity;

        public OrderItemCommand(Long bookId, int quantity) {
            this.bookId = bookId;
            this.quantity = quantity;
//            ValidationHelper.validate(this);
        }
    }

    @Value
    class UpdateStatusCommand {
        Long orderId;
        OrderStatus status;
        UserDetails user;
    }

    class PlaceOrderResponse extends Either<Error, Long> {
        public PlaceOrderResponse(boolean success, Error left, Long right) {
            super(success, left, right);
        }

        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, null, orderId);
        }

        public static PlaceOrderResponse failure(Error error) {
            return new PlaceOrderResponse(false, error, null);
        }
    }

    class UpdateStatusResponse extends Either<Error, OrderStatus> {
        public UpdateStatusResponse(boolean success, Error left, OrderStatus right) {
            super(success, left, right);
        }

        public static UpdateStatusResponse success(OrderStatus status) {
            return new UpdateStatusResponse(true, null, status);
        }

        public static UpdateStatusResponse failure(Error error) {
            return new UpdateStatusResponse(false, error, null);
        }
    }

    @AllArgsConstructor
    @Getter
    enum Error {

        NOT_FOUND(HttpStatus.NOT_FOUND),
        FORBIDDEN(HttpStatus.FORBIDDEN);

        private final HttpStatus status;
    }
}
