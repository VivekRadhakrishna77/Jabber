package com.bham.fsd.assignments.jabberserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class JabberClient extends Application  {
    final static int PORT_NUMBER = 44444;
	public static Socket clientSocket;
	static ObjectOutputStream oos;
    static ObjectInputStream ois;
    
    public static void main(String[] args) {
   	
    	try{
        	connect();
        	System.out.println("Client running!");
        	launch(args);

        } catch (Exception e) {
        	System.out.println("Error: ");
            e.printStackTrace();
        }

        
    }
    
    public static void connect() throws Exception {
    	setClientSocket(new Socket("localhost", PORT_NUMBER));
    	setOos(new ObjectOutputStream(clientSocket.getOutputStream()));
    	oos.flush();
    	setOis(new ObjectInputStream(clientSocket.getInputStream()));
    	
    }
    
    //setters
    public static void setClientSocket(Socket socket) {
		JabberClient.clientSocket = socket;
	}
    
    public static void setOos(ObjectOutputStream Oos) {
		JabberClient.oos = Oos;
	}
    
    public static void setOis(ObjectInputStream Ois) {
		JabberClient.ois = Ois;
	}
    
    //getters
    public static ObjectOutputStream getOos() {
		return oos;
	}
    
    public static ObjectInputStream getOis() {
		return ois;
	}
    
    
    public static Socket getSocket() {
        return clientSocket;
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	 
        //TODO Is this required in here or should it all go in the controller?
    	Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
    	
        primaryStage.setTitle("Jabber Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();
        primaryStage.show();
        
  
}



}
