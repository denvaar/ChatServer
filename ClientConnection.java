package ChatServer;

/*
 *  ClientConnection.java
 *
 *  This class represents a connection from the server to the client.
 *  This class is created when a new connection is made to the 
 *  ServerSocket object within the Server class.
 *
 *  As a ClientConnection object is running, it listens for messages from
 *  the client via its DataInputStream. As a message is recieved, it
 *  is relayed to the server so that all of the other client connections
 *  can recieve it.
 *
 */


import java.net.Socket;
import java.lang.Thread;
import java.io.*;

public class ClientConnection extends Thread {

    private Server myServer;
    private Socket mySocket;
    private String myIpAddress;
    private String myAlias;
    private DataOutputStream output;
    private DataInputStream input;

    public ClientConnection(Server server, Socket socket) {
        this.myServer = server;
        this.mySocket = socket;
        this.myIpAddress = socket.getInetAddress().toString().replaceAll("\\/", "");
        this.output = null;
        this.input = null;
        this.myAlias = null;

        start();
    }

    public void closeConnection() throws IOException {
        this.mySocket.close();
    }

    public DataOutputStream getClientOutputStream() throws IOException {
        return new DataOutputStream(this.mySocket.getOutputStream());
    }

    public String getIpAddress() { return this.myIpAddress; }
    public String getAlias() { return this.myAlias; }

    public void messageDispatch() throws IOException {
        this.input = new DataInputStream(this.mySocket.getInputStream());

        while (true) {
            String message = this.input.readUTF() + "|" + this.myIpAddress;
            
            if (this.myAlias == null) {
                String[] _message = message.split("\\|");
                System.out.println("Alias not set yet.");
                this.myAlias = _message[1];
            }
            this.myServer.sendToAll(message);
        }
    }

    public void run() {
        try {
            messageDispatch();
        } catch (IOException e) {
            this.myServer.removeConnection(this);
        }/* finally {
            this.myServer.removeConnection(this);
        }*/

    }

}
