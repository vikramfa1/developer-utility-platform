pipeline {
    agent any

    environment {
        // Define any environment variables here if needed
        MAVEN_HOME = tool name: 'Maven 3.8.1', type: 'maven'
        // Define environment variables
        GITHUB_USERNAME = 'vikramfa1'
        DOCKER_IMAGE = "ghcr.io/${GITHUB_USERNAME}/developer-utility-app"
        DOCKER_TAG = "latest"
        GITHUB_TOKEN = credentials('github-credentials-id')
    }

    stages {
        stage('Compile') {
            steps {
                echo "1maven path: ${MAVEN_HOME}/bin/mvn clean compile"
                // Compile the project using Maven
                sh "${MAVEN_HOME}/bin/mvn clean compile"
            }
        }

        stage('Test') {
            steps {
                // Run tests using Maven
                sh "${MAVEN_HOME}/bin/mvn test"
            }
        }

        stage('Build') {
            steps {
                sh '${MAVEN_HOME}/bin/mvn -B -DskipTests clean package'
            }
        }

        stage('Build Docker Image') {
                    steps {
                        // Build the Docker image
                        sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'
                    }
                }

        stage('Login to GitHub Container Registry') {
            steps {
                // Login to GitHub Container Registry using GitHub token
                sh "echo ${GITHUB_TOKEN} | docker login ghcr.io -u ${GITHUB_USERNAME} --password-stdin"
            }
        }

        stage('Push Docker Image') {
                    steps {
                        script {
                            sh 'docker push ${DOCKER_IMAGE}:${DOCKER_TAG}'
                        }
                    }
                }

                stage('Logout from GitHub Container Registry') {
                    steps {
                        script {
                            sh 'docker logout ghcr.io'
                        }
                    }
                }
    }

    post {
        // Actions to perform after the pipeline completes
        always {
            // Archive test results
            junit '**/target/test-*.xml'
            // Clean up
            cleanWs()
        }
    }
}