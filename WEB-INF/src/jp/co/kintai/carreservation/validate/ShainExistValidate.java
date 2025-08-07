package jp.co.kintai.carreservation.validate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

import org.apache.commons.lang3.StringUtils;

public class ShainExistValidate extends ValidateBase {

	public ShainExistValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		/**
		 * 詳細説明
		 * 
		 * 社員NOの存在チェック
		 */
		
		boolean result					= false;
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// チェック対象の社員NO
		String shainNo					= this.getParameter("txtShainNO");
		
		
		//=====================================================================
		// 処理
		//=====================================================================
		
		// チェック対象の社員情報の取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, null, null, null, null, null, null);
		
		if (0 < mstShains.size()) {
			//社員マスタの検索結果が0件でなければtrue
			result = true;
		}
		
		// 結果返却
		return result;
	}
}