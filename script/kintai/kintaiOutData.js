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
	setTimeout(function(){
		$("#srhTxtTaishoNengetsuF").select();
		//20260813-全選択追加
	});

	
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