<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

	String shainNO = "";
	String shainName = "";
	String eigyoshoCode = "";
	String eigyoshoName = "";
	String bushoCode = "";
	String bushoName = "";
	String loginDate = "";
	
	// ユーザー情報の取得
	UserInformation userInformation		= (UserInformation)request.getSession().getAttribute(Define.SESSION_ID);

	if (userInformation != null) {
		shainNO						= userInformation.getShainNO();
		shainName					= userInformation.getShainName();
		eigyoshoCode			= userInformation.getEigyoshoCode();
		eigyoshoName			= userInformation.getEigyoshoName();
		bushoCode					= userInformation.getBushoCode();
		bushoName					= userInformation.getBushoName();
		loginDate					= userInformation.getLoginDate();
	}
	
%>

<header id="header" >
	<div>
		<input type="hidden" name="hdnDialogSearchId" id="hdnDialogSearchId" value="" />
		<input type="hidden" name="hdnDialogReturnId" id="hdnDialogReturnId" value="" />
		<div class="inputArea" style="margin-left:0px" >
			<table style="border-collapse:separate;">
				<tr>
					<td class="w80">
						<ul class="">
							<li>
								<img class="img border" src="./images/home.png">
								<a href="#" onclick="movContents('menu'); return false;">HOME</a>
							</li>
						</ul>
					</td>
					<td class="w100">
						<ul class="">
							<li>
								<img class="img border" src="./images/logout.png">
								<!-- <a href="#" onclick="movContents('index'); return false;">LOGOUT</a> -->
								<a href="#" onclick="onLogout(); return false;">LOGOUT</a>
							</li>
						</ul>
					</td>
					<td class="w140">
						<ul class="">
							<li>
								<img class="img border" src="./images/arrow_left.png">
								<a href="#" onclick="chgSideMenu(); return false;">MENU ON/OFF</a>
							</li>
						</ul>
					</td>
				</tr>
			</table>
		</div>
	</div>	
	<div class="w900">
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w80">営業所</td>
					<td class="value w200">
						<%=UtilEscape.htmlspecialchars(eigyoshoCode) %>　<%=UtilEscape.htmlspecialchars(eigyoshoName) %>
					</td>
					<td class="title center w80">部署</td>
					<td class="value w200">
						<%=UtilEscape.htmlspecialchars(bushoCode) %>　<%=UtilEscape.htmlspecialchars(bushoName) %>
					</td>
					<td class="value center w100">
						<%=UtilEscape.htmlspecialchars(loginDate) %>　
					</td>
					<td class="value right w200" style="border:none; background-color: transparent;">
						<img class="img border" src="./images/user.png">
						<%=UtilEscape.htmlspecialchars(shainName) %>
					</td>
				</tr>
			</table>
		</div>
	</div>
</header>

<div class="main">
	<div class="container">
