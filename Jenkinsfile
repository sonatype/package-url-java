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
            withMaven(
//                    jdk: 'Java 8',
//                    maven: 'Maven 3.0.x',
                    mavenSettingsConfig: 'public-settings.xml',
                    mavenLocalRepo: '.repository'
            ) {
                sh "mvn -V -B -e clean deploy"
            }
        }

        stage('Build') {
            when {
                not {
                    branch 'master'
                }
            }
            withMaven(
//                    jdk: 'Java 8',
//                    maven: 'Maven 3.0.x',
                    mavenSettingsConfig: 'public-settings.xml',
                    mavenLocalRepo: '.repository'
            ) {
                sh "mvn -V -B -e clean install"
            }
        }
    }
}