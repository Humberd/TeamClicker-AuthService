# Team Clicker Auth Service

`docker run --name tc-postgres -e POSTGRES_PASSWORD=admin123 -p 5432:5432 -d postgres`

## Deployment requirements

### Files

 1. _src/main/resources/jwt_public_key.der_ - **public** jwt validation key
 2. _src/main/resources/jwt_private_key.der_ - **private** jwt signing key

How to generate keys -> https://stackoverflow.com/a/19387517/4256929

### Environment variables

 1. _TC_AUTH_DATABASE_URL_ - url to the PostgreSQL database, for example:
 `127.0.0.1:4321/auth-service`
 2. _TC_AUTH_DATABASE_USERNAME_ - PostgreSQL database username, for example:
 `admin`
 3. _TC_AUTH_DATABASE_PASSWORD_ - PostgreSQL database user password, for example:
 `admin123`
 4. _TC_KAFKA_URL_ - Kafka url, for example: `192.168.99.100:32769`

## Deployment Testing requirements

### Files

 1. _src/**test**/resources/jwt_public_key.der_ - **public** jwt validation key
 2. _src/**test**/resources/jwt_private_key.der_ - **private** jwt signing key

How to generate keys -> https://stackoverflow.com/a/19387517/4256929

### Environment variables

 1. _TC_AUTH__**_TESTS_**__DATABASE_URL_ - url to the PostgreSQL database, for example:
 `127.0.0.1:4321/auth-service-tests`
 2. _TC_AUTH__**_TESTS_**__DATABASE_USERNAME_ - PostgreSQL database username, for example:
 `admin`
 3. _TC_AUTH__**_TESTS_**__DATABASE_PASSWORD_ - PostgreSQL database user password, for example:
 `admin123`
 4. _TC_KAFKA_URL_ - Kafka url, for example: `192.168.99.100:32769`
