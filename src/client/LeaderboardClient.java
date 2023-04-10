/*
    Name:       Matthew Chen
    Date:       4/04/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
 */


package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import minesweeper.MinesweeperLeaderboard;

public class LeaderboardClient extends Thread {

    private int port;
    private String ip;

    private BufferedReader in;
    private Socket socket;

    private MinesweeperLeaderboard leaderboard;
    
    private volatile boolean running = true;


    public LeaderboardClient(String serverIP, int serverPort) {
        this.ip = serverIP;
        this.port = serverPort;
        this.leaderboard = null;
    }


    public void connectLeaderboard(MinesweeperLeaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }


    private void startClient() {
        try {
            this.socket = new Socket(ip, port); 
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String inputLine;
        this.startClient();

        try {
            while (this.running) {
                inputLine = in.readLine();
                if (inputLine != null && this.leaderboard != null)
                    this.leaderboard.updateLeaderboard(inputLine);
            }
            
            System.out.println("closing leaderboard client socket");
            this.socket.close();
            System.out.println("leaderboard client socket closed: " + this.socket.isClosed());
            return;
 
        } catch (IOException e) {
            e.printStackTrace();
        
        }

    }
    
    
    public void close() {
        System.out.println("setting running to false");
        this.running = false;
        
        System.out.println("running: " + this.running);
        this.interrupt();
    }
}
