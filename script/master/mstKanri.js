//****************************************************************************
// getMstKanri
//
//
//
//
//****************************************************************************
function getMstKanri() {

	proc("search", {srhKanriCode: $("#srhKanriCode").val()}, function(data) {

		if (data == undefined) { return; }
		if (data["contents"] == undefined) { return; }

		var contents = data["contents"];
		console.log("data:", data);
		console.log("contents:", data.contents);
		console.log("mstDatas:", data.contents?.mstDatas);
		if (contents["mstDatas"] == undefined) { return; }
		

		// 取得した値の格納
		//var isNew = contents["isNew"];
		//$("#hdnIsNew").val(isNew);

		// mainAreaを表示する。
		$("#mainArea").css("visibility", "visible");
		$("#buttonArea").css("visibility", "visible");

		// 既に背景色が設定されている場合は一旦削除
		$("#mainArea").removeClass('ins');
		$("#mainArea").removeClass('upd');


		// 背景色を設定
		$("#mainArea").addClass("upd");


		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtKanriCode").val(mstDatas[i]["KanriCode"]);
			$("#txtNendoKakuteiStatus").val(mstDatas[i]["NendoKakuteiStatus"]);
			$("#txtGenzaishoriNengetsudo").val(mstDatas[i]["GenzaishoriNengetsudo"]);
			$("#txtKintaiKishuGetsudo").val(mstDatas[i]["KintaiKishuGetsudo"]);
			$("#txtKintaiGetsudoShimebi").val(mstDatas[i]["KintaiGetsudoShimebi"]);
			$("#txtKintaiKihonSagyoJikan").val(mstDatas[i]["KintaiKihonSagyoJikan"]);
			
			$("#hdnSaishuKoshinShainNO").val(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#hdnSaishuKoshinShainName").val(mstDatas[i]["SaishuKoshinShainName"]);
			$("#hdnSaishuKoshinDate").val(mstDatas[i]["SaishuKoshinDate"]);
			$("#hdnSaishuKoshinJikan").val(mstDatas[i]["SaishuKoshinJikan"]);
			// $("#lblSaishuKoshinShainNO").html(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#lblSaishuKoshinShainName").html(mstDatas[i]["SaishuKoshinShainName"]);
			$("#lblSaishuKoshinDate").html(mstDatas[i]["SaishuKoshinDate"]);
			$("#lblSaishuKoshinJikan").html(mstDatas[i]["SaishuKoshinJikan"]);

		}

	});

}

