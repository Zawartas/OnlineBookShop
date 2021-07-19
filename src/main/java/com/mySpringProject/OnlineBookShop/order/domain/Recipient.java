package com.mySpringProject.OnlineBookShop.order.domain;

import com.mySpringProject.OnlineBookShop.jpa.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@Embeddable
@Entity
public class Recipient extends BaseEntity {

    private String email;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
}
