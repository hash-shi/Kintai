<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

//処理区分の取得
ArrayList<HashMap<String, String>> mstKubun0500 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0500");
ArrayList<HashMap<String, String>> mstKubun0010 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0010");
ArrayList<HashMap<String, String>> mstKubun0011 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0011");
ArrayList<HashMap<String, String>> mstKubun0012 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0012");
ArrayList<HashMap<String, String>> mstKubun0052 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0052");
ArrayList<HashMap<String, String>> mstKubun0053 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0053");
ArrayList<HashMap<String, String>> mstKubun0013 = (ArrayList<HashMap<String, String>>)request.getAttribute("mstKubun0013");

%>

<main id="main-content" class="nom">

    <div class="headerArea" id="headerArea">
		<div class="inputArea">
			<table>
			    <tr>
					<td class="title center w300">社員マスタメンテ</td>
				</tr>
			</table>
			<table>
				<tr>
					<td class="title center w80">
						<a href="#" onclick="opnDialog('srhMstShain','srhTxtShainNO'); return false;">社員NO</a>
					</td>
					<td class="value w165">
						<input type="text" class="w80" maxlength="4" name="srhTxtShainNO" id="srhTxtShainNO"  value="" onblur="getShainName('srhTxtShainNO', '');">
						<img class="img border" src="./images/search.png"  onclick="opnDialog('srhMstShain','srhTxtShainNO');">
						<button type="button" onclick="getMstShain();">検索</button>
					</td>
				</tr>
			</table>
		</div>
	</div>
	
	<div class="mainArea" id="mainArea" style="visibility:hidden;">
	
		<div class="inputArea">
			<table>
				<tr>
					<td class="title center w150 req">社員NO</td>
					<td class="value w500">
						<input type="text" class="w80" maxlength="4" name="txtShainNO" id="txtShainNO"  value="">
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">社員名</td>
					<td class="value w500">
						<input type="text" class="w200" maxlength="20" name="txtShainName" id="txtShainName"  value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">パスワード</td>
					<td class="value w500">
						<input type="text" class="w200" maxlength="50" name="txtPassword" id="txtPassword"  value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">ユーザ区分</td>
					<td class="value w500">
				  		<input type="text" class="w80"  maxlength="2" name="txtUserKbn" id="txtUserKbn"  value="" onblur="setSelectFromInput('txtUserKbn','selectUserKbnName');" >		
						<select class="w150" name="selectUserKbnName" id="selectUserKbnName" onchange="setInputFromSelect('selectUserKbnName','txtUserKbn');">
						<option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0500.size() ; count++){ HashMap<String, String> record = mstKubun0500.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
						</select>
					</td>
				</tr>
				
				<tr>
					<td class="title center w150 req">社員区分</td>
					<td class="value w500">
				  		<input type="text" class="w80"  maxlength="2" name="txtShainKbn" id="txtShainKbn"  value=""  onblur="setSelectFromInput('txtShainKbn','selectShainKbnName'); updateActiveSwitch()" >
					    <select class="w150" name="selectShainKbnName" id="selectShainKbnName" onchange="setInputFromSelect('selectShainKbnName','txtShainKbn'); updateActiveSwitch()">
					    <option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0010.size() ; count++){ HashMap<String, String> record = mstKubun0010.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
					    </select>
				  	</td>
				</tr>
				<tr>
				  	<td class="title center w150 req">出勤簿入力区分</td>
				  	<td class="value w500">
				  		<input type="text" class="w80"  maxlength="2" name="txtShukinboKbn" id="txtShukinboKbn"  value=""  onblur="setSelectFromInput('txtShukinboKbn','selectShukinboKbnName'); updateActiveSwitch();" >
				  		<select class="w150" name="selectShukinboKbnName" id="selectShukinboKbnName" onchange="setInputFromSelect('selectShukinboKbnName','txtShukinboKbn'); updateActiveSwitch();" >
				  		<option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0011.size() ; count++){ HashMap<String, String> record = mstKubun0011.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
					    </select>
				  	</td>
				</tr>		
				<tr>
					<td class="title center w150 req">
						<a href="#" onclick="opnDialog('srhMstEigyosho','txtEigyoshoCode','txtEigyoshoName'); return false;">営業所</a>
					</td>
					<td class="value w500">
						<input type="text" class="w80"  maxlength="3" name="txtEigyoshoCode" id="txtEigyoshoCode"  value=""  onblur="getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');" >
						<input type="text" class="w200"  name="txtEigyoshoName" id="txtEigyoshoName" value="" readonly>
					</td>
				</tr>			
				<tr>
					<td class="title center w150 req">
						<a href="#" onclick="opnDialog('srhMstBusho','txtBushoCode','txtBushoName'); return false;">部署</a>
					</td>
					<td class="value w500">
						<input type="text" class="w80"  maxlength="4" name="txtBushoCode" id="txtBushoCode"  value="" onblur="getBushoName('txtBushoCode','txtBushoName');">
						<input type="text" class="w200"  name="txtBushoName" id="txtBushoName"  value="" readonly>
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">有給休暇付与日数</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="5" name="txtYukyuKyukaFuyoNissu" id="txtYukyuKyukaFuyoNissu"  value="0.0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">時給日給区分</td>
					<td class="value w500">
				  		<input type="text" class="w80" maxlength="2" name="txtJikyuNikkyuKbn" id="txtJikyuNikkyuKbn"  value=""  onblur="setSelectFromInput('txtJikyuNikkyuKbn','selectJikyuNikkyuKbnName');" >
						<select class="w150" name="selectJikyuNikkyuKbnName" id="selectJikyuNikkyuKbnName" onchange="setInputFromSelect('selectJikyuNikkyuKbnName','txtJikyuNikkyuKbn');" >
						<option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0012.size() ; count++){ HashMap<String, String> record = mstKubun0012.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
						</select>	
					</td>
				</tr>
				<tr>
					<td class="title center w150">勤務開始時刻</td>
					<td class="value w500">
						<input type="hidden" id="hdnKinmuKaishiJi" value="0052">
						<input type="hidden" class="w50" name="txtKinmuKaishiJiKbn" id="txtKinmuKaishiJiKbn" value="" readonly onchange="getKubunName('hdnKinmuKaishiJi', 'txtKinmuKaishiJiKbn', 'txtKinmuKaishiJiKbnName');">
						<input type="hidden" class="w50" name="txtKinmuKaishiJiKbnName" id="txtKinmuKaishiJiKbnName" value="" readonly>
					    <select class="w80" name="selectKinmuKaishiJiKbnName" id="selectKinmuKaishiJiKbnName" onchange="setInputFromSelect('selectKinmuKaishiJiKbnName','txtKinmuKaishiJiKbn');">
					    <option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0052.size() ; count++){ HashMap<String, String> record = mstKubun0052.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
					    </select>時
					    
					    <input type="hidden" id="hdnKinmuKaishiFun" value="0053">
						<input type="hidden" class="w50" name="txtKinmuKaishiFunKbn" id="txtKinmuKaishiFunKbn" value="" readonly onchange="getKubunName('hdnKinmuKaishiFun', 'txtKinmuKaishiFunKbn', 'txtKinmuKaishiFunKbnName');">
						<input type="hidden" class="w50" name="txtKinmuKaishiFunKbnName" id="txtKinmuKaishiFunKbnName" value="" readonly>
					    <select class="w80" name="selectKinmuKaishiFunKbnName" id="selectKinmuKaishiFunKbnName" onchange="setInputFromSelect('selectKinmuKaishiFunKbnName','txtKinmuKaishiFunKbn');">
					    <option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0053.size() ; count++){ HashMap<String, String> record = mstKubun0053.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
					    </select>分			
					</td>
				</tr>
				<tr>
					<td class="title center w150">勤務終了時刻</td>
					<td class="value w500">
						<input type="hidden" id="hdnKinmuShuryoJi" value="0052">
						<input type="hidden" class="w50" name="txtKinmuShuryoJiKbn" id="txtKinmuShuryoJiKbn" value="" readonly onchange="getKubunName('hdnKinmuShuryoJi', 'txtKinmuShuryoJiKbn', 'txtKinmuShuryoJiKbnName');">
						<input type="hidden" class="w50" name="txtKinmuShuryoJiKbnName" id="txtKinmuShuryoJiKbnName" value="" readonly>
					    <select class="w80" name="selectKinmuShuryoJiKbnName" id="selectKinmuShuryoJiKbnName" onchange="setInputFromSelect('selectKinmuShuryoJiKbnName','txtKinmuShuryoJiKbn');">
					    <option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0052.size() ; count++){ HashMap<String, String> record = mstKubun0052.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
					    </select>時
					    
					    <input type="hidden" id="hdnKinmuShuryoFun" value="0053">
						<input type="hidden" class="w50" name="txtKinmuShuryoFunKbn" id="txtKinmuShuryoFunKbn" value="" readonly onchange="getKubunName('hdnKinmuShuryoFun', 'txtKinmuShuryoFunKbn', 'txtKinmuShuryoFunKbnName');">
						<input type="hidden" class="w50" name="txtKinmuShuryoFunKbnName" id="txtKinmuShuryoFunKbnName" value="" readonly>
					    <select class="w80" name="selectKinmuShuryoFunKbnName" id="selectKinmuShuryoFunKbnName" onchange="setInputFromSelect('selectKinmuShuryoFunKbnName','txtKinmuShuryoFunKbn');">
					    <option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0053.size() ; count++){ HashMap<String, String> record = mstKubun0053.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
					    </select>分				
					</td>
				</tr>
				<tr>
					<td class="title center w150">勤務実働時間</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtKeiyakuJitsudoJikan" id="txtKeiyakuJitsudoJikan"  value="0.00" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">01 勤務時間 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka01" id="txtShinseiTanka01"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">02 時間外勤務 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka02" id="txtShinseiTanka02"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">03 深夜勤務 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka03" id="txtShinseiTanka03"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">04 休日勤務 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka04" id="txtShinseiTanka04"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">05 有給休暇 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka05" id="txtShinseiTanka05"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">06 半日有給 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka06" id="txtShinseiTanka06"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">07 控除 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka07" id="txtShinseiTanka07"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">08 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka08" id="txtShinseiTanka08"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">09 通勤費 単価／月給</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka09" id="txtShinseiTanka09"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">10 時間外勤務 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka10" id="txtShinseiTanka10"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">11 特別有給休暇 単価</td>
					<td class="value w500">
						<input type="text" class="w80 right"  maxlength="6"  name="txtShinseiTanka11" id="txtShinseiTanka11"  value="0" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">通勤費精算区分</td>
					<td class="value w500">
				  		<input type="text" class="w80" maxlength="2" name="txtTsukinHiKbn" id="txtTsukinHiKbn"  value="" onblur="setSelectFromInput('txtTsukinHiKbn','selectTsukinHiKbnName');" >
						<select class="w150" name="selectTsukinHiKbnName" id="selectTsukinHiKbnName" onchange="setInputFromSelect('selectTsukinHiKbnName','txtTsukinHiKbn');" >
						<option value=""> </option>
							<% for (int count = 0 ; count < mstKubun0013.size() ; count++){ HashMap<String, String> record = mstKubun0013.get(count);%>
								<option value="<%=UtilEscape.htmlspecialchars(record.get("Code")) %>"><%=UtilEscape.htmlspecialchars(record.get("KbnName")) %></option>
							<% } %>
					    </select>			
					</td>
				</tr>
				<tr>
					<td class="title center w150">退職年月日</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="10"  name="txtTaisyokuDate" id="txtTaisyokuDate"  value="" >				
					</td>
				</tr>
			</table>
			
				<button type="button" onclick="addShoriKanoEigyosho();">追加</button>
				
			<table>
				<tbody id="ShoriKanoEigyoshoResult">

				</tbody>
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
					<input type="hidden" class="" name="hdnShainNO" id="hdnShainNO"  value="" >
				</tr>
			</table>
		</div>
	</div>

	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
		<button type="button" class="" name="btnDelete" id="btnDelete" onclick="onDelete()">削除 [ F2 ] </button>
		<button type="button" class="" name="btnUpdate" id="btnUpdate" onclick="onUpdate()">確定 [ F9 ] </button>
	</div>
	
</main>




