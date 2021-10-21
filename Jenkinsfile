def TAG_SELECTOR = "UNINTIALIZED"
pipeline {
	agent { label 'java-small' }
	environment {
		DOCKERHUB_CREDENTIALS=credentials('DOCKER_HUB_CREDS')
	}

	stages {
		stage('Build') {
		    git url: 'https://github.com/Recks11/UkGovAPI.git', branch: 'main'
			withMaven(maven: 'Maven 3') {
			    sh 'mvn clean compile'
			}
		}

		stage('test') {
            withMaven {
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }

		stage('PUSH_IMAGE') {
			steps {
			    script {
                    TAG_SELECTOR = readMavenPom().getVersion()
                }
				sh "echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin"
				sh "docker push rexijie/ukgovapi:$TAG_SELECTOR"
			}
		}
	}

	post {
		always {
			sh 'docker logout'
		}
	}

}