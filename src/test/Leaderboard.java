package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import minesweeper.MinesweeperLeaderboard;
import utils.JSONElement;

public class Leaderboard extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		MinesweeperLeaderboard leaderboard = new MinesweeperLeaderboard();
		
		String jsonStr = "[\n"
				+ "	{\n"
				+ "		\"_id\": \"642b88ed22f9733c3bb6be84\",\n"
				+ "		\"name\": \"mchen354\",\n"
				+ "		\"score\": 526,\n"
				+ "		\"level\": 0,\n"
				+ "		\"createdAt\": \"2023-04-04T02:18:21.173Z\",\n"
				+ "		\"updatedAt\": \"2023-04-04T02:20:56.613Z\"\n"
				+ "	},\n"
				+ "	{\n"
				+ "		\"_id\": \"642e4cb619c1e5b8984002f1\",\n"
				+ "		\"name\": \"mchen352\",\n"
				+ "		\"score\": 30,\n"
				+ "		\"level\": 1,\n"
				+ "		\"createdAt\": \"2023-04-06T04:38:14.788Z\",\n"
				+ "		\"updatedAt\": \"2023-04-06T04:43:14.140Z\"\n"
				+ "	},\n"
				+ "	{\n"
				+ "		\"_id\": \"642cc6a0b9b2ba0b769fd6c2\",\n"
				+ "		\"name\": \"newUser\",\n"
				+ "		\"score\": 783,\n"
				+ "		\"level\": 1,\n"
				+ "		\"createdAt\": \"2023-04-05T00:53:52.197Z\",\n"
				+ "		\"updatedAt\": \"2023-04-05T00:53:52.197Z\"\n"
				+ "	},\n"
				+ "	{\n"
				+ "		\"_id\": \"642c5e4ef6533b4bc133e28b\",\n"
				+ "		\"name\": \"newUser\",\n"
				+ "		\"score\": 783,\n"
				+ "		\"level\": 2,\n"
				+ "		\"createdAt\": \"2023-04-04T17:28:46.626Z\",\n"
				+ "		\"updatedAt\": \"2023-04-04T17:28:46.626Z\"\n"
				+ "	}\n"
				+ "]";
		
		String json = JSONElement.cleanup(jsonStr);
		
		leaderboard.initLeaderboard(json, 1);
		
		Scene scene = new Scene(leaderboard);
		primaryStage.setScene(scene);
		
		primaryStage.show();
		
	}
	
	
	
}
