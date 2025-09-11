package jp.co.kintai.carreservation.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiKakuteiNotHonshaShainValidate extends ValidateBase {

	public KintaiKakuteiNotHonshaShainValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		/**
		 * 詳細説明
		 * 
		 * 対象社員の区分チェック
		 */
		
		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		String loginUserKbn = userInformation.getUserKbn();
		

		//ログインユーザーのユーザ区分 = 本社(01)でないならエラー
		if(!loginUserKbn.equals("01")){ return false; }
		
		// 結果返却
		return true;
	}
}