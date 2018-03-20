node {
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
        withMaven(maven: "Maven") {
            sh "mvn clean install -DskipTests=true"
        }
    }

    stage("Test") {
        //noinspection GroovyAssignabilityCheck
        dockerComposeFile = "production.testing.docker-compose.yml"

        sh "docker-compose -f ${dockerComposeFile} down --rmi all --remove-orphans"
        sh "docker-compose -f ${dockerComposeFile} up -d"

        containerName = "tc-auth-service-tests-db"
        dbURL = getContainerInternalIp(containerName)
        try {
            withEnv([
                    "COMMIT=${getCommit()}",
                    "BUILD_NO=${getBuildNumber()}",
                    "TC_AUTH_TESTS_DATABASE_URL=jdbc:postgresql://localhost:5400/postgres",
                    "TC_AUTH_TESTS_DATABASE_USERNAME=postgres",
                    "TC_AUTH_TESTS_DATABASE_PASSWORD=admin123"
            ]) {
                withMaven(maven: "Maven") {
                    sh "mvn test -DargLine='-Dspring.profiles.active=production'"
                }
            }
        } finally {
            sh "docker-compose -f ${dockerComposeFile} down --rmi all --remove-orphans"
        }
    }

//    stage("Deploy") {
//        dockerComposeFile = "production.deployment.docker-compose.yml"
//
//        /**
//         * Setting environment variables only for a docker container
//         */
//        withEnv([
//                "COMMIT=${getCommit()}",
//                "BUILD_NO=${getBuildNumber()}"
//        ]) {
//            sh "docker-compose -f ${dockerComposeFile} down --rmi all --remove-orphans"
//            sh "docker-compose -f ${dockerComposeFile} up -d"
//        }
//    }

    stage("Post Cleanup") {
        deleteDir()
    }
}

def getCommit() {
    return sh(
            script: "git show -s",
            returnStdout: true
    ).trim()
}

def getBuildNumber() {
    return env.BUILD_NUMBER
}

def getContainerInternalIp(containerName) {
    return sh(
            script: "docker exec ${containerName} bash -c \"ifconfig eth0 | grep 'inet addr' | cut -d ':' -f 2 | cut -d ' ' -f 1\"",
            returnStdout: true
    ).trim()
}