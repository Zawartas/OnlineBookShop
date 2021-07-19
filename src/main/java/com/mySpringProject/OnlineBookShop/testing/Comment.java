package com.mySpringProject.OnlineBookShop.testing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude="post")
public class Comment {
    @Id
    @GeneratedValue
    private Long id;
    private String content;
//    @ManyToOne(cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY)
//    private Post post;

    public Comment(String content) {
        this.content = content;
    }
}
