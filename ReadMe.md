# basicbooksapi
 
A REST API for managing books and authors, built with Spring Boot and PostgreSQL, as part of a hands-on backend Java learning project.
 
> **Note on AI use**: All the project code (entities, controllers, tests, Docker configuration, etc.) was written by hand. Only this README file was AI-generated.
 
To get more details, feel free to contact me at pro@fergalmechin.fr or on LinkedIn - Fergal MECHIN.

## Project goal
 
This project serves as a sandbox to consolidate the fundamentals of a modern Java backend: from raw SQL to a containerized, tested, and documented API.
 
## Tech stack
 
- **Language**: Java 21
- **Framework**: Spring Boot 4.1 (Web, Data JPA)
- **Database**: PostgreSQL 16
- **Migrations**: Flyway
- **Testing**: JUnit 5, MockMvc, Mockito
- **Observability**: Micrometer Tracing (Brave)
- **Build**: Maven
- **Containerization**: Docker (multi-stage build) + Docker Compose
- **Utilities**: Lombok, SLF4J
## Architecture
 
Organized by technical layer (`controller`, `entity`, `repository`, `dto`, `exception`), which fits the current size of the project.
 
```
src/main/java/fr/fergalmechin/basicbooksapi
├── controller/       # REST endpoints (AuthorController, BookController)
├── entity/           # JPA entities (Author, Book)
├── repository/       # Spring Data JPA interfaces
├── dto/
│   ├── request/       # Request DTOs (e.g. BookRequest)
│   └── response/       # Response DTOs (e.g. BookResponse)
└── exception/         # Custom exceptions + centralized handling (@ControllerAdvice)
```
 
## Data model
 
Two entities linked by a `@ManyToOne` / implicit `@OneToMany` relationship:
 
- **Author**: `id`, `name` (unique), `country`
- **Book**: `id`, `title`, `year`, `author` (reference to `Author`)
The schema is fully version-controlled through Flyway migrations (`src/main/resources/db/migration`).
 
## Endpoints
 
### Authors
 
| Method | Endpoint             | Description                          |
|--------|-----------------------|---------------------------------------|
| GET    | `/api/authors`         | List all authors                      |
| GET    | `/api/authors/{id}`    | Get an author by id                   |
| POST   | `/api/authors`         | Create an author                      |
| PUT    | `/api/authors/{id}`    | Update an author                      |
| DELETE | `/api/authors/{id}`    | Delete an author                      |
 
### Books
 
| Method | Endpoint           | Description                                        |
|--------|---------------------|------------------------------------------------------|
| GET    | `/api/books`         | List all books (author flattened in response)        |
| GET    | `/api/books/{id}`    | Get a book by id                                     |
| POST   | `/api/books`         | Create a book (references the author via `authorId`) |
| PUT    | `/api/books/{id}`    | Update a book                                        |
| DELETE | `/api/books/{id}`    | Delete a book                                        |
 
### Error handling
 
Centralized through a global `@ControllerAdvice`:
 
| Code | Case                                              |
|------|-----------------------------------------------------|
| 404  | Resource not found (author, book)                    |
| 409  | Conflict (e.g. author name already exists)            |
| 400  | Invalid request                                       |
 
## Running the project
 
### Prerequisites
 
- Docker and Docker Compose
- Java 21 (for running outside a container)
### With Docker Compose (recommended)
 
1. Copy `.env.example` to `.env` and adjust values if needed:
```bash
   cp .env.example .env
```
2. Start everything (API + database):
```bash
   docker compose up --build
```
3. The API is available at `http://localhost:8080`.
Flyway migrations run automatically on startup.
 
### Locally, without Docker
 
1. Start a PostgreSQL instance accessible locally.
2. Adjust `src/main/resources/application.properties` (or the corresponding environment variables) with your connection details.
3. Run the application:
```bash
   ./mvnw spring-boot:run
```
 
## Tests
 
Web-layer integration tests (`@WebMvcTest` + Mockito) on both controllers, covering both happy paths and error cases (resource not found, duplicate, invalid reference).
 
```bash
./mvnw test
```
 
## Notable technical points
 
- **Multi-stage Docker build**: lightweight final image (JRE only), without the Maven build tooling.
- **Externalized configuration**: database credentials passed through environment variables (`.env`, not committed), no sensitive values in the repository.
- **PostgreSQL healthcheck**: the API service waits for the database to actually be ready (`pg_isready`) before starting, not just for the container to exist.
- **Dedicated DTOs**: separation between the persistence model (JPA entities) and the exposed API contract, avoiding internal structure leaks and serialization cycles.
- **Tracing**: trace/span identifiers automatically injected into logs, in preparation for a future multi-service architecture.
## Author
 
Fergal Mechin — [fergalmechin.fr](https://fergalmechin.fr) — pro@fergalmechin.fr