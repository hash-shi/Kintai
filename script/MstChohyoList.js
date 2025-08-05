function toggleVisibility(){
	proc("change",{}, function(data){
		
		if (data == undefined){ return; }
		if (data["contents"] == undefined){ return; }

		var contents				= data["contents"];
		if (contents["result"] == undefined){ return; }
		if (contents["date"] == undefined){ return; }
		if (contents["eigyosho"] == undefined){ return; }
		var result			= contents["result"];
		var date  			= contents["date"];
		var eigyosho  			= contents["eigyosho"];
		
		onDisplayOutputArea(result,date,eigyosho);
		
	});
}

function onDisplayOutputArea(result,date,eigyosho){
	$("#displayShoriArea").children().remove();
	$("#displayOutputArea").children().remove();
	$("#displayDateArea").children().remove();
	$("#displayBottonArea").children().remove();
	let displayShoriAreaHtml = "";
	let displayOutputAreaHtml = "";
	let displayDateAreaHtml = "";
	let displayBottonAreaHtml = "";
	let eigyoshoCode = eigyosho[0];
	let Saisho = eigyoshoCode["Saisho"];
	let Saidai = eigyoshoCode["Saidai"];
	
	displayShoriAreaHtml = 
	"<tr>" +
		"<td class=\"title center w100\">" +
			"<a>処理選択</a>" +
		"</td>" +
		"<td class=\"value w170\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtShoriSentaku\" id=\"txtShoriSentaku\" value=\"\" readonly>" +
		"</td>" +
	"</tr>";
	$("#displayShoriArea").append(displayShoriAreaHtml);
	
	if(result == "01"){
	 displayOutputAreaHtml =
	   "<tr>" +
	 	 "<td class=\"title center w100\">" +
	 	   "<a>営業所</a>" +
	 	 "</td>" +
	 	 "<td class=\"value w1000\">" +
	 	   "<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtFromEigyoshoCode\" id=\"txtFromEigyoshoCode\"  value=\"" + Saisho + "\"  onblur=\"getEigyoshoName('txtFromEigyoshoCode', 'txtFromEigyoshoName');\" >" +
	 	   "<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstEigyosho','txtFromEigyoshoCode','txtFromEigyoshoName');\">" +
	 	   "<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtFromEigyoshoName\" id=\"txtFromEigyoshoName\" value=\"\" readonly>" +
	 	   "<a>～</a>" +
	 	   "<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtToEigyoshoCode\" id=\"txtToEigyoshoCode\"  value=\"" + Saidai + "\"  onblur=\"getEigyoshoName('txtToEigyoshoCode', 'txtToEigyoshoName');\" >" +
	 	   "<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstEigyosho','txtToEigyoshoCode','txtToEigyoshoName');\">" +
	 	   "<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtToEigyoshoName\" id=\"txtToEigyoshoName\" value=\"\" readonly>" +
	 	 "</td>" +
		"</tr>";
	} else if(result == "02"){
	 displayOutputAreaHtml =
		"<tr>" +
		  "<td class=\"title center w100\">" +
			"<a>営業所</a>" +
		  "</td>" +
		  "<td class=\"value w1000\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtFromEigyoshoCode\" id=\"txtFromEigyoshoCode\"  value=\"" + Saisho + "\"  onblur=\"getEigyoshoName('txtFromEigyoshoCode', 'txtFromEigyoshoName');\" >" +
			"<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstEigyosho','txtFromEigyoshoCode','txtFromEigyoshoName');\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtFromEigyoshoName\" id=\"txtFromEigyoshoName\" value=\"\" readonly>" +
			"<a>～</a>" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtToEigyoshoCode\" id=\"txtToEigyoshoCode\"  value=\"" + Saidai + "\"  onblur=\"getEigyoshoName('txtToEigyoshoCode', 'txtToEigyoshoName');\" >" +
			"<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstEigyosho','txtToEigyoshoCode','txtToEigyoshoName');\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtToEigyoshoName\" id=\"txtToEigyoshoName\" value=\"\" readonly>" +
		  "</td>" +
		"</tr>"+
		"<tr>" +
		  "<td class=\"title center w100\">" +
			 "<a>部署</a>" +
		  "</td>" +
		  "<td class=\"value w1000\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtFromBushoCode\" id=\"txtFromBushoCode\"  value=\"\"  onblur=\"getBushoName('txtFromBushoCode', 'txtFromBushoName');\" >" +
			"<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstBusho','txtFromBushoCode','txtFromBushoName');\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtFromBushoName\" id=\"txtFromBushoName\" value=\"\" readonly>" +
			"<a>～</a>" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtToBushoCode\" id=\"txtToBushoCode\"  value=\"\"  onblur=\"getBushoName('txtToBushoCode', 'txtToBushoName');\" >" +
			"<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstBusho','txtToBushoCode','txtToBushoName');\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtToBushoName\" id=\"txtToBushoName\" value=\"\" readonly>" +
		  "</td>" +
		 "</tr>" ;
	} else if(result == "03"){
		displayOutputAreaHtml =
		"<tr>" +
		  "<td class=\"title center w100\">" +
			"<a>営業所</a>" +
		  "</td>" +
		  "<td class=\"value w1000\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtFromEigyoshoCode\" id=\"txtFromEigyoshoCode\"  value=\"" + Saisho + "\"  onblur=\"getEigyoshoName('txtFromEigyoshoCode', 'txtFromEigyoshoName');\" >" +
			"<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstEigyosho','txtFromEigyoshoCode','txtFromEigyoshoName');\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtFromEigyoshoName\" id=\"txtFromEigyoshoName\" value=\"\" readonly>" +
			"<a>～</a>" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtToEigyoshoCode\" id=\"txtToEigyoshoCode\"  value=\"" + Saidai + "\"  onblur=\"getEigyoshoName('txtToEigyoshoCode', 'txtToEigyoshoName');\" >" +
			"<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstEigyosho','txtToEigyoshoCode','txtToEigyoshoName');\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtToEigyoshoName\" id=\"txtToEigyoshoName\" value=\"\" readonly>" +
		  "</td>" +
		"</tr>"+
		"<tr>" +
		  "<td class=\"title center w100\">" +
		    "<a>社員NO</a>" +
		  "</td>" +
		  "<td class=\"value w1000\">" +
		    "<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtFromShainNO\" id=\"txtFromShainNO\"  value=\"\"  onblur=\"getShainName('txtFromShainNO', 'txtFromShainName');\" >" +
		    "<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstShain','txtFromShainNO','txtFromShainName');\">" +
		    "<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtFromShainName\" id=\"txtFromShainName\" value=\"\" readonly>" +
		    "<a>～</a>" +
		    "<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtToShainNO\" id=\"txtToShainNO\"  value=\"\"  onblur=\"getShainName('txtToShainNO', 'txtToShainName');\" >" +
		    "<img class=\"img border\" src=\"./images/search.png\"  onclick=\"opnDialog('srhMstShain','txtToShainNO','txtToShainName');\">" +
		    "<input type=\"text\" class=\"\"  style=\"width: 120px\"\" name=\"txtToShainName\" id=\"txtToShainName\" value=\"\" readonly>" +
		  "</td>" +
		"</tr>" ;
	} else {
	 displayOutputAreaHtml =
	 "<tr>" +
	 	"<td class=\"title center w100\">" +
	 		"<a>区分コード</a>" +
	 	"</td>" +
	 	"<td class=\"value w1000\">" +
	 		"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtFromKbnCode\" id=\"txtFromKbnCode\"  value=\"\">" +
	 		"<a>～</a>" +
	 		"<input type=\"text\" class=\"\"  style=\"width: 80px\"\" name=\"txtToKbnCode\" id=\"txtToKbnCode\"  value=\"\">" +
	 	"</td>"+
	"</tr>";
	}
	$("#displayOutputArea").append(displayOutputAreaHtml);
	
	displayDateAreaHtml = 
	"<tr>" +
		"<td class=\"title center w100\">" +
			"<a>最終更新日</a>" +
		"</td>" +
		"<td class=\"value w500\">" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px; text-align: right;\"\" name=\"txtFromSaishuKoshinDate\" id=\"txtFromSaishuKoshinDate\" value=\"" + date + "\" >" +
			"<a> ～</a>" +
			"<input type=\"text\" class=\"\"  style=\"width: 80px; text-align: right;\"\" name=\"txtToSaishuKoshinDate\" id=\"txtToSaishuKoshinDate\" value=\"" + date + "\" >" +
			"<a>(YYYY/MM/DD)</a>" +
		"</td>" +
	"</tr>";
	
	$("#displayDateArea").append(displayDateAreaHtml);
	
	displayBottonAreaHtml = 
	"<button type=\"button\" onclick=\"opnDialog('srhMstKbn','txtFromKbnCode','txtFromKbnName');\">作表[F12]</button>" ;

	$("#displayBottonArea").append(displayBottonAreaHtml);
}