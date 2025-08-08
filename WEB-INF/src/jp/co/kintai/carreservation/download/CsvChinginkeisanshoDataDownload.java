package jp.co.kintai.carreservation.download;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.wakhok.tomoharu.csv.CSVLine;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CsvChinginkeisanshoDataDownload extends DownloadBase {
	
	public CsvChinginkeisanshoDataDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String taishoNengetsuF	= req.getParameter("txtTaishoNengetsuF");
		String taishoNengetsuT	= req.getParameter("txtTaishoNengetsuT");
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
//		// PJActionBaseからデータ検索も可能
//		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, null, null, null, null, null, null);
				
		// 改行コード
		String newLine = "\r\n";
		
		// CSVデータ
		StringBuffer csvString = new StringBuffer();
		
		// CSVデータヘッダ
		CSVLine csvStringTitle = new CSVLine();
		for (int i = 0; i < 10; i++) {
			csvStringTitle.addItem( i + "列目");
		}
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		// 明細部の設定
		for (int i = 0; i < 10; i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			csvStringRecord.addItem(taishoNengetsuF);
			csvStringRecord.addItem(taishoNengetsuT);
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes());
		// 名前を付けて保存
		this.setFilename("csvFileDownload.csv");
	}
	
}