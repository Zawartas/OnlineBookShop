package com.mySpringProject.OnlineBookShop.uploads.db;

import com.mySpringProject.OnlineBookShop.uploads.domain.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}
