<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<div class="dialogBody">
		<div class="searchConditionArea">
			<table>
				<tr>
					<td class="title center w100">社員NO</td>
					<td class="value w170">
						<input type="text" class=""  style="width: 150px" name="srhDlgTxtShainNO" id="srhDlgTxtShainNO" value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w100">社員名</td>
					<td class="value w170">
						<input type="text" class=""  style="width: 150px" name="srhDlgTxtShainName" id="srhDlgTxtShainName" value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w100">営業所コード</td>
					<td class="value w170">
						<input type="text" class=""  style="width: 150px" name="srhDlgTxtEigyoshoCode" id="srhDlgTxtEigyoshoCode" value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w100">社員区分</td>
					<td class="value w170">
						<input type="text" class=""  style="width: 150px" name="srhDlgTxtShainKbn" id="srhDlgTxtShainKbn" value="" >
					</td>
				</tr>
			</table>
		</div>
		<div class="searchButtonArea">
			<button type="button" onclick="getMstShains();">検索</button>
		</div>
	<div class="searchResultArea" style="max-height: 465px">
		<table class="searchRecord" style="table-layout: fixed">
			<thead>
				<tr>
					<th>社員NO</th>
					<th>社員名</th>
					<th>営業所コード</th>
					<th>社員区分</th>
				</tr>
			</thead>
			<tbody id="searchShainResult">
			</tbody>

		</table>
	</div>
</div>