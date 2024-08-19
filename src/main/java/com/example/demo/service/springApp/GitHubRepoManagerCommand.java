package com.example.demo.service.springApp;

import java.io.IOException;

import com.example.demo.DTO.OperationsDTO;
import com.example.demo.configuration.GithubConfig;
import com.example.demo.service.Command;
import com.example.demo.service.TaskUpdateStatusHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Component("SPRING_GITHUB_CLONE")
@Slf4j
public class GitHubRepoManagerCommand implements Command {

    @Autowired
    private GithubConfig githubConfig;

    @Autowired
    private TaskUpdateStatusHelper taskUpdateStatusHelper;

    @Override
    public void execute(OperationsDTO operationsDTO) throws Exception {


        String newRepoUrl = "https://github.com/vikramfa1/"+operationsDTO.getJobName()+".git";

        Path directoryPath = Paths.get("/Users/v0s05vp/Documents/docs/repo_temp/repo");
        deleteDirectory(directoryPath);
        File localRepoDir = new File("/Users/v0s05vp/Documents/docs/repo_temp/repo"); // Local directory to clone the repository

        createRepository(operationsDTO.getJobName(), "This is a new repository created via API");
        log.info("Cloning repository...");
        Git git = Git.cloneRepository()
                .setURI(githubConfig.getJavaSpringRepo())
                .setDirectory(localRepoDir)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubConfig.getUsername(), githubConfig.getToken() ))
                .call();

        log.info("Configuring new remote...");
        git.remoteSetUrl().setRemoteName("origin").setRemoteUri(new URIish(newRepoUrl)).call();

        log.info("Pushing to new repository...");
        git.push()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubConfig.getUsername(), githubConfig.getToken() ))
                .call();

        log.info("Repository cloned and pushed successfully.");
        Thread.sleep(Duration.ofSeconds(15).toMillis());
        taskUpdateStatusHelper.updateTaskStatus(operationsDTO.getTaskId(), Long.parseLong(operationsDTO.getJobId()), operationsDTO.getOperationType(), operationsDTO.getJobName(),"COMPLETED");
    }
    public static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path entry : directoryStream) {
                    if (Files.isDirectory(entry)) {
                        deleteDirectory(entry); // Recursively delete subdirectories
                    } else {
                        Files.delete(entry); // Delete files
                    }
                }
            }
            Files.delete(path); // Finally delete the directory itself
        } else {
            log.info("Directory does not exist.");
        }
    }
    private void createRepository(String repoName, String description) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Create repository JSON payload
        String json = "{ \"name\": \"" + repoName + "\", \"description\": \"" + description + "\", \"private\": false }";

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(githubConfig.getUrl() + "/user/repos")
                .post(body)
                .header("Authorization", "token " + githubConfig.getToken())
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            log.info("Repository created: " + response.body().string());
        }
    }
}

