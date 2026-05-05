package servicargo.mail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SmtpClient {
    private final String server;
    private final int port;

    public SmtpClient(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void send(String from, String to, String subject, String body) throws IOException {
        try (Socket socket = new Socket(server, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            readLine(in);
            sendCmd(out, in, "EHLO " + server);
            sendCmd(out, in, "MAIL FROM: <" + from + ">");
            sendCmd(out, in, "RCPT TO: <" + to + ">");
            sendCmd(out, in, "DATA");

            StringBuilder msg = new StringBuilder();
            msg.append("Subject: ").append(subject).append("\r\n");
            msg.append("From: ").append(from).append("\r\n");
            msg.append("To: ").append(to).append("\r\n");
            msg.append("\r\n");
            msg.append(body).append("\r\n");
            msg.append(".\r\n");

            out.writeBytes(msg.toString());
            readLine(in);
            sendCmd(out, in, "QUIT");
        }
    }

    private void sendCmd(DataOutputStream out, BufferedReader in, String cmd) throws IOException {
        out.writeBytes(cmd + "\r\n");
        readMultiline(in);
    }

    private void readMultiline(BufferedReader in) throws IOException {
        while (true) {
            String line = readLine(in);
            if (line.length() >= 4 && line.charAt(3) == ' ') {
                break;
            }
        }
    }

    private String readLine(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("SMTP connection closed");
        }
        return line;
    }
}
