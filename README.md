# p4pa-io-notification

This application belong to the **outbound** tier of the **Piattaforma Unitaria** product.

See [PU Microservice Architecture](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1405845916/Architettura+microservizi) for more details.

## 🧱 Role

* To handle IO notification send through app IO.

## 🌐 APIs
See [OpenAPI](openapi/p4pa-io-notification.openapi.yaml), exposed through the following path:
* `/swagger-ui/index.html`

See [Postman collection](/postman/p4pa-io-notification-E2E.postman_collection.json) and [Postman Environment](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1094615081/Environment+collection+postman).

### 📌 Relevant APIs
* `POST /ionotification/service/{organizationId}/{debtPositionTypeOrgId}`: To create a service on IO;
* `GET /ionotification/service/{organizationId}/{debtPositionTypeOrgId}`: To get a service previous created on IO;
* `DELETE /ionotification/service/{serviceId}`: To delete a service on IO;
* `POST /ionotification/message`: To send a message using IO.

### 📌 Common HTTP status returned:
* `401`: Invalid access token provided, thus a new login is required;
* `403`: Trying to access a not authorized resource.

## 🔎 Monitoring
See available actuator endpoints through the following path:
* `/actuator`

### 📌 Relevant endpoints
* Health (provide an accessToken to see details): `/actuator/health`
    * Liveness: `/actuator/health/liveness`
    * Readiness: `/actuator/health/readiness`
* Metrics: `/actuator/metrics`
    * Prometheus: `/actuator/prometheus`

Further endpoints are exposed through the JMX console.

## ✏️ Logging
See [log configured pattern](/src/main/resources/logback-spring.xml).

## 🔗 Dependencies

### 🗄️ Resources
* MongoDB

### 🧩 Microservices
* [p4pa-organization](https://github.com/pagopa/p4pa-organization):
    * To retrieve organization IO Api Key;

### 🌍 External
* PagoPA IO backend - The PagoPA IO application used to send notification to citizen:
  * [OpenAPI](https://github.com/pagopa/io-functions-services/blob/master/openapi/index.yaml): To handle IO services and send notifications.

## 🗃️ Entities handled
* `io_notification`
* `io_service`

## 🔧 Configuration

See [application.yml](src/main/resources/application.yml) for each configurable property.

### 📌 Relevant configurations

#### 🌐 Application Server
| ENV         | DESCRIPTION                       | DEFAULT |
|-------------|-----------------------------------|---------|
| SERVER_PORT | Application server listening port | 8080    |

#### ✏️ Logging
| ENV                                   | DESCRIPTION                                                                                                                                                                     | DEFAULT |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| LOG_LEVEL_ROOT                        | Base level                                                                                                                                                                      | INFO    |
| LOG_LEVEL_PAGOPA                      | Base level of custom classes                                                                                                                                                    | INFO    |
| LOG_LEVEL_SPRING                      | Level applied to Spring framework                                                                                                                                               | INFO    |
| LOG_LEVEL_SPRING_BOOT_AVAILABILITY    | To print availability events                                                                                                                                                    | DEBUG   |
| LOGGING_LEVEL_API_REQUEST_EXCEPTION   | Level applied to APIs exception                                                                                                                                                 | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG             | Level applied to [PerformanceLog](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.-Log-di-performance)                                               | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_API_REQUEST | Level applied to [API Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.1.-Log-di-perfomance-per-le-API)                            | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_REST_INVOKE | Level applied to [REST invoke Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.2.-Log-di-performance-per-i-servizi-REST-integrati) | INFO    |

#### 🔁 Integrations

##### 🗄️ Resources
| ENV                                           | DESCRIPTION                             | DEFAULT                   |
|-----------------------------------------------|-----------------------------------------|---------------------------|
| MONGODB_URI                                   | Mongo connection string                 | mongodb://localhost:27017 |
| MONGODB_DBNAME                                | Mongo db name                           | payhub                    |
| MONGODB_CONNECTIONPOOL_MAX_SIZE               | Mongo connection pool max size          | 100                       |
| MONGODB_CONNECTIONPOOL_MIN_SIZE               | Mongo connection pool max size          | 0                         |
| MONGODB_CONNECTIONPOOL_MAX_WAIT_MS            | Timeout milliseconds                    | 120000                    |
| MONGODB_CONNECTIONPOOL_MAX_CONNECTION_LIFE_MS | Connection lifetime (milliseconds)      | 0                         |
| MONGODB_CONNECTIONPOOL_MAX_CONNECTION_IDLE_MS | Connection idle lifetime (milliseconds) | 120000                    |
| MONGODB_CONNECTIONPOOL_MAX_CONNECTING         | Max parallel creating connections       | 2                         |

##### 🔗 REST
| ENV                                               | DESCRIPTION                               | DEFAULT |
|---------------------------------------------------|-------------------------------------------|---------|
| DEFAULT_REST_CONNECTION_POOL_SIZE                 | Default connection pool size              | 10      |
| DEFAULT_REST_CONNECTION_POOL_SIZE_PER_ROUTE       | Default connection pool size per route    | 5       |
| DEFAULT_REST_CONNECTION_POOL_TIME_TO_LIVE_MINUTES | Default connection pool TTL (minutes)     | 10      |
| DEFAULT_REST_TIMEOUT_CONNECT_MILLIS               | Default connection timeout (milliseconds) | 120000  |
| DEFAULT_REST_TIMEOUT_READ_MILLIS                  | Default read timeout (milliseconds)       | 120000  |

##### 🧩 Microservices
| ENV                                  | DESCRIPTION                                                                       | DEFAULT |
|--------------------------------------|-----------------------------------------------------------------------------------|---------|
| ORGANIZATION_BASE_URL                | Organization microservice URL                                                     |         |
| ORGANIZATION_MAX_ATTEMPTS            | Organization API max attempts                                                     | 3       |
| ORGANIZATION_WAIT_TIME_MILLIS        | Organization retry waiting time (milliseconds)                                    | 500     |
| ORGANIZATION_PRINT_BODY_WHEN_ERROR   | To print body when an error occurs                                                | true    |

##### 🌍 External services
| ENV                                     | DESCRIPTION                                                           | DEFAULT |
|-----------------------------------------|-----------------------------------------------------------------------|---------|
| IO_MANAGE_BACKEND_SERVICE_BASE_URL      | URL towards IO backend                                                |         |
| IO_BACKEND_SERVICE_SUBSCRIPTION_API_KEY | ApiKey used when handling IO services (creation, retrieve and delete) |         |
| IO_BACKEND_SERVICE_LIMIT                | Max IO services fetched                                               | 99      |
| IO_BACKEND_NOTIFICATION_TTL_SECONDS     | TTL applied to notification send through IO (seconds)                 | 3600    |


#### 🔑 keys
| ENV                          | DESCRIPTION                                         | DEFAULT |
|------------------------------|-----------------------------------------------------|---------|
| JWT_TOKEN_PUBLIC_KEY         | p4pa-auth JWT public key                            |         |

## 🛠️ Getting Started

### 📝 Prerequisites

Ensure the following tools are installed on your machine:

1. **Java 21+**
2. **Gradle** (or use the Gradle wrapper included in the repository)
3. **Docker** (to build and run on an isolated environment, optional)

### 🔐 Write Locks

```sh
./gradlew dependencies --write-locks
```

### ⚙️ Build

```sh
./gradlew clean build
```

### 🧪 Test

#### 📌 JUnit
```sh
./gradlew test
```

### 🚀 Run local

```sh
./gradlew bootRun
```

### 🐳 Build & run through Docker
```sh
docker build -t <APP_NAME> .
docker run --env-file <ENV_FILE> <APP_NAME>
```

### ⚖️ Generate dependencies licenses
```sh
./gradlew generateLicenseReport
```