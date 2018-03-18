# Team Clicker Auth Service

## Required deployment files:

 1. jwtRS512.key - private key
 2. jwtRS512.key.pub - public key

```bash
ssh-keygen -t rsa -b 4096 -f jwtRS512.key
# Don't add passphrase
openssl rsa -in jwtRS512.key -pubout -outform PEM -out jwtRS512.key.pub
```

## Required deployment environment variables:

 1. TC_AUTH_DATABSE_URL - url to the database
 2. TC_AUTH_DATABSE_USERNAME - database username
 3. TC_AUTH_DATABSE_PASSWORD - database password