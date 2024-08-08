package com.example.demo.service;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.QueueReference;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class JenkinsPipelineCreator {
    private static final String JENKINS_URL = "http://localhost:8080"; // Your Jenkins server URL
    private static final String JENKINS_USER = "vikramfa1"; // Jenkins username
    private static final String JENKINS_TOKEN = "11eb5658c4878b1bea361852c0e1b6db80"; // Jenkins API token

    public static void main(String[] args) {
        try {
            JenkinsServer jenkins = new JenkinsServer(new URI(JENKINS_URL), JENKINS_USER, JENKINS_TOKEN);

            // Create a new multibranch pipeline job
            createMultibranchPipelineJob(jenkins, "developer-utility-platform1", "https://github.com/vikramfa1/developer-utility-platform1.git");
            Thread.sleep(Duration.ofSeconds(20).toMillis());
            //triggerScan("developer-utility-platform1");
            //Thread.sleep(Duration.ofSeconds(20).toMillis());
            fetchJobs();
            //triggerScan1("developer-utility-platform1");
            Thread.sleep(Duration.ofSeconds(5).toMillis());
            //triggerBranchBuild("developer-utility-platform1","main");
            System.out.println("Multibranch pipeline job created successfully.");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void triggerScan1(String jobName) throws IOException {
        // Create an HttpClient with custom configuration
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)  // Connection timeout
                        .setSocketTimeout(5000)   // Socket timeout
                        .build())
                .build();

        // Create JenkinsHttpClient using the custom HttpClient
        JenkinsHttpClient jenkinsClient = new JenkinsHttpClient(URI.create(JENKINS_URL), JENKINS_USER, JENKINS_TOKEN);


        // Trigger the branch indexing scan
        String scanUrl = JENKINS_URL + "/job/" + jobName + "/scan";
        jenkinsClient.post(scanUrl);

        System.out.println("Branch scan triggered for job: " + jobName);
    }

    private static void triggerBranchBuild(String jobName, String branchName) throws IOException {
        // Create an HttpClient with custom configuration
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)  // Connection timeout
                        .setSocketTimeout(5000)   // Socket timeout
                        .build())
                .build();

        JenkinsHttpClient jenkinsClient = new JenkinsHttpClient(URI.create(JENKINS_URL), JENKINS_USER, JENKINS_TOKEN);

        // Construct URL to trigger the build for a specific branch
        String buildUrl = JENKINS_URL + "/job/" + jobName + "/job/" + branchName + "/build";
        jenkinsClient.post(buildUrl);

        System.out.println("Build triggered for branch: " + branchName);
    }

    private static void triggerJobForBranch(String jobName, String branchName) throws IOException {
        // Create an HttpClient with custom configuration
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)  // Connection timeout
                        .setSocketTimeout(5000)   // Socket timeout
                        .build())
                .build();

        // Create JenkinsHttpClient using the custom HttpClient
        JenkinsHttpClient jenkinsClient = new JenkinsHttpClient(URI.create(JENKINS_URL), JENKINS_USER, JENKINS_TOKEN);

        // Construct the URL to trigger the build for a specific branch
        String jobUrl = JENKINS_URL + "/job/" + jobName + "/build?branch=" + branchName;
        jenkinsClient.post(jobUrl); // Trigger the build

        System.out.println("Build triggered for branch: " + branchName);
    }
    private static void triggerScan(String jobName) throws IOException {
        // Create an HttpClient with custom configuration
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)  // Connection timeout
                        .setSocketTimeout(5000)   // Socket timeout
                        .build())
                .build();

        // Construct the URL to trigger the repository scan
        String scanUrl = JENKINS_URL + "/job/" + jobName + "/build?delay=0sec";

        // Create and execute the HTTP POST request
        HttpPost httpPost = new HttpPost(scanUrl);
        httpPost.setHeader("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((JENKINS_USER + ":" + JENKINS_TOKEN).getBytes()));

        try (var response = httpClient.execute(httpPost)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response: " + responseBody);
        } finally {
            httpClient.close();
        }
    }

    private static void createMultibranchPipelineJob(JenkinsServer jenkins, String jobName, String gitRepoUrl) throws IOException {
        String jobConfigXml = generateMultibranchPipelineConfigXml(gitRepoUrl);

        jenkins.createJob(jobName, jobConfigXml);
        System.out.println("Job created: " + jobName);
    }

    private static void fetchJobs() throws IOException, URISyntaxException {
        JenkinsServer jenkins = new JenkinsServer(new URI(JENKINS_URL), JENKINS_USER, JENKINS_TOKEN);
        Map<String, Job> jobs = jenkins.getJobs();

        // Print the list of job names
        for (Job job : jobs.values()) {
            System.out.println("Job Name: " + job.getName());
        }

    }
    private static void triggerJob(String jobName) throws IOException {
        // Create JenkinsHttpClient
        JenkinsHttpClient client = new JenkinsHttpClient(URI.create(JENKINS_URL), JENKINS_USER, JENKINS_TOKEN);

        // Trigger the job
        String jobUrl = JENKINS_URL + "/job/" + jobName + "/build";
        client.post(jobUrl); // Trigger the build

        System.out.println("Build triggered for job: " + jobName);
    }

    private static String generateMultibranchPipelineConfigXml(String gitRepoUrl) {
        return "<?xml version='1.1' encoding='UTF-8'?>\n" +
                "<org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin=\"workflow-multibranch@783.787.v50539468395f\">\n" +
                "  <actions/>\n" +
                "  <description>developer-utility-platform1</description>\n" +
                "  <properties/>\n" +
                "  <folderViews class=\"jenkins.branch.MultiBranchProjectViewHolder\" plugin=\"branch-api@2.1178.v969d9eb_c728e\">\n" +
                "    <owner class=\"org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject\" reference=\"../..\"/>\n" +
                "  </folderViews>\n" +
                "  <healthMetrics/>\n" +
                "  <icon class=\"jenkins.branch.MetadataActionFolderIcon\" plugin=\"branch-api@2.1178.v969d9eb_c728e\">\n" +
                "    <owner class=\"org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject\" reference=\"../..\"/>\n" +
                "  </icon>\n" +
                "  <orphanedItemStrategy class=\"com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy\" plugin=\"cloudbees-folder@6.928.v7c780211d66e\">\n" +
                "    <pruneDeadBranches>true</pruneDeadBranches>\n" +
                "    <daysToKeep>-1</daysToKeep>\n" +
                "    <numToKeep>-1</numToKeep>\n" +
                "    <abortBuilds>false</abortBuilds>\n" +
                "  </orphanedItemStrategy>\n" +
                "  <triggers/>\n" +
                "  <disabled>false</disabled>\n" +
                "  <sources class=\"jenkins.branch.MultiBranchProject$BranchSourceList\" plugin=\"branch-api@2.1178.v969d9eb_c728e\">\n" +
                "    <data>\n" +
                "      <jenkins.branch.BranchSource>\n" +
                "        <source class=\"org.jenkinsci.plugins.github_branch_source.GitHubSCMSource\" plugin=\"github-branch-source@1793.v1831e9c68d77\">\n" +
                "          <id></id>\n" +
                "          <apiUri>https://api.github.com</apiUri>\n" +
                "          <credentialsId>02525803-2bbb-4763-a444-fbd719e5ae2d</credentialsId>\n" +
                "          <repoOwner>vikramfa1</repoOwner>\n" +
                "          <repository>developer-utility-platform1</repository>\n" +
                "          <repositoryUrl>https://github.com/vikramfa1/developer-utility-platform1.git</repositoryUrl>\n" +
                "          <traits>\n" +
                "            <org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait>\n" +
                "              <strategyId>3</strategyId>\n" +
                "            </org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait>\n" +
                "            <org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait>\n" +
                "              <strategyId>2</strategyId>\n" +
                "            </org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait>\n" +
                "            <org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait>\n" +
                "              <strategyId>2</strategyId>\n" +
                "              <trust class=\"org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission\"/>\n" +
                "            </org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait>\n" +
                "          </traits>\n" +
                "        </source>\n" +
                "        <strategy class=\"jenkins.branch.DefaultBranchPropertyStrategy\">\n" +
                "          <properties class=\"empty-list\"/>\n" +
                "        </strategy>\n" +
                "      </jenkins.branch.BranchSource>\n" +
                "    </data>\n" +
                "    <owner class=\"org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject\" reference=\"../..\"/>\n" +
                "  </sources>\n" +
                "  <factory class=\"org.jenkinsci.plugins.workflow.multibranch.WorkflowBranchProjectFactory\">\n" +
                "    <owner class=\"org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject\" reference=\"../..\"/>\n" +
                "    <scriptPath>Jenkinsfile</scriptPath>\n" +
                "  </factory>\n" +
                "</org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject>";
    }
}
