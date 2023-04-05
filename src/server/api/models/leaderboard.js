/**
 * Database Schema for the leaderboard
 * @author Matthew Chen
 * @version 1.0.0
 */

const mongoose = require('mongoose');

var Schema = mongoose.Schema;

const LeaderboardSchema = new Schema(
    {
        name: String,
        score: Number,
        level: Number
    },
    {
        timestamps: true,
        versionKey: false
    }
);


module.exports = mongoose.model('Leaderboard', LeaderboardSchema);