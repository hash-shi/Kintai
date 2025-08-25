//****************************************************************************
// 初期処理
//
//
//
//
//****************************************************************************
$(document).ready(function(){
	var inputform = document.getElementById("inputform");
	inputform.onsubmit = function onsubmit(e) { 
		e.preventDefault();
		return false
	};
	
	// セッションストレージの削除
	sessionStorage.clear()
	
});

//****************************************************************************
// onLogin
//
//
//
//
//****************************************************************************
function onLogin(){

	proc("login",{}, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		if(contents["result"]){
			location.href			= "./menu";
		} else {
			// ログインに失敗したらMessageを出す(自分が分かる用)
			if (contents["message"] == undefined){ return; }
			alert(contents["message"]);
		}
	});
}
