package test;

import java.net.ConnectException;

import client.MinesweeperClient;


public class Client {

    public static void main(String[] args) {
        MinesweeperClient client = new MinesweeperClient();
        try {
            client.startConnection("192.168.1.182", 5555);
            System.out.println("connection started");
            client.login("mchen354", "password");


        } catch (ConnectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
