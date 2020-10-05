package ro.marius.bedwars.game.mechanics;

import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientSocket {

    private final String hostName;
    private final int port;
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;

    public ClientSocket(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;

        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(hostName, port), 2000);
            this.output = new DataOutputStream(this.socket.getOutputStream());
            this.input = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientSocket clientSocket = new ClientSocket("localhost", 6666);
        clientSocket.startReading();
    }

    public void startReading() {

        String read = "";

        List<AMatch> games = new ArrayList<>(ManagerHandler.getGameManager().getPlayerMatch().values());
        AMatch match = games.isEmpty() ? null : games.get(0);

        while (!"stop".equals(read)) {

            try {

                // AICI TRIMITE DATE CONTINUU FARA SA DEA READ CU STOP

                this.output.writeUTF(match.toString());

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                System.out.println("Closing the socket.");
                this.close();
            }

        }

        try {
            this.input.close();
            this.output.close();
            this.socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) {
        try {
            this.output.writeUTF(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.input.close();
            this.output.close();
            this.socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getPort() {
        return this.port;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public DataOutputStream getOutput() {
        return this.output;
    }

    public DataInputStream getInput() {
        return this.input;
    }
}
