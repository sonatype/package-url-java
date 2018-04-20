pipeline {
    agent any

    tools {
        maven 'Maven 3.5.3'
        jdk 'jdk8'
    }

    stages {
        stage('Build') {
            steps {
                mvn clean install
            }
        }
    }
}