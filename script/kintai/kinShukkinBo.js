let kinShukkinBoResultAll = [];
let shinseiKingaku01 = 0;
let shinseiKingaku02 = 0;

let shukinboKbn = "";
let kinmuKaishiJi = "";
let kinmuKaishiFun = "";
let kinmuShuryoJi = "";
let kinmuShuryoFun = "";
let keiyakuJitsudoJikan = "";

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
* 出勤簿検索
*
*/
function onSearchKinShukkinBo(){
	
	let honshaKakuteizumiFlg = false;

	shukinboKbn = "";
	kinmuKaishiJi = "";
	kinmuKaishiFun = "";
	kinmuShuryoJi = "";
	kinmuShuryoFun = "";
	keiyakuJitsudoJikan = "";

	//検索対象の社員の出勤簿入力区分、勤務開始/終了時刻の取得
	proc("getShukkinboNyuuryokuKbn", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];
		
		//出勤簿入力区分、勤務開始/終了時刻の取得
		shukinboKbn =			result["ShukinboKbn"];
		kinmuKaishiJi =			result["KinmuKaishiJi"];
		kinmuKaishiFun =		result["KinmuKaishiFun"];
		kinmuShuryoJi =			result["KinmuShuryoJi"];
		kinmuShuryoFun =		result["KinmuShuryoFun"];
		keiyakuJitsudoJikan =	result["KeiyakuJitsudoJikan"];
		
	});

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

		let result			= contents["result"];
		kinShukkinBoResultAll = result;

		onDisplayNyuryokuArea(true);

		if(kinShukkinBoResultAll[0]["txtKakuteiKbn"] == "03"){
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
* 検索結果を表示
*
*/
function onDisplayNyuryokuArea(firstHalfFlg){
	// 検索結果エリアをクリアする
	$("#kihonNyuryokuArea").children().remove();
	$("#numShinseiKingaku01").val(0);
	$("#numShinseiKingaku02").val(0);

	let yoteiList = [];
	let kintaiKubunList = [];
	let sinseiKubunList = [];
	//各種ドロップダウンの内容取得
	proc("getDDL", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];

		for(let record of result){
			if(record["DDLName"] == "yotei"){
				yoteiList.push(record);
			}
			if(record["DDLName"] == "kintai"){
				kintaiKubunList.push(record);
			}
			if(record["DDLName"] == "shinsei"){
				sinseiKubunList.push(record);
			}
		}
	});
	
	let focusIndex = -1;
	for(let i = 0; i < kinShukkinBoResultAll.length; i++){
		let record = kinShukkinBoResultAll[i];
		let taishoNengappi = record["txtTaishoNengappi"];
		let taishoGetsu = ("00" + record["txtTaishoGetsu"]).slice(-2);
		let taishoBi = ("00" + record["txtTaishoBi"]).slice(-2);
		let yobiKbn = record["txtYobiKbn"];
		let shukkinYoteiKbn = record["selShukkinYoteiKbn"];
		let kintaiKbn = record["selKintaiKbn"];

		let shusshaJi =		record["numShusshaJi"];
		let shusshaFun =	record["numShusshaFun"];
		let taishaJi =		record["numTaishaJi"];
		let taishaFun =		record["numTaishaFun"];
		let jitsudoJikan =	record["numJitsudoJikan"];

		let kintaiShinseiBiko =			record["txtKintaiShinseiBiko"];
		let kintaiShinseiKbn1 =			record["selKintaiShinseiKbn1"];
		let kintaiShinseiKaishiJi1 =	record["numKintaiShinseiKaishiJi1"];
		let kintaiShinseiKaishiFun1 =	record["numKintaiShinseiKaishiFun1"];
		let kintaiShinseiShuryoJi1 =	record["numKintaiShinseiShuryoJi1"];
		let kintaiShinseiShuryoFun1 =	record["numKintaiShinseiShuryoFun1"];
		let kintaiShinseiJikan1 =		record["numKintaiShinseiJikan1"];
		let kintaiShinseiKbn2 =			record["selKintaiShinseiKbn2"];
		let kintaiShinseiKaishiJi2 =	record["numKintaiShinseiKaishiJi2"];
		let kintaiShinseiKaishiFun2 =	record["numKintaiShinseiKaishiFun2"];
		let kintaiShinseiShuryoJi2 =	record["numKintaiShinseiShuryoJi2"];
		let kintaiShinseiShuryoFun2 =	record["numKintaiShinseiShuryoFun2"];
		let kintaiShinseiJikan2 =		record["numKintaiShinseiJikan2"];
		let kintaiShinseiKbn3 =			record["selKintaiShinseiKbn3"];
		let kintaiShinseiKaishiJi3 =	record["numKintaiShinseiKaishiJi3"];
		let kintaiShinseiKaishiFun3 =	record["numKintaiShinseiKaishiFun3"];
		let kintaiShinseiShuryoJi3 =	record["numKintaiShinseiShuryoJi3"];
		let kintaiShinseiShuryoFun3 =	record["numKintaiShinseiShuryoFun3"];
		let kintaiShinseiJikan3 =		record["numKintaiShinseiJikan3"];
		
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
		
		
		//予定のセレクトボックス
		let yoteiSelectBox = "";
		yoteiSelectBox += 	"<select class=\"kinShukkinBoText\" name=\"selShukkinYoteiKbn" + i + "\" id=\"selShukkinYoteiKbn" + i + "\" value=\"" + shukkinYoteiKbn + "\" " ;
		if(shukkinYoteiKbn == "02" || shukkinYoteiKbn == "03"){
			yoteiSelectBox += 		"style = \"COLOR: red\" ";
		}
		else{
			yoteiSelectBox += 		"style = \"COLOR: black\" ";
		}
		yoteiSelectBox += 		"onchange=\"yoteiChangeColor(this);changeShukkinYotei(" + i + ");setShukkinBo('selShukkinYoteiKbn', " + i + ");\" >" ;

		for(let yoteiRecord of yoteiList){
			yoteiSelectBox += 		"<option value=\"" + yoteiRecord["Code"] + "\" ";
			if(yoteiRecord["Code"] == "02" || yoteiRecord["Code"] == "03"){
				yoteiSelectBox += 		"style = \"COLOR: red\" ";
			}
			else{
				yoteiSelectBox += 		"style = \"COLOR: black\" ";
			}

			if(shukkinYoteiKbn == yoteiRecord["Code"]){
				yoteiSelectBox += 		"selected";
			}
			yoteiSelectBox += 		"><a>" + yoteiRecord["KbnName"] + "</a></option>" ;
		}
		
		//勤怠区分のセレクトボックス
		let kintaiSelectBox = "";
		kintaiSelectBox += 	"<select class=\"kinShukkinBoText\" name=\"selKintaiKbn" + i + "\" id=\"selKintaiKbn" + i + "\" value=\"" + kintaiKbn + "\" " ;
		if(kintaiKbn == "04" || kintaiKbn == "05" || kintaiKbn == "08" || kintaiKbn == "10"){
			kintaiSelectBox += 		"style = \"COLOR: red\" ";
		}
		else if(kintaiKbn == "03"){
			kintaiSelectBox += 		"style = \"COLOR: green\" ";
		}
		else{
			kintaiSelectBox += 		"style = \"COLOR: black\" ";
		}
		kintaiSelectBox += 		"onchange=\"kintaiChangeColor(this);setShukkinBo('selKintaiKbn', " + i + ");setDefaultJitsudoJikan(" + i + ");disabledShusshaTaishaJikan(" + i +");\">" ;

		for(let kintaiKubunRecord of kintaiKubunList){
			kintaiSelectBox += 		"<option value=\"" + kintaiKubunRecord["Code"] + "\" ";
			if(kintaiKubunRecord["Code"] == "04" || kintaiKubunRecord["Code"] == "05" || kintaiKubunRecord["Code"] == "08" || kintaiKubunRecord["Code"] == "10"){
				kintaiSelectBox += 		"style = \"COLOR: red\" ";
			}
			else if(kintaiKubunRecord["Code"] == "03"){
				kintaiSelectBox += 		"style = \"COLOR: green\" ";
			}
			else{
				kintaiSelectBox += 		"style = \"COLOR: black\" ";
			}

			if(kintaiKbn == kintaiKubunRecord["Code"]){
				kintaiSelectBox += 		"selected";
			}
			kintaiSelectBox += 		"><a>" + kintaiKubunRecord["KbnName"] + "</a></option>" ;
		}
		
		//申請区分1のセレクトボックス
		let sinsei1SelectBox = "";
		sinsei1SelectBox += 	"<select class=\"kinShukkinBoText\" name=\"selKintaiShinseiKbn1" + i + "\" id=\"selKintaiShinseiKbn1" + i + "\" value=\"" + kintaiShinseiKbn1 + "\"  onchange=\"setShukkinBo('selKintaiShinseiKbn1', " + i + ");resetJikan('1', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei1SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(kintaiShinseiKbn1 == sinseiKubunRecord["Code"]){
				sinsei1SelectBox += 		"selected";
			}
			sinsei1SelectBox += 		"><a>" + sinseiKubunRecord["KbnName"] + "</a></option>" ;
		}
		
		//申請区分2のセレクトボックス
		let sinsei2SelectBox = "";
		sinsei2SelectBox += 	"<select class=\"kinShukkinBoText\" name=\"selKintaiShinseiKbn2" + i + "\" id=\"selKintaiShinseiKbn2" + i + "\" value=\"" + kintaiShinseiKbn2 + "\"  onchange=\"setShukkinBo('selKintaiShinseiKbn2', " + i + ");resetJikan('2', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei2SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(kintaiShinseiKbn2 == sinseiKubunRecord["Code"]){
				sinsei2SelectBox += 		"selected";
			}
			sinsei2SelectBox += 		"><a>" + sinseiKubunRecord["KbnName"] + "</a></option>" ;
		}
		
		//申請区分3のセレクトボックス
		let sinsei3SelectBox = "";
		sinsei3SelectBox += 	"<select class=\"kinShukkinBoText\" name=\"selKintaiShinseiKbn3" + i + "\" id=\"selKintaiShinseiKbn3" + i + "\" value=\"" + kintaiShinseiKbn3 + "\"  onchange=\"setShukkinBo('selKintaiShinseiKbn3', " + i + ");resetJikan('3', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei3SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(kintaiShinseiKbn3 == sinseiKubunRecord["Code"]){
				sinsei3SelectBox += 		"selected";
			}
			sinsei3SelectBox += 		"><a>" + sinseiKubunRecord["KbnName"] + "</a></option>" ;
		}
		
		let kihonNyuryokuAreaHtml = "";
		if(
			(firstHalfFlg == true && record["txtTaishoGetsu"] == kinShukkinBoResultAll[0]["txtTaishoGetsu"]) ||
			(firstHalfFlg != true && record["txtTaishoGetsu"] != kinShukkinBoResultAll[0]["txtTaishoGetsu"])
		){
			//フォーカス処理に備え、出社時が空欄の最初の行を取得
			if(focusIndex == -1 && (kintaiKbn == "" || kintaiKbn == "00")){
				focusIndex = i;
			}
			
			kihonNyuryokuAreaHtml =
				"<tr>" +
					"<input type=\"hidden\" name=\"txtTaishoNengappi" + i + "\" id=\"txtTaishoNengappi" + i + "\" value=\"" + taishoNengappi + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinDate" + i + "\" id=\"txtMeisaiSaishuKoshinDate" + i + "\" value=\"" + meisaiSaishuKoshinDate + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinJikan" + i + "\" id=\"txtMeisaiSaishuKoshinJikan" + i + "\" value=\"" + meisaiSaishuKoshinJikan + "\">" +
					
					"<td class=\"value center\"><a class=\"kinShukkinBoText\">" + taishoGetsu + "</a></td>" +
					"<input type=\"hidden\" name=\"txtTaishoGetsu" + i + "\" id=\"txtTaishoGetsu" + i + "\" value=\"" + taishoGetsu + "\">" +
					"<td class=\"value center\"><a class=\"kinShukkinBoText\">" + taishoBi + "</a></td>" +
					"<input type=\"hidden\" name=\"txtTaishoBi" + i + "\" id=\"txtTaishoBi" + i + "\" value=\"" + taishoBi + "\">" +
					"<td class=\"value center\"><a class=\"kinShukkinBoText " + yobiColorClass + "\">" + yobiKbn + "</a></td>" +
					"<td class=\"value center\">" + 
						yoteiSelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						kintaiSelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numShusshaJi" + i + "\" id=\"numShusshaJi" + i + "\"  value=\"" + shusshaJi + "\"  onchange=\"set2ketaFormat('numShusshaJi', " + i + ");setShukkinBo('numShusshaJi', " + i + ");calcJitsudoJikan(" + i + ");\"" ; 
						//20260818 出社退社時間制御　追加
						if(    kintaiKbn == "03" || kintaiKbn == "04"|| kintaiKbn == "06" || kintaiKbn == "07"
							|| kintaiKbn == "08" || kintaiKbn == "09"|| kintaiKbn == "10"){
								kihonNyuryokuAreaHtml += "disabled";
						}
					kihonNyuryokuAreaHtml += " > </td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numShusshaFun" + i + "\" id=\"numShusshaFun" + i + "\"  value=\"" + shusshaFun + "\"  onchange=\"set2ketaFormat('numShusshaFun', " + i + ");setShukkinBo('numShusshaFun', " + i + ");calcJitsudoJikan(" + i + ");\"" ; 
						if(    kintaiKbn == "03" || kintaiKbn == "04" || kintaiKbn == "06" || kintaiKbn == "07"
							|| kintaiKbn == "08" || kintaiKbn == "09" || kintaiKbn == "10"){
								kihonNyuryokuAreaHtml += "disabled";
						}
						kihonNyuryokuAreaHtml += " > </td>" +
					"<td class=\"value center\">" + 
						"<a class=\"kinShukkinBoText\">-</a>" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numTaishaJi" + i + "\" id=\"numTaishaJi" + i + "\"  value=\"" + taishaJi + "\"  onchange=\"set2ketaFormat('numTaishaJi', " + i + ");setShukkinBo('numTaishaJi', " + i + ");calcJitsudoJikan(" + i + ");\"" ;
						if(    kintaiKbn == "03" || kintaiKbn == "04" || kintaiKbn == "06" || kintaiKbn == "07"
							|| kintaiKbn == "08" || kintaiKbn == "09" || kintaiKbn == "10"){
								kihonNyuryokuAreaHtml += "disabled";
						}
						kihonNyuryokuAreaHtml += " > </td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numTaishaFun" + i + "\" id=\"numTaishaFun" + i + "\"  value=\"" + taishaFun + "\"  onchange=\"set2ketaFormat('numTaishaFun', " + i + ");setShukkinBo('numTaishaFun', " + i + ");calcJitsudoJikan(" + i + ");\"" ; 
						if(    kintaiKbn == "03" || kintaiKbn == "04" || kintaiKbn == "06" || kintaiKbn == "07"
							|| kintaiKbn == "08" || kintaiKbn == "09" || kintaiKbn == "10"){
								kihonNyuryokuAreaHtml += "disabled";
						}
						kihonNyuryokuAreaHtml += " > </td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"numJitsudoJikan" + i + "\" id=\"numJitsudoJikan" + i + "\"  value=\"" + jitsudoJikan + "\"  onchange=\"setDecimalPoint2ketaFormat('numJitsudoJikan', " + i + ");setShukkinBo('numJitsudoJikan', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText bikoTextBox\"  maxlength=\"40\" name=\"txtKintaiShinseiBiko" + i + "\" id=\"txtKintaiShinseiBiko" + i + "\"  value=\"" + kintaiShinseiBiko + "\"  onchange=\"setShukkinBo('txtKintaiShinseiBiko', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei1SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiKaishiJi1" + i + "\" id=\"numKintaiShinseiKaishiJi1" + i + "\"  value=\"" + kintaiShinseiKaishiJi1 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiKaishiJi1', " + i + ");setShukkinBo('numKintaiShinseiKaishiJi1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiKaishiFun1" + i + "\" id=\"numKintaiShinseiKaishiFun1" + i + "\"  value=\"" + kintaiShinseiKaishiFun1 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiKaishiFun1', " + i + ");setShukkinBo('numKintaiShinseiKaishiFun1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiShuryoJi1" + i + "\" id=\"numKintaiShinseiShuryoJi1" + i + "\"  value=\"" + kintaiShinseiShuryoJi1 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiShuryoJi1', " + i + ");setShukkinBo('numKintaiShinseiShuryoJi1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiShuryoFun1" + i + "\" id=\"numKintaiShinseiShuryoFun1" + i + "\"  value=\"" + kintaiShinseiShuryoFun1 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiShuryoFun1', " + i + ");setShukkinBo('numKintaiShinseiShuryoFun1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"numKintaiShinseiJikan1" + i + "\" id=\"numKintaiShinseiJikan1" + i + "\"  value=\"" + kintaiShinseiJikan1 + "\"  onchange=\"setDecimalPoint2ketaFormat('numKintaiShinseiJikan1', " + i + ");setShukkinBo('numKintaiShinseiJikan1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei2SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiKaishiJi2" + i + "\" id=\"numKintaiShinseiKaishiJi2" + i + "\"  value=\"" + kintaiShinseiKaishiJi2 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiKaishiJi2', " + i + ");setShukkinBo('numKintaiShinseiKaishiJi2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiKaishiFun2" + i + "\" id=\"numKintaiShinseiKaishiFun2" + i + "\"  value=\"" + kintaiShinseiKaishiFun2 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiKaishiFun2', " + i + ");setShukkinBo('numKintaiShinseiKaishiFun2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiShuryoJi2" + i + "\" id=\"numKintaiShinseiShuryoJi2" + i + "\"  value=\"" + kintaiShinseiShuryoJi2 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiShuryoJi2', " + i + ");setShukkinBo('numKintaiShinseiShuryoJi2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiShuryoFun2" + i + "\" id=\"numKintaiShinseiShuryoFun2" + i + "\"  value=\"" + kintaiShinseiShuryoFun2 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiShuryoFun2', " + i + ");setShukkinBo('numKintaiShinseiShuryoFun2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"numKintaiShinseiJikan2" + i + "\" id=\"numKintaiShinseiJikan2" + i + "\"  value=\"" + kintaiShinseiJikan2 + "\"  onchange=\"setDecimalPoint2ketaFormat('numKintaiShinseiJikan2', " + i + ");setShukkinBo('numKintaiShinseiJikan2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei3SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiKaishiJi3" + i + "\" id=\"numKintaiShinseiKaishiJi3" + i + "\"  value=\"" + kintaiShinseiKaishiJi3 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiKaishiJi3', " + i + ");setShukkinBo('numKintaiShinseiKaishiJi3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiKaishiFun3" + i + "\" id=\"numKintaiShinseiKaishiFun3" + i + "\"  value=\"" + kintaiShinseiKaishiFun3 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiKaishiFun3', " + i + ");setShukkinBo('numKintaiShinseiKaishiFun3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiShuryoJi3" + i + "\" id=\"numKintaiShinseiShuryoJi3" + i + "\"  value=\"" + kintaiShinseiShuryoJi3 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiShuryoJi3', " + i + ");setShukkinBo('numKintaiShinseiShuryoJi3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"numKintaiShinseiShuryoFun3" + i + "\" id=\"numKintaiShinseiShuryoFun3" + i + "\"  value=\"" + kintaiShinseiShuryoFun3 + "\"  onchange=\"set2ketaFormat('numKintaiShinseiShuryoFun3', " + i + ");setShukkinBo('numKintaiShinseiShuryoFun3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"tel\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"numKintaiShinseiJikan3" + i + "\" id=\"numKintaiShinseiJikan3" + i + "\"  value=\"" + kintaiShinseiJikan3 + "\"  onchange=\"setDecimalPoint2ketaFormat('numKintaiShinseiJikan3', " + i + ");setShukkinBo('numKintaiShinseiJikan3', " + i + ");\" >" + 
					"</td>" +
				"</tr>";
		}
		else{
			kihonNyuryokuAreaHtml =
					"<input type=\"hidden\" name=\"txtTaishoNengappi" + i +				"\" id=\"txtTaishoNengappi" + i +			"\" value=\"" + taishoNengappi + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinDate" + i +		"\" id=\"txtMeisaiSaishuKoshinDate" + i +	"\" value=\"" + meisaiSaishuKoshinDate + "\">" +
					"<input type=\"hidden\" name=\"txtMeisaiSaishuKoshinJikan" + i +	"\" id=\"txtMeisaiSaishuKoshinJikan" + i +	"\" value=\"" + meisaiSaishuKoshinJikan + "\">" +
					"<input type=\"hidden\" name=\"txtTaishoGetsu" + i +				"\" id=\"txtTaishoGetsu" + i +				"\" value=\"" + taishoGetsu + "\">" +
					"<input type=\"hidden\" name=\"txtTaishoBi" + i +					"\" id=\"txtTaishoBi" + i +					"\" value=\"" + taishoBi + "\">" +

					"<input type=\"hidden\" name=\"selShukkinYoteiKbn" + i +			"\" id=\"selShukkinYoteiKbn" + i +			"\" value=\"" + shukkinYoteiKbn + "\">" +
					"<input type=\"hidden\" name=\"selKintaiKbn" + i +					"\" id=\"selKintaiKbn" + i +				"\" value=\"" + kintaiKbn + "\">" +
					
					"<input type=\"hidden\" name=\"numShusshaJi" + i +					"\" id=\"numShusshaJi" + i +				"\"  value=\"" + shusshaJi + "\"  >" + 
					"<input type=\"hidden\" name=\"numShusshaFun" + i +					"\" id=\"numShusshaFun" + i +				"\"  value=\"" + shusshaFun + "\"  >" + 
					"<input type=\"hidden\" name=\"numTaishaJi" + i +					"\" id=\"numTaishaJi" + i +					"\"  value=\"" + taishaJi + "\"  >" + 
					"<input type=\"hidden\" name=\"numTaishaFun" + i +					"\" id=\"numTaishaFun" + i +				"\"  value=\"" + taishaFun + "\"  >" + 
					"<input type=\"hidden\" name=\"numJitsudoJikan" + i +				"\" id=\"numJitsudoJikan" + i +				"\"  value=\"" + jitsudoJikan + "\"  >" + 
					"<input type=\"hidden\" name=\"txtKintaiShinseiBiko" + i +			"\" id=\"txtKintaiShinseiBiko" + i +		"\"  value=\"" + kintaiShinseiBiko + "\"  >" + 

					"<input type=\"hidden\" name=\"selKintaiShinseiKbn1" + i +			"\" id=\"selKintaiShinseiKbn1" + i +		"\" value=\"" + kintaiShinseiKbn1 + "\">" +
					"<input type=\"hidden\" name=\"numKintaiShinseiKaishiJi1" + i +		"\" id=\"numKintaiShinseiKaishiJi1" + i +	"\"  value=\"" + kintaiShinseiKaishiJi1 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiKaishiFun1" + i +	"\" id=\"numKintaiShinseiKaishiFun1" + i +	"\"  value=\"" + kintaiShinseiKaishiFun1 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiShuryoJi1" + i +		"\" id=\"numKintaiShinseiShuryoJi1" + i +	"\"  value=\"" + kintaiShinseiShuryoJi1 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiShuryoFun1" + i +	"\" id=\"numKintaiShinseiShuryoFun1" + i +	"\"  value=\"" + kintaiShinseiShuryoFun1 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiJikan1" + i +		"\" id=\"numKintaiShinseiJikan1" + i +		"\"  value=\"" + kintaiShinseiJikan1 + "\"  >" + 

					"<input type=\"hidden\" name=\"selKintaiShinseiKbn2" + i +			"\" id=\"selKintaiShinseiKbn2" + i +		"\" value=\"" + kintaiShinseiKbn2 + "\">" +
					"<input type=\"hidden\" name=\"numKintaiShinseiKaishiJi2" + i +		"\" id=\"numKintaiShinseiKaishiJi2" + i +	"\"  value=\"" + kintaiShinseiKaishiJi2 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiKaishiFun2" + i +	"\" id=\"numKintaiShinseiKaishiFun2" + i +	"\"  value=\"" + kintaiShinseiKaishiFun2 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiShuryoJi2" + i +		"\" id=\"numKintaiShinseiShuryoJi2" + i +	"\"  value=\"" + kintaiShinseiShuryoJi2 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiShuryoFun2" + i +	"\" id=\"numKintaiShinseiShuryoFun2" + i +	"\"  value=\"" + kintaiShinseiShuryoFun2 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiJikan2" + i +		"\" id=\"numKintaiShinseiJikan2" + i +		"\"  value=\"" + kintaiShinseiJikan2 + "\"  >" + 

					"<input type=\"hidden\" name=\"selKintaiShinseiKbn3" + i +			"\" id=\"selKintaiShinseiKbn3" + i +		"\" value=\"" + kintaiShinseiKbn3 + "\">" +
					"<input type=\"hidden\" name=\"numKintaiShinseiKaishiJi3" + i +		"\" id=\"numKintaiShinseiKaishiJi3" + i +	"\"  value=\"" + kintaiShinseiKaishiJi3 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiKaishiFun3" + i +	"\" id=\"numKintaiShinseiKaishiFun3" + i +	"\"  value=\"" + kintaiShinseiKaishiFun3 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiShuryoJi3" + i +		"\" id=\"numKintaiShinseiShuryoJi3" + i +	"\"  value=\"" + kintaiShinseiShuryoJi3 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiShuryoFun3" + i +	"\" id=\"numKintaiShinseiShuryoFun3" + i +	"\"  value=\"" + kintaiShinseiShuryoFun3 + "\"  >" + 
					"<input type=\"hidden\" name=\"numKintaiShinseiJikan3" + i +		"\" id=\"numKintaiShinseiJikan3" + i +		"\"  value=\"" + kintaiShinseiJikan3 + "\"  >" + 
				"";
		}

		$("#kihonNyuryokuArea").append(kihonNyuryokuAreaHtml);
	}
	$("#numShinseiKingaku01").val(kinShukkinBoResultAll[0]["numShinseiKingaku01"]);
	$("#numShinseiKingaku02").val(kinShukkinBoResultAll[0]["numShinseiKingaku02"]);
	$("#txtKihonSaishuKoshinDate").val(kinShukkinBoResultAll[0]["txtKihonSaishuKoshinDate"]);
	$("#txtKihonSaishuKoshinJikan").val(kinShukkinBoResultAll[0]["txtKihonSaishuKoshinJikan"]);


	//前・次一覧ボタンの活性変更
	if(firstHalfFlg == true){
		document.getElementById("btnFirstHalf").disabled = true;
		document.getElementById("btnSecondHalf").disabled = false;
		if(focusIndex != -1){
			//一覧表示時、出勤予定区分が空欄の最初の行をフォーカス
			$("#selShukkinYoteiKbn" + String(focusIndex)).focus();
		}
		else{
			//前一覧表示時、最初の予定をフォーカス
			$("#selShukkinYoteiKbn0").focus();
			setTimout(function(){
				$("#selShukkinYoteiKbn0").select();
				//20260813-全選択追加
			});
		}
	}
	else{
		document.getElementById("btnFirstHalf").disabled = false;
		document.getElementById("btnSecondHalf").disabled = true;
		if(focusIndex != -1){
			//一覧表示時、出勤予定区分が空欄の最初の行をフォーカス
			$("#selShukkinYoteiKbn" + String(focusIndex)).focus();
		}
		else{
			//次一覧表示時、最後の予定をフォーカス
			$("#selShukkinYoteiKbn" + (kinShukkinBoResultAll.length - 1)).focus()
			setTimeout(function(){
				$("#selShukkinYoteiKbn" + (kinShukkinBoResultAll.length - 1)).select();
				//20260813-全選択追加
			});

		}
	}


}

function changeShukkinYotei(nowRow){
	//予定区分をクリアしたら、同行の項目をクリア
	if(
		$("#selShukkinYoteiKbn" + nowRow).val() == "" ||
		$("#selShukkinYoteiKbn" + nowRow).val() == "00"
	){
		let fieldName = "selKintaiKbn";
		$("#" + fieldName + nowRow).val("00");
		kintaiChangeColor(document.getElementById (fieldName + nowRow));
		setShukkinBo(fieldName, nowRow);
		
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
		
		fieldName = "txtKintaiShinseiBiko";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "selKintaiShinseiKbn1";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiKaishiJi1";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiKaishiFun1";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoJi1";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoFun1";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiJikan1";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "selKintaiShinseiKbn2";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiKaishiJi2";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiKaishiFun2";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoJi2";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoFun2";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiJikan2";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
		fieldName = "selKintaiShinseiKbn3";
		$("#" + fieldName + nowRow).val("00");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiKaishiJi3";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiKaishiFun3";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoJi3";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoFun3";
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiJikan3";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
		
	}
	
}

/*
*
* 入力した値を0埋め2桁に変換
*
*/
function set2ketaFormat(fieldName, nowRow){
	let wk = $("#" + fieldName + nowRow).val();
	let checkIfNumber = /^[0-9]+$/;
	//数字で入力済みの時のみ0埋めする
	if(wk != "" && checkIfNumber.test(wk)){
		$("#" + fieldName + nowRow).val(("00" + String(wk)).slice(-2));
	}
}

/*
*
* 入力した値を小数点以下2桁0埋めに変換
*
*/
function setDecimalPoint2ketaFormat(fieldName, nowRow){
	let wk = Number($("#" + fieldName + nowRow).val());
	//入力された値が数値なら小数点以下2桁0埋め処理を行う
	if(Number.isNaN(wk) == false){
		$("#" + fieldName + nowRow).val(wk.toFixed(2));
	}
}

/*
*
* 入力した値を内部的な配列に取得
*
*/
function setShukkinBo(fieldName, nowRow){
	kinShukkinBoResultAll[nowRow][fieldName] = $("#" + fieldName + nowRow).val();
}

/*
*
* 入力した値を内部的な配列に取得
*
*/
function setShinseiKingaku01(){
	kinShukkinBoResultAll[0]["numShinseiKingaku01"] = $("#numShinseiKingaku01").val();
}

/*
*
* 入力した値を内部的な配列に取得
*
*/
function setShinseiKingaku02(){
	kinShukkinBoResultAll[0]["numShinseiKingaku02"] = $("#numShinseiKingaku02").val();
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
function kintaiChangeColor(kintai){
	if( kintai.value == "03" ){
		kintai.style.color = 'green';
	}
	else if( kintai.value == "04" || kintai.value == "05" || kintai.value == "08" || kintai.value == "10" ){
		kintai.style.color = 'red';
	}
	else {
		kintai.style.color = 'black';
	}
}

function setDefaultJitsudoJikan(nowRow){
	let fieldName = "selKintaiKbn";
	
	//勤怠区分が"00"(-)の場合は出社退社時間をクリアする
	if(($("#" + fieldName + nowRow).val() == "00")){
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

		//取得しておいた実働時間を設定
		fieldName = "numJitsudoJikan";
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
	}
	//勤怠区分が"01"(出勤)または"02"(出張)　かつ　出勤簿入力区分が"00"(月給)の場合のみ実行する
	else if(
		($("#" + fieldName + nowRow).val() == "01") || ($("#" + fieldName + nowRow).val() == "02") &&
		shukinboKbn == "00"
	){
		//取得しておいた勤務開始/終了時刻を設定
		fieldName = "numShusshaJi";
		$("#" + fieldName + nowRow).val(kinmuKaishiJi);
		setShukkinBo(fieldName, nowRow);
		fieldName = "numShusshaFun";
		$("#" + fieldName + nowRow).val(kinmuKaishiFun);
		setShukkinBo(fieldName, nowRow);
		fieldName = "numTaishaJi";
		$("#" + fieldName + nowRow).val(kinmuShuryoJi);
		setShukkinBo(fieldName, nowRow);
		fieldName = "numTaishaFun";
		$("#" + fieldName + nowRow).val(kinmuShuryoFun);
		setShukkinBo(fieldName, nowRow);

		//取得しておいた実働時間を設定
		fieldName = "numJitsudoJikan";
		$("#" + fieldName + nowRow).val(keiyakuJitsudoJikan);
		setShukkinBo(fieldName, nowRow);
	}
}

function resetJikan(nowCol,nowRow){
	let fieldName = "selKintaiShinseiKbn" + nowCol;
	
	//勤怠区分が"00"(-)の場合は出社退社時間をクリアする
	if(($("#" + fieldName + nowRow).val() == "00")){
		fieldName = "numKintaiShinseiKaishiJi" + nowCol;
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiKaishiFun" + nowCol;
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoJi" + nowCol;
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiShuryoFun" + nowCol;
		$("#" + fieldName + nowRow).val("");
		setShukkinBo(fieldName, nowRow);
		fieldName = "numKintaiShinseiJikan" + nowCol;
		$("#" + fieldName + nowRow).val("0.00");
		setShukkinBo(fieldName, nowRow);
	}
}

/*
*
* 20260815 追加
* 出勤・退社時間　入力制御
*
*/
function disabledShusshaTaishaJikan(nowRow){
	let fieldName = "selKintaiKbn";
	
	//勤怠区分が03(欠勤),04(有給休暇),06(積立有給),07(特別休暇),08(休日),09(代休),10(振替休日)の時
	//出社、退社の入力欄を入力不可に変更
	if(    $("#" + fieldName + nowRow).val() == "03" || $("#" + fieldName + nowRow).val() == "04" 
		|| $("#" + fieldName + nowRow).val() == "06" || $("#" + fieldName + nowRow).val() == "07"
		|| $("#" + fieldName + nowRow).val() == "08" || $("#" + fieldName + nowRow).val() == "09"
		|| $("#" + fieldName + nowRow).val() == "10"){
		
		fieldName = "numShusshaJi";
		$("#" + fieldName + nowRow).prop("disabled", true);
		fieldName = "numShusshaFun";
		$("#" + fieldName + nowRow).prop("disabled", true);
		fieldName = "numTaishaJi";
		$("#" + fieldName + nowRow).prop("disabled", true);
		fieldName = "numTaishaFun";
		$("#" + fieldName + nowRow).prop("disabled", true);
		
	}else {
		//それ以外を選択したときに、入力可能に変更
		fieldName = "numShusshaJi";
		$("#" + fieldName + nowRow).prop("disabled", false);
		fieldName = "numShusshaFun";
		$("#" + fieldName + nowRow).prop("disabled", false);
		fieldName = "numTaishaJi";
		$("#" + fieldName + nowRow).prop("disabled", false);
		fieldName = "numTaishaFun";
		$("#" + fieldName + nowRow).prop("disabled", false);
	}
}


/*
*
* 20260819 エンターキー タブ移動制御追加
*
*/
function onShukkinBoEnterKeyEvent($current){

	// フォーカス対象(予定/勤怠区分/button)
	const selector = `select[id^="selShukkinYoteiKbn"],select[id^="selKintaiKbn"],button`;
	
	let $focusables = $(selector)

	// button は特定属性を持つものだけフォーカス
		.filter(function() {

			if ($(this).is('button')) {

				// buttonAreaがhiddenの場合は、data-focusのon/offに関係なくフォーカスしない
				const $parentArea = $(this).closest('#buttonArea');
				if ($parentArea.length && $parentArea.css('visibility') === 'hidden') {
					return false;
				}

				// ※data-focus="off" のボタンだけフォーカスしない
				return !($(this).data('focus') === 'off');
			}
			return true;

		});
	// 現在のインデックス
	const index = $focusables.index($current);


	// 次の要素へフォーカス移動
	if (index >= 0 && index < $focusables.length - 1) {
		$focusables.eq(index + 1).focus();
	} else {
		// 最初の項目にフォーカス移動
		if (0 < $focusables.length) {
			$focusables.eq(0).focus();
		}
	}
	
}


/*
*
* 開始時分、終了時分から時間を計算
*
*/
function calcJitsudoJikan(nowRow){
	let checkIfNumber = /^[0-9]+$/;

	let kaishiJi = kinShukkinBoResultAll[nowRow]["numShusshaJi"];
	let kaishiFun = kinShukkinBoResultAll[nowRow]["numShusshaFun"];
	let shuryoJi = kinShukkinBoResultAll[nowRow]["numTaishaJi"];
	let shuryoFun = kinShukkinBoResultAll[nowRow]["numTaishaFun"];
	
	
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
			
			kinShukkinBoResultAll[nowRow]["numJitsudoJikan"] = jikanDisp;
			$("#numJitsudoJikan" + nowRow).val(jikanDisp);
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

	let kaishiJi = kinShukkinBoResultAll[nowRow]["numKintaiShinseiKaishiJi" + nowCol];
	let kaishiFun = kinShukkinBoResultAll[nowRow]["numKintaiShinseiKaishiFun" + nowCol];
	let shuryoJi = kinShukkinBoResultAll[nowRow]["numKintaiShinseiShuryoJi" + nowCol];
	let shuryoFun = kinShukkinBoResultAll[nowRow]["numKintaiShinseiShuryoFun" + nowCol];
	
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
			
			kinShukkinBoResultAll[nowRow]["numKintaiShinseiJikan" + nowCol] = jikanDisp;
			$("#numKintaiShinseiJikan" + nowCol + nowRow).val(jikanDisp);
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
					onSearchKinShukkinBo();
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
//				$("#srhTxtTaishoYM").val($("#txtTaishoYM").val());
//				$("#srhTxtShainNO").val($("#txtShainNO").val());
//				onSearchShainName();//社員名再取得
//	
//				//検索結果が0の時のため、画面非表示
//				$("#nyuryokuArea").css("visibility", "hidden");
//				$("#buttonArea").css("visibility", "hidden");
//				onSearchKinShukkinBo();
				
				//20260815 初期表示に変更
				movContents('kinShukkinBo');
			}
			else{
				alert("このデータはすでに、別のユーザーに更新されています。\r\nもう一度データを確認してください。");
			}
		});
	});
}
