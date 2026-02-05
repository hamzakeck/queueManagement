pipeline {
    agent any

    // Prefer Jenkins-managed tools (works well on Docker/Linux controllers too)
    tools {
        // Configure this in: Manage Jenkins > Tools
        maven 'Maven-3.9'
        // Optional (only if you configured it):
        // jdk 'JDK-11'
    }

    environment {
        // Application settings
        APP_NAME = 'queue-management'
        WAR_FILE = "target/${APP_NAME}.war"
        
        // Tomcat deployment settings (update these for your environment)
        TOMCAT_URL = 'http://localhost:8080'
        
        // Database settings for testing
        DB_HOST = 'localhost'
        DB_NAME = 'queue_management_test'
    }

    options {
        // Avoid double checkout (Declarative: Checkout SCM) + our explicit checkout
        skipDefaultCheckout(true)
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
                script {
                    runMvn('clean compile -DskipTests')
                }
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
                script {
                    runMvn('test')
                }
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

        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube code analysis...'
                // If SonarQube isn't configured yet, don't fail the whole build.
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    script {
                        withSonarQubeEnv('SonarQube') {
                            runMvn('sonar:sonar -Dsonar.projectKey=queue-management-system -Dsonar.projectName="Queue Management System" -Dsonar.java.binaries=target/classes')
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo 'Checking SonarQube Quality Gate...'
                // Quality Gate requires SonarQube webhook + Jenkins SonarQube server config.
                // Keep it non-blocking until SonarQube is fully wired.
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the application...'
                script {
                    runMvn('package -DskipTests')
                }
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
                allOf {
                    branch 'develop'
                    expression { return !isUnix() }
                    expression { return (env.ENABLE_DEPLOY ?: 'false').toBoolean() }
                }
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
                allOf {
                    branch 'staging'
                    expression { return !isUnix() }
                    expression { return (env.ENABLE_DEPLOY ?: 'false').toBoolean() }
                }
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
                allOf {
                    branch 'main'
                    expression { return !isUnix() }
                    expression { return (env.ENABLE_DEPLOY ?: 'false').toBoolean() }
                }
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

// Cross-platform Maven runner (Docker/Linux uses sh; Windows uses bat)
def runMvn(String args) {
    if (isUnix()) {
        sh "mvn -B ${args}"
    } else {
        bat "mvn -B ${args}"
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
