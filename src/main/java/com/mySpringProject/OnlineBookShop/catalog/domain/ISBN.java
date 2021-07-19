package com.mySpringProject.OnlineBookShop.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ISBN {

    @Id
    private Long id;
    int number;
//    #1
//    @OneToOne(mappedBy = "isbn")
    @OneToOne
    @MapsId
//    @JoinColumn
    @JsonIgnoreProperties("isbn")
    Book book;

    public ISBN(int number, Book book) {
        this.number = number;
        this.book = book;
    }
}
