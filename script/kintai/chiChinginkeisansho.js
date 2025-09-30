let chiChinginkeisanshoResultAll = [];
let shinseiKingaku01 = 0;
let shinseiKingaku02 = 0;

let yoteiList = [];
let chinginKubunList = [];


/*
*
* 社員名フォーカスアウト時のフォーマット編集処理
*
*/
function onSearchShainName(){
	//もともとの社員NOを保持
	let wkTxtShainNO = $("#srhTxtShainNO").val();

	//0埋めした社員NOをセット
	$("#srhTxtShainNO").val(right("0000" + $("#srhTxtShainNO").val(), 4));
	//0埋めした社員NOで社員名を取得
	getShainName('srhTxtShainNO', 'lblShainName');
	//社員名を取得できなかったら、社員NO項目を戻す
	if($("#lblShainName").val() == ""){
		$("#srhTxtShainNO").val(wkTxtShainNO);
	}
}
function right(str, n) {
    l = str.length;
    if (n > l) n = l;
    return str.substring(l - n, l);
}

/*
*
* 賃金計算書検索
*
*/
function onSearchChiChinginkeisansho(){
	let honshaKakuteizumiFlg = false;

	//検索結果表示
	proc("search", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		//更新処理に備え、検索条件を保持
		$("#txtTaishoYM").val($("#srhTxtTaishoYM").val());
		$("#txtShainNO").val($("#srhTxtShainNO").val());

		//検索結果があれば入力項目表示
		$("#nyuryokuArea").css("visibility", "");

		//検索データ表示
		let chinginkeisanshoResult			= contents["result"]["chinginkeisanshoArea"];
		let tokubetsuNyuryokuResult			= contents["result"]["tokubetsuNyuryokuArea"];
		let shukeiResult			= contents["result"]["shukeiArea"];
		displaySearchOrRecalcData(chinginkeisanshoResult, tokubetsuNyuryokuResult, shukeiResult);

		if(contents["result"]["shukeiArea"].KakuteiKbn == "03"){
			honshaKakuteizumiFlg = true;
		}


		//本社確定済みチェック　検索結果表示するが更新は不可
		proc("honshaKakuteizumiCheck", {}, function(data){

			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
			
			let contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
			
			let result			= contents["result"];
			if(result == "1"){
				honshaKakuteizumiFlg = true;
			}
		});


		if(honshaKakuteizumiFlg){
			alert("本社確定済みのため処理できません。");
			if (!$("#nyuryokuArea").hasClass("nom")) {
				$("#nyuryokuArea").addClass("nom");
			}
			if ($("#nyuryokuArea").hasClass("ins")) {
				$("#nyuryokuArea").removeClass("ins");
			}
			if ($("#nyuryokuArea").hasClass("upd")) {
				$("#nyuryokuArea").removeClass("upd");
			}
			$("#buttonArea").css("visibility", "hidden");
		}
		else{
			//取得した更新日付・時間が空の時、新規登録として背景色を変更する
			if($("#txtKihonSaishuKoshinDate").val() == "" && $("#txtKihonSaishuKoshinJikan").val() == ""){
				if (!$("#nyuryokuArea").hasClass("ins")) {
					$("#nyuryokuArea").addClass("ins");
				}
				if ($("#nyuryokuArea").hasClass("upd")) {
					$("#nyuryokuArea").removeClass("upd");
				}
				if ($("#nyuryokuArea").hasClass("nom")) {
					$("#nyuryokuArea").removeClass("nom");
				}
			}
			else{
				if (!$("#nyuryokuArea").hasClass("upd")) {
					$("#nyuryokuArea").addClass("upd");
				}
				if ($("#nyuryokuArea").hasClass("ins")) {
					$("#nyuryokuArea").removeClass("ins");
				}
				if ($("#nyuryokuArea").hasClass("nom")) {
					$("#nyuryokuArea").removeClass("nom");
				}
			}
			$("#buttonArea").css("visibility", "");
			if ($("#nyuryokuArea").hasClass("upd")) {
				//更新モードの時、削除ボタン活性化
				document.getElementById("btnDelete").disabled = false;
			}
			else{
				//更新モード以外の時、削除ボタン非活性化
				document.getElementById("btnDelete").disabled = true;
			}
		}
	});
	
	
	
}

/*
*
* 検索または再表示のデータを表示
*
*/
function displaySearchOrRecalcData(chinginkeisanshoResult, tokubetsuNyuryokuResult, shukeiResult){

	chiChinginkeisanshoResultAll = chinginkeisanshoResult;
	onDisplayNyuryokuArea(true);

	//勤務開始・終了時間、実働時間表示
	$("#lblKinmuKaishi").text(tokubetsuNyuryokuResult.kinmuKaishi);
	$("#lblKinmuShuryo").text(tokubetsuNyuryokuResult.kinmuShuryo);
	$("#lblJitsudojikan").text(tokubetsuNyuryokuResult.jitsudojikan);

	//集計エリア表示
	$("#lblShinseinissu01").text(Number(shukeiResult.ShinseiNissu01??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu02").text(Number(shukeiResult.ShinseiNissu02??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu03").text(Number(shukeiResult.ShinseiNissu03??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu04").text(Number(shukeiResult.ShinseiNissu04??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu05").text(Number(shukeiResult.ShinseiNissu05??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu06").text(Number(shukeiResult.ShinseiNissu06??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu07").text(Number(shukeiResult.ShinseiNissu07??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu08").text(Number(shukeiResult.ShinseiNissu08??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu09").text(Number(shukeiResult.ShinseiNissu09??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu10").text(Number(shukeiResult.ShinseiNissu10??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseinissu11").text(Number(shukeiResult.ShinseiNissu11??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));

	$("#lblShinseinissukyujitsu").text(Number(shukeiResult.ShinseiNissuKyujitsu??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));

	$("#lblShinseijikan01").text(Number(shukeiResult.ShinseiJikan01??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan02").text(Number(shukeiResult.ShinseiJikan02??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan03").text(Number(shukeiResult.ShinseiJikan03??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan04").text(Number(shukeiResult.ShinseiJikan04??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan05").text(Number(shukeiResult.ShinseiJikan05??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan06").text(Number(shukeiResult.ShinseiJikan06??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan07").text(Number(shukeiResult.ShinseiJikan07??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan08").text(Number(shukeiResult.ShinseiJikan08??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan09").text(Number(shukeiResult.ShinseiJikan09??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan10").text(Number(shukeiResult.ShinseiJikan10??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseijikan11").text(Number(shukeiResult.ShinseiJikan11??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));

	$("#lblShinseitanka01").text(Number(shukeiResult.ShinseiTanka01??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka02").text(Number(shukeiResult.ShinseiTanka02??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka03").text(Number(shukeiResult.ShinseiTanka03??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka04").text(Number(shukeiResult.ShinseiTanka04??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka05").text(Number(shukeiResult.ShinseiTanka05??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka06").text(Number(shukeiResult.ShinseiTanka06??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka07").text(Number(shukeiResult.ShinseiTanka07??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka08").text(Number(shukeiResult.ShinseiTanka08??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka09").text(Number(shukeiResult.ShinseiTanka09??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka10").text(Number(shukeiResult.ShinseiTanka10??0).toLocaleString("ja-JP"));
	$("#lblShinseitanka11").text(Number(shukeiResult.ShinseiTanka11??0).toLocaleString("ja-JP"));

	$("#lblShinseikingakugoukei01").text(Number(shukeiResult.ShinseiKingakuGoukei01??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei02").text(Number(shukeiResult.ShinseiKingakuGoukei02??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei03").text(Number(shukeiResult.ShinseiKingakuGoukei03??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei04").text(Number(shukeiResult.ShinseiKingakuGoukei04??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei05").text(Number(shukeiResult.ShinseiKingakuGoukei05??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei06").text(Number(shukeiResult.ShinseiKingakuGoukei06??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei07").text(Number(shukeiResult.ShinseiKingakuGoukei07??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei08").text(Number(shukeiResult.ShinseiKingakuGoukei08??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei09").text(Number(shukeiResult.ShinseiKingakuGoukei09??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei10").text(Number(shukeiResult.ShinseiKingakuGoukei10??0).toLocaleString("ja-JP"));
	$("#lblShinseikingakugoukei11").text(Number(shukeiResult.ShinseiKingakuGoukei11??0).toLocaleString("ja-JP"));

	$("#txtTokkijiko").val(shukeiResult.TokkiJiko);

	$("#lblShinseinisuugoukei").text(Number(shukeiResult.ShinseiNisuuGoukei??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
	$("#lblShinseijikangoukei").text(Number(shukeiResult.ShinseiJikanGoukei??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
	$("#lblShinseikingakugoukeigoukei").text(Number(shukeiResult.ShinseiKingakuGoukeiGoukei??0).toLocaleString("ja-JP"));

}

/*
*
* 検索結果を表示
*
*/
function onDisplayNyuryokuArea(firstHalfFlg){
	// 検索結果エリアをクリアする
	$("#kihonNyuryokuArea").children().remove();

	let sinseiKubunList = [];
	proc("getDDL", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];

		for(let record of result){
			sinseiKubunList.push(record);
		}
	});
	
	let focusIndex = -1;
	for(let i = 0; i < chiChinginkeisanshoResultAll.length; i++){
		let record = chiChinginkeisanshoResultAll[i];
		let taishoNengappi = record["txtTaishoNengappi"];
		let taishoGetsu = ("00" + record["txtTaishoGetsu"]).slice(-2);
		let taishoBi = ("00" + record["txtTaishoBi"]).slice(-2);
		let yobiKbn = record["txtYobiKbn"];

		let shusshaJi =		record["numShusshaJi"];
		let shusshaFun =	record["numShusshaFun"];
		let taishaJi =		record["numTaishaJi"];
		let taishaFun =		record["numTaishaFun"];
		let jitsudoJikan =	record["numJitsudoJikan"];

		let chinginShinseiKbn1 = record["selChinginShinseiKbn1"];
		let chinginShinseiJikan1 = record["numChinginShinseiJikan1"];
		let chinginShinseiKbn2 = record["selChinginShinseiKbn2"];
		let chinginShinseiJikan2 = record["numChinginShinseiJikan2"];
		let chinginShinseiKbn3 = record["selChinginShinseiKbn3"];
		let chinginShinseiJikan3 = record["numChinginShinseiJikan3"];
		
		let meisaiSaishuKoshinDate = record["txtMeisaiSaishuKoshinDate"];
		let meisaiSaishuKoshinJikan = record["txtMeisaiSaishuKoshinJikan"];
		
		//曜日項目の表示色変更
		let yobiColorClass = "";
		if(yobiKbn == "土"){
			yobiColorClass = "sat";
		}
		else if(yobiKbn == "日"){
			yobiColorClass = "sun";
		}
		
		

		
		//申請区分1のセレクトボックス
		let sinsei1SelectBox = "";
		sinsei1SelectBox += 	"<select name=\"selChinginShinseiKbn1" + i + "\" id=\"selChinginShinseiKbn1" + i + "\" value=\"" + chinginShinseiKbn1 + "\"  onchange=\"setShukkinBo('selChinginShinseiKbn1', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei1SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(chinginShinseiKbn1 == sinseiKubunRecord["Code"]){
				sinsei1SelectBox += 		"selected";
			}
			sinsei1SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
		}
		
		//申請区分2のセレクトボックス
		let sinsei2SelectBox = "";
		sinsei2SelectBox += 	"<select name=\"selChinginShinseiKbn2" + i + "\" id=\"selChinginShinseiKbn2" + i + "\" value=\"" + chinginShinseiKbn2 + "\"  onchange=\"setShukkinBo('selChinginShinseiKbn2', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei2SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(chinginShinseiKbn2 == sinseiKubunRecord["Code"]){
				sinsei2SelectBox += 		"selected";
			}
			sinsei2SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
		}
		
		//申請区分3のセレクトボックス
		let sinsei3SelectBox = "";
		sinsei3SelectBox += 	"<select name=\"selChinginShinseiKbn3" + i + "\" id=\"selChinginShinseiKbn3" + i + "\" value=\"" + chinginShinseiKbn3 + "\"  onchange=\"setShukkinBo('selChinginShinseiKbn3', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei3SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(chinginShinseiKbn3 == sinseiKubunRecord["Code"]){
				sinsei3SelectBox += 		"selected";
			}
			sinsei3SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
		}


		let kihonNyuryokuAreaHtml = "";
		if(
			(firstHalfFlg == true && record["txtTaishoGetsu"] == chiChinginkeisanshoResultAll[0]["txtTaishoGetsu"]) ||
			(firstHalfFlg != true && record["txtTaishoGetsu"] != chiChinginkeisanshoResultAll[0]["txtTaishoGetsu"])
		){
			//フォーカス処理に備え、出社時が空欄の最初の行を取得
			if(focusIndex == -1 && shusshaJi == ""){
				focusIndex = i;
			}
			
			kihonNyuryokuAreaHtml =
				"<tr>" +
					"<input type=\"hidden\" name=\"txtTaishoNengappi" + i + "\" id=\"txtTaishoNengappi" + i + "\" value=\"" + taishoNengappi + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinDate" + i + "\" id=\"txtMeisaiSaishuKoshinDate" + i + "\" value=\"" + meisaiSaishuKoshinDate + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinJikan" + i + "\" id=\"txtMeisaiSaishuKoshinJikan" + i + "\" value=\"" + meisaiSaishuKoshinJikan + "\">" +
					
					"<td class=\"value center\"><a >" + taishoGetsu + "</a></td>" +
					"<input type=\"hidden\" name=\"txtTaishoGetsu" + i + "\" id=\"txtTaishoGetsu" + i + "\" value=\"" + taishoGetsu + "\">" +
					"<td class=\"value center\"><a >" + taishoBi + "</a></td>" +
					"<input type=\"hidden\" name=\"txtTaishoBi" + i + "\" id=\"txtTaishoBi" + i + "\" value=\"" + taishoBi + "\">" +
					"<td class=\"value center\"><a class=\"" + yobiColorClass + "\">" + yobiKbn + "</a></td>" +
	
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"numShusshaJi" + i + "\" id=\"numShusshaJi" + i + "\"  value=\"" + shusshaJi + "\"  onchange=\"changeShusshaJi(" + i + ");setShukkinBo('numShusshaJi', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"numShusshaFun" + i + "\" id=\"numShusshaFun" + i + "\"  value=\"" + shusshaFun + "\"  onchange=\"setShukkinBo('numShusshaFun', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<a >-</a>" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"numTaishaJi" + i + "\" id=\"numTaishaJi" + i + "\"  value=\"" + taishaJi + "\"  onchange=\"setShukkinBo('numTaishaJi', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"numTaishaFun" + i + "\" id=\"numTaishaFun" + i + "\"  value=\"" + taishaFun + "\"  onchange=\"setShukkinBo('numTaishaFun', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"numJitsudoJikan" + i + "\" id=\"numJitsudoJikan" + i + "\"  value=\"" + jitsudoJikan + "\"  onchange=\"setShukkinBo('numJitsudoJikan', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei1SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"numChinginShinseiJikan1" + i + "\" id=\"numChinginShinseiJikan1" + i + "\"  value=\"" + chinginShinseiJikan1 + "\"  onchange=\"setShukkinBo('numChinginShinseiJikan1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei2SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"numChinginShinseiJikan2" + i + "\" id=\"numChinginShinseiJikan2" + i + "\"  value=\"" + chinginShinseiJikan2 + "\"  onchange=\"setShukkinBo('numChinginShinseiJikan2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei3SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"numChinginShinseiJikan3" + i + "\" id=\"numChinginShinseiJikan3" + i + "\"  value=\"" + chinginShinseiJikan3 + "\"  onchange=\"setShukkinBo('numChinginShinseiJikan3', " + i + ");\" >" + 
					"</td>" +
				"</tr>";
		}
		else{
			kihonNyuryokuAreaHtml =
					"<input type=\"hidden\" name=\"txtTaishoNengappi" + i + "\" id=\"txtTaishoNengappi" + i + "\" value=\"" + taishoNengappi + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinDate" + i + "\" id=\"txtMeisaiSaishuKoshinDate" + i + "\" value=\"" + meisaiSaishuKoshinDate + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinJikan" + i + "\" id=\"txtMeisaiSaishuKoshinJikan" + i + "\" value=\"" + meisaiSaishuKoshinJikan + "\">" +
					
					"<input type=\"hidden\" name=\"txtTaishoGetsu" + i + "\" id=\"txtTaishoGetsu" + i + "\" value=\"" + taishoGetsu + "\">" +
					"<input type=\"hidden\" name=\"txtTaishoBi" + i + "\" id=\"txtTaishoBi" + i + "\" value=\"" + taishoBi + "\">" +
					"<input type=\"hidden\" name=\"numShusshaJi" + i + "\" id=\"numShusshaJi" + i + "\"  value=\"" + shusshaJi + "\" >" + 
					"<input type=\"hidden\" name=\"numShusshaFun" + i + "\" id=\"numShusshaFun" + i + "\"  value=\"" + shusshaFun + "\" >" +
					"<input type=\"hidden\" name=\"numTaishaJi" + i + "\" id=\"numTaishaJi" + i + "\"  value=\"" + taishaJi + "\" >" +
					"<input type=\"hidden\" name=\"numTaishaFun" + i + "\" id=\"numTaishaFun" + i + "\"  value=\"" + taishaFun + "\" >" +
					"<input type=\"hidden\" name=\"numJitsudoJikan" + i + "\" id=\"numJitsudoJikan" + i + "\"  value=\"" + jitsudoJikan + "\" >" +
					"<input type=\"hidden\" name=\"selChinginShinseiKbn1" + i + "\" id=\"selChinginShinseiKbn1" + i + "\"  value=\"" + chinginShinseiKbn1 + "\" >" + 
					"<input type=\"hidden\" name=\"numChinginShinseiJikan1" + i + "\" id=\"numChinginShinseiJikan1" + i + "\"  value=\"" + chinginShinseiJikan1 + "\" >" + 
					"<input type=\"hidden\" name=\"selChinginShinseiKbn2" + i + "\" id=\"selChinginShinseiKbn2" + i + "\"  value=\"" + chinginShinseiKbn2 + "\" >" + 
					"<input type=\"hidden\" name=\"numChinginShinseiJikan2" + i + "\" id=\"numChinginShinseiJikan2" + i + "\"  value=\"" + chinginShinseiJikan2 + "\" >" + 
					"<input type=\"hidden\" name=\"selChinginShinseiKbn3" + i + "\" id=\"selChinginShinseiKbn3" + i + "\"  value=\"" + chinginShinseiKbn3 + "\" >" + 
					"<input type=\"hidden\" name=\"numChinginShinseiJikan3" + i + "\" id=\"numChinginShinseiJikan3" + i + "\"  value=\"" + chinginShinseiJikan3 + "\" >";
		}

		$("#kihonNyuryokuArea").append(kihonNyuryokuAreaHtml);
	}
	$("#txtKihonSaishuKoshinDate").val(chiChinginkeisanshoResultAll[0]["txtKihonSaishuKoshinDate"]);
	$("#txtKihonSaishuKoshinJikan").val(chiChinginkeisanshoResultAll[0]["txtKihonSaishuKoshinJikan"]);


	//前・次一覧ボタンの活性変更
	if(firstHalfFlg == true){
		document.getElementById("btnFirstHalf").disabled = true;
		document.getElementById("btnSecondHalf").disabled = false;
		if(focusIndex != -1){
			//一覧表示時、出社時が空欄の最初の行をフォーカス
			$("#numShusshaJi" + String(focusIndex)).focus();
		}
		else{
			//一覧表示時、最初の行をフォーカス
			$("#numShusshaJi0").focus();
		}
	}
	else{
		document.getElementById("btnFirstHalf").disabled = false;
		document.getElementById("btnSecondHalf").disabled = true;
		if(focusIndex != -1){
			//一覧表示時、出社時が空欄の最初の行をフォーカス
			$("#numShusshaJi" + String(focusIndex)).focus();
		}
		else{
			//一覧表示時、最後の行をフォーカス
			$("#numShusshaJi" + (chiChinginkeisanshoResultAll.length - 1)).focus();
		}
	}

}

function changeShusshaJi(nowRow){
	//出社時をクリアしたら、同行の項目をクリア
	if($("#numShusshaJi" + nowRow).val() == ""){
		fieldName = "numShusshaJi";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numShusshaFun";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numTaishaJi";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numTaishaFun";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numJitsudoJikan";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "selChinginShinseiKbn1";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numChinginShinseiJikan1";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "selChinginShinseiKbn2";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numChinginShinseiJikan2";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "selChinginShinseiKbn3";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numChinginShinseiJikan3";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
	}
	
}

/*
*
* 入力した値を内部的な配列に取得
*
*/
function setShukkinBo(fieldName, nowRow){
	chiChinginkeisanshoResultAll[nowRow][fieldName] = $("#" + fieldName + nowRow).val();
}

/*
*
* 開始時分、終了時分から時間を計算
*
*/
function calcJitsudoJikan(nowRow){
	let checkIfNumber = /^[0-9]+$/;

	let kaishiJi = chiChinginkeisanshoResultAll[nowRow]["numShusshaJi"];
	let kaishiFun = chiChinginkeisanshoResultAll[nowRow]["numShusshaFun"];
	let shuryoJi = chiChinginkeisanshoResultAll[nowRow]["numTaishaJi"];
	let shuryoFun = chiChinginkeisanshoResultAll[nowRow]["numTaishaFun"];
	
	
	//開始時分、終了時分が全て数字で入力済みの時のみ自動計算する
	if(
		kaishiJi != "" && checkIfNumber.test(kaishiJi) &&
		kaishiFun != "" && checkIfNumber.test(kaishiFun) &&
		shuryoJi != "" && checkIfNumber.test(shuryoJi) &&
		shuryoFun != "" && checkIfNumber.test(shuryoFun) 
	){
		let kaishiJiNum = Number(kaishiJi);
		let kaishiFunNum = Number(kaishiFun);
		let shuryoJiNum = Number(shuryoJi);
		let shuryoFunNum = Number(shuryoFun);
		//すべて時や分に合う場合のみ自動計算する
		if(
			kaishiJiNum < 24 &&
			kaishiFunNum < 60 &&
			shuryoJiNum < 24 &&
			shuryoFunNum < 60
		){
			//開始時分＞終了時分の場合、24:00を回ったと判定する
			if((kaishiJiNum * 60 + kaishiFunNum) > (shuryoJiNum * 60 + shuryoFunNum)){
				shuryoJiNum = shuryoJiNum + 24;
			}
			//1.分単位で時間計算
			let jikanWk = (shuryoJiNum * 60 + shuryoFunNum) - (kaishiJiNum * 60 + kaishiFunNum);
			//2.勤怠申請時間の時部分を計算
			let jikanJi = Math.floor(jikanWk / 60);
			//3.勤怠申請時間の分部分を計算
			let jikanFun = jikanWk % 60;
			//4.実際に項目に表示する値を計算 小数点以下部分は、100分率表示とする(30分を0.5時間とする)
			jikanFun = Math.trunc((jikanFun / 60) * 100);
			//12:00～13:00が含まれていたら、その分を除く
			if(
				(
					((kaishiJiNum * 60 + kaishiFunNum) <= 12*60) &&	//開始時が12:00以前
					((shuryoJiNum * 60 + shuryoFunNum) >= 13*60)	//終了時が13:00以降
				) ||
				(
					(shuryoJiNum * 60 + shuryoFunNum) >= (13+24)*60	//終了時が24:00を回ったうえで13:00以降
				)
			){
				jikanJi = jikanJi - 1;
			}
			let jikanDisp = String(jikanJi) + "." + (("00" + String(jikanFun)).slice(-2));
			
			chiChinginkeisanshoResultAll[nowRow]["numJitsudoJikan"] = jikanDisp;
			$("#numJitsudoJikan" + nowRow).val(jikanDisp);
		}
	}
}

/*
*
* 削除処理呼び出し
*
*/
function onKeyEventF02(){
	if($("#buttonArea").css("visibility") != "hidden"){
		onDelete();
	}
}
function onDelete(){
	if ($("#nyuryokuArea").hasClass("upd")) {
		proc("delete", {}, function(data){
			// 確認メッセージ
			if(!confirm("データの削除を行います。\nよろしいですか？")) { return; }
			//削除処理呼び出し
			proc("delete_", {}, function(data){
		
				if (data == undefined){ return; }
				if (data["contents"] == undefined){ return; }
				
				let contents		= data["contents"];
				if (contents["result"] == undefined){ return; }
				
				let result			= contents["result"];
		
				if(result == true){
					alert("正常に削除しました。");
					//再検索する
					//更新処理に備え、検索条件を保持
					$("#srhTxtTaishoYM").val($("#txtTaishoYM").val());
					$("#srhTxtShainNO").val($("#txtShainNO").val());
					onSearchShainName();//社員名再取得

					//検索結果が0の時のため、画面非表示
					$("#nyuryokuArea").css("visibility", "hidden");
					$("#buttonArea").css("visibility", "hidden");
					onSearchChiChinginkeisansho();
				}
				else{
					alert("このデータはすでに、別のユーザーに更新されています。\r\nもう一度データを確認してください。");
				}
			});
		});
	}
}

/*
*
* 再表示処理呼び出し
*
*/
function onKeyEventF08(){
	if($("#buttonArea").css("visibility") != "hidden"){
		onRecalc();
	}
}
function onRecalc(){
	//更新処理呼び出し
	proc("recalc", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		//更新処理に備え、検索条件を保持
		$("#txtTaishoYM").val($("#srhTxtTaishoYM").val());
		$("#txtShainNO").val($("#srhTxtShainNO").val());

		//検索結果があれば入力項目表示
		$("#nyuryokuArea").css("visibility", "");

		//検索データ表示
		let chinginkeisanshoResult			= contents["result"]["chinginkeisanshoArea"];
		let tokubetsuNyuryokuResult			= contents["result"]["tokubetsuNyuryokuArea"];
		let shukeiResult			= contents["result"]["shukeiArea"];

		//実際には更新されていないので、最終更新日・更新時間を戻す
		for(let i = 0; i < chiChinginkeisanshoResultAll.length; i++){
			chinginkeisanshoResult[i]["txtMeisaiSaishuKoshinDate"] = chiChinginkeisanshoResultAll[i]["txtMeisaiSaishuKoshinDate"];
			chinginkeisanshoResult[i]["txtMeisaiSaishuKoshinJikan"] = chiChinginkeisanshoResultAll[i]["txtMeisaiSaishuKoshinJikan"];
			chinginkeisanshoResult[i]["txtKihonSaishuKoshinDate"] = chiChinginkeisanshoResultAll[i]["txtKihonSaishuKoshinDate"];
			chinginkeisanshoResult[i]["txtKihonSaishuKoshinJikan"] = chiChinginkeisanshoResultAll[i]["txtKihonSaishuKoshinJikan"];
		}

		displaySearchOrRecalcData(chinginkeisanshoResult, tokubetsuNyuryokuResult, shukeiResult);

	});
}

/*
*
* 登録更新処理呼び出し
*
*/
function onKeyEventF09(){
	if($("#buttonArea").css("visibility") != "hidden"){
		onUpdate();
	}
}
function onUpdate(){
	proc("update", {}, function(data){
		// 確認メッセージ
		if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
		//更新処理呼び出し
		proc("update_", {}, function(data){
	
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
			
			let contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
			
			let result			= contents["result"];
	
			if((result == 1) || (result == 2)){
				if(result == 1){
					alert("正常に登録しました。");
				}
				if(result == 2){
					alert("正常に更新しました。");
				}
				//再検索する
				//更新処理に備え、検索条件を保持
				$("#srhTxtTaishoYM").val($("#txtTaishoYM").val());
				$("#srhTxtShainNO").val($("#txtShainNO").val());
				onSearchShainName();//社員名再取得
	
				//検索結果が0の時のため、画面非表示
				$("#nyuryokuArea").css("visibility", "hidden");
				$("#buttonArea").css("visibility", "hidden");
				onSearchChiChinginkeisansho();
			}
			else{
				alert("このデータはすでに、別のユーザーに更新されています。\r\nもう一度データを確認してください。");
			}
		});
	});
}
