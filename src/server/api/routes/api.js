/**
* Routes for the API
* @author Matthew Chen
* @version 1.0.0
*/

const express = require('express')
const router = express.Router();

const leaderboardController = require('../controllers/leaderboard');
const playerController = require('../controllers/player');


/** GET the entire leaderboard */
router.get('/leaderboard', leaderboardController.leaderboard_list);

/** GET a player's high score */
router.get('/leaderboard/:id', leaderboardController.get_player);

/** POST an update to a player's score */
router.post('/leaderboard/:id', leaderboardController.update_player);


/** POST a new user */
router.post('/player/create', playerController.create_user);

/** POST request to authenticate a user */
router.post('/player/login', playerController.login_user);

/** DELETE request for logging a player out */
router.post('/player/logout', playerController.logout_user)


module.exports = router;