package com.example.demo.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskUpdatesDTO {
    String operationType;
    String taskId;
    Long jobId;
    String jobName;
    String taskStatus;

}
