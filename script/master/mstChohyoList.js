//****************************************************************************
// setShoriSentaku
//
//
//
//
//****************************************************************************
function setShoriSentaku(){
	
	// 選択した処理内容を格納
	// 値を取得する方法
	var value = $("#selShoriSentaku").val();
	// テキスト内容を取得する方法
	var name = $("#selShoriSentaku option:selected").text();
	
	// 処理選択に格納する。
	$("#txtShoriSentaku").val(name);
	
	// mainAreaを表示する。
	$("#mainArea").css("visibility", "visible");
	$("#buttonArea").css("visibility", "visible");
	
	// 既に背景色が設定されている場合は一旦削除
	$("#mainArea").removeClass('ins');
	// 背景色を設定
	$("#mainArea").addClass("ins");
	
	// 処理選択によって表示する内容を変える。
	if (value == "01") {
		// 営業所
		$("#eigyosho").css("display", "table-row");
		$("#busho").css("display", "none");
		$("#shain").css("display", "none");
		$("#kubun").css("display", "none");
		$("#saishuKoshin").css("display", "table-row");
	}
	else if (value == "02") {
		// 部署
		$("#eigyosho").css("display", "table-row");
		$("#busho").css("display", "table-row");
		$("#shain").css("display", "none");
		$("#kubun").css("display", "none");
		$("#saishuKoshin").css("display", "table-row");
	}
	else if (value == "03") {
		// 社員
		$("#eigyosho").css("display", "table-row");
		$("#busho").css("display", "none");
		$("#shain").css("display", "table-row");
		$("#kubun").css("display", "none");
		$("#saishuKoshin").css("display", "table-row");
	}
	else if (value == "04") {
		// 区分
		$("#eigyosho").css("display", "none");
		$("#busho").css("display", "none");
		$("#shain").css("display", "none");
		$("#kubun").css("display", "table-row");
		$("#saishuKoshin").css("display", "table-row");
	} else {
		$("#eigyosho").css("display", "none");
		$("#busho").css("display", "none");
		$("#shain").css("display", "none");
		$("#kubun").css("display", "none");
		$("#saishuKoshin").css("display", "none");
	}

	// 営業所に初期値を設定
	let eigyoshoCodeF = $("#hidEigyoshoCodeF").val();
	let eigyoshoCodeT = $("#hidEigyoshoCodeT").val();
	
	// 最終更新日にシステム日付を格納する
	// 	システム日付が15日までの場合、その年月。
	//  システム日付が16日以降の場合、その翌月の年月
	var dateTime = new Date();
	var date = new Date();
	if (dateTime.getDate() <= 15) {
		date = new Date(dateTime.getFullYear(), dateTime.getMonth(), dateTime.getDate());
	} else {
		date = new Date(dateTime.getFullYear(), (dateTime.getMonth() + 1), dateTime.getDate());
	}
	
	// コードセット/名称取得
	// 営業所
	$("#srhTxtEigyoshoCodeF").val(eigyoshoCodeF);
	getEigyoshoName('srhTxtEigyoshoCodeF', 'srhTxtEigyoshoNameF');
	$("#srhTxtEigyoshoCodeT").val(eigyoshoCodeT);
	getEigyoshoName('srhTxtEigyoshoCodeT', 'srhTxtEigyoshoNameT');
	// 部署
	$("#srhTxtBushoCodeF").val("");
	getBushoName('srhTxtBushoCodeF', 'srhTxtBushoNameF');
	$("#srhTxtBushoCodeT").val("");
	getBushoName('srhTxtBushoCodeT', 'srhTxtBushoNameT');
	// 社員
	$("#srhTxtShainNoF").val("");
	getShainName('srhTxtShainNoF', 'srhTxtShainNameF');
	$("#srhTxtShainNoT").val("");
	getShainName('srhTxtShainNoT', 'srhTxtShainNameT');
	// 区分
	$("#srhTxtKbnCodeF").val("");
	$("#srhTxtKbnCodeT").val("");
	// 最終更新日
	$("#srhTxtSaishuKoshinDateF").val(formatDateYYYYMM(date, "/"));
	$("#srhTxtSaishuKoshinDateT").val(formatDateYYYYMM(date, "/"));
	
}

//****************************************************************************
// onDownload
//
//
//
//
//****************************************************************************
function output(){
	
	// 処理選択を取得
	var value = $("#selShoriSentaku").val();
	
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
	}
	else if (value == "02") {
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
	}
	else if (value == "03") {
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
	}
	else if (value == "04") {
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
	
	// displayBottonAreaの表示状態を取得
	var display = $("#buttonArea").css("visibility");
	
	// displayBottonAreaが非表示(初期表示時)はスキップする。
	if (display == "visible") {
		// 該当の処理を呼び出す。
		output();
	}
}