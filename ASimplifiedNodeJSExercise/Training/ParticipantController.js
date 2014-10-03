var mongoose = require("mongoose");
var Participant = mongoose.model("Participant");

exports.update = function(req,res){
	var token = req.params["token"];
	var params = {token:token};
	Participant.findOne(params,function(err,record){
		if(err)
			res.send("Error finding participant with token: " + token);
		else{
				if(record == null)
		   	  		return res.send("Token " + token + " is invalid");
		   	  	else{
		   	  		record.appaddress = req.params["address"];
		   	  		record.save(function(err){
		   	  			if(err)
		   	  				return res.send("Error updating application address");
		   	  			else
		   	  				return res.send("Application updated successfully for " + token);
		   	  		});

				}
		}		
	});
};