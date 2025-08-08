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
	
	$("#txtTaishoNengetsuF").val(formatDateYYYYMM(date, "/"));
	$("#txtTaishoNengetsuT").val(formatDateYYYYMM(date, "/"));
	
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
		proc("download",{}, function(data, dataType){
			onDownloadPost("csvKyuyokeisanData");
		});
	}
	else if (value == "02") {
		proc("download",{}, function(data, dataType){
			onDownloadPost("csvChinginkeisanshoData");
		});	
	}
}