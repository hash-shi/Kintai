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
				<td class="value w170">
					<input type="text" class="w150" maxlength="3" name="srhDlgTxtEigyoshoCode" id="srhDlgTxtEigyoshoCode" value="" >
				</td>
			</tr>
			<tr>
				<td class="title center w100">営業所名</td>
				<td class="value w170">
					<input type="text" class="w150" maxlength="20" name="srhDlgTxtEigyoshoName" id="srhDlgTxtEigyoshoName" value="" >
				</td>
			</tr>
		</table>
	</div>
	<div class="searchButtonArea">
		<button type="button" onclick="getMstEigyoshos();">検索</button>
	</div>
	<div class="searchResultArea" style="max-height: 460px">
		<table class="searchRecord" style="table-layout: fixed">
			<thead>
				<tr>
					<th>営業所コード</th>
					<th>営業所名</th>
				</tr>
			</thead>
			<tbody id="searchEigyoshoResult">
			</tbody>
		</table>
	</div>
</div>