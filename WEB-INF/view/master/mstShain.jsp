<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>
<%@ page import="jp.co.kintai.carreservation.define.Define" %>
<%@ page import="jp.co.kintai.carreservation.information.UserInformation" %>

<%

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
					<td class="value w250">
						<input type="text" class="w80" maxlength="4" name="srhTxtShainNO" id="srhTxtShainNO"  value="" onblur="getShainName('srhTxtShainNO', '');">
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
						<input type="text" class="w80" name="txtShainNO" id="txtShainNO"  value="" readonly>
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">社員名</td>
					<td class="value w500">
						<input type="text" class="w200" name="txtShainName" id="txtShainName"  value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">パスワード</td>
					<td class="value w500">
						<input type="text" class="w200" name="txtPassword" id="txtPassword"  value="" >
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">ユーザ区分</td>
					<td class="value w500">
						<input type="hidden" id="txtUserKbnCode" name="txtUserKbnCode" value="0500">
				  		<input type="text" class="w80"  name="txtUserCode" id="txtUserCode"  value=""  onblur="getKubunNameAndSetSelect('txtUserKbnCode','txtUserCode','txtUserKbnName','selectUserKbnName');" >		
						<select class="w150" name="selectUserKbnName" id="selectUserKbnName" onfocus="getKubunNameAndSetSelect('txtUserKbnCode','txtUserCode','txtUserKbnName','selectUserKbnName');" ></select>
					</td>
				</tr>
				
				<tr>
					<td class="title center w150 req">社員区分</td>
					<td class="value w500">
						<input type="hidden" id="txtShainKbnCode" name="txtShainKbnCode" value="0010">
				  		<input type="text" class="w80"  name="txtShainCode" id="txtShainCode"  value=""  onblur="getKubunNameAndSetSelect('txtShainKbnCode','txtShainCode','txtShainKbnName','selectShainKbnName');" >
					    <select class="w150" name="selectShainKbnName" id="selectShainKbnName" onfocus="getKubunNameAndSetSelect('txtShainKbnCode','txtShainCode','txtShainKbnName','selectShainKbnName');" ></select>
				  	</td>
				</tr>
				<tr>
				  	<td class="title center w150 req">出勤簿入力区分</td>
				  	<td class="value w500">
				  		<input type="hidden" id="txtShukinboKbnCode" name="txtShukinboKbnCode" value="0011">
				  		<input type="text" class="w80"  name="txtShukinboCode" id="txtShukinboCode"  value=""  onblur="getKubunNameAndSetSelect('txtShukinboKbnCode','txtShukinboCode','txtShukinboKbnName','selectShukinboKbnName');" >
				  		<select class="w150" name="selectShukinboKbnName" id="selectShukinboKbnName" onfocus="getKubunNameAndSetSelect('txtShukinboKbnCode','txtShukinboCode','txtShukinboKbnName','selectShukinboKbnName');" ></select>
				  	</td>
				</tr>		
				<tr>
					<td class="title center w150 req">
						<a href="#" onclick="opnDialog('srhMstEigyosho','txtEigyoshoCode','txtEigyoshoName'); return false;">営業所</a>
					</td>
					<td class="value w500">
						<input type="text" class="w80"  name="txtEigyoshoCode" id="txtEigyoshoCode"  value=""  onblur="getEigyoshoName('txtEigyoshoCode', 'txtEigyoshoName');" >
						<input type="text" class="w200"  name="txtEigyoshoName" id="txtEigyoshoName" value="" readonly>
					</td>
				</tr>			
				<tr>
					<td class="title center w150 req">
						<a href="#" onclick="opnDialog('srhMstBusho','txtBushoCode','txtBushoName'); return false;">部署</a>
					</td>
					<td class="value w500">
						<input type="text" class="w80"  name="txtBushoCode" id="txtBushoCode"  value="" onblur="getBushoName('txtBushoCode','txtBushoName');">
						<input type="text" class="w200"  name="txtBushoName" id="txtBushoName"  value="" readonly>
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">有給休暇付与日数</td>
					<td class="value w500">
						<input type="text" class="w80" name="txtYukyuKyukaFuyoNissu" id="txtYukyuKyukaFuyoNissu"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">時給日給区分</td>
					<td class="value w500">
				  		<input type="hidden" id="txtJikyuNikkyuKbnCode" name="txtJikyuNikkyuKbnCode" value="0012">
				  		<input type="text" class="w80"  name="txtJikyuNikkyuCode" id="txtJikyuNikkyuCode"  value=""  onblur="getKubunNameAndSetSelect('txtJikyuNikkyuKbnCode','txtJikyuNikkyuCode','txtJikyuNikkyuKbnName','selectJikyuNikkyuKbnName');" >
						<select class="w150" name="selectJikyuNikkyuKbnName" id="selectJikyuNikkyuKbnName" onfocus="getKubunNameAndSetSelect('txtJikyuNikkyuKbnCode','txtJikyuNikkyuCode','txtJikyuNikkyuKbnName','selectJikyuNikkyuKbnName');"></select>	
					</td>
				</tr>
				<tr>
					<td class="title center w150">勤務開始時刻</td>
					<td class="value w500">
						<input type="hidden" id="txtKinmuKaishiJiKbnCode" name="txtKinmuKaishiJiKbnCode" value="0052">
						<input type="hidden" id="txtKinmuKaishiJiCode" value="" readonly>
					    <select class="w80" name="selectKinmuKaishiJiKbnName" id="selectKinmuKaishiJiKbnName" onfocus="getKubunNameAndSetSelect('txtKinmuKaishiJiKbnCode','txtKinmuKaishiJiCode','txtKinmuKaishiJiKbnName','selectKinmuKaishiJiKbnName');">
					    </select>時
					    
					    <input type="hidden" id="txtKinmuKaishiFunKbnCode" name="txtKinmuKaishiFunKbnCode" value="0053">
						<input type="hidden" id="txtKinmuKaishiFunCode" value="" readonly>
					    <select class="w80" name="selectKinmuKaishiFunKbnName" id="selectKinmuKaishiFunKbnName" onfocus="getKubunNameAndSetSelect('txtKinmuKaishiFunKbnCode','txtKinmuKaishiFunCode','txtKinmuKaishiFunKbnName','selectKinmuKaishiFunKbnName');">
					    </select>分			
					</td>
				</tr>
				<tr>
					<td class="title center w150">勤務終了時刻</td>
					<td class="value w500">
						<input type="hidden" id="txtKinmuShuryoJiKbnCode" name="txtKinmuShuryoJiKbnCode" value="0052">
						<input type="hidden" id="txtKinmuShuryoJiCode" value="" readonly>
					    <select class="w80" name="selectKinmuShuryoJiKbnName" id="selectKinmuShuryoJiKbnName" onfocus="getKubunNameAndSetSelect('txtKinmuShuryoJiKbnCode','txtKinmuShuryoJiCode','txtKinmuShuryoJiKbnName','selectKinmuShuryoJiKbnName');">
					    </select>時
					    
					    <input type="hidden" id="txtKinmuShuryoFunKbnCode" name="txtKinmuShuryoFunKbnCode" value="0053">
						<input type="hidden" id="txtKinmuShuryoFunCode" value="" readonly>
					    <select class="w80" name="selectKinmuShuryoFunKbnName" id="selectKinmuShuryoFunKbnName" onfocus="getKubunNameAndSetSelect('txtKinmuShuryoFunKbnCode','txtKinmuShuryoFunCode','txtKinmuShuryoFunKbnName','selectKinmuShuryoFunKbnName');">
					    </select>分				
					</td>
				</tr>
				<tr>
					<td class="title center w150">勤務実働時間</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtKeiyakuJitsudoJikan" id="txtKeiyakuJitsudoJikan"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">01 勤務時間 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka01" id="txtShinseiTanka01"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">02 勤務時間 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka02" id="txtShinseiTanka02"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">03 深夜勤務 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka03" id="txtShinseiTanka03"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">04 休日勤務 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka04" id="txtShinseiTanka04"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">05 有給休暇 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka05" id="txtShinseiTanka05"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">06 半日有給 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka06" id="txtShinseiTanka06"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">07 控除 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka07" id="txtShinseiTanka07"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">08 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka08" id="txtShinseiTanka08"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">09 通勤費 単価／月給</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka09" id="txtShinseiTanka09"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">10 時間外勤務 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka10" id="txtShinseiTanka10"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">11 特別有給休暇 単価</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtShinseiTanka11" id="txtShinseiTanka11"  value="" >				
					</td>
				</tr>
				<tr>
					<td class="title center w150 req">通勤費精算区分</td>
					<td class="value w500">
						<input type="hidden" id="txtTsukinHiKbnCode" name="txtTsukinHiKbnCode" value="0013">
				  		<input type="text" class="w80"  name="txtTsukinHiCode" id="txtTsukinHiCode"  value="" onblur="getKubunNameAndSetSelect('txtTsukinHiKbnCode','txtTsukinHiCode','txtTsukinHiKbnName','selectTsukinHiKbnName');" >
						<select class="w150" name="selectTsukinHiKbnName" id="selectTsukinHiKbnName" onfocus="getKubunNameAndSetSelect('txtTsukinHiKbnCode','txtTsukinHiCode','txtTsukinHiKbnName','selectTsukinHiKbnName');" ></select>			
					</td>
				</tr>
				<tr>
					<td class="title center w150">退職年月日</td>
					<td class="value w500">
						<input type="text" class="w200"  maxlength="50"  name="txtTaisyokuDate" id="txtTaisyokuDate"  value="" >				
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
				</tr>
			</table>
		</div>
	</div>

	<div class="buttonArea right" id="buttonArea" style="visibility:hidden;">
		<button type="button" class="" name="btnDelete" id="btnDelete" onclick="onDelete()">削除 [ F2 ] </button>
		<button type="button" class="" name="btnUpdate" id="btnUpdate" onclick="onUpdate()">確定 [ F9 ] </button>
	</div>
	
</main>




