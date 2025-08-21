//****************************************************************************
// getmstKubun
//
//
//
//
//****************************************************************************
function getmstKubun(){

	proc("search", {}, function(data){

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
		$("#txtKbnName").focus();
				
		// 既に背景色が設定されている場合は一旦削除
		$("#mainArea").removeClass('ins');
		$("#mainArea").removeClass('upd');
		// 背景色を設定
		if (isNew == "1") {
			$("#mainArea").addClass("ins");
		} else {
			$("#mainArea").addClass("upd");
		}
		
		// 新規の時は区分コードを非活性
		$("#txtKbnCode").prop('readonly', false);
		if (isNew == "1") {
			$("#txtKbnCode").prop('readonly', true);
		}
		
		// 新規の時はコードを非活性
		$("#txtCode").prop('readonly', false);
		if (isNew == "1") {
			$("#txtCode").prop('readonly', true);
		}
		
		// 新規の時は削除ボタンは非活性
		$("#btnDelete").prop('disabled', false);
		if (isNew == "1") {
			$("#btnDelete").prop('disabled', true);
		}
		
		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtKbnCode").val(mstDatas[i]["KbnCode"]);
			$("#txtCode").val(mstDatas[i]["Code"]);
			$("#txtKbnName").val(mstDatas[i]["KbnName"]);
			$("#txtKbnRyaku").val(mstDatas[i]["KbnRyaku"]);
			$("#txtGroupCode1").val(mstDatas[i]["GroupCode1"]);
			$("#txtGroupCode2").val(mstDatas[i]["GroupCode2"]);					
			$("#lblSaishuKoshinShainName").html(mstDatas[i]["SaishuKoshinShainName"]);
			$("#lblSaishuKoshinDate").html(mstDatas[i]["SaishuKoshinDate"]);
			$("#lblSaishuKoshinJikan").html(mstDatas[i]["SaishuKoshinJikan"]);
			
			$("#hdnKbnCode").val(mstDatas[i]["KbnCode"]);
			$("#hdnCode").val(mstDatas[i]["Code"]);
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
			getmstKubun();
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
	var txtKbnCode = $("#txtKbnCode").val();
	var hdnKbnCode = $("#hdnKbnCode").val();
	var txtCode    = $("#txtCode").val();
	var hdnCode    = $("#hdnCode").val();
		
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
				// 処理した営区分コードとコードで再読込
				$("#srhTxtKbnCode").val($("#txtKbnCode").val());
				$("#srhTxtCode").val($("#txtCode").val());
				getmstKubun();
			});
		});
	} else {
		// 更新モード
		proc("update",{}, function(data){

			if (txtKbnCode == hdnKbnCode && txtCode == hdnCode) {
				// 画面項目と隠し項目が同じ値 = 単純にデータ更新
				// 確認メッセージ
				if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
				// 登録処理の本体
				proc("update_",{}, function(data){
					// 完了メッセージ
					alert("データが正常に更新されました。");
					// 画面のクリアなど何かしらの処理
					// 処理した営業所コードで再読込
					$("#srhTxtKbnCode").val($("#txtKbnCode").val());
					$("#srhTxtCode").val($("#txtCode").val());
					getmstKubun();
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
					$("#srhTxtKbnCode").val($("#txtKbnCode").val());
					$("#srhTxtCode").val($("#txtCode").val());
					getmstKubun();
				});
			}
		});
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
	
	// mainAreaの表示状態を取得
	var display = $("#buttonArea").css("visibility");
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "visible") {
		// 該当の処理を呼び出す。
		onUpdate();
	}
}

//****************************************************************************
// ファンクションキーF2
//
//
//
//
//****************************************************************************
function onKeyEventF02() {
	
	//ボタンaの表示状態を取得
	var display = $("#buttonArea").css("visibility");
	var isNew = $("#hdnIsNew").val();
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "visible"&& isNew != "1") {
			// 該当の処理を呼び出す。
			onDelete();
	}
}
