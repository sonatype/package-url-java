pipeline {
    agent {
        label 'ubuntu-zion'
    }

    tools {
        maven 'Maven 3.0.x'
        jdk 'Java 8'
    }

    stages {
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                withMaven(mavenSettingsConfig: 'public-settings.xml', mavenLocalRepo: '.repository') {
                    sh "mvn -V -B -e clean deploy"
                }
            }
        }

        stage('Build') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                withMaven(mavenSettingsConfig: 'public-settings.xml', mavenLocalRepo: '.repository') {
                    sh "mvn -V -B -e clean install"
                }
            }
        }
    }
}