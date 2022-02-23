package com.bham.fsd.assignments.jabberserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class ClientConnection implements Runnable {

    private Socket clientSocket;
    
    JabberDatabase jDB;
    JabberMessage jm;
    JabberMessage request;
    
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    
    ThreadLocal<String> username = new ThreadLocal<>();
    ThreadLocal<String> jabText = new ThreadLocal<>();
    ThreadLocal<Integer> userID = new ThreadLocal<>();
    ThreadLocal<Integer> jabID = new ThreadLocal<>();
    ThreadLocal<String> toFollow = new ThreadLocal<>();
    
    public ClientConnection(Socket client, JabberDatabase jabberDatabase) throws Exception{
        this.clientSocket = client;
        this.jDB = jabberDatabase;
        //jDB.resetDatabase();
    }

    
    @Override
    public void run() {
    	
    		
	        try {
	        	
	        	oos = new ObjectOutputStream(clientSocket.getOutputStream());
	            
		        ois = new ObjectInputStream(clientSocket.getInputStream());
		        
		        
	            while(!clientSocket.isClosed()) {
	            
	            request = (JabberMessage) ois.readObject();
	            
	            if(request.getMessage().isEmpty()) {
	            	continue;
	            }
	            
	            //System.out.println(request.getMessage());
	    		
	            String[] reqParts = request.getMessage().split(" ");
	            
	            	
		            switch (reqParts[0]) {
		            
		                case "signin": {
		                    //parse username from Message
		                    username.set(reqParts[1]);
		                    
		                    //search database for username
		                    if(jDB.getUserID(username.get()) >= 0) {
		                        jm = new JabberMessage("signedin", null); //Successful sign in
		                    } else {
		                        jm = new JabberMessage("unknown-user", null); //Unsuccessful sign in
		                    }
		
		                    oos.writeObject(jm);
		                    oos.flush();
		                    break;
		
		                }
		                case "register":{
		                    //parse username from Message
		                    username.set(reqParts[1]);

		                    //register new user
		                    jDB.addUser(username.get(), username.get() + "@gmail.com");
		                    
		                    //send message to client indicating signin
		                    jm = new JabberMessage("signedin", null);
		                    oos.writeObject(jm);
		                    oos.flush();
		                    break;
		                }
		                case "signout": {
		                    
		                	ois.close();
		                	oos.close();
		                    clientSocket.close();
		                    return;
		                }
		                case "timeline": {
		                    jm = new JabberMessage("timeline", jDB.getTimelineOfUserEx(username.get()));
		                    oos.writeObject(jm);
		                    oos.flush();
		                    break;
		                }
		                case "users": {
		                	userID.set(jDB.getUserID(username.get()));
		                    jm = new JabberMessage("users", jDB.getUsersNotFollowed(userID.get()));
		                    oos.writeObject(jm);
		                    oos.flush();
		                    break;
		                }
		                case "post": {
		                    jabText.set(request.getMessage().replaceFirst(reqParts[0]+" ", ""));
		                    jDB.addJab(username.get(), jabText.get());
		                    jm = new JabberMessage("posted", null);
		                    oos.writeObject(jm);
		                    oos.flush();
		                    break;
		                }
		                case "like": {
		                    jabID.set(Integer.parseInt(reqParts[1]));
		                    jDB.addLike(jDB.getUserID(username.get()), jabID.get());
		                    jm = new JabberMessage("posted", null);
		                    oos.writeObject(jm);
		                    oos.flush();
		                    break;
		                }
		                case "follow": {
		                    toFollow.set(reqParts[1]);
		                    jDB.addFollower(jDB.getUserID(username.get()), toFollow.get());
		                    jm = new JabberMessage("posted", null);
		                    oos.writeObject(jm);
		                    oos.flush();
		                    break;
		                }
		                default:
		                    throw new IllegalArgumentException("Unexpected value: " + request.getMessage());
		            }
		            

	            }
	            
	        }catch (Exception e){
	            e.printStackTrace();
	        }
    }

}
