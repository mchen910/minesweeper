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

public class LeaderboardClient implements Runnable {

	private int port;
	private String ip;
	
	private BufferedReader in;
	private Socket socket;
	
    private MinesweeperLeaderboard leaderboard;
    
	
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
        startClient();
        
        String inputLine;

        try {
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.equals(".")) {
					this.socket.close();
					break;
				}
				
				System.out.println("broadcast received");
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    
	
}