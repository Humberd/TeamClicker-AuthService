node {
    def project = "team-clicker"
    def appName = "auth-service"
    def imageTag = "eu.gcr.io/${project}/${appName}:${getBuildNumber()}"
    /* ---TEST--- */
    def testDatabase = "tc-auth-service-tests-db:5432/auth-service"
    def testDatabaseUsername = "postgres"
    def testDatabasePassword = "admin123"
    /* ---PROD--- */
    def prodDatabase = "35.205.205.92:5432/auth-service"
    def prodDatabaseUsername = null //from secrets
    def prodDatabasePassword = null //from secrets

    sh "echo foobar"
    sh "echo ${prodDatabasePassword}"

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
                    "COMMIT=${getCommit()}",
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
            withCredentials([usernamePassword(credentialsId: 'team-clicker-auth-service-prod-db-user', usernameVariable: 'prodDatabaseUsername', passwordVariable: 'prodDatabasePassword')]) {
                sh script: """
                docker build \
                    -f ${dockerfile} \
                    -t ${imageTag} \
                    --build-arg COMMIT=${getCommit()} \
                    --build-arg BUILD_NO=%{${getBuildNumber()} \
                    --build-arg TC_AUTH_DATABASE_URL=jdbc:postgresql://${prodDatabase}" \
                    --build-arg TC_AUTH_DATABASE_USERNAME=${prodDatabaseUsername} \
                    --build-arg TC_AUTH_DATABASE_PASSWORD=${prodDatabasePassword} .
                    """, returnStdout: true
            }
        } finally {
            sh "docker rmi ${imageTag}"
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
def getSecretText(String secretId) {
    withCredentials([string(credentialsId: secretId, variable: secretId)]) {
        def value = sh script: "echo ${secretId}", returnStdout: true
        return value
    }
}