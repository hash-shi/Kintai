<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<div class="dialogBody">
	<div class="searchConditionArea">
		<ul>
			<li>
				<div class="label">ユーザID</div>
				<div class="value">
					<input type="text" name="txtSrhUserID" id="txtSrhUserID">
				</div>
			</li>
		</ul>
	</div>
	<div class="searchButtonArea">
		<button type="button" onclick="getSearchUser();">検索</button>
	</div>
	<div class="searchResultArea">
<!--		<ul>-->
<!--			<li>-->
<!--				<div class="name">名前</div>-->
<!--				<div class="name"></div>-->
<!--			</li>-->
<!--		</ul>-->
		<ul id="searchCustomerResult">
			<li><div class="name">一郎</div><div class="button"><button type="button" onclick="setResultUserID('0001');closeDialog();getUserNm();">選択</button></div></li>
			<li><div class="name">次郎</div><div class="button"><button type="button" onclick="setResultUserID('0002');closeDialog();getUserNm();">選択</button></div></li>
			<li><div class="name">ｻﾌﾞﾛｳ</div><div class="button"><button type="button" onclick="setResultUserID('0003');closeDialog();getUserNm();">選択</button></div></li>
			<li><div class="name">太郎</div><div class="button"><button type="button" onclick="setResultUserID('0004');closeDialog();getUserNm();">選択</button></div></li>
		</ul>
	</div>
</div>