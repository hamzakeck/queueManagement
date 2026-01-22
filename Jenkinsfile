pipeline {
    agent any

    // Tools configured via system PATH - no Jenkins tool configuration needed
    // Make sure Maven and JDK are installed on the Jenkins agent

    environment {
        // Application settings
        APP_NAME = 'queue-management'
        WAR_FILE = "target/${APP_NAME}.war"
        
        // Java and Maven paths (adjust if needed)
        JAVA_HOME = "${env.JAVA_HOME ?: 'C:\\Program Files\\Java\\jdk-11'}"
        MAVEN_HOME = "${env.MAVEN_HOME ?: 'C:\\Program Files\\Apache\\maven'}"
        PATH = "${MAVEN_HOME}\\bin;${JAVA_HOME}\\bin;${env.PATH}"
        
        // Tomcat deployment settings (update these for your environment)
        TOMCAT_URL = 'http://localhost:8080'
        
        // Database settings for testing
        DB_HOST = 'localhost'
        DB_NAME = 'queue_management_test'
    }

    options {
        // Keep only last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Add timestamps to console output
        timestamps()
        // Timeout after 30 minutes
        timeout(time: 30, unit: 'MINUTES')
        // Don't run concurrent builds
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                bat 'mvn clean compile -DskipTests'
            }
            post {
                failure {
                    echo 'Build failed!'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                bat 'mvn test'
            }
            post {
                always {
                    // Publish test results
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
                failure {
                    echo 'Some tests failed!'
                }
            }
        }

        stage('Code Quality') {
            steps {
                echo 'Running code quality checks...'
                // Uncomment if you have SonarQube configured
                // withSonarQubeEnv('SonarQube') {
                //     bat 'mvn sonar:sonar'
                // }
                echo 'Code quality check placeholder - configure SonarQube for actual analysis'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the application...'
                bat 'mvn package -DskipTests'
            }
            post {
                success {
                    // Archive the WAR file
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                }
            }
        }

        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Deploying to Development environment...'
                script {
                    deployToTomcat('dev')
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'staging'
            }
            steps {
                echo 'Deploying to Staging environment...'
                script {
                    deployToTomcat('staging')
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'master'
            }
            steps {
                echo 'Deploying to Production environment...'
                // Manual approval before production deployment
                input message: 'Deploy to Production?', ok: 'Deploy'
                script {
                    deployToTomcat('prod')
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed!'
            // Clean workspace
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
            // Uncomment to send notifications
            // emailext (
            //     subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            //     body: "Build succeeded: ${env.BUILD_URL}",
            //     to: 'team@example.com'
            // )
        }
        failure {
            echo 'Pipeline failed!'
            // Uncomment to send notifications
            // emailext (
            //     subject: "FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            //     body: "Build failed: ${env.BUILD_URL}",
            //     to: 'team@example.com'
            // )
        }
    }
}

// Helper function for Tomcat deployment
def deployToTomcat(String environment) {
    echo "Deploying to ${environment} environment..."
    
    // Simple deployment - copy WAR to Tomcat webapps folder
    // Adjust the path based on your Tomcat installation
    bat """
        copy /Y target\\queue-management.war "C:\\xampp\\tomcat\\webapps\\"
    """
    
    echo "Deployment to ${environment} completed!"
}
