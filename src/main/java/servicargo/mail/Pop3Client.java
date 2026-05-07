package servicargo.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Pop3Client implements AutoCloseable {
    public static class MailMessage {
        public final String from;
        public final String subject;
        public final String body;
        public final int index;

        public MailMessage(String from, String subject, String body, int index) {
            this.from = from;
            this.subject = subject;
            this.body = body;
            this.index = index;
        }
    }

    private final String server;
    private final int port;
    private final String user;
    private final String pass;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Pop3Client(String server, int port, String user, String pass) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public void open() throws IOException {
        socket = new Socket(server, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        expectOk(in.readLine());
        send("USER " + user);
        send("PASS " + pass);
    }

    public List<Integer> listMessageIds() throws IOException {
        sendRaw("STAT");
        String stat = in.readLine();
        expectOk(stat);
        String[] parts = stat.split(" ");
        int total = Integer.parseInt(parts[1]);
        List<Integer> ids = new ArrayList<>();
        if (total == 0) {
            return ids;
        }

        sendRaw("LIST");
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals(".")) break;
            if (line.startsWith("+OK")) continue;
            String[] msgParts = line.split(" ");
            ids.add(Integer.parseInt(msgParts[0]));
        }
        return ids;
    }

    public MailMessage retrieve(int msgNum) throws IOException {
        sendRaw("RETR " + msgNum);
        String line = in.readLine();
        expectOk(line);

        String from = "";
        String subject = "";
        boolean inBody = false;

        while ((line = in.readLine()) != null) {
            if (line.equals(".")) break;
            if (!inBody) {
                if (line.isEmpty()) {
                    inBody = true;
                    continue;
                }
                if (line.toLowerCase().startsWith("from:")) {
                    from = parseAddress(line.substring(5).trim());
                }
                if (line.toLowerCase().startsWith("subject:")) {
                    subject = line.substring(8).trim();
                }
            } else {
                // Body is intentionally ignored per new subject-only format.
            }
        }

        return new MailMessage(from, subject, "", msgNum);
    }

    public void delete(int msgNum) throws IOException {
        send("DELE " + msgNum);
    }

    public void quit() throws IOException {
        sendRaw("QUIT");
        in.readLine();
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    private void send(String command) throws IOException {
        sendRaw(command);
        expectOk(in.readLine());
    }

    private void sendRaw(String command) {
        out.println(command);
    }

    private void expectOk(String response) throws IOException {
        if (response == null || !response.startsWith("+OK")) {
            throw new IOException("POP3 error: " + response);
        }
    }

    private String parseAddress(String value) {
        int lt = value.indexOf('<');
        int gt = value.indexOf('>');
        if (lt >= 0 && gt > lt) {
            return value.substring(lt + 1, gt);
        }
        return value;
    }
}
