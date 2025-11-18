pipeline {
    agent none
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh './gradlew build'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
                sh './gradlew test'
            }
            post {
                always {
                    junit 'test-results.tests/*.xml'
                    publishHTML {
                        reportDir: 'build/reports/tests/test',
                        reportFiles: index.html,
                        reportName: 'Test Report',
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true
                }
            }
        }
    }
}
