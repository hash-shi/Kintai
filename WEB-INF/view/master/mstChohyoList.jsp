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
	    <input type="hidden" name="numSrhShorisentaku" id = "numSrhShorisentaku" value="">
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
					<input type="text" class=""  style="width: 80px"" name="txtSrhEigyoshoCodeF" id="txtSrhEigyoshoCodeF"  value=""  onblur="getEigyoshoName('txtSrhEigyoshoCodeF', 'txtSrhEigyoshoNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtSrhEigyoshoCodeF','txtSrhEigyoshoNameF');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhEigyoshoNameF" id="txtSrhEigyoshoNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhEigyoshoCodeT" id="txtSrhEigyoshoCodeT"  value=""  onblur="getEigyoshoName('txtSrhEigyoshoCodeT', 'txtSrhEigyoshoNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtSrhEigyoshoCodeT','txtSrhEigyoshoNameT');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhEigyoshoNameT" id="txtSrhEigyoshoNameT" value="" readonly>
				</td>
			</tr>
			<tr id="displayKbnArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>区分コード</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtSrhKbnCodeF" id="txtSrhKbnCodeF"  value="">
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhKbnCodeT" id="txtSrhKbnCodeT"  value="" >
				</td>
			</tr>
			<tr id="displayShainArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>社員NO</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtSrhShainNOF" id="txtSrhShainNOF"  value=""  onblur="getShainName('txtSrhShainNOF', 'txtSrhShainNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtSrhShainNOF','txtSrhShainNameF');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhShainNameF" id="txtSrhShainNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhShainNOT" id="txtSrhShainNOT"  value=""  onblur="getShainName('txtSrhShainNOT', 'txtSrhShainNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtSrhShainNOT','txtSrhShainNameT');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhShainNameT" id="txtSrhShainNameT" value="" readonly>
				</td>
			</tr>
			<tr id="displayBushoArea" class ="inputArea" style="display: none;">
				<td class="title center w100">
					<a>部署</a>
				</td>
				<td class="value w1000">
					<input type="text" class=""  style="width: 80px"" name="txtSrhBushoCodeF" id="txtSrhBushoCodeF"  value=""  onblur="getBushoName('txtSrhBushoCodeF', 'txtSrhBushoNameF');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','txtSrhBushoCodeF','txtSrhBushoNameF');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhBushoNameF" id="txtSrhBushoNameF" value="" readonly>
					<a>～</a>
					<input type="text" class=""  style="width: 80px"" name="txtSrhBushoCodeT" id="txtSrhBushoCodeT"  value=""  onblur="getBushoName('txtSrhBushoCodeT', 'txtSrhBushoNameT');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstBusho','txtSrhBushoCodeT','txtSrhBushoNameT');">
					<input type="text" class=""  style="width: 120px"" name="txtSrhBushoNameT" id="txtSrhBushoNameT" value="" readonly>
				</td>
			</tr>
			<tr>
				<td class="title center w100">
					<a>最終更新日</a>
				</td>
				<td class="value w500">
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtSrhSaishuKoshinDateF" id="txtSrhSaishuKoshinDateF" value="">
					<a>～</a>
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtSrhSaishuKoshinDateT" id="txtSrhSaishuKoshinDateT" value="">
					<a>(YYYY/MM/DD)</a>
				</td>
			</tr>
		</table>
	</div>
	<div id="displayBottonArea" style="text-align: right; display: none;">
			<button type="button" onclick="output();">作表[F12]</button>
	</div>
	
</main>