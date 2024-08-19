package com.example.demo.service.javaLib;

import com.example.demo.DTO.OperationsDTO;
import com.example.demo.configuration.GithubConfig;
import com.example.demo.service.Command;
import com.example.demo.service.TaskUpdateStatusHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component("JAVA_LIB_GITHUB_CLONE")
@Slf4j
public class JavaLibGitHubRepoManagerCommand implements Command {

    @Autowired
    private GithubConfig githubConfig;

    @Autowired
    private TaskUpdateStatusHelper taskUpdateStatusHelper;

    public void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path entry : directoryStream) {
                    if (Files.isDirectory(entry)) {
                        deleteDirectory(entry);
                    } else {
                        Files.delete(entry);
                    }
                }
            }
            Files.delete(path);
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

    @Override
    public void execute(OperationsDTO operationsDTO) throws Exception {

        String newRepoUrl = "https://github.com/vikramfa1/"+operationsDTO.getJobName()+".git";


        Path directoryPath = Paths.get("/Users/v0s05vp/Documents/docs/repo_temp/repo");
        deleteDirectory(directoryPath);
        File localRepoDir = new File("/Users/v0s05vp/Documents/docs/repo_temp/repo");


        createRepository(operationsDTO.getJobName(), "This is a new repository created via API");

        log.info("Cloning repository...");

        Git git = Git.cloneRepository()
                .setURI(githubConfig.getJavaLibRepo())
                .setDirectory(localRepoDir)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubConfig.getUsername(), githubConfig.getToken() ))
                .call();
        modifyGithubRepo(operationsDTO);
        commitChanges(git, "updated");

        // Configure the new repository URL
        log.info("Configuring new remote...");
        git.remoteSetUrl().setRemoteName("origin").setRemoteUri(new URIish(newRepoUrl)).call();

        // Add and push to the new repository
        log.info("Pushing to new repository...");
        git.push()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubConfig.getUsername(), githubConfig.getToken() ))
                .call();

        log.info("Repository cloned and pushed successfully.");
        Thread.sleep(Duration.ofSeconds(15).toMillis());
        taskUpdateStatusHelper.updateTaskStatus(operationsDTO.getTaskId(), Long.parseLong(operationsDTO.getJobId()), operationsDTO.getOperationType(), operationsDTO.getJobName(),"COMPLETED");
    }
    public static void commitChanges(Git git, String commitMessage) throws IOException, GitAPIException {
            git.add().addFilepattern(".").call();
            git.commit().setMessage(commitMessage).call();

        }

    private void modifyGithubRepo(OperationsDTO operationsDTO) {
        Path filePath = Paths.get("/Users/v0s05vp/Documents/docs/repo_temp/repo/pom.xml");
        String oldContent = "bootstapJavaLib";
        String newContent = operationsDTO.getJobName();

        try {
            List<String> lines = Files.readAllLines(filePath);
            List<String> updatedLines = lines.stream()
                    .map(line -> line.replace(oldContent, newContent))
                    .collect(Collectors.toList());

            Files.write(filePath, updatedLines);
            log.info("File content replaced successfully.");
        } catch (IOException e) {
            log.error("Error reading or writing to file: " + e.getMessage(), e);
        }
    }
}

