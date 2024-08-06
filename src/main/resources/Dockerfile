# Use the official Jenkins image as a base
FROM jenkins/jenkins:lts

# Switch to the root user to install Docker
USER root

# Install Docker
RUN apt-get update && apt-get install -y docker.io && rm -rf /var/lib/apt/lists/*

RUN echo 'root:123' | chpasswd
# Switch back to the Jenkins user
USER jenkins