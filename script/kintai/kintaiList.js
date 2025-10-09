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
	// テキスト内容を取得する方法
	var name = $("#selShoriSentaku option:selected").text();
	
	// 処理選択に格納する。
	$("#txtShoriSentaku").val(name);
	
	// mainAreaを表示する。
	$("#mainArea").css("visibility", "visible");
	$("#buttonArea").css("visibility", "visible");
	
	// 既に背景色が設定されている場合は一旦削除
	$("#mainArea").removeClass('ins');
	// 背景色を設定
	$("#mainArea").addClass("ins");
	
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
		$("#srhTxtTaishoNengetsuF").focus();
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
		$("#srhTxtTaishoNendoF").focus();
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
	
	// 対象年月に管理マスタの現在処理年月度を格納
	var taishoNengetsu = $("#hdnTaishoNengetsu").val();
	$("#srhTxtTaishoNengetsuF").val(taishoNengetsu);
	$("#srhTxtTaishoNengetsuT").val(taishoNengetsu);
	
	var taishoNendo = taishoNengetsu.split('/')[0];
	$("#srhTxtTaishoNendoF").val(taishoNendo);
	$("#srhTxtTaishoNendoT").val(taishoNendo);
	
	// ユーザ区分、処理可能営業所による初期値/活性制御
	var shainNo = $("#hdnShainNo").val();
	var eigyoshoCode = $("#hdnEigyoshoCode").val();
	var bushoCode = $("#hdnBushoCode").val();
	var bushoKbn = $("#hdnBushoKbn").val();
	var userKbn = $("#hdnUserKbn").val();
	var shoriKanoEigyoshoCode = $("#hdnShoriKanoEigyoshoCode").val();
	// 処理可能営業所をカンマ区切りの配列に変換
	shoriKanoEigyoshoCode = shoriKanoEigyoshoCode.split(",");
	
	if ((bushoKbn == "00" && userKbn == "01") || (userKbn == "01" && shoriKanoEigyoshoCode.length >= 2) || (userKbn == "02" && shoriKanoEigyoshoCode.length >= 2)) {
		// 部署区分が「00；本社」かつユーザ区分が「01：本社」
		// 部署区分が「00；本社」以外かつユーザ区分が「01：本社」かつ処理可能営業所が複数
		// ユーザ区分が「02：営業所」かつ処理可能営業所が複数
		// 営業所
		eigyoshoCode = "";
		// 部署
		bushoCode = "";
		// 社員
		shainNo = "";
		// 全て活性
		$("#srhTxtEigyoshoCodeF").prop('readonly', false);
		$("#srhTxtEigyoshoCodeT").prop('readonly', false);
		$("#srhTxtBushoCodeF").prop('readonly', false);
		$("#srhTxtBushoCodeT").prop('readonly', false);
		$("#srhTxtShainNoF").prop('readonly', false);
		$("#srhTxtShainNoT").prop('readonly', false);
	}
	else if ((userKbn == "01" && shoriKanoEigyoshoCode.length == 1) || (userKbn == "02" && shoriKanoEigyoshoCode.length == 1)) {
//		// ユーザ区分が「01：営業所」かつ処理可能営業所が1つ(所属している営業所のみ)
//		// ユーザ区分が「02：営業所」かつ処理可能営業所が1つ(所属している営業所のみ)
//		// 営業所
//		eigyoshoCode = "";
		// 部署
		bushoCode = "";
		// 社員
		shainNo = "";
		// 営業所のみ非活性
		$("#srhTxtEigyoshoCodeF").prop('readonly', true);
		$("#srhTxtEigyoshoCodeT").prop('readonly', true);
		$("#srhTxtBushoCodeF").prop('readonly', false);
		$("#srhTxtBushoCodeT").prop('readonly', false);
		$("#srhTxtShainNoF").prop('readonly', false);
		$("#srhTxtShainNoT").prop('readonly', false);
	}
	else if (userKbn == "03") {
//		// ユーザ区分が「03：部署」
//		// 営業所
//		eigyoshoCode = "";
//		// 部署
//		bushoCode = "";
		// 社員
		shainNo = "";
		// 営業所、部署は非活性
		$("#srhTxtEigyoshoCodeF").prop('readonly', true);
		$("#srhTxtEigyoshoCodeT").prop('readonly', true);
		$("#srhTxtBushoCodeF").prop('readonly', true);
		$("#srhTxtBushoCodeT").prop('readonly', true);
		$("#srhTxtShainNoF").prop('readonly', false);
		$("#srhTxtShainNoT").prop('readonly', false);	
	}
	else if (userKbn == "04") {
//		// ユーザ区分が「04：個人」
//		// 営業所
//		eigyoshoCode = "";
//		// 部署
//		bushoCode = "";
//		// 社員
//		shainNo = "";
		// 全て非活性
		$("#srhTxtEigyoshoCodeF").prop('readonly', true);
		$("#srhTxtEigyoshoCodeT").prop('readonly', true);
		$("#srhTxtBushoCodeF").prop('readonly', true);
		$("#srhTxtBushoCodeT").prop('readonly', true);
		$("#srhTxtShainNoF").prop('readonly', true);
		$("#srhTxtShainNoT").prop('readonly', true);
	}
	else {
//		// ユーザ区分なし
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
	$("#srhSelJoken option[value='']").prop('selected', true);
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
	var value = $("#txtShoriSentaku").val();
	// 出力形式を取得
	var pdfcsv = $("#srhRdoOutput:checked").val();
	
	if (value == "出勤簿出力") {
		proc("kinShukkinBo",{}, function(data, dataType){
			
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
			var contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
			var result   = contents["result"];
			
			if(result){
				if (pdfcsv == "01") {
					onDownloadPost("pdfKinShukkinBo");
				}
				else if (pdfcsv == "02") {
					onDownloadPost("csvKinShukkinBo");
				}
			} else {
				if(contents["message"] == undefined){ return; }
				alert(contents["message"]);
			}
		});
	}
	else if (value == "賃金計算書出力") {
		proc("chiChinginkeisansho",{}, function(data, dataType){
			
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
			var contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
			var result   = contents["result"];
			
			if(result){
				if (pdfcsv == "01") {
					onDownloadPost("pdfChiChinginkeisansho");
				}
				else if (pdfcsv == "02") {
					onDownloadPost("csvChiChinginkeisansho");
				}
			} else {
				if(contents["message"] == undefined){ return; }
				alert(contents["message"]);
			}
		});	
	}
	else if (value == "年次有給休暇台帳出力") {
		proc("kinYukyuKyukaDaicho",{}, function(data, dataType){
			
			if (data == undefined){ return; }
			if (data["contents"] == undefined){ return; }
			var contents		= data["contents"];
			if (contents["result"] == undefined){ return; }
			var result   = contents["result"];

			if(result){
				if (pdfcsv == "01") {
					onDownloadPost("pdfKinYukyuKyukaDaicho");
				}
				else if (pdfcsv == "02") {
					onDownloadPost("csvKinYukyuKyukaDaicho");
				}
			} else {
				if(contents["message"] == undefined){ return; }
				alert(contents["message"]);
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
	var display = $("#mainArea").css("visibility");

	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "visible") {
		// 該当の処理を呼び出す。
		onPdfCsvDownload();
	}
}