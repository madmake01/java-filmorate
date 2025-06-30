package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "ru.yandex.practicum.filmorate.storage",
})
public class TestJdbcConfig {
}
