pipeline {
    agent any

    tools {
        maven 'Maven 3.0.x' // FIXME: should be 3.5.x
        jdk 'Java 8'
    }

    stages {
        stage('Build') {
            steps {
                sh "mvn -V -B -e clean install"
            }
        }
    }
}