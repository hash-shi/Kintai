package jp.co.kintai.carreservation.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiKakuteiHonshaValidate extends ValidateBase {

	public KintaiKakuteiHonshaValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
		//本社確定待ち有無
		int honshaCnt = 0;
				
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
			gekkyuSb		.append("hdnTxtKakuteiKbn01")	.append(String.valueOf(i));
			String gekkyu				= this.getParameter(gekkyuSb.toString());
			// 時給日給制取得
			StringBuilder nikkyuSb	= new StringBuilder();
			nikkyuSb		.append("hdnTxtKakuteiKbn02")	.append(String.valueOf(i));
			String nikkyu				= this.getParameter(nikkyuSb.toString());
			if(gekkyu.equals("03") || nikkyu.equals("03")) {
			honshaCnt += 1;
			}
		}
		
		if(honshaCnt > 0) {
			this.addValidateMessage("本社確定済みなので更新できません。");
			return false;
		}
			return true;
	}
}