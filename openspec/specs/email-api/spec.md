# email-api Specification

## Purpose

Defines the `EmailService` interface and associated data transfer objects (`EmailMessage`, `BinaryAttachment`) that form the public API contract for sending templated emails with attachments through an OSGi service.

## Architecture

The API module contains a single interface `hu.blackbelt.email.api.EmailService` with two inner classes:

- `EmailMessage<M>` — a generic builder-based DTO representing an email message. The type parameter `M` allows any object type as the template model.
- `BinaryAttachment` — a builder-based DTO for stream-based binary attachments with MIME type metadata.

Both inner classes use Lombok `@Builder` and `@Getter`. `EmailMessage` uses `@Singular` for collection fields.

### EmailMessage Fields

| Field | Type | Purpose |
|-------|------|---------|
| `from` | `String` | Sender email address |
| `tos` | `Set<String>` | Recipient addresses (singular: `to`) |
| `ccs` | `Set<String>` | Carbon copy addresses (singular: `cc`) |
| `bccs` | `Set<String>` | Blind carbon copy addresses (singular: `bcc`) |
| `replyTo` | `String` | Reply-to address (optional) |
| `subject` | `String` | Email subject line |
| `plaintTemplate` | `String` | Plain text Handlebars template |
| `htmlTemplate` | `String` | HTML Handlebars template |
| `model` | `M` | Template model object |
| `inputStreamAttachments` | `Map<String, BinaryAttachment>` | Named stream-based attachments |
| `inputStreamInlinedContents` | `Map<String, BinaryAttachment>` | Named stream-based inline content (CID) |
| `fileAttachments` | `Map<String, File>` | Named file-based attachments |
| `fileInlinedContents` | `Map<String, File>` | Named file-based inline content (CID) |

### BinaryAttachment Fields

| Field | Type | Purpose |
|-------|------|---------|
| `inputStream` | `InputStream` | Binary data as a stream |
| `bytes` | `byte[]` | Binary data as a byte array |
| `mimeType` | `String` | MIME type of the attachment |

## Requirements

### Requirement: EmailService interface SHALL define sendMessage methods

The `EmailService` interface SHALL provide two overloaded `sendMessage` methods — one accepting an `EmailMessage<M>` and one accepting an `EmailMessage.EmailMessageBuilder<M>`.

#### Scenario: Send message with built EmailMessage
- **GIVEN** an `EmailMessage<M>` instance built via `EmailMessage.emailBuilder()`
- **WHEN** `sendMessage(EmailMessage<M> message)` is called
- **THEN** the message SHALL be sent to all specified recipients

#### Scenario: Send message with builder directly
- **GIVEN** an `EmailMessage.EmailMessageBuilder<M>` with all required fields set
- **WHEN** `sendMessage(EmailMessage.EmailMessageBuilder<M> builder)` is called
- **THEN** the builder SHALL be built and the resulting message SHALL be sent

### Requirement: EmailMessage SHALL support builder pattern construction

The `EmailMessage` class SHALL be constructable via Lombok's `@Builder(builderMethodName = "emailBuilder")` pattern.

#### Scenario: Build message with singular collection items
- **GIVEN** an `EmailMessageBuilder` instance
- **WHEN** `.to("a@b.com").to("c@d.com").cc("e@f.com")` is called
- **THEN** `tos` SHALL contain both addresses and `ccs` SHALL contain one address

### Requirement: EmailMessage SHALL support both text and HTML templates

The `EmailMessage` SHALL carry both `plaintTemplate` and `htmlTemplate` fields to support plain text, HTML, or multipart alternative email bodies.

#### Scenario: Plain text only message
- **GIVEN** an `EmailMessage` with `plaintTemplate` set and `htmlTemplate` null
- **WHEN** the message is processed
- **THEN** only the plain text body SHALL be rendered

#### Scenario: Multipart message with both templates
- **GIVEN** an `EmailMessage` with both `plaintTemplate` and `htmlTemplate` set
- **WHEN** the message is processed
- **THEN** a multipart alternative body SHALL be rendered with both variants

### Requirement: BinaryAttachment SHALL support stream and byte array input

The `BinaryAttachment` class SHALL accept binary content via either `inputStream` or `bytes`, along with a `mimeType` descriptor.

#### Scenario: Create attachment from input stream
- **GIVEN** a `BinaryAttachment` built with `inputStream` and `mimeType("image/png")`
- **WHEN** the attachment is added to an `EmailMessage`
- **THEN** the attachment SHALL be accessible with the specified MIME type
