/*
    Name:       Matthew Chen
    Date:       3/30/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
 */

package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import utils.Dotenv;


public class MinesweeperServer {

    private static MinesweeperServer instance = null;

    private ServerSocket serverSocket;
    private Process apiProcess;
    private Dotenv dotenv;

    private static ArrayList<MinesweeperBroadcastHandler> connections;
    private ArrayList<String> ipConnections;


    private MinesweeperServer() {
        try {
            this.dotenv = new Dotenv(".env");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        MinesweeperServer.connections = new ArrayList<>();
        this.ipConnections = new ArrayList<>();
    }


    public static synchronized MinesweeperServer getInstance() {
        if (instance == null) 
            instance = new MinesweeperServer();
        return instance;
    }


    public void start() {
        System.out.println("server started!");
        try {
            // Start the API
            this.startAPI();

            // Start the server socket
            int port = Integer.parseInt(this.dotenv.get("SERVER_PORT"));
            this.serverSocket = new ServerSocket(port);

            while (true) {
                // Check if there is already a socket with the same IP address. If one already exists, the next 
                // will be passed to MinesweeperBroadcastHandler as opposed to MinesweeperClientHandler
                Socket socket = this.serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();

                if (this.ipConnections.contains(ip)) {
                    MinesweeperBroadcastHandler broadcastHandler = new MinesweeperBroadcastHandler(socket);
                    MinesweeperServer.connections.add(broadcastHandler);
                    broadcastHandler.start();

                } else {
                    MinesweeperClientHandler handler = new MinesweeperClientHandler(socket);
                    this.ipConnections.add(ip);
                    handler.start();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            stop();
        }
    }


    public void stop() {
        try {
            this.serverSocket.close();
            this.apiProcess.destroy();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void broadcastMessage(String res) {
        for (MinesweeperBroadcastHandler handler : MinesweeperServer.connections) {
            handler.out.println(res);
        }
    }


    private void startAPI() {
        try {
            ProcessBuilder pb = new ProcessBuilder(this.dotenv.get("NODE_PATH"), "src/server/api/index.js");
            apiProcess = pb.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class MinesweeperClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        /** MinesweeperRequest object for every client handler */
        private MinesweeperRequest req;

        public MinesweeperClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.req = new MinesweeperRequest();
        }

        public void run() {
            try {
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // check for login
                    if (inputLine.equals("minesweeper-login")) {
                        this.out.println("confirmed");
                        continue;
                    }

                    // Format of a string: [u|l][0|1|2]:[username/password/token/level/score]
                    String res = null;

                    if (inputLine.startsWith("u")) {
                        int hyphenIdx = inputLine.indexOf("-");
                        String username = inputLine.substring(3, hyphenIdx);
                        String passwordOrToken = inputLine.substring(hyphenIdx + 1);

                        // user operations
                        switch (inputLine.charAt(1)) {
                        // create
                        case '0':
                            res = this.req.createNewUser(username, passwordOrToken);
                            break;

                            // login
                        case '1':
                            res = this.req.loginUser(username, passwordOrToken);
                            break;

                            // logout
                        case '2':
                            res = this.req.logoutUser(username, passwordOrToken);
                            break;
                        }

                    } else if (inputLine.startsWith("l")) {
                        // leaderboard operations
                        switch (inputLine.charAt(1)) {
                        // leaderboard
                        case '0':
                            String token0 = inputLine.substring(3);
                            res = this.req.getLeaderboard(token0);
                            break;

                            // leaderboard entry
                        case '1':
                            int firstHyphenIdx1 = inputLine.indexOf('-');
                            int secondHyphenIdx1 = inputLine.indexOf('-', firstHyphenIdx1 + 1);

                            String username1 = inputLine.substring(3, firstHyphenIdx1);
                            int level1 = Integer.parseInt(inputLine.substring(firstHyphenIdx1 + 1, secondHyphenIdx1));
                            String token1 = inputLine.substring(secondHyphenIdx1 + 1);

                            res = this.req.getLeaderboardEntry(username1, level1, token1);
                            break;

                            // logout
                        case '2':
                            int firstHyphenIdx2 = inputLine.indexOf('-');
                            int secondHyphenIdx2 = inputLine.indexOf('-', firstHyphenIdx2 + 1);
                            int thirdHyphenIdx2 = inputLine.indexOf('-', secondHyphenIdx2 + 1);

                            String username2 = inputLine.substring(3, firstHyphenIdx2);
                            int score2 = Integer.parseInt(inputLine.substring(firstHyphenIdx2 + 1, secondHyphenIdx2));
                            int level2 = Integer.parseInt(inputLine.substring(secondHyphenIdx2 + 1, thirdHyphenIdx2));
                            String token2 = inputLine.substring(thirdHyphenIdx2 + 1);

                            res = this.req.createLeaderboardEntry(username2, score2, level2, token2);

                            // Broadcast the new leaderboard to all connected clients
                            String newLeaderboard = this.req.getLeaderboard(token2);
                            for (MinesweeperBroadcastHandler h : MinesweeperServer.connections) 
                                h.broadcast(newLeaderboard);

                            break;
                        }
                    }

                    out.println(res);
                }

                in.close();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private static class MinesweeperBroadcastHandler extends Thread {
        private Socket clientSocket;
        public PrintWriter out;

        public MinesweeperBroadcastHandler(Socket socket) {
            this.clientSocket = socket;

            try {
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void broadcast(String s) {
            System.out.println("broadcasted!");
            this.out.println(s);
        }
    }
}
