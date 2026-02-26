# OSGi Email Service - Project Documentation

## Project Overview

**Repository:** BlackBeltTechnology/osgi-email
**License:** Apache License 2.0
**Java Version:** 21 (Zulu JDK)
**Build System:** Maven 3.9+ with Maven Bundle Plugin for OSGi packaging

1. Provides an OSGi-based email service API (`EmailService`) with builder-pattern DTOs for constructing email messages with attachments and inline content.
2. Implements email sending via Spring Mail (`JavaMailSender`) with Handlebars template processing for both plain text and HTML bodies.
3. Supports full SMTP configuration through OSGi Config Admin, including authentication, TLS/SSL, proxy, and SASL settings.
4. Includes an embedded SubEthaSMTP test server (`LogSmtpServer`) for development and integration testing.
5. Deploys as Apache Karaf features with integration tests running in a real Karaf container via Pax Exam.

## Directory Structure

```
osgi-email-parent/
├── osgi-email-api/              # Service interface and data models (bundle)
├── osgi-email-impl/             # Spring Mail + Handlebars implementation (bundle)
├── osgi-email-karaf-features/   # Karaf feature XML descriptor (feature)
├── osgi-email-itest/            # Pax Exam integration tests (jar)
├── .github/                     # CI workflows and documentation
├── .mvn/                        # Maven wrapper and extensions
├── logback-test.xml             # Test logging configuration
└── pom.xml                      # Parent POM with shared config
```

## Core Modules

### API Layer

| Module | Type | Purpose |
|--------|------|---------|
| `osgi-email-api/` | OSGi bundle | Defines `EmailService` interface with `sendMessage()` methods, `EmailMessage<M>` builder DTO (from, tos, ccs, bccs, replyTo, subject, templates, model, attachments), and `BinaryAttachment` builder DTO. No external dependencies. |

### Implementation Layer

| Module | Type | Purpose |
|--------|------|---------|
| `osgi-email-impl/` | OSGi bundle | `EmailServiceImpl` — immediate DS component implementing `EmailService`. Uses Spring's `JavaMailSender` (injected via `@Reference`), Handlebars templating with Map/JavaBean/Field value resolvers, and RFC2822 email validation. Supports `SimpleMailMessage` for plain text and `MimeMessageHelper` for HTML/attachments. |
| `osgi-email-impl/` | OSGi bundle | `JavaMailSenderActivator` — config-required DS component that reads SMTP settings from OSGi Config Admin and registers a `JavaMailSender` service. Configured via `JavaMailSenderConfiguration` metatype (70+ SMTP properties). |
| `osgi-email-impl/` | OSGi bundle | `LogSmtpServer` — config-required DS component that starts an embedded SubEthaSMTP server and logs all received emails. Used for testing. |

### Deployment Layer

| Module | Type | Purpose |
|--------|------|---------|
| `osgi-email-karaf-features/` | Karaf feature | Feature XML declaring the `osgi-email` feature with dependencies on `http`, `spring [5.3,5.4)`, `guava-30`, `subethamail`, `scr`, and all required bundle JARs. |

### Test Layer

| Module | Type | Purpose |
|--------|------|---------|
| `osgi-email-itest/` | jar | Pax Exam integration tests (`@RunWith(PaxExam.class)`) running inside a real Karaf 4.4.7 container. Tests email sending with templates, attachments, and inline content against the embedded SubEthaSMTP server on port 10025. Uses JUnit 4 (Pax Exam requirement). |

## Technology Stack

### Core Technologies
- **OSGi Declarative Services** (6.0) — Component model with `@Component`, `@Reference`, `@Activate`, `@Deactivate`
- **OSGi Metatype** — Configuration via `@ObjectClassDefinition`, `@Designate`, `@AttributeDefinition`
- **Spring Mail** (5.2.0) — `JavaMailSender`, `SimpleMailMessage`, `MimeMessageHelper`
- **Handlebars** (4.2.0) — Mustache-style template engine for email bodies
- **javax.mail** (1.6.2) — Java Mail API
- **SubEthaSMTP** (3.1.7) — Embedded SMTP server for testing
- **emailaddress-rfc2822** (2.2.0) — RFC2822-compliant email address validation
- **Lombok** (1.18.34) — `@Builder`, `@Slf4j`, `@Getter`, `@SneakyThrows` code generation
- **Guava** (30.0-jre) — Utility collections

### Build & Quality
- **Maven** 3.9+ with `./mvnw` wrapper
- **Maven Bundle Plugin** (6.0.0) — OSGi bundle packaging
- **Karaf Maven Plugin** (4.4.7) — Feature verification
- **Flatten Maven Plugin** (1.1.0) — CI-friendly `${revision}` versioning
- **JUnit 5** (5.6.2) — Unit testing (Jupiter)
- **JUnit 4** — Integration testing (Pax Exam requires it)
- **Pax Exam** (4.13.5) — OSGi container integration testing
- **Mockito** (3.0.0) — Mocking
- **Hamcrest** (2.1) — Assertions
- **JaCoCo** (0.8.12) — Code coverage
- **SonarQube** — Code quality analysis

## Build Commands

```bash
# Full build
mvn clean install

# Run all tests
mvn clean test

# Run integration tests only
mvn test -pl osgi-email-itest

# Run a specific test class
mvn test -pl osgi-email-itest -Dtest=EmailITest

# Skip submodules (parent only)
mvn clean install -DskipModules=true

# Using Maven wrapper
./mvnw clean install
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `modules` | Default — includes all four submodules (active unless `-DskipModules=true`) |
| `sign-artifacts` | GPG-sign artifacts using `sign-maven-plugin` |
| `release-judong` | Deploy snapshots to JUDO Nexus (`nexus.judo.technology`) |
| `release-central` | Deploy releases to Maven Central via Sonatype OSSRH |
| `generate-github-asciidoc-diagrams` | Generate HTML from AsciiDoc with PlantUML diagram support |
| `update-source-code-license` | Update Apache 2.0 license headers in all source files |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Parent POM — shared dependencies, plugin management, profiles, `${revision}` versioning |
| `logback-test.xml` | Logback configuration for test execution |
| `.mvn/extensions.xml` | Maven extensions (wagon-file, wagon-webdav, buildtime, profile-activator) |
| `osgi-email-karaf-features/src/main/feature/feature.xml` | Karaf feature descriptor for the `osgi-email` feature |
| `osgi-email-itest/src/test/resources/test-features.xml` | Test-specific Karaf features for Pax Exam |

## Development Environment

**Required:**
- Java 21 JDK (Zulu recommended)
- Maven 3.9+ (or use `./mvnw`)

**IDE Settings:**
- `.vscode/settings.json` and `.zed/settings.json` are provided with Java format disabled, auto-import disabled, and Maven source download enabled

**OSGi Configuration for Testing:**
- `LogSmtpServer` listens on `localhost:10025`
- `JavaMailSenderActivator` configured with test SMTP credentials (`user`/`password`)

## Git Workflow

- **Main Branch:** `develop`
- **Versioning:** `${revision}` property (currently `1.0.1-SNAPSHOT`), managed by `flatten-maven-plugin`
- **Branch naming:** GitFlow — `feature/JNG-*`, `release/*`, `bugfix/JNG-*`, `support/JNG-*`, `hotfix/JNG-*`
- **Rule:** Every commit must reference a JIRA ticket (`JNG-xxx`)
- **CI:** GitHub Actions — build on push to `develop`, PRs to `develop`/`master`/`release/*`

## Important Notes

1. The `EmailMessage` uses a generic type parameter `<M>` for the template model — any object type can be used as a model, resolved via Handlebars' MapValueResolver, JavaBeanValueResolver, and FieldValueResolver.
2. `JavaMailSenderActivator` uses `ConfigurationPolicy.REQUIRE` — the service will not activate without OSGi configuration being provided.
3. The bundle plugin's Export-Package currently references `hu.blackbelt.epsilon.runtime.*` which appears to be a copy-paste artifact; the actual packages are `hu.blackbelt.email.api` and `hu.blackbelt.email.impl`.
4. Integration tests require the full Karaf container to be provisioned — they are significantly slower than unit tests.
5. The project uses `@SneakyThrows` from Lombok to handle checked exceptions in `sendMessage()`.
6. Template field is named `plaintTemplate` (not `plainTemplate`) — this is the established API name.

## Related Documentation

- [README.md](README.md) — Project overview with architecture diagrams
- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and contribution guidelines
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD pipeline and branching strategy
