# AGENTS.md

## Build

Requires JDK 24 and Maven 3.9+ on PATH. Set `JAVA_HOME` to a JDK 24 install
(a known one on this machine: `C:\Users\Aldwin\.jdks\openjdk-24.0.1`).

- Compile and test: `mvn clean verify`
- Run: `mvn spring-boot:run`
- Run tests only: `mvn test`
- Package a jar: `mvn clean package`

After startup:

- API: http://localhost:8080/hello
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:appdb`, user `sa`, blank password)

## Layout

Layered. Dependencies point one way: controller -> service -> repository.

`entity/`, `service/`, `repository/`, `dto/` and `exception/` are **planned, not yet
created**. Today the app has only a boot check plus OpenAPI config.

```
com.example.demo
  DemoApplication      @SpringBootApplication entry point
  config/              cross-cutting beans (OpenAPI, CORS, Jackson)
  controller/          @RestController, HTTP mapping only, no business logic
  service/             @Service, @Transactional on writes, business rules
  repository/          JpaRepository interfaces, no custom JPQL unless needed
  entity/              @Entity classes, never leave the service layer
  dto/                 records for request and response bodies
  exception/           custom exceptions + @RestControllerAdvice handler
```

## Conventions

- No Lombok. Records for DTOs, plain classes for entities and services.
- Entities never cross the controller boundary. Controllers accept and return DTOs.
- Validating DTO records with `@Valid`. Constraints live on the record components.
- `BigDecimal` for money. Never `double`.
- Generated IDs use `GenerationType.IDENTITY`.
- One public method per service operation, named after the use case, not the verb
  on the HTTP method.
- Throw a custom exception from the service layer for missing rows; map it in the
  `@RestControllerAdvice` so the API returns a consistent error body.
- Tests mirror packages. Use `@DataJpaTest` for repositories. For controllers use
  `@WebMvcTest`, or `@SpringBootTest` **plus `@AutoConfigureMockMvc`** — a bare
  `@SpringBootTest` does not register a `MockMvc` bean and the autowire fails.

## Data

H2 in-memory by default, schema from Hibernate (`ddl-auto: update`). There is no
`data.sql` yet; when one is added to `src/main/resources` it will load because
`spring.jpa.defer-datasource-initialization` is true. The `test` profile sets
`spring.sql.init.mode: never` so main seed data never leaks into tests, and uses
its own in-memory database with `ddl-auto: create-drop`.
