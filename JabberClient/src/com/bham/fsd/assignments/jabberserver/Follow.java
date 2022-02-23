package com.bham.fsd.assignments.jabberserver;


import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Follow {
	
		
		private final SimpleStringProperty user;
		private final Button follow;
		
		
		
		public Follow(String User, Button Follow) {
			super();
			this.user = new SimpleStringProperty(User);
			this.follow = Follow;
			
		}


		public String getUser() {
			return user.get();
		}


		public Button getFollow() {
			Image likeImg = new Image("follow.png", 30, 30, false, false);
		    ImageView likeView = new ImageView(likeImg);
		    follow.setGraphic(likeView);
			return follow;
		}
}