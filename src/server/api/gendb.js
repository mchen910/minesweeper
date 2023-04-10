const mongoose = require('mongoose');

const Leaderboard = require('./models/leaderboard');
const Player = require('./models/player');

require('dotenv').config();

const mongoURI = process.env.URI;


// Connect to the database
mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true });
mongoose.Promise = global.Promise;

let db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error'));


const entries = 50;
var playerList = [];


function randomStr(length) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
    const charactersLength = characters.length;
    let counter = 0;
    while (counter < length) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
      counter += 1;
    }
    return result;
}


function randomNumber(length) {
    let result = '';
    const characters = '0123456789';
    const charactersLength = characters.length;
    let counter = 0;
    while (counter < length) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
      counter += 1;
    }
    return result;
}


function randomUsername() {
    return "user" + randomNumber(10);
}


function randomScore() {
    return Math.floor(Math.random() * 450 + 50);
}


function randomLevel() {
    return Math.floor(Math.random() * 3);
}


async function createLeaderboardEntry(name) {
    const obj = {
        name: name,
        score: randomScore(),
        level: randomLevel()
    };

    const entry = new Leaderboard(obj);
    console.log(entry);
    await entry.save();
}


async function createUserEntry() {
    const obj = {
        username: randomUsername(),
        password: 'password'
    };

    playerList.push(obj.username);

    const entry = new Player(obj);
    await entry.save();
}


async function genLeaderboard() {
    var list = [];
    
    for (let i = 0; i < entries; i++) {
        list.push(createLeaderboardEntry(playerList[i]));
    }

	await Promise.all(list);
}

async function genPlayers() {
    var list = [];
    
    for (let i = 0; i < entries; i++) {
        list.push(createUserEntry());
    }

	await Promise.all(list);
}


async function run () {
    //await Player.collection.drop();
    //await Leaderboard.collection.drop();
    await genPlayers();
    await genLeaderboard();
    console.log("Done");
    process.exit(0);
}

run();