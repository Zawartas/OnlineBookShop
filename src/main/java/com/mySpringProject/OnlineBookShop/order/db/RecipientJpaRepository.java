package com.mySpringProject.OnlineBookShop.order.db;

import com.mySpringProject.OnlineBookShop.order.domain.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipientJpaRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByEmailIgnoreCase(String email);
}
