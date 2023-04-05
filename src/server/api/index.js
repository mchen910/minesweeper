const express = require('express');
const mongoose = require('mongoose');
const createError = require('http-errors');
const bodyParser = require('body-parser');

const apiRouter = require('./routes/api');


/** Configure environment variables */
require('dotenv').config();

/** Create the app */
var app = express();

/** Get environment variables */
var mongoURI = process.env.URI;
var port = process.env.API_PORT || 3000;

/** Connect to the URI */
mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true });

/** Set listener functions */
var db = mongoose.connection;
mongoose.Promise = global.Promise;
db.on('connection', () => console.log("Successfully connected"));
db.on('error', () => console.error('MongoDB connection error'));

/** Use express.json middleware */
app.use(express.json());

/** API route */
app.use('/api', apiRouter);


/** catch 404 and forward to error handler */
app.use( (req, res, next) => next(createError(404)));

/** Error handler */
app.use((err, req, res, next) => {
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};
    res.status(err.status || 500);
    res.json({
        error: err,
    });
});


/** Listen on port 3000 */
app.listen(port, () => {
    console.log(`Connected to port ${port}`)
})


