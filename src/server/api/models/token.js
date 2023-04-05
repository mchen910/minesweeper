/**
 * API key Schema to implement a blacklist for logging out
 * @author Matthew Chen
 * @version 1.0.0
 */

const mongoose = require('mongoose');

var Schema = mongoose.Schema;

const TokenSchema = new Schema(
    {
        key: String,
    },
    {
        timestamps: true,
        versionKey: false
    }
);


TokenSchema.index({ createdOn: 1 }, { expireAfterSeconds: 86400 });

module.exports = mongoose.model('Token', TokenSchema);