pipeline {
    agent any

    environment {
        // Define any environment variables here if needed
        MAVEN_HOME = tool name: 'Maven 3.8.1', type: 'maven'
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