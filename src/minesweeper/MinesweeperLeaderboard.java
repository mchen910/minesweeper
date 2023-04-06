/*
    Name:       Matthew Chen
    Date:       4/04/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
*/


package minesweeper;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.util.Pair;
import utils.JSONArray;

public class MinesweeperLeaderboard extends Pane {
	
	private ArrayList<Pair<String, Integer>> entries;
	
	
	public MinesweeperLeaderboard() {
		this.getChildren();
	}
	
	
	public void updateLeaderboard(String jsonLeaderboard) {
		JSONArray leaderboardArr = new JSONArray(jsonLeaderboard);
		ArrayList<Pair<String, Integer>> newLeaderboard = new ArrayList<>();
		
		for (int i = 0; i < leaderboardArr.size(); i++) {
			
		}
	}
	
}
