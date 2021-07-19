package com.mySpringProject.OnlineBookShop.catalog.db;

import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors JOIN FETCH b.isbn")
    List<Book> findAllEager();

//    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(String firstName, String lastName);

    List<Book> findByAuthors_nameContainsIgnoreCase(String name);

    List<Book> findByTitleStartsWithIgnoreCase(String title);

    @Query(" SELECT b FROM Book b JOIN b.authors a " +
                    " WHERE " +
                    " lower(a.name) LIKE lower(concat('%', :name,'%')) ")
    List<Book> findByAuthor(@Param("name") String name);
}
