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

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    	super();
    	
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
        
        for (int i = 1; i <= levelArr.size(); i++) {
        	Pair<String, Integer> entry = levelArr.get(i - 1);
        	Image placeImg = this.imgs.get("place_" + i + ".gif");
        	ImageView placeImgView = new ImageView(placeImg);
        	
        	placeImgView.setFitWidth(32);
        	placeImgView.setFitHeight(32);
        	
        	Label nameLbl = new Label(entry.getKey());
        	Label scoreLbl = new Label("" + entry.getValue());
        	
        	HBox innerLayout = new HBox();
        	innerLayout.getChildren().addAll(placeImgView, nameLbl, scoreLbl);
        	this.layout.getChildren().add(innerLayout);
        }
        
    }
    

    public void updateLeaderboard(String jsonLeaderboard) {
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
        
        // find the index to shift by

    }
    
    
    private void initImgs() {
    	this.imgs = new HashMap<>();
    	
    	File imgPath = new File("images/");
        File[] imgs = imgPath.listFiles();

        for (File f : imgs) {
            String name = f.getName();
            Image img = new Image("file:images/" + name);
            this.imgs.put(name, img);
        }
    	
    }

    
    private int findInsertIdx(ArrayList<Pair<String, Integer>> origL, ArrayList<Pair<String, Integer>> newL) {
    	for (int i = 0; i < origL.size(); i++) {
    		if (!newL.get(i).equals(origL.get(i)))
    			return i;
    	}
    	
    	return -1;
    }
    
    
    
    private class LeaderboardGroup extends Group {
    	
    	public LeaderboardGroup(ImageView placeNum, Label name, Label score) {
    		this.getChildren().addAll(placeNum, name, score);
    		
    		placeNum.setX(0);
    		placeNum.setY(0);
    		
    	}
    }
}
