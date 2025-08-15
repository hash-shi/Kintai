function toggleVisibility(){
		// 選択した処理内容を格納
		// 値を取得する方法
		var value = $("#rdoShoriSentaku").val();
		// 対象年月にシステム日付を格納する
		var dateTime = new Date();
		var date = new Date();
		date = new Date(dateTime.getFullYear(), dateTime.getMonth(), dateTime.getDate());
		var kbnName = $("#rdoShoriSentaku option:selected").text();
		let Saisho = $("#hidSrhEigyoshoCodeF").val();
		let Saidai = $("#hidSrhEigyoshoCodeT").val();
		
		$("#displayShoriArea").css("visibility", "visible");
		$("#txtShoriSentaku").val(kbnName);
		$("#txtSrhEigyoshoCodeF").val(Saisho);
		$("#txtSrhEigyoshoCodeT").val(Saidai);
		getEigyoshoName('txtSrhEigyoshoCodeF', 'txtSrhEigyoshoNameF');
		getEigyoshoName('txtSrhEigyoshoCodeT', 'txtSrhEigyoshoNameT');
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
		
		if(value == "01"||value == "02"||value == "03"){
			$("#displayKbnArea").css("display", "none");
			$("#displayEigyoshoArea").css("display", "table-row");
			$("#txtSrhEigyoshoCodeF").focus();
		 if(value == "02"){
			$("#displayBushoArea").css("display", "table-row");
			$("#displayShainArea").css("display", "none");
		 } else if(value == "03") {
			$("#displayBushoArea").css("display", "none");
			$("#displayShainArea").css("display", "table-row");
		 } else {
			$("#displayBushoArea").css("display", "none");
			$("#displayShainArea").css("display", "none");
		 }
		} else {
			$("#displayEigyoshoArea").css("display", "none");
			$("#displayKbnArea").css("display", "table-row");
			$("#displayBushoArea").css("display", "none");
			$("#displayShainArea").css("display", "none");
			$("#txtSrhKbnCodeF").focus();
		}
		$("#txtSrhSaishuKoshinDateF").val(formatDateYYYYMMDD(date, "/"));
		$("#txtSrhSaishuKoshinDateT").val(formatDateYYYYMMDD(date, "/"));
		$("#displayBottonArea").css("visibility", "visible");
		// mainAreaの背景色を変更
		if (!$("#displayShoriArea").hasClass("ins")) {
			$("#displayShoriArea").addClass("ins");
		}
}

function output(){
	// 処理選択を取得
	var value = $("#rdoShoriSentaku").val();
	if (value == "01") {
	 proc("eigyosho",{}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		var result   = contents["result"];
		
		if(result){
			onDownloadPost("csvMstEigyosho");
		} else {
			if(contents["message"] == undefined){ return; }
			alert(contents["message"]);
		}
	 });
	}else if (value == "02") {
	 proc("busho",{}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		var result   = contents["result"];
				
		if(result){
			onDownloadPost("csvMstBusho");
		} else {
			if(contents["message"] == undefined){ return; }
			alert(contents["message"]);
		}
	 });
	} else if (value == "03") {
 	 proc("shain",{}, function(data){
 		if (data == undefined){ return; }
 		if (data["contents"] == undefined){ return; }
 		var contents		= data["contents"];
 		if (contents["result"] == undefined){ return; }
		var result   = contents["result"];
 				
 		if(result){
 			onDownloadPost("csvMstShain");
 		} else {
 			if(contents["message"] == undefined){ return; }
 			alert(contents["message"]);
 		}
 	 });
 	} else {
	proc("kbn",{}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		var result   = contents["result"];
		 				
		if(result){
		 	onDownloadPost("csvMstKubun");
		} else {
		    if(contents["message"] == undefined){ return; }
		 	alert(contents["message"]);
		}
	 });
	}
	
}

//****************************************************************************
// ファンクションキーF12
//
//
//
//
//****************************************************************************
function onKeyEventF12() {
	
	// mainAreaの表示状態を取得
	var display = $("#displayBottonArea").css("display");
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "block") {
		// 該当の処理を呼び出す。
		output();
	}
}