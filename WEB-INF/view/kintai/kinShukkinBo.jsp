<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

	String shainNO = "";
	String shainName = "";
	String userKbn = "";
	boolean shainNOReadonlyFlg = false;
	
	// ユーザー情報の取得
	UserInformation userInformation		= (UserInformation)request.getSession().getAttribute(Define.SESSION_ID);

	if (userInformation != null) {
		shainNO = userInformation.getShainNO();
		shainName = userInformation.getShainName();
		userKbn = userInformation.getUserKbn();
	}
	//ユーザー区分が"04"(個人)の場合、社員NOは入力不可にする
	if("04".equals(userKbn)){
		shainNOReadonlyFlg = true;
	}
	
	//対象年月初期値の取得
	String	taishoYM = (String)request.getAttribute("result");
%>

<main id="main-content" class="nom">
    <div class="headerArea" id="headerArea">
		<div class="inputArea">
			<table>
			    <tr>
					<td class="title center w300">出勤簿入力</td>
				</tr>
			</table>
			<div class="box">
				<table>
					<tr>
						<td class="title center w100 req">
							<a >対象年月</a>
						</td>
						<td class="value w100">
							<input type="text" class=""  style="width: 80px; text-align: right;" name="srhTxtTaishoYM" id="srhTxtTaishoYM" value="<%=UtilEscape.htmlspecialchars(taishoYM) %>" maxlength="7" onblur="getTaishoYMFormat();" autofocus onfocus="this.setSelectionRange(7, 7)">
							<input type="hidden" name="txtTaishoYM" id="txtTaishoYM" value="<%=UtilEscape.htmlspecialchars(taishoYM) %>">
						</td>
						<td class="title center w100 req">
							<a href="#" 
								<% if(shainNOReadonlyFlg == true){ %>
									readonly  tabindex="-1"
									onclick=""
								<% } %>
								<% if(shainNOReadonlyFlg == false){ %>
									onclick="opnDialog('srhMstShain','srhTxtShainNO','lblShainName'); return false;"
								<% } %>
							>社員NO</a>
						</td>
						<td class="value w300">
							<input type="text" class=""  style="width: 80px" name="srhTxtShainNO" id="srhTxtShainNO" value="<%=UtilEscape.htmlspecialchars(shainNO) %>" maxlength="4" onblur="getShainNOFormat();" 
								<% if(shainNOReadonlyFlg == true){ %>
								readonly  tabindex="-1"
								<% } %>
							>
							<input type="hidden" name="txtShainNO" id="txtShainNO" value="">
							<img class="img border" src="./images/search.png"  
								<% if(shainNOReadonlyFlg == false){ %>
									onclick="opnDialog('srhMstShain','srhTxtShainNO','lblShainName');"
								<% } %>
							>
							<input type="text" class=""  style="width: 120px" name="lblShainName" id="lblShainName" value="<%=UtilEscape.htmlspecialchars(shainName) %>" readonly tabindex="-1">
						</td>
						<td class="value w50">
							<button type="button" onclick="onSearchKinShukkinBo();">検索</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div id = "nyuryokuArea" class="mainArea" style="visibility: hidden;">
		<div class="inputArea">
			<div class="box">
				<button type="button" id="btnFirstHalf" onclick="onDisplayNyuryokuArea(true);" disabled>◀前一覧</button>
				<button type="button" id="btnSecondHalf" onclick="onDisplayNyuryokuArea(false);" disabled>次一覧▶</button>
			</div>
			<div class="box shukkinboArea">
				<table class="kinShukkinBoSearchRecord">
					<thead>
						<tr>
							<th class="title center" colspan="3">	<a class="kinShukkinBoText">月日</a></th>
							<th class="title center">				<a class="kinShukkinBoText">予定</a></th>
							<th class="title center">				<a class="kinShukkinBoText">勤怠区分</a></th>

							<th class="title center" colspan="2">	<a class="kinShukkinBoText">出社</a></th>
							<th class="title center">				<a class="kinShukkinBoText">-</a></th>
							<th class="title center" colspan="2">	<a class="kinShukkinBoText">退社</a></th>
							<th class="title center">				<a class="kinShukkinBoText">通常勤務</a></th>

							<th class="title center0">				<a class="kinShukkinBoText">備考</a></th>
							<th class="title center">				<a class="kinShukkinBoText">申請区分１</a></th>
							<th class="title center" colspan="2">	<a class="kinShukkinBoText">開始</a></th>
							<th class="title center" colspan="2">	<a class="kinShukkinBoText">終了</a></th>
							<th class="title center">				<a class="kinShukkinBoText">時間</a></th>
							<th class="title center">				<a class="kinShukkinBoText">申請区分２</a></th>
							<th class="title center" colspan="2">	<a class="kinShukkinBoText">開始</a></th>
							<th class="title center" colspan="2">	<a class="kinShukkinBoText">終了</a></th>
							<th class="title center">				<a class="kinShukkinBoText">時間</a></th>
							<th class="title center">				<a class="kinShukkinBoText">申請区分３</a></th>
							<th class="title center" colspan="2">	<a class="kinShukkinBoText">開始</a></th>
							<th class="title center" colspan="2">	<a class="kinShukkinBoText">終了</a></th>
							<th class="title center">				<a class="kinShukkinBoText">時間</a></th>
						</tr>
					</thead>
					<tbody id="kihonNyuryokuArea">
					</tbody>
				</table>
			</div>
			<div class="box">
				<table>
					<tbody id="tokubetsuNyuryokuArea">
						<tr>
							<td class="title center w100">
								<a >特別作業金額</a>
							</td>
							<td class="value w100">
								<input type="text" class="" maxlength="7" style="width: 80px; text-align: right;" name="txtShinseiKingaku01" id="txtShinseiKingaku01" value="" onblur="setShinseiKingaku01();">
							</td>
							<th class="w10">
							</th>
							<td class="title center w100">
								<a >その他</a>
							</td>
							<td class="value w100">
								<input type="text" class="" maxlength="7" style="width: 80px; text-align: right;" name="txtShinseiKingaku02" id="txtShinseiKingaku02" value="" onblur="setShinseiKingaku02();">
							</td>
							<input type="hidden" name="hdnKihonSaishuKoshinDate" id="hdnKihonSaishuKoshinDate" value="">
							<input type="hidden" name="hdnKihonSaishuKoshinJikan" id="hdnKihonSaishuKoshinJikan" value="">
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
		<button type="button" id="btnDelete" onclick="onDelete();">削除 [ F2 ]</button>
		<button type="button" id="btnUpdate" onclick="onUpdate();">確定 [ F9 ]</button>
	</div>
</main>