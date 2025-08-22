//****************************************************************************
// getMstShain
//
//
//
//
//****************************************************************************
function getMstShain() {
	
	// 処理可能営業所リスト初期化、表示クリア
	eigyoshoList = [];
    $("#ShoriKanoEigyoshoResult").children().remove();

    proc("search", {}, function(data) {
		
        if (data == undefined) { return; }
        if (data["contents"] == undefined) { return; }

        var contents 		= data["contents"];
        if (contents["isNew"] == undefined || contents["mstDatas"] == undefined){ return; }
		
		// 取得した値の格納
		var isNew = contents["isNew"];
		$("#hdnIsNew").val(isNew);
		
		// mainAreaを表示する。
		$("#mainArea").css("visibility", "visible");
		$("#buttonArea").css("visibility", "visible");
		
		// 既に背景色が設定されている場合は一旦削除
		$("#mainArea").removeClass('ins');
		$("#mainArea").removeClass('upd');
		
		// 背景色を設定
		if (isNew == "1") {
			$("#mainArea").addClass("ins");
		} else {
			$("#mainArea").addClass("upd");
		}
		
		// 新規の時は社員NOを非活性
		$("#txtShainNO").prop('readonly', false);
		if (isNew == "1") {
			$("#txtShainNO").prop('readonly', true);
		}
		
		// 新規の時は削除ボタンは非活性
		$("#btnDelete").prop('disabled', false);
		if (isNew == "1") {
			$("#btnDelete").prop('disabled', true);
		}

		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtShainNO").val(mstDatas[i]["ShainNO"]);
			$("#hdnShainNO").val(mstDatas[i]["ShainNO"]);
			$("#txtShainName").val(mstDatas[i]["ShainName"]);
			$("#txtPassword").val(mstDatas[i]["Password"]);
			$("#txtUserKbn").val(mstDatas[i]["UserKbn"]);
			$("#txtShainKbn").val(mstDatas[i]["ShainKbn"]);
			$("#txtShukinboKbn").val(mstDatas[i]["ShukinboKbn"]);
			$("#txtEigyoshoCode").val(mstDatas[i]["EigyoshoCode"]);
			$("#txtBushoCode").val(mstDatas[i]["BushoCode"]);
			$("#txtYukyuKyukaFuyoNissu").val(mstDatas[i]["YukyuKyukaFuyoNissu"]);
			$("#txtJikyuNikkyuKbn").val(mstDatas[i]["JikyuNikkyuKbn"]);		
			$("#txtKinmuKaishiJiKbnName").val(mstDatas[i]["KinmuKaishiJi"]);			
			$("#txtKinmuKaishiFunKbnName").val(mstDatas[i]["KinmuKaishiFun"]);
			$("#txtKinmuShuryoJiKbnName").val(mstDatas[i]["KinmuShuryoJi"]);
			$("#txtKinmuShuryoFunKbnName").val(mstDatas[i]["KinmuShuryoFun"]);
			$("#txtKeiyakuJitsudoJikan").val(mstDatas[i]["KeiyakuJitsudoJikan"]);
			$("#txtShinseiTanka01").val(mstDatas[i]["ShinseiTanka01"]);
			$("#txtShinseiTanka02").val(mstDatas[i]["ShinseiTanka02"]);
			$("#txtShinseiTanka03").val(mstDatas[i]["ShinseiTanka03"]);
			$("#txtShinseiTanka04").val(mstDatas[i]["ShinseiTanka04"]);
			$("#txtShinseiTanka05").val(mstDatas[i]["ShinseiTanka05"]);
			$("#txtShinseiTanka06").val(mstDatas[i]["ShinseiTanka06"]);
			$("#txtShinseiTanka07").val(mstDatas[i]["ShinseiTanka07"]);
			$("#txtShinseiTanka08").val(mstDatas[i]["ShinseiTanka08"]);
			$("#txtShinseiTanka09").val(mstDatas[i]["ShinseiTanka09"]);
			$("#txtShinseiTanka10").val(mstDatas[i]["ShinseiTanka10"]);
			$("#txtShinseiTanka11").val(mstDatas[i]["ShinseiTanka11"]);
			$("#txtTsukinHiKbn").val(mstDatas[i]["TsukinHiKbn"]);
			$("#txtTaisyokuDate").val(mstDatas[i]["TaisyokuDate"]);
			
			if (isNew == "1") {
				// 新規登録 → 空リストにする
				eigyoshoList = [];
			} else {
				// 既存データ → サーバーの営業所リストを利用	
				eigyoshoList = mstDatas[i]["ShoriKanoEigyoshos"] || []; 
			}
				
			$("#hdnSaishuKoshinShainNO").val(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#hdnSaishuKoshinShainName").val(mstDatas[i]["SaishuKoshinShainName"]);
			$("#hdnSaishuKoshinDate").val(mstDatas[i]["SaishuKoshinDate"]);
			$("#hdnSaishuKoshinJikan").val(mstDatas[i]["SaishuKoshinJikan"]);
			// $("#lblSaishuKoshinShainNO").html(mstDatas[i]["SaishuKoshinShainNO"]);
			$("#lblSaishuKoshinShainName").html(mstDatas[i]["SaishuKoshinShainName"]);
			$("#lblSaishuKoshinDate").html(mstDatas[i]["SaishuKoshinDate"]);
			$("#lblSaishuKoshinJikan").html(mstDatas[i]["SaishuKoshinJikan"]);
		}
		
		// 処理可能営業所描画
		getShoriKanoEigyoshos();
		
		console.log("処理可能営業所リスト:", eigyoshoList);
		// ユーザ区分名
		setSelectFromInput('txtUserKbn','selectUserKbnName');
		// 社員区分名
		setSelectFromInput('txtShainKbn','selectShainKbnName');
		// 出勤簿入力区分名
		setSelectFromInput('txtShukinboKbn','selectShukinboKbnName');
		// 営業所名
		getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');
		// 部署名
		getBushoName('txtBushoCode', 'txtBushoName');
		// 時給日給区分名
		setSelectFromInput('txtJikyuNikkyuKbn','selectJikyuNikkyuKbnName');
		// 勤務開始時刻（時）
		setSelectKinmuFromInput('txtKinmuKaishiJiKbnName','selectKinmuKaishiJiKbnName');
		getCodeFromKbnName('txtKinmuKaishiJiKbnName','txtKinmuKaishiJiKbn','selectKinmuKaishiJiKbnName');
		// 勤務開始時刻（分）
		setSelectKinmuFromInput('txtKinmuKaishiFunKbnName','selectKinmuKaishiFunKbnName');
		getCodeFromKbnName('txtKinmuKaishiFunKbnName','txtKinmuKaishiFunKbn','selectKinmuKaishiFunKbnName');
		// 勤務終了時刻（時）
		setSelectKinmuFromInput('txtKinmuShuryoJiKbnName','selectKinmuShuryoJiKbnName');
		getCodeFromKbnName('txtKinmuShuryoJiKbnName','txtKinmuShuryoJiKbn','selectKinmuShuryoJiKbnName');
		// 勤務終了時刻（分）
		setSelectKinmuFromInput('txtKinmuShuryoFunKbnName','selectKinmuShuryoFunKbnName');	
		getCodeFromKbnName('txtKinmuShuryoFunKbnName','txtKinmuShuryoFunKbn','selectKinmuShuryoFunKbnName');
		// 通勤費精算区分名
		setSelectFromInput('txtTsukinHiKbn','selectTsukinHiKbnName');		
		// 処理可能営業所名
		getEigyoshoName('txtShoriKanoEigyoshoCode', 'txtShoriKanoEigyoshoName');
		
		//活性・非活性切り替え
		updateActiveSwitch();
		
	});
}

//****************************************************************************
// プルダウン同期処理
//
// 勤務開始・終了時刻についてはDBに直接、
//「区分名称」（例：00, 15, 30, 45）が保存されているため、
// 画面上の <select> で使用する
//「区分コード」（例：01, 02, 03, 04）を取得する必要がある。
//****************************************************************************
// 入力欄からプルダウンに同期
function setSelectFromInput(inputId, selectId) { 
	var val = $("#" + inputId).val(); 
	$("#" + selectId).val(val); 
}

// 勤務開始・終了時刻プルダウン
function setSelectKinmuFromInput(inputId, selectId) {
    var val = $("#" + inputId).val();
	var $sel = $("#" + selectId);
	var found = false;

	$sel.find("option").each(function() {
	    if ($(this).text() === val) {
	        $sel.val($(this).val());
	        found = true;
	        return false;
	    }
	});

	if (!found) {
	    $sel.val("");
	}
	// 名称 input も更新
	var nameInputId = inputId + "Name"; // txtKinmuKaishiJiKbn → txtKinmuKaishiJiKbnName
	$("#" + nameInputId).val(val);
}

// プルダウンから入力欄に同期
function setInputFromSelect(selectId, inputId) {
    var val = $("#" + selectId).val();
    $("#" + inputId).val(val).trigger("change");
}

//****************************************************************************
// getShoriKanoEigyoshos
// 処理可能営業所描画
//
//
//
//****************************************************************************
function getShoriKanoEigyoshos() {
    proc("getShoriKanoEigyoshoList", {}, function(data){
        if (data == undefined){ return; }
        if (data["contents"] == undefined){ return; }
        
        var contents = data["contents"];
        if (contents["result"] == undefined){ return; }
        
        var result = contents["result"];
        eigyoshoList = result;
        
        // 検索結果エリアをクリア
        $("#ShoriKanoEigyoshoResult").children().remove();
        
        for (var count = 0 ; count < result.length ; count++) {
            var item = result[count];
            var code = item.EigyoshoCode || "";
            var row = 
                "<tr>" +
                    "<td class=\"title center w150 req\">" +
                        "<a href=\"#\" onclick=\"opnDialog('srhMstEigyosho','eigyoshoCode_" + count + "','eigyoshoName_" + count + "'); return false;\">" +
                            "処理可能営業所" +
                        "</a>" +
                    "</td>" +
                    "<td class=\"value w500\">" +
                        "<input type=\"text\" class=\"w80\" maxlength=\"3\" id=\"eigyoshoCode_" + count + "\" name=\"eigyoshoCode[]\" value=\"" + code + "\" " +
                               "onblur=\"getEigyoshoName('eigyoshoCode_" + count + "','eigyoshoName_" + count + "');\">" +
                        "<input type=\"text\" class=\"w200\" id=\"eigyoshoName_" + count + "\" name=\"eigyoshoName[]\" value=\"\" readonly>" +
                        "<button type=\"button\" style=\"font-weight: bold;\" onclick=\"removeShoriKanoEigyosho(" + count + ")\">×</button>" +
                    "</td>" +
                "</tr>";
            
            $("#ShoriKanoEigyoshoResult").append(row);
            
            // コードがあれば営業所名を取得してセット
            if (code) {
                getEigyoshoName('eigyoshoCode_' + count, 'eigyoshoName_' + count);
            }
        }
    });
}

//****************************************************************************
// addShoriKanoEigyosho
// 空行追加
//
//
//
//****************************************************************************
function addShoriKanoEigyosho() {
	reflectShoriKanoEigyosho();
    eigyoshoList.push({ EigyoshoCode: "" });
    renderEigyoshoTable();
}

//****************************************************************************
// removeShoriKanoEigyosho
// 指定行削除
//
//
//
//****************************************************************************
function removeShoriKanoEigyosho(index) {
	reflectShoriKanoEigyosho()
    eigyoshoList.splice(index, 1);   // 配列から削除
    renderEigyoshoTable();           // 再描画
}

//****************************************************************************
// reflectShoriKanoEigyosho
// 入力欄の値を eigyoshoList に反映
//
//
//
//****************************************************************************
function reflectShoriKanoEigyosho() {
    $("#ShoriKanoEigyoshoResult tr").each(function(index) {
        var code = $("#eigyoshoCode_" + index).val();
        eigyoshoList[index].EigyoshoCode = code;
    });
}

//****************************************************************************
// renderEigyoshoTable
// eigyoshoList を元にテーブル描画
//
//
//
//****************************************************************************
function renderEigyoshoTable() {
    $("#ShoriKanoEigyoshoResult").children().remove();
    
    for (var count = 0; count < eigyoshoList.length; count++) {
        var item = eigyoshoList[count];
        var code = item.EigyoshoCode || "";
        var row =
            "<tr>" +
                "<td class=\"title center w150 req\">" +
                    "<a href=\"#\" onclick=\"opnDialog('srhMstEigyosho','eigyoshoCode_" + count + "','eigyoshoName_" + count + "'); return false;\">" +
                        "処理可能営業所" +
                    "</a>" +
                "</td>" +
                "<td class=\"value w500\">" +
                    "<input type=\"text\" class=\"w80\" maxlength=\"3\" id=\"eigyoshoCode_" + count + "\" name=\"eigyoshoCode[]\" value=\"" + code + "\" " +
                           "onblur=\"getEigyoshoName('eigyoshoCode_" + count + "','eigyoshoName_" + count + "');\">" +
                    "<input type=\"text\" class=\"w200\" id=\"eigyoshoName_" + count + "\" name=\"eigyoshoName[]\" value=\"\" readonly>" +
                    "<button type=\"button\" style=\"font-weight: bold;\" onclick=\"removeShoriKanoEigyosho(" + count + ")\">×</button>" +
                "</td>" +
            "</tr>";
        
        $("#ShoriKanoEigyoshoResult").append(row);
        
        if (code) {
            getEigyoshoName('eigyoshoCode_' + count, 'eigyoshoName_' + count);
        }
    }
}

//****************************************************************************
// onDelete
//
//
//
//
//****************************************************************************
function onDelete() {
	
	proc("delete",{}, function(data){
		// 確認メッセージ
		if(!confirm("データの削除を行います。\nよろしいですか？")) { return; }
		
		// 削除処理の本体
		proc("delete_",{}, function(data){
			// 完了メッセージ
			alert("データが正常に更新されました。");
			// 画面のクリアなど何かしらの処理
			getMstShain();
		});
		
	});
}

//****************************************************************************
// onUpdate
//
//
//
//
//****************************************************************************
function onUpdate() {
	
	// 新規モードと更新モードで分岐
	var isNew = $("#hdnIsNew").val();
	
	// 更新モード時の切り替えに必要
	var txtShainNO = $("#txtShainNO").val();
	var hdnShainNO = $("#hdnShainNO").val();
	
	if (isNew == "1") {
		// 新規モード
		proc("insert",{}, function(data){
			// 確認メッセージ
			if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
			// 登録処理の本体
			proc("insert_",{}, function(data){
				// 完了メッセージ
				alert("データが正常に更新されました。");
				// 画面のクリアなど何かしらの処理
				// 処理した社員NOで再読込
				$("#srhTxtShainNO").val($("#txtShainNO").val());
				getMstShain();
			});
		});
	} else {
		// 更新モード
		proc("update",{}, function(data){
			
			if (txtShainNO == hdnShainNO) {
				// 画面項目と隠し項目が同じ値 = 単純にデータ更新
				// 確認メッセージ
				if(!confirm("データの更新を行います。\nよろしいですか？")) { return; }
				// 登録処理の本体
				proc("update_",{}, function(data){
					// 完了メッセージ
					alert("データが正常に更新されました。");
					// 画面のクリアなど何かしらの処理
					// 処理した社員NOで再読込
					$("#srhTxtShainNO").val($("#txtShainNO").val());
					getMstShain();
				});
			} else {
				// 画面項目と隠し項目が異なる値 = データコピー(画面で入力した新しい値で登録)
				// 確認メッセージ
				if(!confirm("データのコピーを行います。\nよろしいですか？")) { return; }
				
				// 登録処理の本体
				proc("copy_",{}, function(data){
					// 完了メッセージ
					alert("データが正常に更新されました。");
					// 画面のクリアなど何かしらの処理
					// 処理した社員NOで再読込
					$("#srhTxtShainNO").val($("#txtShainNO").val());
					getMstShain();
				});
			}
		});
	}
}

//****************************************************************************
// updateActiveSwitch
// 各区分に応じて入力欄活性切り替え
// 一旦現行システムと合わせるためコメントアウト
// そもそも入力をさせないようにするならコメントを解除し、
// 下部のupdateActiveSwitchと置き換え
//****************************************************************************
/*
function updateActiveSwitch() {
    var shainboVal   = $("#txtShainKbn").val();     // 社員区分
    var shukinboVal  = $("#txtShukinboKbn").val();  // 出勤簿入力区分

    var shainDisable    = (shainboVal === "00");    // 社員区分が00なら無効
    var shukinboEnable  = (shukinboVal === "01");   // 出勤簿入力区分が01なら有効

    // --- 時給日給区分 ---
    // 社員区分が00でなく、かつ出勤簿区分が01のときだけ活性
    var jikyuEnable = !shainDisable && shukinboEnable;
    $("#txtJikyuNikkyuKbn").prop("disabled", !jikyuEnable);
    $("#selectJikyuNikkyuKbnName").prop("disabled", !jikyuEnable);

    // --- 勤務開始・終了時刻 ---
    $("#selectKinmuKaishiJiKbnName").prop("disabled", shainDisable);
    $("#selectKinmuKaishiFunKbnName").prop("disabled", shainDisable);
    $("#selectKinmuShuryoJiKbnName").prop("disabled", shainDisable);
    $("#selectKinmuShuryoFunKbnName").prop("disabled", shainDisable);

    // --- 勤務実働時間 ---
    $("#txtKeiyakuJitsudoJikan").prop("disabled", !shukinboEnable);

    // --- 単価01～11 ---
    for (var i = 1; i <= 11; i++) {
        $("#txtShinseiTanka" + (i < 10 ? "0" + i : i)).prop("disabled", !shukinboEnable);
    }

    // --- 通勤費精算区分 ---
    $("#txtTsukinHiKbn").prop("disabled", !shukinboEnable);
    $("#selectTsukinHiKbnName").prop("disabled", !shukinboEnable);
}
*/

//****************************************************************************
// updateActiveSwitch
// 出勤簿入力関連活性切り替え
//
//
//
//****************************************************************************
function updateActiveSwitch() {
    var shukinboVal = $("#txtShukinboKbn").val(); // 出勤簿入力区分の値
    var enable = (shukinboVal === "01");          // 01 のときだけ活性

    // 時給日給区分
    $("#txtJikyuNikkyuKbn").prop("disabled", !enable);
    $("#selectJikyuNikkyuKbnName").prop("disabled", !enable);

    // 勤務実働時間
    $("#txtKeiyakuJitsudoJikan").prop("disabled", !enable);

	// 単価01～11
	for (var i = 1; i <= 11; i++) {
	    $("#txtShinseiTanka" + (i < 10 ? "0" + i : i)).prop("disabled", !enable);
	}
	
	// 通勤費精算区分
	$("#txtTsukinHiKbn").prop("disabled", !enable);
	$("#selectTsukinHiKbnName").prop("disabled", !enable);
}

//****************************************************************************
// getCodeFromName
// 名称からコード取得
//
//
//
//****************************************************************************
function getCodeFromKbnName(inputId, hiddenId, selectId) {
    var name = $("#" + inputId).val(); // 入力された名称
    var code = "";
    $("#" + selectId + " option").each(function() {
        if ($(this).text() === name) {
            code = $(this).val(); // option の value がコード
            return false; // break
        }
    });
    $("#" + hiddenId).val(code);
}

//****************************************************************************
// ファンクションキーF9
//
//
//
//
//****************************************************************************
function onKeyEventF09() {
	
	// mainAreaの表示状態を取得
	var display = $("#buttonArea").css("visibility");
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "visible") {
		// 該当の処理を呼び出す。
		onUpdate();
	}
}

//****************************************************************************
// ファンクションキーF2
//
//
//
//
//****************************************************************************
function onKeyEventF02() {
	
	//ボタンaの表示状態を取得
	var display = $("#buttonArea").css("visibility");
	var isNew = $("#hdnIsNew").val();
	
	// mainAreaが非表示(初期表示時)はスキップする。
	if (display == "visible"&& isNew != "1") {
			// 該当の処理を呼び出す。
			onDelete();
	}
}