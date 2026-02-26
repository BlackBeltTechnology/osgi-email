# karaf-features Specification

## Purpose

Defines the Apache Karaf feature descriptor for deploying the OSGi Email service as a single installable feature with all required bundles and dependencies.

## Architecture

The module produces a Karaf features XML file declaring the `osgi-email` feature. This feature aggregates:

- Feature dependencies: `http`, `spring [5.3,5.4)`, `guava-30`, `subethamail`, `scr`
- Bundle JARs: `handlebars`, `osgi-email-api`, `osgi-email-impl`, `javax.mail-api`, `emailaddress-rfc2822`, `throwing-function`
- Service capability: `org.springframework.mail.javamail.JavaMailSender`

## Requirements

### Requirement: Feature SHALL declare all required bundle dependencies

The `osgi-email` feature SHALL include all bundles necessary for the email service to function without requiring manual bundle installation.

#### Scenario: Install feature in Karaf
- **GIVEN** a Karaf container with standard features available
- **WHEN** `feature:install osgi-email` is executed
- **THEN** the following bundles SHALL be installed and started: `handlebars`, `osgi-email-api`, `osgi-email-impl`, `javax.mail-api`, `emailaddress-rfc2822`, `throwing-function`

### Requirement: Feature SHALL depend on prerequisite features

The `osgi-email` feature SHALL declare dependencies on `http`, `spring [5.3,5.4)`, `guava-30`, `subethamail`, and `scr` features.

#### Scenario: Feature resolution
- **GIVEN** a Karaf container
- **WHEN** the `osgi-email` feature is resolved
- **THEN** the `scr` feature SHALL be installed as a dependency
- **AND** a Spring framework version in the `[5.3,5.4)` range SHALL be available

### Requirement: Feature SHALL advertise JavaMailSender service capability

The feature SHALL declare an `osgi.service` capability for `org.springframework.mail.javamail.JavaMailSender` so that dependent features can express service requirements.

#### Scenario: Service capability declaration
- **GIVEN** the feature XML is parsed
- **WHEN** capabilities are evaluated
- **THEN** an `osgi.service` capability with `objectClass=org.springframework.mail.javamail.JavaMailSender` SHALL be present
