package com.mySpringProject.OnlineBookShop.testing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Post {
    @Id
    private Long id;
    private String title;
    private String content;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private Set<Comment> comments = new HashSet<>();

    public Post(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
//        comment.setPost(this);
    }
}
