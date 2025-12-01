pipeline {
    agent any
    tools {
        jdk '21'
        gradle '8.10'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh './gradlew clean assemble'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
                sh './gradlew test jacocoTestReport'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                    publishHTML target: [
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report',
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true
                    ]
                    publishHTML target: [
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: 'Coverage Report',
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true
                    ]
                }
            }
        }
    }
}
