/*
    Name:       Matthew Chen
    Date:       3/28/2023
    Period:     1

    Is this lab fully working?  No
    If not, explain: 
    If resubmitting, explain: 
*/

package minesweeper;

import java.io.File;

import client.MinesweeperClient;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class MinesweeperGUI extends Application {
	
	private final int BEGINNER_ROWS = 10;
	private final int BEGINNER_COLS = 10;
	private final int BEGINNER_MINES = (int)(BEGINNER_ROWS * BEGINNER_COLS * 0.10);
	
	private final int INTERMEDIATE_ROWS = 20;
	private final int INTERMEDIATE_COLS = 20;
	private final int INTERMEDIATE_MINES = (int)(INTERMEDIATE_ROWS * INTERMEDIATE_COLS * 0.15);
	
	private final int EXPERT_ROWS = 25;
	private final int EXPERT_COLS = 25;
	private final int EXPERT_MINES = (int)(EXPERT_ROWS * EXPERT_COLS * 0.20);
	
	private int currentRows = BEGINNER_ROWS;
	private int currentCols = BEGINNER_COLS;
	private int currentMines = BEGINNER_MINES;
	
	private MinesweeperModel model;
	private MinesweeperPane view;
	private MinesweeperTimer timer;
	
	private MinesweeperClient client;
	
	private Stage webStage;
	private Stage aboutStage;

	private Label mineLbl;
	private Label timeLbl;
	
	private Button loginButton;
	private Button playAsGuestButton;
	private Button createAccountButton;
	
	private long time;
	private int flags;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Minesweeper");
		stage.getIcons().add(new Image("file:images/icon.png"));
		
		BorderPane root = new BorderPane();
		root.setPrefSize(350, 350);
		
		SplitPane split = new SplitPane();
		
		MenuBar menuBar = new MenuBar();
		Menu gameMenu = new Menu("Game");
		Menu optionsMenu = new Menu("Options");
		Menu helpMenu = new Menu("Help");
		
		MenuItem beginnerGame = new MenuItem("New Beginner Game");
		MenuItem intermediateGame = new MenuItem("New Intermediate Game");
		MenuItem expertGame = new MenuItem("New Expert Game");
		MenuItem customGame = new MenuItem("New Custom Game");
		MenuItem exitGame = new MenuItem("Exit");
		MenuItem numMinesOption = new MenuItem("Number of Mines");
		MenuItem gridSizeOption = new MenuItem("Grid Size");
		MenuItem howToPlayMenu = new MenuItem("Rules");
		MenuItem aboutMenu = new MenuItem("About");
		
		gameMenu.getItems().addAll(beginnerGame, intermediateGame, expertGame, customGame, exitGame);
		optionsMenu.getItems().addAll(numMinesOption, gridSizeOption);
		helpMenu.getItems().addAll(howToPlayMenu, aboutMenu);
		
		menuBar.getMenus().addAll(gameMenu, optionsMenu, helpMenu);
		root.setTop(menuBar);
		
		
		/* ========================= STAGES ========================= */
		WebView webview = new WebView();
		WebEngine engine = webview.getEngine();

		File temp = new File("html/howToPlay.html"); 
		String url = "file:" + temp.getAbsolutePath();
		engine.load(url);
		
		Scene webScene = new Scene(webview);
		webStage = new Stage();
		webStage.setScene(webScene);

		VBox dialogLayout = new VBox();
		dialogLayout.setAlignment(Pos.CENTER);
		dialogLayout.setPadding(new Insets(10, 80, 10, 80));
		
		Label gameLbl = new Label("Minesweeper v1.0");
		gameLbl.setTextAlignment(TextAlignment.CENTER);
		gameLbl.setPadding(new Insets(10, 0, 10, 0));
		
		Label nameLbl = new Label("Matthew Chen");
		nameLbl.setTextAlignment(TextAlignment.CENTER);
		nameLbl.setPadding(new Insets(10, 0, 10, 0));
		
		dialogLayout.getChildren().addAll(gameLbl, nameLbl);
		
		aboutStage = new Stage();
		Scene aboutScene = new Scene(dialogLayout);
		aboutStage.setScene(aboutScene);
		aboutStage.setTitle("About");
		
		
		
		/* ========================= MVC ========================= */
		view = new MinesweeperPane();
		model = new MinesweeperModel(BEGINNER_ROWS, BEGINNER_COLS, BEGINNER_MINES);
		view.setModel(model);
		
		view.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.SECONDARY) {
					int col = view.rowForYPos(e.getY());
					int row = view.colForXPos(e.getX());

					// Row out of bounds
					if (row >= model.getNumRows())
						row = model.getNumRows() - 1;
					if (row < 0) 
						row = 0;
					
					// Col out of bounds
					if (col >= model.getNumCols())
						col = model.getNumCols() - 1;
					else if (col < 0) 
						col = 0;
										
					if (e.getButton() == MouseButton.PRIMARY) {
						model.reveal(row, col);
						
					} else {
						model.setFlag(row, col);
						
						// Set mine number label
						if (model.isFlag(row, col)) 
							flags--;
						else
							flags++;
						
						mineLbl.setText("Mines Remaining\n" + flags);
					}
				}
			}
		});
		
		
		/* ======================= TIMER ========================= */
		timer = new MinesweeperTimer((long)1e9);
		timer.start();
		
		
		
		/* ======================= MENUS ========================= */
		beginnerGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model = new MinesweeperModel(BEGINNER_ROWS, BEGINNER_COLS, BEGINNER_MINES);
				view.setModel(model);
				
				currentRows = BEGINNER_ROWS;
				currentCols = BEGINNER_COLS;
				currentMines = BEGINNER_MINES;
				
				timer.stop();
				time = 0;
				timer.start();
				stage.sizeToScene();
				
				flags = model.getNumMines();
				mineLbl.setText("Mines Remaining\n" + flags);
			}
		});
		
		intermediateGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model = new MinesweeperModel(INTERMEDIATE_ROWS, INTERMEDIATE_COLS, INTERMEDIATE_MINES);
				view.setModel(model);
				view.setTileSize(15);
				
				currentRows = INTERMEDIATE_ROWS;
				currentCols = INTERMEDIATE_COLS;
				currentMines = INTERMEDIATE_MINES;
				
				timer.stop();
				time = 0;
				timer.start();
				stage.sizeToScene();
				
				flags = model.getNumMines();
				mineLbl.setText("Mines Remaining\n" + flags);
			}
		});
		
		expertGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model = new MinesweeperModel(EXPERT_ROWS, EXPERT_COLS, EXPERT_MINES);
				view.setModel(model);
				view.setTileSize(15);
				
				currentRows = EXPERT_ROWS;
				currentCols = EXPERT_COLS;
				currentMines = EXPERT_MINES;
				
				timer.stop();
				time = 0;
				timer.start();
				stage.sizeToScene();
				
				flags = model.getNumMines();
				mineLbl.setText("Mines Remaining\n" + flags);
			}
		});
		
		
		exitGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});
		
		numMinesOption.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog input = new TextInputDialog();
				input.setTitle("Number of Mines");
				input.setHeaderText("How many mines would you like?");  
				input.showAndWait();  
				
				String answer = input.getEditor().getText(); 
				int mines = Integer.parseInt(answer);
				
				if (mines > 0 && mines < currentRows * currentCols)
					currentMines = mines;
				
				model = new MinesweeperModel(currentRows, currentCols, currentMines);
				view.setModel(model);
			}
		});
		
		aboutMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {				
				aboutStage.show();
			}
		});
		
		howToPlayMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				webStage.show();
			}
		});


		
		/* ======================= GUI ========================= */
		VBox layout = new VBox();
		HBox upperLayout = new HBox();
		upperLayout.setPadding(new Insets(10));
		upperLayout.setAlignment(Pos.CENTER);
		layout.setAlignment(Pos.CENTER);
		
		flags = model.getNumMines();
		mineLbl = new Label();
		mineLbl.setText("Mines Remaining\n" + flags);
		mineLbl.setTextAlignment(TextAlignment.CENTER);
		mineLbl.setPadding(new Insets(10));
		
		
		time = 0;
		timeLbl = new Label();
		timeLbl.setText("Time Elapsed\n" + this.time);
		timeLbl.setTextAlignment(TextAlignment.CENTER);
		timeLbl.setPadding(new Insets(10));
		
		Image face = new Image("file:images/face_smile.gif");
		ImageView smileyFace = new ImageView(face);
		
		upperLayout.getChildren().addAll(mineLbl, smileyFace, timeLbl);
		layout.getChildren().addAll(upperLayout, view);
		layout.setAlignment(Pos.CENTER);
		
		split.getItems().add(layout);

		root.setCenter(split);
		layout.setPadding(new Insets(15));
		
		Scene gameScene = new Scene(root);
		//stage.setScene(gameScene);

		
		
		/* ======================= LOGIN ========================= */
//		ImageView titleImg = new ImageView(new Image("images/icon.png"));
//		titleImg.setFitWidth(100);
//		titleImg.setFitHeight(100);
		
		this.client = new MinesweeperClient();
		client.startConnection("127.0.0.1", 5555);
		
		VBox titleLayout = new VBox();
		this.loginButton = new Button("Login");
		this.createAccountButton = new Button("Create Account");
		this.playAsGuestButton = new Button("Play as Guest");
		
		titleLayout.getChildren().addAll(this.loginButton, this.createAccountButton, this.playAsGuestButton);
		
		
		/* ======================= LOGIN SCENE ========================= */
		VBox loginLayout = new VBox();
		HBox usernameLayout = new HBox();
		HBox passwordLayout = new HBox();
		
		Label usernameLabel = new Label("Username");
		Label passwordLabel = new Label("Password");
		TextField usernameField = new TextField();
		PasswordField passwordField = new PasswordField();
		
		usernameLayout.getChildren().addAll(usernameLabel, usernameField);
		passwordLayout.getChildren().addAll(passwordLabel, passwordField);
		
		loginLayout.getChildren().addAll(usernameLayout, passwordLayout);
		
		Button okBtn = new Button("OK");
		okBtn.setAlignment(Pos.BASELINE_RIGHT);
		loginLayout.getChildren().add(okBtn);
		
		Scene loginScene = new Scene(loginLayout);
		
		
		/* ======================= SIGNUP SCREEN ========================= */
		VBox signupLayout = new VBox();
		HBox usernameSignupLayout = new HBox();
		HBox passwordSignupLayout = new HBox();
		
		Label usernameSignupLabel = new Label("Username");
		Label passwordSignupLabel = new Label("Password");
		TextField usernameSignupField = new TextField();
		PasswordField passwordSignupField = new PasswordField();
		
		usernameSignupLayout.getChildren().addAll(usernameSignupLabel, usernameSignupField);
		passwordSignupLayout.getChildren().addAll(passwordSignupLabel, passwordSignupField);
		
		signupLayout.getChildren().addAll(usernameSignupLayout, passwordSignupLayout);
		
		Button signupBtn = new Button("Sign Up");
		okBtn.setAlignment(Pos.BASELINE_RIGHT);
		signupLayout.getChildren().add(signupBtn);
		
		Scene signupScene = new Scene(signupLayout);
		
		
		
		this.loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.setScene(loginScene);
			}
		});
		
		
		this.createAccountButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.setScene(signupScene);	
			}
			
		});
		
		
		this.playAsGuestButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.setScene(gameScene);
			}
		});
		
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String username = usernameField.getText();
				String password = passwordField.getText();
				client.login(username, password);
				
				if (client.getStatus() > 299)
					System.out.println("status: " + client.getStatus() + ", login failed");
				
				stage.setScene(gameScene);
			}
		});
		
		signupBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String username = usernameSignupField.getText();
				String password = passwordSignupField.getText();
				
				client.createUser(username, password);
				
				if (client.getStatus() > 299)
					System.out.println("status: " + client.getStatus() + ", signup failed");
				
				stage.setScene(gameScene);
			}
		});
		
		Scene titleScene = new Scene(titleLayout);
		stage.setScene(titleScene);
		stage.show();
		
		
		
		
		
		
		
		
		// click smiley face to reset
		
		
		
		stage.show();
	}

	
	private class MinesweeperTimer extends AnimationTimer {
		private long delay;
		private long previousTime = -1;
		
		public MinesweeperTimer(long delay) {
			this.delay = delay;
		}
		
		@Override
		public void handle(long now) {
			// "Instantiate" previousTime
			if (this.previousTime == -1)
				this.previousTime = now;
			
			if (now - this.previousTime >= this.delay) {
				if (model.isGameOver() || model.isGameWon()) {
					this.stop();
					System.out.println("Game over!");
					System.exit(0);
				}
					
				time++;
				timeLbl.setText("Time Elapsed\n" + time);
				this.previousTime = now;
			}
		}
	}
}
