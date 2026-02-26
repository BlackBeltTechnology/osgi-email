# integration-tests Specification

## Purpose

Validates the complete OSGi Email service deployment and email sending functionality inside a real Apache Karaf container using Pax Exam.

## Architecture

The integration test module uses Pax Exam to provision a Karaf container with the `osgi-email` feature and test features. Key components:

- `EmailITest` — JUnit 4 test class with `@RunWith(PaxExam.class)` and `@ExamReactorStrategy(PerClass.class)`. Injects `EmailService`, `SimpleMessageListener`, `BundleContext`, and `LogService`.
- `KarafFeatureProvider` — Utility class providing Karaf container configuration, port allocation, service lookup with timeout, and bundle assertions.

### Test Infrastructure

The test configures:
- `LogSmtpServer` on `localhost:10025` via OSGi Config Admin
- `JavaMailSenderActivator` with test credentials (`user`/`password`) pointing to `localhost:10025`
- Test Karaf features from `test-features.xml` (SubEthaSMTP, SCR, wrap)

## Requirements

### Requirement: Integration test SHALL verify email delivery through the full stack

The test SHALL send an email through `EmailService` and verify it is received by the embedded `LogSmtpServer`.

#### Scenario: Send templated email with attachments
- **GIVEN** a Karaf container with the `osgi-email` feature installed and `LogSmtpServer` running on port 10025
- **AND** `JavaMailSenderActivator` configured to connect to `localhost:10025`
- **WHEN** `emailService.sendMessage()` is called with a message containing:
  - from address, to address, cc, bcc
  - subject line
  - plain text and HTML Handlebars templates with a model
  - file attachments and file-based inline content
  - stream-based attachments and stream-based inline content
- **THEN** the email SHALL be delivered to the `LogSmtpServer`
- **AND** the `SimpleMessageListener` SHALL receive the message

### Requirement: Pax Exam container SHALL provision all required services

The Karaf container SHALL be configured to have all OSGi services available for injection.

#### Scenario: Service injection
- **GIVEN** the Pax Exam container is started with the `osgi-email` feature
- **WHEN** the test class is instantiated
- **THEN** `EmailService`, `SimpleMessageListener`, `BundleContext`, and `LogService` SHALL all be injectable via `@Inject`

### Requirement: Test SHALL use PerClass reactor strategy

The test SHALL use `@ExamReactorStrategy(PerClass.class)` to reuse a single Karaf container across all test methods in the class, avoiding the overhead of repeated container provisioning.

#### Scenario: Container reuse
- **GIVEN** multiple test methods in `EmailITest`
- **WHEN** the test suite runs
- **THEN** a single Karaf container SHALL be provisioned and shared across all test methods
