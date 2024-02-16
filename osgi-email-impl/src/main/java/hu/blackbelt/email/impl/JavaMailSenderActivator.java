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
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = JavaMailSenderConfiguration.class)
@Slf4j
public class JavaMailSenderActivator {

    ServiceRegistration serviceRegistration;

    JavaMailSenderConfiguration javaMailSenderConfiguration;

    @Activate
    public void activate(ComponentContext componentContext, JavaMailSenderConfiguration javaMailSenderConfiguration) {
        serviceRegistration = componentContext.getBundleContext()
                .registerService(JavaMailSender.class, getJavaMailSender(componentContext.getProperties()), new Hashtable<>());
    }

    @Deactivate
    public void deactivate() {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
    }

    public JavaMailSender getJavaMailSender(Dictionary<String, Object> serviceParams) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        Properties props = mailSender.getJavaMailProperties();
        Enumeration<String> keys = serviceParams.keys();

        if (serviceParams.get("mail.smtp.host") != null && !serviceParams.get("mail.smtp.host").toString().isBlank()) {
            mailSender.setHost(serviceParams.get("mail.smtp.host").toString());
        }
        if (serviceParams.get("mail.smtp.user") != null && !serviceParams.get("mail.smtp.user").toString().isBlank()) {
            mailSender.setUsername(serviceParams.get("mail.smtp.user").toString());
        }
        if (serviceParams.get("mail.smtp.password") != null && !serviceParams.get("mail.smtp.password").toString().isBlank()) {
            mailSender.setPassword(serviceParams.get("mail.smtp.password").toString());
        }
        if (serviceParams.get("mail.smtp.port") != null && !serviceParams.get("mail.smtp.port").toString().isBlank()) {
            mailSender.setPort(Integer.parseInt(serviceParams.get("mail.smtp.port").toString()));
        }

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.startsWith("mail.")) {
                Object val = serviceParams.get(key);
                props.setProperty(key, val.toString());
            }
        }

        return mailSender;
    }

}
