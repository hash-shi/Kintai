//****************************************************************************
// getMstKanri
//
//
//
//
//****************************************************************************
function getMstKanri() {

	proc("search", {}, function(data) {

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
		
		// 管理コードは常に非活性
		$("#txtKanriCode").prop('readonly', true);
		
//		// 新規の時は営業所コードを非活性
//		$("#txtKanriCode").prop('readonly', false);
//		if (isNew == "1") {
//			$("#txtKanriCode").prop('readonly', true);
//		}
		
//		// 新規の時は削除ボタンは非活性
//		$("#btnDelete").prop('disabled', false);
//		if (isNew == "1") {
//			$("#btnDelete").prop('disabled', true);
//		}
		
		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtKanriCode").val(mstDatas[i]["KanriCode"]);
			$("#txtNendoKakuteiStatus").val(mstDatas[i]["NendoKakuteiStatus"]);
			$("#selNendoKakuteiStatus").val(mstDatas[i]["NendoKakuteiStatus"]);
			$("#txtGenzaishoriNengetsudo").val(mstDatas[i]["GenzaishoriNengetsudo"]);
			$("#txtKintaiKishuGetsudo").val(mstDatas[i]["KintaiKishuGetsudo"]);
			$("#txtKintaiGetsudoShimebi").val(mstDatas[i]["KintaiGetsudoShimebi"]);
			$("#txtKintaiKihonSagyoJikan").val(mstDatas[i]["KintaiKihonSagyoJikan"]);
			
			// $("#lblSaishuKoshinShainNO").html(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#lblSaishuKoshinShainName").html(mstDatas[i]["SaishuKoshinShainName"]);
			$("#lblSaishuKoshinDate").html(mstDatas[i]["SaishuKoshinDate"]);
			$("#lblSaishuKoshinJikan").html(mstDatas[i]["SaishuKoshinJikan"]);
			
			$("#hdnSaishuKoshinShainNO").val(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#hdnSaishuKoshinShainName").val(mstDatas[i]["SaishuKoshinShainName"]);
			$("#hdnSaishuKoshinDate").val(mstDatas[i]["SaishuKoshinDate"]);
			$("#hdnSaishuKoshinJikan").val(mstDatas[i]["SaishuKoshinJikan"]);
			
		}

	});

}

//****************************************************************************
// 入力値に合わせて部署区分をセット
// 
// 
//
//
//****************************************************************************
function setNendoKakuteiStatus() {
	var txtNendoKakuteiStatus = $("#txtNendoKakuteiStatus").val();
	$("#selNendoKakuteiStatus").val(txtNendoKakuteiStatus);
}

function setNendoKakuteiStatusbox() {
	var NendoKakuteiStatus = $("#selNendoKakuteiStatus").val();
	$("#txtNendoKakuteiStatus").val(NendoKakuteiStatus);
}

//****************************************************************************
// onUpdate
//
//
//
//
//****************************************************************************
function onUpdate() {
	
	proc("update", {}, function(data) {
		// 更新モード
		// 確認メッセージ
		if (!confirm("データの更新を行います。\nよろしいですか？")) { return; }
		
		// 登録処理の本体
		proc("update_", {}, function(data) {
			// 完了メッセージ
			alert("データが正常に更新されました。");
			// 画面のクリアなど何かしらの処理
			// 処理した営業所コードで再読込
			$("#srhTxtKanriCode").val($("#txtKanriCode").val());
			getMstKanri();
		});
		
	});
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
		var display = $("#buttonArea").css("display");
	
		// mainAreaが非表示(初期表示時)はスキップする。
		if (display == "block") {
			// 該当の処理を呼び出す。
			onUpdate();
	}
}
