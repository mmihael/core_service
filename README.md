# Core service

Starting point for spring backend service.

## Example config

```yaml
server:
  servlet:
    session:
      timeout: 60m
spring:
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
```