<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<%
	ArrayList<HashMap<String, String>> mstKubuns	= (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubuns");
%>


<main id="main-content">

	<h2>ようこそ</h2>
	<p>これはメインエリアです。</p>
  
	<div class="inputArea">
		<table>
			<tr>
				<td class="title center w100">
					<a href="#" onclick="opnDialog('srhMstEigyosho','txtEigyoshoCode','txtEigyoshoName'); return false;">営業所</a>
				</td>
				<td class="value w500">
					<input type="text" class=""  style="width: 80px"" name="txtEigyoshoCode" id="txtEigyoshoCode"  value=""  onblur="getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');" >
					<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstEigyosho','txtEigyoshoCode','txtEigyoshoName');">
					<input type="text" class=""  style="width: 120px"" name="txtEigyoshoName" id="txtEigyoshoName" value="" readonly>
				</td>
			</tr>
			<tr>
				<td class="title center w100">マスタ</td>
				<td class="value w500">
					<select name="" id="">
						<option></option>
						<% if (mstKubuns != null) {%>
							<% for (int count = 0 ; count < mstKubuns.size(); count++){ HashMap<String, String> record = mstKubuns.get(count); %>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>" ><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
						<% } %>
					</select>
				</td>
			</tr>
		</table>
	</div>

</main>