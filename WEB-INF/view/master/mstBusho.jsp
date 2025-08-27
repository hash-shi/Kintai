<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<% ArrayList<HashMap<String, String>> mstKubun0153 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0153");%>

<main id="main-content" class="nom">

	<div class="headerArea" id="headerArea">
		 <div class="inputArea">
		 	<table>
		 		<tr>
					<td class="title center w300">部署マスタメンテ</td>
				</tr>
		 	</table>
			<table>
				<tr>
					<td class="title center w100">
						<a href="#" onclick="opnDialog('srhMstBusho','srhTxtBushoCode'); return false;">部署</a>
					</td>
					<td class="value w165">
						<input type="text" class="w80" maxlength="4" name="srhTxtBushoCode" id="srhTxtBushoCode" value="" onblur="getBushoName('srhTxtBushoCode', '');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','srhTxtBushoCode');">
						<button type="button" onclick="getMstBusho()">検索</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div id = "mainArea" class="mainArea" style="visibility: hidden;">
	
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w100 req">部署コード</td>
					<td class="value w500">
						<input type="text" class="w80" maxlength="4" name="txtBushoCode" id="txtBushoCode" value="" >
						<input type="hidden" class="" name="hdnBushoCode" id="hdnBushoCode"  value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w100 req">部署名</td>
					<td class="value w500">
						<input type="text" class="w200" maxlength="20" name="txtBushoName" id="txtBushoName" value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w100 req">部署区分</td>
					<td class="value w500">
						<input type="text" class="w80" maxlength="2" name="txtBushoKbn" id="txtBushoKbn" value="" onblur="setSelBushoKbnName();">
						<select  class="w120" name = "selBushoKbnName" id="selBushoKbnName" value="" onchange="setTxtBushoKbn();">
							<option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0153.size() ; count++){ HashMap<String, String> record = mstKubun0153.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
						</select>
					</td>
				</tr>
				<tr>
					<td class="title center w100 req">
						<a href="#" onclick="opnDialog('srhMstEigyosho','txtEigyoshoCode','txtEigyoshoName'); return false;">営業所</a>
					</td>
					<td class="value w500">
						<input type="text" class="w80" maxlength="3" name="txtEigyoshoCode" id="txtEigyoshoCode" value="" onblur="getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');">
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtEigyoshoCode','txtEigyoshoName');">
						<input type="text" class="w200" maxlength="20" name="txtEigyoshoName" id="txtEigyoshoName" value="" readonly>
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
	
	<div id="buttonArea"  class="buttonArea right"  style="visibility: hidden;">
		<button type="button" class="" name="btnDelete" id="btnDelete" onclick="onDelete()">削除 [ F2 ]</button>
		<button type="button" class="" name="btnUpdate" id="btnUpdate" onclick="onUpdate()">確定 [ F9 ]</button>
	</div>
</main>
