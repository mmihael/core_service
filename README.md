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
  forward-headers-strategy: native 
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

Property `server.forward-headers-strategy=native` is required in order for nginx ws proxy to work. 

## Nginx config for API and WS

```
        location /api {
                proxy_pass http://127.0.0.1:8081;
        }

        location /public/api {
                proxy_pass http://127.0.0.1:8081;
        }

        location /websocket {
                proxy_pass http://127.0.0.1:8081;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header Host $host;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header X-Forwarded-Proto $scheme;
                proxy_set_header X-Forwarded-Port $server_port;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection upgrade; // might need to switch from always defaulting to 'upgrade'

                proxy_connect_timeout 7d;
                proxy_send_timeout 7d;
                proxy_read_timeout 7d;
        }
```