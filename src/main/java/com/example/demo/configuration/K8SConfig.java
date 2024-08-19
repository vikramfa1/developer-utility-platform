package com.example.demo.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class K8SConfig {

    @Value("${k8s.script.namespace}")
    private String namespaceYaml;

    @Value("${k8s.script.secrets}")
    private String secretsYaml;

    @Value("${k8s.script.deployment}")
    private String deploymentYaml;
    @Value("${k8s.script.svc}")
    private String svcYaml;
}
