/*
*
* サイドメニュー関連の処理を記入
*
*/

// サイドメニューの開閉状態を保持する
var sskSideMenu = 'sideMenu';

// サイドメニューの親項目の開閉状態を保持する
var sskProcessID = 'processID';


// ----------------------------------------------------------------------------
// 初期化処理
// 
// 
// ----------------------------------------------------------------------------
$(document).ready(function(){
	// セッションストレージよりメニューの開閉状態を取得して反映する。
	getSskSideMenu();
	getSskProcessID();
	
});

//****************************************************************************
// アコーディオン機能（submit防止 + 切り替え）
//----------------------------------------------------------------------------
//
//
//
//****************************************************************************
document.addEventListener('DOMContentLoaded', function () {
	
	const toggles = document.querySelectorAll('.sideMenuParent');
	toggles.forEach(toggle => {
		toggle.addEventListener('click', function () {
			const submenu = this.nextElementSibling;
			submenu.style.display = (submenu.style.display === 'block' ? 'none' : 'block');
		});
	});
	
});

//****************************************************************************
// サイドメニューの表示切替え
//----------------------------------------------------------------------------
//
//
//
//****************************************************************************
function chgSideMenu() {
	
		// id=sideMenuを取得する。
		var sideMenu = $("[id=sideMenu]");
	
		// sideMenuの開閉状態を取得する。
		var display = sideMenu.css("display");
		console.log(display);
		
		// 開いている場合は閉じる
		// 閉じている場合は開く
		if (display == "block") {
			sideMenu.css("display", "none");
		}
		else {
			sideMenu.css("display", "block");
		}
}

//****************************************************************************
// 画面遷移
//----------------------------------------------------------------------------
//
//
//
//****************************************************************************
function movContents(pageID){
	if (pageID == undefined){ return; }
	if (pageID) {
		
		// メニューの開閉状態を保存する。
		setSskSideMenu();
		// 機能ボタンの開閉状態を保存する。
		setSskProcessID();
		
		// 画面遷移
		location.href = "./" + pageID;
	}
}

//****************************************************************************
// ストレージの取得
//----------------------------------------------------------------------------
//
//
//
//****************************************************************************
function getSskSideMenu() {
	
	var value = "";
	
	// セッションストレージを取得する。
	value = sessionStorage.getItem(sskSideMenu);
	
	// ない場合はスキップ
	console.log(value);
	if (!value) { return; }
	
	// id=sideMenuを取得する。
	var sideMenu = $("[id=sideMenu]");

	// サイドメニューの開閉状態を設定
	sideMenu.css("display", value);
	
}

function getSskProcessID() {
	
	var value = "";
	
	// セッションストレージを取得する。
	value = sessionStorage.getItem(sskProcessID);	
	
	// ない場合はスキップ
	console.log(value);
	if (!value) { return; }
	
	//,で区切っているので分割
	var values = value.split(',');
	
	// 先頭行は空のため1からスタート
	for (var i = 1; i < values.length; i++) {
		
		var process = values[i];
		console.log(process);
		
		// id と 開閉状態を分割
		var id = process.split('-')[0];
		var display = process.split('-')[1];
		
		// idで要素を検索
		var parent = $("[id=" + id +"]");
		
		// 要素がない場合はスキップ
		console.log(parent);
		if (!parent) { continue; }
		
		// parentの次の要素を取得する。(sideMenuChildが取れるハズ)
		const child = parent[0].nextElementSibling;
		console.log(child);
		
		// サイドメニューの開閉状態を設定
		//child.css("display", display);
		child.style.display = display;
	}
}

//****************************************************************************
// ストレージの登録
//----------------------------------------------------------------------------
//
//
//
//****************************************************************************
function setSskSideMenu() {
	
//	var value = "";
	
	// id=sideMenuを取得する。
	var sideMenu = $("[id=sideMenu]");

	// sideMenuの開閉状態を取得する。
	var display = sideMenu.css("display");
	console.log(display);
	
	// セッションストレージに登録する。
	sessionStorage.setItem(sskSideMenu, display);

}

function setSskProcessID() {
	
	var value = "";
	
	// class=sideMenuParentを取得
	var parents = $("[class=sideMenuParent]");
	console.log(parents);
	
	for (var i = 0; i < parents.length; i++) {
		var parent = parents[i];
		console.log(parent.id);
		
		// parentの次の要素を取得する。(sideMenuChildが取れるハズ)
		const child = parent.nextElementSibling;
		console.log(child);
		
		// 開閉状態を取得する。
		var display = child.style.display;
		console.log(display);

		// セッションストレージに登録する情報の作成。
		value += ',' + parent.id + '-' + display
		console.log(parent.id + '-' + display);
	}
	
	// セッションストレージに登録する。
	console.log(value);
	sessionStorage.setItem(sskProcessID, value);
	
}

////****************************************************************************
//// ストレージの登録
////----------------------------------------------------------------------------
////
////
////
////****************************************************************************
//function setSessionStorage() {
//	
//	var value = "";
//	
//	// id=side-の要素を取得する。
//	var toggles = $("[id^=sideMenu]");
//	
//	for (var i = 0; i < toggles.length; i++) {
//		console.log(toggles[i]);
//		// 同じ親要素内の次の要素を取得
//		const submenu = toggles[i].nextElementSibling;
//		// id + 開閉状態を取得
//		console.log(toggles[i].id);
//		console.log(submenu.style.display);
//		value += ',' + toggles[i].id + '_' + submenu.style.display
//	}
//	
//	// セッションストレージに登録する。
//	console.log(value);
//	sessionStorage.setItem(sessionStorageKey, value);
//	
//}



////****************************************************************************
//// ストレージの取得
////----------------------------------------------------------------------------
////
////
////
////****************************************************************************
//function getSessionStorage() {
//	
//	var value = "";
//	
//	// セッションストレージを取得する。
//	value = sessionStorage.getItem(sessionStorageKey);
//	
//	// ない場合はスキップ
//	console.log(value);
//	if (!value) { return; }
//	
//	//,で区切っているので分割
//	var values = value.split(',');
//	
//	// 先頭行は空のため1からスタート
//	for (var i = 1; i < values.length; i++) {
//		// id と 開閉状態を分割
//		console.log(values[i]);
//		var id = values[i].split('_')[0];
//		var display = values[i].split('_')[1];
//		
//		// idで要素を検索
//		var toggle = $("[id=" + id +"]");
//		
//		// 要素がない場合はスキップ
//		console.log(toggle);
//		if (!toggle) { continue; }
//		
//		// 同じ親要素内の次の要素を取得
//		const submenu = toggle[0].nextElementSibling;
//		
//		// 開閉状態を付加
//		console.log(submenu);
//		submenu.style.display = (display === '' ? 'none' : display);
//		console.log(submenu.style.display);
//	}
//	
//}
//
