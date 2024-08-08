package com.example.demo.service;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class KubernetesService {

    private final CoreV1Api coreV1Api;
    private final AppsV1Api appsV1Api;
    private final ObjectMapper objectMapper;

    public KubernetesService() throws Exception {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        this.coreV1Api = new CoreV1Api(client);
        this.appsV1Api = new AppsV1Api(client);
        this.objectMapper = new ObjectMapper(new YAMLFactory());
    }

    public void createNamespace(String namespace) throws Exception {
        V1Namespace ns = loadYamlFromResources("namespace.yaml", V1Namespace.class);
        CoreV1Api.APIcreateNamespaceRequest createResult = coreV1Api.createNamespace(ns);
        createResult.execute();
    }

    public void createDeployment(String namespace) throws Exception {
        V1Deployment deployment = loadYamlFromResources("deployment.yaml", V1Deployment.class);
        AppsV1Api.APIcreateNamespacedDeploymentRequest createResult = appsV1Api.createNamespacedDeployment(namespace, deployment);
        createResult.execute();

    }

    public void createService(String namespace) throws Exception {
        V1Service service = loadYamlFromResources("service.yaml", V1Service.class);
        CoreV1Api.APIcreateNamespacedServiceRequest createResult = coreV1Api.createNamespacedService(namespace, service);
        createResult.execute();
    }

    public void createNamespacedSecret(String namespace) throws Exception {
        V1Secret service = loadYamlFromResources("my-app-sec.yaml", V1Secret.class);
        CoreV1Api.APIcreateNamespacedSecretRequest createResult = coreV1Api.createNamespacedSecret(namespace, service);
        createResult.execute();
    }

    public String getServiceEndpoint(String namespace, String serviceName) throws Exception {
        try {
            // Reading the service details
            CoreV1Api.APIreadNamespacedServiceRequest response = coreV1Api.readNamespacedService(serviceName, namespace);
            V1Service service = response.executeWithHttpInfo().getData();

            // Extracting the load balancer ingress details
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

