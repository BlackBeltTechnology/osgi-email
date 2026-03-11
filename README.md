# OSGi Email Service

[![Build](https://github.com/BlackBeltTechnology/osgi-email/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/BlackBeltTechnology/osgi-email/actions/workflows/build.yml)

## Overview

An OSGi-based email service for Apache Karaf that provides a clean API for sending templated emails. The service supports Handlebars templates (both plain text and HTML), file and stream-based attachments, inline content (CID-referenced images), and full SMTP configuration via OSGi Configuration Admin.

## Architecture

The project is organized into four Maven modules that form a layered architecture — a pure API, an implementation backed by Spring Mail, a Karaf feature descriptor for deployment, and an integration test suite that spins up a real Karaf container.

```mermaid
graph TD
    API["osgi-email-api<br/><i>Service interface + DTOs</i>"]
    IMPL["osgi-email-impl<br/><i>Spring Mail + Handlebars</i>"]
    FEATURES["osgi-email-karaf-features<br/><i>Karaf feature XML</i>"]
    ITEST["osgi-email-itest<br/><i>Pax Exam integration tests</i>"]

    IMPL --> API
    FEATURES --> API
    FEATURES --> IMPL
    ITEST -.->|tests| FEATURES
    ITEST -.->|tests| API
```

### Module Details

| Module | Packaging | Purpose |
|--------|-----------|---------|
| `osgi-email-api` | `bundle` | Defines `EmailService` interface, `EmailMessage` and `BinaryAttachment` builder DTOs. No external dependencies. |
| `osgi-email-impl` | `bundle` | Implements `EmailService` using Spring's `JavaMailSender` and Handlebars templating. Includes `JavaMailSenderActivator` (config-driven OSGi component) and `LogSmtpServer` (embedded test SMTP). |
| `osgi-email-karaf-features` | `feature` | Karaf feature descriptor that bundles all required JARs and declares feature dependencies (spring, guava, subethamail, scr). |
| `osgi-email-itest` | `jar` | Integration tests using Pax Exam with JUnit 4, running inside a real Karaf container with an embedded SubEthaSMTP server. |

### Component Interaction

The OSGi runtime wires components together through Declarative Services. Configuration is provided via OSGi Config Admin, and services are discovered and injected automatically.

```mermaid
sequenceDiagram
    participant Config as OSGi Config Admin
    participant Activator as JavaMailSenderActivator
    participant Sender as JavaMailSender
    participant EmailSvc as EmailServiceImpl
    participant Client as Client Code
    participant HB as Handlebars Engine
    participant SMTP as SMTP Server

    Config->>Activator: Provide SMTP configuration
    Activator->>Sender: Create & register as OSGi service
    Sender-->>EmailSvc: @Reference injection
    Client->>EmailSvc: sendMessage(EmailMessage)
    EmailSvc->>HB: Compile & apply templates
    HB-->>EmailSvc: Rendered content
    EmailSvc->>Sender: send(MimeMessage)
    Sender->>SMTP: Deliver via SMTP
```

### Class Structure

```mermaid
classDiagram
    class EmailService {
        <<interface>>
        +sendMessage(EmailMessage message)
        +sendMessage(EmailMessageBuilder builder)
    }

    class EmailMessage~M~ {
        +String from
        +Set~String~ tos
        +Set~String~ ccs
        +Set~String~ bccs
        +String replyTo
        +String subject
        +String plaintTemplate
        +String htmlTemplate
        +M model
        +Map inputStreamAttachments
        +Map fileAttachments
        +Map inputStreamInlinedContents
        +Map fileInlinedContents
    }

    class BinaryAttachment {
        +InputStream inputStream
        +byte[] bytes
        +String mimeType
    }

    class EmailServiceImpl {
        -JavaMailSender emailSender
        +sendMessage(EmailMessage message)
        -getMessage(String template, Object model) String
    }

    class JavaMailSenderActivator {
        +activate(ComponentContext, JavaMailSenderConfiguration)
        +deactivate()
        -getJavaMailSender(Dictionary) JavaMailSender
    }

    class LogSmtpServer {
        -SMTPServer smtpServer
        +activate(Config)
        +deactivate()
        +accept(String, String) boolean
        +deliver(String, String, InputStream)
    }

    EmailService <|.. EmailServiceImpl
    EmailMessage --* EmailService
    BinaryAttachment --* EmailMessage
    EmailServiceImpl --> JavaMailSender : @Reference
    JavaMailSenderActivator --> JavaMailSender : registers
```

### Key Dependencies

```mermaid
graph LR
    subgraph External
        Spring["Spring Mail 5.2"]
        JavaMail["javax.mail 1.6"]
        Handlebars["Handlebars 4.2"]
        SubEtha["SubEthaSMTP 3.1"]
        RFC2822["emailaddress-rfc2822"]
        OSGi["OSGi DS 6.0"]
        Karaf["Apache Karaf 4.4"]
    end
    subgraph Project
        API["osgi-email-api"]
        IMPL["osgi-email-impl"]
    end
    IMPL --> Spring
    IMPL --> JavaMail
    IMPL --> Handlebars
    IMPL --> SubEtha
    IMPL --> RFC2822
    IMPL --> OSGi
    API --> OSGi
```

## Quick Start

### Requirements

- **JDK 21** (Zulu recommended)
- **Maven 3.9+**

### Build Commands

```bash
# Full build with all tests
mvn clean install

# Run only unit tests
mvn clean test

# Run integration tests (Pax Exam / Karaf container)
mvn test -pl osgi-email-itest

# Run a specific test class
mvn test -pl osgi-email-itest -Dtest=EmailITest

# Skip all submodules (parent only)
mvn clean install -DskipModules=true
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `modules` | Default — includes all four submodules |
| `sign-artifacts` | GPG-sign artifacts for release |
| `release-judong` | Deploy snapshots to JUDO Nexus |
| `release-central` | Deploy releases to Maven Central (Sonatype OSSRH) |
| `generate-github-asciidoc-diagrams` | Generate HTML from AsciiDoc with PlantUML diagrams |
| `update-source-code-license` | Update Apache 2.0 license headers in source files |

### Sending an Email (API Example)

```java
emailService.sendMessage(
    EmailService.EmailMessage.emailBuilder()
        .from("sender@example.com")
        .tos(Set.of("recipient@example.com"))
        .subject("Hello")
        .plaintTemplate("Hi {{name}}, welcome!")
        .htmlTemplate("<h1>Hi {{name}}</h1><p>Welcome!</p>")
        .model(Map.of("name", "World"))
        .build()
);
```

### OSGi Configuration

Deploy the Karaf feature and provide SMTP configuration via Config Admin:

**`etc/hu.blackbelt.email.impl.JavaMailSenderActivator.cfg`**
```properties
mail.smtp.host=smtp.example.com
mail.smtp.port=587
mail.smtp.user=user@example.com
mail.smtp.password=secret
mail.smtp.auth=true
mail.smtp.starttls.enable=true
```

## Contributing

Everyone is welcome to contribute. Please read the [Contributing Guide](CONTRIBUTING.md) and the [CI Flow documentation](.github/CIFLOW.md) for details on the development workflow.

## License

This project is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
