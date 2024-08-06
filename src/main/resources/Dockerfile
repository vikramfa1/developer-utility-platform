# Use the official Jenkins image as a base
FROM jenkins/jenkins:lts

# Switch to the root user to install Docker
USER root

# Install Docker
#RUN apt-get update && apt-get install -y docker.io && rm -rf /var/lib/apt/lists/*
# Install Docker
RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates curl gnupg2 software-properties-common && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" && \
    apt-get update && \
    apt-get install -y docker-ce-cli

RUN echo 'root:123' | chpasswd
# Switch back to the Jenkins user
USER jenkins