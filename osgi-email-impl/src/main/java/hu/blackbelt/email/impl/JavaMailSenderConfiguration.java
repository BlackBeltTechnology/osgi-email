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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition()
public @interface JavaMailSenderConfiguration {

    @AttributeDefinition(required = false,
            name = "mail.smtp.user",
            description = "Default user name for SMTP.",
            type = AttributeType.STRING)
    String mail_smtp_user();

    @AttributeDefinition(required = false,
            name = "mail.smtp.password",
            description = "Default password for SMTP.",
            type = AttributeType.STRING)
    String mail_smtp_password();

    @AttributeDefinition(required = false,
            name = "mail.smtp.host",
            description = "The SMTP server to connect to.",
            type = AttributeType.STRING)
    String mail_smtp_host();

    @AttributeDefinition(required = false,
            name = "mail.smtp.port",
            description = "The SMTP server port to connect to, if the connect() " +
                    "method doesn't explicitly specify one. Defaults to 25.",
            type = AttributeType.INTEGER)
    int mail_smtp_port();

    @AttributeDefinition(required = false,
            name = "mail.smtp.connectiontimeout",
            description = "Socket connection timeout value in milliseconds. " +
                    "This timeout is implemented by java.net.Socket. Default is infinite timeout.",
            type = AttributeType.INTEGER)
    int mail_smtp_connectiontimeout();

    @AttributeDefinition(required = false,
            name = "mail.smtp.timeout",
            description = "Socket read timeout value in milliseconds. This timeout is " +
                    "implemented by java.net.Socket. Default is infinite timeout.",
            type = AttributeType.INTEGER)
    int mail_smtp_timeout();

    @AttributeDefinition(required = false,
            name = "mail.smtp.writetimeout",
            description = "Socket write timeout value in milliseconds. This timeout is implemented " +
                    "by using a java.util.concurrent.ScheduledExecutorService per connection that schedules " +
                    "a thread to close the socket if the timeout expires. Thus, the overhead of using this " +
                    "timeout is one thread per connection. Default is infinite timeout.",
            type = AttributeType.INTEGER)
    int mail_smtp_writetimeout();

    @AttributeDefinition(required = false,
            name = "mail.smtp.from",
            description = "Email address to use for SMTP MAIL command. This sets the envelope return address. " +
                    "Defaults to msg.getFrom() or InternetAddress.getLocalAddress(). NOTE: mail.smtp.user was " +
                    "previously used for this.",
            type = AttributeType.STRING)
    String mail_smtp_from();

    @AttributeDefinition(required = false,
            name = "mail.smtp.localhost",
            description = "Local host name used in the SMTP HELO or EHLO command. Defaults to " +
                    "InetAddress.getLocalHost().getHostName(). Should not normally need to be set if your JDK " +
                    "and your name service are configured properly.",
            type = AttributeType.STRING)
    String mail_smtp_localhost();

    @AttributeDefinition(required = false,
            name = "mail.smtp.localaddress",
            description = "Local address (host name) to bind to when creating the SMTP socket. Defaults to the " +
                    "address picked by the Socket class. Should not normally need to be set, but useful with " +
                    "multi-homed hosts where it's important to pick a particular local address to bind to.",
            type = AttributeType.STRING)
    String mail_smtp_localaddress();

    @AttributeDefinition(required = false,
            name = "mail.smtp.localport",
            description = "Local port number to bind to when creating the SMTP socket. Defaults to the port " +
                    "number picked by the Socket class.",
            type = AttributeType.INTEGER)
    int mail_smtp_localport();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ehlo",
            description = "If false, do not attempt to sign on with the EHLO command. Defaults to true. " +
                    "Normally failure of the EHLO command will fallback to the HELO command; this property exists " +
                    "only for servers that don't fail EHLO properly or don't implement EHLO properly.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_ehlo();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth",
            description = "If true, attempt to authenticate the user using the AUTH command. Defaults to false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_auth();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.mechanisms",
            description = "If set, lists the authentication mechanisms to consider, and the order in which to " +
                    "consider them. Only mechanisms supported by the server and supported by the current " +
                    "implementation will be used. The default is \"LOGIN PLAIN DIGEST-MD5 NTLM\", which includes all " +
                    "the authentication mechanisms supported by the current implementation except XOAUTH2.",
            type = AttributeType.STRING)
    String mail_smtp_auth_mechanisms();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.login.disable",
            description = "If true, prevents use of the AUTH LOGIN command. Default is false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_auth_login_disable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.plain.disable",
            description = "If true, prevents use of the AUTH PLAIN command. Default is false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_auth_plain_disable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.digest-md5.disable",
            description = "If true, prevents use of the AUTH DIGEST-MD5 command. Default is false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_auth_digest$_$md5_disable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.ntlm.disable",
            description = "If true, prevents use of the AUTH NTLM command. Default is false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_auth_ntlm_disable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.ntlm.domain",
            description = "The NTLM authentication domain.",
            type = AttributeType.STRING)
    String mail_smtp_auth_ntlm_domain();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.ntlm.flags",
            description = "NTLM protocol-specific flags. See http://curl.haxx.se/rfc/ntlm.html#theNtlmFlags " +
                    "for details.",
            type = AttributeType.INTEGER)
    int mail_smtp_auth_ntlm_flags();

    @AttributeDefinition(required = false,
            name = "mail.smtp.auth.xoauth2.disable",
            description = "If true, prevents use of the AUTHENTICATE XOAUTH2 command. Because the OAuth 2.0 protocol " +
                    "requires a special access token instead of a password, this mechanism is disabled by default. " +
                    "Enable it by explicitly setting this property to \"false\" or by setting the " +
                    "\"mail.smtp.auth.mechanisms\" property to \"XOAUTH2\".",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_auth_xoauth2_disable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.submitter",
            description = "The submitter to use in the AUTH tag in the MAIL FROM command. Typically used by a mail " +
                    "relay to pass along information about the original submitter of the message. See also the " +
                    "setSubmitter method of SMTPMessage. Mail clients typically do not use this.",
            type = AttributeType.STRING)
    String mail_smtp_submitter();

    @AttributeDefinition(required = false,
            name = "mail.smtp.dsn.notify",
            description = "The NOTIFY option to the RCPT command. Either NEVER, or some combination of SUCCESS, " +
                    "FAILURE, and DELAY (separated by commas).",
            type = AttributeType.STRING)
    String mail_smtp_dsn_notify();

    @AttributeDefinition(required = false,
            name = "mail.smtp.dsn.ret",
            description = "The RET option to the MAIL command. Either FULL or HDRS.",
            type = AttributeType.STRING)
    String mail_smtp_dsn_ret();

    @AttributeDefinition(required = false,
            name = "mail.smtp.allow8bitmime",
            description = "If set to true, and the server supports the 8BITMIME extension, text parts of messages " +
                    "that use the \"quoted-printable\" or \"base64\" encodings are converted to use \"8bit\" " +
                    "encoding if they follow the RFC2045 rules for 8bit text.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_allow8bitmime();

    @AttributeDefinition(required = false,
            name = "mail.smtp.sendpartial",
            description = "If set to true, and a message has some valid and some invalid addresses, send the " +
                    "message anyway, reporting the partial failure with a SendFailedException. If set to false " +
                    "(the default), the message is not sent to any of the recipients if there is an invalid " +
                    "recipient address.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_sendpartial();

    @AttributeDefinition(required = false,
            name = "mail.smtp.sasl.enable",
            description = "If set to true, attempt to use the javax.security.sasl package to choose an " +
                    "authentication mechanism for login. Defaults to false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_sasl_enable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.sasl.mechanisms",
            description = "A space or comma separated list of SASL mechanism names to try to use.",
            type = AttributeType.STRING)
    String mail_smtp_sasl_mechanisms();

    @AttributeDefinition(required = false,
            name = "mail.smtp.sasl.authorizationid",
            description = "The authorization ID to use in the SASL authentication. If not set, the " +
                    "authentication ID (user name) is used.",
            type = AttributeType.STRING)
    String mail_smtp_sasl_authorizationid();

    @AttributeDefinition(required = false,
            name = "mail.smtp.sasl.realm",
            description = "The realm to use with DIGEST-MD5 authentication.",
            type = AttributeType.STRING)
    String mail_smtp_sasl_realm();

    @AttributeDefinition(required = false,
            name = "mail.smtp.sasl.usecanonicalhostname",
            description = "If set to true, the canonical host name returned by InetAddress.getCanonicalHostName is " +
                    "passed to the SASL mechanism, instead of the host name used to connect. Defaults to false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_sasl_usecanonicalhostname();

    @AttributeDefinition(required = false,
            name = "mail.smtp.quitwait",
            description = "If set to false, the QUIT command is sent and the connection is immediately closed. " +
                    "If set to true (the default), causes the transport to wait for the response to the QUIT command.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_quitwait();

    @AttributeDefinition(required = false,
            name = "mail.smtp.reportsuccess", description = "If set to true, causes the transport to include an " +
            "SMTPAddressSucceededException for each address that is successful. Note also that this will cause a " +
            "SendFailedException to be thrown from the sendMessage method of SMTPTransport even if all addresses " +
            "were correct and the message was sent successfully.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_reportsuccess();

//    @AttributeDefinition(required = false,
//          name = "mail.smtp.socketFactory",
//          description = "If set to a class that implements the javax.net.SocketFactory interface, this class will
//          be used to create SMTP sockets. Note that this is an instance of a class, not a name, and must be set
//          using the put method, not the setProperty method.", type = AttributeType.SocketFactory)
//    SocketFactory mail.smtp.socketFactory();

    @AttributeDefinition(required = false,
            name = "mail.smtp.socketFactory.class",
            description = "If set, specifies the name of a class that implements the javax.net.SocketFactory " +
                    "interface. This class will be used to create SMTP sockets.",
            type = AttributeType.STRING)
    String mail_smtp_socketFactory_class();

    @AttributeDefinition(required = false,
            name = "mail.smtp.socketFactory.fallback",
            description = "If set to true, failure to create a socket using the specified socket factory class " +
                    "will cause the socket to be created using the java.net.Socket class. Defaults to true.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_socketFactory_fallback();

    @AttributeDefinition(required = false,
            name = "mail.smtp.socketFactory.port",
            description = "Specifies the port to connect to when using the specified socket factory. If not set, " +
                    "the default port will be used.",
            type = AttributeType.INTEGER)
    int mail_smtp_socketFactory_port();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ssl.enable",
            description = "If set to true, use SSL to connect and use the SSL port by default. Defaults to false " +
                    "for the \"smtp\" protocol and true for the \"smtps\" protocol.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_ssl_enable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ssl.checkserveridentity",
            description = "If set to true, check the server identity as specified by RFC 2595. These additional " +
                    "checks based on the content of the server's certificate are intended to prevent " +
                    "man-in-the-middle attacks. Defaults to false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_ssl_checkserveridentity();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ssl.trust", description = "If set, and a socket factory hasn't been specified, " +
            "enables use of a MailSSLSocketFactory. If set to \"*\", all hosts are trusted. If set to a whitespace " +
            "separated list of hosts, those hosts are trusted. Otherwise, trust depends on the certificate the " +
            "server presents.",
            type = AttributeType.STRING)
    String mail_smtp_ssl_trust();

//    @AttributeDefinition(required = false,
//          name = "mail.smtp.ssl.socketFactory",
//          description = "If set to a class that extends the javax.net.ssl.SSLSocketFactory class, this class
//          will be used to create SMTP SSL sockets. Note that this is an instance of a class, not a name, and
//          must be set using the put method, not the setProperty method.", type = AttributeType.SSLSocketFactory)
//    SSLSocketFactory mail.smtp.ssl.socketFactory();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ssl.socketFactory.class",
            description = "If set, specifies the name of a class that extends the javax.net.ssl.SSLSocketFactory " +
                    "class. This class will be used to create SMTP SSL sockets.",
            type = AttributeType.STRING)
    String mail_smtp_ssl_socketFactory_class();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ssl.socketFactory.port",
            description = "Specifies the port to connect to when using the specified socket factory. If not set, " +
                    "the default port will be used.",
            type = AttributeType.INTEGER)
    int mail_smtp_ssl_socketFactory_port();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ssl.protocols",
            description = "Specifies the SSL protocols that will be enabled for SSL connections. The property " +
                    "value is a whitespace separated list of tokens acceptable to the " +
                    "javax.net.ssl.SSLSocket.setEnabledProtocols method.",
            type = AttributeType.STRING)
    String mail_smtp_ssl_protocols();

    @AttributeDefinition(required = false,
            name = "mail.smtp.ssl.ciphersuites",
            description = "Specifies the SSL cipher suites that will be enabled for SSL connections. The property " +
                    "value is a whitespace separated list of tokens acceptable to the " +
                    "javax.net.ssl.SSLSocket.setEnabledCipherSuites method.",
            type = AttributeType.STRING)
    String mail_smtp_ssl_ciphersuites();

    @AttributeDefinition(required = false,
            name = "mail.smtp.starttls.enable",
            description = "If true, enables the use of the STARTTLS command (if supported by the server) to switch " +
                    "the connection to a TLS-protected connection before issuing any login commands. If the server " +
                    "does not support STARTTLS, the connection continues without the use of TLS; see the " +
                    "mail.smtp.starttls.required property to fail if STARTTLS isn't supported. Note that an " +
                    "appropriate trust store must configured so that the client will trust the server's certificate. " +
                    "Defaults to false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_starttls_enable();

    @AttributeDefinition(required = false,
            name = "mail.smtp.starttls.required",
            description = "If true, requires the use of the STARTTLS command. If the server doesn't support " +
                    "the STARTTLS command, or the command fails, the connect method will fail. Defaults to false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_starttls_required();

    @AttributeDefinition(required = false,
            name = "mail.smtp.proxy.host", description = "Specifies the host name of an HTTP web proxy server that " +
            "will be used for connections to the mail server.",
            type = AttributeType.STRING)
    String mail_smtp_proxy_host();

    @AttributeDefinition(required = false,
            name = "mail.smtp.proxy.port", description = "Specifies the port number for the HTTP web proxy server. " +
            "Defaults to port 80.",
            type = AttributeType.STRING)
    String mail_smtp_proxy_port();

    @AttributeDefinition(required = false,
            name = "mail.smtp.proxy.user", description = "Specifies the user name to use to authenticate with " +
            "the HTTP web proxy server. By default, no authentication is done.",
            type = AttributeType.STRING)
    String mail_smtp_proxy_user();

    @AttributeDefinition(required = false,
            name = "mail.smtp.proxy.password", description = "Specifies the password to use to authenticate with " +
            "the HTTP web proxy server. By default, no authentication is done.",
            type = AttributeType.STRING)
    String mail_smtp_proxy_password();

    @AttributeDefinition(required = false,
            name = "mail.smtp.socks.host", description = "Specifies the host name of a SOCKS5 proxy server that " +
            "will be used for connections to the mail server.",
            type = AttributeType.STRING)
    String mail_smtp_socks_host();

    @AttributeDefinition(required = false,
            name = "mail.smtp.socks.port", description = "Specifies the port number for the SOCKS5 proxy server. " +
            "This should only need to be used if the proxy server is not using the standard port number of 1080.",
            type = AttributeType.STRING)
    String mail_smtp_socks_port();

    @AttributeDefinition(required = false,
            name = "mail.smtp.mailextension", description = "Extension string to append to the MAIL command. The " +
            "extension string can be used to specify standard SMTP service extensions as well as vendor-specific " +
            "extensions. Typically the application should use the SMTPTransport method supportsExtension to verify " +
            "that the server supports the desired service extension. See RFC 1869 and other RFCs that define specific " +
            "extensions.",
            type = AttributeType.STRING)
    String mail_smtp_mailextension();

    @AttributeDefinition(required = false,
            name = "mail.smtp.userset",
            description = "If set to true, use the RSET command instead of the NOOP command in the isConnected " +
                    "method. In some cases sendmail will respond slowly after many NOOP commands; use of RSET avoids " +
                    "this sendmail issue. Defaults to false.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_userset();

    @AttributeDefinition(required = false,
            name = "mail.smtp.noop.strict",
            description = "If set to true (the default), insist on a 250 response code from the NOOP command to " +
                    "indicate success. The NOOP command is used by the isConnected method to determine if the " +
                    "connection is still alive. Some older servers return the wrong response code on success, some " +
                    "servers don't implement the NOOP command at all and so always return a failure code. Set this " +
                    "property to false to handle servers that are broken in this way. Normally, when a server times " +
                    "out a connection, it will send a 421 response code, which the client will see as the response " +
                    "to the next command it issues. Some servers send the wrong failure response code when timing " +
                    "out a connection. Do not set this property to false when dealing with servers that are broken " +
                    "in this way.",
            type = AttributeType.BOOLEAN)
    boolean mail_smtp_noop_strict();


}
