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
	$("#lblShoriSentaku").val(name);
	
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
		$("#srhTxtEigyoshoCodeF").focus();
	}
	else if (value == "02") {
		// 部署
		$("#eigyosho").css("display", "table-row");
		$("#busho").css("display", "table-row");
		$("#shain").css("display", "none");
		$("#kubun").css("display", "none");
		$("#saishuKoshin").css("display", "table-row");
		$("#srhTxtEigyoshoCodeF").focus();
	}
	else if (value == "03") {
		// 社員
		$("#eigyosho").css("display", "table-row");
		$("#busho").css("display", "none");
		$("#shain").css("display", "table-row");
		$("#kubun").css("display", "none");
		$("#saishuKoshin").css("display", "table-row");
		$("#srhTxtEigyoshoCodeF").focus();
	}
	else if (value == "04") {
		// 区分
		$("#eigyosho").css("display", "none");
		$("#busho").css("display", "none");
		$("#shain").css("display", "none");
		$("#kubun").css("display", "table-row");
		$("#saishuKoshin").css("display", "table-row");
		$("#srhTxtKbnCodeF").focus();
	} else {
		$("#eigyosho").css("display", "none");
		$("#busho").css("display", "none");
		$("#shain").css("display", "none");
		$("#kubun").css("display", "none");
		$("#saishuKoshin").css("display", "none");
	}

	// 営業所に初期値を設定
	let eigyoshoCodeF = $("#txtEigyoshoCodeF").val();
	let eigyoshoCodeT = $("#txtEigyoshoCodeT").val();
	
	// コードセット/名称取得
	// 営業所
	$("#srhTxtEigyoshoCodeF").val(eigyoshoCodeF);
	getEigyoshoName('srhTxtEigyoshoCodeF', 'lblEigyoshoNameF');
	$("#srhTxtEigyoshoCodeT").val(eigyoshoCodeT);
	getEigyoshoName('srhTxtEigyoshoCodeT', 'lblEigyoshoNameT');
	// 部署
	$("#srhTxtBushoCodeF").val("");
	getBushoName('srhTxtBushoCodeF', 'lblBushoNameF');
	$("#srhTxtBushoCodeT").val("");
	getBushoName('srhTxtBushoCodeT', 'lblBushoNameT');
	// 社員
	$("#srhTxtShainNoF").val("");
	getShainName('srhTxtShainNoF', 'lblShainNameF');
	$("#srhTxtShainNoT").val("");
	getShainName('srhTxtShainNoT', 'lblShainNameT');
	// 区分
	$("#srhTxtKbnCodeF").val("");
	$("#srhTxtKbnCodeT").val("");
	// 最終更新日
	var date = new Date();
	$("#srhTxtSaishuKoshinDateF").val(formatDateYYYYMMDD(date, "/"));
	$("#srhTxtSaishuKoshinDateT").val(formatDateYYYYMMDD(date, "/"));
	
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
// 退職年月日フォーカスアウト時のフォーマット編集処理
//
//
//
//
//****************************************************************************

function onChangeSaishuKoshinDate(targetId){
	let strReplacing = $("#" + targetId).val();
	let strReplaced = "";
	
	// 全角→半角
	strReplacing = strReplacing.replace(/[０-９]/g, function(s) {
		return String.fromCharCode(s.charCodeAt(0) - 0xFEE0);
	});
	// ／や/を統一
	strReplacing = strReplacing.replace(/[／\/]/g,"/");
	
	// --- 数字だけで入力された場合 ---
	let checkIfNumber = /^[0-9]+$/;
	if (checkIfNumber.test(strReplacing)) {
		if(strReplacing.length === 8) {
			// YYYYMMDD
			let y = strReplacing.substring(0, 4);
			let m = strReplacing.substring(4, 6);
			let d = strReplacing.substring(6, 8);
			strReplaced = y + "/" + m + "/" + d;
		} else if(strReplacing.length === 7) {
			// YYYYMDD → YYYY/0M/DD
			let y = strReplacing.substring(0, 4);
			let m = "0" + strReplacing.substring(4, 5);
			let d = strReplacing.substring(5, 7);
			strReplaced = y + "/" + m + "/" + d;
		} else if(strReplacing.length === 6) {
			// YYYYMD → YYYY/0M/0D
			let y = strReplacing.substring(0, 4);
			let m = "0" + strReplacing.substring(4, 5);
			let d = "0" + strReplacing.substring(5, 6);
			strReplaced = y + "/" + m + "/" + d;
		}
	}
	
	// --- スラッシュ区切りで入力された場合 (YYYY/M/D) ---
	if(strReplaced === ""){
		let parts = strReplacing.split("/");
		if(parts.length === 3){
			let y = parts[0];
			let m = parts[1].padStart(2,"0");
			let d = parts[2].padStart(2,"0");
			strReplaced = y + "/" + m + "/" + d;
		} else {
			strReplaced = strReplacing;
		}
	}
	
	$("#" + targetId).val(strReplaced);
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