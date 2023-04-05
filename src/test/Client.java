package test;

import client.MinesweeperClient;


public class Client {
	
	public static void main(String[] args) {
		MinesweeperClient client = new MinesweeperClient();
		client.startConnection("192.168.1.201", 5555);
		System.out.println("connection started");
		client.login("mchen354", "password");
//		
		System.out.println(client.getStatus());
		
		System.out.println(client.getInfo(0));
		System.out.println(client.getInfo(1));
		System.out.println(client.getInfo(2));
		
		client.logout();
		System.out.println(client.getStatus());
	}
	
}
