package com.mySpringProject.OnlineBookShop.uploads.application;

import com.mySpringProject.OnlineBookShop.uploads.application.ports.UploadUseCase;
import com.mySpringProject.OnlineBookShop.uploads.db.UploadJpaRepository;
import com.mySpringProject.OnlineBookShop.uploads.domain.Upload;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
class UploadService implements UploadUseCase {
    private final UploadJpaRepository repository;

    @Override
    public Upload save(SaveUploadCommand command) {
        Upload upload = new Upload(
                command.getFile(),
                command.getContentType(),
                command.getFilename()
                );
        repository.save(upload);
        System.out.println("Upload saved: " + upload.getFilename() + " with id: " + upload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }
}
