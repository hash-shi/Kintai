<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<% ArrayList<HashMap<String, String>> mstKubun0505 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0505");%>

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
						<td class="title center w100">管理コード</td>
						<td class="value w150">
						<input type="text" class="w80" maxlength="2" name="srhKanriCode" id="srhKanriCode" value="01" readonly>
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
						<input type="text" name="txtKanriCode" id="txtKanriCode" value="" class="w80" maxlength="2">
					</tr>

					<tr>
						<td class="title center w150 req">年度確定ステータス</td>
						<td class="value w500">
						<input type="text" name="txtNendoKakuteiStatus" id="txtNendoKakuteiStatus" value="" class="w80" maxlength="1" onblur="setNendoKakuteiStatus();">
							<select name="selNendoKakuteiStatus" id="selNendoKakuteiStatus" class="w80" onchange="setNendoKakuteiStatusbox();">
								<option value=""> </option>
								<option value="0">未確定</option>
								<option value="1">確定</option>
							</select>
						</td>
					</tr>

					<tr>
						<td class="title center w150 req">現在処理年月度</td>
						<td class="value w500">
						<input type="text" class="" style="width: 80px; text-align: right;" " name="txtGenzaishoriNengetsudo" id="txtGenzaishoriNengetsudo" maxlength="7" value="" class="w80">(YYYY/MM)</td>
					</tr>

					<tr>
						<td class="title center w150 req">勤怠期首月度</td>
						<td class="value w500">
						<input type="text" name="txtKintaiKishuGetsudo" id="txtKintaiKishuGetsudo" value=""  class="w80" maxlength="2" style="text-align: right;">
					</tr>

					<tr>
						<td class="title center w150 req">勤怠月度締日</td>
						<td class="value w500">
						<input type="text" name="txtKintaiGetsudoShimebi" id="txtKintaiGetsudoShimebi" value=""  class="w80" maxlength="2" style="text-align: right;">
					</tr>

					<tr>
						<td class="title center w150 req">勤怠基本作業時間</td>
						<td class="value w500"><input type="text" name="txtKintaiKihonSagyoJikan" id="txtKintaiKihonSagyoJikan" value=""  class="w80" maxlength="5" style="text-align: right;">
					</tr>
				</table>

			</div>
			
			<div class="inputArea lastEdit">
				<table>
					<tr>
						<td class="title center w100 rep">最終更新社員</td>
						<td class="label w200" id="lblSaishuKoshinShainName"></td>
						<td class="title center w100 rep">最終更新日時</td>
						<td class="label w80 center" id="lblSaishuKoshinDate"></td>
						<td class="label w80 center" id="lblSaishuKoshinJikan"></td>
						<input type="hidden" class="" name="hdnSaishuKoshinShainNO" id="hdnSaishuKoshinShainNO"  value="" >
						<input type="hidden" class="" name="hdnSaishuKoshinShainName" id="hdnSaishuKoshinShainName"  value="" >
						<input type="hidden" class="" name="hdnSaishuKoshinDate" id="hdnSaishuKoshinDate"  value="" >
						<input type="hidden" class="" name="hdnSaishuKoshinJikan" id="hdnSaishuKoshinJikan"  value="" >
						<input type="hidden" class="" name="hdnIsNew" id="hdnIsNew"  value="" >
					</tr>
				</table>
			</div>
		</div>
	
		<div id="buttonArea"  class="buttonArea right"  style="visibility: hidden;">
			<button type="button" class="" name="btnUpdate" id="btnUpdate" onclick="onUpdate()">確定 [ F9 ]</button>
		</div>
	</div>
</main>

