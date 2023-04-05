/*
    Name:       Matthew Chen
    Date:       3/30/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
*/


package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import utils.JSONObject;


public class MinesweeperClient {

    private Socket clientSocket;
    
    private PrintWriter out;
    private BufferedReader in;

    private String token;
    private String username;
    private String password;
    private String ip;
    
    private int statusCode;
    private int port;
    
    
    private Thread leaderboardClient;
    
    
    public MinesweeperClient() {
    	this.token = null;
    	this.username = null;
    	this.password = null;
    	this.leaderboardClient = null;
    	this.ip = null;
    	this.port = -1;
    }
    
    
    public boolean isConnected() {
    	return this.ip != null;
    }
    
    
    public String getIP() {
    	return this.ip;
    }
    
    
    public int getPort() {
    	return this.port;
    }


    public void startConnection(String ip, int port) throws ConnectException {
        try {
        	this.ip = ip;
        	this.port = port;
        	
            this.clientSocket = new Socket(ip, port);
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            
            // Send a login message and wait for confirmation
            this.out.println("minesweeper-login");
            String res = this.in.readLine();
            
            if (!res.equals("confirmed")) {
            	throw new ConnectException("Invalid server address");
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createUser(String username, String password) {
    	String res = null;
    	try {
    		out.println("u0:" + username + "-" + password);
    		res = in.readLine();
    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Check for errors
    	JSONObject obj = new JSONObject(res);
    	if (obj.getJSONElement("error") != null) {
    		this.statusCode = Integer.parseInt(((JSONObject)obj.getJSONElement("error")).getJSONElement("code").getValue());
    		return;
    	}
    	
    	this.statusCode = 201;
    }
    
    
    public void login(String username, String password) {
    	String res = null;
    	this.username = username;
    	this.password = password;
    	
    	try {
    		out.println("u1:" + this.username + "-" + this.password);
    		res = in.readLine();
    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Check for errors
    	JSONObject obj = new JSONObject(res);
    	if (obj.getJSONElement("error") != null) {
    		this.statusCode = Integer.parseInt(((JSONObject)obj.getJSONElement("error")).getJSONElement("code").getValue());
    		return;
    	}
    	
    	// Extract the token
    	this.token = obj.getJSONElement("token").getValue();
    	this.statusCode = 201;
    	
    	// Start the leaderboard client once the user logs in successfully
    	this.leaderboardClient = new Thread(new LeaderboardClient(this.ip, this.port));
    	leaderboardClient.start();
    }
    
    
    public void logout() {
    	String res = null;
    	
    	try {
    		out.println("u2:" + this.username + "-" + this.token);
    		res = in.readLine();
    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Check for errors
    	JSONObject obj = new JSONObject(res);
    	if (obj.getJSONElement("error") != null) {
    		this.statusCode = Integer.parseInt(((JSONObject)obj.getJSONElement("error")).getJSONElement("code").getValue());
    		return;
    	}
    	
    	// Extract the token
    	this.statusCode = 201;
    	
    	// Close the leaderboard client once the user logs out
    	this.leaderboardClient.interrupt();
    }
    
    
    public String leaderboard() {
    	String res = null;
    	try {
    		out.println("l0:" + this.token);
    		res = in.readLine();
    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Check for errors
    	JSONObject obj = new JSONObject(res);
    	if (obj.getJSONElement("error") != null) {
    		this.statusCode = Integer.parseInt(((JSONObject)obj.getJSONElement("error")).getJSONElement("code").getValue());
    	} else {
    		this.statusCode = 201;
    	}

    	return res;
    }
    
    
    public String getInfo(int level) {
    	String res = null;
    	
    	try {
    		out.println("l1:" + this.username + "-" + level + "-" + this.token);
    		res = in.readLine();
    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Check for errors
    	System.out.println(res);
    	JSONObject obj = new JSONObject(res);
    	if (obj.getJSONElement("error") != null) {
    		this.statusCode = Integer.parseInt(((JSONObject)obj.getJSONElement("error")).getJSONElement("code").getValue());
    		res = null;
    		
    	} else {
    		this.statusCode = 200;
    	}

    	return res;
    }
    
    
    public void updateInfo(int level, int score) {
    	String res = null;
    	
    	try {
    		out.println("l2:" + this.username + "-" + score + "-" + level + "-" + this.token);
    		res = in.readLine();
    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	// Check for errors
    	JSONObject obj = new JSONObject(res);
    	if (obj.getJSONElement("error") != null) {
    		this.statusCode = Integer.parseInt(((JSONObject)obj.getJSONElement("errors")).getJSONElement("code").getValue());
    		return;
    		
    	} else {
    		this.statusCode = 201;
    	}
    }

    
    public void stopConnection() {
        try {
            this.in.close();
            this.out.close();
            this.clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public int getStatus() {
    	return this.statusCode;
    }
}
