package com.mySpringProject.OnlineBookShop.catalog.application;

import com.mySpringProject.OnlineBookShop.catalog.application.port.AuthorsUseCase;
import com.mySpringProject.OnlineBookShop.catalog.db.AuthorJpaRepository;
import com.mySpringProject.OnlineBookShop.catalog.domain.Author;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService implements AuthorsUseCase {
        private final AuthorJpaRepository repository;

        @Override
        public List<Author> findAll() {
            return repository.findAll();
        }
}
