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
				<td class="title center w100">区分コード</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px" name="srhDlgTxtKbnCode" id="srhDlgTxtKbnCode" value="" >
				</td>
			</tr>
			<tr>
				<td class="title center w100">コード</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px" name="srhDlgTxtCode" id="srhDlgTxtCode" value="">
				</td>
			</tr>
			<tr>
				<td class="title center w100">名称</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px" name="srhDlgTxtKbnName" id="srhDlgTxtKbnName" value="">
				</td>
			</tr>
		</table>
	</div>
	<div class="searchButtonArea">
		<button type="button" onclick="getMstKubuns();">検索</button>
	</div>
	<div class="searchResultArea" style="max-height: 465px">
		<table class="searchRecord">
			<thead>
				<tr>
					<th>区分コード</th>
					<th>コード</th>
					<th>名称</th>
				</tr>
			</thead>
			<tbody id="searchKubunResult">
			</tbody>
		</table>
	</div>
</div>