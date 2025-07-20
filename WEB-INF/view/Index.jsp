<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<div class="login">
	<div class="title" >
		<span>ログインしてください。</span>
	</div>
	<div class="inputArea">
		<table>
			<tr>
				<td class="title right w90">社員NO</td>
				<td class="value">
					<input type="text" class="w150" name="txtShainNO" id="txtShainNO" value="" >
				</td>
			</tr>
			<tr>
				<td class="title right w90">パスワード</td>
				<td class="value">
					<input type="password" class="w150" name="txtPassword" id="txtPassword" value="">
				</td>
			</tr>
		</table>
	</div>
	<div class="buttonArea">
		<button type="button" onclick="onLogin();">ログイン</button>
	</div>
</div>