package ro.marius.bedwars.socketclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ClientSocket extends Thread {

    public static boolean runThread = true;
    private final String hostName;
    private final int port;
    private final UUID uuid = UUID.randomUUID();
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientSocket(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public boolean establishConnection() {
        try {
            this.socket = new Socket(this.hostName, this.port);
            this.input = new DataInputStream(this.socket.getInputStream());
            this.output = new DataOutputStream(this.socket.getOutputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void sendMessage(String message) {
        try {
            this.output.writeUTF(message);
            this.output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
//		String read = "";

//		System.out.println("Reading message from server " + read);

        while (runThread) {


        }

        System.out.println("Closing the streams and socket from client.");

        this.closeSocket();

    }

    public void closeSocket(){
        try {
            this.socket.close();
        } catch (IOException io) {
            io.printStackTrace();
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

    public DataInputStream getInput() {
        return this.input;
    }

    public DataOutputStream getOutput() {
        return this.output;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
