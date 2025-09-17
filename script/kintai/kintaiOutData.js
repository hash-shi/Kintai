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
	$("#srhTxtTaishoNengetsuF").focus();
	
	// 既に背景色が設定されている場合は一旦削除
	$("#mainArea").removeClass('ins');
	// 背景色を設定
	$("#mainArea").addClass("ins");
	
	// 対象年月度に管理マスタの現在処理年月度を格納
	var defaultDate = $("#defaultTaishoDate").val();
	$("#srhTxtTaishoNengetsuF").val(defaultDate);
	$("#srhTxtTaishoNengetsuT").val(defaultDate);
}

//****************************************************************************
// 対象年月フォーカスアウト時のフォーマット編集処理
//
//
//
//
//****************************************************************************
function onChangeTaishoYM(targetId){
	let strReplacing = $("#" + targetId).val();
	let strReplaced = "";
	//全角半角変換
	strReplacing = strReplacing.replace(/[０-９]/g, function(s) {
		return String.fromCharCode(s.charCodeAt(0) - 0xFEE0);
	});
	strReplacing = strReplacing.replace("／","/");

	let checkIfNumber = /^[0-9]+$/;
	
	// 日付のフォーマットを変換する
	// 全て数字で6文字の場合、YYYYMMとする
	if(strReplacing.length == 6 && checkIfNumber.test(strReplacing)) {
		strReplaced += strReplacing.substring(0, 4);
		strReplaced += "/";
		strReplaced += strReplacing.substring(4, 6);
	}
	// 全て数字で5文字の場合、YYYYMとする
	else if(strReplacing.length == 5 && checkIfNumber.test(strReplacing)) {
		strReplaced += strReplacing.substring(0, 4);
		strReplaced += "/0";
		strReplaced += strReplacing.substring(4, 5);
	}
	// スラッシュ区切りの場合、YYYY/0Mとする
	else if(strReplacing.includes("/")) {
		let parts = strReplacing.split("/");
		let y = parts[0];
		let m = (parts[1] || "").padStart(2,"0");
		strReplaced = y + "/" + m;
	}
	// それ以外の場合、そのまま
	else {
		strReplaced = strReplacing;
	}

	$("#" + targetId).val(strReplaced);
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

	
	if (value == "01") {
		proc("kyuyokeisanData",{}, function(data, dataType){
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
			var contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
			var result   = contents["result"];

			if(result){
				onDownloadPost("csvKyuyokeisanData");
			} else {
				if(contents["message"] == undefined){ return; }
				alert(contents["message"]);
			}	
		});
	} else {
		proc("chinginkeisanshoData",{}, function(data, dataType){
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
			var contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
			var result   = contents["result"];

			if(result){
				onDownloadPost("csvChinginkeisanshoData");
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
	var display = $("#mainArea").css("visibility");
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "visible") {
		// 該当の処理を呼び出す。
		onCsvDownload();
	}
}