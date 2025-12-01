pipeline {
    agent any
    tools {
        jdk 'temurin-21'
        gradle 'gradle-8.10'
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
                    junit 'build/test-results/tests/*.xml'
                    jacoco execPattern: 'build/jacoco/test.exec',
                           classPattern: 'build/classes/java/main',
                           sourcePattern: 'src/main/java',
                           htmlReportDir: 'build/reports/jacoco/test/html'
                    publishHTML target: [
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report',
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true
                    ]
                }
            }
        }
    }
    post {
        cleanup {
            cleanWs()
        }
    }
}
