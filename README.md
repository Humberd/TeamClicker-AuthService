# Team Clicker Auth Service

## Required deployment files:

 1. src/main/resources/public_key.der - private key
 2. src/main/resources/private_key.der - public key

How to create keys -> https://stackoverflow.com/a/19387517/4256929

## Required deployment environment variables:

 1. TC_AUTH_DATABSE_URL - url to the database
 2. TC_AUTH_DATABSE_USERNAME - database username
 3. TC_AUTH_DATABSE_PASSWORD - database password
 4. TC_AUTH_TESTS_DATABASE_URL - url to the testing database
 5. TC_AUTH_TESTS_DATABASE_USERNAME - test database username
 6. TC_AUTH_TESTS_DATABASE_OASSWORD = test database password