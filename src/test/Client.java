package test;

import java.net.ConnectException;

import client.MinesweeperClient;


public class Client {

    public static void main(String[] args) {
        MinesweeperClient client = new MinesweeperClient();
        try {
            client.startConnection("10.19.94.10", 5555);
            System.out.println("connection started");
            client.login("mchen354", "password");
            //			
            System.out.println(client.leaderboard());
            client.logout();
            client.stopConnection();

        } catch (ConnectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
