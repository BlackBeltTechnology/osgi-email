package hu.blackbelt.email.impl;

/*-
 * #%L
 * Email services :: Karaf :: Implementation
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

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import hu.blackbelt.email.api.EmailService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressValidator;
import org.osgi.service.component.annotations.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.pivovarit.function.ThrowingBiConsumer.sneaky;


@Component(immediate = true)
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Reference
    JavaMailSender emailSender;

    @Override
    public <M> void sendMessage(EmailMessage.EmailMessageBuilder<M> builder) {
        sendMessage(builder.build());
    }

    @SneakyThrows
    public void sendMessage(EmailMessage message) {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Transport.class.getClassLoader());

        try {
            boolean html = false;
            if (message.getHtmlTemplate() != null && !"".equals(message.getHtmlTemplate().trim())) {
                html = true;
            }

            boolean plain = false;
            if (message.getPlaintTemplate() != null && !"".equals(message.getPlaintTemplate().trim())) {
                plain = true;
            }

            boolean attachment = false;
            if (message.getInputStreamAttachments() != null && message.getInputStreamAttachments().size() > 0) {
                attachment = true;
            }
            if (message.getFileAttachments() != null && message.getFileAttachments().size() > 0) {
                attachment = true;
            }

            if (!html && !plain) {
                throw new RuntimeException("No HTML or Plain message defined");
            }

            if (!html && !attachment) {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(message.getFrom());
                msg.setBcc(listToArray(message.getBccs()));
                msg.setCc(listToArray(message.getCcs()));
                msg.setTo(listToArray(message.getTos()));
                msg.setSubject(message.getSubject());
                msg.setText(getMessage(message.getPlaintTemplate(), message.getModel()));
                emailSender.send(msg);

            } else {
                MimeMessage msg = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                helper.setFrom(message.getFrom());
                helper.setBcc(listToArray(message.getBccs()));
                helper.setCc(listToArray(message.getCcs()));
                helper.setTo(listToArray(message.getTos()));
                helper.setSubject(message.getSubject());

                if (html && !plain) {
                    helper.setText(getMessage(message.getHtmlTemplate(), message.getModel()), true);
                } else if (html && plain) {
                    helper.setText(getMessage(message.getPlaintTemplate(), message.getModel()),
                            getMessage(message.getHtmlTemplate(), message.getModel()));
                } else if (!html && plain) {
                    helper.setText(getMessage(message.getPlaintTemplate(), message.getModel()));
                }
                if (attachment) {
                    if (message.getFileAttachments() != null) {
                        message.getFileAttachments().forEach(
                                sneaky((String name, File file) ->
                                        helper.addAttachment(name, new FileSystemResource(file))));
                    }

                    /*
                    if (message.getInputStreamAttachments() != null) {
                        message.getInputStreamAttachments().forEach(
                                unchecked((String name, BinaryAttachment binaryAttachment) ->
                                        helper.addAttachment(name,
                                                new ByteArrayDataSource(binaryAttachment.getInputStream(),
                                                        binaryAttachment.getMimeType()))
                                )
                        );
                    } */
                }
                emailSender.send(msg);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

    }

    private String getMessage(String template, Object model) throws IOException {
        Context context = Context
                .newBuilder(model)
                .resolver(
                        MapValueResolver.INSTANCE,
                        JavaBeanValueResolver.INSTANCE,
                        FieldValueResolver.INSTANCE
                ).build();

        Handlebars handlebars = new Handlebars();
        Template tmpl = handlebars.compileInline(template);
        return tmpl.apply(context);
    }

    @SneakyThrows
    private String[] listToArray(Collection<String> strings) {
        if (strings != null && strings.size() > 0) {
            Collection<String> addresses = new ArrayList();
            for (String str : strings) {
                if (!EmailAddressValidator.isValid(str)) {
                    throw new IllegalArgumentException("Email is not valid: " + str);
                }
                addresses.add(str);
            }
            return addresses.toArray(new String[addresses.size()]);
        } else {
            return new String[]{};
        }
    }
}
