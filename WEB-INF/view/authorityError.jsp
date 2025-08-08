<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="message-box">
	<img src="./images/warning.png" alt="警告" class="icon" />
	<span>ログインセッションがタイムアウトになりました。</span>
</div>
<hr />
<div class="content">
	<p>セッションタイムアウトが発生しました。<br>再ログインしてください。</p>
	<button type="button" onclick="location.href='./index'">ログイン画面へ戻る</button>
</div>

<style>

	html, body {
		margin			: 0;
		padding			: 0;
		/*height			: 100%;*/
		font-family	: Arial, sans-serif;
	}
	
	form {
		height			: 100%;
		width				: 100%;
	}

	.message-box {
		margin						: 20px 0;
		background-color	: #BCE6BE;
		border						: 1px solid #B6E2B6;
		padding						: 20px 40px;
		align-items				: center;
		font-size					: 18px;
		text-align				: center;
	}

	.message-box .icon {
		width						: 36px;
		height					: 36px;
		margin-right		: 10px;
		vertical-align	: middle;
	}

	hr {
		width					: 100%;
		border				: none;
		border-top		: 1px solid #aaa;
	}
	
	.content {
		text-align		: center;
		margin-top		: 40px;
	}
	
	.content p {
		font-size			: 14px;
		margin-bottom	: 20px;
	}
	
	button {
		padding				: 5px 15px;
		font-size			: 14px;
		border				: 1px solid #ccc;
		background		: linear-gradient(to bottom, #ffffff, #e5e5e5);
		border-radius	: 3px;
		cursor				: pointer;
	}
	
	button:hover {
		background		: #e8e8e8;
	}
</style>