<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<%

//処理区分の取得
ArrayList<HashMap<String, String>> mstKubun			= (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun");

%>

<main id="main-content" class="nom">

	<div class="headerArea"　id="headerArea">
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w300">データ出力</td>
				</tr>
			</table>
			<table>
				<tr>
					<td class="title center w70">処理選択</td>
					<td class="value w210">
						<select name="selShoriSentaku"  id="selShoriSentaku" >
					      <% for (int count = 0 ; count < mstKubun.size() ; count++){ HashMap<String, String> record = mstKubun.get(count);%>
						   <option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
					      <% } %>
						</select>
						<button type="button" onclick="setShoriSentaku()">検索</button>
					</td>
				</tr>
			</table>
		</div>
	</div>

	<div class="mainArea" id="mainArea" style="display: none;">
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
				<tr>
					<td class="title center w70 req">対象年月</td>
					<td class="value">
						<input type="text" class="w60 right"  name="srhTxtTaishoNengetsuF" id="srhTxtTaishoNengetsuF" value="">
						-
						<input type="text" class="w60 right"  name="srhTxtTaishoNengetsuT" id="srhTxtTaishoNengetsuT" value="">
						(YYYY/MM)
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div class="buttonArea right" id="buttonArea" style="display: none;">
		<button type="button" onclick="onCsvDownload()">作表[F12]</button>
	</div>
	
</main>