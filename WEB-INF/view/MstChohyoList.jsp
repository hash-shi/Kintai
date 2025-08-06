<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="jp.co.tjs_net.java.framework.common.UtilEscape" %>

<main id="main-content">
	<h2>マスタリスト</h2>
  
	<div class="inputArea">
		<table>
			<tr>
				<td class="title center w100">
					<a>処理選択</a>
				<td class="value w170">
					<select name = "rdoShoriSentaku" id = "rdoShoriSentaku">
					 <option value="01">営業所マスタ</option>
					 <option value="02">部署マスタ</option>
					 <option value="03">社員マスタ</option>
					 <option value="04">区分名称マスタ</option>
					</select>
					<button type="button" onclick="toggleVisibility();">検索</button>
				</td>
			</tr>
		</table>
	</div>
	
	<p></p>
	
	<div class ="inputArea">
		<table>
			<tbody id="displayShoriArea">
	        </tbody>
		</table>
	</div>
	<div>
	    <p></p>
	</div>
	<div class ="inputArea">
		<table>
			<tbody id="displayOutputArea">
	        </tbody>
			<tbody id="displayDateArea">
	        </tbody>
		</table>
	</div>
	<div style="text-align: right;">
			<dbody id="displayBottonArea">
	        </dbody>
	</div>
	
</main>