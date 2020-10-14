package hu.blackbelt.email.impl;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.email.api.EmailService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressValidator;
import org.osgi.service.component.annotations.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;


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
                            unchecked((String name, File file) ->
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

    @FunctionalInterface
    private interface ThrowingBiConsumer<T, K> {
        void accept(T t, K k) throws Exception;
    }

    private static <T, K> BiConsumer<T, K> unchecked(
            ThrowingBiConsumer<T, K> throwingBiConsumer
    ) {
        return (i, j) -> {
            try {
                throwingBiConsumer.accept(i, j);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}