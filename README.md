# Minesweeper #
This game uses JavaFX and NodeJS to create a game of minesweeper that can be played in two ways: either by yourself as a standalone game, or against others by creating an account and logging in to save your score on a leaderboard, which updates dynamically.

## ⚠️ NOTE: this project is not complete yet ⚠️ ##
This project is modularized so that the users only need the `client`, `utils`, and `minesweeper` packages in the `src` directory, while the server only needs the `server` and `utils` packages.

To play the standalone game, run `MinesweeperGUI.java` in `src/minesweeper` and click the option to play as a guest. This mode doesn't have a leaderboard, and functions just like a regular game of minesweeper.

To play a game with others, a server needs to be staretd. It's easiest to host the server on your own device, and have others (or yourself) connect to it from their own computers. Follow the steps below to set up dependencies and start a server.

## Dependencies ##
If you plan to play the standalone game, there are no other dependencies apart from JavaFX. Otherwise, to set this project up, open a command prompt or a terminal in the root directory of the project and run the following commands:
```bash
# If node isn't installed, install it from the official website or use a package manager

cd src/server/api

npm install
```

This installs the required packages (Express.js, mongoose, bcrypt, dotenv, jsonwebtoken, express-validator) on to your computer.

## Environment Variables ##
Then, create an environment file in the project's root directory named `.env` (same level as `src` or `images`), and follow the format below:
```
URI=<mongodb_uri>
API_PORT=<port1>
SERVER_PORT=<port2>
API_KEY=<hex_string>
NODE_PATH=<path_to_node>
```

For the `URI` field, if you don't feel like signing up for a mongodb account, setting up a cluster, etc., use my URI: `mongodb+srv://mchen354:minesweeper@minesweeper.zaqw0r2.mongodb.net/?retryWrites=true&w=majority`

Choose ports for the API and server (I chose 3000 and 5555, respectively, but any port should work as long as its not in use).

For the `API_KEY` field, use any 128-bit base 16 string. The API key is used to hash passwords and generate web tokens. 

Finally, for the `NODE_PATH` field, put the absolute path to your `node` installation (find it by running `which node` on MacOS/Linux or `file node` on Windows).

## Starting the Server ##
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

## Connecting to the Server ##
After running `MinesweeperGUI.java` to create the game window, the user is presented with three options: login, create account, or play as guest. 

Before logging in or creating an account, the user must input the IP address and the port that the server is hosted on to connect. This sends a confirmation message, and if the correct response isn't sent within 5 seconds, then the connection is severed and an error is presented to the user.

Once the user logs in, the game functions as normal but with a leaderboard that dynamically reloads. High scores are directly saved to the database, and will show up on any client's leaderboard that is currently connected.

At the end, the user can logout or directly exit the game, which will log the user out as well. 


## Project Specifics ##
The structure of the project is as follows: a user starts a server which hosts an API on a separate port. The server uses the API to communicate with the database. Then, clients connect to the server and communicate through Java Sockets. Every time a user updates the leaderboard, a new copy is sent to every user to display. 

### Server Specifics ###
I originally started this project because I had some experience setting up APIs and databases with NodeJS, so a project like this sounded interesting and challenging. I began by writing the API and writing code for the models, controllers, and routes that were to be used by the API.

| Routes | HTTP Method | Functionality |
|--------|-------------|---------------|
| `/leaderboard` | GET| Receive the entire leaderboard. |
| `/leaderboard/:id` | GET | Receive the data for a certain player. |
| `/leaderboard/:id` | POST | Update a certain player's score. |
| `/player/create` | POST | Create a new account for a player. |
| `/player/login` | POST | Log a player into the game. |
| `/player/logout` | POST | Log a player out of the game. |

Every route above except for the `/player/create` and `/player/login` route requires a JSON web token to authenticate the user before providing any sort of data (otherwise it returns a 401 Unauthorized error). Logging in to the system provides the user with a JSON web token which is then used in the `MinesweeperClient` class to make requests, etc.. 

Creating a player given a username and password stores the username and a hashed password in the database, which is then used to authenticate the user during login.

The web token is passed in the header of the request, and other fields like the username are passed in the body of the request.

I then wrote a JSON parser from scratch (since Java has no builtin JSON parser for Java1.8) to parse through the responses, as well as a Dotenv class to read .env files.

Next, the `MinesweeperRequest` class served as a way to abstract the process of using Java's `HttpURLConnection` objects to make requests. Instead, I wrote a couple of methods that took in the required information (username, token, etc.) and returned the response as a string.

Finally, the `MinesweeperServer` class keeps track of all the socket connections, and uses the inner classes `MinesweeperClientHandler` and `MinesweeperBroadcastHandler` to send and receive data. The client communicates through sending strings in the format [u|l]\[0|1|2]:[username/password/token/level/score], which is then interpreted by the server class and used to send the right requests through the `MinesweeperRequest` class.

### Client Specifics ###
Besides the additions to the GUI in the `MinesweeperGUI` class, three new classes were added. 

The first is the `MinesweeperClient` class, which keeps track of the user's username, password, and web token, as well as a LeaderboardClient object responsible for dynamically updating the leaderboard. It's also responsible for starting the connection, and has methods used in the GUI class to send the formatted strings to the server to be interpreted, as well as code to extract data from the responses.

Next is the `LeaderboardClient` class, which opens another socket connection that can only listen to the server. The server class differentiates between two socket connections opened by the same client by adding the second one (the LeaderboardClient) to a separate ArrayList, which is then iterated over when broadcasting the leaderboard. One problem is that this LeaderboardClient needs to be opened in a new thread, which I can't stop at the moment, but as long as the user exits the game, it shouldn't be a problem.

Finally, I created a `MinesweeperLeaderboard` class which extends Pane and animates when the leaderboard updates.

