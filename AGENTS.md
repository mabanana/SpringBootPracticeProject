# AGENTS.md

## Build

Requires JDK 24 and Maven 3.9+ on PATH. Set `JAVA_HOME` to a JDK 24 install
(a known one on this machine: `C:\Users\Aldwin\.jdks\openjdk-24.0.1`).

This repo lives inside a OneDrive-synced folder, which makes `mvn clean` fail
intermittently with "Failed to delete" because OneDrive holds open handles on
build output. A `MAVEN_BUILD_DIR` environment variable pointing outside OneDrive
(already set to `%LOCALAPPDATA%\MavenBuild\demo`) activates the
`build-outside-onedrive` profile in `pom.xml`, which redirects the build
directory. Without it, builds go to `target/` and may fail to clean.

- Compile and test: `mvn clean verify`
- Run: `mvn spring-boot:run`
- Run tests only: `mvn test`
- Package a jar: `mvn clean package`

After startup:

- API: http://localhost:8080/api/v1/products
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:appdb`, user `sa`, blank password)

There is no `/` mapping, so the root path returns a 404 Whitelabel page. That is
expected, not a fault.

## Layout

Layered. Dependencies point one way: controller -> service -> repository.

```
com.example.productmanagement
  ProductManagementApplication  @SpringBootApplication entry point; must stay at
                                 the package root or component scanning breaks
  config/              cross-cutting beans (OpenAPI, CORS, Jackson)
  controller/          @RestController, HTTP mapping only, no business logic
  service/             @Service, @Transactional on writes, business rules
  repository/          JpaRepository interfaces, no custom JPQL unless needed
  entity/              @Entity classes, never leave the service layer
  dto/                 records for request and response bodies
  exception/           custom exceptions + @RestControllerAdvice handler
```

### Status: scaffolding only

Every domain class is a documented placeholder with no behaviour. They exist to
fix the shape of the design, not to implement it. Before the app will boot, two
placeholders must be made real together, because each breaks the other:

- `entity/Product.java` needs `@Entity`, `@Table`, `@Id`,
  `@GeneratedValue(strategy = IDENTITY)` on a non-final `id`, and a no-argument
  constructor.
- `repository/ProductRepository.java` needs
  `extends JpaRepository<Product, Long>`. Spring Data resolves the domain type at
  startup, so an interface extending `JpaRepository<Product, Long>` while `Product`
  lacks `@Entity` aborts the context with `Not a managed type`.

Also still stubs: `service/ProductService.java` is empty, and
`controller/ProductController.java` returns placeholder strings rather than
delegating to the service. Its POST/PUT return `null`, which serialises as an
empty 200 rather than a created resource.

`data.sql` is written but disabled — `spring.sql.init.mode: never` — because it
inserts into a table Hibernate does not yet create. Flip it to `embedded` in the
same change that implements `Product`.

## Conventions

- No Lombok. Records for DTOs, plain classes for entities and services.
- Entities never cross the controller boundary. Controllers accept and return DTOs.
- Endpoints are versioned under `/api/v1/`, declared in a `private static final`
  String constant. Annotation attribute values must be compile-time constants.
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

H2 in-memory by default, schema from Hibernate (`ddl-auto: create-drop`). There is
`data.sql` in `src/main/resources`; when it is added to `src/main/resources` it will
load because `spring.jpa.defer-datasource-initialization` is true. The `test`
profile sets `spring.sql.init.mode: never` so main seed data never leaks into
tests, and uses its own in-memory database with `ddl-auto: create-drop`.
