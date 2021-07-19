package com.mySpringProject.OnlineBookShop.uploads.domain;

import com.mySpringProject.OnlineBookShop.jpa.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Upload extends BaseEntity {

    private byte[] file;
    private String contentType;
    private String filename;
}
