<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape"%>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

	String shainNO = "";
	String password = "";
	

	// ユーザー情報の取得
	UserInformation userInformation		= (UserInformation)request.getSession().getAttribute(Define.SESSION_ID);

	if (userInformation != null) {
		shainNO				= userInformation.getShainNO();
		password		= userInformation.getPassword();
	}
	
%>

<main id="main-content">
	<div class="inputArea">
		<table class="header">
			<tr>
				<td class="title green1 bold">パスワード変更</td>
			</tr>
			<tr>
				<td class="title green2"></td>
			</tr>
		</table>
	</div>
  
	<div class="inputArea">
		<table class="body">
			<tr>
				<td class="title w160">現在のパスワード</td>
				<td class="value w200">
					<input type="hidden" class="" name="hdnShainNO" id="hdnShainNO"  value="<%=UtilEscape.htmlspecialchars(shainNO) %>" >
					<input type="hidden" class="" name="hdnPassword" id="hdnPassword"  value="<%=UtilEscape.htmlspecialchars(password) %>" >
					<input type="password" class="w190"  maxlength="50"  name="txtPasswordNow" id="txtPasswordNow"  value="" >
				</td>
			</tr>
			<tr>
				<td class="title w160">新しいパスワード</td>
				<td class="value w200">
					<input type="password" class="w190"  maxlength="50"  name="txtPasswordNew" id="txtPasswordNew"  value="" >
				</td>
			</tr>
			<tr>
				<td class="title w160">新しいパスワード(確認)</td>
				<td class="value w200">
					<input type="password" class="w190"  maxlength="50"  name="txtPasswordRe" id="txtPasswordRe"  value="" >
				</td>
			</tr>
		</table>
	</div>
	<div class="buttonArea">
		<button type="button" onclick="onUpdate();">登録</button>
	</div>
</main>