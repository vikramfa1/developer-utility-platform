pipeline {
    agent any

    environment {
        // Define any environment variables here if needed
        MAVEN_HOME = tool name: 'Maven 3.8.1', type: 'maven'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout source code from GitHub
                git url: 'https://github.com/vikramfa1/developer-utility-platform.git'
            }
        }

        stage('Compile') {
            steps {
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