package jp.co.kintai.carreservation.download;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.ac.wakhok.tomoharu.csv.CSVLine;
import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CsvMstEigyoshoDownload extends DownloadBase {
	
	public CsvMstEigyoshoDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		HashMap<String, String> columns = new HashMap<String, String>();
		String eigyoshoCodeF		= req.getParameter("srhTxtEigyoshoCodeF");
		String eigyoshoCodeT		= req.getParameter("srhTxtEigyoshoCodeT");
		String saishuKoshinDateF	= req.getParameter("srhTxtSaishuKoshinDateF");
		String saishuKoshinDateT	= req.getParameter("srhTxtSaishuKoshinDateT");
		
		// 現在日時を取得
		LocalDateTime now = LocalDateTime.now();
		
		// フォーマットを指定
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		
		// フォーマットに従って日時を文字列に変換
		String formattedDateTime = now.format(formatter);
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con					= this.getConnection("kintai", req);
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// データ取得
		//=====================================================================
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_EIGYOSHO ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(eigyoshoCodeF)) {
			sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
			pstmtf.addValue("String", eigyoshoCodeF);
		}
		
		if (StringUtils.isNotBlank(eigyoshoCodeT)) {
			sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
			pstmtf.addValue("String", eigyoshoCodeT);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateF)) {
			sql.append(" AND SaishuKoshinDate >= ?");
			pstmtf.addValue("String", saishuKoshinDateF);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateT)) {
			sql.append(" AND SaishuKoshinDate <= ?");
			pstmtf.addValue("String", saishuKoshinDateT);
		}
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			ResultSetMetaData metaData = rset.getMetaData();
			
			// カラム数(列数)の取得
			int colCount = metaData.getColumnCount(); 
			
			// レコード数分繰り返す
			while (rset.next()){
				// 1レコード分の配列を用意
				HashMap<String, String> record = new HashMap<String, String>();
				HashMap<String, String> recordc = new HashMap<String, String>();
				// カラム名をkeyとして値を格納
				for (int i = 1; i <= colCount; i++) {
					record.put(metaData.getColumnLabel(i), StringUtils.stripToEmpty(rset.getString(i)));
					// カラムのSQLデータ型を取得
					recordc.put(metaData.getColumnLabel(i), metaData.getColumnTypeName(i));
				}
				// 配列の格納
				data.add(record);
				columns = recordc;
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
				
		// 改行コード
		String newLine = "\r\n";
		
		// CSVデータ
		StringBuffer csvString = new StringBuffer();
		
		// CSVデータヘッダ
		CSVLine csvStringTitle = new CSVLine();
		csvStringTitle.addItem("営業所コード",true);
		csvStringTitle.addItem("営業所名",true);
		csvStringTitle.addItem("最終更新社員NO",true);
		csvStringTitle.addItem("最終更新日",true);
		csvStringTitle.addItem("最終更新時刻",true);
		
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		// 明細部の設定
		for (int i = 0; i < data.size(); i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			
			// 1行取得
			HashMap<String, String> d = data.get(i);
			
			csvStringRecord.addItem(d.get("EigyoshoCode"), PJActionBase.getQuotation(columns, "EigyoshoCode"));
			csvStringRecord.addItem(d.get("EigyoshoName"), PJActionBase.getQuotation(columns, "EigyoshoName"));
			csvStringRecord.addItem(d.get("SaishuKoshinShainNO"), PJActionBase.getQuotation(columns, "SaishuKoshinShainNO"));
			csvStringRecord.addItem(d.get("SaishuKoshinDate"), PJActionBase.getQuotation(columns, "SaishuKoshinDate"));
			csvStringRecord.addItem(d.get("SaishuKoshinJikan"), PJActionBase.getQuotation(columns, "SaishuKoshinJikan"));
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("CsvMstEigyosho_" + formattedDateTime + ".csv");
	}
	
}