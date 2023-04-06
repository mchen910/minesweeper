# Minesweeper #
This game can be played in two ways: either by yourself as a standalone game, or against others by creating an account and logging in to save your score on a leaderboard.

## ⚠️ NOTE: this project is not complete yet ⚠️ ##
Currently running `src/minesweeper/MinesweeperGUI.java` will look like Lab 7.4, but I'm planning on resubmitting. Below is a description of my project idea and what I have accomplished so far.

<br>To play the standalone game, run `MinesweeperGUI.java` in `src/minesweeper` and don't click the login button. 

To play a game with others, someone needs to start a server. It's easiest to host the server on your own device, and have others connect to it from their own computers. Because the program needs to access a database in the cloud, I used Node.js with `mongoose` to write an API. To set this up, follow the steps below: 

### Dependencies ###
To set this project up, run the following commands:
```bash
# If node isn't installed, install it from the official website or use a package manager

cd src/server/api

npm install
```

This installs the required packages on to your computer.<br>​

### Environment Variables ###
Then, create an environment file in the project's root directory named `.env` (same level as `src` or `images`), and follow the format below:
```
URI=<mongodb_uri>
API_PORT=<port1>
SERVER_PORT=<port2>
API_KEY=<hex_string>
NODE_PATH=<path_to_node>
```

For the `URI` field, if you don't feel like signing up for a mongodb account, setting up a cluster, etc., use my URI: `mongodb+srv://mchen354:minesweeper@minesweeper.zaqw0r2.mongodb.net/?retryWrites=true&w=majority`

Choose ports for the API and server (I chose 3000 and 5555, respectively).

For the API_KEY field, use any 128-bit base 16 string.

Finally, for the NODE_PATH field, put the absolute path to your `node` installation.

### Starting the Server ###
Start the server by creating a file named `Server.java` in the src folder. To create the server, add the following:

```java
import server.MinesweeperServer;

public class Server {
    public static void main(String[] args) {
        MinesweeperServer server = MinesweeperServer.getInstance();
        server.start();
    }
}
```

### Connecting to the Server ###
The functionality has already been coded in, but there is currently no login page for the user. The underlying code for connecting to the server, authenticating the user, getting and posting data, etc. is all there but I didn't have enough time to finish the application portion.

When finished, the program will allow the user to enter in an IP address and port supplied by whoever is running the server (maybe a better way to do this, or an automatic one?) and show a dynamically updating leaderboard.

I'm turning it in now, but I'll resubmit later in the week when it is completed.
