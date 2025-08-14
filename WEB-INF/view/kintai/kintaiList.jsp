<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

String shainNo = "";
String eigyoshoCode = "";
String bushoCode = "";
String userKbn = "";
String bushoKbn = "";

// ユーザー情報の取得
UserInformation userInformation = (UserInformation)request.getSession().getAttribute(Define.SESSION_ID);
if (userInformation != null) {
	shainNo						= userInformation.getShainNO();
	eigyoshoCode			= userInformation.getEigyoshoCode();
	bushoCode					= userInformation.getBushoCode();
	userKbn						= userInformation.getUserKbn();
	bushoKbn					= userInformation.getBushoKbn();
}

//処理区分の取得
ArrayList<HashMap<String, String>> mstKubun0504 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0504");
ArrayList<HashMap<String, String>> mstKubun0050 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0050");

%>

<main id="main-content" class="nom">
	
	<div class="headerArea" id="headerArea">
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w300">勤怠リスト</td>
					<input type="hidden" class="" name="hdnShainNo" id="hdnShainNo"  value="<%=UtilEscape.htmlspecialchars(shainNo) %>" >
					<input type="hidden" class="" name="hdnEigyoshoCode" id="hdnEigyoshoCode"  value="<%=UtilEscape.htmlspecialchars(eigyoshoCode) %>" >
					<input type="hidden" class="" name="hdnBushoCode" id="hdnBushoCode"  value="<%=UtilEscape.htmlspecialchars(bushoCode) %>" >
					<input type="hidden" class="" name="hdnUserKbn" id="hdnUserKbn"  value="<%=UtilEscape.htmlspecialchars(userKbn) %>" >
					<input type="hidden" class="" name="hdnBushoKbn" id="hdnBushoKbn"  value="<%=UtilEscape.htmlspecialchars(bushoKbn) %>" >
				</tr>
			</table>
			<table>
				<tr>
					<td class="title center w70">処理選択</td>
					<td class="value w210">
						<select name="selShoriSentaku"  id="selShoriSentaku" >
							<% for (int count = 0 ; count < mstKubun0504.size() ; count++){ HashMap<String, String> record = mstKubun0504.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
						</select>
						<button type="button" onclick="setShoriSentaku()">検索</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div class="mainArea" id="mainArea" style="visibility:hidden;">
	
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w70">処理選択</td>
					<td class="value w210">
						<input type="text" class="w200"  name="txtShoriSentaku" id="txtShoriSentaku" value="" readonly>
					</td>
				</tr>
			</table>
		</div>
		
		<div class="inputArea">
			<table>
			
				<tr id="taishoNengetsu" style="display: none">
					<td class="title center w70 req">対象年月</td>
					<td class="value w600">
						<input type="text" class="w60 right" maxlength="7" name="srhTxtTaishoNengetsuF" id="srhTxtTaishoNengetsuF" value="">
						-
						<input type="text" class="w60 right" maxlength="7" name="srhTxtTaishoNengetsuT" id="srhTxtTaishoNengetsuT" value="">
						(YYYY/MM)
					</td>
				</tr>
				
				<tr id="taishoNendo" style="display: none;">
					<td class="title center w70 req">対象年度</td>
					<td class="value w600">
						<input type="text" class="w60 right" maxlength="4" name="srhTxtTaishoNendoF" id="srhTxtTaishoNendoF" value="">
						-
						<input type="text" class="w60 right" maxlength="4" name="srhTxtTaishoNendoT" id="srhTxtTaishoNendoT" value="">
						(YYYY)
					</td>
				</tr>
				
				<tr id="eigyosho" style="display: none;">
					<td class="title center w70">営業所</td>
					<td class="value w600">
						<input type="text" class=""  style="width: 80px"  maxlength="3" name="srhTxtEigyoshoCodeF" id="srhTxtEigyoshoCodeF"  value=""  onblur="getEigyoshoName('srhTxtEigyoshoCodeF', 'srhTxtEigyoshoNameF');" >
						<img class="img border" src="./images/search.png" onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCodeF','srhTxtEigyoshoNameF');">
						<input type="text" class=""  style="width: 120px" name="srhTxtEigyoshoNameF" id="srhTxtEigyoshoNameF" value="" readonly>
						～
						<input type="text" class=""  style="width: 80px"  maxlength="3" name="srhTxtEigyoshoCodeT" id="srhTxtEigyoshoCodeT"  value=""  onblur="getEigyoshoName('srhTxtEigyoshoCodeT', 'srhTxtEigyoshoNameT');" >
						<img class="img border" src="./images/search.png" onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCodeT','srhTxtEigyoshoNameT');">
						<input type="text" class=""  style="width: 120px" name="srhTxtEigyoshoNameT" id="srhTxtEigyoshoNameT" value="" readonly>
					</td>
				</tr>
				
				<tr id="busho" style="display: none;">
					<td class="title center w70">部署</td>
					<td class="value w600">
						<input type="text" class=""  style="width: 80px"  maxlength="4" name="srhTxtBushoCodeF" id="srhTxtBushoCodeF"  value=""  onblur="getBushoName('srhTxtBushoCodeF', 'srhTxtBushoNameF');" >
						<img class="img border" src="./images/search.png" onclick="opnDialog('srhMstBusho','srhTxtBushoCodeF','srhTxtBushoNameF');">
						<input type="text" class=""  style="width: 120px" name="srhTxtBushoNameF" id="srhTxtBushoNameF" value="" readonly>
						～
						<input type="text" class=""  style="width: 80px"  maxlength="4" name="srhTxtBushoCodeT" id="srhTxtBushoCodeT"  value=""  onblur="getBushoName('srhTxtBushoCodeT', 'srhTxtBushoNameT');" >
						<img class="img border" src="./images/search.png" onclick="opnDialog('srhMstBusho','srhTxtBushoCodeT','srhTxtBushoNameT');">
						<input type="text" class=""  style="width: 120px" name="srhTxtBushoNameT" id="srhTxtBushoNameT" value="" readonly>
					</td>
				</tr>
				
				<tr id="shain" style="display: none;">
					<td class="title center w70">社員NO</td>
					<td class="value w600">
						<input type="text" class=""  style="width: 80px"  maxlength="4" name="srhTxtShainNoF" id="srhTxtShainNoF"  value=""  onblur="getShainName('srhTxtShainNoF', 'srhTxtShainNameF');" >
						<img class="img border" src="./images/search.png" onclick="opnDialog('srhMstShain','srhTxtShainNoF','srhTxtShainNameF');">
						<input type="text" class=""  style="width: 120px" name="srhTxtShainNameF" id="srhTxtShainNameF" value="" readonly>
						～
						<input type="text" class=""  style="width: 80px"  maxlength="4" name="srhTxtShainNoT" id="srhTxtShainNoT"  value=""  onblur="getShainName('srhTxtShainNoT', 'srhTxtShainNameT');" >
						<img class="img border" src="./images/search.png" onclick="opnDialog('srhMstShain','srhTxtShainNoT','srhTxtShainNameT');">
						<input type="text" class=""  style="width: 120px" name="srhTxtShainNameT" id="srhTxtShainNameT" value="" readonly>
					</td>
				</tr>
				
				<tr id="joken" style="display: none;">
					<td class="title center w70">条件</td>
					<td class="value w600">
						<select name="srhSelJoken"  id="srhSelJoken" >
							<option value="00"></option>
							<% for (int count = 0 ; count < mstKubun0050.size() ; count++){ HashMap<String, String> record = mstKubun0050.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
						</select>
					</td>
				</tr>
				
				<tr id="order" style="display: none;">
					<td class="title center w70">出力順</td>
					<td class="value w600">
						<input type="radio"  id="srhRdoOrder"  name="srhRdoOrder" value="01">社員NO
						<input type="radio"  id="srhRdoOrder"  name="srhRdoOrder" value="02">営業所・社員NO
					</td>
				</tr>
				
				<tr id="output" style="display: none;">
					<td class="title center w70">出力形式</td>
					<td class="value w600">
						<input type="radio"  id="srhRdoOutput"  name="srhRdoOutput" value="01">PDF
						<input type="radio"  id="srhRdoOutput"  name="srhRdoOutput" value="02">CSV
					</td>
				</tr>
				
			</table>
		</div>
		
	</div>
	
	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
		<button type="button" onclick="onPdfCsvDownload()">作表 [ F12 ] </button>
	</div>
	
</main>