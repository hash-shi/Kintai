package jp.co.kintai.carreservation.validate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class ChiChinginkeisanshoNotRequiredShainValidate extends ValidateBase {

	public ChiChinginkeisanshoNotRequiredShainValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// チェック対象の社員NO
		String shainNo	= req.getParameter(this.params.get("shainNo").toString());
		
		//=====================================================================
		// 処理
		//=====================================================================
		// チェック対象の社員の存在確認、区分取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, null, null, null, null, null, null);
		
		//社員が存在しなければ処理終了
		if (0 >= mstShains.size()){ return true; }
		
		HashMap<String, String> mstShain = mstShains.get(0);

		//出勤簿入力区分が"01"(時給日給制)以外なら処理終了
		if("01".equals(mstShain.get("ShukinboKbn")) == false){ return false; }
		
		// 結果返却
		return true;
	}
}