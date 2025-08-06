/*
*
* 初期値設定
*
*/
let kinShukkinBoResultAll = [];
let shinseiKingaku01 = 0;
let shinseiKingaku02 = 0;
window.onload = function(){
	let defaultYM = "";
	let today = new Date();
	
	if(today.getDate() > 15){
		today.setDate(1);
		today.setMonth(today.getMonth() + 1);
	}
	defaultYM = today.getFullYear() + "/" + (today.getMonth()+1).toString().padStart(2, '0');

	$("#txtTaishoYM").val(defaultYM);
}

/*
*
* メニュー関連の処理を記入
*
*/
function onSearchKinShukkinBo(){


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
			
			let	kihonNyuryokuAreaHtml =
				"<tr>" +
					"<td class=\"value center w50\"><a >" + taishoGetsu + "</a></td>" +
					"<td class=\"value center w50\"><a >" + taishoBi + "</a></td>" +
					"<td class=\"value center w50\"><a >" + yobiKbn + "</a></td>" +
					"<td class=\"value center w100\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"ShukkinYoteiKbn" + i + "\" id=\"ShukkinYoteiKbn" + i + "\"  value=\"" + shukkinYoteiKbn + "\"  onblur=\"setShukkinBo('ShukkinYoteiKbn', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"KintaiKbn" + i + "\" id=\"KintaiKbn" + i + "\"  value=\"" + kintaiKbn + "\"  onblur=\"setShukkinBo('KintaiKbn', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"KintaiShinseiBiko" + i + "\" id=\"KintaiShinseiBiko" + i + "\"  value=\"" + kintaiShinseiBiko + "\"  onblur=\"setShukkinBo('KintaiShinseiBiko', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"w50\"></td>" +
					"<td class=\"value center w100\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"KintaiShinseiKbn1" + i + "\" id=\"KintaiShinseiKbn1" + i + "\"  value=\"" + kintaiShinseiKbn1 + "\"  onblur=\"setShukkinBo('KintaiShinseiKbn1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiJi1" + i + "\" id=\"KintaiShinseiKaishiJi1" + i + "\"  value=\"" + kintaiShinseiKaishiJi1 + "\"  onblur=\"setShukkinBo('KintaiShinseiKaishiJi1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiFun1" + i + "\" id=\"KintaiShinseiKaishiFun1" + i + "\"  value=\"" + kintaiShinseiKaishiFun1 + "\"  onblur=\"setShukkinBo('KintaiShinseiKaishiFun1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoJi1" + i + "\" id=\"KintaiShinseiShuryoJi1" + i + "\"  value=\"" + kintaiShinseiShuryoJi1 + "\"  onblur=\"setShukkinBo('KintaiShinseiShuryoJi1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoFun1" + i + "\" id=\"KintaiShinseiShuryoFun1" + i + "\"  value=\"" + kintaiShinseiShuryoFun1 + "\"  onblur=\"setShukkinBo('KintaiShinseiShuryoFun1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiJikan1" + i + "\" id=\"KintaiShinseiJikan1" + i + "\"  value=\"" + kintaiShinseiJikan1 + "\"  onblur=\"setShukkinBo('KintaiShinseiJikan1', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"KintaiShinseiKbn2" + i + "\" id=\"KintaiShinseiKbn2" + i + "\"  value=\"" + kintaiShinseiKbn2 + "\"  onblur=\"setShukkinBo('KintaiShinseiKbn2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiJi2" + i + "\" id=\"KintaiShinseiKaishiJi2" + i + "\"  value=\"" + kintaiShinseiKaishiJi2 + "\"  onblur=\"setShukkinBo('KintaiShinseiKaishiJi2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiFun2" + i + "\" id=\"KintaiShinseiKaishiFun2" + i + "\"  value=\"" + kintaiShinseiKaishiFun2 + "\"  onblur=\"setShukkinBo('KintaiShinseiKaishiFun2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoJi2" + i + "\" id=\"KintaiShinseiShuryoJi2" + i + "\"  value=\"" + kintaiShinseiShuryoJi2 + "\"  onblur=\"setShukkinBo('KintaiShinseiShuryoJi2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoFun2" + i + "\" id=\"KintaiShinseiShuryoFun2" + i + "\"  value=\"" + kintaiShinseiShuryoFun2 + "\"  onblur=\"setShukkinBo('KintaiShinseiShuryoFun2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiJikan2" + i + "\" id=\"KintaiShinseiJikan2" + i + "\"  value=\"" + kintaiShinseiJikan2 + "\"  onblur=\"setShukkinBo('KintaiShinseiJikan2', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w100\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"KintaiShinseiKbn3" + i + "\" id=\"KintaiShinseiKbn3" + i + "\"  value=\"" + kintaiShinseiKbn3 + "\"  onblur=\"setShukkinBo('KintaiShinseiKbn3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiJi3" + i + "\" id=\"KintaiShinseiKaishiJi3" + i + "\"  value=\"" + kintaiShinseiKaishiJi3 + "\"  onblur=\"setShukkinBo('KintaiShinseiKaishiJi3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiKaishiFun3" + i + "\" id=\"KintaiShinseiKaishiFun3" + i + "\"  value=\"" + kintaiShinseiKaishiFun3 + "\"  onblur=\"setShukkinBo('KintaiShinseiKaishiFun3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoJi3" + i + "\" id=\"KintaiShinseiShuryoJi3" + i + "\"  value=\"" + kintaiShinseiShuryoJi3 + "\"  onblur=\"setShukkinBo('KintaiShinseiShuryoJi3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiShuryoFun3" + i + "\" id=\"KintaiShinseiShuryoFun3" + i + "\"  value=\"" + kintaiShinseiShuryoFun3 + "\"  onblur=\"setShukkinBo('KintaiShinseiShuryoFun3', " + i + ");\" >" + 
					"</td>" +
					"<td class=\"value center w50\">" + 
						"<input type=\"text\" class=\"\"  style=\"width: 40px\"\" name=\"KintaiShinseiJikan3" + i + "\" id=\"KintaiShinseiJikan3" + i + "\"  value=\"" + kintaiShinseiJikan3 + "\"  onblur=\"setShukkinBo('KintaiShinseiJikan3', " + i + ");\" >" + 
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
