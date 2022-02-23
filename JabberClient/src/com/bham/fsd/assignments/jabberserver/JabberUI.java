package com.bham.fsd.assignments.jabberserver;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class JabberUI implements Initializable {

    private static Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static ArrayList<ArrayList<String>> timelineData;

    JabberMessage jm;



    Image heartImg = new Image("smallHeart.png");
    ImageView heartView = new ImageView(heartImg);

    Image likeImg = new Image("follow.png", 30, 30, false, false);
    ImageView likeView = new ImageView(likeImg);

    //FXML declarations
    @FXML
    private TableView<Post> jabberTable;

    @FXML
    private TableColumn<Post, String> jabposter;

    @FXML
    private TableColumn<Post, String> jabtext;

    @FXML //the heart button used to signify liking it
    private TableColumn<Post, Button> heartButton;

    @FXML //the number of likes a certain post has
    private TableColumn<Post, Integer> numberOfLikes;

    // post Jab
    @FXML
    private TextField jabTextToPost;

    @FXML
    private Button postJab;

    @FXML
    private Button signout;

    @FXML
    private TableView<Follow> followTable;

    @FXML
    private TableColumn<Follow, String> nameFollow;

    @FXML
    private TableColumn<Follow, Boolean> boolFollow;



    //this needs to fill in the values
    public ObservableList<String> userList = FXCollections.observableArrayList();
    public ObservableList<String> jabList = FXCollections.observableArrayList();
    public ObservableList<Integer> jabIDList = FXCollections.observableArrayList();
    public ObservableList<Integer> heartList = FXCollections.observableArrayList();
    public ObservableList<Post> timelineList = FXCollections.observableArrayList();


    public ObservableList<Follow> followList = FXCollections.observableArrayList();
    public ObservableList<String> userFollowList = FXCollections.observableArrayList();


    public void showTimeline() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                try {

                    userList.clear();
                    jabList.clear();
                    jabIDList.clear();
                    heartList.clear();
                    timelineList.clear();

                    jm = new JabberMessage("timeline");
                    oos.writeObject(jm);
                    oos.flush();

                    //get the message with the data back
                    jm = (JabberMessage) ois.readObject();

                    //create a 2d arraylist to hold the data from the JabberMessage
                    timelineData = jm.getData();


                    jabposter.setText("Username");

                    jabtext.setText("Jab");

                    heartButton.setText("Like");

                    numberOfLikes.setText("Number of Likes");


                    for(int i=0; i<timelineData.size();i++) {
                        userList.add(timelineData.get(i).get(0));
                    }

                    for(int i=0; i<timelineData.size();i++) {
                        jabList.add(timelineData.get(i).get(1));
                    }

                    for(int i=0; i<timelineData.size();i++) {
                        jabIDList.add(Integer.parseInt(timelineData.get(i).get(2)));
                    }

                    for(int i=0; i<timelineData.size();i++) {
                        heartList.add(Integer.parseInt(timelineData.get(i).get(3)));
                    }

                    for(int i=0; i<userList.size();i++) {
                        Button likeBtn = new Button();
                        likeBtn.setText(jabIDList.get(i).toString());
                        likeBtn.setTextFill(Color.GREY);




                        Post newPost = getPost(userList.get(i), jabList.get(i), likeBtn, heartList.get(i));
                        timelineList.add(newPost);

                    }

                    //Set Cell Values for each Column
                    jabposter.setCellValueFactory(new PropertyValueFactory<Post, String>("user"));
                    jabtext.setCellValueFactory(new PropertyValueFactory<Post, String>("jab"));
                    heartButton.setCellValueFactory(new PropertyValueFactory<Post, Button>("like"));
                    numberOfLikes.setCellValueFactory(new PropertyValueFactory<Post, Integer>("numberLikes"));


                    jabberTable.setItems(timelineList);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }



    public void showUsers() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {

                    userFollowList.clear();
                    followList.clear();

                    jm = new JabberMessage("users");
                    oos.writeObject(jm);
                    oos.flush();

                    JabberMessage in = (JabberMessage) ois.readObject();
                    ArrayList<ArrayList<String>> usersData = in.getData();





                    for(int i=0; i<usersData.size();i++) {
                        userFollowList.addAll(usersData.get(i));
                    }


                    for(int i=0; i<userFollowList.size();i++) {
                        Button followBtn = new Button();
                        Follow newFollow = getFollow(userFollowList.get(i), followBtn);
                        followList.add(newFollow);
                    }


                    nameFollow.setCellValueFactory(new PropertyValueFactory<Follow, String>("user"));
                    boolFollow.setCellValueFactory(new PropertyValueFactory<Follow, Boolean>("follow"));


                    followTable.setItems(followList);



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public Post getPost(String User, String Jab, Button btn, Integer Likes) {
        btn.setGraphic(heartView);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                likeJab(btn);
            }
        });
        return new Post(User, Jab, btn, Likes);

    }

    public Follow getFollow(String User, Button btn) {
        btn.setGraphic(likeView);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                followUser(User);
            }
        });
        return new Follow(User, btn);

    }


    public void postingJab() {
        try {
            String text = jabTextToPost.getText();

            jm = new JabberMessage("post " + text);
            oos.writeObject(jm);
            oos.flush();
            JabberMessage in = (JabberMessage) ois.readObject();
            System.out.println(in.getMessage());
            jabTextToPost.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void likeJab(Button like) {

        try {
            String jabid = like.getText();
            jm = new JabberMessage("like " + jabid);
            oos.writeObject(jm);
            oos.flush();

            JabberMessage in = (JabberMessage) ois.readObject();
            System.out.println(in.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void followUser(String user) {
        try {

            jm = new JabberMessage("follow " + user);
            oos.writeObject(jm);
            oos.flush();

            JabberMessage in = (JabberMessage) ois.readObject();
            System.out.println(in.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signOut() throws IOException {

        try {
        	// send the signout message back to the server

            jm = new JabberMessage("signout");

            oos.writeObject(jm);
            oos.flush();
            socket.close();


        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
        System.exit(1);
    }


    //attempting to provide two timers to send
    public void guiUpdate() {
        Timer timelinetimer = new Timer();
        timelinetimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                showTimeline();
            }
        },2000,1000);

        Timer userTimer = new Timer();
        userTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                showUsers();
            }
        },1000,1000);
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub

        socket = JabberClient.getSocket();
        oos = JabberClient.getOos();
        ois = JabberClient.getOis();

        //initially display
        showTimeline();
        showUsers();

        //begin function to periodically update
        guiUpdate();



    }


}
