package com.mySpringProject.OnlineBookShop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
class ApplicationStartup implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("------START------");
    }
}
