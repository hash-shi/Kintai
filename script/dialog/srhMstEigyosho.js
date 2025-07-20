//****************************************************************************
// searchDialog
//
//
//
//
//****************************************************************************
function getMstEigyoshos(){

	proc("search", {}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result			= contents["result"];
		
		// 検索結果エリアをクリアする。
		$("#searchStaffResult").children().remove();
		
		for (var count = 0 ; count < result.length ; count++){
			var record			= result[count];
//			$("#searchStaffResult").append("<li><div class=\"name\">" + record["nmSyain"] + " " + "(" + record["kjTentanms"] + ")" + "</div><div class=\"button\"><button type=\"button\" onclick=\"$('#input_responsibleCdSyain').val('" + record["cdSyain"] + "');closeDialog();getStaffInformation();\">選択</button></div></li>");
		}
		
	});
	
}