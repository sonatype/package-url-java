String jdkVersion = 'Java 8'

String mavenVersion = 'Maven 3.3.x'
String mavenSettings = 'public-settings.xml'
String mavenRepo = '.repo'
String mavenOptions = '-V -B -e'

String deployBranch = 'master'

pipeline {
  agent {
    label 'ubuntu-zion'
  }

  triggers {
    pollSCM('*/15 * * * *')
  }

  tools {
    maven mavenVersion
    jdk jdkVersion
  }

  stages {
    stage('Build') {
      when {
        not {
          branch deployBranch
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
        branch deployBranch
      }
      steps {
        withMaven(maven: mavenVersion, jdk: jdkVersion, mavenSettingsConfig: mavenSettings, mavenLocalRepo: mavenRepo) {
          sh "mvn $mavenOptions clean deploy"
        }
      }
    }

    stage('Evaluate Policy') {
      steps {
        nexusPolicyEvaluation iqApplication: 'oss-index-website', iqStage: 'build',
            // HACK: bogus path here to only scan indexed modules
            iqScanPatterns: [[scanPattern: 'no-such-path/*']]
      }
    }
  }

  post {
    always {
      junit '**/target/*-reports/*.xml'
    }
  }
}