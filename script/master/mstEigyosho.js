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
		
		// mainAreaを表示する。
		$("#mainArea").css("visibility", "visible");
		$("#buttonArea").css("visibility", "visible");
		
		// 既に背景色が設定されている場合は一旦削除
		$("#mainArea").removeClass('ins');
		$("#mainArea").removeClass('upd');
		// 背景色を設定
		if (contents["isNew"]) {
			$("#mainArea").addClass("ins");
		} else {
			$("#mainArea").addClass("upd");
		}
		
		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtEigyoshoCode").val(mstDatas[i]["EigyoshoCode"]);
			$("#txtEigyoshoName").val(mstDatas[i]["EigyoshoName"]);
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
