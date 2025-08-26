<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

%>

<main id="main-content" class="nom">
	
	<div class="headerArea" id="headerArea">
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w300">営業所マスタメンテ</td>
				</tr>
			</table>
			<table>
				<tr>
					<td class="title center w100">
						<a href="#" onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCode'); return false;">営業所</a>
					</td>
					<td class="value w165">
						<input type="text" class="w80" maxlength="3" name="srhTxtEigyoshoCode" id="srhTxtEigyoshoCode" value="" onblur="getEigyoshoName('srhTxtEigyoshoCode', '');">
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCode');">
						<button type="button" onclick="getMstEigyosho()">検索</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div class="mainArea" id="mainArea" style="visibility:hidden;">

		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w100 req">営業所コード</td>
					<td class="value w500">
						<input type="text" class="w80" maxlength="3" name="txtEigyoshoCode" id="txtEigyoshoCode" value="" >
						<input type="hidden" class="" name="hdnEigyoshoCode" id="hdnEigyoshoCode"  value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w100 req">営業所名</td>
					<td class="value w500">
						<input type="text" class="w200" maxlength="20" name="txtEigyoshoName" id="txtEigyoshoName" value="" >
					</td>
				</tr>
			</table>
		</div>
		
		<div class="inputArea lastEdit">
			<table>
				<tr>
					<td class="title center w100 rep">最終更新社員</td>
					<td class="label w200" id="lblSaishuKoshinShainName"></td>
					<td class="title center w100 rep">最終更新日時</td>
					<td class="label w80 center" id="lblSaishuKoshinDate"></td>
					<td class="label w80 center" id="lblSaishuKoshinJikan"></td>
					<input type="hidden" class="" name="hdnSaishuKoshinShainNO" id="hdnSaishuKoshinShainNO"  value="" >
					<input type="hidden" class="" name="hdnSaishuKoshinShainName" id="hdnSaishuKoshinShainName"  value="" >
					<input type="hidden" class="" name="hdnSaishuKoshinDate" id="hdnSaishuKoshinDate"  value="" >
					<input type="hidden" class="" name="hdnSaishuKoshinJikan" id="hdnSaishuKoshinJikan"  value="" >
					<input type="hidden" class="" name="hdnIsNew" id="hdnIsNew"  value="" >
				</tr>
			</table>
		</div>

	</div>
	
	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
		<button type="button" class="" name="btnDelete" id="btnDelete" onclick="onDelete()">削除 [ F2 ] </button>
		<button type="button" class="" name="btnUpdate" id="btnUpdate" onclick="onUpdate()">確定 [ F9 ] </button>
	</div>
	
</main>