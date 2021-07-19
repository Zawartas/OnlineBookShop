package com.mySpringProject.OnlineBookShop.order.domain;

import com.mySpringProject.OnlineBookShop.jpa.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id")
    @Singular
    private Set<OrderItem> items;

    @ManyToOne(cascade = CascadeType.ALL)
    private Recipient recipient;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Delivery delivery = Delivery.COURIER;

    public UpdateStatusResult updateStatus(OrderStatus newStatus) {
        UpdateStatusResult result = this.status.updateStatus(newStatus);
        this.status = result.getNewStatus();
        return result;
    }

    public BigDecimal getItemsPrice() {
        return items.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDeliveryPrice() {
        if(items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return delivery.getPrice();
    }
}
