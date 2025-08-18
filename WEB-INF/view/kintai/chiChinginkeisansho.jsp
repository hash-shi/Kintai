<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

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
							<input type="text" class=""  style="width: 80px; text-align: right;" name="txtTaishoYM" id="txtTaishoYM" value="" maxlength="7" onblur="getTaishoYMFormat();" autofocus>
							<input type="hidden" name="txtSearchedTaishoYM" id="txtSearchedTaishoYM" value="">
						</td>
						<td class="title center w100 req">
							<a name="lnkShainSearch" id="linkShainSearch" href="#" onclick="opnDialog('srhMstShain','txtShainNO','txtShainName'); return false;">社員NO</a>
						</td>
						<td class="value w300">
							<input type="text" class=""  style="width: 80px" name="txtShainNO" id="txtShainNO" value="" maxlength="4" onblur="getShainNOFormat();" >
							<input type="hidden" name="hdnWkShainNO" id="hdnWkShainNO" value="">
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
	<div id = "nyuryokuArea" class="mainArea" style="visibility: hidden;">
		<div class="inputArea">
			<div class="box">
				<button type="button" id="btnFirstHalf" onclick="onDisplayNyuryokuArea(true);" disabled>◀前一覧</button>
				<button type="button" id="btnSecondHalf" onclick="onDisplayNyuryokuArea(false);" disabled>次一覧▶</button>
			</div>
			<div style="display: flex;">
				<div>
					<div class="box shukkinboArea" style="margin-right:10px;">
						<table class="kinShukkinBoSearchRecord">
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
										<a >99:99</a>
									</td>
									<td class="title center w50">
										<a >-</a>
									</td>
									<td class="value center w100">
										<a >99:99</a>
									</td>
								</tr>
								<tr>
									<th class="title center w150">
										<a >実働</a>
									</th>
									<td class="value center w150" colspan="2">
										<a >99:99</a>
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
										<a >勤務時間</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >休日勤務</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >時間外勤務</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >深夜勤務</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >有給休暇</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >特別有給休暇</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >通勤費</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
		
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >控除</a>
									</th>
									<td class="value right w70">
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
									</td>
								</tr>
								<tr>
									<th class="title center w120">
										<a >休日</a>
									</th>
									<td class="value right w70">
										<a >99</a>
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
										<a >99</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >99,999</a>
									</td>
									<td class="value right w70">
										<a >999,999</a>
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
										<textarea class="w340 h100"></textarea>
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