package hu.blackbelt.email.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public interface EmailService {

    <M> void sendMessage(EmailMessage<M> message);

    <M> void sendMessage(EmailMessage.EmailMessageBuilder<M> builder);

    @Builder(builderMethodName = "emailBuilder")
    @Getter
    class EmailMessage<M> {
        String from;
        @Singular
        Set<String> tos;
        @Singular
        Set<String> bccs;
        @Singular
        Set<String> ccs;
        @Singular
        Map<String, BinaryAttachment> inputStreamAttachments;
        @Singular
        Map<String, File> fileAttachments;
        String subject;
        String plaintTemplate;
        String htmlTemplate;
        M model;
    }

    @Builder(builderMethodName = "binaryAttachmentBuilder")
    @Getter
    class BinaryAttachment {
        InputStream inputStream;
        byte[] bytes;
        String mimeType;
    }

}
