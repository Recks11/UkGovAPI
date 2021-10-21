def TAG_SELECTOR = "UNINTIALIZED"
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
                sh 'mvn package -DskipTests'
            }
        }

		stage('PUSH_IMAGE') {
			steps {
			    script {
                    TAG_SELECTOR = readMavenPom().getVersion()
                }
				sh "echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin"
				sh "echo project version $TAG_SELECTOR"
				sh "docker push rexijie/ukgovapi:$TAG_SELECTOR"
			}

			post {
                always {
                    sh 'docker logout'
                }
            }
		}
	}

}