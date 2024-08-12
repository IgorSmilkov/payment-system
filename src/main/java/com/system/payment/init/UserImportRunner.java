package com.system.payment.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.system.payment.service.UserImportService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserImportRunner implements CommandLineRunner {

    private final UserImportService userImportService;

    @Override
    public void run(String... args) throws Exception {
        userImportService.importUsers();
    }
}
