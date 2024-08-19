package com.example.demo.service;

import com.example.demo.DTO.TaskUpdatesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class TaskUpdateStatusHelper {

    @Autowired
    private RestTemplate restTemplate;

    public void updateTaskStatus(String task, Long id, String webAppType, String jobName, String taskStatus) {
        log.info("updating task status: task: {}, id:{}, appType:{}, taskStatus: {}", task, id, webAppType, taskStatus);
        String endpointUrl = "http://localhost:8084/v1/job/"+id+"/update";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);

        TaskUpdatesDTO operationsDTO = TaskUpdatesDTO.builder().taskId(task).jobId(id).operationType(webAppType).jobName(jobName).taskStatus(taskStatus).build();
        HttpEntity<TaskUpdatesDTO> requestEntity = new HttpEntity<>(operationsDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                endpointUrl,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            log.info("POST request successful: " + response.getBody());
        } else {
            log.error("POST request failed: " + response.getStatusCode());
        }
    }
}
