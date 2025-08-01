//****************************************************************************
// onLogout
//
//
//
//
//****************************************************************************
function onLogout(){
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

//********************************************************************************************************************************************************
// 営業所マスタ検索ダイアログ用制御
//********************************************************************************************************************************************************

//****************************************************************************
// searchDialog
//
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
		$("#searchStaffResult").children().remove();
		
		for (var count = 0 ; count < result.length ; count++){
			var record			= result[count];
//			$("#searchStaffResult").append("<li><div class=\"name\">" + record["nmSyain"] + " " + "(" + record["kjTentanms"] + ")" + "</div><div class=\"button\"><button type=\"button\" onclick=\"$('#input_responsibleCdSyain').val('" + record["cdSyain"] + "');closeDialog();getStaffInformation();\">選択</button></div></li>");
		}
		
	});
	
}


//********************************************************************************************************************************************************
// 部署マスタ検索ダイアログ用制御
//********************************************************************************************************************************************************


//********************************************************************************************************************************************************
// 社員マスタ検索ダイアログ用制御
//********************************************************************************************************************************************************


//********************************************************************************************************************************************************
// 区分マスタ検索ダイアログ用制御
//********************************************************************************************************************************************************


//********************************************************************************************************************************************************
// 検索ダイアログのドラッグ移動用制御
//**************************************************************************************************************************
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