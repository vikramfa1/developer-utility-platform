package com.example.demo.service.springApp;

import com.example.demo.configuration.JenkinsConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JenkinsJobConfigFetcher {

    @Autowired
    private JenkinsConfig jenkinsConfig;

    private String getJobConfigXml(String jobName) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Construct the request URL to fetch job configuration XML
        String url = jenkinsConfig.getUrl() + "/job/" + jobName + "/config.xml";

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic " + getBase64Credentials(jenkinsConfig.getUsername(), jenkinsConfig.getToken()))
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

