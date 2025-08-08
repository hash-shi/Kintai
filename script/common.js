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
	// 検索結果となる名称項目のid
	console.log(returnId);
	
	var value = $("#" + searchId).val();
	console.log(value);
	
	// クリア
	$("#" + returnId).val("");
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
	// 検索結果となる名称項目のid
	console.log(returnId);
	
	var value = $("#" + searchId).val();
	console.log(value);
	
	// クリア
	$("#" + returnId).val("");
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
	// 検索結果となる名称項目のid
	console.log(returnId);
	
	var value = $("#" + searchId).val();
	console.log(value);
	
	// クリア
	$("#" + returnId).val("");
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
function getKubunName(kbnCodeId, codeId, nameId) {
	// 取得対象の値
	var kbnCode = $("#" + kbnCodeId).val();
	var code    = $("#" + codeId).val();

	console.log("kbnCode =", kbnCode);
	console.log("code    =", code);

	// 名称欄の初期化
	$("#" + nameId).val("");

	// どちらかが空の場合はスキップ
	if (!kbnCode || !code) return;

	proc("getKubunName", { 'kbnCode': kbnCode, 'code': code }, function(data) {
		if (!data || !data["contents"] || !data["contents"]["result"]) return;

		var result = data["contents"]["result"];

		// 名称セット
		$("#" + nameId).val(result);
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
	
	// ない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }
	
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
	
	// ない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }
	
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
	
	// ない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }
	
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
	
	// ない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }
	
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
	
	// ない場合はスキップ
	console.log(searchIds);
	if (!searchIds) { return; }
	
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

			var	eigyoushoCode = record["EigyoshoCode"];
			var eigyoushoName = record["EigyoshoName"];
			
			// onclickで呼ぶ関数を共通化
			var onclickCode = "setDialogReturnValue('" + eigyoushoCode + "'); closeDialog(); setDialogReturnValueEigyoshoName(); return false;";
			
			var rowHtml = "<tr>" + 
								"<td><div class=\"eigyoshoCode\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + eigyoushoCode + "</a></div></td>" + 
								"<td><div class=\"eigyoshoName\"><a href=\"#\" onclick=\"" + onclickCode + "\">" + eigyoushoName + "</a></div></td>" +  
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

function getMstbushos(){

	proc("search", {}, function(data){
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result			= contents["result"];
		
		// 検索結果エリアをクリアする。
		$("#searchStaffResult").children().remove();
		
		for (var count = 0 ; count < result.length ; count++){
			var record			= result[count];
			
			var	eigyoushoCode = record["EigyoshoCode"];
			var bushoCode	  = record["BushoCode"];
			var bushoName	  = record["BuushoName"];
			
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
function formatDateYYYY() {
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