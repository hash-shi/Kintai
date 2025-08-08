let kinShukkinBoResultAll = [];
let shinseiKingaku01 = 0;
let shinseiKingaku02 = 0;

let yoteiList = [];
let kintaiKubunList = [];
let SinseiKubunList = [];

/*
*
* 初期値設定
*
*/
window.onload = function(){
	let defaultYM = "";
	proc("getTaishoYM", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];
		
		$("#txtTaishoYM").val(result);
		$("#hidgenzaishorinengetsudo").val(result);
	});

	proc("getDDL", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		let result			= contents["result"];

		console.log("getDDLのresult");
		console.log(result);
		for(let record of result){
			if(record["DDLName"] == "yotei"){
				yoteiList.push(record);
			}
			if(record["DDLName"] == "kintai"){
				kintaiKubunList.push(record);
			}
			if(record["DDLName"] == "shinsei"){
				SinseiKubunList.push(record);
			}
		}
	});


}

/*
*
* メニュー関連の処理を記入
*
*/
function onSearchKinShukkinBo(){

	let honshaKakuteizumiFlg = false;

	//検索結果表示
	proc("search", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		let contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		//検索結果があれば入力項目表示
		document.getElementById("nyuryokuArea").style.display = "";

		let result			= contents["result"];
		kinShukkinBoResultAll = result;

		onDisplayNyuryokuArea(true);

		if(kinShukkinBoResultAll[0]["KakuteiKbn"] == "03"){
			honshaKakuteizumiFlg = true;
		}
	});

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
		document.getElementById("btnDelete").disabled = true;
		document.getElementById("btnUpdate").disabled = true;
	}
	else{
		document.getElementById("btnDelete").disabled = false;
		document.getElementById("btnUpdate").disabled = false;
	}
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
		if(
			(firstHalfFlg == true && record["TaishoGetsu"] == kinShukkinBoResultAll[0]["TaishoGetsu"]) ||
			(firstHalfFlg != true && record["TaishoGetsu"] != kinShukkinBoResultAll[0]["TaishoGetsu"])
		){
			let taishoGetsu = record["TaishoGetsu"];
			let taishoBi = record["TaishoBi"];
			let yobiKbn = record["YobiKbn"];
			let shukkinYoteiKbn = record["ShukkinYoteiKbn"];
			let kintaiKbn = record["KintaiKbn"];
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

			// onclickで呼ぶ関数を共通化
//			let onclickCode = "setDialogReturnValue('" + shainNo + "'); closeDialog(); setDialogReturnValueShainName(); return false;";
			
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
			yoteiSelectBox += 	"<select name=\"ShukkinYoteiKbn" + i + "\" id=\"ShukkinYoteiKbn" + i + "\" value=\"" + shukkinYoteiKbn + "\" " ;
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
			kintaiSelectBox += 	"<select name=\"KintaiKbn" + i + "\" id=\"KintaiKbn" + i + "\" value=\"" + kintaiKbn + "\" " ;
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
			sinsei1SelectBox += 	"<select name=\"KintaiShinseiKbn1" + i + "\" id=\"KintaiShinseiKbn1" + i + "\" value=\"" + kintaiShinseiKbn1 + "\"  onchange=\"setShukkinBo('KintaiShinseiKbn1', " + i + ");\" >" ;

			for(let sinseiKubunRecord of SinseiKubunList){
				sinsei1SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
				if(kintaiShinseiKbn1 == sinseiKubunRecord["Code"]){
					sinsei1SelectBox += 		"selected";
				}
				sinsei1SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
			}
			
			//申請区分2のセレクトボックス
			let sinsei2SelectBox = "";
			sinsei2SelectBox += 	"<select name=\"KintaiShinseiKbn2" + i + "\" id=\"KintaiShinseiKbn2" + i + "\" value=\"" + kintaiShinseiKbn2 + "\"  onchange=\"setShukkinBo('KintaiShinseiKbn2', " + i + ");\" >" ;

			for(let sinseiKubunRecord of SinseiKubunList){
				sinsei2SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
				if(kintaiShinseiKbn2 == sinseiKubunRecord["Code"]){
					sinsei2SelectBox += 		"selected";
				}
				sinsei2SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
			}
			
			//申請区分3のセレクトボックス
			let sinsei3SelectBox = "";
			sinsei3SelectBox += 	"<select name=\"KintaiShinseiKbn3" + i + "\" id=\"KintaiShinseiKbn3" + i + "\" value=\"" + kintaiShinseiKbn3 + "\"  onchange=\"setShukkinBo('KintaiShinseiKbn3', " + i + ");\" >" ;

			for(let sinseiKubunRecord of SinseiKubunList){
				sinsei3SelectBox += 		"<option value=\"" + sinseiKubunRecord["Code"] + "\" ";
				if(kintaiShinseiKbn3 == sinseiKubunRecord["Code"]){
					sinsei3SelectBox += 		"selected";
				}
				sinsei3SelectBox += 		">" + sinseiKubunRecord["KbnName"] + "</option>" ;
			}
			





			let	kihonNyuryokuAreaHtml =
				"<tr>" +
					"<td class=\"value center w50\"><a >" + taishoGetsu + "</a></td>" +
					"<td class=\"value center w50\"><a >" + taishoBi + "</a></td>" +
					"<td class=\"value center w50\"><a class=\"" + yobiColorClass + "\">" + yobiKbn + "</a></td>" +
					"<td class=\"value center w50\">" + 
						yoteiSelectBox + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						kintaiSelectBox + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"KintaiShinseiBiko" + i + "\" id=\"KintaiShinseiBiko" + i + "\"  value=\"" + kintaiShinseiBiko + "\"  onchange=\"setShukkinBo('KintaiShinseiBiko', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"w50\"></td>" +
					"<td class=\"value center w100\">" + 
						sinsei1SelectBox + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiJi1" + i + "\" id=\"KintaiShinseiKaishiJi1" + i + "\"  value=\"" + kintaiShinseiKaishiJi1 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiJi1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiFun1" + i + "\" id=\"KintaiShinseiKaishiFun1" + i + "\"  value=\"" + kintaiShinseiKaishiFun1 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiFun1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoJi1" + i + "\" id=\"KintaiShinseiShuryoJi1" + i + "\"  value=\"" + kintaiShinseiShuryoJi1 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoJi1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoFun1" + i + "\" id=\"KintaiShinseiShuryoFun1" + i + "\"  value=\"" + kintaiShinseiShuryoFun1 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoFun1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiJikan1" + i + "\" id=\"KintaiShinseiJikan1" + i + "\"  value=\"" + kintaiShinseiJikan1 + "\"  onchange=\"setShukkinBo('KintaiShinseiJikan1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						sinsei2SelectBox + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiJi2" + i + "\" id=\"KintaiShinseiKaishiJi2" + i + "\"  value=\"" + kintaiShinseiKaishiJi2 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiJi2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiFun2" + i + "\" id=\"KintaiShinseiKaishiFun2" + i + "\"  value=\"" + kintaiShinseiKaishiFun2 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiFun2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoJi2" + i + "\" id=\"KintaiShinseiShuryoJi2" + i + "\"  value=\"" + kintaiShinseiShuryoJi2 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoJi2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoFun2" + i + "\" id=\"KintaiShinseiShuryoFun2" + i + "\"  value=\"" + kintaiShinseiShuryoFun2 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoFun2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiJikan2" + i + "\" id=\"KintaiShinseiJikan2" + i + "\"  value=\"" + kintaiShinseiJikan2 + "\"  onchange=\"setShukkinBo('KintaiShinseiJikan2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						sinsei3SelectBox + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiJi3" + i + "\" id=\"KintaiShinseiKaishiJi3" + i + "\"  value=\"" + kintaiShinseiKaishiJi3 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiJi3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiFun3" + i + "\" id=\"KintaiShinseiKaishiFun3" + i + "\"  value=\"" + kintaiShinseiKaishiFun3 + "\"  onchange=\"setShukkinBo('KintaiShinseiKaishiFun3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoJi3" + i + "\" id=\"KintaiShinseiShuryoJi3" + i + "\"  value=\"" + kintaiShinseiShuryoJi3 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoJi3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoFun3" + i + "\" id=\"KintaiShinseiShuryoFun3" + i + "\"  value=\"" + kintaiShinseiShuryoFun3 + "\"  onchange=\"setShukkinBo('KintaiShinseiShuryoFun3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiJikan3" + i + "\" id=\"KintaiShinseiJikan3" + i + "\"  value=\"" + kintaiShinseiJikan3 + "\"  onchange=\"setShukkinBo('KintaiShinseiJikan3', " + i + ");\" >" + 
					"</td>" +
				"</tr>";
			$("#kihonNyuryokuArea").append(kihonNyuryokuAreaHtml);
		}
	}


	if(firstHalfFlg == true){
		document.getElementById("btnFirstHalf").disabled = true;
		document.getElementById("btnSecondHalf").disabled = false;
	}
	else{
		document.getElementById("btnFirstHalf").disabled = false;
		document.getElementById("btnSecondHalf").disabled = true;
	}

	$("#txtShinseiKingaku01").val(kinShukkinBoResultAll[0]["ShinseiKingaku01"]);
	$("#txtShinseiKingaku02").val(kinShukkinBoResultAll[0]["ShinseiKingaku02"]);
}

/*
*
* 入力した値を内部的な配列に取得
*
*/
function setShukkinBo(fieldName, nowRow){
//	kinShukkinBoResultAll[nowRow][fieldName] = document.getElementById(fieldName + nowRow).value;
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


function yoteiChangeColor(yotei){
	if( yotei.value == "02" || yotei.value == "03" ){
		yotei.style.color = 'red';
	}
	else{
		yotei.style.color = 'black';
	}
}

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




function onDelete(){
	alert("onDelete押下");
}
function onUpdate(){
	alert("onUpdate押下");
}
