/*
*
* 初期値設定
*
*/
window.onload = function(){
	proc("getTaishoYM", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
			
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
			
		let result			= contents["result"];
			
		$("#srhTxtTaishoYM").val(result);
		$("#txtSearchedTaishoYM").val(result);
	});
}
/*
*
* 対象年月フォーカスアウト時のフォーマット編集処理
*
*/
function getTaishoYMFormat(){
	let strReplacing = $("#srhTxtTaishoYM").val();
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
	// それ以外の場合、そのまま
	else {
		strReplaced = strReplacing;
	}

	$("#srhTxtTaishoYM").val(strReplaced);
}

//****************************************************************************
// onSearchKintaiKakutei
//
//
//
//
//****************************************************************************
function onSearchKintaiKakutei(){	
	//更新処理に備え、検索条件を保持
	$("#txtSearchedTaishoYM").val($("#srhTxtTaishoYM").val());
	//検索結果表示
	proc("searchKintaiKakutei", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
			
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		$("#nyuryokuArea").css("visibility", "");
		$("#nyuryokuArea").addClass("upd");
		$("#buttonArea").css("visibility", "");

		let result			= contents["result"];

		onDisplayNyuryokuArea(result);
		
		// 全選択チェックボックスの取得
		const selectAllCheckbox = document.getElementById('cbxKakuteiAll');
		// 個別チェックボックスの取得
		const checkboxes = document.querySelectorAll('.cbxKakutei');
						
		// 初期状態で全選択
		selectAllCheckbox.checked = true;
		selectAllCheckbox.value = "01";
		checkboxes.forEach(checkbox => {
			checkbox.checked = true;
			checkbox.value	 = selectAllCheckbox.value;
		});
		
		let kintaiKakuteiResultAll = [];
		kintaiKakuteiResultAll = result;
		$("#kakuteiCount").val(kintaiKakuteiResultAll.length);
		
	});
	
}

//****************************************************************************
// onDisplayNyuryokuArea
//
//
//
//
//****************************************************************************
function onDisplayNyuryokuArea(result){
	// 検索結果エリアをクリアする
	$("#kihonNyuryokuArea").children().remove();
	
	let kintaiKakuteiResultAll = [];
	kintaiKakuteiResultAll = result;
	
	for(let i = 0; i < kintaiKakuteiResultAll.length; i++){
		let record = kintaiKakuteiResultAll[i];
		let eigyoshoCode = record["EigyoshoCode"];
		let eigyoshoName = record["EigyoshoName"];
		let kakuteiKbn01 = record["KakuteiKbn01"];
		let kakuteiKbn02 = record["KakuteiKbn02"];
		
		let kakuteiKbnName01 = record["KakuteiKbnName01"];
		let kakuteiKbnName02 = record["KakuteiKbnName02"];
		//一部項目の表示色変更
		let mojiColorClass1 = "";
		if(kakuteiKbnName01 != "本社確定済み"){
			mojiColorClass1 = "kbnColor";			
		}
		
		let mojiColorClass2 = "";
		if(kakuteiKbnName02 != "本社確定済み"){
			mojiColorClass2 = "kbnColor";			
		}

		let kihonNyuryokuAreaHtml = "";
			kihonNyuryokuAreaHtml =
				"<tr>" +
					"<td class=\"value center\">" + 
						"<input type =\"checkbox\" class=\"cbxKakutei\" name=\"cbxKakutei" + i + "\" id=\"cbxKakutei" + i + "\" values= \"\" onclick=\"onSentaku();\">" + 
					"</td>" +
					"<td class=\"value center w50\"><a name=\"txtEigyoshoCode" + i + "\" id = \"txtEigyoshoCode" + i + "\">" + eigyoshoCode + "</a></td>" +
					"<td class=\"value w150\"><a>" + eigyoshoName + "</a></td>" +
					"<td class=\"value w140\"><a name=\"txtKakuteiKbnName01" + i + "\" id = \"txtKakuteiKbnName01" + i + "\" class = \"" +mojiColorClass1+ "\" values= \"\">" + kakuteiKbnName01 + "</a></td>" +
					"<td class=\"value w140\"><a name=\"txtKakuteiKbnName02" + i + "\" id = \"txtKakuteiKbnName02" + i + "\" class = \"" +mojiColorClass2+ "\" values= \"\">" + kakuteiKbnName02 + "</a></td>" +
					"<td><input type=\"hidden\" name=\"hdnTxtEigyoshoCode" + i + "\" id=\"hdnTxtEigyoshoCode1" + i + "\" value=\"" + eigyoshoCode + "\"></td>" +
					"<td><input type=\"hidden\" name=\"hdnTxtKakuteiKbn01" + i + "\" id=\"hdnTxtKakuteiKbn01" + i + "\" value=\"" + kakuteiKbn01 + "\"></td>" +
					"<td><input type=\"hidden\" name=\"hdnTxtKakuteiKbn02" + i + "\" id=\"hdnTxtKakuteiKbn02" + i + "\" value=\"" + kakuteiKbn02 + "\"></td>" +
				"</tr>";

		$("#kihonNyuryokuArea").append(kihonNyuryokuAreaHtml);
	}
}

//****************************************************************************
// onSentakuAll
//
//
//
//
//****************************************************************************
function onSentakuAll() {

	// 全選択チェックボックスの取得
	const selectAllCheckbox = document.getElementById('cbxKakuteiAll');
	// 個別チェックボックスの取得
	const checkboxes = document.querySelectorAll('.cbxKakutei');
	
	// 全選択チェックボックスのイベントリスナー
	selectAllCheckbox.addEventListener('change', function () {
		checkboxes.forEach(checkbox => {
			if (selectAllCheckbox.checked) {
				selectAllCheckbox.value = "01";
			} else {
				selectAllCheckbox.value = "02";
			}
			checkbox.checked = selectAllCheckbox.checked;
			checkbox.value	 = selectAllCheckbox.value;
		});
	});
}

//****************************************************************************
// onSentaku
//
//
//
//
//****************************************************************************
function onSentaku() {

	// 全選択チェックボックスの取得
	const selectAllCheckbox = document.getElementById('cbxKakuteiAll');
	// 個別チェックボックスの取得
	const checkboxes = document.querySelectorAll('.cbxKakutei');
	
	// 個別チェックボックスの状態を監視して全選択を制御
	checkboxes.forEach(checkbox => {
		checkbox.addEventListener('change', function () {
			if (checkbox.checked) {
				checkbox.value = "01";
			 } else {
				checkbox.value = "02";
			 }
			if (!checkbox.checked) {
				selectAllCheckbox.checked = false;
			} else if (Array.from(checkboxes).every(cb => cb.checked)) {
				selectAllCheckbox.checked = true;
			}
		});
	});
}

//****************************************************************************
// onKakuteiKaijo
//
//
//
//
//****************************************************************************
function onKakuteiKaijo(){
	//確定解除処理呼び出し
	proc("kaijo", {}, function(data){
		
		// 確認メッセージ
		if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
		proc("kaijo_", {}, function(data){
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
		
			let contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
		
			let result			= contents["result"];

			if(result == true){
				alert("正常に登録しました。");
				onSearchKintaiKakutei();
			} else {
				alert("このデータはすでに、別のユーザに更新されています。もう一度データを確認してください。");
			}
			document.getElementById("txtTaishoYM").focus();
			//画面表示を初期状態に戻す
			$("#nyuryokuArea").css("visibility", "hidden");
			$("#buttonArea").css("visibility", "hidden");
		});
	});
}

//****************************************************************************
// onKakutei
//
//
//
//
//****************************************************************************
function onKakutei(){
	//更新処理呼び出し
	proc("kakutei", {}, function(data){
		
		// 確認メッセージ
		if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
		proc("kakutei_", {}, function(data){
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
		
			let contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
		
			let result			= contents["result"];

			if(result == true){
				alert("正常に登録しました。");
				onSearchKintaiKakutei();
			} else {
				alert("このデータはすでに、別のユーザに更新されています。もう一度データを確認してください。");
			}
			document.getElementById("txtTaishoYM").focus();
			//画面表示を初期状態に戻す
			$("#nyuryokuArea").css("visibility", "hidden");
			$("#buttonArea").css("visibility", "hidden");
		});
	});
}

//****************************************************************************
// ファンクションキーF2
//
//
//
//
//****************************************************************************
function onKeyEventF02() {
	
	// buttonAreaの表示状態を取得
	var display = $("#buttonArea").css("visibility");

	// buttonAreaが非表示(初期表示時)はスキップする。
	if (display == "visible") {
		// 該当の処理を呼び出す。
		onKakuteiKaijo();
	}
}

//****************************************************************************
// ファンクションキーF9
//
//
//
//
//****************************************************************************
function onKeyEventF09() {
	
	// buttonAreaの表示状態を取得
	var display = $("#buttonArea").css("visibility");

	// buttonAreaが非表示(初期表示時)はスキップする。
	if (display == "visible") {
		// 該当の処理を呼び出す。
		onKakutei();
	}
}