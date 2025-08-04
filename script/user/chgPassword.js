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
			alert("変更しました。");
		}
	});
}