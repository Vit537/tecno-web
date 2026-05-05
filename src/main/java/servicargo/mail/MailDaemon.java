package servicargo.mail;

import servicargo.commands.CommandProcessor;

import java.util.List;

public class MailDaemon {
    public static void main(String[] args) {
        CommandProcessor processor = new CommandProcessor();
        SmtpClient smtp = new SmtpClient(MailConfig.SMTP_SERVER, MailConfig.SMTP_PORT);

        while (true) {
            try (Pop3Client pop3 = new Pop3Client(
                MailConfig.POP3_SERVER,
                MailConfig.POP3_PORT,
                MailConfig.MAIL_USER,
                MailConfig.MAIL_PASS)) {

                pop3.open();
                List<Integer> ids = pop3.listMessageIds();
                for (int id : ids) {
                    Pop3Client.MailMessage msg = pop3.retrieve(id);
                    String response = processor.process(msg.subject, msg.body);
                    String subject = "RE: " + msg.subject;
                    smtp.send(MailConfig.MAIL_FROM, msg.from, subject, response);
                    pop3.delete(id);
                }
                pop3.quit();

                Thread.sleep(10000);
            } catch (Exception e) {
                System.err.println("Mail loop error: " + e.getMessage());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
