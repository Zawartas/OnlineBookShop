package com.mySpringProject.OnlineBookShop.catalog.web;

import com.mySpringProject.OnlineBookShop.catalog.application.port.AuthorsUseCase;
import com.mySpringProject.OnlineBookShop.catalog.domain.Author;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/authors")
class AuthorsController {
    private final AuthorsUseCase authors;

    @GetMapping
    public List<Author> findAll() {
        return authors.findAll();
    }
}
