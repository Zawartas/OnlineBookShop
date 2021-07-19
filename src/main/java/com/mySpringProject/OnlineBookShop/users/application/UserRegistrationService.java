package com.mySpringProject.OnlineBookShop.users.application;

import com.mySpringProject.OnlineBookShop.user.db.UserEntityRepository;
import com.mySpringProject.OnlineBookShop.user.domain.UserEntity;
import com.mySpringProject.OnlineBookShop.users.port.UserRegistrationUseCase;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserRegistrationService implements UserRegistrationUseCase {

    private final UserEntityRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponse register(String username, String password) {
        if (repository.findByUsernameIgnoreCase(username).isPresent()) {
            RegisterResponse.failure("User already exists");
        }
        UserEntity entity = new UserEntity(username, passwordEncoder.encode(password));
        return RegisterResponse.success(repository.save(entity));
    }
}
