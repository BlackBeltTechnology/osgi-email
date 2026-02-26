# email-impl Specification

## Purpose

Implements the `EmailService` interface using Spring Mail (`JavaMailSender`) for SMTP delivery and Handlebars for template processing. Provides OSGi configuration-driven activation of the mail sender and an embedded SMTP server for testing.

## Architecture

The implementation module contains three OSGi Declarative Services components:

- `EmailServiceImpl` — Immediate component implementing `EmailService`. Injects `JavaMailSender` via `@Reference`. Processes templates with Handlebars and sends via Spring Mail.
- `JavaMailSenderActivator` — Configuration-required component that reads SMTP settings from OSGi Config Admin and registers a `JavaMailSender` service. Configured via `JavaMailSenderConfiguration` metatype.
- `LogSmtpServer` — Configuration-required component that starts an embedded SubEthaSMTP server and logs received emails. Implements `SimpleMessageListener`.

### Component Dependencies

```
JavaMailSenderActivator --registers--> JavaMailSender (OSGi service)
EmailServiceImpl --@Reference--> JavaMailSender
EmailServiceImpl --uses--> Handlebars (template engine)
EmailServiceImpl --uses--> EmailAddressValidator (RFC2822)
LogSmtpServer --implements--> SimpleMessageListener
```

## Requirements

### Requirement: EmailServiceImpl SHALL send plain text emails via SimpleMailMessage

When the message has only a plain text template and no attachments or inline content, `EmailServiceImpl` SHALL use Spring's `SimpleMailMessage` for delivery.

#### Scenario: Send plain text email
- **GIVEN** an `EmailMessage` with `plaintTemplate` set, `htmlTemplate` null, and no attachments
- **WHEN** `sendMessage()` is called
- **THEN** a `SimpleMailMessage` SHALL be created with from, to, cc, bcc, subject, and the Handlebars-rendered plain text body
- **AND** `emailSender.send(SimpleMailMessage)` SHALL be invoked

### Requirement: EmailServiceImpl SHALL send MIME messages for HTML or attachments

When the message has an HTML template or any attachments/inline content, `EmailServiceImpl` SHALL use `MimeMessage` with `MimeMessageHelper` for delivery.

#### Scenario: Send HTML-only email
- **GIVEN** an `EmailMessage` with `htmlTemplate` set and `plaintTemplate` null
- **WHEN** `sendMessage()` is called
- **THEN** a `MimeMessage` SHALL be created with the HTML body (isHtml=true)

#### Scenario: Send multipart alternative email
- **GIVEN** an `EmailMessage` with both `plaintTemplate` and `htmlTemplate` set
- **WHEN** `sendMessage()` is called
- **THEN** `MimeMessageHelper.setText(plainText, htmlText)` SHALL be called to create a multipart alternative message

#### Scenario: Send email with file attachments
- **GIVEN** an `EmailMessage` with `fileAttachments` containing named file entries
- **WHEN** `sendMessage()` is called
- **THEN** each file SHALL be added via `helper.addAttachment(name, FileSystemResource)`

#### Scenario: Send email with stream-based inline content
- **GIVEN** an `EmailMessage` with `inputStreamInlinedContents` containing CID-keyed binary attachments
- **WHEN** `sendMessage()` is called
- **THEN** each entry SHALL be added via `helper.addInline(cid, ByteArrayDataSource)`

### Requirement: EmailServiceImpl SHALL reject messages without any template

If neither `plaintTemplate` nor `htmlTemplate` is provided (both null or blank), `EmailServiceImpl` SHALL throw a `RuntimeException` with message "No HTML or Plain message defined".

#### Scenario: No template provided
- **GIVEN** an `EmailMessage` with both `plaintTemplate` and `htmlTemplate` null
- **WHEN** `sendMessage()` is called
- **THEN** a `RuntimeException("No HTML or Plain message defined")` SHALL be thrown

### Requirement: EmailServiceImpl SHALL validate email addresses using RFC2822

All email addresses in `tos`, `ccs`, and `bccs` SHALL be validated using `EmailAddressValidator.isValid()`. Invalid addresses SHALL cause an `IllegalArgumentException`.

#### Scenario: Invalid email address in recipients
- **GIVEN** an `EmailMessage` with `tos` containing `"not-an-email"`
- **WHEN** `sendMessage()` is called
- **THEN** an `IllegalArgumentException("Email is not valid: not-an-email")` SHALL be thrown

### Requirement: EmailServiceImpl SHALL process templates with Handlebars

Templates SHALL be compiled inline using `Handlebars.compileInline()` and applied with a context built from the message model using `MapValueResolver`, `JavaBeanValueResolver`, and `FieldValueResolver`.

#### Scenario: Template variable interpolation
- **GIVEN** a `plaintTemplate` of `"Hello {{name}}"` and a model `Map.of("name", "World")`
- **WHEN** the template is processed
- **THEN** the result SHALL be `"Hello World"`

### Requirement: EmailServiceImpl SHALL set reply-to when provided

When `EmailMessage.replyTo` is non-null, `EmailServiceImpl` SHALL set the reply-to header on the outgoing message.

#### Scenario: Reply-to address set
- **GIVEN** an `EmailMessage` with `replyTo` set to `"reply@example.com"`
- **WHEN** `sendMessage()` is called
- **THEN** the outgoing message SHALL have reply-to set to `"reply@example.com"`

### Requirement: JavaMailSenderActivator SHALL register JavaMailSender from OSGi config

`JavaMailSenderActivator` SHALL read SMTP configuration from OSGi Config Admin properties and register a configured `JavaMailSender` service.

#### Scenario: Activate with SMTP configuration
- **GIVEN** OSGi configuration with `mail.smtp.host=smtp.example.com`, `mail.smtp.port=587`, `mail.smtp.user=user`, `mail.smtp.password=pass`
- **WHEN** the component is activated
- **THEN** a `JavaMailSenderImpl` SHALL be created with host, port, username, and password set
- **AND** all `mail.*` properties SHALL be passed through to `JavaMailProperties`
- **AND** the `JavaMailSender` service SHALL be registered in the OSGi service registry

#### Scenario: Deactivate unregisters service
- **WHEN** the component is deactivated
- **THEN** the `JavaMailSender` service registration SHALL be unregistered

### Requirement: LogSmtpServer SHALL start an embedded SMTP server for testing

`LogSmtpServer` SHALL start a SubEthaSMTP server on the configured host and port, accepting all messages and logging them via SLF4J.

#### Scenario: Start and receive email
- **GIVEN** `LogSmtpServer` configured with `logSmtpServerHost=localhost` and `logSmtpServerPort=10025`
- **WHEN** the component activates
- **THEN** an SMTP server SHALL listen on `localhost:10025`
- **AND** all incoming emails SHALL be accepted (`accept()` returns true)
- **AND** received email content SHALL be logged with from, to, and body

#### Scenario: Stop on deactivation
- **WHEN** the component is deactivated
- **THEN** the SMTP server SHALL be stopped
