<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

	<main id="main-content">
	
		<div>
			<h2>ダイアログ表示エリア</h2>
			<table>
				<tr>
					<td>タイトル</td>
					<td>
						<input type="text" id="txtUserID" name="txtUserID"  value=""  />
						&nbsp;
						<button type="button" onclick="openDialogSrhMstUser();">検索</button>
						&nbsp;
						<div id="lblUserNm" name="lblUserNm" ></div>
					</td>
				</tr>
			</table>
		</div>
		
		<div>
			<h2>CSV出力エリア</h2>
			<table>
				<tr>
					<td>タイトル</td>
					<td>
						<input type="text" id="inp_text01" name="inp_text01"  value=""  />
						&nbsp;
						<button type="button" onclick="downloadCsvFile();">出力</button>
					</td>
				</tr>
			</table>
		</div>
		
		<div>
			<h2>PDF出力エリア</h2>
			<table>
				<tr>
					<td>タイトル</td>
					<td>
						<input type="text" id="inp_text02" name="inp_text02"  value=""  />
						&nbsp;
						<button type="button" onclick="downloadPdfFile();">出力</button>
					</td>
				</tr>
			</table>
		</div>
		
	</main>