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
				<li><a id="lnkChgPassword"  href="#" onclick="movContents('ChgPassword'); return false;">パスワード設定</a></li>
			</ul>
		</li>
		<li>
			<div id="sideMenuKintai"  class="sideMenuParent">勤怠</div>
			<ul class="sideMenuChild">
				<li><a id="lnkKinShukkinBo"  href="#" onclick="movContents('KinShukkinBo'); return false;">出勤簿入力</a></li>
				<li><a id="lnkChiChinginkeisansho"  href="#" onclick="movContents('ChiChinginkeisansho'); return false;">賃金計算書入力</a></li>
				<li><a id="lnkKintaiList"  href="#" onclick="movContents('KintaiList'); return false;">勤怠リスト</a></li>
				<% if (userKbn.equals("01") && bushoKbn.equals("00")) { %>
				<li><a id="lnkKintaiKakutei"  href="#" onclick="movContents('KintaiKakutei'); return false;">月次確定入力</a></li>
				<li><a id="lnkKintaiOutData"  href="#" onclick="movContents('KintaiOutData'); return false;">データ出力</a></li>
				<% } %>
			</ul>
		</li>
		<% if (userKbn.equals("01") && bushoKbn.equals("00")) { %>
		<li>
			<div id="sideMenuMaster"  class="sideMenuParent">マスタメンテ</div>
			<ul class="sideMenuChild">
				<li><a id="lnkMstKanri"  href="#" onclick="movContents('MstKanri'); return false;">管理マスタ</a></li>
				<li><a id="lnkMstEigyosho"  href="#" onclick="movContents('MstEigyosho'); return false;">営業所マスタ</a></li>
				<li><a id="lnkMstBusho"  href="#" onclick="movContents('MstBusho'); return false;">部署マスタ</a></li>
				<li><a id="lnkMstShain"  href="#" onclick="movContents('MstShain'); return false;">社員マスタ</a></li>
				<li><a id="lnkMstKubun"  href="#" onclick="movContents('MstKubun'); return false;">区分名称マスタ</a></li>
				<li><a id="lnkMstChohyoList"  href="#" onclick="movContents('MstChohyoList'); return false;">マスタ帳票リスト</a></li>
			</ul>
		</li>
		<% } %>
	</ul>
</side>