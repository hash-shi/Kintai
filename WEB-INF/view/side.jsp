<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape"%>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

	String userKbn = "";
	String bushoKbn = "";

	// ユーザー情報の取得
	UserInformation userInformation		= (UserInformation)request.getSession().getAttribute(Define.SESSION_ID);

	if (userInformation != null) {
		userKbn						= userInformation.getUserKbn();
		bushoKbn					= userInformation.getBushoKbn();
	}
	
%>

<side id="sideMenu" >
	<ul class="sideMenu">
		<li>
			<div id="sideMenuUser"  class="sideMenuParent">ユーザ設定</div>
			<ul class="sideMenuChild">
				<li><a id="lnkChgPassword"  href="#" onclick="movContents('chgPassword'); return false;">パスワード設定</a></li>
			</ul>
		</li>
		<li>
			<div id="sideMenuKintai"  class="sideMenuParent">勤怠</div>
			<ul class="sideMenuChild">
				<li><a id="lnkKinShukkinBo"  href="#" onclick="movContents('kinShukkinBo'); return false;">出勤簿入力</a></li>
				<li><a id="lnkChiChinginkeisansho"  href="#" onclick="movContents('chiChinginkeisansho'); return false;">賃金計算書入力</a></li>
				<li><a id="lnkKintaiList"  href="#" onclick="movContents('kintaiList'); return false;">勤怠リスト</a></li>
				<% if (userKbn.equals("01") && bushoKbn.equals("00")) { %>
				<li><a id="lnkKintaiKakutei"  href="#" onclick="movContents('kintaiKakutei'); return false;">月次確定入力</a></li>
				<li><a id="lnkKintaiOutData"  href="#" onclick="movContents('kintaiOutData'); return false;">データ出力</a></li>
				<% } %>
			</ul>
		</li>
		<% if (userKbn.equals("01") && bushoKbn.equals("00")) { %>
		<li>
			<div id="sideMenuMaster"  class="sideMenuParent">マスタメンテ</div>
			<ul class="sideMenuChild">
				<li><a id="lnkMstKanri"  href="#" onclick="movContents('mstKanri'); return false;">管理マスタ</a></li>
				<li><a id="lnkMstEigyosho"  href="#" onclick="movContents('mstEigyosho'); return false;">営業所マスタ</a></li>
				<li><a id="lnkMstBusho"  href="#" onclick="movContents('mstBusho'); return false;">部署マスタ</a></li>
				<li><a id="lnkMstShain"  href="#" onclick="movContents('mstShain'); return false;">社員マスタ</a></li>
				<li><a id="lnkMstKubun"  href="#" onclick="movContents('mstKubun'); return false;">区分名称マスタ</a></li>
				<li><a id="lnkMstChohyoList"  href="#" onclick="movContents('mstChohyoList'); return false;">マスタ帳票リスト</a></li>
			</ul>
		</li>
		<% } %>
	</ul>
</side>