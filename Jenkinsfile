pipeline {
    agent any

    tools {
        maven "M3"
    }

    stages {
        stage('Build') {
            steps {
                sh "mvn -DskipTests clean install"
            }
        }
        stage('Docker Build'){
            when{
                branch 'master'
            }
            steps{
                sh 'docker build -t katai2099/ecommerce-backend .'
            }
        }
        stage('Docker Deploy'){
            when{
                branch 'master'
            }
            environment {
             DOCKERHUB_CREDENTIALS = credentials('dockerHub')
            }
            steps{
                withCredentials([usernamePassword(credentialsId: 'dockerHub', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USER')]){
                    sh 'docker login -u $DOCKERHUB_USER -p $DOCKERHUB_PASSWORD'
                    sh 'docker push katai2099/ecommerce-backend'
                }
            }
        }
        stage('Release'){
            when{
                branch 'master'
            }
            steps{
                withCredentials([sshUserPrivateKey(credentialsId: 'EC2', keyFileVariable: 'EC2keyfile', usernameVariable: 'EC2_USER'),
                                usernamePassword(credentialsId: 'MAIL_SERVER', passwordVariable: 'MAIL_SERVER_PASS', usernameVariable: 'MAIL_SERVER_USER'),
                                usernamePassword(credentialsId: 'RDS', passwordVariable: 'RDS_PASS', usernameVariable: 'RDS_USER'),
                                string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET'), string(credentialsId: 'STRIPE_API_KEY', variable: 'STRIPE_API_KEY'),
                                ]){
                                    sh './deploy.sh'
                }
            }
        }
    }
}
