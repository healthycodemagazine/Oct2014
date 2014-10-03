var participantController = require("./ParticipantController");
module.exports = function(app){
	app.get("/training/:token/:address",function(req,res){
		participantController.update(req,res);
	});
};