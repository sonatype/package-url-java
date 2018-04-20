node('ubuntu-zion') {
    stage('Build') {
        withMaven(
                jdk: 'Java 8',
                maven: 'Maven 3.0.x',
                mavenSettingsConfig: 'public-settings.xml',
                mavenLocalRepo: '.repository'
        ) {
            sh "mvn -V -B -e clean deploy"
        }
    }
}