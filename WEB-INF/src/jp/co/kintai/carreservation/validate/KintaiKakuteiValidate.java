package jp.co.kintai.carreservation.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiKakuteiValidate extends ValidateBase {

	public  KintaiKakuteiValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// 明細件数
		String count				= req.getParameter(this.params.get("count").toString());
		int cnt						= Integer.parseInt(count);
		
		//=====================================================================
		// 初期値宣言
		//=====================================================================
		//選択有無
		int checkCnt = 0;
		//営業所確定待ち有無
		int eigyoshoCnt = 0;
		
		//=====================================================================
		// 選択チェック
		//=====================================================================
		// チェックボックスの値を取得
		for(int i = 0;i < cnt;i++){
			// 選択状態取得
			StringBuilder checkSb	= new StringBuilder();
			checkSb		.append("cbxKakutei")	.append(String.valueOf(i));
			String check				= this.getParameter(checkSb.toString());
			if(check.equals("01")) {
				checkCnt += 1;
			}
		}
		
		if(checkCnt <= 0) {
			this.addValidateMessage("更新対象が選択されていません。");
			return false;
		}

		
		//=====================================================================
		// 営業所確定待ち有無チェック
		//=====================================================================
		//明細件数分ループ
		for(int i = 0;i < cnt;i++){
			// 選択状態取得
			StringBuilder checkSb	= new StringBuilder();
			checkSb		.append("cbxKakutei")	.append(String.valueOf(i));
			String check				= this.getParameter(checkSb.toString());
			if(!check.equals("01")) {
			//選択されていない場合、その行の入力チェックをしない
				continue;
			}
			// 月給制取得
			StringBuilder gekkyuSb	= new StringBuilder();
			gekkyuSb		.append("hdnTxtKakuteiKbnName01")	.append(String.valueOf(i));
			String gekkyu				= this.getParameter(gekkyuSb.toString());
			// 時給日給制取得
			StringBuilder nikkyuSb	= new StringBuilder();
			nikkyuSb		.append("hdnTxtKakuteiKbnName02")	.append(String.valueOf(i));
			String nikkyu				= this.getParameter(nikkyuSb.toString());
			if(gekkyu.equals("営業所確定待ち") || nikkyu.equals("営業所確定待ち")) {
				eigyoshoCnt += 1;
			}
		}
		
		if(eigyoshoCnt > 0) {
			this.addValidateMessage("営業所確定待ちがある場合、確定解除できません。");
			return false;
		}
		return true;
	}

}