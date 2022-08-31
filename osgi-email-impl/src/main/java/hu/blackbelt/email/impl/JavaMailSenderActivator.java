package hu.blackbelt.email.impl;

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