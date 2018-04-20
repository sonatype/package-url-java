pipeline {
    agent any

    tools {
        maven 'Maven 3.0.x'
        jdk 'Java 8'
    }

    stages {
        stage('Build') {
            steps {
                mvn clean install
            }
        }
    }
}