<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>
<%@ page import="java.util.HashMap" %>

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
	HashMap<String, Object> result = (HashMap<String, Object>)request.getAttribute("result");
	String	taishoDate = String.valueOf(result.get("taishoYM"));
	
	//集計項目の項目名の取得
	String kbnRyaku01 = String.valueOf(result.get("kintaiShinseiKbnRyaku01"));
	String kbnRyaku02 = String.valueOf(result.get("kintaiShinseiKbnRyaku02"));
	String kbnRyaku03 = String.valueOf(result.get("kintaiShinseiKbnRyaku03"));
	String kbnRyaku04 = String.valueOf(result.get("kintaiShinseiKbnRyaku04"));
	String kbnRyaku05 = String.valueOf(result.get("kintaiShinseiKbnRyaku05"));
	String kbnRyaku07 = String.valueOf(result.get("kintaiShinseiKbnRyaku07"));
	String kbnRyaku09 = String.valueOf(result.get("kintaiShinseiKbnRyaku09"));
	String kbnRyaku11 = String.valueOf(result.get("kintaiShinseiKbnRyaku11"));
%>

<main id="main-content" class="nom">
    <div class="headerArea" id="headerArea">
		<div class="inputArea">
			<table>
			    <tr>
					<td class="title center w300">賃金計算書入力</td>
				</tr>
			</table>
			<div class="box">
				<table>
					<tr>
						<td class="title center w100 req">
							<a >対象年月</a>
						</td>
						<td class="value w100">
							<input type="text" class=""  style="width: 80px; text-align: right;" name="srhTxtTaishoYM" id="srhTxtTaishoYM" value="<%=UtilEscape.htmlspecialchars(taishoDate) %>" maxlength="7" onblur="onChangeYM('srhTxtTaishoYM');" autofocus onfocus="this.setSelectionRange(7, 7)">
							<input type="hidden" name="txtTaishoYM" id="txtTaishoYM" value="<%=UtilEscape.htmlspecialchars(taishoDate) %>">
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
							<input type="text" class=""  style="width: 80px" name="srhTxtShainNO" id="srhTxtShainNO" value="<%=UtilEscape.htmlspecialchars(shainNO) %>" maxlength="4" onblur="onSearchShainName();" 
								<% if(shainNOReadonlyFlg == true){ %>
								readonly  tabindex="-1"
								<% } %>
							>
							<input type="hidden" name="txtShainNO" id="txtShainNO" value="<%=UtilEscape.htmlspecialchars(shainNO) %>">
							<img class="img border" src="./images/search.png"  
								<% if(shainNOReadonlyFlg == false){ %>
									onclick="opnDialog('srhMstShain','srhTxtShainNO','lblShainName');"
								<% } %>
							>
							<input type="text" class=""  style="width: 120px" name="lblShainName" id="lblShainName" value="<%=UtilEscape.htmlspecialchars(shainName) %>" readonly tabindex="-1">
						</td>
						<td class="value w50">
							<button type="button" onclick="onSearchChiChinginkeisansho();">検索</button>
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
			<div style="display: flex;">
				<div>
					<div class="box chinginkeisanshoArea" style="margin-right:10px;">
						<table class="chiChinginkeisanshoSearchRecord">
							<thead>
								<tr>
									<th class="title center" colspan="3">	<a >月日</a></th>
		
									<th class="title center" colspan="2">	<a >出社</a></th>
									<th class="title center">				<a >-</a></th>
									<th class="title center" colspan="2">	<a >退社</a></th>
									<th class="title center">				<a >通常勤務</a></th>
		
									<th class="title center">				<a >申請区分１</a></th>
									<th class="title center">				<a >時間</a></th>
									<th class="title center">				<a >申請区分２</a></th>
									<th class="title center">				<a >時間</a></th>
									<th class="title center">				<a >申請区分３</a></th>
									<th class="title center">				<a >時間</a></th>
								</tr>
							</thead>
							<tbody id="kihonNyuryokuArea">
							</tbody>
						</table>
					</div>
				</div>
				<div>
					<div class="box">
						<table>
							<tbody id="tokubetsuNyuryokuArea">
								<tr>
									<th class="title center w150">
										<a >所定(契約)勤務時間</a>
									</th>
									<td class="value center w100">
										<a id="lblKinmuKaishi" name="lblKinmuKaishi"></a>
									</td>
									<td class="title center w50">
										<a >-</a>
									</td>
									<td class="value center w100">
										<a id="lblKinmuShuryo" name="lblKinmuShuryo"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w150">
										<a >実働</a>
									</th>
									<td class="value center w150" colspan="2">
										<a id="lblJitsudojikan" name="lblJitsudojikan"></a>
									</td>
									<th class="title center w100">
										<a >時間</a>
									</th>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="box">
						<table>
							<tbody id="shukeiArea">
								<tr>
									<th class="title center w400" colspan="5">
										<a >集計</a>
									</th>
								</tr>
								<tr>
									<th class="title center w120">
										<a >項目</a>
									</th>
									<th class="title center w70">
										<a >日数</a>
									</th>
									<th class="title center w70">
										<a >時間</a>
									</th>
									<th class="title center w70">
										<a >単価</a>
									</th>
									<th class="title center w70">
										<a >金額</a>
									</th>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku01) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu01" name="lblShinseinissu01"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikan01" name="lblShinseijikan01"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka01" name="lblShinseitanka01"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei01" name="lblShinseikingakugoukei01"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku04) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu04" name="lblShinseinissu04"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikan04" name="lblShinseijikan04"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka04" name="lblShinseitanka04"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei04" name="lblShinseikingakugoukei04"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku02) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu02" name="lblShinseinissu02"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikan02" name="lblShinseijikan02"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka02" name="lblShinseitanka02"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei02" name="lblShinseikingakugoukei02"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku03) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu03" name="lblShinseinissu03"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikan03" name="lblShinseijikan03"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka03" name="lblShinseitanka03"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei03" name="lblShinseikingakugoukei03"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku05) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu05" name="lblShinseinissu05"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikan05" name="lblShinseijikan05"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka05" name="lblShinseitanka05"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei05" name="lblShinseikingakugoukei05"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku11) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu11" name="lblShinseinissu11"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikan11" name="lblShinseijikan11"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka11" name="lblShinseitanka11"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei11" name="lblShinseikingakugoukei11"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku09) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu09" name="lblShinseinissu09"></a>
									</td>
									<td class="value right w70">
		
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka09" name="lblShinseitanka09"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei09" name="lblShinseikingakugoukei09"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a ><%=UtilEscape.htmlspecialchars(kbnRyaku07) %></a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissu07" name="lblShinseinissu07"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikan07" name="lblShinseijikan07"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseitanka07" name="lblShinseitanka07"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukei07" name="lblShinseikingakugoukei07"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >休日</a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinissukyujitsu" name="lblShinseinissukyujitsu"></a>
									</td>
									<td class="value right w70">
		
									</td>
									<td class="value right w70">
		
									</td>
									<td class="value right w70">
		
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >計</a>
									</th>
									<td class="value right w70">
										<a id="lblShinseinisuugoukei" name="lblShinseinisuugoukei"></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseijikangoukei" name="lblShinseijikangoukei"></a>
									</td>
									<td class="value right w70">
										<a ></a>
									</td>
									<td class="value right w70">
										<a id="lblShinseikingakugoukeigoukei" name="lblShinseikingakugoukeigoukei"></a>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="box">
						<table>
							<tbody id="tokkijikoArea">
								<tr>
									<th class="title center w50 h100" style="writing-mode: vertical-rl;">
										<a >特記事項</a>
									</th>
									<td class="title center w350">
										<textarea class="w340 h100" id="txtTokkijiko" name="txtTokkijiko"></textarea>
									</td>
									<input type="hidden" name="txtKihonSaishuKoshinDate" id="txtKihonSaishuKoshinDate" value="">
									<input type="hidden" name="txtKihonSaishuKoshinJikan" id="txtKihonSaishuKoshinJikan" value="">
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
		<button type="button" id="btnDelete" onclick="onDelete();">削除 [ F2 ]</button>
		<button type="button" id="btnRecalc" onclick="onRecalc();">再表示 [ F8 ]</button>
		<button type="button" id="btnUpdate" onclick="onUpdate();">確定 [ F9 ]</button>
	</div>
</main>