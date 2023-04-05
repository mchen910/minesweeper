/**
* Database Schema for the players (usernames, passwords)
* @author Matthew Chen
* @version 1.0.0
*/

const mongoose = require('mongoose');

var Schema = mongoose.Schema;

const PlayerSchema = new Schema(
    {
        username: {
            type: String,
            required: [true, 'Username is required'],

        },
        password: {
            type: String,
            required: [true, 'Password is required'],
            validate: {
                validator: (value) => { return value.length >= 6 },
                message: () => 'password must be at least 6 characters long'
            }
        },
        apiKey: {
            type: String,
        }
    },
    {
        timestamps: false,
        versionKey: false
    }
);


module.exports = mongoose.model('Player', PlayerSchema);