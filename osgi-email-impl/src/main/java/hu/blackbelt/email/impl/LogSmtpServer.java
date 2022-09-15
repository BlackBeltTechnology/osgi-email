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

import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = LogSmtpServer.Config.class)
public class LogSmtpServer implements SimpleMessageListener {

    @ObjectClassDefinition()
    public @interface Config {

        @AttributeDefinition(required = false, name = "Log SMTP server hostname")
        String logSmtpServerHost() default "localhost";

        @AttributeDefinition(required = false, name = "Log SMTP server port", type = AttributeType.INTEGER)
        int logSmtpServerPort() default 10025;
    }

    private SMTPServer smtpServer;

    @Activate
    public void activate(Config config) {
        smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(this));
        smtpServer.setHostName(config.logSmtpServerHost());
        smtpServer.setPort(config.logSmtpServerPort());
        smtpServer.start();
    }

    @Deactivate
    public void deactivate() {
        smtpServer.stop();
    }

    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8))) {
            final String body = br.lines().collect(Collectors.joining(System.lineSeparator()));
            log.info("\nFrom: " + from + "\nTo: " + recipient + "Data: \n" + body);
        }
    }
}
