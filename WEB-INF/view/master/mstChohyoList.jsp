<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<%
//処理区分の取得
ArrayList<HashMap<String, String>> shoriSentaku			= (ArrayList<HashMap<String, String>>)request.getAttribute("shoriSentaku");
%>

<main id="main-content">
	<h2>マスタリスト</h2>
  
	<div class="inputArea">
		<table>
			<tr>
				<td class="title center w100">
					<a>処理選択</a>
				</td>
				<td class="value w170">
					<select name = "rdoShoriSentaku" >
				      <% for (int count = 0 ; count < shoriSentaku.size() ; count++){ HashMap<String, String> record = shoriSentaku.get(count);%>
					   <option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
				      <% } %>
					</select>
					<button type="button" onclick="toggleVisibility();">検索</button>
				</td>
			</tr>
		</table>
	</div>
	
	<p></p>
	
	<div id="displayShoriArea" class ="inputArea" style="display: none;">
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
	<div>
	    <p></p>
	</div>
	<div id="displayOutputArea"  class ="inputArea" style="display: none;">
		<table>
		    <tr id="displayEigyoshoArea" class ="inputArea" style="display: none;" >
				<td class="title center w100">
					<a>営業所</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtFromEigyoshoCode" id="txtFromEigyoshoCode"  value=""  onblur="getEigyoshoName('txtFromEigyoshoCode', 'txtFromEigyoshoName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtFromEigyoshoCode','txtFromEigyoshoName');">
					<input type="text" class=""  style="width: 120px"" name="txtFromEigyoshoName" id="txtFromEigyoshoName" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtToEigyoshoCode" id="txtToEigyoshoCode"  value=""  onblur="getEigyoshoName('txtToEigyoshoCode', 'txtToEigyoshoName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtToEigyoshoCode','txtToEigyoshoName');">
					<input type="text" class=""  style="width: 120px"" name="txtToEigyoshoName" id="txtToEigyoshoName" value="" readonly>
				</td>
			</tr>
			<tr id="displayKbnArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>区分コード</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtFromKbnCode" id="txtFromKbnCode"  value=""  onblur="getKbnName('txtFromKbnCode', 'txtFromKbnName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstKubun','txtFromKbnCode','txtFromKbnName');">
					<input type="text" class=""  style="width: 120px"" name="txtFromKbnName" id="txtFromKbnName" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtToKbnCode" id="txtToKbnCode"  value=""  onblur="getKbnName('txtToKbnCode', 'txtToKbnName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstKubun','txtToKbnCode','txtToKbnName');">
					<input type="text" class=""  style="width: 120px"" name="txtToKbnName" id="txtToKbnName" value="" readonly>
				</td>
			</tr>
			<tr id="displayShainArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>社員NO</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtFromShainNO" id="txtFromShainNO"  value=""  onblur="getShainName('txtFromShainNO', 'txtFromShainName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtFromShainNO','txtFromShainName');">
					<input type="text" class=""  style="width: 120px"" name="txtFromShainName" id="txtFromShainName" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtToShainNO" id="txtToShainNO"  value=""  onblur="getShainName('txtToShainNO', 'txtToShainName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtToShainNO','txtToShainName');">
					<input type="text" class=""  style="width: 120px"" name="txtToShainName" id="txtToShainName" value="" readonly>
				</td>
			</tr>
			<tr id="displayBushoArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>部署</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtFromBushoCode" id="txtFromBushoCode"  value=""  onblur="getBushoName('txtFromBushoCode', 'txtFromBushoName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','txtFromBushoCode','txtFromBushoName');">
					<input type="text" class=""  style="width: 120px"" name="txtFromBushoName" id="txtFromBushoName" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtToBushoCode" id="txtToBushoCode"  value=""  onblur="getBushoName('txtToBushoCode', 'txtToBushoName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','txtToBushoCode','txtToBushoName');">
					<input type="text" class=""  style="width: 120px"" name="txtToBushoName" id="txtToBushoName" value="" readonly>
				</td>
			</tr>
			<tr>
				<td class="title center w100">
					<a>最終更新日</a>
				</td>
				<td class="value w500">
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtFromSaishuKoshinDate" id="txtFromSaishuKoshinDate" value="">
					<a>～</a>
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtToSaishuKoshinDate" id="txtToSaishuKoshinDate" value="">
					<a>(YYYY/MM/DD)</a>
				</td>
			</tr>
		</table>
	</div>
	<div id="displayBottonArea" style="text-align: right; display: none;">
			<button type="button" onclick="opnDialog('srhMstKbn','txtFromKbnCode','txtFromKbnName');">作表[F12]</button>
	</div>
	
</main>