pipeline {
    agent any

    tools {
        maven 'Maven-3.9'  // Configure this in Jenkins Global Tool Configuration
        jdk 'JDK-11'       // Configure this in Jenkins Global Tool Configuration
    }

    environment {
        // Application settings
        APP_NAME = 'queue-management'
        WAR_FILE = "target/${APP_NAME}.war"
        
        // Tomcat deployment settings (update these for your environment)
        TOMCAT_URL = 'http://localhost:8080'
        TOMCAT_CREDENTIALS = credentials('tomcat-deployer')  // Configure in Jenkins credentials
        
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
                branch 'main'
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
    
    // Using curl to deploy to Tomcat Manager
    // Make sure Tomcat Manager is configured with deployment permissions
    bat """
        curl -u %TOMCAT_CREDENTIALS_USR%:%TOMCAT_CREDENTIALS_PSW% ^
             -T target/queue-management.war ^
             "${TOMCAT_URL}/manager/text/deploy?path=/queue-management&update=true"
    """
    
    echo "Deployment to ${environment} completed!"
}
