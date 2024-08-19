package com.example.demo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
public class JenkinsConfig {

    @Value("${ci.jenkins.url}")
    private String url;

    @Value("${ci.jenkins.creds.username}")
    private String username;

    @Value("${ci.jenkins.creds.token}")
    private String token;
}
