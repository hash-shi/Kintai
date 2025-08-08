<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<main id="main-content">
	<h2>出勤簿入力</h2>
	<div class="inputArea">
		<div>
			<table>
				<tr>
					<td class="title center w100">
						<a >対象年月</a>
					</td>
					<td class="value w100">
						<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtTaishoYM" id="txtTaishoYM" value="">
						<input type="hidden" name="hidgenzaishorinengetsudo" id="hidgenzaishorinengetsudo" value="">
					</td>
					<td class="title center w100">
						<a href="#" onclick="opnDialog('srhMstShain','txtShainNO','txtShainName'); return false;">社員NO</a>
					</td>
					<td class="value w300">
						<input type="text" class=""  style="width: 80px"" name="txtShainNO" id="txtShainNO" value=""  onblur="getShainName('txtShainNO', 'txtShainName');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtShainNO','txtShainName');">
						<input type="text" class=""  style="width: 120px"" name="txtShainName" id="txtShainName" value="" readonly>
					</td>
					<td class="value w50">
						<button type="button" onclick="onSearchKinShukkinBo();">検索</button>
					</td>
				</tr>
			</table>
		</div>
		<div id = "nyuryokuArea" style="display: none;">
			<div>
				<button type="button" id="btnFirstHalf" onclick="onDisplayNyuryokuArea(true);" disabled>◀前一覧</button>
				<button type="button" id="btnSecondHalf" onclick="onDisplayNyuryokuArea(false);" disabled>次一覧▶</button>
			</div>
			<div>
				<table class="kinShukkinBoSearchRecord">
					<thead>
						<tr>
							<th class="title center w150" colspan="3">	<a >月日</a></th>
							<th class="title center w50">				<a >予定</a></th>
							<th class="title center w100">				<a >勤怠区分</a></th>
							<th class="title center w100">				<a >備考</a></th>
							<th class="w50"></th>
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
			<div>
				<table>
					<tbody id="tokubetsuNyuryokuArea">
						<tr>
							<td class="title center w100">
								<a >特別作業金額</a>
							</td>
							<td class="value w100">
								<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtShinseiKingaku01" id="txtShinseiKingaku01" value="" onblur="setShinseiKingaku01();">
							</td>
							<th class="w50">
							</th>
							<td class="title center w100">
								<a >営業日当手当</a>
							</td>
							<td class="value w100">
								<input type="text" class=""  style="width: 80px; text-align: right;"" name="txtShinseiKingaku02" id="txtShinseiKingaku02" value="" onblur="setShinseiKingaku02();">
							</td>
								</tr>
					</tbody>
				</table>
			</div>
			<div style="text-align: right;">
				<button type="button" id="btnDelete" onclick="onDelete();">削除 [ F2 ]</button>
				<button type="button" id="btnUpdate" onclick="onUpdate();">確定 [ F2 ]</button>
			</div>
		</div>
	</div>
</main>