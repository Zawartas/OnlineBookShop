package com.mySpringProject.OnlineBookShop.testing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
