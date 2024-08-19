package com.example.demo.service;

import com.example.demo.DTO.OperationsDTO;

public interface Command {
    void execute(OperationsDTO operationsDTO) throws Exception;
}
