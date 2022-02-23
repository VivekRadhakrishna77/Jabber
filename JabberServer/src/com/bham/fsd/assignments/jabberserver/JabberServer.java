package com.bham.fsd.assignments.jabberserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JabberServer implements Runnable {

    private static final int PORT_NUMBER = 44444;
    private ServerSocket serverSocket;

    private static ArrayList<ClientConnection> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(3);
    private static ExecutorService executor;



    public JabberServer() throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
        serverSocket.setReuseAddress(true);
        //serverSocket.setSoTimeout(30000);
        new Thread(this).start();

    }
    @Override
    public void run() {

        while (true) {
            try {

                Socket clientSocket = serverSocket.accept();
                

                System.out.println("INFO CLIENT: " + clientSocket + " is connected");
                Thread.sleep(300);
                ClientConnection client = new ClientConnection(clientSocket, new JabberDatabase());
                
                clients.add(client);
                pool.execute(client);
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        try {
        	JabberServer jabberServer = new JabberServer();
        	
            System.out.println("Server waiting for connections");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

