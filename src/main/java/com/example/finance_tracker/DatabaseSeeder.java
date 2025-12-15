package com.example.finance_tracker;

import com.example.finance_tracker.services.SeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true"
)
public class DatabaseSeeder implements ApplicationRunner {

    private final SeedService seedService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedService.seed();
    }
}

