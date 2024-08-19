package com.example.demo.service;

import com.example.demo.DTO.OperationsDTO;
import com.example.demo.DTO.TaskUpdatesDTO;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    Map<String, Command> taskServiceMap;
    @Override
    @Async
    public void executeTask(OperationsDTO operationsDTO) throws Exception {
        taskServiceMap.get(operationsDTO.getOperationType()+"_"+operationsDTO.getTaskId()).execute(operationsDTO);
    }
}
