package test;

import client.MinesweeperClient;

public class Client2 {
	public static void main(String[] args) {
		MinesweeperClient client = new MinesweeperClient();
		client.startConnection("127.0.0.1", 5555);
		System.out.println("connection started");
		client.login("newUser", "password");
		
		System.out.println("logged in");
//		
//		System.out.println(client.getStatus());
//		
//		System.out.println(client.getToken());
//		
		client.updateInfo(1, 783);
		
		client.logout();
		System.out.println(client.getStatus());
	}
}
