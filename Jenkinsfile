
pipeline {
    agent any

    environment {
        APP_NAME = 'remittance-rates-api'
        CONTAINER_NAME = 'remittance-api'
        APP_PORT = '8081'
    }

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
                sh 'docker build -t ${APP_NAME}:${BUILD_NUMBER} .'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    set -eu

                    NEW_IMAGE="${APP_NAME}:${BUILD_NUMBER}"

                    # Save the currently deployed image
                    if docker inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
                        docker inspect \
                          --format='{{.Config.Image}}' \
                          "${CONTAINER_NAME}" > .previous-image
                    else
                        : > .previous-image
                    fi

                    # Mark that deployment has started
                    touch .deployment-started

                    # Replace the running container
                    docker rm -f "${CONTAINER_NAME}" || true

                    docker run -d \
                      --name "${CONTAINER_NAME}" \
                      --restart unless-stopped \
                      -p "${APP_PORT}:8081" \
                      "${NEW_IMAGE}"
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    for i in $(seq 1 30); do
                        if curl -fsS \
                          http://localhost:${APP_PORT}/actuator/health; then
                            echo "Deployment health check passed!"
                            exit 0
                        fi

                        sleep 2
                    done

                    echo "Deployment health check failed!"
                    docker logs ${CONTAINER_NAME} || true
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            sh 'rm -f .deployment-started .previous-image'
            echo 'CI/CD deployment successful!'
        }

        failure {
            sh '''
                if [ -f .deployment-started ]; then
                    echo "Deployment failed. Starting rollback..."

                    OLD_IMAGE=$(cat .previous-image 2>/dev/null || true)

                    docker rm -f "${CONTAINER_NAME}" || true

                    if [ -n "$OLD_IMAGE" ]; then
                        echo "Restoring previous image: $OLD_IMAGE"

                        docker run -d \
                          --name "${CONTAINER_NAME}" \
                          --restart unless-stopped \
                          -p "${APP_PORT}:8081" \
                          "$OLD_IMAGE"

                        echo "Rollback container started."
                    else
                        echo "No previous deployment available to restore."
                    fi

                    rm -f .deployment-started .previous-image
                else
                    echo "Failure occurred before deployment. No rollback needed."
                fi
            '''
            echo 'Pipeline failed. Check the logs.'
        }
    }
}
