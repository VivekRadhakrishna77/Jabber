package com.bham.fsd.assignments.jabberserver;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button B1; //Login button
    @FXML
    private Button B2; //Register button
    @FXML
    private TextField userName; //Username field
    @FXML
    private Label signedinLabel;
   

   
    private Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    
    private static String username;
    JabberMessage jm;

    

    /**
     * This function allows a user to sign in.
     * We send a signin message to the server and add the input username.
     * We receive the message back from the server.
     * If it is valid, we set the signed in label to successfully signed in and move to startJabber
     * else, we set text to unsuccessful  and create an alert to prompt the user to try again
     * we also clear the username field.
     **/
    
    
    public void signIn() {
        username = userName.getText();

        jm = new JabberMessage("signin" + " " + username);

        try {
            
            //oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(jm);
            oos.flush();

            //ois = new ObjectInputStream(socket.getInputStream());
            JabberMessage jabberMessage = (JabberMessage) ois.readObject();
            String message = jabberMessage.getMessage();
            System.out.println(message);

            if (message.equals("unknown-user")) {
                //load a new scene showing unsuccessful login
                signedinLabel.setText("UNSUCCESSFUL");
                userName.clear();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unsuccessful login");
                alert.setContentText("That is not a valid username");
                alert.showAndWait();
            } else {
                //TODO What happens after the user signs in successfully?
                signedinLabel.setText("Successfully signed in.");
                startJabber();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * This function is called when the user enters a username into the login screen
     * and clicks log in. It registers a new account.
     */
    public void register() {
        username = userName.getText();

        //create a jm and convert username to jm with relevant protocol
        jm = new JabberMessage("register " + username);
        
        try {
        	
            oos.writeObject(jm);
            oos.flush();

           
           JabberMessage in = (JabberMessage) ois.readObject();
           if(in.getMessage().equals("signedin"))
        	   startJabber();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("this function hasnt worked");
        }

    }

    

    /**
     * This function should send a message to the server requesting the timeline.
     **/
    

    public void startJabber() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("JabberGUI.fxml"));
        Stage jabberStage = new Stage();
        jabberStage.setTitle(username);
        jabberStage.setScene(new Scene(root));
        jabberStage.centerOnScreen();
        jabberStage.show();
        

    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

        	
        	socket = JabberClient.getSocket();
        	oos = JabberClient.getOos();
        	ois = JabberClient.getOis();
            System.out.println("Connected to server socket");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
