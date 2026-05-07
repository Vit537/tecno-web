package servicargo.mail;

import java.io.IOException;
import java.util.List;

public class MailReader implements AutoCloseable {
    private final Pop3Client client;

    public MailReader(String server, int port, String user, String pass) {
        this.client = new Pop3Client(server, port, user, pass);
    }

    public void open() throws IOException {
        client.open();
    }

    public List<Integer> listMessageIds() throws IOException {
        return client.listMessageIds();
    }

    public Pop3Client.MailMessage retrieve(int msgNum) throws IOException {
        return client.retrieve(msgNum);
    }

    public void delete(int msgNum) throws IOException {
        client.delete(msgNum);
    }

    public void quit() throws IOException {
        client.quit();
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
