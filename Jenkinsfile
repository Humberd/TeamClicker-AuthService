node {
    def project = "team-clicker"
    def appName = "auth-service"
    def imageTag = "eu.gcr.io/${project}/${appName}:${getBuildNumber()}"
    /* ---TEST--- */
    def testDatabase = "tc-auth-service-tests-db:5432/auth-service-tests"
    def testDatabaseUsername = "postgres"
    def testDatabasePassword = "admin123"
    /* PROD */
    def deploymentName = "auth-service"
    def containerName = "app"

    /**
     * Making sure, that there are only at most 2 artifacts stored on a server,
     * because We don't want to waste a storage on a server, do we?
     */
    //noinspection GroovyAssignabilityCheck
    properties([buildDiscarder(logRotator(
            artifactDaysToKeepStr: '',
            artifactNumToKeepStr: '1',
            daysToKeepStr: '',
            numToKeepStr: '')),
                disableConcurrentBuilds(),
                pipelineTriggers([githubPush()])])

    stage("Pre Cleanup") {
        deleteDir()
    }

    stage("Checkout") {
        git "https://github.com/Humberd/TeamClicker-AuthService.git"
    }

    stage("Build") {
        //noinspection GroovyAssignabilityCheck
        withCredentials([file(credentialsId: 'TC_JWT_PRIVATE_KEY', variable: 'TC_JWT_PRIVATE_KEY'),
                         file(credentialsId: 'TC_JWT_PUBLIC_KEY', variable: 'TC_JWT_PUBLIC_KEY')]) {
            sh "cp \$TC_JWT_PRIVATE_KEY src/main/resources/jwt_private_key.der"
            sh "cp \$TC_JWT_PUBLIC_KEY src/main/resources/jwt_public_key.der"

            withMaven(maven: "Maven") {
                sh "mvn clean install -DskipTests=true"
            }
        }
    }

    stage("Test") {
        //noinspection GroovyAssignabilityCheck
        dockerComposeFile = "production.testing.docker-compose.yml"

        sh "docker-compose -f ${dockerComposeFile} down --rmi all --remove-orphans"
        sh "docker-compose -f ${dockerComposeFile} up -d"

        try {
            withEnv([
                    "COMMIT_HASH=${getCommitHash()}",
                    "BUILD_NO=${getBuildNumber()}",
                    "TC_AUTH_TESTS_DATABASE_URL=jdbc:postgresql://${testDatabase}",
                    "TC_AUTH_TESTS_DATABASE_USERNAME=${testDatabaseUsername}",
                    "TC_AUTH_TESTS_DATABASE_PASSWORD=${testDatabasePassword}"
            ]) {
                withMaven(maven: "Maven") {
                    sh "mvn test -DargLine='-Dspring.profiles.active=production'"
                }
            }
        } finally {
            sh "docker-compose -f ${dockerComposeFile} down --rmi all --remove-orphans"
        }
    }

    stage("Deploy") {
        dockerfile = "production.deploy.Dockerfile"

        try {
            sh script: """
                docker build \
                    -f ${dockerfile} \
                    -t ${imageTag} \
                    --build-arg COMMIT_HASH=${getCommitHash()} \
                    --build-arg BUILD_NUMBER=${getBuildNumber()} . \
                    """, returnStdout: true

            withCredentials([file(credentialsId: 'TeamClickerAuthServiceDeployer', variable: 'TeamClickerAuthServiceDeployer')]) {
                sh "gcloud auth activate-service-account --key-file=\$TeamClickerAuthServiceDeployer"
                sh "gcloud docker -- push ${imageTag}"

//                sh "replica_spec=\$(kubectl get ${deploymentName}/${containerName} -o jsonpath='{.spec.replicas}')"
//                sh "kubectl scale --replicas=0 ${deploymentName} ${containerName}"
                sh "kubectl set image ${deploymentName}/${containerName} app-container=${imageTag}"
//                sh "kubectl scale --replicas=\$replica_spec ${deploymentName} ${containerName}"
            }
        } finally {
//            sh "docker rmi ${imageTag}"
        }
    }

    stage("Post Cleanup") {
        deleteDir()
    }
}

def getCommitHash() {
    return sh(
            script: "git show -s --format=%H",
            returnStdout: true
    ).trim()
}

def getBuildNumber() {
    return env.BUILD_NUMBER
}

def getFileContent(fileName) {
    return sh(
            script: '\$' + fileName,
            returnStdout: true
    )
}