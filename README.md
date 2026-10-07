# Kotlin + Ktor Demo App

A small, self-contained REST API built with [Ktor](https://ktor.io) and Kotlin.
It is the reference example for a series of one-minute videos — every file
focuses on **one** concept, so each video can point to exactly one place in the code.

## Quick start

Requirements: JDK 17 or newer (tested setup: JDK 25).

```bash
./gradlew run        # start the server on http://localhost:8080
./gradlew test       # run all tests
./gradlew installDist # build a runnable distribution in build/install/
```

Try it:

```bash
curl http://localhost:8080/tasks
curl -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d '{"title":"Try Ktor"}'
```

Or open [`requests.http`](requests.http) in IntelliJ IDEA and click the ▶ icons.

## Endpoints

| Method | Path          | Description                              |
|--------|---------------|------------------------------------------|
| GET    | `/`           | Hello world                              |
| GET    | `/health`     | Health check                             |
| GET    | `/tasks`      | List tasks (optional `?done=true/false`) |
| GET    | `/tasks/{id}` | Get one task                             |
| POST   | `/tasks`      | Create a task                            |
| PATCH  | `/tasks/{id}` | Update title and/or done                 |
| DELETE | `/tasks/{id}` | Delete a task                            |

## Project map (one topic per file)

| Topic                         | File                                                                                       |
|-------------------------------|--------------------------------------------------------------------------------------------|
| Starting the server           | [`Application.kt`](src/main/kotlin/com/example/Application.kt)                             |
| Routing basics                | [`plugins/Routing.kt`](src/main/kotlin/com/example/plugins/Routing.kt)                     |
| REST routes / CRUD            | [`tasks/TaskRoutes.kt`](src/main/kotlin/com/example/tasks/TaskRoutes.kt)                   |
| JSON with kotlinx.serialization | [`plugins/Serialization.kt`](src/main/kotlin/com/example/plugins/Serialization.kt)       |
| Data classes / DTOs           | [`tasks/Task.kt`](src/main/kotlin/com/example/tasks/Task.kt)                               |
| Error handling (StatusPages)  | [`plugins/StatusPages.kt`](src/main/kotlin/com/example/plugins/StatusPages.kt)             |
| Request validation            | [`plugins/Validation.kt`](src/main/kotlin/com/example/plugins/Validation.kt)               |
| Logging (CallLogging)         | [`plugins/Monitoring.kt`](src/main/kotlin/com/example/plugins/Monitoring.kt)               |
| Repository pattern            | [`tasks/TaskRepository.kt`](src/main/kotlin/com/example/tasks/TaskRepository.kt)           |
| Thread-safe in-memory storage | [`tasks/InMemoryTaskRepository.kt`](src/main/kotlin/com/example/tasks/InMemoryTaskRepository.kt) |
| Testing routes (`testApplication`) | [`TaskRoutesTest.kt`](src/test/kotlin/com/example/tasks/TaskRoutesTest.kt)            |
| Unit testing                  | [`InMemoryTaskRepositoryTest.kt`](src/test/kotlin/com/example/tasks/InMemoryTaskRepositoryTest.kt) |
| Version catalog               | [`gradle/libs.versions.toml`](gradle/libs.versions.toml)                                   |
| Docker                        | [`Dockerfile`](Dockerfile)                                                                 |

## Docker

```bash
docker build -t ktor-demo .
docker run -p 8080:8080 ktor-demo
```

## Configuration

| Variable | Default | Description      |
|----------|---------|------------------|
| `PORT`   | `8080`  | HTTP server port |
