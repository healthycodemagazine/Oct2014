var mongoose = require("mongoose");
var Schema = mongoose.Schema;
var ParticipantSchema = new Schema({
	"name" : String,
	"token" : String,
	"email"  : String,
	"appaddress"	 : String
});
mongoose.model("Participant",ParticipantSchema);