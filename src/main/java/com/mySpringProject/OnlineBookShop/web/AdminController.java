package com.mySpringProject.OnlineBookShop.web;

import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogInitializerUseCase;
import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase;
import com.mySpringProject.OnlineBookShop.catalog.db.AuthorJpaRepository;
import com.mySpringProject.OnlineBookShop.catalog.domain.Author;
import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import com.mySpringProject.OnlineBookShop.order.application.port.QueryOrderUseCase;
import com.mySpringProject.OnlineBookShop.order.domain.Recipient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

import static com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase.CreateBookCommand;
import static com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.OrderItemCommand;
import static com.mySpringProject.OnlineBookShop.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;

@Slf4j
@RestController
@Secured({"ROLE_ADMIN"})
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CatalogInitializerUseCase initializer;

    @PostMapping("/initialization")
    @Transactional
    @Secured("ROLE_ADMIN")
    public void initialize() {
        initializer.initialize();
    }
}
