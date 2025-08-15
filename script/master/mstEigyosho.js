//****************************************************************************
// getMstEigyosho
//
//
//
//
//****************************************************************************
function getMstEigyosho() {
	
	proc("search",{}, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["isNew"] == undefined || contents["mstDatas"] == undefined){ return; }
		
		// 取得した値の格納
		var isNew = contents["isNew"];
		$("#hdnIsNew").val(isNew);
		
		// mainAreaを表示する。
		$("#mainArea").css("visibility", "visible");
		$("#buttonArea").css("visibility", "visible");
		
		// 既に背景色が設定されている場合は一旦削除
		$("#mainArea").removeClass('ins');
		$("#mainArea").removeClass('upd');
		// 背景色を設定
		if (isNew == "1") {
			$("#mainArea").addClass("ins");
		} else {
			$("#mainArea").addClass("upd");
		}
		
		// 新規の時は削除ボタンは非活性
		$("#btnDelete").prop('disabled', false);
		if (isNew == "1") {
			$("#btnDelete").prop('disabled', true);
		}
		
		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtEigyoshoCode").val(mstDatas[i]["EigyoshoCode"]);
			$("#txtEigyoshoName").val(mstDatas[i]["EigyoshoName"]);
			// $("#lblSaishuKoshinShainNO").html(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#lblSaishuKoshinShainName").html(mstDatas[i]["SaishuKoshinShainName"]);
			$("#lblSaishuKoshinDate").html(mstDatas[i]["SaishuKoshinDate"]);
			$("#lblSaishuKoshinJikan").html(mstDatas[i]["SaishuKoshinJikan"]);
			
			$("#hdnEigyoshoCode").val(mstDatas[i]["EigyoshoCode"]);
			$("#hdnSaishuKoshinShainNO").val(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#hdnSaishuKoshinShainName").val(mstDatas[i]["SaishuKoshinShainName"]);
			$("#hdnSaishuKoshinDate").val(mstDatas[i]["SaishuKoshinDate"]);
			$("#hdnSaishuKoshinJikan").val(mstDatas[i]["SaishuKoshinJikan"]);
			
		}
		
	});
}

//****************************************************************************
// onDelete
//
//
//
//
//****************************************************************************
function onDelete() {
	
	proc("delete",{}, function(data){
		// 確認メッセージ
		if(!confirm("データの削除を行います。\nよろしいですか？")) { return; }
		
		// 削除処理の本体
		proc("delete_",{}, function(data){
			// 完了メッセージ
			alert("データが正常に更新されました。");
			// 画面のクリアなど何かしらの処理
			getMstEigyosho();
		});
		
	});
}

//****************************************************************************
// onUpdate
//
//
//
//
//****************************************************************************
function onUpdate() {
	
	proc("update",{}, function(data){
		// 確認メッセージ
		if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
		
		// 更新処理の本体
		proc("update_",{}, function(data){
			// 完了メッセージ
			alert("データが正常に更新されました。");
			// 画面のクリアなど何かしらの処理
			getMstEigyosho();
		});
	});
}