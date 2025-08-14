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
				<td class="value w170">
					<select name = "rdoShoriSentaku"  id="rdoShoriSentaku">
				      <% for (int count = 0 ; count < shoriSentaku.size() ; count++){ HashMap<String, String> record = shoriSentaku.get(count);%>
					   <option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
				      <% } %>
					</select>
					<button type="button" onclick="toggleVisibility();">検索</button>
				</td>
			</tr>
		</table>
	  </div>
	</div>
	
	<div id="displayShoriArea"  class="mainArea" style="display: none;">
	  <div class="inputArea">
		<table>
		    <tr>
				<td class="title center w100">
					<a>処理選択</a>
				</td>
				<td class="value w170">
					<input type="text" class=""  style="width: 120px"" name="txtShoriSentaku" id="txtShoriSentaku" value="" readonly>
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
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtSrhEigyoshoCodeF" id="txtSrhEigyoshoCodeF" maxlength="3" value=""  onblur="getEigyoshoName('txtSrhEigyoshoCodeF', 'txtSrhEigyoshoNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtSrhEigyoshoCodeF','txtSrhEigyoshoNameF');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhEigyoshoNameF" id="txtSrhEigyoshoNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhEigyoshoCodeT" id="txtSrhEigyoshoCodeT" maxlength="3" value=""  onblur="getEigyoshoName('txtSrhEigyoshoCodeT', 'txtSrhEigyoshoNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtSrhEigyoshoCodeT','txtSrhEigyoshoNameT');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhEigyoshoNameT" id="txtSrhEigyoshoNameT" value="" readonly>
				</td>
			</tr>
			<tr id="displayKbnArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>区分コード</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtSrhKbnCodeF" id="txtSrhKbnCodeF" maxlength="4"   value="">
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhKbnCodeT" id="txtSrhKbnCodeT" maxlength="4"  value="" >
				</td>
			</tr>
			<tr id="displayShainArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>社員NO</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtSrhShainNOF" id="txtSrhShainNOF" maxlength="4"  value=""  onblur="getShainName('txtSrhShainNOF', 'txtSrhShainNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtSrhShainNOF','txtSrhShainNameF');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhShainNameF" id="txtSrhShainNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhShainNOT" id="txtSrhShainNOT" maxlength="4"  value=""  onblur="getShainName('txtSrhShainNOT', 'txtSrhShainNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtSrhShainNOT','txtSrhShainNameT');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhShainNameT" id="txtSrhShainNameT" value="" readonly>
				</td>
			</tr>
			<tr id="displayBushoArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>部署</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtSrhBushoCodeF" id="txtSrhBushoCodeF" maxlength="4"  value=""  onblur="getBushoName('txtSrhBushoCodeF', 'txtSrhBushoNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','txtSrhBushoCodeF','txtSrhBushoNameF');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhBushoNameF" id="txtSrhBushoNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhBushoCodeT" id="txtSrhBushoCodeT" maxlength="4"  value=""  onblur="getBushoName('txtSrhBushoCodeT', 'txtSrhBushoNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','txtSrhBushoCodeT','txtSrhBushoNameT');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhBushoNameT" id="txtSrhBushoNameT" value="" readonly>
				</td>
			</tr>
			<tr>
				<td class="title center w100">
					<a>最終更新日</a>
				</td>
				<td class="value w500">
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtSrhSaishuKoshinDateF" id="txtSrhSaishuKoshinDateF" maxlength="10" value="">
					<a>～</a>
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtSrhSaishuKoshinDateT" id="txtSrhSaishuKoshinDateT" maxlength="10" value="">
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
	<div class="buttonArea right" id="displayBottonArea" style="display: none;">
			<button type="button" onclick="output()">作表[F12]</button>
	</div>
	
</main>