<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

%>


	<main id="main-content" class="nom">
		<div class="headerArea" 　id="headerArea">
			<div class="inputArea">
				<table>
					<tr>
						<td class="title center w300">管理マスタメンテ</td>
					</tr>
				</table>
				<table>
					<tr>
						<td class="title center w150">管理コード</td>
						<td class="value w250">
						<input type="text" name="srhKanriCode" id="srhKanriCode" value="01" class="w80" readonly>
							<button type="button" onclick="getMstKanri();">検索</button></td>
					</tr>
				</table>
			</div>
		</div>

		<div id="mainArea" class="mainArea" style="visibility:hidden;">
			<div class="inputArea">
				<table>
					<tr>
						<td class="title center w150 req">管理コード</td>
						<td class="value w500">
						<input type="text" name="txtKanriCode" id="txtKanriCode" value="01" class="w80" readonly>
					</tr>

					<tr>
						<td class="title center w150 req">年度確定ステータス</td>
						<td class="value w500">
						<input type="text" name="txtNendoKakuteiStatus" id="txtNendoKakuteiStatus" value="" class="w80">
							<select name="rdoStatusSentaku" class="w150">
								<option value=""></option>
								<option value="Mikakutei">未確定</option>
								<option value="Kakutei">確定</option>

						</select>
					</tr>

					<tr>
						<td class="title center w150 req">現在処理年月度</td>
						<td class="value w500">
						<input type="text" class="" style="width: 80px; text-align: right;" " name="txtGenzaishoriNengetsudo" id="txtGenzaishoriNengetsudo" maxlength="10" value="" class="w80">(YYYY/MM/DD)</td>
					</tr>

					<tr>
						<td class="title center w150 req">勤怠期首月度</td>
						<td class="value w500">
						<input type="text" name="txtKintaiKishuGetsudo" id="txtKintaiKishuGetsudo" value=""  class="w80" style="text-align: right;">
					</tr>

					<tr>
						<td class="title center w150 req">勤怠月度締日</td>
						<td class="value w500">
						<input type="text" name="txtKintaiGetsudoShimebi" id="txtKintaiGetsudoShimebi" value=""  class="w80" style="text-align: right;">
					</tr>

					<tr>
						<td class="title center w150 req">勤本作業時間</td>
						<td class="value w500"><input type="text" name="txtKintaiKihonSagyoJikan" id="txtKintaiKihonSagyoJikan" value=""  class="w80" style="text-align: center;">
					</tr>
				</table>

			</div>
		</div>
		<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
			<button type="button" onclick="output()">確定[F9]</button>
		</div>
	</main>

