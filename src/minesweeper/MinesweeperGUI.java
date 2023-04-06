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
import java.net.ConnectException;
import java.util.Optional;

import client.MinesweeperClient;
import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;


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
    private Button settingsButton;

    private long time;
    private int flags;
    private boolean animationsOn;
    private boolean soundsOn;


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

        view.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.SECONDARY) {
                int col = view.rowForYPos(e.getY());
                int row = view.colForXPos(e.getX());

                if (model.getNumRevealed() == 0) {
                    // first click, start timer
                    timer.start();

                    // make sure the user can't click on a mine on their first click
                    while (model.isMine(row, col)) {
                        model = new MinesweeperModel(currentRows, currentCols, currentMines);
                        view.setModel(model);
                    }
                }

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
        });


        /* ======================= TIMER ========================= */
        timer = new MinesweeperTimer((long)1e9);



        /* ======================= MENUS ========================= */
        beginnerGame.setOnAction(event -> {
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
        });


        intermediateGame.setOnAction(event -> {
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
        });

        expertGame.setOnAction(event -> {
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
        });


        exitGame.setOnAction(event -> System.exit(0));

        numMinesOption.setOnAction(event -> {
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
        });

        aboutMenu.setOnAction(event -> aboutStage.show());
        howToPlayMenu.setOnAction(event -> webStage.show());



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



        /* ======================= OPEN SCREEN ========================= */
        // Create the MinesweeperClient responsible for communicating with the server
        this.client = new MinesweeperClient();

        // Borderpane for the opening screen
        BorderPane openPane = new BorderPane();

        VBox titleLayout = new VBox();
        this.loginButton = new Button("Login");
        this.createAccountButton = new Button("Create Account");
        this.playAsGuestButton = new Button("Play as Guest");
        this.settingsButton = new Button("settings");

        titleLayout.getChildren().addAll(this.loginButton, this.createAccountButton, this.playAsGuestButton, this.settingsButton);
        openPane.setCenter(titleLayout);

        MenuBar openMenuBar = new MenuBar();


        /* ======================= SETTINGS =========================== */
        Dialog<Pair<Pair<String, String>, Pair<Boolean, Boolean>>> settingsDialog = new Dialog<>();
        settingsDialog.setTitle("Settings");
        settingsDialog.setHeaderText(null);

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(10);
        settingsGrid.setPadding(new Insets(20, 50, 10, 10));

        TextField ipField = new TextField();
        ipField.setPromptText("Server IP Address");
        TextField portField = new TextField();
        portField.setPromptText("Port Number");

        ToggleSwitch animationToggle = new ToggleSwitch();
        animationToggle.setSize(30, 15);
        ToggleSwitch soundToggle = new ToggleSwitch();
        soundToggle.setSize(30, 15);

        settingsGrid.add(new Label("Server IP"), 0, 0);
        settingsGrid.add(ipField, 1, 0);
        settingsGrid.add(new Label("Port Number"), 0, 1);
        settingsGrid.add(portField, 1, 1);
        settingsGrid.add(new Label("Animations"), 0, 2);
        settingsGrid.add(animationToggle, 1, 2);
        settingsGrid.add(new Label("Sounds"), 0, 3);
        settingsGrid.add(soundToggle, 1, 3);

        ButtonType applyBtnType = new ButtonType("Apply", ButtonData.APPLY);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        settingsDialog.getDialogPane().getButtonTypes().setAll(applyBtnType, cancelBtnType);

        Node applyBtn = settingsDialog.getDialogPane().lookupButton(applyBtnType);
        applyBtn.setDisable(true);

        ipField.textProperty().addListener((observable, oldValue, newValue) -> {
            String ipPattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
            applyBtn.setDisable(
                    !newValue.matches(ipPattern) || 
                    (!portField.getText().matches("\\d+") || 
                            portField.getText().length() == 0 || 
                            Integer.parseInt(portField.getText()) < 1 || 
                            Integer.parseInt(portField.getText()) > 65535)
                    );
        });

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            String ipPattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
            applyBtn.setDisable(
                    !ipField.getText().matches(ipPattern) || 
                    (!newValue.matches("\\d+") || 
                            newValue.length() == 0 || 
                            Integer.parseInt(newValue) < 1 || 
                            Integer.parseInt(newValue) > 65535)
                    );
        });

        settingsDialog.setResultConverter(dialogButton -> {
            Boolean animationsOn = animationToggle.switchedOnProperty().getValue();
            Boolean soundsOn = soundToggle.switchedOnProperty().getValue();

            if (dialogButton == applyBtnType) 
                return new Pair<Pair<String, String>, Pair<Boolean, Boolean>>(
                        new Pair<String, String>(ipField.getText(), portField.getText()),
                        new Pair<Boolean, Boolean>(animationsOn, soundsOn)
                        );
            return null;
        });

        settingsDialog.getDialogPane().setContent(settingsGrid);




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



        this.loginButton.setOnAction(event -> {
            if (!client.isConnected()) {
                String message = "Not connected to a server. Input the server's IP address and port number from the settings menu.";
                Alert a = new Alert(AlertType.WARNING, message, ButtonType.OK);
                a.setTitle("Missing Server Address");
                a.setHeaderText(null);
                a.showAndWait();

            } else {
                stage.setScene(loginScene);
            }
        });


        this.createAccountButton.setOnAction(event -> {
            if (!client.isConnected()) {
                String message = "Not connected to a server. Input the server's IP address and port number from the settings menu.";
                Alert a = new Alert(AlertType.WARNING, message, ButtonType.OK);
                a.setTitle("Missing Server Address");
                a.setHeaderText(null);
                a.showAndWait();

            } else {
                stage.setScene(signupScene);
            }
        });


        this.playAsGuestButton.setOnAction(event -> stage.setScene(gameScene));

        this.settingsButton.setOnAction(event -> {
            Optional<Pair<Pair<String, String>, Pair<Boolean, Boolean>>> result = settingsDialog.showAndWait();
            result.ifPresent(res -> {
                String ip = res.getKey().getKey();
                int port = Integer.parseInt(res.getKey().getValue());

                try {
                    System.out.println(ip);
                    System.out.println(port);
                    this.client.startConnection(ip, port);

                } catch (ConnectException e) {
                    Alert a = new Alert(AlertType.ERROR, "Connection refused. Check the IP address or port number of the server.");
                    a.setTitle("Invalid Server Information");
                    a.setHeaderText(null);
                    a.showAndWait();
                    settingsDialog.showAndWait();
                }

                this.animationsOn = res.getValue().getKey();
                this.soundsOn = res.getValue().getValue();
            });
        });


        okBtn.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            client.login(username, password);

            if (client.getStatus() == 401) {
                Alert a = new Alert(AlertType.ERROR, "Incorrect password.", ButtonType.OK);
                a.setTitle("Incorrect Password");
                a.setHeaderText(null);
                a.showAndWait();

            } else if (client.getStatus() == 404) {
                Alert a = new Alert(AlertType.ERROR, "Username doesn't exist. Create a new user.", ButtonType.OK);
                a.setTitle("Incorrect Password");
                a.setHeaderText(null);
                a.showAndWait();

            } else {
                stage.setScene(gameScene);
            }
        });

        signupBtn.setOnAction(event -> {
            String username = usernameSignupField.getText();
            String password = passwordSignupField.getText();

            client.createUser(username, password);

            if (client.getStatus() == 401) {
                String message = "A user with the username " + username + " already exists. Please choose a different username.";
                Alert a = new Alert(AlertType.ERROR, message, ButtonType.OK);
                a.setTitle("Username Error");
                a.setHeaderText(null);
                a.showAndWait();
            }

            stage.setScene(gameScene);
        });

        Scene titleScene = new Scene(openPane);
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


    private class ToggleSwitch extends Parent {

        private BooleanProperty switchedOn;

        private TranslateTransition translateAnimation;
        private FillTransition fillAnimation;
        private ParallelTransition animation;

        private Rectangle background;
        private Circle trigger;

        private Color offColor = Color.WHITE;
        private Color onColor = Color.DEEPSKYBLUE;
        private Color strokeColor = Color.LIGHTGRAY;


        public ToggleSwitch() {
            this.switchedOn = new SimpleBooleanProperty(false);
            this.translateAnimation = new TranslateTransition(Duration.seconds(0.25));
            this.fillAnimation = new FillTransition(Duration.seconds(0.25));
            this.animation = new ParallelTransition(translateAnimation, fillAnimation);

            this.initComponents();
            this.initAnimations();

            this.setOnMouseClicked(event -> {
                switchedOn.set(!switchedOn.get());
            });

        }


        public void setSize(int width, int height) {
            this.background.setWidth(width);
            this.background.setHeight(height);
            this.background.setArcWidth(width / 2);
            this.background.setArcHeight(height);
            this.trigger.setRadius(height / 2);
            this.trigger.setCenterX(height / 2);
            this.trigger.setCenterY(height / 2);
        }


        public void setOffColor(Color color) {
            this.background.setFill(this.offColor);
            this.background.setStroke(this.strokeColor);
            this.trigger.setFill(this.offColor);
            this.trigger.setStroke(this.strokeColor);
        }


        public void setOnColor(Color color) {
            this.onColor = color;
        }


        private void initComponents() {
            this.background = new Rectangle(100, 50);
            this.background.setFill(this.offColor);
            this.background.setStroke(this.strokeColor);
            this.background.setArcWidth(50);
            this.background.setArcHeight(50);

            this.trigger = new Circle(25);
            this.trigger.setCenterX(25);
            this.trigger.setCenterY(25);
            this.trigger.setFill(this.offColor);
            this.trigger.setStroke(this.strokeColor);

            this.getChildren().addAll(this.background, this.trigger);
        }


        private void initAnimations() {
            translateAnimation.setNode(trigger);
            fillAnimation.setShape(background);

            this.switchedOn.addListener((observable, oldState, newState) -> {
                boolean isOn = newState.booleanValue();
                translateAnimation.setToX(isOn ? this.background.getWidth() - this.trigger.getRadius() * 2 : 0);
                fillAnimation.setFromValue(isOn ? this.offColor : this.onColor);
                fillAnimation.setToValue(isOn ? this.onColor : this.offColor);

                animation.play();
            });
        }


        public BooleanProperty switchedOnProperty() {
            return switchedOn;
        }

    }
}
