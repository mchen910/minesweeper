package test;

import java.net.ConnectException;

import client.MinesweeperClient;


public class Client {

    public static void main(String[] args) {
        MinesweeperClient client = new MinesweeperClient();
        try {
            client.startConnection("192.168.1.201", 5555);
            System.out.println("connection started");
            client.login("mchen354", "password");
            //			
            System.out.println(client.leaderboard());
            client.logout();
            System.out.println("logged out");
            client.stopConnection();
            System.out.println("connection stopped");
            System.out.println(client.leaderboardClient.isAlive());
            client.leaderboardClient.interrupt();
            System.out.println(client.leaderboardClient.isAlive());

        } catch (ConnectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
