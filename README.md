# Core service

Starting point for spring backend service.

Features:

* Users 
* Roles
* Organizations
* File upload/share

## Example config

```yaml
server:
  servlet:
    session:
      timeout: 60m
spring:
  servlet:
    multipart:
      max-file-size: 128MB
      max-request-size: 128MB 
  jpa:
    hibernate:
      ddl-auto: validate
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/core_service?useUnicode=yes&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false&createDatabaseIfNotExist=true
    username: user
    password: pass
  session:
    store-type: jdbc
    jdbc:
      initialize-schema: always
dev:
  mm:
    core:
      coreservice:
        file-storage-location: /tmp/storage
```