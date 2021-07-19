package com.mySpringProject.OnlineBookShop.order.domain;

import com.mySpringProject.OnlineBookShop.catalog.domain.Book;
import com.mySpringProject.OnlineBookShop.jpa.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    private int quantity;
}
