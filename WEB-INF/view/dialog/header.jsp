<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<div class="dialog">
	<table width="100%" cellspacing="0" cellpadding="0">
		<tr height="25" id="dialogBar" style="background-color: #008000">
			<td style="position: relative;">
				<div id="dialogTitle" style="height: 25px; line-height: 25px; font-size: 12px; margin-left: 5px; -ms-overflow-y: hidden; color: #ffffff;">!#TITLE#!</div>
				<div id="dialogClose" style="position:absolute;right:2px;top:1px;"><img src="./images/close.png" style="width:23px;height:23px;cursor:pointer;" onclick="closeDialog();"></div>
			</td>
		</tr>
	</table>