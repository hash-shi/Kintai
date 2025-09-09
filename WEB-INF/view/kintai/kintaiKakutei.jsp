<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%
	//対象年月初期値の取得
	String	taishoDate					= (String)request.getAttribute("result");
%>

<main id="main-content" class="nom">
	<div class="headerArea" id="headerArea">
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w300">勤怠確定入力</td>
				</tr>
			</table>
			<table>
				<tr>
					<td class="title center w100 req">対象年月</td>
					<td class="value w100">
						<input type="text" class=""  style="width: 80px; text-align: right;" name="srhTxtTaishoYM" id="srhTxtTaishoYM" value="<%=UtilEscape.htmlspecialchars(taishoDate) %>" maxlength="7" onblur="getTaishoYMFormat();" autofocus onfocus="this.setSelectionRange(7, 7)">
						<input type="hidden" name="txtTaishoYM" id="txtTaishoYM" value="">
					</td>
					<td class="value w50">
						<button type="button" onclick="onSearchKintaiKakutei();">検索</button>
					</td>
					<td><input type="hidden" name="txtKakuteiCount" id="txtKakuteiCount" value=""></td>
				</tr>
			</table>
		</div>
	</div>
	
	<div class="mainArea" id="nyuryokuArea" style="visibility:hidden;">
		<div class="inputArea">
				<table>
					<thead>
						<tr>
							<th class="title center">	<input type="checkbox" id="cbxKakuteiAll" class="kintaiKakuteiText" name="option" value="" onclick="onSentakuAll();"></th>
							<th class="title center w210" colspan="2">	<a class="kintaiKakuteiText">営業所</a></th>
							<th class="title center w140">				<a class="kintaiKakuteiText">月給制</a></th>
							<th class="title center w140">				<a class="kintaiKakuteiText">時給日給制</a></th>
						</tr>
					</thead>
					<tbody id="kihonNyuryokuArea">
					</tbody>
				</table>
		</div>
	</div>
	
	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
	    <button type="button" onclick="onKakuteiKaijo();">確定解除 [ F2 ] </button>
		<button type="button" onclick="onKakutei();">確定 [ F9 ] </button>
	</div>
	
</main>