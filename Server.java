package ChatServer;

/*  Server.java
 *  
 *  At the core of this class, is the ServerSocket object. It listens
 *  for connections from a client on the given port. When a new
 *  connection is found, the connection is stored in a new socket
 *  object. These objects are then added into an array of ClientConnection
 *  objects. The array of ClientConnection objects represent all of the
 *  clients connected to the server.
 *
 *  Messages are sent by obtaining the DataOutputStream for each
 *  client connection. The sending of the actual message happens
 *  from this class.
 *
 *  When a client connection is lost or needs to be removed, it is
 *  removed from the array, and a call is issued to the client
 *  object to close the socket.
 *
 */


import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    ServerSocket ss;
    private ArrayList<ClientConnection> clients = new ArrayList<>();

    public Server(int port) throws IOException {
        
        this.ss = null;
        listen(port);

    }

    private void listen(int port) throws IOException {
        
        this.ss = new ServerSocket(port);

        System.out.println("Listening on port " + this.ss.getLocalPort());

        while (true) {

            Socket s = this.ss.accept();

            String ipAddress = s.getInetAddress().toString();
            ipAddress.replaceAll("/", "");

            System.out.println("New connection from " + ipAddress);

            ClientConnection newClientConnection = new ClientConnection(this, s);
            this.clients.add(newClientConnection); 
        }

    }

    public void removeConnection(ClientConnection client) {

        synchronized(this.clients) {
            
            System.out.println("Removing connection to " + client.getIpAddress());
            this.clients.remove(client);

            try {
                client.closeConnection();
            } catch (IOException e) {
                System.out.println("Error closing connection to " + client.getIpAddress());
            }
        }
    }

    public void sendToAll(String message) {
        System.out.println("Server::sendToAll()");
        synchronized(this.clients) {
        System.out.println("Server::sendToAll() -- synchronized");
            for (ClientConnection client : this.clients) {
                System.out.println("Server::sendToAll() -- synchronized -- for");
                System.out.println(message);
                
                try {
                    DataOutputStream output = client.getClientOutputStream();
                    output.writeUTF(message); // This is where the message is written.
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void sendToOne(String message) {

    }

    public static void main(String args[]) throws Exception {
        
        int port = 0;
        
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("Usage:    Server <port number>");
        }

        new Server(port);

    }

}
