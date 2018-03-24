node {
    def project = "team-clicker"
    def appName = "auth-service"
    def imageTag = "eu.gcr.io/${project}/${appName}:${getBranchName()}.${getBuildNumber()}"

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

        def dbURL = "tc-auth-service-tests-db"
        try {
            withEnv([
                    "COMMIT=${getCommit()}",
                    "BUILD_NO=${getBuildNumber()}",
                    "TC_AUTH_TESTS_DATABASE_URL=jdbc:postgresql://${dbURL}:5432/postgres",
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

    stage("Deploy") {
        dockerfile = "production.deploy.Dockerfile"

        def dbURL = "foobar"
        withEnv([
                "COMMIT=${getCommit()}",
                "BUILD_NO=${getBuildNumber()}",
                "TC_AUTH_DATABASE_URL=jdbc:postgresql://${dbURL}:5432/postgres",
                "TC_AUTH_DATABASE_USERNAME=postgres",
                "TC_AUTH_DATABASE_PASSWORD=admin123"
        ]) {
            sh script: """
                docker build \
                    -f ${dockerfile} \
                    -t ${imageTag} \
                    --build-arg TC_AUTH_DATABASE_URL=\$TC_AUTH_DATABASE_URL \
                    --build-arg TC_AUTH_DATABASE_USERNAME=\$TC_AUTH_DATABASE_USERNAME \
                    --build-arg TC_AUTH_DATABASE_PASSWORD=\$TC_AUTH_DATABASE_PASSWORD .
                    """, returnStdout: true
        }
    }

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

def getBranchName() {
    return env.BRANCH_NAME
}