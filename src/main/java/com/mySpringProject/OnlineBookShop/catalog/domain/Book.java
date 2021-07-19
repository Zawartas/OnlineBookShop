package com.mySpringProject.OnlineBookShop.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mySpringProject.OnlineBookShop.jpa.BaseEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString(exclude = "authors")
@RequiredArgsConstructor
@Entity
public class Book extends BaseEntity {

    @Column(unique = true)
    private String title;
    private Integer year;
    private BigDecimal price;
    private Long coverId;
    private Long available;
    @ManyToMany
    @JoinTable
    @JsonIgnoreProperties("books")
    private Set<Author> authors = new HashSet<>();
    @OneToOne(mappedBy = "book", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @PrimaryKeyJoinColumn
    @JsonIgnoreProperties("book") // if not - than infinite loop
    private ISBN isbn;

    public Book(String title, Integer year, BigDecimal price, Long available) {
        this.title = title;
        this.year = year;
        this.price = price;
        this.available = available;
        isbn = new ISBN(year - 1222, this);
    }

    public void addAuthor(Author author) {
        authors.add(author);
        log.info("--------------adding Author--------------------");
        author.getBooks().forEach(System.out::println);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void removeAuthors() {
        Book self = this;
        authors.forEach(author -> author.getBooks().remove(self));
        authors.clear();
    }
}