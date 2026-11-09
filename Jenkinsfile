pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean verify'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t remittance-rates-api:${BUILD_NUMBER} .'
            }
        }
    }

    post {
        success {
            echo 'Build, tests, and Docker image creation completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs.'
        }
    }
}
