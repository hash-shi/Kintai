/*
*
* メニュー関連の処理を記入
*
*/
function onSearchKinShukkinBo(){

	proc("search", {}, function(data){

		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }
		
		var contents		= data["contents"];
		if (contents["result"] == undefined){ return; }
		
		var result			= contents["result"];
		
		console.log(result);
//		// 検索結果エリアをクリアする。
//		$("#searchStaffResult").children().remove();
//		
//		for (var count = 0 ; count < result.length ; count++){
//			var record			= result[count];
//		}
		
	});

}
