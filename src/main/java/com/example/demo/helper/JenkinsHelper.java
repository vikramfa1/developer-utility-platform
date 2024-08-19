package com.example.demo.helper;

import com.example.demo.configuration.JenkinsConfig;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@Component
public class JenkinsHelper {

    @Autowired
    private JenkinsConfig jenkinsConfig;

    public static void createMultibranchPipelineJob(JenkinsServer jenkins, String jobName, String gitRepoUrl) throws IOException {
        String jobConfigXml = generateMultibranchPipelineConfigXml(jobName, gitRepoUrl);

        jenkins.createJob(jobName, jobConfigXml);
        log.info("Job created: " + jobName);
    }

    public void fetchJobs() throws IOException, URISyntaxException {
        JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getToken());
        Map<String, Job> jobs = jenkins.getJobs();

        // Print the list of job names
        for (Job job : jobs.values()) {
            log.info("Job Name: " + job.getName());
        }

    }

    private static String generateMultibranchPipelineConfigXml(String jobName, String gitRepoUrl) {
        return "<?xml version='1.1' encoding='UTF-8'?>\n" +
                "<org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin=\"workflow-multibranch@783.787.v50539468395f\">\n" +
                "  <actions/>\n" +
                "  <description>"+jobName+"</description>\n" +
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
                "<credentialsId>ee031808-8be9-4ae9-8343-d2508217b74c</credentialsId>\n"+
                "          <repoOwner>vikramfa1</repoOwner>\n" +
                "          <repository>"+jobName+"</repository>\n" +
                "          <repositoryUrl>"+gitRepoUrl+"</repositoryUrl>\n" +
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
