//****************************************************************************
// setShoriSentaku
//
//
//
//
//****************************************************************************
function setShoriSentaku(){	

	// 選択した処理内容を格納
	// 値を取得する方法
	var value = $("#selShoriSentaku").val();
	console.log(value);
	// テキスト内容を取得する方法
	var name = $("#selShoriSentaku option:selected").text();
	console.log(name);
	
	// 処理選択に格納する。
	$("#txtShoriSentaku").val(name);
	
	// mainAreaを表示する。
	$("#mainArea").css("display", "block");
	$("#buttonArea").css("display", "block");
	
	// mainAreaの背景色を変更
	if (!$("#mainArea").hasClass("ins")) {
		$("#mainArea").addClass("ins");
	}
	
	// 処理選択によって表示する内容が変わる。
	if (value == "01" || value == "02") {
		$("#taishoNengetsu").css("display", "table-row");
		$("#taishoNendo").css("display", "none");
		$("#eigyosho").css("display", "table-row");
		$("#busho").css("display", "table-row");
		$("#shain").css("display", "table-row");
		$("#joken").css("display", "table-row");
		$("#order").css("display", "table-row");
		$("#output").css("display", "table-row");
	}
	else if (value == "03") {
		$("#taishoNengetsu").css("display", "none");
		$("#taishoNendo").css("display", "table-row");
		$("#eigyosho").css("display", "table-row");
		$("#busho").css("display", "table-row");
		$("#shain").css("display", "table-row");
		$("#joken").css("display", "none");
		$("#order").css("display", "table-row");
		$("#output").css("display", "table-row");
	}
	else {
		$("#taishoNengetsu").css("display", "none");
		$("#taishoNendo").css("display", "none");
		$("#eigyosho").css("display", "none");
		$("#busho").css("display", "none");
		$("#shain").css("display", "none");
		$("#joken").css("display", "none");
		$("#order").css("display", "none");
		$("#output").css("display", "none");
	}
	
	// 対象年月にシステム日付を格納する
	// 	システム日付が15日までの場合、その年月。
	//  システム日付が16日以降の場合、その翌月の年月
	var dateTime = new Date();
	var date = new Date();
	if (dateTime.getDate() <= 15) {
		date = new Date(dateTime.getFullYear(), dateTime.getMonth(), dateTime.getDate());
	} else {
		date = new Date(dateTime.getFullYear(), (dateTime.getMonth() + 1), dateTime.getDate());
	}
	
	$("#srhTxtTaishoNengetsuF").val(formatDateYYYYMM(date, "/"));
	$("#srhTxtTaishoNengetsuT").val(formatDateYYYYMM(date, "/"));
	$("#srhTxtTaishoNendoF").val(formatDateYYYY(date));
	$("#srhTxtTaishoNendoT").val(formatDateYYYY(date));
	
	// ユーザー権限による初期値/活性制御
	var shainNo = $("#hdnShainNo").val();
	var eigyoshoCode = $("#hdnEigyoshoCode").val();
	var bushoCode = $("#hdnBushoCode").val();
	var bushoKbn = $("#hdnBushoKbn").val();
	var userKbn = $("#hdnUserKbn").val();
	console.log(bushoKbn);
	console.log(userKbn);
	
	if (bushoKbn == "00" && userKbn == "01") {
		// 管理者(権限有り)
		// 営業所
		eigyoshoCode = "";
		// 部署
		bushoCode = "";
		// 社員
		shainNo = "";
		// 活性
		$("#srhTxtEigyoshoCodeF").prop('readonly', false);
		$("#srhTxtEigyoshoCodeT").prop('readonly', false);
		$("#srhTxtBushoCodeF").prop('readonly', false);
		$("#srhTxtBushoCodeT").prop('readonly', false);
		$("#srhTxtShainNoF").prop('readonly', false);
		$("#srhTxtShainNoT").prop('readonly', false);
		
	} else {
//		// 一般(権限なし)
//		// 営業所
//		eigyoshoCode = "";
//		// 部署
//		bushoCode = "";
//		// 社員
//		shainNo = "";
		// 非活性
		$("#srhTxtEigyoshoCodeF").prop('readonly', true);
		$("#srhTxtEigyoshoCodeT").prop('readonly', true);
		$("#srhTxtBushoCodeF").prop('readonly', true);
		$("#srhTxtBushoCodeT").prop('readonly', true);
		$("#srhTxtShainNoF").prop('readonly', true);
		$("#srhTxtShainNoT").prop('readonly', true);
	}
	
	// コードセット/名称取得
	// 営業所
	$("#srhTxtEigyoshoCodeF").val(eigyoshoCode);
	getEigyoshoName('srhTxtEigyoshoCodeF', 'srhTxtEigyoshoNameF');
	$("#srhTxtEigyoshoCodeT").val(eigyoshoCode);
	getEigyoshoName('srhTxtEigyoshoCodeT', 'srhTxtEigyoshoNameT');
	// 部署
	$("#srhTxtBushoCodeF").val(bushoCode);
	getBushoName('srhTxtBushoCodeF', 'srhTxtBushoNameF');
	$("#srhTxtBushoCodeT").val(bushoCode);
	getBushoName('srhTxtBushoCodeT', 'srhTxtBushoNameT');
	// 社員
	$("#srhTxtShainNoF").val(shainNo);
	getShainName('srhTxtShainNoF', 'srhTxtShainNameF');
	$("#srhTxtShainNoT").val(shainNo);
	getShainName('srhTxtShainNoT', 'srhTxtShainNameT');
	// 条件
	$("#srhSelJoken option[value='00']").prop('selected', true);
	// 出力順
	$("#srhRdoOrder[value='01']").prop('checked', true);	
	// 出力形式
	$("#srhRdoOutput[value='01']").prop('checked', true);	
	
}

//****************************************************************************
// onDownload
//
//
//
//
//****************************************************************************
function onPdfCsvDownload(){
	
	// 処理選択を取得
	var value = $("#selShoriSentaku").val();
	console.log(value);
	// 出力形式を取得
	var pdfcsv = $("#srhRdoOutput:checked").val();
	console.log(pdfcsv);
	
	if (value == "01") {
		proc("kinShukkinBo",{}, function(data, dataType){
			if (pdfcsv == "01") {
				onDownloadPost("pdfKinShukkinBo");
			}
			else if (pdfcsv == "02") {
				onDownloadPost("csvKinShukkinBo");
			}
		});
	}
	else if (value == "02") {
		proc("chiChinginkeisansho",{}, function(data, dataType){
			if (pdfcsv == "01") {
				onDownloadPost("pdfChiChinginkeisansho");
			}
			else if (pdfcsv == "02") {
				onDownloadPost("csvChiChinginkeisansho");
			}
		});	
	}
	else if (value == "03") {
		proc("kinYukyuKyukaDaicho",{}, function(data, dataType){
			if (pdfcsv == "01") {
				onDownloadPost("pdfKinYukyuKyukaDaicho");
			}
			else if (pdfcsv == "02") {
				onDownloadPost("csvKinYukyuKyukaDaicho");
			}
		});	
	}
}

//****************************************************************************
// ファンクションキーF12
//
//
//
//
//****************************************************************************
function onKeyEventF12() {
	
	// mainAreaの表示状態を取得
	var display = $("#mainArea").css("display");
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "block") {
		// 該当の処理を呼び出す。
		onPdfCsvDownload();
	}
}