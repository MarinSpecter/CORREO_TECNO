package communication;

import utils.Email;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import io.github.cdimascio.dotenv.Dotenv;

public class SendEmailThread implements Runnable {

    private static final Dotenv dotenv = Dotenv.configure().load();

    // Variables configuradas dinámicamente
    private final static String HOST;
    private final static String PORT_SMTP;
    private final static String PROTOCOL;
    private final static String USER;
    private final static String MAIL;
    private final static String MAIL_PASSWORD;
    private static final Properties properties = new Properties();

    static {
        String server = dotenv.get("SMTP_SERVER");

        if ("gmail".equalsIgnoreCase(server)) {
            HOST = dotenv.get("SMTP_HOST_GMAIL");
            PORT_SMTP = dotenv.get("SMTP_PORT_GMAIL");
            PROTOCOL = dotenv.get("SMTP_PROTOCOL_GMAIL");
            USER = dotenv.get("SMTP_USER_GMAIL");
            MAIL = dotenv.get("SMTP_MAIL_GMAIL");
            MAIL_PASSWORD = dotenv.get("SMTP_PASSWORD_GMAIL");

            // Configuración de propiedades para Gmail
            properties.setProperty("mail.smtp.host", HOST);
            properties.setProperty("mail.smtp.port", PORT_SMTP);
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.ssl.enable", "true");

        } else if ("tecnoweb".equalsIgnoreCase(server)) {
            HOST = dotenv.get("SMTP_HOST_TECNOWEB");
            PORT_SMTP = dotenv.get("SMTP_PORT_TECNOWEB");
            PROTOCOL = "smtp";  // Protocolo fijo para tecnoweb
            USER = dotenv.get("SMTP_USER_TECNOWEB");
            MAIL = dotenv.get("SMTP_MAIL_TECNOWEB");
            MAIL_PASSWORD = dotenv.get("SMTP_PASSWORD_TECNOWEB");

            // Configuración de propiedades para Tecnoweb
            properties.setProperty("mail.smtp.host", HOST);
            properties.setProperty("mail.smtp.port", PORT_SMTP);
            properties.setProperty("mail.smtp.auth", "false");
            properties.setProperty("mail.smtp.starttls.enable", "false");
            //properties.setProperty("mail.smtp.localhost", "tecnoweb.org.bo");
        } else {
            throw new IllegalArgumentException("Servidor SMTP no reconocido. Configura 'SMTP_SERVER' en el archivo .env como 'gmail' o 'tecnoweb'.");
        }
    }

    private final Email email;

    public SendEmailThread(Email email) {
        this.email = email;
    }

    @Override
    public void run() {
        // Uso de las propiedades ya configuradas condicionalmente
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, MAIL_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL));

            InternetAddress[] toAddresses = {new InternetAddress(email.getTo())};
            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);
            message.setSubject(email.getSubject());

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);
            message.saveChanges();

            Transport.send(message);
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmailThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}