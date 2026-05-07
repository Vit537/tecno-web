package servicargo.mail;

import java.util.List;

public class MailDaemon {
    public static void main(String[] args) {
        CommandRouter router = new CommandRouter();
        MailSender sender = new MailSender(MailConfig.SMTP_SERVER, MailConfig.SMTP_PORT);

        while (true) {
            try (MailReader reader = new MailReader(
                MailConfig.POP3_SERVER,
                MailConfig.POP3_PORT,
                MailConfig.MAIL_USER,
                MailConfig.MAIL_PASS)) {

                reader.open();
                List<Integer> ids = reader.listMessageIds();
                for (int id : ids) {
                    Pop3Client.MailMessage msg = reader.retrieve(id);
                    String command = SubjectParser.extraerComando(msg.subject);
                    List<String> params = SubjectParser.extraerParametros(msg.subject);
                    String response = router.process(command, params);
                    String subject = "RE: " + msg.subject;
                    sender.send(MailConfig.MAIL_FROM, msg.from, subject, response);
                    reader.delete(id);
                }
                reader.quit();

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
