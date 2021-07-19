package com.mySpringProject.OnlineBookShop.catalog.web;

import com.mySpringProject.OnlineBookShop.catalog.application.port.CatalogUseCase;
import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CatalogController.class})
public class CatalogControllerTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    CatalogController controller;

    @Test
    public void shouldGetAllBooks() {
        // given
        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L);
        Book concurrency = new Book("Java Concurrency", 2006, new BigDecimal("99.00"), 50L);
        when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));

        // when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());

        // then
        assertEquals(2, all.size());
    }
}
