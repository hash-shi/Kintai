//************************************************************** 
// システム名	海自造修整備補給システム
// 業務名	在庫管制
// 機能名	W2uiグリッドの一部共通化
// 画面名	共通
//
// 作成者	富士通(株)
//	
// 更新履歴
// 日付		担当	内容
//---------------------------------------------------------------------------------------------
// 20170810	TJS	新規作成
//**************************************************************

//**********************************************	
// 関数名	グリッドOnChange処理
// 機能		テキストボックスのOnChange時
//			変更マークを付与する。
//
// 引数		targetGrid	グリッドオブジェクト
//			obj			テキストボックスオブジェクト
// 戻り値	なし
//**********************************************
function fn_GridOnChange(targetGrid, obj){
	var recid = $(obj).attr('recid');
	var elementBaseName = $(obj).attr('elementBaseName');
	targetGrid.get(recid)[elementBaseName]	= obj.value;
	if( false == fn_GridIsNewRecord(targetGrid, recid) ){
		targetGrid.get(recid).status		= '●';
		targetGrid.refreshCell(recid, 'status'	);
	}
}

//**********************************************	
// 関数名	新規行チェック
// 機能		新規に追加された行かをチェックする
//
// 引数		targetGrid	グリッドオブジェクト
//			recid		行に設定されたrecid
// 戻り値	boolean		true	:	新規追加行
//						false	:	既存行
//**********************************************
function fn_GridIsNewRecord(targetGrid, recid){
	return (targetGrid.get(recid).newline !='');
}

//**********************************************	
// 関数名	グリッド行追加処理
// 機能		行を追加する
//
// 引数		targetGrid	グリッドオブジェクト
//			individual_data	自分自身で設定したい個別データ
//			callback	コールバック
// 戻り値	なし
//**********************************************
function fn_GridAddRecord(targetGrid, individual_data, callback){
	var insertRowData = {};

	// 必須
	targetGrid.recid = targetGrid.recid + 1;
	insertRowData['recid']	= targetGrid.recid;

	// 追加データをマージ
	$.extend(insertRowData, individual_data);
	
	// 上記項目以外を空白にする
	$.each(targetGrid.columns, function(){
		if(this.field in insertRowData == false){
			insertRowData[this.field] = '';
		}
	});
	
	targetGrid.add(insertRowData);

	// 追加行へのイベント登録
	fn_SetGridChangeEvent(targetGrid, callback);
	
}


//**********************************************
// 関数名	グリッド行削除処理
// 機能		行を削除する
//			既存行の場合、削除マークを付ける
//			新規追加行の場合、物理的に削除する
//			
// 引数		targetGrid	グリッドオブジェクト
// 戻り値	なし
//**********************************************
function fn_GridDeleteRecord(targetGrid){
	// チェックボックスのIDの先頭が固定
	var recids = $('input[id^=select_check]:checked').map(function(){
		//$(this)でjQueryオブジェクトが取得できる。val()で値をvalue値を取得。
		return $(this).attr('recid');
	}).get();
	
	$.each(recids, function(){
		if( true == fn_GridIsNewRecord(targetGrid, this) ){
			// 物理削除
			targetGrid.remove(this);
		}else{
			// 削除マーク
			targetGrid.get(this).status = '-';
			targetGrid.refreshCell(this, 'status');
		}
	});
}

//**********************************************
// 関数名	特定列の非表示処理
// 機能		指定列を非表示にする
//			
// 引数		targetGrid	グリッドオブジェクト
//			columns		列名
// 戻り値	なし
//**********************************************
function fn_SetGridHiddenColumns(targetGrid, columns){
	if(Array.isArray(columns)){
		for(var count=0;count<columns.length;count++){
			targetGrid.hideColumn(columns[count]);
		}
	}
	else{
		targetGrid.hideColumn(columns);
	}
}

//**********************************************
// 関数名	テキストボックスにイベント登録
// 機能		テキストボックスにイベント登録
//			
// 引数		targetGrid	グリッドオブジェクト
//			callback	コールバック
// 戻り値	なし
//**********************************************
function fn_SetGridChangeEvent(targetGrid, callback){
	$('div[name='+targetGrid.name+'] input[type="text"]').off('change');
	$('div[name='+targetGrid.name+'] input[type="text"]').on("change", function(){ fn_GridOnChange(targetGrid, this);callback(); });
}


//**********************************************	
//関数名	fn_MouseOverAtW2ui
//引数		obj
//			なし
//戻り値		なし
//**********************************************
function fn_MouseOverAtW2ui(obj){
	$(obj).attr('title', $(obj).val());
}