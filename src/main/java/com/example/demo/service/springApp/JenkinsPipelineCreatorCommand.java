package com.example.demo.service.springApp;

import com.example.demo.DTO.OperationsDTO;
import com.example.demo.configuration.JenkinsConfig;
import com.example.demo.helper.JenkinsHelper;
import com.example.demo.service.Command;
import com.example.demo.service.TaskUpdateStatusHelper;
import com.offbytwo.jenkins.JenkinsServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;

@Component("SPRING_JENKINS_PIPELINE")
@Slf4j
public class JenkinsPipelineCreatorCommand implements Command {

    @Autowired
    private JenkinsConfig jenkinsConfig;

    @Autowired
    private JenkinsHelper jenkinsHelper;
    @Autowired
    private TaskUpdateStatusHelper taskUpdateStatusHelper;
    @Override
    public void execute(OperationsDTO operationsDTO) throws Exception {


        JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getToken());

        // Create a new multibranch pipeline job
        JenkinsHelper.createMultibranchPipelineJob(jenkins, operationsDTO.getJobName(), "https://github.com/vikramfa1/"+operationsDTO.getJobName()+".git");
        Thread.sleep(Duration.ofSeconds(20).toMillis());

        jenkinsHelper.fetchJobs();

        Thread.sleep(Duration.ofSeconds(5).toMillis());
        log.info("Multibranch pipeline job created successfully.");
    }
}
