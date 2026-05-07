package servicargo.mail;

import java.io.IOException;

public class MailSender {
    private final SmtpClient client;

    public MailSender(String server, int port) {
        this.client = new SmtpClient(server, port);
    }

    public void send(String from, String to, String subject, String body) throws IOException {
        client.send(from, to, subject, body);
    }
}
