package com.example.demo.service.springApp;
import com.example.demo.DTO.OperationsDTO;
import com.example.demo.configuration.K8SConfig;
import com.example.demo.service.Command;
import com.example.demo.service.TaskUpdateStatusHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.openapi.models.V1LoadBalancerIngress;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;

@Component("SPRING_K8S_DEPLOYMENT")
@Slf4j
public class KubernetesDeploymentCommand implements Command {

    private final CoreV1Api coreV1Api;
    private final AppsV1Api appsV1Api;
    private final ObjectMapper objectMapper;

    @Autowired
    private K8SConfig k8SConfig;

    @Autowired
    private TaskUpdateStatusHelper taskUpdateStatusHelper;

    public KubernetesDeploymentCommand() throws Exception {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        this.coreV1Api = new CoreV1Api(client);
        this.appsV1Api = new AppsV1Api(client);
        this.objectMapper = new ObjectMapper(new YAMLFactory());
    }

    @Override
    public void execute(OperationsDTO operationsDTO) throws Exception {
        deploy("my-namespace");
        Thread.sleep(Duration.ofSeconds(15).toMillis());
        taskUpdateStatusHelper.updateTaskStatus(operationsDTO.getTaskId(), Long.parseLong(operationsDTO.getJobId()), operationsDTO.getOperationType(), operationsDTO.getJobName(),"COMPLETED");
    }

    public String deploy(String namespace) throws Exception {
        createNamespace(namespace);
        createNamespacedSecret(namespace);
        createDeployment(namespace);
        createService(namespace);

        Thread.sleep(10000);

        return getServiceEndpoint(namespace, "my-app-service");
    }

    public void createNamespace(String namespace) throws Exception {
        log.info("creating namespace");
        V1Namespace ns = loadYamlFromResources(k8SConfig.getNamespaceYaml(), V1Namespace.class);
        CoreV1Api.APIcreateNamespaceRequest createResult = coreV1Api.createNamespace(ns);
        createResult.execute();
        log.info("namespace created");
    }

    public void createDeployment(String namespace) throws Exception {
        log.info("creating deployment");
        V1Deployment deployment = loadYamlFromResources(k8SConfig.getDeploymentYaml(), V1Deployment.class);
        AppsV1Api.APIcreateNamespacedDeploymentRequest createResult = appsV1Api.createNamespacedDeployment(namespace, deployment);
        createResult.execute();
        log.info("deployment created");

    }

    public void createService(String namespace) throws Exception {
        log.info("creating service");
        V1Service service = loadYamlFromResources(k8SConfig.getSvcYaml(), V1Service.class);
        CoreV1Api.APIcreateNamespacedServiceRequest createResult = coreV1Api.createNamespacedService(namespace, service);
        createResult.execute();
        log.info("service created");
    }

    public void createNamespacedSecret(String namespace) throws Exception {
        log.info("creating namespaced secret");
        V1Secret service = loadYamlFromResources(k8SConfig.getSecretsYaml(), V1Secret.class);
        CoreV1Api.APIcreateNamespacedSecretRequest createResult = coreV1Api.createNamespacedSecret(namespace, service);
        createResult.execute();
        log.info("service created");
    }

    public String getServiceEndpoint(String namespace, String serviceName) throws Exception {
        try {

            CoreV1Api.APIreadNamespacedServiceRequest response = coreV1Api.readNamespacedService(serviceName, namespace);
            V1Service service = response.executeWithHttpInfo().getData();

            List<V1LoadBalancerIngress> ingressList = service.getStatus().getLoadBalancer().getIngress();
            if (ingressList != null && !ingressList.isEmpty()) {
                V1LoadBalancerIngress ingress = ingressList.get(0);
                return (ingress.getHostname() != null) ? ingress.getHostname() : ingress.getIp();
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving service endpoint", e);
        }
        return null;
    }

    private <T> T loadYamlFromResources(String fileName, Class<T> clazz) throws Exception {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            return objectMapper.readValue(reader, clazz);
        }
    }
}

