package jp.co.kintai.carreservation.action.master;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstEigyoshoAction extends PJActionBase {
	public MstEigyoshoAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 画面表示
		this.setView("success");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String eigyoshoCode	= req.getParameter("srhTxtEigyoshoCode");
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);
		
		//=====================================================================
		// 検索
		//=====================================================================

		Boolean isNew = false;
		ArrayList<HashMap<String, String>> mstDatas = PJActionBase.getMstEigyoshos(con, eigyoshoCode, null);
		
		// データが0件 = 新規の時は空の配列を作成する。
		if (mstDatas.size() == 0) {
			// 新規モード
			isNew = true;
			// 1レコード分の配列を用意
			HashMap<String, String> record = new HashMap<String, String>();
			record.put("EigyoshoCode", eigyoshoCode);
			record.put("EigyoshoName", "");
			record.put("SaishuKoshinShainNO", "");
			record.put("SaishuKoshinShainName", "");
			record.put("SaishuKoshinDate", "");
			record.put("SaishuKoshinJikan", "");
			// 配列の格納
			mstDatas.add(record);
		}
		
		this.addContent("isNew", isNew);
		this.addContent("mstDatas", mstDatas);
	}
}