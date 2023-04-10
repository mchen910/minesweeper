/**
 * Controller for leaderboard API
 * @author Matthew Chen
 * @version 1.0.0
 */

const createError = require('http-errors');
const jwt = require('jsonwebtoken');

const { body, param, query, validationResult, header } = require('express-validator');

const Leaderboard = require('../models/leaderboard');

require('dotenv').config();
const API_KEY = process.env.API_KEY;


exports.leaderboard_list = [
    header('authorization')
        .exists({ checkFalsy: true })
        .withMessage('Missing Authorization Header')
        .bail()
        .contains('Bearer')
        .withMessage('Authorization Token is not Bearer'),

    async (req, res, next) => {
        const errors = validationResult(req);
        if (!errors.isEmpty()) 
            return next(createError(401, errors.array()));

        const token = req.headers.authorization.split(' ')[1];
        jwt.verify(token, API_KEY, (err, authorizedData) => {
            if (err) {
                return next(createError(401, 'Could not authorize request'));
            }

            // Sort the scores by level first and then by score
            Leaderboard.find().sort({ 'level': 1, 'score': 1 }).then((playerList) => {
                res.status(200).json(playerList);
            }).catch((err) => next(err));
        });
    }
]


exports.get_player = [
    header('authorization')
        .exists({ checkFalsy: true })
        .withMessage('Missing Authorization Header')
        .bail()
        .contains('Bearer')
        .withMessage('Authorization Token is not Bearer'),
    query('level')
        .optional()
        .isInt({min: 0, max: 2})
        .withMessage('Invalid level query value'),
    param('id')
        .exists()
        .withMessage('Missing name parameter')
        .isString()
        .withMessage('Name parameter must be a string'),

    async (req, res, next) => {
        const errors = validationResult(req);
        if (!errors.isEmpty()) 
            return next(createError(422, errors.array()));

        const token = req.headers.authorization.split(' ')[1];
        jwt.verify(token, API_KEY, (err, authorizedData) => {
            if (err) {
                return next(createError(401, 'Could not authorize request'));
            }

            const name = req.params.id;
            const level = req.query.level;

            let query = {name: name};
            if (level !== undefined) 
                query.level = level;

            Leaderboard.findOne(query).exec().then((player) => {
                if (!player)
                    return res.status(200).json({});
                    
                return res.status(200).json({
                    name: player.name, score: player.score, level: player.level
                });
            }).catch((err) => next(err));
        });
    }
]


exports.update_player = [
    header('authorization')
        .exists({ checkFalsy: true })
        .withMessage('Missing Authorization Header')
        .bail()
        .contains('Bearer')
        .withMessage('Authorization Token is not Bearer'),
    body('score')
        .exists()
        .withMessage('Missing score value')
        .isInt({ min: 0 })
        .withMessage('Invalid score value'),
    body('level')
        .exists()
        .withMessage('Missing level value')
        .isInt({ min: 0, max: 2 })
        .withMessage('Invalid level value'),
    param('id')
        .exists()
        .withMessage('Missing username')
        .isString()
        .withMessage('Username parameter must be a string'),

    async (req, res, next) => {
        const errors = validationResult(req);
        if (!errors.isEmpty())
            return next(createError(422, errors.array()));

        const token = req.headers.authorization.split(' ')[1];
        console.log(token);
        jwt.verify(token, API_KEY, (err, authorizedData) => {
            if (err) {
                return next(createError(401, 'Could not authorize request'));
            }

            const score = req.body.score;
            const level = req.body.level;
            const name = req.params.id;
    
            const leaderboardEntry = new Leaderboard({
                name: name,
                score: score,
                level: level
            });

            // Check if an entry with the level already exists, otherwise create a new entry
            Leaderboard.findOne({ name: name, level: level }).exec().then((entry) => {
                if (entry === null) {  
                    leaderboardEntry.save()
                        .then((entry) => res.status(201).json(
                            { name: name, level: level, score: score }))
                        .catch((err) => next(err));
                    return;
                }

                // Level exists, but if the score is greater than or equal to one that already exists, just return 
                // the original entry and a 202 Accepted response
                if (entry.score <= score)
                    return res.status(202).json({ name: name, level: level, score: entry.score });

            }).catch((err) => next(err));

            // Level exists, but the score is better, so update
            Leaderboard.updateOne({ name: name, level: level }, { score: score }, { new: true })
                .exec()
                .then((entry) => res.status(201).json({ name: entry.name, level: entry.level, score: entry.score }))
                .catch((err) => next(err));
        });
    }
]
