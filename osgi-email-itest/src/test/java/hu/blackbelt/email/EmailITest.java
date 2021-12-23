package hu.blackbelt.email;

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.email.api.EmailService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.subethamail.smtp.helper.SimpleMessageListener;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static hu.blackbelt.email.api.EmailService.BinaryAttachment.binaryAttachmentBuilder;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EmailITest {
    public static final String BLACKBELT = "hu.blackbelt";
    public static final String OSGI_EMAIL_KARAF_FEATURES = "osgi-email-karaf-features";
    public static final String FEATURES = "features";
    public static final String XML = "xml";
    @Inject
    LogService log;

    @Inject
    BundleContext bundleContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Inject
    EmailService emailService;

    @Inject
    SimpleMessageListener messageListener;

    public static MavenArtifactUrlReference osgiEmail() {
        return maven()
                .groupId(BLACKBELT)
                .artifactId(OSGI_EMAIL_KARAF_FEATURES)
                .versionAsInProject()
                .classifier(FEATURES)
                .type(XML);
    }
    @Configuration
    public Option[] config() throws MalformedURLException {

        return combine(KarafFeatureProvider.karafConfig(this.getClass()),

                editConfigurationFilePut("etc/hu.blackbelt.email.impl.LogSmtpServer.cfg",
                        "logSmtpServerPort", "10025"),

                editConfigurationFilePut("etc/hu.blackbelt.email.impl.JavaMailSenderActivator.cfg",
                        "mail.smtp.user", "user"),
                editConfigurationFilePut("etc/hu.blackbelt.email.impl.JavaMailSenderActivator.cfg",
                        "mail.smtp.password", "password"),
                editConfigurationFilePut("etc/hu.blackbelt.email.impl.JavaMailSenderActivator.cfg",
                        "mail.smtp.host", "localhost"),
                editConfigurationFilePut("etc/hu.blackbelt.email.impl.JavaMailSenderActivator.cfg",
                        "mail.smtp.port", "10025"),

                features(osgiEmail(), "osgi-email")

        );

    }

    @Test
    public void test() throws UnsupportedEncodingException {
        emailService.sendMessage(EmailService.EmailMessage.emailBuilder()
                        .from("rr@dd.hu")
                        .to("adfadf@sdasdas.ju")
//                .htmlTemplate("<b>dasdasd</b><span>asdjasdj {{ test }}asasdasd</span>")
                        .inputStreamAttachment("test.txt",
                                binaryAttachmentBuilder()
                                        .inputStream(new ByteArrayInputStream("Test".getBytes("UTF-8")))
                                        .mimeType("text/plain").build())
                        .plaintTemplate("dasdasd asdjasdj {{ test }}asasdasd")
                        .model(ImmutableMap.of("test", "Ejjj!"))
                        .subject("Tesadvajsd askdh akshdas")
        );


        Assert.assertTrue(true);
    }
}