<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<main id="main-content">
	<h2>出勤簿入力</h2>
	<p>これはメインエリアです。</p>
  
	<div class="inputArea">
		<div>
			<table>
				<tr>
					<td class="title center w100">
						<a href="#" onclick="opnDialog('srhMstEigyosho','txtEigyoshoCode','txtEigyoshoName'); return false;">対象年月</a>
					</td>
					<td class="value w100">
						<input type="text" class=""  style="width: 80px"" name="txtEigyoshoCode" id="txtEigyoshoCode"  value=""  onblur="getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');" >
					</td>
					<td class="title center w100">
						<a href="#" onclick="opnDialog('srhMstShain','txtShainCode','txtShainName'); return false;">社員NO</a>
					</td>
					<td class="value w300">
						<input type="text" class=""  style="width: 80px"" name="txtShainCode" id="txtShainCode"  value=""  onblur="getShainName('txtShainCode', 'txtShainName');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','txtShainCode','txtShainName');">
						<input type="text" class=""  style="width: 120px"" name="txtShainName" id="txtShainName" value="" readonly>
					</td>
					<td class="value w50">
						<button type="button" onclick="onSearchKinShukkinBo();">検索</button>
					</td>
				</tr>
			</table>
		</div>
		<div>
			<button type="button" onclick="onDisplayNyuryokuArea(true);">◀前一覧</button>
			<button type="button" onclick="onDisplayNyuryokuArea(false);">次一覧▶</button>
		</div>
		<div>
			<table class="kinShukkinBoSearchRecord">
				<thead>
					<tr>
						<th class="title center w150" colspan="3">	<a >月日</a></th>
						<th class="title center w100">				<a >予定</a></th>
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
				</tbody>
			</table>
		</div>
		<div style="text-align: right;">
			<button type="button" onclick="onUpdate();">削除 [ F2 ]</button>
			<button type="button" onclick="onUpdate();">確定 [ F2 ]</button>
		</div>
	</div>
</main>