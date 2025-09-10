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
	String	taishoDate = (String)request.getAttribute("result");
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
							<input type="text" class=""  style="width: 80px; text-align: right;" name="srhTxtTaishoYM" id="srhTxtTaishoYM" value="<%=UtilEscape.htmlspecialchars(taishoDate) %>" maxlength="7" onblur="getTaishoYMFormat();" autofocus onfocus="this.setSelectionRange(7, 7)">
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
							<input type="text" class=""  style="width: 80px" name="srhTxtShainNO" id="srhTxtShainNO" value="<%=UtilEscape.htmlspecialchars(shainNO) %>" maxlength="4" onblur="getShainNOFormat();" 
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
										<a id="kinmuKaishi" name="kinmuKaishi"></a>
									</td>
									<td class="title center w50">
										<a >-</a>
									</td>
									<td class="value center w100">
										<a id="kinmuShuryo" name="kinmuShuryo"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w150">
										<a >実働</a>
									</th>
									<td class="value center w150" colspan="2">
										<a id="jitsudojikan" name="jitsudojikan"></a>
									</td>
									<th class="title center w100">
										<a >時間</a>
									</th>
								</tr>
								<input type="hidden" name="hidEigyoshoCode" id="hidEigyoshoCode" value="">
								<input type="hidden" name="hidBushoCode" id="hidBushoCode" value="">
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
										<a >勤務時間</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu01" name="shinseinissu01"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikan01" name="shinseijikan01"></a>
									</td>
									<td class="value right w70">
										<a id="shinseitanka01" name="shinseitanka01"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei01" name="shinseikingakugoukei01"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >休日勤務</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu04" name="shinseinissu04"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikan04" name="shinseijikan04"></a>
									</td>
									<td class="value right w70">
										<a id="shinseitanka04" name="shinseitanka04"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei04" name="shinseikingakugoukei04"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >時間外勤務</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu02" name="shinseinissu02"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikan02" name="shinseijikan02"></a>
									</td>
									<td class="value right w70">
										<a id="shinseitanka02" name="shinseitanka02"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei02" name="shinseikingakugoukei02"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >深夜勤務</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu03" name="shinseinissu03"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikan03" name="shinseijikan03"></a>
									</td>
									<td class="value right w70">
										<a id="shinseitanka03" name="shinseitanka03"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei03" name="shinseikingakugoukei03"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >有給休暇</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu05" name="shinseinissu05"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikan05" name="shinseijikan05"></a>
									</td>
									<td class="value right w70">
										<a id="shinseitanka05" name="shinseitanka05"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei05" name="shinseikingakugoukei05"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >特別有給休暇</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu11" name="shinseinissu11"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikan11" name="shinseijikan11"></a>
									</td>
									<td class="value right w70">
										<a id="shinseitanka11" name="shinseitanka11"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei11" name="shinseikingakugoukei11"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >通勤費</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu09" name="shinseinissu09"></a>
									</td>
									<td class="value right w70">
		
									</td>
									<td class="value right w70">
										<a id="shinseitanka09" name="shinseitanka09"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei09" name="shinseikingakugoukei09"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >控除</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissu07" name="shinseinissu07"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikan07" name="shinseijikan07"></a>
									</td>
									<td class="value right w70">
										<a id="shinseitanka07" name="shinseitanka07"></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukei07" name="shinseikingakugoukei07"></a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >休日</a>
									</th>
									<td class="value right w70">
										<a id="shinseinissukyujitsu" name="shinseinissukyujitsu"></a>
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
										<a id="shinseinisuugoukei" name="shinseinisuugoukei"></a>
									</td>
									<td class="value right w70">
										<a id="shinseijikangoukei" name="shinseijikangoukei"></a>
									</td>
									<td class="value right w70">
										<a ></a>
									</td>
									<td class="value right w70">
										<a id="shinseikingakugoukeigoukei" name="shinseikingakugoukeigoukei"></a>
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