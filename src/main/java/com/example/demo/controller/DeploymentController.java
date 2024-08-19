package com.example.demo.controller;

import com.example.demo.DTO.OperationsDTO;
import com.example.demo.DTO.OperationsResponseDTO;
import com.example.demo.service.springApp.KubernetesDeploymentCommand;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class DeploymentController {

    @Autowired
    private KubernetesDeploymentCommand kubernetesService;

    @Autowired
    private TaskService taskService;

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

    @PostMapping("/task")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OperationsResponseDTO executeTask(@RequestBody OperationsDTO operationsDTO) throws Exception {
        taskService.executeTask(operationsDTO);
        return OperationsResponseDTO.builder().status("submitted").build();
    }
}

