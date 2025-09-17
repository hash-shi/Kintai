<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<%
	//処理区分の取得
	ArrayList<HashMap<String, String>> mstKubun0501	= (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0501");
	//営業所初期値の取得
	ArrayList<HashMap<String, String>> mstDatas			= (ArrayList<HashMap<String, String>>)request.getAttribute("mstDatas");
	HashMap<String, String> mstData	= mstDatas.get(0);
%>

<main id="main-content" class="nom">
	<div class="headerArea"　id="headerArea">
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w300">マスタリスト</td>
					<input type="hidden" name="txtEigyoshoCodeF" id="txtEigyoshoCodeF" value="<%=UtilEscape.htmlspecialchars(mstData.get("eigyoshoCodeF")) %>">
					<input type="hidden" name="txtEigyoshoCodeT" id="txtEigyoshoCodeT" value="<%=UtilEscape.htmlspecialchars(mstData.get("eigyoshoCodeT")) %>">
				</tr>
			</table>
			<table>
				<tr>
					<td class="title center w100">処理選択</td>
					<td class="value w180">
						<select name = "selShoriSentaku" class="w120"  id="selShoriSentaku" autofocus>
							<% for (int count = 0 ; count < mstKubun0501.size() ; count++){ HashMap<String, String> record = mstKubun0501.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
						</select>
						<button type="button" onclick="setShoriSentaku();">検索</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div id="mainArea"  class="mainArea" style="visibility:hidden;">
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w100">処理選択</td>
					<td class="value w170">
						<input type="text" class=""  style="width: 160px"" name="lblShoriSentaku" id="lblShoriSentaku" value="" readonly>
					</td>
				</tr>
			</table>
		</div>
		<div class="inputArea">
			<table>
				<tr id="eigyosho" class ="inputArea" style="display: none;" >
					<td class="title center w100">営業所</td>
					<td class="value w600">
						<input type="text" class=""  style="width: 80px"" name="srhTxtEigyoshoCodeF" id="srhTxtEigyoshoCodeF" maxlength="3" value=""  onblur="getEigyoshoName('srhTxtEigyoshoCodeF', 'lblEigyoshoNameF');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCodeF','lblEigyoshoNameF');">
						<input type="text" class=""  style="width: 150px"" name="lblEigyoshoNameF" id="lblEigyoshoNameF" value="" readonly>
						～
						<input type="text" class=""  style="width: 80px"" name="srhTxtEigyoshoCodeT" id="srhTxtEigyoshoCodeT" maxlength="3" value=""  onblur="getEigyoshoName('srhTxtEigyoshoCodeT', 'lblEigyoshoNameT');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCodeT','lblEigyoshoNameT');">
						<input type="text" class=""  style="width: 150px"" name="lblEigyoshoNameT" id="lblEigyoshoNameT" value="" readonly>
					</td>
				</tr>
				<tr id="busho" class ="inputArea" style="display: none;">
					<td class="title center w100">部署</td>
					<td class="value w600">
						<input type="text" class=""  style="width: 80px"" name="srhTxtBushoCodeF" id="srhTxtBushoCodeF" maxlength="4"  value=""  onblur="getBushoName('srhTxtBushoCodeF', 'lblBushoNameF');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','srhTxtBushoCodeF','lblBushoNameF');">
						<input type="text" class=""  style="width: 150px"" name="lblBushoNameF" id="lblBushoNameF" value="" readonly>
						～
						<input type="text" class=""  style="width: 80px"" name="srhTxtBushoCodeT" id="srhTxtBushoCodeT" maxlength="4"  value=""  onblur="getBushoName('srhTxtBushoCodeT', 'lblBushoNameT');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','srhTxtBushoCodeT','lblBushoNameT');">
						<input type="text" class=""  style="width: 150px"" name="lblBushoNameT" id="lblBushoNameT" value="" readonly>
					</td>
				</tr>
				<tr id="shain" class ="inputArea" style="display: none;">
					<td class="title center w100">社員NO</td>
					<td class="value w600">
						<input type="text" class=""  style="width: 80px"" name="srhTxtShainNoF" id="srhTxtShainNoF" maxlength="4"  value=""  onblur="getShainName('srhTxtShainNoF', 'lblShainNameF');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','srhTxtShainNoF','lblShainNameF');">
						<input type="text" class=""  style="width: 150px"" name="lblShainNameF" id="lblShainNameF" value="" readonly>
						～
						<input type="text" class=""  style="width: 80px"" name="srhTxtShainNoT" id="srhTxtShainNoT" maxlength="4"  value=""  onblur="getShainName('srhTxtShainNoT', 'lblShainNameT');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','srhTxtShainNoT','lblShainNameT');">
						<input type="text" class=""  style="width: 150px"" name="lblShainNameT" id="lblShainNameT" value="" readonly>
					</td>
				</tr>
				<tr id="kubun" class ="inputArea" style="display: none;">
					<td class="title center w100">区分コード</td>
					<td class="value w310">
						<input type="text" class=""  style="width: 80px"" name="srhTxtKbnCodeF" id="srhTxtKbnCodeF" maxlength="4"   value="">
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstKubun','srhTxtKbnCodeF','srhTxtKbnCodeF');">
						～
						<input type="text" class=""  style="width: 80px"" name="srhTxtKbnCodeT" id="srhTxtKbnCodeT" maxlength="4"  value="">
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstKubun','srhTxtKbnCodeT','srhTxtKbnCodeT');">
					</td>
				</tr>
				<tr id="saishuKoshin" class ="inputArea" style="display: none;">
					<td class="title center w100">最終更新日</td>
					<td class="value w310">
						<input type="text" class=""  style="width: 80px; text-align: right;"" name="srhTxtSaishuKoshinDateF" id="srhTxtSaishuKoshinDateF" maxlength="10" value="" onchange="onChangeSaishuKoshinDate('srhTxtSaishuKoshinDateF')">
						～
						<input type="text" class=""  style="width: 80px; text-align: right;"" name="srhTxtSaishuKoshinDateT" id="srhTxtSaishuKoshinDateT" maxlength="10" value="" onchange="onChangeSaishuKoshinDate('srhTxtSaishuKoshinDateT')">
						(YYYY/MM/DD)
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
		<button type="button" onclick="output()">作表[F12]</button>
	</div>
</main>