package com.bham.fsd.assignments.jabberserver;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Post {
	
		private final SimpleStringProperty user;
		private final SimpleStringProperty jab;
		private final Button like;
		private final SimpleIntegerProperty numberLikes;
		
		
		public Post(String User, String Jab, Button Like, Integer NumberLikes) {
			super();
			this.user = new SimpleStringProperty(User);
			this.jab = new SimpleStringProperty(Jab);
			this.like = Like;
			this.numberLikes = new SimpleIntegerProperty(NumberLikes);
		}


		public String getUser() {
			return user.get();
		}


		public String getJab() {
			return jab.get();
		}

		public Button getLike() {
			Image heartImg = new Image("smallHeart.png");
		    ImageView heartView = new ImageView(heartImg);
		    like.setGraphic(heartView);
			return like;
		}

		public Integer getNumberLikes() {
			return numberLikes.get();
		}
}