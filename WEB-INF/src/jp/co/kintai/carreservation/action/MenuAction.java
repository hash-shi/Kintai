package jp.co.kintai.carreservation.action;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MenuAction extends PJActionBase {
	public MenuAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con = this.getConnection("kintai", req);
		// 区分一覧
		req.setAttribute("mstKubuns", PJActionBase.getMstKubuns(con, "0501", null, null));
		// 画面表示
		this.setView("success");
	}
}