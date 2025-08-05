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
				<td class="title center w100">営業所コード</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px"" name="srhDlgTxtEigyoshoCode" id="srhDlgTxtEigyoshoCode" value="" >
				</td>
			</tr>
			<tr>
				<td class="title center w100">部署コード</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px"" name="srhDlgTxtBushoCode" id="srhDlgTxtBushoCode" value="" >
				</td>
			</tr>
			<tr>
				<td class="title center w100">部署名</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px"" name="srhDlgTxtBushoName" id="srhDlgTxtBushoName" value="" >
				</td>
			</tr>
		</table>
	</div>
	<div class="searchButtonArea">
		<button type="button" onclick="getMstBushos();">検索</button>
	</div>
	<div class="searchResultArea" style="max-height: 465px">
		<table class="searchRecord">
			<thead>
				<tr>
					<th>営業所コード</th>
					<th>部署コード</th>
					<th>部署名</th>
				</tr>
			</thead>
			<tbody id = "searchBushoResult">
			</tbody>
		</table>
	</div>
</div>