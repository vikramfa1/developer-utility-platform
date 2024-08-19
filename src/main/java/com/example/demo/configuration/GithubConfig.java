package com.example.demo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class GithubConfig {

    @Value("${repo.github.url}")
    private String url;

    @Value("${repo.github.creds.username}")
    private String username;

    @Value("${repo.github.creds.token}")
    private String token;

    @Value("${repo.github.java.lib.repo}")
    private String javaLibRepo;

    @Value("${repo.github.java.spring.repo}")
    private String javaSpringRepo;
}
