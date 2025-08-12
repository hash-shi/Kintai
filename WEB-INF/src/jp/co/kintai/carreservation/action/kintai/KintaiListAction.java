package jp.co.kintai.carreservation.action.kintai;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiListAction extends PJActionBase {
	public KintaiListAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
				
		//=====================================================================
		// 結果返却
		//=====================================================================
		
		// 処理選択
		ArrayList<HashMap<String, String>> mstKubun0504 = PJActionBase.getMstKubuns(con, "0504", "", "");
		req.setAttribute("mstKubun0504", mstKubun0504);
		
		// 出力条件
		ArrayList<HashMap<String, String>> mstKubun0050 = PJActionBase.getMstKubuns(con, "0050", "", "");
		req.setAttribute("mstKubun0050", mstKubun0050);
		
		this.setView("success");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void kinShukkinBo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void chiChinginkeisansho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void kinYukyuKyukaDaicho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
}