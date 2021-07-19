package com.mySpringProject.OnlineBookShop.order.application.port;

import com.mySpringProject.OnlineBookShop.order.application.RichOrder;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {

    List<RichOrder> findAll();
    Optional<RichOrder> findById(Long id);

}
