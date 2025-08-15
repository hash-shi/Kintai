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
		
		// 新規の時は営業所コードを非活性
		$("#txtEigyoshoCode").prop('readonly', false);
		if (isNew == "1") {
			$("#txtEigyoshoCode").prop('readonly', true);
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
	
	// 新規モードと更新モードで分岐
	var isNew = $("#hdnIsNew").val();
	
	// 更新モード時の切り替えに必要
	var txtEigyoshoCode = $("#txtEigyoshoCode").val();
	var hdnEigyoshoCode = $("#hdnEigyoshoCode").val();
	
	if (isNew == "1") {
		// 新規モード
		proc("insert",{}, function(data){
			// 確認メッセージ
			if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
			// 登録処理の本体
			proc("insert_",{}, function(data){
				// 完了メッセージ
				alert("データが正常に更新されました。");
				// 画面のクリアなど何かしらの処理
				// 処理した営業所コードで再読込
				$("#srhTxtEigyoshoCode").val($("#txtEigyoshoCode").val());
				getMstEigyosho();
			});
		});
	} else {
		// 更新モード
		proc("update",{}, function(data){
			
			if (txtEigyoshoCode == hdnEigyoshoCode) {
				// 画面項目と隠し項目が同じ値 = 単純にデータ更新
				// 確認メッセージ
				if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
				// 登録処理の本体
				proc("update_",{}, function(data){
					// 完了メッセージ
					alert("データが正常に更新されました。");
					// 画面のクリアなど何かしらの処理
					// 処理した営業所コードで再読込
					$("#srhTxtEigyoshoCode").val($("#txtEigyoshoCode").val());
					getMstEigyosho();
				});
			} else {
				// 画面項目と隠し項目が異なる値 = データコピー(画面で入力した新しい値で登録)
				// 確認メッセージ
				if(!confirm("データのコピーを行います。\nよろしいですか？")) { return; }
				
				// 登録処理の本体
				proc("copy_",{}, function(data){
					// 完了メッセージ
					alert("データが正常に更新されました。");
					// 画面のクリアなど何かしらの処理
					// 処理した営業所コードで再読込
					$("#srhTxtEigyoshoCode").val($("#txtEigyoshoCode").val());
					getMstEigyosho();
				});
			}
		});
	}
}