<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

    String loginDate = "";

	// ユーザー情報の取得
	UserInformation userInformation		= (UserInformation)request.getSession().getAttribute(Define.SESSION_ID);

	if (userInformation != null) {
		loginDate					= userInformation.getLoginDate();
	}

%>

<main id="main-content">
	<h2>マスタリスト</h2>
  
	<div class="inputArea">
		<table>
			<tr>
				<td class="title center w100">
					<a>処理選択</a>
				<td class="value w170">
					<select name = "rdoShoriSentaku" id = "rdoShoriSentaku">
					 <option value="01">営業所マスタ</option>
					 <option value="02">部署マスタ</option>
					 <option value="03">社員マスタ</option>
					 <option value="04">区分名称マスタ</option>
					</select>
					<button type="button" onclick="toggleVisibility();">検索</button>
				</td>
			</tr>
		</table>
	</div>
	
	<p></p>
	
	<div  id = "outputArea" class ="outputArea" style="display: none;">
		<table>
			<tr>
				<td class="title center w100">
					<a>処理選択</a>
				</td>
				<td class="value w170">
					<input type="text" class=""  style="width: 120px"" name="txtShoriSentaku" id="txtShoriSentaku" value="" readonly>
				</td>
			</tr>
				<td class="title center w100">
				<p></p>
			</td>
			<tr>
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
			<tr>
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
			<tr>
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
			<tr>
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
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtFromSaishuKoshinDate" id="txtFromSaishuKoshinDate" value="<%=UtilEscape.htmlspecialchars(loginDate) %>">
					<a>～</a>
					<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtToSaishuKoshinDate" id="txtToSaishuKoshinDate" value="<%=UtilEscape.htmlspecialchars(loginDate) %>">
					<a>(YYYY/MM/DD)</a>
				</td>
			</tr>
			<tr>
				<td class="title center w100">
					<button type="button" onclick="opnDialog('srhMstKbn','txtFromKbnCode','txtFromKbnName');">作表[F12]</button>
				</td>
			</tr>
		</table>
	</div>
	
</main>