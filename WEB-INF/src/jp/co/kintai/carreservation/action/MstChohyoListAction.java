package jp.co.kintai.carreservation.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstChohyoListAction extends PJActionBase {
	public MstChohyoListAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 画面表示
		this.setView("success");
	}
	
	public void change(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// ドロップダウンの値を取得
        String selectedValue = req.getParameter("rdoShoriSentaku");
        System.out.println("選択された値: " + selectedValue);
		this.addContent("result",selectedValue);
	}
}