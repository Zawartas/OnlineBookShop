package com.mySpringProject.OnlineBookShop.order.application;

import com.mySpringProject.OnlineBookShop.order.application.price.OrderPrice;
import com.mySpringProject.OnlineBookShop.order.application.price.PriceService;
import com.mySpringProject.OnlineBookShop.order.application.port.QueryOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.db.OrderJpaRepository;
import com.mySpringProject.OnlineBookShop.order.domain.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {

    private final OrderJpaRepository repository;
    private final PriceService priceService;

    @Override
    @Transactional
    public List<RichOrder> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toRichOrder)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<RichOrder> findById(Long id) {
        return repository.findById(id).map(this::toRichOrder);
    }

    private RichOrder toRichOrder(Order order) {
        OrderPrice orderPrice = priceService.calculatePrice(order);
        return new RichOrder(
                order.getId(),
                order.getStatus(),
                order.getItems(),
                order.getRecipient(),
                order.getCreatedAt(),
                orderPrice,
                orderPrice.finalPrice()
        );
    }
}
