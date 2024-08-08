package com.example.demo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class JenkinsJobConfigFetcher {

    private static final String JENKINS_URL = "http://localhost:8080"; // Your Jenkins server URL
    private static final String JENKINS_USER = "vikramfa1"; // Jenkins username
    private static final String JENKINS_TOKEN = "11eb5658c4878b1bea361852c0e1b6db80"; // Jenkins API token

    public static void main(String[] args) {
        try {
            String jobName = "Developer-utility-pipeline"; // Replace with your job name
            String xmlConfig = getJobConfigXml(jobName);
            System.out.println("Job Configuration XML:\n" + xmlConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getJobConfigXml(String jobName) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Construct the request URL to fetch job configuration XML
        String url = JENKINS_URL + "/job/" + jobName + "/config.xml";

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic " + getBase64Credentials(JENKINS_USER, JENKINS_TOKEN))
                .build();

        // Execute the request and get the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    private static String getBase64Credentials(String username, String token) {
        String credentials = username + ":" + token;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}

