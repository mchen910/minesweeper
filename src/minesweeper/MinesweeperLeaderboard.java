/*
    Name:       Matthew Chen
    Date:       4/04/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
 */


package minesweeper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;
import utils.JSONArray;
import utils.JSONObject;

public class MinesweeperLeaderboard extends Pane {

    private ArrayList<Pair<String, Integer>> entries0;
    private ArrayList<Pair<String, Integer>> entries1;
    private ArrayList<Pair<String, Integer>> entries2;
    
    private VBox layout;
    
    private int currentLevelDisplayed;
    
    private HashMap<String, Image> imgs;


    public MinesweeperLeaderboard() {
    	this.entries0 = new ArrayList<>();
    	this.entries1 = new ArrayList<>();
    	this.entries2 = new ArrayList<>();
    	
    	this.layout = new VBox();
    	
    	this.initImgs();
    	this.getChildren().add(layout);
    }

    
    public void initLeaderboard(String jsonLeaderboard, int displayedLevel) {
    	this.currentLevelDisplayed = displayedLevel;
    	JSONArray leaderboardArr = new JSONArray(jsonLeaderboard);

        // Parse the new json leaderboard
        for (int i = 0; i < leaderboardArr.size(); i++) {
        	JSONObject entry = (JSONObject) leaderboardArr.getJSONElement(i);
        	String username = entry.getJSONElement("name").getValue();
        	String level = entry.getJSONElement("level").getValue();
        	int score = Integer.parseInt(entry.getJSONElement("score").getValue());
        	
        	Pair<String, Integer> pair = new Pair<>(username, score);
        	
        	switch (level) {
        	case "0":
        		this.entries0.add(pair);
        		break;
        		
        	case "1":
        		this.entries1.add(pair);
        		break;
        		
        	case "2":
        		this.entries2.add(pair);
        		break;
        	}
        }
        
        
        ArrayList<Pair<String, Integer>> levelArr = displayedLevel == 0 ? 
        		this.entries0 : (displayedLevel == 1 ? 
        				this.entries1 : this.entries2);
        
        // limit to 8 because I only have numbers 1-8
        for (int i = 1; i <= Math.min(8, levelArr.size()); i++) {
        	Pair<String, Integer> entry = levelArr.get(i - 1);
        	Image placeImg = this.imgs.get("place_" + i + ".gif");
        	ImageView placeImgView = new ImageView(placeImg); 	
        	Label nameLbl = new Label(entry.getKey());
        	Label scoreLbl = new Label("" + entry.getValue());
        	
        	LeaderboardPane entryPane = new LeaderboardPane(placeImgView, nameLbl, scoreLbl);
        	entryPane.setPadding(new Insets(1, 1, 1, 1));
        	this.layout.getChildren().add(entryPane);
        }
        
    }
    

    public void updateLeaderboard(String jsonLeaderboard) {
        System.out.println(jsonLeaderboard);
    	ArrayList<Pair<String, Integer>> updatedEntries0 = new ArrayList<>();
        ArrayList<Pair<String, Integer>> updatedEntries1 = new ArrayList<>();
        ArrayList<Pair<String, Integer>> updatedEntries2 = new ArrayList<>();
        
        JSONArray leaderboardArr = new JSONArray(jsonLeaderboard);

        // Parse the new json leaderboard
        for (int i = 0; i < leaderboardArr.size(); i++) {
        	JSONObject entry = (JSONObject) leaderboardArr.getJSONElement(i);
        	String username = entry.getJSONElement("name").getValue();
        	String level = entry.getJSONElement("level").getValue();
        	int score = Integer.parseInt(entry.getJSONElement("score").getValue());
        	
        	Pair<String, Integer> pair = new Pair<>(username, score);
        	
        	switch (level) {
        	case "0":
        		updatedEntries0.add(pair);
        		break;
        		
        	case "1":
        		updatedEntries1.add(pair);
        		break;
        		
        	case "2":
        		updatedEntries2.add(pair);
        		break;
        	}
        }
        
        // find the index to shift by and animate
        int idx;
        switch (this.currentLevelDisplayed) {
        case 0:
            idx = this.findInsertIdx(updatedEntries0, this.entries0);
            this.entries1 = updatedEntries1;
            this.entries2 = updatedEntries2;
            
            System.out.println(idx);
            System.out.println(updatedEntries0);
            System.out.println(this.entries0);
            
            this.animate(this.entries0, updatedEntries0, idx);
            break;
            
        case 1:
            idx = this.findInsertIdx(updatedEntries1, this.entries1);
            this.entries0 = updatedEntries0;
            this.entries2 = updatedEntries2;
            
            this.animate(this.entries1, updatedEntries1, idx);
            break;
            
        case 2:
            idx = this.findInsertIdx(updatedEntries2, this.entries2);
            this.entries0 = updatedEntries0;
            this.entries1 = updatedEntries1;
            
            this.animate(this.entries2, updatedEntries2, idx);
            break;
        }
    }
    
    
    private void animate(ArrayList<Pair<String, Integer>> origL, ArrayList<Pair<String, Integer>> newL, int idx) {
        for (int i = idx; i < Math.min(8, origL.size()); i++) {
            LeaderboardPane lPane = (LeaderboardPane)this.layout.getChildren().get(i);
            lPane.rotate(newL.get(i).getKey(), newL.get(i).getValue());
        }
    }
    
    
    private void initImgs() {
    	this.imgs = new HashMap<>();
    	
    	File imgPath = new File("images/places/");
        File[] imgs = imgPath.listFiles();

        for (File f : imgs) {
            String name = f.getName();
            Image img = new Image("file:images/places/" + name);
            this.imgs.put(name, img);
        }
    }

    
    private int findInsertIdx(ArrayList<Pair<String, Integer>> origL, ArrayList<Pair<String, Integer>> newL) {
        System.out.println(origL);
        System.out.println(newL);
    	for (int i = 0; i < origL.size(); i++) {
    		if (newL.get(i).getValue() != origL.get(i).getValue() || !newL.get(i).getKey().equals(origL.get(i).getKey())) {
    		    System.out.println(i);
    			return i;
    		}
    	}
    	
    	return -1;
    }
    
    
    
    private class LeaderboardPane extends StackPane {
        
        private ImageView placeNum;
        private Label nameLbl;
        private Label scoreLbl;
        private Font font;
        private HBox layout;
        
        private RotateTransition rotator1;
        private RotateTransition rotator2;
        
    	
    	public LeaderboardPane(ImageView placeNum, Label name, Label score) {
    	    this.placeNum = placeNum;
    	    this.nameLbl = name;
    	    this.scoreLbl = score;
    		
    	    // Load the font in the fonts directory
    	    this.font = Font.loadFont("file:fonts/Emulogic-zrEw.ttf", 12);
    	    
    	    // Style the pane
    	    this.initLayout();
    	    
    	    // Create RotateTransition
    	    this.rotator1 = new RotateTransition(Duration.millis(1000), this);
    	    this.rotator1.setAxis(Rotate.X_AXIS);
    	    this.rotator1.setFromAngle(0);
    	    this.rotator1.setToAngle(90);
    	    this.rotator1.setInterpolator(Interpolator.LINEAR);
    	    
    	    this.rotator2 = new RotateTransition(Duration.millis(1000), this);
            this.rotator2.setAxis(Rotate.X_AXIS);
            this.rotator2.setFromAngle(-90);
            this.rotator2.setToAngle(0);
            this.rotator2.setInterpolator(Interpolator.LINEAR);
    	}
    	
    	
    	public void setNewLayout(String name, int score) {
    	    this.nameLbl.setText(name);
    	    this.scoreLbl.setText("" + score);
    	}
    	
    	
    	public void rotate(String newName, int newScore) {
    	    this.rotator1.play();
    	    this.setNewLayout(newName, newScore);
    	    this.rotator2.play();
    	}
    	
    	
    	private void initLayout() {
    	    this.layout = new HBox();

            this.placeNum.setFitWidth(25);
            this.placeNum.setFitHeight(25);
            
            StackPane imgRegion = new StackPane();
            imgRegion.getChildren().add(this.placeNum);
            StackPane.setAlignment(this.placeNum, Pos.CENTER);
            imgRegion.setStyle("-fx-border-color: gray; -fx-border-style: solid; -fx-border-width: 2");

            this.nameLbl.setTextAlignment(TextAlignment.CENTER);
            this.nameLbl.setFont(this.font);
            this.nameLbl.setMaxWidth(200);
            this.nameLbl.setPadding(new Insets(0, 10, 0, 10));

            this.scoreLbl.setTextAlignment(TextAlignment.RIGHT);
            this.scoreLbl.setFont(this.font);
            this.scoreLbl.setPadding(new Insets(0, 10, 0, 10));
            
            this.setBackground(new Background(new BackgroundFill(Color.grayRgb(225), null, null)));
            this.setStyle("-fx-border-color: gray; -fx-border-style: solid; -fx-border-radius: 5 5 5 5; -fx-background-radius: 5 5 5 5;");

            Region expandingRegion = new Region();
            
            this.layout.getChildren().addAll(imgRegion, this.nameLbl, expandingRegion, this.scoreLbl);
            HBox.setHgrow(expandingRegion, Priority.ALWAYS);
            this.layout.setAlignment(Pos.CENTER_LEFT);
            
            this.getChildren().add(this.layout);
    	}
    }
}
