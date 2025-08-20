let kinShukkinBoResultAll = [];
let shinseiKingaku01 = 0;
let shinseiKingaku02 = 0;

let yoteiList = [];
let kintaiKubunList = [];
let sinseiKubunList = [];

/*
*
* 初期値設定
*
*/
window.onload = function(){
	proc("getTaishoYM", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];
		
		$("#txtTaishoYM").val(result);
		$("#txtSearchedTaishoYM").val(result);
	});
	
	proc("getShainNO", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];
		
		$("#txtShainNO").val(result);
		$("#txtSearchedShainNO").val(result);
		
		getShainName('txtShainNO', 'txtShainName');
		
	});

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
	
	//ログイン社員の社員区分が"04"(個人)の場合、社員NOは入力不可にする
	proc("getLoginUserkbn", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];

		if(result == "04"){
			document.getElementById("txtShainNO").readOnly = true;
			document.getElementById("txtShainNO").disabled = true;
			document.getElementById("linkShainSearch").onclick = "";
			document.getElementById("linkShainSearch").tabIndex = "-1";
			document.getElementById("btnShainSearch").onclick = "";
		}
	});
	
	
}

/*
*
* 対象年月フォーカスアウト時のフォーマット編集処理
*
*/
function getTaishoYMFormat(){
	let strReplacing = $("#txtTaishoYM").val();
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

	$("#txtTaishoYM").val(strReplaced);
}

/*
*
* 対象年月フォーカスアウト時のフォーマット編集処理
*
*/
function getShainNOFormat(){
	//作業用隠し項目に、0埋めした社員NOをセット
	$("#hdnWkShainNO").val(right("0000" + $("#txtShainNO").val(), 4));
	//0埋めした社員NOで社員名を取得
	getShainName('hdnWkShainNO', 'txtShainName');
	//社員名を取得できたら、社員NO項目に「0埋めした社員NO」をセット
	if($("#txtShainName").val() != ""){
		$("#txtShainNO").val($("#hdnWkShainNO").val());
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
	//更新処理に備え、検索条件を保持
	$("#txtSearchedTaishoYM").val($("#txtTaishoYM").val());
	$("#txtSearchedShainNO").val($("#txtShainNO").val());

	//検索結果が0の時のため、画面非表示
	$("#nyuryokuArea").css("visibility", "hidden");
	$("#buttonArea").css("visibility", "hidden");
	document.getElementById("btnDelete").disabled = true;
	document.getElementById("btnUpdate").disabled = true;
	
	let honshaKakuteizumiFlg = false;

	//検索結果表示
	proc("search", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		//検索結果があれば入力項目表示
		$("#nyuryokuArea").css("visibility", "");

		let result			= contents["result"];
		kinShukkinBoResultAll = result;

		onDisplayNyuryokuArea(true);

		if(kinShukkinBoResultAll[0]["KakuteiKbn"] == "03"){
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
	
	for(let i = 0; i < kinShukkinBoResultAll.length; i++){
		let record = kinShukkinBoResultAll[i];
		let taishoNengappi = record["TaishoNengappi"];
		let taishoGetsu = ("00" + record["TaishoGetsu"]).slice(-2);
		let taishoBi = ("00" + record["TaishoBi"]).slice(-2);
		let yobiKbn = record["YobiKbn"];
		let shukkinYoteiKbn = record["ShukkinYoteiKbn"];
		let kintaiKbn = record["KintaiKbn"];

		let shusshaJi =		record["ShusshaJi"];
		let shusshaFun =	record["ShusshaFun"];
		let taishaJi =		record["TaishaJi"];
		let taishaFun =		record["TaishaFun"];
		let jitsudoJikan =	record["JitsudoJikan"];

		let kintaiShinseiBiko = record["KintaiShinseiBiko"];
		let kintaiShinseiKbn1 = record["KintaiShinseiKbn1"];
		let kintaiShinseiKaishiJi1 = record["KintaiShinseiKaishiJi1"];
		let kintaiShinseiKaishiFun1 = record["KintaiShinseiKaishiFun1"];
		let kintaiShinseiShuryoJi1 = record["KintaiShinseiShuryoJi1"];
		let kintaiShinseiShuryoFun1 = record["KintaiShinseiShuryoFun1"];
		let kintaiShinseiJikan1 = record["KintaiShinseiJikan1"];
		let kintaiShinseiKbn2 = record["KintaiShinseiKbn2"];
		let kintaiShinseiKaishiJi2 = record["KintaiShinseiKaishiJi2"];
		let kintaiShinseiKaishiFun2 = record["KintaiShinseiKaishiFun2"];
		let kintaiShinseiShuryoJi2 = record["KintaiShinseiShuryoJi2"];
		let kintaiShinseiShuryoFun2 = record["KintaiShinseiShuryoFun2"];
		let kintaiShinseiJikan2 = record["KintaiShinseiJikan2"];
		let kintaiShinseiKbn3 = record["KintaiShinseiKbn3"];
		let kintaiShinseiKaishiJi3 = record["KintaiShinseiKaishiJi3"];
		let kintaiShinseiKaishiFun3 = record["KintaiShinseiKaishiFun3"];
		let kintaiShinseiShuryoJi3 = record["KintaiShinseiShuryoJi3"];
		let kintaiShinseiShuryoFun3 = record["KintaiShinseiShuryoFun3"];
		let kintaiShinseiJikan3 = record["KintaiShinseiJikan3"];
		
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
		
		
		//予定のセレクトボックス
		let yoteiSelectBox = "";
		yoteiSelectBox += 	"<select class=\"kinShukkinBoText\" name=\"ShukkinYoteiKbn" + i + "\" id=\"ShukkinYoteiKbn" + i + "\" value=\"" + shukkinYoteiKbn + "\" " ;
		if(shukkinYoteiKbn == "02" || shukkinYoteiKbn == "03"){
			yoteiSelectBox += 		"style = \"COLOR: red\" ";
		}
		else{
			yoteiSelectBox += 		"style = \"COLOR: black\" ";
		}
		yoteiSelectBox += 		"onchange=\"yoteiChangeColor(this);setShukkinBo('ShukkinYoteiKbn', " + i + ");\" >" ;

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
		kintaiSelectBox += 	"<select class=\"kinShukkinBoText\" name=\"KintaiKbn" + i + "\" id=\"KintaiKbn" + i + "\" value=\"" + kintaiKbn + "\" " ;
		if(kintaiKbn == "04" || kintaiKbn == "05" || kintaiKbn == "08" || kintaiKbn == "10"){
			kintaiSelectBox += 		"style = \"COLOR: red\" ";
		}
		else if(kintaiKbn == "03"){
			kintaiSelectBox += 		"style = \"COLOR: green\" ";
		}
		else{
			kintaiSelectBox += 		"style = \"COLOR: black\" ";
		}
		kintaiSelectBox += 		"onchange=\"kintaiChangeColor(this);setShukkinBo('KintaiKbn', " + i + ");\" >" ;

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
		sinsei1SelectBox += 	"<select class=\"kinShukkinBoText\" name=\"KintaiShinseiKbn1" + i + "\" id=\"KintaiShinseiKbn1" + i + "\" value=\"" + kintaiShinseiKbn1 + "\"  onchange=\"setShukkinBo('KintaiShinseiKbn1', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei1SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(kintaiShinseiKbn1 == sinseiKubunRecord["Code"]){
				sinsei1SelectBox += 		"selected";
			}
			sinsei1SelectBox += 		"><a>" + sinseiKubunRecord["KbnName"] + "</a></option>" ;
		}
		
		//申請区分2のセレクトボックス
		let sinsei2SelectBox = "";
		sinsei2SelectBox += 	"<select class=\"kinShukkinBoText\" name=\"KintaiShinseiKbn2" + i + "\" id=\"KintaiShinseiKbn2" + i + "\" value=\"" + kintaiShinseiKbn2 + "\"  onchange=\"setShukkinBo('KintaiShinseiKbn2', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei2SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(kintaiShinseiKbn2 == sinseiKubunRecord["Code"]){
				sinsei2SelectBox += 		"selected";
			}
			sinsei2SelectBox += 		"><a>" + sinseiKubunRecord["KbnName"] + "</a></option>" ;
		}
		
		//申請区分3のセレクトボックス
		let sinsei3SelectBox = "";
		sinsei3SelectBox += 	"<select class=\"kinShukkinBoText\" name=\"KintaiShinseiKbn3" + i + "\" id=\"KintaiShinseiKbn3" + i + "\" value=\"" + kintaiShinseiKbn3 + "\"  onchange=\"setShukkinBo('KintaiShinseiKbn3', " + i + ");\" >" ;

		for(let sinseiKubunRecord of sinseiKubunList){
			sinsei3SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
			if(kintaiShinseiKbn3 == sinseiKubunRecord["Code"]){
				sinsei3SelectBox += 		"selected";
			}
			sinsei3SelectBox += 		"><a>" + sinseiKubunRecord["KbnName"] + "</a></option>" ;
		}
		
		let kihonNyuryokuAreaHtml = "";
		if(
			(firstHalfFlg == true && record["TaishoGetsu"] == kinShukkinBoResultAll[0]["TaishoGetsu"]) ||
			(firstHalfFlg != true && record["TaishoGetsu"] != kinShukkinBoResultAll[0]["TaishoGetsu"])
		){
			kihonNyuryokuAreaHtml =
				"<tr>" +
					"<input type=\"hidden\" name=\"TaishoNengappi" + i + "\" id=\"TaishoNengappi" + i + "\" value=\"" + taishoNengappi + "\">" +
					"<input type=\"hidden\" name=\"MeisaiSaishuKoshinDate" + i + "\" id=\"MeisaiSaishuKoshinDate" + i + "\" value=\"" + meisaiSaishuKoshinDate + "\">" +
					"<input type=\"hidden\" name=\"MeisaiSaishuKoshinJikan" + i + "\" id=\"MeisaiSaishuKoshinJikan" + i + "\" value=\"" + meisaiSaishuKoshinJikan + "\">" +
					
					"<td class=\"value center\"><a class=\"kinShukkinBoText\">" + taishoGetsu + "</a></td>" +
					"<input type=\"hidden\" name=\"TaishoGetsu" + i + "\" id=\"TaishoGetsu" + i + "\" value=\"" + taishoGetsu + "\">" +
					"<td class=\"value center\"><a class=\"kinShukkinBoText\">" + taishoBi + "</a></td>" +
					"<input type=\"hidden\" name=\"TaishoBi" + i + "\" id=\"TaishoBi" + i + "\" value=\"" + taishoBi + "\">" +
					"<td class=\"value center\"><a class=\"kinShukkinBoText " + yobiColorClass + "\">" + yobiKbn + "</a></td>" +
					"<td class=\"value center\">" + 
						yoteiSelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						kintaiSelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"ShusshaJi" + i + "\" id=\"ShusshaJi" + i + "\"  value=\"" + shusshaJi + "\"  onchange=\"setShukkinBo('ShusshaJi', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"ShusshaFun" + i + "\" id=\"ShusshaFun" + i + "\"  value=\"" + shusshaFun + "\"  onchange=\"setShukkinBo('ShusshaFun', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<a class=\"kinShukkinBoText\">-</a>" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"TaishaJi" + i + "\" id=\"TaishaJi" + i + "\"  value=\"" + taishaJi + "\"  onchange=\"setShukkinBo('TaishaJi', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"TaishaFun" + i + "\" id=\"TaishaFun" + i + "\"  value=\"" + taishaFun + "\"  onchange=\"setShukkinBo('TaishaFun', " + i + ");calcJitsudoJikan(" + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"JitsudoJikan" + i + "\" id=\"JitsudoJikan" + i + "\"  value=\"" + jitsudoJikan + "\"  onchange=\"setShukkinBo('JitsudoJikan', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText bikoTextBox\"  maxlength=\"40\" name=\"KintaiShinseiBiko" + i + "\" id=\"KintaiShinseiBiko" + i + "\"  value=\"" + kintaiShinseiBiko + "\"  onchange=\"setShukkinBo('KintaiShinseiBiko', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei1SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiKaishiJi1" + i + "\" id=\"KintaiShinseiKaishiJi1" + i + "\"  value=\"" + kintaiShinseiKaishiJi1 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiJi1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiKaishiFun1" + i + "\" id=\"KintaiShinseiKaishiFun1" + i + "\"  value=\"" + kintaiShinseiKaishiFun1 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiFun1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiShuryoJi1" + i + "\" id=\"KintaiShinseiShuryoJi1" + i + "\"  value=\"" + kintaiShinseiShuryoJi1 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoJi1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiShuryoFun1" + i + "\" id=\"KintaiShinseiShuryoFun1" + i + "\"  value=\"" + kintaiShinseiShuryoFun1 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoFun1', " + i + ");calcShinseiJikan(1, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"KintaiShinseiJikan1" + i + "\" id=\"KintaiShinseiJikan1" + i + "\"  value=\"" + kintaiShinseiJikan1 + "\"  onchange=\"setShukkinBo('KintaiShinseiJikan1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei2SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiKaishiJi2" + i + "\" id=\"KintaiShinseiKaishiJi2" + i + "\"  value=\"" + kintaiShinseiKaishiJi2 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiJi2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiKaishiFun2" + i + "\" id=\"KintaiShinseiKaishiFun2" + i + "\"  value=\"" + kintaiShinseiKaishiFun2 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiFun2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiShuryoJi2" + i + "\" id=\"KintaiShinseiShuryoJi2" + i + "\"  value=\"" + kintaiShinseiShuryoJi2 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoJi2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiShuryoFun2" + i + "\" id=\"KintaiShinseiShuryoFun2" + i + "\"  value=\"" + kintaiShinseiShuryoFun2 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoFun2', " + i + ");calcShinseiJikan(2, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"KintaiShinseiJikan2" + i + "\" id=\"KintaiShinseiJikan2" + i + "\"  value=\"" + kintaiShinseiJikan2 + "\"  onchange=\"setShukkinBo('KintaiShinseiJikan2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						sinsei3SelectBox + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiKaishiJi3" + i + "\" id=\"KintaiShinseiKaishiJi3" + i + "\"  value=\"" + kintaiShinseiKaishiJi3 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiJi3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiKaishiFun3" + i + "\" id=\"KintaiShinseiKaishiFun3" + i + "\"  value=\"" + kintaiShinseiKaishiFun3 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiFun3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiShuryoJi3" + i + "\" id=\"KintaiShinseiShuryoJi3" + i + "\"  value=\"" + kintaiShinseiShuryoJi3 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoJi3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText kaishishuryoTextBox\"  maxlength=\"2\" name=\"KintaiShinseiShuryoFun3" + i + "\" id=\"KintaiShinseiShuryoFun3" + i + "\"  value=\"" + kintaiShinseiShuryoFun3 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoFun3', " + i + ");calcShinseiJikan(3, " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center\">" + 
						"<input type=\"text\" class=\"kinShukkinBoText jikanTextBox\"  maxlength=\"5\" name=\"KintaiShinseiJikan3" + i + "\" id=\"KintaiShinseiJikan3" + i + "\"  value=\"" + kintaiShinseiJikan3 + "\"  onchange=\"setShukkinBo('KintaiShinseiJikan3', " + i + ");\" >" + 
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

					"<input type=\"hidden\" name=\"ShukkinYoteiKbn" + i + "\" id=\"ShukkinYoteiKbn" + i + "\" value=\"" + shukkinYoteiKbn + "\">" +
					"<input type=\"hidden\" name=\"KintaiKbn" + i + "\" id=\"KintaiKbn" + i + "\" value=\"" + kintaiKbn + "\">" +
					
					"<input type=\"hidden\" name=\"ShusshaJi" + i + "\" id=\"ShusshaJi" + i + "\"  value=\"" + shusshaJi + "\"  >" + 
					"<input type=\"hidden\" name=\"ShusshaFun" + i + "\" id=\"ShusshaFun" + i + "\"  value=\"" + shusshaFun + "\"  >" + 
					"<input type=\"hidden\" name=\"TaishaJi" + i + "\" id=\"TaishaJi" + i + "\"  value=\"" + taishaJi + "\"  >" + 
					"<input type=\"hidden\" name=\"TaishaFun" + i + "\" id=\"TaishaFun" + i + "\"  value=\"" + taishaFun + "\"  >" + 
					"<input type=\"hidden\" name=\"JitsudoJikan" + i + "\" id=\"JitsudoJikan" + i + "\"  value=\"" + jitsudoJikan + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiBiko" + i + "\" id=\"KintaiShinseiBiko" + i + "\"  value=\"" + kintaiShinseiBiko + "\"  >" + 

					"<input type=\"hidden\" name=\"KintaiShinseiKbn1" + i + "\" id=\"KintaiShinseiKbn1" + i + "\" value=\"" + kintaiShinseiKbn1 + "\">" +
					"<input type=\"hidden\" name=\"KintaiShinseiKaishiJi1" + i + "\" id=\"KintaiShinseiKaishiJi1" + i + "\"  value=\"" + kintaiShinseiKaishiJi1 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiKaishiFun1" + i + "\" id=\"KintaiShinseiKaishiFun1" + i + "\"  value=\"" + kintaiShinseiKaishiFun1 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiShuryoJi1" + i + "\" id=\"KintaiShinseiShuryoJi1" + i + "\"  value=\"" + kintaiShinseiShuryoJi1 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiShuryoFun1" + i + "\" id=\"KintaiShinseiShuryoFun1" + i + "\"  value=\"" + kintaiShinseiShuryoFun1 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiJikan1" + i + "\" id=\"KintaiShinseiJikan1" + i + "\"  value=\"" + kintaiShinseiJikan1 + "\"  >" + 

					"<input type=\"hidden\" name=\"KintaiShinseiKbn2" + i + "\" id=\"KintaiShinseiKbn2" + i + "\" value=\"" + kintaiShinseiKbn2 + "\">" +
					"<input type=\"hidden\" name=\"KintaiShinseiKaishiJi2" + i + "\" id=\"KintaiShinseiKaishiJi2" + i + "\"  value=\"" + kintaiShinseiKaishiJi2 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiKaishiFun2" + i + "\" id=\"KintaiShinseiKaishiFun2" + i + "\"  value=\"" + kintaiShinseiKaishiFun2 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiShuryoJi2" + i + "\" id=\"KintaiShinseiShuryoJi2" + i + "\"  value=\"" + kintaiShinseiShuryoJi2 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiShuryoFun2" + i + "\" id=\"KintaiShinseiShuryoFun2" + i + "\"  value=\"" + kintaiShinseiShuryoFun2 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiJikan2" + i + "\" id=\"KintaiShinseiJikan2" + i + "\"  value=\"" + kintaiShinseiJikan2 + "\"  >" + 

					"<input type=\"hidden\" name=\"KintaiShinseiKbn3" + i + "\" id=\"KintaiShinseiKbn3" + i + "\" value=\"" + kintaiShinseiKbn3 + "\">" +
					"<input type=\"hidden\" name=\"KintaiShinseiKaishiJi3" + i + "\" id=\"KintaiShinseiKaishiJi3" + i + "\"  value=\"" + kintaiShinseiKaishiJi3 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiKaishiFun3" + i + "\" id=\"KintaiShinseiKaishiFun3" + i + "\"  value=\"" + kintaiShinseiKaishiFun3 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiShuryoJi3" + i + "\" id=\"KintaiShinseiShuryoJi3" + i + "\"  value=\"" + kintaiShinseiShuryoJi3 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiShuryoFun3" + i + "\" id=\"KintaiShinseiShuryoFun3" + i + "\"  value=\"" + kintaiShinseiShuryoFun3 + "\"  >" + 
					"<input type=\"hidden\" name=\"KintaiShinseiJikan3" + i + "\" id=\"KintaiShinseiJikan3" + i + "\"  value=\"" + kintaiShinseiJikan3 + "\"  >" + 
				"";
		}

		$("#kihonNyuryokuArea").append(kihonNyuryokuAreaHtml);
	}
	$("#txtShinseiKingaku01").val(kinShukkinBoResultAll[0]["ShinseiKingaku01"]);
	$("#txtShinseiKingaku02").val(kinShukkinBoResultAll[0]["ShinseiKingaku02"]);
	$("#hdnKihonSaishuKoshinDate").val(kinShukkinBoResultAll[0]["KihonSaishuKoshinDate"]);
	$("#hdnKihonSaishuKoshinJikan").val(kinShukkinBoResultAll[0]["KihonSaishuKoshinJikan"]);


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
		$("#ShukkinYoteiKbn" + (kinShukkinBoResultAll.length - 1)).focus();
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
	kinShukkinBoResultAll[0]["ShinseiKingaku01"] = $("#txtShinseiKingaku01").val();
}

/*
*
* 入力した値を内部的な配列に取得
*
*/
function setShinseiKingaku02(){
	kinShukkinBoResultAll[0]["ShinseiKingaku02"] = $("#txtShinseiKingaku02").val();
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

/*
*
* 開始時分、終了時分から時間を計算
*
*/
function calcJitsudoJikan(nowRow){
	let checkIfNumber = /^[0-9]+$/;

	let kaishiJi = kinShukkinBoResultAll[nowRow]["ShusshaJi"];
	let kaishiFun = kinShukkinBoResultAll[nowRow]["ShusshaFun"];
	let shuryoJi = kinShukkinBoResultAll[nowRow]["TaishaJi"];
	let shuryoFun = kinShukkinBoResultAll[nowRow]["TaishaFun"];
	
	let jikan = kinShukkinBoResultAll[nowRow]["JitsudoJikan"];
	
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
			let jikanDisp = jikanJi + (jikanFun * 0.01);
			
			kinShukkinBoResultAll[nowRow]["JitsudoJikan"] = jikanDisp.toFixed(2);
			$("#JitsudoJikan" + nowRow).val(jikanDisp.toFixed(2));
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

	let kaishiJi = kinShukkinBoResultAll[nowRow]["KintaiShinseiKaishiJi" + nowCol];
	let kaishiFun = kinShukkinBoResultAll[nowRow]["KintaiShinseiKaishiFun" + nowCol];
	let shuryoJi = kinShukkinBoResultAll[nowRow]["KintaiShinseiShuryoJi" + nowCol];
	let shuryoFun = kinShukkinBoResultAll[nowRow]["KintaiShinseiShuryoFun" + nowCol];
	
	let jikan = kinShukkinBoResultAll[nowRow]["KintaiShinseiJikan" + nowCol];
	
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
			let jikanDisp = jikanJi + (jikanFun * 0.01);
			
			kinShukkinBoResultAll[nowRow]["KintaiShinseiJikan" + nowCol] = jikanDisp.toFixed(2);
			$("#KintaiShinseiJikan" + nowCol + nowRow).val(jikanDisp.toFixed(2));
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
	//削除処理呼び出し
	proc("delete", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];

		if(result == true){
			alert("正常に削除しました。");
		}
		else{
			alert("このデータはすでに、別のユーザーに更新されています。\r\nもう一度データを確認してください。");
		}
		document.getElementById("txtTaishoYM").focus();
		//画面表示を初期状態に戻す
		$("#nyuryokuArea").css("visibility", "hidden");
		$("#buttonArea").css("visibility", "hidden");
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
	//更新処理呼び出し
	proc("update", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];

		if(result == 1){
			alert("正常に登録しました。");
		}
		else if(result == 2){
			alert("正常に更新しました。");
		}
		else{
			alert("このデータはすでに、別のユーザーに更新されています。\r\nもう一度データを確認してください。");
		}
		document.getElementById("txtTaishoYM").focus();
		//画面表示を初期状態に戻す
		$("#nyuryokuArea").css("visibility", "hidden");
		$("#buttonArea").css("visibility", "hidden");
	});
}
