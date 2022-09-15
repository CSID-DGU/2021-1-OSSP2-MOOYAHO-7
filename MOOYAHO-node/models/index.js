'use strict';

const fs = require('fs');
const path = require('path');
const Sequelize = require('sequelize');
const basename = path.basename(__filename);
const env = process.env.NODE_ENV || 'development';
const config = require(__dirname + '/../config/config.json')[env];
const Board = require('./board');
const Review = require('./review');
const db = {};

const sequelize = new Sequelize(config.database, config.username, config.password, config);

db.sequelize = sequelize;
db.Sequelize = Sequelize;

//db.User = User;
db.Board = Board;
db.Review = Review;

//User.init(sequelize);
Board.init(sequelize);
Review.init(sequelize);

module.exports = db;
