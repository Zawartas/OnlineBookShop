package com.mySpringProject.OnlineBookShop.catalog.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.security.PrivateKey;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "author_id")
    private Author author;

    private String street;
}
