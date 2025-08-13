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
		
		document.getElementById("displayShoriArea").style.display = "";
		$("#txtShoriSentaku").val(kbnName);
		$("#numSrhShorisentaku").val(value);
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
			document.getElementById("displayKbnArea").style.display = 'none';
			document.getElementById("displayEigyoshoArea").style.display = "";
		 if(value == "02"){
			document.getElementById("displayBushoArea").style.display = "";
			document.getElementById("displayShainArea").style.display = 'none';
		 } else if(value == "03") {
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
		document.getElementById("displayBottonArea").style.display = "";
		$("#txtSrhSaishuKoshinDateF").val(formatDateYYYYMMDD(date, "/"));
		$("#txtSrhSaishuKoshinDateT").val(formatDateYYYYMMDD(date, "/"));
		// mainAreaの背景色を変更
		if (!$("#displayShoriArea").hasClass("ins")) {
			$("#displayShoriArea").addClass("ins");
		}
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