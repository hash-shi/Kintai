package jp.co.kintai.carreservation.action.kintai;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiOutDataAction extends PJActionBase {
	public KintaiOutDataAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
				
		//=====================================================================
		// 結果返却
		//=====================================================================
		// 取得
		ArrayList<HashMap<String, String>> mstKubun = PJActionBase.getMstKubuns(con, "0503", "", "");
		req.setAttribute("mstKubun", mstKubun);
		
		this.setView("success");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void download(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
}