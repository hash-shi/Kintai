<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<main id="main-content">
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
							<input type="text" class=""  style="width: 80px; text-align: right;" name="txtTaishoYM" id="txtTaishoYM" value="" maxlength="7" onblur="getTaishoYMFormat();" autofocus>
							<input type="hidden" name="txtSearchedTaishoYM" id="txtSearchedTaishoYM" value="">
						</td>
						<td class="title center w100 req">
							<a name="lnkShainSearch" id="linkShainSearch" href="#" onclick="opnDialog('srhMstShain','txtShainNO','txtShainName'); return false;">社員NO</a>
						</td>
						<td class="value w300">
							<input type="text" class=""  style="width: 80px" name="txtShainNO" id="txtShainNO" value="" maxlength="4" onblur="getShainName('txtShainNO', 'txtShainName');" >
							<input type="hidden" name="txtSearchedShainNO" id="txtSearchedShainNO" value="">
							<img class="img border" name="btnShainSearch" id="btnShainSearch" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtShainNO','txtShainName');">
							<input type="text" class=""  style="width: 120px" name="txtShainName" id="txtShainName" value="" disabled readonly>
						</td>
						<td class="value w50">
							<button type="button" onclick="onSearchKinShukkinBo();">検索</button>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div id = "nyuryokuArea" class="mainArea" style="display: none;">
		<div class="inputArea">
			<div class="box">
				<button type="button" id="btnFirstHalf" onclick="onDisplayNyuryokuArea(true);" disabled>◀前一覧</button>
				<button type="button" id="btnSecondHalf" onclick="onDisplayNyuryokuArea(false);" disabled>次一覧▶</button>
			</div>
			<div class="box shukkinboArea">
				<table class="kinShukkinBoSearchRecord">
					<thead>
						<tr>
							<th class="title center w150" colspan="3">	<a >月日</a></th>
							<th class="title center w50">				<a >予定</a></th>
							<th class="title center w100">				<a >勤怠区分</a></th>

							<th class="title center w100" colspan="2">	<a >出社</a></th>
							<th class="title center w50">				<a >-</a></th>
							<th class="title center w100" colspan="2">	<a >退社</a></th>
							<th class="title center w70">				<a >通常勤務</a></th>

							<th class="title center w320">				<a >備考</a></th>
							<th class="title center w100">				<a >申請区分１</a></th>
							<th class="title center w100" colspan="2">	<a >開始</a></th>
							<th class="title center w100" colspan="2">	<a >終了</a></th>
							<th class="title center w50">				<a >時間</a></th>
							<th class="title center w100">				<a >申請区分２</a></th>
							<tH class="title center w100" colspan="2">	<a >開始</a></th>
							<th class="title center w100" colspan="2">	<a >終了</a></th>
							<th class="title center w50">				<a >時間</a></th>
							<th class="title center w100">				<a >申請区分３</a></th>
							<th class="title center w100" colspan="2">	<a >開始</a></th>
							<th class="title center w100" colspan="2">	<a >終了</a></th>
							<th class="title center w50">				<a >時間</a></th>
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
							<th class="w50">
							</th>
							<td class="title center w100">
								<a >営業日当手当</a>
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
			<div style="text-align: right;" class="box">
				<button type="button" id="btnDelete" onclick="onDelete();">削除 [ F2 ]</button>
				<button type="button" id="btnUpdate" onclick="onUpdate();">確定 [ F9 ]</button>
			</div>
		</div>
	</div>
</main>