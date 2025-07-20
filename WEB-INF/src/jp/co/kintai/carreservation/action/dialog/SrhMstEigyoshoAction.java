package jp.co.kintai.carreservation.action.dialog;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class SrhMstEigyoshoAction extends PJActionBase {
	public SrhMstEigyoshoAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 画面表示
		this.setView("success");
	}
	
	/**
	 * 営業所取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 検索条件取得
		String eigyoshoCode	= this.getParameter("srhTxtEigyoshoCode");
		String eigyoshoName	= this.getParameter("srhTxtEigyoshoName");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		this.addContent("result", this.getMstEigyoshos(con, eigyoshoCode, eigyoshoName));
	}
	
}