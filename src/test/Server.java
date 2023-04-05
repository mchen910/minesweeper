package test;

import server.MinesweeperServer;

public class Server {
	public static void main(String[] args) {
		MinesweeperServer server = MinesweeperServer.getInstance();
		server.start();
	}
}
