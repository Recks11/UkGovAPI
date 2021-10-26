def IMG_TAG = "UNINTIALIZED"
def IMG_NAME = ""
pipeline {
	agent { label 'java-small' }
	tools {
	    jdk 'JDK 17'
	    maven 'Maven 3'
	}
	environment {
		DOCKERHUB_CREDENTIALS=credentials('docker-hub-creds')
	}

	stages {
		stage('Build') {
		    steps {
                sh 'mvn clean compile'
		    }
		}

		stage('test') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }

        stage('build image') {
            steps {
                sh 'mvn spring-boot:build-image -DskipTests'
            }
        }

		stage('PUSH_IMAGE') {
			steps {
			    script {
                    IMG_TAG = readMavenPom().getVersion()
                    IMG_NAME = readMavenPom.getArtifactId()
                }
				sh "echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin"
				sh "echo project version $TAG_SELECTOR"
				sh "docker push rexijie/$IMG_NAME:$IMG_TAG"
			}

			post {
                always {
                    sh 'docker logout'
                }
            }
		}
	}
}