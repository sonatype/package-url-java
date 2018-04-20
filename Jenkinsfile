String mavenVersion = 'Maven 3.0.x'
String jdkVersion = 'Java 8'

pipeline {
    agent {
        label 'ubuntu-zion'
    }

    tools {
        maven mavenVersion
        jdk jdkVersion
    }

    stages {
        stage('Build') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                withMaven(maven: mavenVersion, jdk: jdkVersion, mavenSettingsConfig: 'public-settings.xml', mavenLocalRepo: '.repository') {
                    sh "mvn -V -B -e clean install"
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                withMaven(maven: mavenVersion, jdk: jdkVersion, mavenSettingsConfig: 'public-settings.xml', mavenLocalRepo: '.repository') {
                    sh "mvn -V -B -e clean deploy"
                }
            }
        }
    }
}