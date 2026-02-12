pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
        APP_NAME = 'queue-management'
        WAR_FILE = "target/${APP_NAME}.war"
        TOMCAT_URL = 'http://localhost:8080'
        
        // SonarQube Server Details (for API calls)
        SQ_URL = 'http://localhost:9000'
        SQ_PROJECT_KEY = 'queue-management-system'
        // Ideally, use credentials() here, but for local study env, plain text is fine
        SQ_AUTH = 'admin:admin' 
    }

    options {
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
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
        }

        stage('Unit Tests') {
            steps {
                echo 'Running unit tests...'
                script {
                    runMvn('verify')
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube code analysis...'
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    script {
                        withSonarQubeEnv('SonarQube') {
                            runMvn("sonar:sonar -Dsonar.projectKey=${SQ_PROJECT_KEY} -Dsonar.projectName='Queue Management System' -Dsonar.java.binaries=target/classes -Dsonar.junit.reportPaths=target/surefire-reports -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml")
                        }
                    }
                }
            }
        }

        stage('Quality Gate & Export') {
            steps {
                echo 'Checking Quality Gate and Exporting Issues for Agent...'
                script {
                    // 1. Wait for SonarQube to finish processing
                    timeout(time: 5, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        
                        // 2. Write simple status file for the Agent
                        writeFile file: 'quality_gate_status.txt', text: "Status: ${qg.status}"
                        
                        // 3. Download the specific issues to a JSON file so Claude can read them
                        // This fetches all open issues for this project
                        downloadSonarIssues()
                        
                        if (qg.status != 'OK') {
                            error "Quality Gate failure: ${qg.status}"
                        }
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
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                }
            }
        }
        
        // ... (Your Deploy stages remain unchanged) ...
        stage('Deploy to Dev') {
            when {
                allOf {
                    branch 'main'
                    expression { return !isUnix() }
                    expression { return (env.ENABLE_DEPLOY ?: 'false').toBoolean() }
                }
            }
            steps {
                script { deployToTomcat('dev') }
            }
        }
    }

    post {
        always {
            // CRITICAL FOR AGENT: Write the final build outcome to a file
            writeFile file: 'jenkins_build_status.txt', text: "Result: ${currentBuild.currentResult}"
            cleanWs(deleteDirs: true, patterns: [[pattern: 'target/**', type: 'INCLUDE']]) 
            // Note: We keep the txt/json files we just made so the agent can read them!
        }
    }
}

// --- Helper Functions ---

def runMvn(String args) {
    if (isUnix()) {
        sh "mvn -B ${args}"
    } else {
        bat "mvn -B ${args}"
    }
}

def deployToTomcat(String environment) {
    echo "Deploying to ${environment}..."
    bat """
        copy /Y target\\queue-management.war "C:\\xampp\\tomcat\\webapps\\"
    """
}

def downloadSonarIssues() {
    def apiUrl = "${env.SQ_URL}/api/issues/search?componentKeys=${env.SQ_PROJECT_KEY}&resolved=false"
    
    echo "Downloading SonarQube issues from ${apiUrl}..."
    
    if (isUnix()) {
        sh "curl -u ${env.SQ_AUTH} '${apiUrl}' -o sonar-issues.json"
    } else {
        // Windows curl (available in Win 10+)
        bat "curl -u ${env.SQ_AUTH} \"${apiUrl}\" -o sonar-issues.json"
    }
}