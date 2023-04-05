/**
 * Controller for player api
 * @author Matthew Chen
 * @version 1.0.0
 */

const bcrypt = require('bcrypt');
const createError = require('http-errors');
const jwt = require('jsonwebtoken');

const { body, header, validationResult } = require('express-validator');

const Player = require('../models/player');
const Token = require('../models/token');

// API Key
require('dotenv').config();
const API_KEY = process.env.API_KEY;


exports.create_user = [
    body('username')
        .exists()
        .withMessage('Username needed'),
    body('password')
        .exists()
        .withMessage('Password needed'),

    async (req, res, next) => {
        const errors = validationResult(req);
        if (!errors.isEmpty())
            return next(createError(422, errors.array()));
        
        const username = req.body.username;
        const password = req.body.password;

        let userExists = await Player.findOne({ username: username });
        if (userExists)
            return next(createError(401, 'user already exists' ));

        // Define salt rounds
        const saltRounds = 10;

        // Hash password
        bcrypt.hash(password, saltRounds, (err, hash) => {
            if (err) 
                return next(createError(500, 'internal server error'));
            
            let newPlayer = new Player({
                username: username,
                password: hash
            });

            newPlayer.save().then(() => {
                res.status(201).json({
                    username: username,
                    password: password
                });
            })
        })
    }
]


exports.login_user = [
    body('username')
        .exists()
        .withMessage('Username needed')
        .isString()
        .withMessage('Username should be a string'),
    body('password')
        .exists()
        .withMessage('Password needed')
        .isString()
        .withMessage('Password should be a string'),

    async (req, res, next) => {
        const errors = validationResult(req);
        if (!errors.isEmpty())
            return next(createError(422, errors.array()));

        const username = req.body.username;
        const password = req.body.password;
        
        await Player.findOne({ username: username }).exec().then((user, err) => {
            if (err)
                return next(createError(401, 'invalid credentials'));

            if (user === null)
                return next(createError(404, 'Username not found'));

            bcrypt.compare(password, user.password, (err, result) => {
                if (result) {
                    // Send the user a JSON web token to give them access to the leaderboard 
                    // API. When the user logs out, add the token to the blacklist which 
                    // deletes itself after a day
                    const token = jwt.sign({ password: password }, API_KEY, { expiresIn: '1d' });
    
                    /* Remove the token from the blacklist */
                    Token.findOneAndDelete({ key: token });
    
                    return res.status(200).json({ token: token });
                }
    
                return next(createError(401, 'Incorrect password'));
            })
        });
    }
]


exports.logout_user = [
    header('authorization')
        .exists({ checkFalsy: true })
        .withMessage('Missing Authorization Header')
        .bail()
        .contains('Bearer')
        .withMessage('Authorization Token is not Bearer'),
    body('username')
        .exists()
        .withMessage('Missing username')
        .isString()
        .withMessage('Username must be a string'),

    async (req, res, next) => {
        const token = req.headers.authorization.split(' ')[1];
        const username = req.body.username;

        Player.findOne({ username: username }).exec().then((user, err) => {
            if (err)
                return next(createError(404, 'User not found'));

            jwt.verify(token, API_KEY, (err, data) => {
                if (err)
                    return next(createError(401, 'Unauthorized'));

                // Delete the token if it exists
                Token.deleteMany({ key: token }).exec();

                // Add the token to the blacklist
                let key = new Token({ key: token });
                key.save().then().catch((err) => next(err));
        
                return res.status(201).json({
                    username: username
                });
            })
        });
    }
]