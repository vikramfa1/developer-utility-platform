package com.example.demo.service;

import com.example.demo.DTO.OperationsDTO;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface TaskService {

    void executeTask(OperationsDTO operationsDTO) throws Exception;
}
