String jdkVersion = 'Java 8'

String mavenVersion = 'Maven 3.0.x'
String mavenSettings = 'public-settings.xml'
String mavenRepo = '.repository'
String mavenOptions = '-V -B -e'

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
                withMaven(maven: mavenVersion, jdk: jdkVersion, mavenSettingsConfig: mavenSettings, mavenLocalRepo: mavenRepo) {
                    sh "mvn $mavenOptions clean install"
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                withMaven(maven: mavenVersion, jdk: jdkVersion, mavenSettingsConfig: mavenSettings, mavenLocalRepo: mavenRepo) {
                    sh "mvn $mavenOptions clean deploy"
                }
            }
        }
    }

    post {
        always {
            junit 'target/*-reports/*.xml'
        }
    }
}