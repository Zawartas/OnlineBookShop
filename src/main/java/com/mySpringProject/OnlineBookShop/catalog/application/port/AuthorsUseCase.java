package com.mySpringProject.OnlineBookShop.catalog.application.port;

import com.mySpringProject.OnlineBookShop.catalog.domain.Author;

import java.util.List;

public interface AuthorsUseCase {
    List<Author> findAll();
}
