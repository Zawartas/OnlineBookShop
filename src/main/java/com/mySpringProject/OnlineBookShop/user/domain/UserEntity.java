package com.mySpringProject.OnlineBookShop.user.domain;

import com.mySpringProject.OnlineBookShop.jpa.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    private String username;
    private String password;

    @CollectionTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = Set.of("ROLE_USER");
    }
}
