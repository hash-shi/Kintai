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
				<td class="title center w100">営業所</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px"" name="srhDlgTxtEigyoshoCode" id="srhDlgTxtEigyoshoCode" value="" >
				</td>
			</tr>
			<tr>
				<td class="title center w100">営業名</td>
				<td class="value w300">
					<input type="text" class=""  style="width: 100px"" name="srhDlgTxtEigyoshoName" id="srhDlgTxtEigyoshoName" value="" >
				</td>
			</tr>
		</table>
	</div>
	<div class="searchButtonArea">
		<button type="button" onclick="getMstEigyoshos();">検索</button>
	</div>
	<div class="searchResultArea" style="max-height: 465px">
		<table class="searchRecord">
			<thead>
				<tr>
					<th>コード</th>
					<th>名称</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
				<tr>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">200</a></td>
					<td><a href="#" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();return false;">本社</a></td>
					<td><button type="button" onclick="setDialogReturnValue('200');closeDialog();setDialogReturnValueEigyoshoName();">選択</button></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>