let chiChinginkeisanshoResultAll = [];
let shinseiKingaku01 = 0;
let shinseiKingaku02 = 0;

let yoteiList = [];
let chinginKubunList = [];


/*
*
* 対象年月フォーカスアウト時のフォーマット編集処理
*
*/
function getTaishoYMFormat(){
	let strReplacing = $("#srhTxtTaishoYM").val();
	let strReplaced = "";
	//全角半角変換
	strReplacing = strReplacing.replace(/[０-９]/g, function(s) {
		return String.fromCharCode(s.charCodeAt(0) - 0xFEE0);
	});
	strReplacing = strReplacing.replace("／","/");

	let checkIfNumber = /^[0-9]+$/;
	
	// 日付のフォーマットを変換する
	// 全て数字で6文字の場合、YYYYMMとする
	if(strReplacing.length == 6 && checkIfNumber.test(strReplacing)) {
		strReplaced += strReplacing.substring(0, 4);
		strReplaced += "/";
		strReplaced += strReplacing.substring(4, 6);
	}
	// 全て数字で5文字の場合、YYYYMとする
	else if(strReplacing.length == 5 && checkIfNumber.test(strReplacing)) {
		strReplaced += strReplacing.substring(0, 4);
		strReplaced += "/0";
		strReplaced += strReplacing.substring(4, 5);
	}
	// それ以外の場合、そのまま
	else {
		strReplaced = strReplacing;
	}

	$("#srhTxtTaishoYM").val(strReplaced);
}

/*
*
* 社員名フォーカスアウト時のフォーマット編集処理
*
*/
function getShainNOFormat(){
	//もともとの社員NOを保持
	let wkTxtShainNO = $("#srhTxtShainNO").val();

	//作業用隠し項目に、0埋めした社員NOをセット
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
	document.getElementById("btnDelete").disabled = true;
	document.getElementById("btnRecalc").disabled = true;
	document.getElementById("btnUpdate").disabled = true;

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

		let chinginkeisanshoResult			= contents["result"]["chinginkeisanshoArea"];
		chiChinginkeisanshoResultAll = chinginkeisanshoResult;
		onDisplayNyuryokuArea(true);

		//勤務開始・終了時間、実働時間表示
		let tokubetsuNyuryokuResult			= contents["result"]["tokubetsuNyuryokuArea"];
		$("#kinmuKaishi").text(tokubetsuNyuryokuResult.kinmuKaishi);
		$("#kinmuShuryo").text(tokubetsuNyuryokuResult.kinmuShuryo);
		$("#jitsudojikan").text(tokubetsuNyuryokuResult.jitsudojikan);
		$("#hidEigyoshoCode").val(tokubetsuNyuryokuResult.eigyoshoCode);
		$("#hidBushoCode").val(tokubetsuNyuryokuResult.bushoCode);

		//集計エリア表示
		let shukeiResult			= contents["result"]["shukeiArea"];
		$("#shinseinissu01").text(Number(shukeiResult.ShinseiNissu01??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu02").text(Number(shukeiResult.ShinseiNissu02??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu03").text(Number(shukeiResult.ShinseiNissu03??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu04").text(Number(shukeiResult.ShinseiNissu04??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu05").text(Number(shukeiResult.ShinseiNissu05??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu06").text(Number(shukeiResult.ShinseiNissu06??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu07").text(Number(shukeiResult.ShinseiNissu07??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu08").text(Number(shukeiResult.ShinseiNissu08??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu09").text(Number(shukeiResult.ShinseiNissu09??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu10").text(Number(shukeiResult.ShinseiNissu10??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu11").text(Number(shukeiResult.ShinseiNissu11??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));

		$("#shinseinissukyujitsu").text(Number(shukeiResult.ShinseiNissuKyujitsu??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));

		$("#shinseijikan01").text(Number(shukeiResult.ShinseiJikan01??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan02").text(Number(shukeiResult.ShinseiJikan02??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan03").text(Number(shukeiResult.ShinseiJikan03??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan04").text(Number(shukeiResult.ShinseiJikan04??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan05").text(Number(shukeiResult.ShinseiJikan05??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan06").text(Number(shukeiResult.ShinseiJikan06??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan07").text(Number(shukeiResult.ShinseiJikan07??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan08").text(Number(shukeiResult.ShinseiJikan08??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan09").text(Number(shukeiResult.ShinseiJikan09??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan10").text(Number(shukeiResult.ShinseiJikan10??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan11").text(Number(shukeiResult.ShinseiJikan11??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));

		$("#shinseitanka01").text(Number(shukeiResult.ShinseiTanka01??0).toLocaleString("ja-JP"));
		$("#shinseitanka02").text(Number(shukeiResult.ShinseiTanka02??0).toLocaleString("ja-JP"));
		$("#shinseitanka03").text(Number(shukeiResult.ShinseiTanka03??0).toLocaleString("ja-JP"));
		$("#shinseitanka04").text(Number(shukeiResult.ShinseiTanka04??0).toLocaleString("ja-JP"));
		$("#shinseitanka05").text(Number(shukeiResult.ShinseiTanka05??0).toLocaleString("ja-JP"));
		$("#shinseitanka06").text(Number(shukeiResult.ShinseiTanka06??0).toLocaleString("ja-JP"));
		$("#shinseitanka07").text(Number(shukeiResult.ShinseiTanka07??0).toLocaleString("ja-JP"));
		$("#shinseitanka08").text(Number(shukeiResult.ShinseiTanka08??0).toLocaleString("ja-JP"));
		$("#shinseitanka09").text(Number(shukeiResult.ShinseiTanka09??0).toLocaleString("ja-JP"));
		$("#shinseitanka10").text(Number(shukeiResult.ShinseiTanka10??0).toLocaleString("ja-JP"));
		$("#shinseitanka11").text(Number(shukeiResult.ShinseiTanka11??0).toLocaleString("ja-JP"));

		$("#shinseikingakugoukei01").text(Number(shukeiResult.ShinseiKingakuGoukei01??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei02").text(Number(shukeiResult.ShinseiKingakuGoukei02??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei03").text(Number(shukeiResult.ShinseiKingakuGoukei03??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei04").text(Number(shukeiResult.ShinseiKingakuGoukei04??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei05").text(Number(shukeiResult.ShinseiKingakuGoukei05??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei06").text(Number(shukeiResult.ShinseiKingakuGoukei06??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei07").text(Number(shukeiResult.ShinseiKingakuGoukei07??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei08").text(Number(shukeiResult.ShinseiKingakuGoukei08??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei09").text(Number(shukeiResult.ShinseiKingakuGoukei09??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei10").text(Number(shukeiResult.ShinseiKingakuGoukei10??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei11").text(Number(shukeiResult.ShinseiKingakuGoukei11??0).toLocaleString("ja-JP"));

		$("#txtTokkijiko").val(shukeiResult.TokkiJiko);

		$("#shinseinisuugoukei").text(Number(shukeiResult.ShinseiNisuuGoukei??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseijikangoukei").text(Number(shukeiResult.ShinseiJikanGoukei??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseikingakugoukeigoukei").text(Number(shukeiResult.ShinseiKingakuGoukeiGoukei??0).toLocaleString("ja-JP"));

		if(shukeiResult.KakuteiKbn == "03"){
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
			document.getElementById("btnDelete").disabled = true;
			document.getElementById("btnRecalc").disabled = true;
			document.getElementById("btnUpdate").disabled = true;
		}
		else{
			//取得した更新日付・時間が空の時、新規登録として背景色を変更する
			if($("#hdnKihonSaishuKoshinDate").val() == "" && $("#hdnKihonSaishuKoshinJikan").val() == ""){
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
			document.getElementById("btnDelete").disabled = false;
			document.getElementById("btnRecalc").disabled = false;
			document.getElementById("btnUpdate").disabled = false;
		}
	});
	
	
	
}

/*
*
* 検索結果を表示
*
*/
function onDisplayNyuryokuArea(firstHalfFlg){
	// 検索結果エリアをクリアする
	$("#kihonNyuryokuArea").children().remove();
	$("#txtShinseiKingaku01").val(0);
	$("#txtShinseiKingaku02").val(0);

	let sinseiKubunList = [];
	proc("getDDL", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];

		console.log("getDDLのresult");
		console.log(result);
		for(let record of result){
			sinseiKubunList.push(record);
		}
	});
	
	for(let i = 0; i < chiChinginkeisanshoResultAll.length; i++){
		let record = chiChinginkeisanshoResultAll[i];
		let taishoNengappi = record["TaishoNengappi"];
		let taishoGetsu = ("00" + record["TaishoGetsu"]).slice(-2);
		let taishoBi = ("00" + record["TaishoBi"]).slice(-2);
		let yobiKbn = record["YobiKbn"];

		let shusshaJi =		record["ShusshaJi"];
		let shusshaFun =	record["ShusshaFun"];
		let taishaJi =		record["TaishaJi"];
		let taishaFun =		record["TaishaFun"];
		let jitsudoJikan =	record["JitsudoJikan"];

		let chinginShinseiKbn1 = record["ChinginShinseiKbn1"];
		let chinginShinseiJikan1 = record["ChinginShinseiJikan1"];
		let chinginShinseiKbn2 = record["ChinginShinseiKbn2"];
		let chinginShinseiJikan2 = record["ChinginShinseiJikan2"];
		let chinginShinseiKbn3 = record["ChinginShinseiKbn3"];
		let chinginShinseiJikan3 = record["ChinginShinseiJikan3"];
		
		let meisaiSaishuKoshinDate = record["MeisaiSaishuKoshinDate"];
		let meisaiSaishuKoshinJikan = record["MeisaiSaishuKoshinJikan"];
		
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
		sinsei1SelectBox += 	"<select name=\"ChinginShinseiKbn1" + i + "\" id=\"ChinginShinseiKbn1" + i + "\" value=\"" + chinginShinseiKbn1 + "\"  onchange=\"setShukkinBo('ChinginShinseiKbn1', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei1SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(chinginShinseiKbn1 == sinseiKubunRecord["Code"]){
				sinsei1SelectBox += 		"selected";
			}
			sinsei1SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
		}
		
		//申請区分2のセレクトボックス
		let sinsei2SelectBox = "";
		sinsei2SelectBox += 	"<select name=\"ChinginShinseiKbn2" + i + "\" id=\"ChinginShinseiKbn2" + i + "\" value=\"" + chinginShinseiKbn2 + "\"  onchange=\"setShukkinBo('ChinginShinseiKbn2', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei2SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(chinginShinseiKbn2 == sinseiKubunRecord["Code"]){
				sinsei2SelectBox += 		"selected";
			}
			sinsei2SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
		}
		
		//申請区分3のセレクトボックス
		let sinsei3SelectBox = "";
		sinsei3SelectBox += 	"<select name=\"ChinginShinseiKbn3" + i + "\" id=\"ChinginShinseiKbn3" + i + "\" value=\"" + chinginShinseiKbn3 + "\"  onchange=\"setShukkinBo('ChinginShinseiKbn3', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei3SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(chinginShinseiKbn3 == sinseiKubunRecord["Code"]){
				sinsei3SelectBox += 		"selected";
			}
			sinsei3SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
		}


		let kihonNyuryokuAreaHtml = "";
		if(
			(firstHalfFlg == true && record["TaishoGetsu"] == chiChinginkeisanshoResultAll[0]["TaishoGetsu"]) ||
			(firstHalfFlg != true && record["TaishoGetsu"] != chiChinginkeisanshoResultAll[0]["TaishoGetsu"])
		){
			kihonNyuryokuAreaHtml =
				"<tr>" +
					"<input type=\"hidden\" name=\"TaishoNengappi" + i + "\" id=\"TaishoNengappi" + i + "\" value=\"" + taishoNengappi + "\">" +
					"<input type=\"hidden\" name=\"MeisaiSaishuKoshinDate" + i + "\" id=\"MeisaiSaishuKoshinDate" + i + "\" value=\"" + meisaiSaishuKoshinDate + "\">" +
					"<input type=\"hidden\" name=\"MeisaiSaishuKoshinJikan" + i + "\" id=\"MeisaiSaishuKoshinJikan" + i + "\" value=\"" + meisaiSaishuKoshinJikan + "\">" +
					
					"<td class=\"value center\"><a >" + taishoGetsu + "</a></td>" +
					"<input type=\"hidden\" name=\"TaishoGetsu" + i + "\" id=\"TaishoGetsu" + i + "\" value=\"" + taishoGetsu + "\">" +
					"<td class=\"value center\"><a >" + taishoBi + "</a></td>" +
					"<input type=\"hidden\" name=\"TaishoBi" + i + "\" id=\"TaishoBi" + i + "\" value=\"" + taishoBi + "\">" +
					"<td class=\"value center\"><a class=\"" + yobiColorClass + "\">" + yobiKbn + "</a></td>" +
	
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"ShusshaJi" + i + "\" id=\"ShusshaJi" + i + "\"  value=\"" + shusshaJi + "\"  onchange=\"changeShusshaJi(" + i + ");setShukkinBo('ShusshaJi', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"ShusshaFun" + i + "\" id=\"ShusshaFun" + i + "\"  value=\"" + shusshaFun + "\"  onchange=\"setShukkinBo('ShusshaFun', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<a >-</a>" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"TaishaJi" + i + "\" id=\"TaishaJi" + i + "\"  value=\"" + taishaJi + "\"  onchange=\"setShukkinBo('TaishaJi', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"2\" name=\"TaishaFun" + i + "\" id=\"TaishaFun" + i + "\"  value=\"" + taishaFun + "\"  onchange=\"setShukkinBo('TaishaFun', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"JitsudoJikan" + i + "\" id=\"JitsudoJikan" + i + "\"  value=\"" + jitsudoJikan + "\"  onchange=\"setShukkinBo('JitsudoJikan', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei1SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"ChinginShinseiJikan1" + i + "\" id=\"ChinginShinseiJikan1" + i + "\"  value=\"" + chinginShinseiJikan1 + "\"  onchange=\"setShukkinBo('ChinginShinseiJikan1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei2SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"ChinginShinseiJikan2" + i + "\" id=\"ChinginShinseiJikan2" + i + "\"  value=\"" + chinginShinseiJikan2 + "\"  onchange=\"setShukkinBo('ChinginShinseiJikan2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei3SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px; text-align: right;\" maxlength=\"5\" name=\"ChinginShinseiJikan3" + i + "\" id=\"ChinginShinseiJikan3" + i + "\"  value=\"" + chinginShinseiJikan3 + "\"  onchange=\"setShukkinBo('ChinginShinseiJikan3', " + i + ");\" >" + 
					"</td>" +
				"</tr>";
		}
		else{
			kihonNyuryokuAreaHtml =
					"<input type=\"hidden\" name=\"TaishoNengappi" + i + "\" id=\"TaishoNengappi" + i + "\" value=\"" + taishoNengappi + "\">" +
					"<input type=\"hidden\" name=\"MeisaiSaishuKoshinDate" + i + "\" id=\"MeisaiSaishuKoshinDate" + i + "\" value=\"" + meisaiSaishuKoshinDate + "\">" +
					"<input type=\"hidden\" name=\"MeisaiSaishuKoshinJikan" + i + "\" id=\"MeisaiSaishuKoshinJikan" + i + "\" value=\"" + meisaiSaishuKoshinJikan + "\">" +
					
					"<input type=\"hidden\" name=\"TaishoGetsu" + i + "\" id=\"TaishoGetsu" + i + "\" value=\"" + taishoGetsu + "\">" +
					"<input type=\"hidden\" name=\"TaishoBi" + i + "\" id=\"TaishoBi" + i + "\" value=\"" + taishoBi + "\">" +
					"<input type=\"hidden\" name=\"ShusshaJi" + i + "\" id=\"ShusshaJi" + i + "\"  value=\"" + shusshaJi + "\" >" + 
					"<input type=\"hidden\" name=\"ShusshaFun" + i + "\" id=\"ShusshaFun" + i + "\"  value=\"" + shusshaFun + "\" >" +
					"<input type=\"hidden\" name=\"TaishaJi" + i + "\" id=\"TaishaJi" + i + "\"  value=\"" + taishaJi + "\" >" +
					"<input type=\"hidden\" name=\"TaishaFun" + i + "\" id=\"TaishaFun" + i + "\"  value=\"" + taishaFun + "\" >" +
					"<input type=\"hidden\" name=\"JitsudoJikan" + i + "\" id=\"JitsudoJikan" + i + "\"  value=\"" + jitsudoJikan + "\" >" +
					"<input type=\"hidden\" name=\"ChinginShinseiKbn1" + i + "\" id=\"ChinginShinseiKbn1" + i + "\"  value=\"" + chinginShinseiKbn1 + "\" >" + 
					"<input type=\"hidden\" name=\"ChinginShinseiJikan1" + i + "\" id=\"ChinginShinseiJikan1" + i + "\"  value=\"" + chinginShinseiJikan1 + "\" >" + 

					"<input type=\"hidden\" name=\"ChinginShinseiKbn2" + i + "\" id=\"ChinginShinseiKbn2" + i + "\"  value=\"" + chinginShinseiKbn2 + "\" >" + 
					"<input type=\"hidden\" name=\"ChinginShinseiJikan2" + i + "\" id=\"ChinginShinseiJikan2" + i + "\"  value=\"" + chinginShinseiJikan2 + "\" >" + 
					"<input type=\"hidden\" name=\"ChinginShinseiKbn3" + i + "\" id=\"ChinginShinseiKbn3" + i + "\"  value=\"" + chinginShinseiKbn3 + "\" >" + 
					"<input type=\"hidden\" name=\"ChinginShinseiJikan3" + i + "\" id=\"ChinginShinseiJikan3" + i + "\"  value=\"" + chinginShinseiJikan3 + "\" >";
		}

		$("#kihonNyuryokuArea").append(kihonNyuryokuAreaHtml);
	}
	$("#txtShinseiKingaku01").val(chiChinginkeisanshoResultAll[0]["ShinseiKingaku01"]);
	$("#txtShinseiKingaku02").val(chiChinginkeisanshoResultAll[0]["ShinseiKingaku02"]);
	$("#hdnKihonSaishuKoshinDate").val(chiChinginkeisanshoResultAll[0]["KihonSaishuKoshinDate"]);
	$("#hdnKihonSaishuKoshinJikan").val(chiChinginkeisanshoResultAll[0]["KihonSaishuKoshinJikan"]);


	//前・次一覧ボタンの活性変更
	if(firstHalfFlg == true){
		document.getElementById("btnFirstHalf").disabled = true;
		document.getElementById("btnSecondHalf").disabled = false;
		//前一覧表示時、最初の予定をフォーカス
		$("#ShukkinYoteiKbn0").focus();
	}
	else{
		document.getElementById("btnFirstHalf").disabled = false;
		document.getElementById("btnSecondHalf").disabled = true;
		//次一覧表示時、最後の予定をフォーカス
		$("#ShukkinYoteiKbn" + (chiChinginkeisanshoResultAll.length - 1)).focus();
	}

}

function changeShusshaJi(nowRow){
	//出社時をクリアしたら、同行の項目をクリア
	if($("#ShusshaJi" + nowRow).val() == ""){
		fieldName = "ShusshaJi";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "ShusshaFun";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "TaishaJi";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "TaishaFun";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "JitsudoJikan";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "ChinginShinseiKbn1";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "ChinginShinseiJikan1";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "ChinginShinseiKbn2";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "ChinginShinseiJikan2";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "ChinginShinseiKbn3";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "ChinginShinseiJikan3";
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
* 入力した値を内部的な配列に取得
*
*/
function setShinseiKingaku01(){
	chiChinginkeisanshoResultAll[0]["ShinseiKingaku01"] = $("#txtShinseiKingaku01").val();
}

/*
*
* 入力した値を内部的な配列に取得
*
*/
function setShinseiKingaku02(){
	chiChinginkeisanshoResultAll[0]["ShinseiKingaku02"] = $("#txtShinseiKingaku02").val();
}


/*
*
* 予定DLL選択時の色変更
*
*/
function yoteiChangeColor(yotei){
	if( yotei.value == "02" || yotei.value == "03" ){
		yotei.style.color = 'red';
	}
	else{
		yotei.style.color = 'black';
	}
}

/*
*
* 勤怠区分DLL選択時の色変更
*
*/
function chinginChangeColor(chingin){
	if( chingin.value == "03" ){
		chingin.style.color = 'green';
	}
	else if( chingin.value == "04" || chingin.value == "05" || chingin.value == "08" || chingin.value == "10" ){
		chingin.style.color = 'red';
	}
	else {
		chingin.style.color = 'black';
	}
}

/*
*
* 開始時分、終了時分から時間を計算
*
*/
function calcJitsudoJikan(nowRow){
	let checkIfNumber = /^[0-9]+$/;

	let kaishiJi = chiChinginkeisanshoResultAll[nowRow]["ShusshaJi"];
	let kaishiFun = chiChinginkeisanshoResultAll[nowRow]["ShusshaFun"];
	let shuryoJi = chiChinginkeisanshoResultAll[nowRow]["TaishaJi"];
	let shuryoFun = chiChinginkeisanshoResultAll[nowRow]["TaishaFun"];
	
	let jikan = chiChinginkeisanshoResultAll[nowRow]["JitsudoJikan"];
	
	//開始時分、終了時分が全て数字で入力済み　かつ　時間が未入力または0の時のみ自動計算する
	if(
		kaishiJi != "" && checkIfNumber.test(kaishiJi) &&
		kaishiFun != "" && checkIfNumber.test(kaishiFun) &&
		shuryoJi != "" && checkIfNumber.test(shuryoJi) &&
		shuryoFun != "" && checkIfNumber.test(shuryoFun) 
//		&&
//		(jikan == "" || Number(jikan) == 0)
	){
		let kaishiJiNum = Number(kaishiJi);
		let kaishiFunNum = Number(kaishiFun);
		let shuryoJiNum = Number(shuryoJi);
		let shuryoFunNum = Number(shuryoFun);
		//すべて時や分に合う　かつ　開始時分＜終了時分の場合のみ自動計算する
		if(
			kaishiJiNum < 24 &&
			kaishiFunNum < 60 &&
			shuryoJiNum < 24 &&
			shuryoFunNum < 60 &&
			((kaishiJiNum * 60 + kaishiFunNum) <= (shuryoJiNum * 60 + shuryoFunNum))
		){
			//1.分単位で時間計算
			let jikanWk = (shuryoJiNum * 60 + shuryoFunNum) - (kaishiJiNum * 60 + kaishiFunNum);
			//2.勤怠申請時間の時部分を計算
			let jikanJi = Math.floor(jikanWk / 60);
			//3.勤怠申請時間の分部分を計算
			let jikanFun = jikanWk % 60;
			//4.実際に項目に表示する値を計算
			let jikanDisp = (Math.floor((jikanJi + (jikanFun / 100)) * 100) / 100).toFixed(2);
			
			chiChinginkeisanshoResultAll[nowRow]["JitsudoJikan"] = jikanDisp;
			$("#JitsudoJikan" + nowRow).val(jikanDisp);
		}
	}
}

/*
*
* 開始時分、終了時分から時間を計算
*
*/
function calcShinseiJikan(nowCol, nowRow){
	let checkIfNumber = /^[0-9]+$/;

	let kaishiJi = chiChinginkeisanshoResultAll[nowRow]["ChinginShinseiKaishiJi" + nowCol];
	let kaishiFun = chiChinginkeisanshoResultAll[nowRow]["ChinginShinseiKaishiFun" + nowCol];
	let shuryoJi = chiChinginkeisanshoResultAll[nowRow]["ChinginShinseiShuryoJi" + nowCol];
	let shuryoFun = chiChinginkeisanshoResultAll[nowRow]["ChinginShinseiShuryoFun" + nowCol];
	
	let jikan = chiChinginkeisanshoResultAll[nowRow]["ChinginShinseiJikan" + nowCol];
	
	//開始時分、終了時分が全て数字で入力済み　かつ　時間が未入力または0の時のみ自動計算する
	if(
		kaishiJi != "" && checkIfNumber.test(kaishiJi) &&
		kaishiFun != "" && checkIfNumber.test(kaishiFun) &&
		shuryoJi != "" && checkIfNumber.test(shuryoJi) &&
		shuryoFun != "" && checkIfNumber.test(shuryoFun)
//		 &&
//		(jikan == "" || Number(jikan) == 0)
	){
		let kaishiJiNum = Number(kaishiJi);
		let kaishiFunNum = Number(kaishiFun);
		let shuryoJiNum = Number(shuryoJi);
		let shuryoFunNum = Number(shuryoFun);
		//すべて時や分に合う　かつ　開始時分＜終了時分の場合のみ自動計算する
		if(
			kaishiJiNum < 24 &&
			kaishiFunNum < 60 &&
			shuryoJiNum < 24 &&
			shuryoFunNum < 60 &&
			((kaishiJiNum * 60 + kaishiFunNum) <= (shuryoJiNum * 60 + shuryoFunNum))
		){
			//1.分単位で時間計算
			let jikanWk = (shuryoJiNum * 60 + shuryoFunNum) - (kaishiJiNum * 60 + kaishiFunNum);
			//2.勤怠申請時間の時部分を計算
			let jikanJi = Math.floor(jikanWk / 60);
			//3.勤怠申請時間の分部分を計算
			let jikanFun = jikanWk % 60;
			//4.実際に項目に表示する値を計算
			let jikanDisp = (Math.floor((jikanJi + (jikanFun / 100)) * 100) / 100).toFixed(2);
			
			chiChinginkeisanshoResultAll[nowRow]["ChinginShinseiJikan" + nowCol] = jikanDisp;
			$("#ChinginShinseiJikan" + nowCol + nowRow).val(jikanDisp);
		}
	}
}

/*
*
* 削除処理呼び出し
*
*/
function onKeyEventF02(){
	if(($("#buttonArea").css("visibility") != "hidden") && (document.getElementById("btnDelete").disabled != true)){
		onDelete();
	}
}
function onDelete(){
	if ($("#nyuryokuArea").hasClass("upd")) {
		var result = window.confirm('データの削除を行います。\nよろしいですか？');
		    
		if( result ) {
			//削除処理呼び出し
			proc("delete", {}, function(data){
		
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
					getShainNOFormat();//社員名再取得

					//検索結果が0の時のため、画面非表示
					$("#nyuryokuArea").css("visibility", "hidden");
					$("#buttonArea").css("visibility", "hidden");
					onSearchChiChinginkeisansho();
				}
				else{
					alert("このデータはすでに、別のユーザーに更新されています。\r\nもう一度データを確認してください。");
				}
			});
		}
	}
}

/*
*
* 再表示処理呼び出し
*
*/
function onKeyEventF08(){
	if(($("#buttonArea").css("visibility") != "hidden") && (document.getElementById("btnRecalc").disabled != true)){
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
		
		//検索結果があれば入力項目表示
		$("#nyuryokuArea").css("visibility", "");

		let chinginkeisanshoResult			= contents["result"]["chinginkeisanshoArea"];
		chiChinginkeisanshoResultAll = chinginkeisanshoResult;
		onDisplayNyuryokuArea(true);

		//勤務開始・終了時間、実働時間表示
		let tokubetsuNyuryokuResult			= contents["result"]["tokubetsuNyuryokuArea"];
		$("#kinmuKaishi").text(tokubetsuNyuryokuResult.kinmuKaishi);
		$("#kinmuShuryo").text(tokubetsuNyuryokuResult.kinmuShuryo);
		$("#jitsudojikan").text(tokubetsuNyuryokuResult.jitsudojikan);
		$("#hidEigyoshoCode").val(tokubetsuNyuryokuResult.eigyoshoCode);
		$("#hidBushoCode").val(tokubetsuNyuryokuResult.bushoCode);

		//集計エリア表示
		let shukeiResult			= contents["result"]["shukeiArea"];
		$("#shinseinissu01").text(Number(shukeiResult.ShinseiNissu01??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu02").text(Number(shukeiResult.ShinseiNissu02??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu03").text(Number(shukeiResult.ShinseiNissu03??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu04").text(Number(shukeiResult.ShinseiNissu04??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu05").text(Number(shukeiResult.ShinseiNissu05??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu06").text(Number(shukeiResult.ShinseiNissu06??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu07").text(Number(shukeiResult.ShinseiNissu07??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu08").text(Number(shukeiResult.ShinseiNissu08??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu09").text(Number(shukeiResult.ShinseiNissu09??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu10").text(Number(shukeiResult.ShinseiNissu10??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseinissu11").text(Number(shukeiResult.ShinseiNissu11??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));

		$("#shinseinissukyujitsu").text(Number(shukeiResult.ShinseiNissuKyujitsu??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));

		$("#shinseijikan01").text(Number(shukeiResult.ShinseiJikan01??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan02").text(Number(shukeiResult.ShinseiJikan02??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan03").text(Number(shukeiResult.ShinseiJikan03??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan04").text(Number(shukeiResult.ShinseiJikan04??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan05").text(Number(shukeiResult.ShinseiJikan05??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan06").text(Number(shukeiResult.ShinseiJikan06??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan07").text(Number(shukeiResult.ShinseiJikan07??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan08").text(Number(shukeiResult.ShinseiJikan08??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan09").text(Number(shukeiResult.ShinseiJikan09??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan10").text(Number(shukeiResult.ShinseiJikan10??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseijikan11").text(Number(shukeiResult.ShinseiJikan11??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));

		$("#shinseitanka01").text(Number(shukeiResult.ShinseiTanka01??0).toLocaleString("ja-JP"));
		$("#shinseitanka02").text(Number(shukeiResult.ShinseiTanka02??0).toLocaleString("ja-JP"));
		$("#shinseitanka03").text(Number(shukeiResult.ShinseiTanka03??0).toLocaleString("ja-JP"));
		$("#shinseitanka04").text(Number(shukeiResult.ShinseiTanka04??0).toLocaleString("ja-JP"));
		$("#shinseitanka05").text(Number(shukeiResult.ShinseiTanka05??0).toLocaleString("ja-JP"));
		$("#shinseitanka06").text(Number(shukeiResult.ShinseiTanka06??0).toLocaleString("ja-JP"));
		$("#shinseitanka07").text(Number(shukeiResult.ShinseiTanka07??0).toLocaleString("ja-JP"));
		$("#shinseitanka08").text(Number(shukeiResult.ShinseiTanka08??0).toLocaleString("ja-JP"));
		$("#shinseitanka09").text(Number(shukeiResult.ShinseiTanka09??0).toLocaleString("ja-JP"));
		$("#shinseitanka10").text(Number(shukeiResult.ShinseiTanka10??0).toLocaleString("ja-JP"));
		$("#shinseitanka11").text(Number(shukeiResult.ShinseiTanka11??0).toLocaleString("ja-JP"));

		$("#shinseikingakugoukei01").text(Number(shukeiResult.ShinseiKingakuGoukei01??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei02").text(Number(shukeiResult.ShinseiKingakuGoukei02??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei03").text(Number(shukeiResult.ShinseiKingakuGoukei03??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei04").text(Number(shukeiResult.ShinseiKingakuGoukei04??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei05").text(Number(shukeiResult.ShinseiKingakuGoukei05??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei06").text(Number(shukeiResult.ShinseiKingakuGoukei06??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei07").text(Number(shukeiResult.ShinseiKingakuGoukei07??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei08").text(Number(shukeiResult.ShinseiKingakuGoukei08??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei09").text(Number(shukeiResult.ShinseiKingakuGoukei09??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei10").text(Number(shukeiResult.ShinseiKingakuGoukei10??0).toLocaleString("ja-JP"));
		$("#shinseikingakugoukei11").text(Number(shukeiResult.ShinseiKingakuGoukei11??0).toLocaleString("ja-JP"));

		$("#txtTokkijiko").val(shukeiResult.TokkiJiko);

		$("#shinseinisuugoukei").text(Number(shukeiResult.ShinseiNisuuGoukei??0).toLocaleString("ja-JP", {maximumFractionDigits: 1,}));
		$("#shinseijikangoukei").text(Number(shukeiResult.ShinseiJikanGoukei??0).toLocaleString("ja-JP", {minimumFractionDigits: 2,}));
		$("#shinseikingakugoukeigoukei").text(Number(shukeiResult.ShinseiKingakuGoukeiGoukei??0).toLocaleString("ja-JP"));

	});
}

/*
*
* 登録更新処理呼び出し
*
*/
function onKeyEventF09(){
	if(($("#buttonArea").css("visibility") != "hidden") && (document.getElementById("btnUpdate").disabled != true)){
		onUpdate();
	}
}
function onUpdate(){
	var result = window.confirm('データの更新を行います。\nよろしいですか？');
	    
	if( result ) {
		//更新処理呼び出し
		proc("update", {}, function(data){
	
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
				getShainNOFormat();//社員名再取得
	
				//検索結果が0の時のため、画面非表示
				$("#nyuryokuArea").css("visibility", "hidden");
				$("#buttonArea").css("visibility", "hidden");
				onSearchChiChinginkeisansho();
			}
			else{
				alert("このデータはすでに、別のユーザーに更新されています。\r\nもう一度データを確認してください。");
			}
		});
	}
}
