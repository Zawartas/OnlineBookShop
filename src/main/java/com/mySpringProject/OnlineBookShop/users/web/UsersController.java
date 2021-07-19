package com.mySpringProject.OnlineBookShop.users.web;

import com.mySpringProject.OnlineBookShop.users.port.UserRegistrationUseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserRegistrationUseCase userRegistrationuseCase;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterCommand command) {
        return userRegistrationuseCase
                .register(command.getUsername(), command.getPassword())
                .handle(
                    userEntity -> ResponseEntity.accepted().build(),
                    error -> ResponseEntity.badRequest().body(error)
                );
    }

    @Data
    static class RegisterCommand {
        @Email
        String username;
        @Size(min = 3, max = 100)
        String password;
    }

}
