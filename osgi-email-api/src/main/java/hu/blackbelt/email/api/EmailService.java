package hu.blackbelt.email.api;

/*-
 * #%L
 * Email services :: Karaf :: API
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
