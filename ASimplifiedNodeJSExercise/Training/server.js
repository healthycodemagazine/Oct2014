var mongoose = require("mongoose");
var mongoUri = "mongodb://localhost/Training";
mongoose.connect(mongoUri);
console.log("Connected to MongoDB");
require("./Participant");

var express = require("express");
var app = express();
require("./routes")(app);
var port = 5000;
app.listen(port);

console.log("Connected to port 5000. Waiting for requests...");