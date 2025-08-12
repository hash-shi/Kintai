function toggleVisibility(){
	proc("change",{}, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }

		var contents				= data["contents"];
		if (contents["result"] == undefined){ return; }
		if (contents["kbnName"] == undefined){ return; }
		if (contents["date"] == undefined){ return; }
		if (contents["eigyosho"] == undefined){ return; }
		var result			= contents["result"];
		var kbnName			= contents["kbnName"];
		var date  			= contents["date"];
		var eigyosho  			= contents["eigyosho"];
		let eigyoshoValue = eigyosho[0];
		let Saisho = eigyoshoValue["Saisho"];
		let Saidai = eigyoshoValue["Saidai"];
		let SaishoName = eigyoshoValue["SaishoName"];
		let SaidaiName = eigyoshoValue["SaidaiName"];
		
		document.getElementById("displayShoriArea").style.display = "";
		$("#numSrhShorisentaku").val(result);
		$("#txtShoriSentaku").val(kbnName);
		$("#txtSrhBushoCodeF").val("");
		$("#txtSrhBushoNameF").val("");
	    $("#txtSrhBushoCodeT").val("");
		$("#txtSrhBushoNameT").val("");
		$("#txtSrhShainNOF").val("");
		$("#txtSrhShainNameF").val("");
		$("#txtSrhShainNOT").val("");
		$("#txtSrhShainNameT").val("");
		$("#txtSrhKbnCodeF").val("");
	    $("#txtSrhKbnCodeT").val("");
		
		if(result == "01"||result == "02"||result == "03"){
			document.getElementById("displayKbnArea").style.display = 'none';
			document.getElementById("displayEigyoshoArea").style.display = "";
			$("#txtSrhEigyoshoCodeF").val(Saisho);
			$("#txtSrhEigyoshoNameF").val(SaishoName);
			$("#txtSrhEigyoshoCodeT").val(Saidai);
			$("#txtSrhEigyoshoNameT").val(SaidaiName);
		 if(result == "02"){
			document.getElementById("displayBushoArea").style.display = "";
			document.getElementById("displayShainArea").style.display = 'none';
		 } else if(result == "03") {
			document.getElementById("displayBushoArea").style.display = 'none';
			document.getElementById("displayShainArea").style.display = "";
		 } else {
			document.getElementById("displayBushoArea").style.display = 'none';
			document.getElementById("displayShainArea").style.display = 'none';
		 }
		} else {
			document.getElementById("displayEigyoshoArea").style.display = 'none';
			document.getElementById("displayKbnArea").style.display = "";
			document.getElementById("displayBushoArea").style.display = 'none';
			document.getElementById("displayShainArea").style.display = 'none';
			$("#txtSrhEigyoshoCodeF").val("");
			$("#txtSrhEigyoshoCodeT").val("");
		}
		document.getElementById("displayBottonArea").style.display = "";
		$("#txtSrhSaishuKoshinDateF").val(date);
		$("#txtSrhSaishuKoshinDateT").val(date);
		// mainAreaの背景色を変更
		if (!$("#displayShoriArea").hasClass("ins")) {
			$("#displayShoriArea").addClass("ins");
		}
	});
}

function output(){
	proc("check",{}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		var result			= contents["result"];
		console.log("データ:"+ result);
		
		if(result){
			console.log("成功");
			onDownloadPost("csvMstchohyolistData");
		} else {
			if(contents["message"] == undefined){ return; }
			console.log("失敗");
			alert(contents["message"]);
		}
	});
	
}