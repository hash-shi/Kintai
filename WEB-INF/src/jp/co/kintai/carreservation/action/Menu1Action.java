package jp.co.kintai.carreservation.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.CsvInformation;
import jp.co.kintai.carreservation.information.PdfInformation;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class Menu1Action extends PJActionBase {
	public Menu1Action(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
	public void downloadCsvFile(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータの取得
		//=====================================================================
		String inpText01 = this.getParameter("inp_text01");
		
		// データの登録や保存など必要な処理を記載。
		
		// CSVダウンロード用のセッションに格納
		CsvInformation cavInformation	= new CsvInformation();
		
		cavInformation.setTextData(inpText01);
		
		req.getSession().setAttribute(Define.SESSION_ID_CSV, cavInformation);
		
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void downloadPdfFile(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータの取得
		//=====================================================================
		String inpText02 = this.getParameter("inp_text02");
		
		// データの登録や保存など必要な処理を記載。
		
		// CSVダウンロード用のセッションに格納
		PdfInformation pdfInformation	= new PdfInformation();
		
		pdfInformation.setTextData(inpText02);
		
		req.getSession().setAttribute(Define.SESSION_ID_PDF, pdfInformation);
		
	}
}