/*
*
* パスワード変更関連の処理を記入
*
*/

//****************************************************************************
// onUpdate
// パスワード変更
//
//
//
//****************************************************************************
function onUpdate() {
	
	proc("update" ,{}, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents				= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		if(contents["result"]){
			
			// 完了メッセージ
			alert("パスワードを変更しました。");
			
			// 画面上の隠し項目を更新する。
			$("#hdnPassword").val(contents["password"]);
			$("#hdnSaishuKoshinDate").val(contents["saishuKoshinDate"]);
			$("#hdnSaishuKoshinJikan").val(contents["saishuKoshinJikan"]);
			
			// 入力項目のリセット
			$("#txtPasswordNow").val("");
			$("#txtPasswordNew").val("");
			$("#txtPasswordRe").val("");
		}
	});
}