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
		let eigyoshoCode = eigyosho[0];
		let Saisho = eigyoshoCode["Saisho"];
		let Saidai = eigyoshoCode["Saidai"];
		
		document.getElementById("displayShoriArea").style.display = "";
		$("#txtShoriSentaku").val(kbnName);
		
		if(result == "01"||result == "02"||result == "03"){
			document.getElementById("displayKbnArea").style.display = 'none';
			document.getElementById("displayEigyoshoArea").style.display = "";
			$("#txtFromEigyoshoCode").val(Saisho);
			$("#txtToEigyoshoCode").val(Saidai);
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
		}
		document.getElementById("displayOutputArea").style.display = "";
		document.getElementById("displayBottonArea").style.display = "";
		$("#txtFromSaishuKoshinDate").val(date);
		$("#txtToSaishuKoshinDate").val(date);
		console.log(result);
	});
}