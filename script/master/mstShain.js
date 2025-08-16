//****************************************************************************
// ページロード時に区分一覧を格納（キャッシュ利用）
//
//
//
//
//****************************************************************************

// キャッシュ用オブジェクト
var kubunCache = {};

// ページロード時にまとめて取得
function initKubunCache() {
    // 取得したい区分コードリスト
    var kbnCodes = [
						"0500", //ユーザ区分
						"0010", //社員区分
						"0011", //出勤簿入力区分
						"0012", //時給日給区分
						"0052", //勤務開始・終了時刻（時）
						"0053", //勤務開始・終了時刻（分）
						"0013"  //通勤費精算区分
					];
    
    kbnCodes.forEach(function(kbnCode) {
        proc("getKubunList", { kbnCode: kbnCode }, function(data){
            if (!data || !data.contents || !data.contents.result) return;
            kubunCache[kbnCode] = data.contents.result; // キャッシュ
			console.log(kbnCode, kubunCache[kbnCode]);  // キャッシュ確認
        });
    });
}
//****************************************************************************
// プルダウンセット（キャッシュ利用）
//
//
//
//
//****************************************************************************
function getKubunNameAndSetSelect(kbnCodeId, codeId, nameId, selectId) {
    var kbnCode = $("#" + kbnCodeId).val();
    if (!kbnCode || !kubunCache[kbnCode]) return;

    var kubuns = kubunCache[kbnCode];
    var currentCodeOrName = $("#" + codeId).val() || $("#" + nameId).val() || "";
    var $select = $("#" + selectId);
    $select.empty();
    $select.append($("<option>").val("").text(""));

    var selectedCode = "";
    var selectedName = "";

    kubuns.forEach(function(item){
        var $option = $("<option>").val(item.Code).text(item.KbnName);
        if (item.Code === currentCodeOrName || item.KbnName === currentCodeOrName) {
            $option.prop("selected", true);
            selectedCode = item.Code;
            selectedName = item.KbnName;
        }
        $select.append($option);
    });

    $("#" + codeId).val(selectedCode);
    $("#" + nameId).val(selectedName);

    $select.off("change").on("change", function() {
        var sc = $(this).val();
        var sn = $(this).find("option:selected").text();
        $("#" + codeId).val(sc);
        $("#" + nameId).val(sn);
    });
}
//****************************************************************************
// コードからプルダウン＆名称セット（キャッシュ利用）
//
//
//
//
//****************************************************************************
function setKubunSelect(txtCodeId, txtNameId, selectId, code) {
    $("#" + txtCodeId).val(code);
    $("#" + selectId).val(code);
    getKubunNameAndSetSelect(txtCodeId, txtCodeId, txtNameId, selectId);
}

//****************************************************************************
// ページロード時にキャッシュ初期化
//
//
//
//
//****************************************************************************
$(function(){
    initKubunCache();
});

//****************************************************************************
// getMstShain
//
//
//
//
//****************************************************************************
var eigyoshoList = [];

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
		
		// 新規の時は削除ボタンは非活性
		$("#btnDelete").prop('disabled', false);
		if (isNew == "1") {
			$("#btnDelete").prop('disabled', true);
		}

		// 取得した値の格納
		var mstDatas = contents["mstDatas"];
		for (var i = 0; i < mstDatas.length; i++) {
			$("#txtShainNO").val(mstDatas[i]["ShainNO"]);
			$("#txtShainName").val(mstDatas[i]["ShainName"]);
			$("#txtPassword").val(mstDatas[i]["Password"]);
			$("#txtUserCode").val(mstDatas[i]["UserKbn"]);
			$("#txtShainCode").val(mstDatas[i]["ShainKbn"]);
			$("#txtShukinboCode").val(mstDatas[i]["ShukinboKbn"]);
			$("#txtEigyoshoCode").val(mstDatas[i]["EigyoshoCode"]);
			$("#txtBushoCode").val(mstDatas[i]["BushoCode"]);
			$("#txtYukyuKyukaFuyoNissu").val(mstDatas[i]["YukyuKyukaFuyoNissu"]);
			$("#txtJikyuNikkyuCode").val(mstDatas[i]["JikyuNikkyuKbn"]);
			$("#txtKinmuKaishiJiCode").val(mstDatas[i]["KinmuKaishiJi"]);
			$("#txtKinmuKaishiFunCode").val(mstDatas[i]["KinmuKaishiFun"]);
			$("#txtKinmuShuryoJiCode").val(mstDatas[i]["KinmuShuryoJi"]);
			$("#txtKinmuShuryoFunCode").val(mstDatas[i]["KinmuShuryoFun"]);
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
			$("#txtTsukinHiCode").val(mstDatas[i]["TsukinHiKbn"]);
			$("#txtTaisyokuDate").val(mstDatas[i]["TaisyokuDate"]);
			
			eigyoshoList = mstDatas[i]["ShoriKanoEigyoshos"] || []; // サーバーからの配列
			
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
		//renderEigyoshoTable();
		getShoriKanoEigyoshos();
		
		console.log("処理可能営業所リスト:", eigyoshoList);
		
		// 営業所名
		getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');
		// 部署名
		getBushoName('txtBushoCode', 'txtBushoName');
		// 処理可能営業所名
		getEigyoshoName('txtShoriKanoEigyoshoCode', 'txtShoriKanoEigyoshoName');
		// ユーザ区分
		getKubunNameAndSetSelect('txtUserKbnCode','txtUserCode','txtUserKbnName','selectUserKbnName');
		// 社員区分
		getKubunNameAndSetSelect('txtShainKbnCode','txtShainCode','txtShainKbnName','selectShainKbnName');
		// 出勤簿入力区分
		getKubunNameAndSetSelect('txtShukinboKbnCode','txtShukinboCode','txtShukinboKbnName','selectShukinboKbnName');
		// 時給日給区分
		getKubunNameAndSetSelect('txtJikyuNikkyuKbnCode','txtJikyuNikkyuCode','txtJikyuNikkyuKbnName','selectJikyuNikkyuKbnName');
		// 通勤費精算区分
		getKubunNameAndSetSelect('txtTsukinHiKbnCode','txtTsukinHiCode','txtTsukinHiKbnName','selectTsukinHiKbnName');
		// 勤務開始・終了時刻
		getKubunNameAndSetSelect('txtKinmuKaishiJiKbnCode','txtKinmuKaishiJiCode','txtKinmuKaishiJiKbnName','selectKinmuKaishiJiKbnName');
		getKubunNameAndSetSelect('txtKinmuKaishiFunKbnCode','txtKinmuKaishiFunCode','txtKinmuKaishiFunKbnName','selectKinmuKaishiFunKbnName');
		getKubunNameAndSetSelect('txtKinmuShuryoJiKbnCode','txtKinmuShuryoJiCode','txtKinmuShuryoJiKbnName','selectKinmuShuryoJiKbnName');
		getKubunNameAndSetSelect('txtKinmuShuryoFunKbnCode','txtKinmuShuryoFunCode','txtKinmuShuryoFunKbnName','selectKinmuShuryoFunKbnName');

	});
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
                        "<input type=\"text\" class=\"w80\" id=\"eigyoshoCode_" + count + "\" name=\"eigyoshoCode[]\" value=\"" + code + "\" " +
                               "onblur=\"getEigyoshoName('eigyoshoCode_" + count + "','eigyoshoName_" + count + "');\">" +
                        "<input type=\"text\" class=\"w200\" id=\"eigyoshoName_" + count + "\" name=\"eigyoshoName[]\" value=\"\" readonly>" +
                        " <button type=\"button\" onclick=\"removeShoriKanoEigyosho(" + count + ")\">×</button>" +
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
    eigyoshoList.splice(index, 1);   // 配列から削除
    renderEigyoshoTable();           // 再描画
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
                    "<input type=\"text\" class=\"w80\" id=\"eigyoshoCode_" + count + "\" name=\"eigyoshoCode[]\" value=\"" + code + "\" " +
                           "onblur=\"getEigyoshoName('eigyoshoCode_" + count + "','eigyoshoName_" + count + "');\">" +
                    "<input type=\"text\" class=\"w200\" id=\"eigyoshoName_" + count + "\" name=\"eigyoshoName[]\" value=\"\" readonly>" +
                    " <button type=\"button\" onclick=\"removeShoriKanoEigyosho(" + count + ")\">×</button>" +
                "</td>" +
            "</tr>";
        
        $("#ShoriKanoEigyoshoResult").append(row);
        
        if (code) {
            getEigyoshoName('eigyoshoCode_' + count, 'eigyoshoName_' + count);
        }
    }
}