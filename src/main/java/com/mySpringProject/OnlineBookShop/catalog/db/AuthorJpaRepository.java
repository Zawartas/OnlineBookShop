package com.mySpringProject.OnlineBookShop.catalog.db;

import com.mySpringProject.OnlineBookShop.catalog.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);
}
