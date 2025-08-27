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
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CsvMstKubunDownload extends DownloadBase {
	
	public CsvMstKubunDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		int count = 0;
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		String fromKbnCode	= req.getParameter("srhTxtKbnCodeF");
		String toKbnCode	= req.getParameter("srhTxtKbnCodeT");
		String fromSaishuKoshinDate	= req.getParameter("srhTxtSaishuKoshinDateF");
		String toSaishuKoshinDate	= req.getParameter("srhTxtSaishuKoshinDateT");
		// 現在日時を取得
        LocalDateTime now = LocalDateTime.now();

        // フォーマットを指定
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // フォーマットに従って日時を文字列に変換
        String formattedDateTime = now.format(formatter);
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
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
		sql.append(" 	MST_KUBUN ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(fromKbnCode)) {
		 sql.append(" AND CAST(KbnCode AS int) >= ? ");
	     pstmtf.addValue("String", fromKbnCode);
		}
		
		if (StringUtils.isNotBlank(toKbnCode)) {
	     sql.append(" AND CAST(KbnCode AS int) <= ? ");
		 pstmtf.addValue("String", toKbnCode);
		}
		
		if(StringUtils.isNotBlank(fromSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate >= ?");
		 pstmtf.addValue("String", fromSaishuKoshinDate);
		}
		
		if(StringUtils.isNotBlank(toSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate <= ?");
		 pstmtf.addValue("String", toSaishuKoshinDate);
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
				// カラム名をkeyとして値を格納
				for (int i = 1; i <= colCount; i++) {
					record.put(metaData.getColumnLabel(i), StringUtils.stripToEmpty(rset.getString(i)));
				}
				// 配列の格納
				data.add(record);
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
			csvStringTitle.addItem( "区分コード");
			csvStringTitle.addItem( "コード");
			csvStringTitle.addItem( "区分名称");
			csvStringTitle.addItem( "区分略称");
		    csvStringTitle.addItem( "グループコード1");
			csvStringTitle.addItem( "グループコード2");

			csvStringTitle.addItem( "最終更新社員NO");
			csvStringTitle.addItem( "最終更新日");
			csvStringTitle.addItem( "最終更新時刻");

		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		// 明細部の設定
		count = data.size();
		for (int i = 0; i < count; i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			csvStringRecord.addItem(data.get(i).get( "KbnCode"));
			csvStringRecord.addItem(data.get(i).get( "Code"));
			csvStringRecord.addItem(data.get(i).get( "KbnName"));
			csvStringRecord.addItem(data.get(i).get( "KbnRyaku"));
			csvStringRecord.addItem(data.get(i).get( "GroupCode1"));
			csvStringRecord.addItem(data.get(i).get( "GroupCode2"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinShainNO"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinDate"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinJikan"));
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes());
		// 名前を付けて保存
		this.setFilename("CsvMstKubun_" + formattedDateTime + ".csv");
	}
	
}