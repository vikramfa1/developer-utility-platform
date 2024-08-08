package com.example.demo.controller;

import com.example.demo.service.KubernetesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeploymentController {

    private final KubernetesService kubernetesService;

    public DeploymentController(KubernetesService kubernetesService) {
        this.kubernetesService = kubernetesService;
    }

    @GetMapping("/deploy")
    public String deploy(@RequestParam String namespace) throws Exception {
        kubernetesService.createNamespace(namespace);
        kubernetesService.createNamespacedSecret(namespace);
        kubernetesService.createDeployment(namespace);
        kubernetesService.createService(namespace);

        // Wait for the service to be exposed
        Thread.sleep(10000); // adjust the sleep time as needed

        return kubernetesService.getServiceEndpoint(namespace, "my-app-service");
    }
}

