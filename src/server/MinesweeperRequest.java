/*
    Name:       Matthew Chen
    Date:       4/01/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
*/


package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import utils.Dotenv;
import utils.JSONElement;


/**
 * Utility class to send requests through. Used in MinesweeperServer's private inner 
 * class MinesweeperClientHandler, which has a MinesweeperRequest object to use.
 *
 * @author Matthew Chen
 */
public class MinesweeperRequest {

	/** Dotenv object to read the .env file */
    private Dotenv dotenv;

    /** The assumption is that the server will be hosted on the same computer as the API (for now) */
    private String URI;

    /** Routes for convenience */
    private final String leaderboardURL = "/leaderboard";
    private final String playerCreate = "/player/create";
    private final String playerLogin = "/player/login";
    private final String playerLogout = "/player/logout";

    /** Integers that represent levels to use in request queries */
    public final static int BEGINNER = 0;
    public final static int INTERMEDIATE = 1;
    public final static int EXPERT = 2;

    public int connectTimeout = 5000;
    public int readTimeout = 5000;


    public MinesweeperRequest() {
    	try {
			this.dotenv = new Dotenv(".env");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
    	
    	int apiPort = Integer.parseInt(this.dotenv.get("API_PORT"));
        this.URI = "http://localhost:" + apiPort + "/api";
    }

    /**
     * Get the entire leaderboard. Sends a GET request to the API, and returns a JSON object. 
     * Use the JSONObject/JSONArray/JSONElement classes to parse through this object.
     * 
     * @return A json string that contains a list of leaderboard entries.
     */
    public String getLeaderboard(String token) {
        try {
            URL url = new URL(this.URI + this.leaderboardURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(this.connectTimeout);
            conn.setReadTimeout(this.readTimeout);
 
            String bearerAuth = "Bearer " + token;
            conn.setRequestProperty("Authorization", bearerAuth);
            conn.setRequestProperty("Content-Type", "application/json");

            return this.readResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get the leaderboard entry for a particular user. Sends a GET request to the API, 
     * and returns a JSON object. Use the JSONObject/JSONArray/JSONElement classes to parse 
     * through this object.
     * 
     * @return A json object string with the _id, username, score, level, and created/updated 
     * timestamps, null if failed.
     */
    public String getLeaderboardEntry(String username, int level, String token) {
    	try {
    		URL url = new URL(this.URI + this.leaderboardURL + "/" + username + "?level=" + level);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(this.connectTimeout);
            conn.setReadTimeout(this.readTimeout);
            
            String bearerAuth = "Bearer " + token;
            conn.setRequestProperty("Authorization", bearerAuth);
            conn.setRequestProperty("Content-Type", "application/json");

            return this.readResponse(conn);

    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    
    /**
     * Update an existing leaderboard entry.
     * @param username Client username
     * @param score New client score
     * @param level Client level (reference MinesweeperRequest.BEGINNER, etc.)
     * @param token JSON web token for authentication
     * @return A json object string with the username, new score, and level, null if failed.
     */
    public String updateLeaderboardEntry(String username, int score, int level, String token) {
    	try {
    		URL url = new URL(this.URI + this.leaderboardURL + "/" + username);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(this.connectTimeout);
            conn.setReadTimeout(this.readTimeout);
            
            String bearerAuth = "Bearer " + token;
            conn.setRequestProperty("Authorization", bearerAuth);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            conn.setDoOutput(true);
            String reqBody = "{\"score\":" + score + ",\"level\":" + level + "}";
            conn.getOutputStream().write(reqBody.getBytes());
            conn.getOutputStream().flush();
            
            return this.readResponse(conn);

    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    
    /**
     * Create a new leaderboard entry for a different level. The routes for the API 
     * aren't different, but this method exists to make the distinction clear. 
     * @param username Client username
     * @param score New client score
     * @param level New client level
     * @param token JSON web token for authentication
     * @return A json object string with the username, new score, and new level, null if failed.
     */
    public String createLeaderboardEntry(String username, int score, int level, String token) {
    	// Creating and updating a leaderboard entry in the API use the same route.
    	// If the level is different then existing entries in the database (which is what this method 
    	// is used for), then the API will create a new entry with a different level value. However 
    	// the parameters or req.body values don't change.
    	
    	return this.updateLeaderboardEntry(username, score, level, token);
    }
    
    
    /**
     * Create a new user account.
     * @param username Account username
     * @param password Account password
     * @return A json object string with username and password, null if user already exists.
     */
    public String createNewUser(String username, String password) {
    	try {
    		URL url = new URL(this.URI + this.playerCreate);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(this.connectTimeout);
            conn.setReadTimeout(this.readTimeout);
            
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            conn.setDoOutput(true);
            String reqBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            conn.getOutputStream().write(reqBody.getBytes());
            conn.getOutputStream().flush();
            
            return this.readResponse(conn);

    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    
    /**
     * Log a user in to the system.
     * @param username User username
     * @param password User password
     * @return JSONObject with a JSON web token that is needed for leaderboard GET/POST requests.
     */
    public String loginUser(String username, String password) {
    	try {
    		URL url = new URL(this.URI + this.playerLogin);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(this.connectTimeout);
            conn.setReadTimeout(this.readTimeout);
            
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            conn.setDoOutput(true);
            String reqBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            conn.getOutputStream().write(reqBody.getBytes());
            conn.getOutputStream().flush();
            
            return this.readResponse(conn);

    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    
    /**
     * Log a user out of the system.
     * @param username User username
     * @param token JSON web token
     * @return A json object string with the username, null if failed.
     */
    public String logoutUser(String username, String token) {
    	try {
    		URL url = new URL(this.URI + this.playerLogout);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(this.connectTimeout);
            conn.setReadTimeout(this.readTimeout);
            
            String bearerAuth = "Bearer " + token;
            conn.setRequestProperty("Authorization", bearerAuth);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            conn.setDoOutput(true);
            String reqBody = "{\"username\":\"" + username + "\"}";
            conn.getOutputStream().write(reqBody.getBytes());
            conn.getOutputStream().flush();
            
            return this.readResponse(conn);

    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }


    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }


    public void setReadTimeout(int timeout) {
        this.readTimeout = timeout;
    }
    
    
    private String readResponse(HttpURLConnection conn) throws IOException {
    	int status = conn.getResponseCode();
        boolean isErr = true;
        
        InputStream stream = conn.getErrorStream();
        if (stream == null) {
        	stream = conn.getInputStream();
        	isErr = false;
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
            content.append(inputLine);
        
        in.close();
        
        if (isErr) {
        	content = content.insert(content.length() - 2, ",'code':" + status);
        }

        return JSONElement.cleanup(content.toString());
    }
    
}