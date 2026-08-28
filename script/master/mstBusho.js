//****************************************************************************
// getMstBusho
//
//
//
//
//****************************************************************************
function getMstBusho(){

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
		// 20260228-初期フォーカス位置の変更
		// $("#txtBushoName").focus();
		$("#txtBushoCode").focus();
		// 20260228-初期フォーカス位置の変更
		setTimeout(function(){
			$("#txtBushoCode").select()
			//20260813-全選択追加
		});

		
		// 既に背景色が設定されている場合は一旦削除
		$("#mainArea").removeClass('ins');
		$("#mainArea").removeClass('upd');
		// 背景色を設定
		if (isNew == "1") {
			$("#mainArea").addClass("ins");
		} else {
			$("#mainArea").addClass("upd");
		}
		
		// 新規の時は部署コードを非活性
		$("#txtBushoCode").prop('readonly', false);
		if (isNew == "1") {
			$("#txtBushoCode").prop('readonly', true);
		}
		
		// 新規の時は削除ボタンは非活性
		$("#btnDelete").prop('disabled', false);
		if (isNew == "1") {
			$("#btnDelete").prop('disabled', true);
		}
		
		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtBushoCode").val(mstDatas[i]["BushoCode"]);
			$("#txtBushoName").val(mstDatas[i]["BushoName"]);
			$("#txtBushoKbn").val(mstDatas[i]["BushoKbn"]);
			$("#selBushoKbnName").val(mstDatas[i]["BushoKbn"]);
			$("#txtEigyoshoCode").val(mstDatas[i]["EigyoshoCode"]);
			// $("#lblSaishuKoshinShainNO").html(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#lblSaishuKoshinShainName").html(mstDatas[i]["SaishuKoshinShainName"]);
			$("#lblSaishuKoshinDate").html(mstDatas[i]["SaishuKoshinDate"]);
			$("#lblSaishuKoshinJikan").html(mstDatas[i]["SaishuKoshinJikan"]);
			
			$("#hdnBushoCode").val(mstDatas[i]["BushoCode"]);
			$("#hdnSaishuKoshinShainNO").val(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#hdnSaishuKoshinShainName").val(mstDatas[i]["SaishuKoshinShainName"]);
			$("#hdnSaishuKoshinDate").val(mstDatas[i]["SaishuKoshinDate"]);
			$("#hdnSaishuKoshinJikan").val(mstDatas[i]["SaishuKoshinJikan"]);
		}
		
		// 営業所名の取得
		getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');
		
	});

}

//****************************************************************************
// 入力値に合わせて部署区分をセット
// 
// 
//
//
//****************************************************************************
function  setSelBushoKbnName() {
	var txtBushoKbn = $("#txtBushoKbn").val();
	$("#selBushoKbnName").val(txtBushoKbn);
}

function setTxtBushoKbn() {
	var selBushoKbnName = $("#selBushoKbnName").val();
	$("#txtBushoKbn").val(selBushoKbnName);
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
			//getMstBusho();
			
			//20260827 初期表示に変更
			//movContents('mstBusho');
			onClear();
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
	var txtBushoCode = $("#txtBushoCode").val();
	var hdnBushoCode = $("#hdnBushoCode").val();
		
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
				//$("#srhTxtBushoCode").val($("#txtBushoCode").val());
				//getMstBusho();
				
				//20260815 初期表示に変更
				//movContents('mstBusho');
				onClear();
			});
		});
	} else {
		// 更新モード
		proc("update",{}, function(data){

			if (txtBushoCode == hdnBushoCode) {
				// 画面項目と隠し項目が同じ値 = 単純にデータ更新
				// 確認メッセージ
				if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
				// 登録処理の本体
				proc("update_",{}, function(data){
					// 完了メッセージ
					alert("データが正常に更新されました。");
					// 画面のクリアなど何かしらの処理
					// 処理した営業所コードで再読込
					//$("#srhTxtBushoCode").val($("#txtBushoCode").val());
					//getMstBusho();
					
					//20260815 初期表示に変更
					//movContents('mstBusho');
					onClear();
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
					//$("#srhTxtBushoCode").val($("#txtBushoCode").val());
					//getMstBusho();
					
					//20260815 初期表示に変更
					//movContents('mstBusho');
					onClear();
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

//****************************************************************************
// onClear
//
//
//
//
//****************************************************************************
function onClear() {
	
	// 処理した部署コードを格納
	$("#srhTxtBushoCode").val($("#txtBushoCode").val());
	
	// mainAreaを非表示する。
	$("#mainArea").css("visibility", "hidden");
	// buttonAreaを非表示する。
	$("#buttonArea").css("visibility", "hidden");
	
	// 初期フォーカス位置の変更
	$("#srhTxtBushoCode").focus();
	setTimeout(function(){
		$("#srhTxtBushoCode").select();
	})
}