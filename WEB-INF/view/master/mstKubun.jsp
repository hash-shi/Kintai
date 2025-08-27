<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<main id="main-content" class="nom">

	<div class="headerArea" id="headerArea">
		 <div class="inputArea">
			<table>
				<tr>
					<td class="title center w300">区分名称マスタメンテ</td>
				</tr>
			</table>
			<table>
				<tr>
					<td class="title center w100">
						<a href="#" onclick="opnDialog('srhMstKubun','srhTxtKbnCode,srhTxtCode',''); return false;">区分コード</a>
					</td>
					<td class="value w90">
						<input type="text" class="w50" maxlength="4" name="srhTxtKbnCode" id="srhTxtKbnCode" value="" onblur="getKbnName('srhTxtKbnCode', 'srhTxtKbnCode','');"  autofocus>
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstKubun','srhTxtKbnCode,srhTxtCode','');">
					</td>
					<td class="title center w100">
						<a href="#" onclick="opnDialog('srhMstKubun','srhTxtKbnCode,srhTxtCode',''); return false;">コード</a>
					</td>
					<td class="value w140">
						<input type="text" class="w50" maxlength="2" name="srhTxtCode" id="srhTxtCode" value="" onblur="getKbnName('srhTxtKbnCode', 'srhTxtCode','');" >
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstKubun','srhTxtKbnCode,srhTxtCode','');">
					<button type="button" onclick="getmstKubun()">検索</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div id = "mainArea" class="mainArea" style="visibility: hidden;">
	
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w120 req">区分コード</td>
					<td class="value w250">
						<input type="text" class="w50" maxlength="4" name="txtKbnCode" id="txtKbnCode" value="" >
						<input type="hidden" class="" name="hdnKbnCode" id="hdnKbnCode"  value="" >
					</td>
					<td class="title center w100 req">コード</td>
					<td class="value w170">
						<input type="text" class="w50" maxlength="2" name="txtCode" id="txtCode" value="" >
						<input type="hidden" class="" name="hdnCode" id="hdnCode"  value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w120 req">区分名称</td>
					<td class="value w700" colspan="3">
						<input type="text" class="w500" maxlength="40" name="txtKbnName" id="txtKbnName" value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w120">区分略称</td>
					<td class="value w700" colspan="3">
						<input type="text" class="w150" maxlength="10" name="txtKbnRyaku" id="txtKbnRyaku" value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w120">グループコード1</td>
					<td class="value w700" colspan="3">
						<input type="text" class="w50" maxlength="4" name="txtGroupCode1" id="txtGroupCode1" value="">
					</td>
				</tr>
				<tr>
					<td class="title center w120">グループコード2</td>
					<td class="value w700" colspan="3">
						<input type="text" class="w50" maxlength="4" name="txtGroupCode2" id="txtGroupCode2" value="">
					</td>
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
			<button type="button" class="" name="btnDelete" id="btnDelete" onclick="onDelete()">削除 [ F2 ]</button>
			<button type="button" class="" name="btnUpdate" id="btnUpdate" onclick="onUpdate()">確定 [ F9 ]</button>
	</div>
</main>
