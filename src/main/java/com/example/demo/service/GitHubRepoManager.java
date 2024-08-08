package com.example.demo.service;

import java.io.IOException;

import okhttp3.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class GitHubRepoManager {

    private static final String GITHUB_API_URL = "https://api.github.com";
    static String GITHUB_TOKEN  = "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";

    public static void main(String[] args) {
        String sourceRepoUrl = "https://github.com/vikramfa1/developer-utility-platform.git"; // Source repository URL
        String newRepoUrl = "https://github.com/vikramfa1/developer-utility-platform1.git"; // New repository URL
        String username = "vikramfa";


        File localRepoDir = new File("D:\\dsa_system_design_docs-main\\repo"); // Local directory to clone the repository

        try {
            // Create the new repository
            createRepository("developer-utility-platform1", "This is a new repository created via API");
            // Clone the source repository
            System.out.println("Cloning repository...");
            Git git = Git.cloneRepository()
                    .setURI(sourceRepoUrl)
                    .setDirectory(localRepoDir)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, GITHUB_TOKEN ))
                    .call();

            // Configure the new repository URL
            System.out.println("Configuring new remote...");
            git.remoteSetUrl().setRemoteName("origin").setRemoteUri(new URIish(newRepoUrl)).call();

            // Add and push to the new repository
            System.out.println("Pushing to new repository...");
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, GITHUB_TOKEN ))
                    .call();

            System.out.println("Repository cloned and pushed successfully.");

        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createRepository(String repoName, String description) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Create repository JSON payload
        String json = "{ \"name\": \"" + repoName + "\", \"description\": \"" + description + "\", \"private\": false }";

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(GITHUB_API_URL + "/user/repos")
                .post(body)
                .header("Authorization", "token " + GITHUB_TOKEN)
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println("Repository created: " + response.body().string());
        }
    }
}

