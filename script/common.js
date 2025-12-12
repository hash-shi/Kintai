//****************************************************************************
// 初期処理
//
//
//
//
//****************************************************************************
$(document).ready(function(){
	// onkeydownイベントハンドラに、key_event関数を登録
	document.onkeydown = onKeyEvent;
});

//****************************************************************************
// キー押下制御
//
//
//
//
//****************************************************************************
function onKeyEvent(event) {
	// 発生したイベントのキーコードを取得
	var keyCode = event.keyCode;
	console.log("keyCode:" + keyCode);
	
	// Enterキー または F1-F12キー以外はスキップ
	if(keyCode != 13 && (keyCode < 112 && 123 < keyCode)) { return ; }
	
	if (keyCode == 13) {
		// Enterキーの場合
		onEnterKeyEvent(event);
	}
	else if(112 <= keyCode && keyCode <= 123) {
		// F1-F12キーの場合
		onFunctionKeyEvent(event);
	}
	
}

//****************************************************************************
// キー押下制御
//
//
//
//
//****************************************************************************
function onEnterKeyEvent(event) {
	
	// フォーカス対象(入力項目/チェックボックス/ラジオボタン/リスト/ボタン)
	const selector = `
		input[type="text"],
		input[type="tel"],
		input[type="number"],
		input[type="email"],
		input[type="url"],
		input[type="password"],
		input[type="checkbox"],
		input[type="radio"],
		textarea,
		select,
		button
	`;
	
	// 現在のフォーカス要素
	const $current = $(event.target);
	
	// textarea型項目は改行をする都合上、フォーカス移動させない。
	if ($current.is('textarea')) {
		return;
	}
	
	// textarea以外はEnterのデフォルト動作を禁止
	event.preventDefault();
	
	// ボタンの場合はフォーカス移動させず、各ボタン処理を実行する。
	if ($current.is('button')) {
		$current.click();
		return;
	}
	
	// ---- フォーカス対象一覧を作成 ----
	let $focusables = $(selector)
	
		// 非表示はフォーカスしない
		.filter(':visible')
		
		// disabled はフォーカスしない
		.filter(':not([disabled])')
		
		// readonly はフォーカスしない
		.filter(function () {
			if ($(this).is('input') && $(this).prop('readonly')) {
				return false;
			}
			return true;
		})
		// button は特定属性を持つものだけフォーカス
		.filter(function () {
			
			if ($(this).is('button')) {
				
				// buttonAreaがhiddenの場合は、data-focusのon/offに関係なくフォーカスしない
				const $parentArea = $(this).closest('#buttonArea');
				if ($parentArea.length && $parentArea.css('visibility') === 'hidden') {
					return false;
				}
				
				// ※data-focus="off" のボタンだけフォーカスしない
				return !($(this).data('focus') === 'off');
			}
			return true;
			
		});
	
	// 現在のインデックス
	const index = $focusables.index($current);

	// 次の要素へフォーカス移動
	if (index >= 0 && index < $focusables.length - 1) {
	    $focusables.eq(index + 1).focus();
	} else {
		// 最初の項目にフォーカス移動
		if (0 < $focusables.length) {
			$focusables.eq(0).focus();
		}
	}
	
}

//****************************************************************************
// ファンクションキー押下制御
//
//
//
//
//****************************************************************************
function onFunctionKeyEvent(event) {
	
//	// 発生したイベントのキーコードを取得
//	var keyCode = event.keyCode;
//	console.log("keyCode:" + keyCode);
	
	// F1-F12キー以外はスキップ
	if(keyCode < 112 && 123 < keyCode) { return ; }
	
	// F1-F12キーであれば、無効化する(F1キー：112,... F12キー:123)
	if(112 <= keyCode && keyCode <= 123) {
	    event.keyCode = null;
	    event.returnValue = false;
	}
	
	// 該当するキーコードで分岐。それぞれのcase内に、実行したい独自の処理を記述する。
	switch(keyCode) {
		// F1キー
		case 112:
			if (typeof onKeyEventF01 == 'function') {
				onKeyEventF01();
			}
			break;
		// F2キー
		case 113:
			if (typeof onKeyEventF02 == 'function') {
				onKeyEventF02();
			}
			break;
		// F3キー
		case 114:
			if (typeof onKeyEventF03 == 'function') {
				onKeyEventF03();
			}
			break;
		// F4キー
		case 115:
			if (typeof onKeyEventF04 == 'function') {
				onKeyEventF04();
			}
			break;
		// F5キー
		case 116:
			if (typeof onKeyEventF05 == 'function') {
				onKeyEventF05();
			}
			break;
		// F6キー
		case 117:
			if (typeof onKeyEventF06 == 'function') {
				onKeyEventF06();
			}
			break;
		// F7キー
		case 118:
			if (typeof onKeyEventF07 == 'function') {
				onKeyEventF07();
			}
			break;
		// F8キー
		case 119:
			if (typeof onKeyEventF08 == 'function') {
				onKeyEventF08();
			}
			break;
		// F9キー
		case 120:
			if (typeof onKeyEventF09 == 'function') {
				onKeyEventF09();
			}
			break;
		// F10キー
		case 121:
			if (typeof onKeyEventF10 == 'function') {
				onKeyEventF10();
			}
			break;
		// F11キー
		case 122:
			if (typeof onKeyEventF11 == 'function') {
				onKeyEventF11();
			}
			break;
		// F12キー
		case 123:
			if (typeof onKeyEventF12 == 'function') {
				onKeyEventF12();
			}
			break;
		default:
			break;
	}
	return;
}

//****************************************************************************
// onLogout
//
//
//
//
//****************************************************************************
function onLogout(){	
	// サーバーサイドのセッションもクリアする。
	proc("sessionInvalidate" ,{ }, function(){ });
	// セッションストレージの削除
	sessionStorage.clear()
	// ログイン画面に遷移
	location.href				= "./index";
}

//****************************************************************************
// getEigyoshoName
// 営業所名取得
//
//
//
//****************************************************************************
function getEigyoshoName(searchId, returnId) {
	
	// 検索対象となるコード項目のid
	console.log(searchId);
	if (!searchId) { return; }
	if(!($("#" + searchId).length)){ return; }
	
	// 検索結果となる名称項目のid
	console.log(returnId);
	if (!returnId) { return; }
	if(!($("#" + returnId).length)){ return; }
	
	// クリア
	$("#" + returnId).val("");
	
	// コードを取得
	var value = $("#" + searchId).val();
	console.log(value);

	// 空の場合はスキップ
	if (value == "") { return; }
	
	proc("getEigyoshoName" ,{ 'eigyoshoCode': value }, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents				= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result					= contents["result"];
		
		// 格納
		$("#" + returnId).val(result);
		
	});
}

//****************************************************************************
// getBushoName
// 部署名取得
//
//
//
//****************************************************************************
function getBushoName(searchId, returnId) {
	
	// 検索対象となるコード項目のid
	console.log(searchId);
	if (!searchId) { return; }
	if(!($("#" + searchId).length)){ return; }

	// 検索結果となる名称項目のid
	console.log(returnId);
	if (!returnId) { return; }
	if(!($("#" + returnId).length)){ return; }

	// クリア
	$("#" + returnId).val("");

	// コードを取得
	var value = $("#" + searchId).val();
	console.log(value);

	// 空の場合はスキップ
	if (value == "") { return; }
	
	proc("getBushoName" ,{ 'bushoCode': value }, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents				= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result					= contents["result"];
		
		// 格納
		$("#" + returnId).val(result);
		
	});
}

//****************************************************************************
// getShainName
// 社員名取得
//
//
//
//****************************************************************************
function getShainName(searchId, returnId) {
	
	// 検索対象となるコード項目のid
	console.log(searchId);
	if (!searchId) { return; }
	if(!($("#" + searchId).length)){ return; }

	// 検索結果となる名称項目のid
	console.log(returnId);
	if (!returnId) { return; }
	if(!($("#" + returnId).length)){ return; }

	// クリア
	$("#" + returnId).val("");

	// コードを取得
	var value = $("#" + searchId).val();
	console.log(value);

	// 空の場合はスキップ
	if (value == "") { return; }
	
	proc("getShainName" ,{ 'shainNo': value }, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents				= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result					= contents["result"];
		
		// 格納
		$("#" + returnId).val(result);
		
	});
}


//****************************************************************************
// getKubunName
// 区分名取得（区分コード＋コード → 名称）
//
//
//
//****************************************************************************
function getKubunName(searchId1, searchId2, returnId) {
	
	// 検索対象となるコード項目のid
	console.log(searchId1);
	if (!searchId1) { return; }
	if(!($("#" + searchId1).length)){ return; }

	// 検索結果となるコード項目のid
	console.log(searchId2);
	if (!searchId2) { return; }
	if(!($("#" + searchId2).length)){ return; }
	
	// 検索結果となる名称項目のid
	console.log(returnId);
	if (!returnId) { return; }
	if(!($("#" + returnId).length)){ return; }
	
	// クリア
	$("#" + returnId).val("");
	
	// コードを取得
	var kbnCode = $("#" + searchId1).val();
	console.log(kbnCode);
	var code = $("#" + searchId2).val();
	console.log(code);
	
	// 空の場合はスキップ
	if (kbnCode == "" || code == "") { return; }
	
	proc("getKubunName", { 'kbnCode': kbnCode, 'code': code }, function(data) {
		if (!data || !data["contents"] || !data["contents"]["result"]) return;

		var result = data["contents"]["result"];

		// 名称セット
		$("#" + returnId).val(result);
	});
}


//****************************************************************************
// opnDialog
// 検索ダイアログの表示
// 
//
//
//****************************************************************************
function opnDialog(dialogId, searchId, returnId) {
	
	// 呼び出す検索ダイアログのid
	console.log(dialogId);
	// 検索対象となるコード項目のid、複数ある場合は「,」で区切る
	console.log(searchId);
	// 検索結果となる名称項目のid、複数ある場合は「,」で区切る
	console.log(returnId);
	
	// ヘッダの隠し項目に項目idを退避させる。
	// ダイアログの検索結果選択時に使用する。
	$("#hdnDialogSearchId").val(searchId);
	$("#hdnDialogReturnId").val(returnId);
	
	this.openDialog(dialogId, 500, 600, {}, function(){
		// ダイアログ終了後に実行したい処理を記述
	});	
}

//****************************************************************************
// setDialogReturnValue
// 検索ダイアログの選択結果を返却
//
//
//
//****************************************************************************
function setDialogReturnValue(values) {
	
	// 検索結果となるコードを親画面の項目に格納する。
	console.log(values);
	
	// 対象となる親画面の項目はヘッダの隠し項目に入れてある。
	var searchIds = $("#hdnDialogSearchId").val();
	
	// 返却項目がない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }
	if(!($("#" + searchIds).length)){ return; }
	
	// 複数項目ある場合に備えて,で分割
	var value = values.split(',');
	var searchId = searchIds.split(',');
	
	// 格納する。
	for (var i = 0; i < searchId.length; i++) {
		$("#" + searchId[i]).val(value[i]);
	}
}

//****************************************************************************
// setDialogReturnValueEigyoshoName
// 検索ダイアログの選択結果を返却した項目に対してgetNameを行う。
// getEigyoshoName用
//
//
//****************************************************************************
function setDialogReturnValueEigyoshoName() {
	
	// setDialogReturnValueで返却値を設定した項目に対してgetNameをする。
	
	// 対象となる親画面の項目はヘッダの隠し項目に入れてある。
	var searchIds = $("#hdnDialogSearchId").val();
	
	// 対象となる親画面の名前項目はヘッダの隠し項目に入れてある。
	var returnIds = $("#hdnDialogReturnId").val();
	
	// 返却項目がない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }
	
	// 返却項目がない場合はスキップ
	console.log(returnIds);
	if (!returnIds) { return; }	
	
	// 複数項目ある場合に備えて,で分割
	var searchId = searchIds.split(',');
	var returnId = returnIds.split(',');
	
	// 名称取得を実行する。
	for (var i = 0; i < searchId.length; i++) {
		getEigyoshoName(searchId[i], returnId[i]);
	}
}

//****************************************************************************
// setDialogReturnValueBushoName
// 検索ダイアログの選択結果を返却した項目に対してgetNameを行う。
// getBushoName用
//
//
//****************************************************************************
function setDialogReturnValueBushoName() {
	
	// setDialogReturnValueで返却値を設定した項目に対してgetNameをする。
	
	// 対象となる親画面の項目はヘッダの隠し項目に入れてある。
	var searchIds = $("#hdnDialogSearchId").val();
	
	// 対象となる親画面の名前項目はヘッダの隠し項目に入れてある。
	var returnIds = $("#hdnDialogReturnId").val();
	
	// 返却項目がない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }

	// 返却項目がない場合はスキップ
	console.log(returnIds);
	if (!returnIds) { return; }	
	
	// 複数項目ある場合に備えて,で分割
	var searchId = searchIds.split(',');
	var returnId = returnIds.split(',');
	
	// 名称取得を実行する。
	for (var i = 0; i < searchId.length; i++) {
		getBushoName(searchId[i], returnId[i]);
	}
}

//****************************************************************************
// setDialogReturnValueShainName
// 検索ダイアログの選択結果を返却した項目に対してgetNameを行う。
// getShainName用
//
//
//****************************************************************************
function setDialogReturnValueShainName() {
	
	// setDialogReturnValueで返却値を設定した項目に対してgetNameをする。
	
	// 対象となる親画面の項目はヘッダの隠し項目に入れてある。
	var searchIds = $("#hdnDialogSearchId").val();
	
	// 対象となる親画面の名前項目はヘッダの隠し項目に入れてある。
	var returnIds = $("#hdnDialogReturnId").val();
	
	// 返却項目がない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }

	// 返却項目がない場合はスキップ
	console.log(returnIds);
	if (!returnIds) { return; }	
	
	// 複数項目ある場合に備えて,で分割
	var searchId = searchIds.split(',');
	var returnId = returnIds.split(',');
	
	// 名称取得を実行する。
	for (var i = 0; i < searchId.length; i++) {
		getShainName(searchId[i], returnId[i]);
	}
}

//****************************************************************************
// setDialogReturnValueKbnName
// 検索ダイアログの選択結果を返却した項目に対してgetNameを行う。
// getKbnName用
//
//
//****************************************************************************
function setDialogReturnValueKbnName() {
	
	// setDialogReturnValueで返却値を設定した項目に対してgetNameをする。
	
	// 対象となる親画面の項目はヘッダの隠し項目に入れてある。
	var searchIds = $("#hdnDialogSearchId").val();
	
	// 対象となる親画面の名前項目はヘッダの隠し項目に入れてある。
	var returnIds = $("#hdnDialogReturnId").val();
	
	// 返却項目がない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }

	// 返却項目がない場合はスキップ
	console.log(returnIds);
	if (!returnIds) { return; }	
	
	// 複数項目ある場合に備えて,で分割
	var searchId = searchIds.split(',');
	var returnId = returnIds.split(',');
	
	// 名称取得を実行する。
	getKubunName(searchId[0], searchId[1], returnId[0]);

}

//****************************************************************************
// 営業所マスタ検索ダイアログ用制御
//****************************************************************************

//****************************************************************************
// searchDialog
// getMstEigyoshos
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
		$("#searchEigyoshoResult").children().remove();
		
		for (var count = 0 ; count < result.length ; count++){
			var record			= result[count];

			var eigyoshoCode = record["EigyoshoCode"];
			var eigyoshoName = record["EigyoshoName"];
			
			// onclickで呼ぶ関数を共通化
			var onclickCode = "setDialogReturnValue('" + eigyoshoCode + "'); closeDialog(); setDialogReturnValueEigyoshoName(); return false;";
			
			var rowHtml = "<tr>" + 
								"<td><div class=\"eigyoshoCode\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + eigyoshoCode + "</a></div></td>" + 
								"<td><div class=\"eigyoshoName\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + eigyoshoName + "</a></div></td>" +  
						  "</tr>";
			
			$("#searchEigyoshoResult").append(rowHtml);
		
		}
		
	});
	
}

//****************************************************************************
// 部署マスタ検索ダイアログ用制御
//****************************************************************************

//****************************************************************************
// searchDialog
// getMstBushos
// 
//
//
//****************************************************************************

function getMstBushos(){

	proc("search", {}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result			= contents["result"];
		
		// 検索結果エリアをクリアする。
		$("#searchBushoResult").children().remove();
		
		for (var count = 0 ; count < result.length ; count++){
			var record			= result[count];
			
			var	eigyoshoCode = record["EigyoshoCode"];
			var bushoCode	  = record["BushoCode"];
			var bushoName	  = record["BushoName"];
			
			// onclickで呼ぶ関数を共通化
			var onclickCode = "setDialogReturnValue('" + bushoCode + "'); closeDialog(); setDialogReturnValueBushoName(); return false;";
			
			var rowHtml = "<tr>" + 
								"<td><div class=\"eigyoshoCode\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + eigyoshoCode + "</a></div></td>" +
								"<td><div class=\"bushoCode\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + bushoCode + "</a></div></td>" +
								"<td><div class=\"bushoName\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + bushoName + "</a></div></td>" +  
						  "</tr>";
			
			$("#searchBushoResult").append(rowHtml);
		
		}
		
	});
	
}


//********************************************************************************************************************************************************
// 社員マスタ検索ダイアログ用制御
//********************************************************************************************************************************************************

//****************************************************************************
// searchDialog
// getMstShains
//
//
//
//****************************************************************************
		
function getMstShains(){
	
	proc("search", {}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result			= contents["result"];
		
		// 検索結果エリアをクリアする。
		$("#searchShainResult").children().remove();		
		
		for (var count = 0 ; count < result.length ; count++){
		    var record = result[count];
			
			
			var shainNo = record["ShainNO"];
			var shainName = record["ShainName"];
			var eigyoshoCode = record["EigyoshoCode"];
			var shainKbn = record["ShainKbn"];
			
			// onclickで呼ぶ関数を共通化
			var onclickCode = "setDialogReturnValue('" + shainNo + "'); closeDialog(); setDialogReturnValueShainName(); return false;";

			var rowHtml = "<tr>" +
							  "<td><div class=\"shainNo\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + shainNo + "</a></div></td>" +
							  "<td><div class=\"shainName\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + shainName + "</a></div></td>" +
							  "<td><div class=\"eigyoshoCode\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + eigyoshoCode + "</a></div></td>" +
							  "<td><div class=\"shainKbn\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + shainKbn + "</a></div></td>" +
						  "</tr>";

						$("#searchShainResult").append(rowHtml);
			
			}
	});
}




//********************************************************************************************************************************************************
// 区分マスタ検索ダイアログ用制御
//********************************************************************************************************************************************************

//****************************************************************************
// searchDialog
// getMstKubuns
//
//
//
//****************************************************************************
function getMstKubuns(){
	
	proc("search", {}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result			= contents["result"];
		
		// 検索結果エリアをクリアする。
		$("#searchKubunResult").children().remove();		
		
		for (var count = 0 ; count < result.length ; count++){
		    var record = result[count];
			
			
			var kbnCode = record["KbnCode"];
			var code = record["Code"];
			var kbnName = record["KbnName"];
			
			// onclickで呼ぶ関数を共通化
			var onclickCode = "setDialogReturnValue('" + kbnCode + "," + code + "'); closeDialog(); setDialogReturnValueKbnName('" + kbnCode + "," + code + "," + kbnName + "'); return false;";

			var rowHtml = "<tr>" +
							  "<td><div class=\"kbnCode\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + kbnCode + "</a></div></td>" +
							  "<td><div class=\"code\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + code + "</a></div></td>" +
							  "<td><div class=\"kbnName\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + kbnName + "</a></div></td>" +
						  "</tr>";

						$("#searchKubunResult").append(rowHtml);
			
			}
	});
}

//****************************************************************************
// 年月フォーカスアウト時のフォーマット編集処理
// YYYY/MM
//
//
//
//****************************************************************************
function onChangeYM(targetId){
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
// 年月日フォーカスアウト時のフォーマット編集処理
//
//
//
//
//****************************************************************************

function onChangeYMD(targetId){
	let strReplacing = $("#" + targetId).val().trim();
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
			// YYYYMMDD → YYYY/MM/DD
			let y = strReplacing.substring(0, 4);
			let m = strReplacing.substring(4, 6);
			let d = strReplacing.substring(6, 8);
			strReplaced = y + "/" + m + "/" + d;
		} else if(strReplacing.length === 7) {
			// YYYYMDD or YYYYMMD→ YYYY/MDD or YYYY/MMD
			let y = strReplacing.substring(0, 4);
			let md = strReplacing.substring(4); // 5〜7文字目
			strReplaced = y + "/" + md; 
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
//********************************************************************************************
// その他
//********************************************************************************************

//****************************************************************************
// formatDateYYYYMMDD
//
//****************************************************************************
function formatDateYYYYMMDD(date, sep="") {
  const yyyy = date.getFullYear();
  const mm = ('00' + (date.getMonth()+1)).slice(-2);
  const dd = ('00' + date.getDate()).slice(-2);
  return `${yyyy}${sep}${mm}${sep}${dd}`;
}
//****************************************************************************
// formatDateYYYYMM
//
//****************************************************************************
function formatDateYYYYMM(date, sep="") {
  const yyyy = date.getFullYear();
  const mm = ('00' + (date.getMonth()+1)).slice(-2);
  return `${yyyy}${sep}${mm}`;
}
//****************************************************************************
// formatDateYYYY
//
//****************************************************************************
function formatDateYYYY(date) {
  const yyyy = date.getFullYear();
  return `${yyyy}`;
}

//********************************************************************************************
// 検索ダイアログのドラッグ移動用制御
//********************************************************************************************
//**********************************************
//関数名		w2uiグリッド初期化関数
//機能		w2uiグリッドを画面中で何度も表示する場合にコケる
//			可能性があるので、w2grid関数を呼ぶ前にこの関数
//			を呼び出してください
//引数		なし
//戻り値	なし
//**********************************************
function initGrid(gridName){
	if (gridName in w2ui)				{ delete w2ui[gridName]; }
	if (gridName + '_toolbar' in w2ui)	{ delete w2ui[gridName + '_toolbar']; }
}

//===============================================
//openDialogInner
//===============================================
function openDialogInner(uuid){
	$("#" + "dialog_" + uuid).append("<input type='hidden' id='dialog_" + uuid + "_hasDialog' value='' />");
	$("#" + "dialog_" + uuid).append("<input type='hidden' id='dialog_" + uuid + "_startX' value='' />");
	$("#" + "dialog_" + uuid).append("<input type='hidden' id='dialog_" + uuid + "_startY' value='' />");
	$("#" + "dialog_" + uuid).append("<input type='hidden' id='dialog_" + uuid + "_marginX' value='' />");
	$("#" + "dialog_" + uuid).append("<input type='hidden' id='dialog_" + uuid + "_marginY' value='' />");
	$("#" + "dialog_" + uuid).append("<input type='hidden' id='dialog_" + uuid + "_mouseX' value='' />");
	$("#" + "dialog_" + uuid).append("<input type='hidden' id='dialog_" + uuid + "_mouseY' value='' />");
	$("#" + "dialog_" + uuid).find("#dialogBar").on("mousedown",function(event){ onStartDialogDrag(event, uuid); });
	$("#" + "dialog_" + uuid).find("#dialogBar").on("mouseup",function(event){ onStopDialogDrag(event, uuid); });
}

//===============================================
//onStartDialogDrag
//===============================================
var _nowMoveDialogUUID		= null;
function onStartDialogDrag(event, dialogUUID){
	// DialogUUIDの取得
	var dialogID		= "dialog_" + dialogUUID;	
	document.body.style.cursor = "move";
	_nowMoveDialogUUID	= dialogUUID;
	$("#" + dialogID + "_hasDialog"		).val("true");
	$("#" + dialogID + "_startX"		).val(parseInt($("#" + dialogID).css("left").replace("px"), 10));
	$("#" + dialogID + "_startY"		).val(parseInt($("#" + dialogID).css("top" ).replace("px"), 10));
	$("#" + dialogID + "_marginX"		).val(parseInt($("#" + dialogID).css("margin-left").replace("px"), 10));
	$("#" + dialogID + "_marginY"		).val(parseInt($("#" + dialogID).css("margin-top" ).replace("px"), 10));
	$("#" + dialogID + "_mouseX"		).val(event.clientX);
	$("#" + dialogID + "_mouseY"		).val(event.clientY);
}

//===============================================
//onStopDialogDrag
//===============================================
function onStopDialogDrag(event, dialogUUID){
	// DialogUUIDの取得
	var dialogID		= "dialog_" + dialogUUID;	
	document.body.style.cursor = "default";
	_nowMoveDialogUUID	= null;
	$("#" + dialogID + "_hasDialog"		).val("false");
	$("#" + dialogID + "_startX"		).val(0);
	$("#" + dialogID + "_startY"		).val(0);
	$("#" + dialogID + "_marginX"		).val(0);
	$("#" + dialogID + "_marginY"		).val(0);
	$("#" + dialogID + "_mouseX"		).val(0);
	$("#" + dialogID + "_mouseY"		).val(0);
}

$(document).on("mousemove", function(e){
	if (_nowMoveDialogUUID != null){
		document.body.style.cursor = "move";
		var mouseX		= e.clientX - parseInt($("#dialog_" + _nowMoveDialogUUID + "_mouseX").val(),10) + parseInt($("#dialog_" + _nowMoveDialogUUID + "_startX").val(),10);
		var mouseY		= e.clientY - parseInt($("#dialog_" + _nowMoveDialogUUID + "_mouseY").val(),10) + parseInt($("#dialog_" + _nowMoveDialogUUID + "_startY").val(),10);
		$("#dialog_" + _nowMoveDialogUUID).css("left", mouseX + "px");
		$("#dialog_" + _nowMoveDialogUUID).css("top" , mouseY + "px");
	}
});

//****************************************************************************