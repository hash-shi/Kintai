<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<%
//処理区分の取得
ArrayList<HashMap<String, String>> shoriSentaku			= (ArrayList<HashMap<String, String>>)request.getAttribute("shoriSentaku");
//営業所初期値の取得
ArrayList<HashMap<String, String>> eigyosho			= (ArrayList<HashMap<String, String>>)request.getAttribute("eigyosho");
HashMap<String, String> eigyoshoCnt = eigyosho.get(0);
%>

<main id="main-content" class="nom">
    <div class="headerArea"　id="headerArea">
	  <div class="inputArea">
		<table>
			<tr>
				<td class="title center w300">マスタリスト</td>
			</tr>
		</table>
		<table>
			<tr>
				<td class="title center w100">
					<a>処理選択</a>
				</td>
				<td class="value w180">
					<select name = "rdoShoriSentaku" class="w120"  id="rdoShoriSentaku"  autofocus>
					<% for (int count = 0 ; count < shoriSentaku.size() ; count++){ HashMap<String, String> record = shoriSentaku.get(count);%>
					   <option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
					<% } %>
					</select>
					<button type="button" onclick="shoriAreaDisplay();">検索</button>
				</td>
			</tr>
		</table>
	  </div>
	</div>
	
	<div id="displayShoriArea"  class="mainArea" style="visibility:hidden;">
	  <div class="inputArea">
		<table>
		    <tr>
				<td class="title center w100">
					<a>処理選択</a>
				</td>
				<td class="value w170">
					<input type="text" class=""  style="width: 160px"" name="txtShoriSentaku" id="txtShoriSentaku" value="" readonly>
				</td>
			</tr>
		</table>
	  </div>
	  <div class="inputArea">
	    <table>
		    <tr id="displayEigyoshoArea" class ="inputArea" style="display: none;" >
				<td class="title center w100">
					<a>営業所</a>
				</td>
				<td class="value w600">
					<input type="text" class=""  style="width: 80px"" name="srhTxtEigyoshoCodeF" id="srhTxtEigyoshoCodeF" maxlength="3" value=""  onblur="getEigyoshoName('srhTxtEigyoshoCodeF', 'srhTxtEigyoshoNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCodeF','srhTxtEigyoshoNameF');">
					<input type="text" class=""  style="width: 150px"" name="srhTxtEigyoshoNameF" id="srhTxtEigyoshoNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="srhTxtEigyoshoCodeT" id="srhTxtEigyoshoCodeT" maxlength="3" value=""  onblur="getEigyoshoName('srhTxtEigyoshoCodeT', 'srhTxtEigyoshoNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','srhTxtEigyoshoCodeT','srhTxtEigyoshoNameT');">
					<input type="text" class=""  style="width: 150px"" name="srhTxtEigyoshoNameT" id="srhTxtEigyoshoNameT" value="" readonly>
				</td>
			</tr>
			<tr id="displayKbnArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>区分コード</a>
				</td>
				<td class="value w310">
					<input type="text" class=""  style="width: 80px"" name="srhTxtKbnCodeF" id="srhTxtKbnCodeF" maxlength="4"   value="">
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="srhTxtKbnCodeT" id="srhTxtKbnCodeT" maxlength="4"  value="" >
				</td>
			</tr>
			<tr id="displayShainArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>社員NO</a>
				</td>
				<td class="value w600">
					<input type="text" class=""  style="width: 80px"" name="srhTxtShainNOF" id="srhTxtShainNOF" maxlength="4"  value=""  onblur="getShainName('srhTxtShainNOF', 'srhTxtShainNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','srhTxtShainNOF','srhTxtShainNameF');">
					<input type="text" class=""  style="width: 150px"" name="srhTxtShainNameF" id="srhTxtShainNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="srhTxtShainNOT" id="srhTxtShainNOT" maxlength="4"  value=""  onblur="getShainName('srhTxtShainNOT', 'srhTxtShainNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','srhTxtShainNOT','srhTxtShainNameT');">
					<input type="text" class=""  style="width: 150px"" name="srhTxtShainNameT" id="srhTxtShainNameT" value="" readonly>
				</td>
			</tr>
			<tr id="displayBushoArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>部署</a>
				</td>
				<td class="value w600">
					<input type="text" class=""  style="width: 80px"" name="srhTxtBushoCodeF" id="srhTxtBushoCodeF" maxlength="4"  value=""  onblur="getBushoName('srhTxtBushoCodeF', 'srhTxtBushoNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','srhTxtBushoCodeF','srhTxtBushoNameF');">
					<input type="text" class=""  style="width: 150px"" name="srhTxtBushoNameF" id="srhTxtBushoNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="srhTxtBushoCodeT" id="srhTxtBushoCodeT" maxlength="4"  value=""  onblur="getBushoName('srhTxtBushoCodeT', 'srhTxtBushoNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','srhTxtBushoCodeT','srhTxtBushoNameT');">
					<input type="text" class=""  style="width: 150px"" name="srhTxtBushoNameT" id="srhTxtBushoNameT" value="" readonly>
				</td>
			</tr>
			<tr>
				<td class="title center w100">
					<a>最終更新日</a>
				</td>
				<td class="value w310">
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="srhTxtSaishuKoshinDateF" id="srhTxtSaishuKoshinDateF" maxlength="10" value="">
					<a>～</a>
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="srhTxtSaishuKoshinDateT" id="srhTxtSaishuKoshinDateT" maxlength="10" value="">
					<a>(YYYY/MM/DD)</a>
				</td>
			</tr>
		</table>
	  </div>
	</div>
	<div>
	    <input type="hidden" name="hidSrhEigyoshoCodeF" id="hidSrhEigyoshoCodeF" value="<%=UtilEscape.htmlspecialchars(eigyoshoCnt.get("Saisho")) %>">
		<input type="hidden" name="hidSrhEigyoshoCodeT" id="hidSrhEigyoshoCodeT" value="<%=UtilEscape.htmlspecialchars(eigyoshoCnt.get("Saidai")) %>">
	</div>
	<div class="buttonArea right" id="displayBottonArea" style="visibility:hidden;">
			<button type="button" onclick="output()">作表[F12]</button>
	</div>
	
</main>