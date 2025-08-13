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
	console.log(value);
	// テキスト内容を取得する方法
	var name = $("#selShoriSentaku option:selected").text();
	console.log(name);
	
	// 処理選択に格納する。
	$("#txtShoriSentaku").val(name);
	
	// mainAreaを表示する。
	$("#mainArea").css("display", "block");
	$("#buttonArea").css("display", "block");
	
	// mainAreaの背景色を変更
	if (!$("#mainArea").hasClass("ins")) {
		$("#mainArea").addClass("ins");
	}
	
	// 対象年月にシステム日付を格納する
	// 	システム日付が15日までの場合、その年月。
	//  システム日付が16日以降の場合、その翌月の年月
	var dateTime = new Date();
	var date = new Date();
	if (dateTime.getDate() <= 15) {
		date = new Date(dateTime.getFullYear(), dateTime.getMonth(), dateTime.getDate());
	} else {
		date = new Date(dateTime.getFullYear(), (dateTime.getMonth() + 1), dateTime.getDate());
	}
	
	$("#srhTxtTaishoNengetsuF").val(formatDateYYYYMM(date, "/"));
	$("#srhTxtTaishoNengetsuT").val(formatDateYYYYMM(date, "/"));
	
}

//****************************************************************************
// onDownload
//
//
//
//
//****************************************************************************
function onCsvDownload(){
	
	// 処理選択を取得
	var value = $("#selShoriSentaku").val();
	console.log(value);
	
	if (value == "01") {
		proc("kyuyokeisanData",{}, function(data, dataType){
			onDownloadPost("csvKyuyokeisanData");
		});
	}
	else if (value == "02") {
		proc("chinginkeisanshoData",{}, function(data, dataType){
			onDownloadPost("csvChinginkeisanshoData");
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
	var display = $("#mainArea").css("display");
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "block") {
		// 該当の処理を呼び出す。
		onCsvDownload();
	}
}