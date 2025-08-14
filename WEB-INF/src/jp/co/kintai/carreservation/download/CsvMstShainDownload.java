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

public class CsvMstShainDownload extends DownloadBase {
	
	public CsvMstShainDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		int count = 0;
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		String fromEigyoshoCode	= req.getParameter("txtSrhEigyoshoCodeF");
		String toEigyoshoCode	= req.getParameter("txtSrhEigyoshoCodeT");
		String fromShainNO	= req.getParameter("txtSrhShainNOF");
		String toShainNO	= req.getParameter("txtSrhShainNOT");
		String fromSaishuKoshinDate	= req.getParameter("txtSrhSaishuKoshinDateF");
	    String toSaishuKoshinDate	= req.getParameter("txtSrhSaishuKoshinDateT");
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
		sql.append(" 	MST_SHAIN ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
		 sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
	     pstmtf.addValue("String", fromEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
	     sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
		 pstmtf.addValue("String", toEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(fromShainNO)) {
	     sql.append(" AND CAST(ShainNO AS int) >= ? ");
		 pstmtf.addValue("String", fromShainNO);
		}
			
		if (StringUtils.isNotBlank(toShainNO)) {
		 sql.append(" AND CAST(ShainNO AS int) <= ? ");
		 pstmtf.addValue("String", toShainNO);
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
			csvStringTitle.addItem( "社員NO");
		    csvStringTitle.addItem( "社員名");
		    csvStringTitle.addItem( "パスワード");
		    csvStringTitle.addItem( "ユーザ区分");
			csvStringTitle.addItem( "社員区分");
			csvStringTitle.addItem( "出勤簿入力区分");
			csvStringTitle.addItem( "営業所コード");
			csvStringTitle.addItem( "部署コード");
			csvStringTitle.addItem( "有給休暇付与日数");
			csvStringTitle.addItem( "時給日給区分");
			csvStringTitle.addItem( "勤務開始時刻（時）");
			csvStringTitle.addItem( "勤務開始時刻（分）");
			csvStringTitle.addItem( "勤務終了時刻（時）");
			csvStringTitle.addItem( "勤務終了時刻（分）");
			csvStringTitle.addItem( "勤務実働時間");
			csvStringTitle.addItem( "01勤務時間単価");
			csvStringTitle.addItem( "02時間外勤務単価");
			csvStringTitle.addItem( "03深夜勤務単価");
			csvStringTitle.addItem( "04休日勤務単価");
			csvStringTitle.addItem( "05有給休暇単価");
			csvStringTitle.addItem( "06半日有給単価");
			csvStringTitle.addItem( "07控除単価");
			csvStringTitle.addItem( "08単価");
			csvStringTitle.addItem( "09通勤費単価/月給");
			csvStringTitle.addItem( "10時間外勤務単価");
			csvStringTitle.addItem( "11特別有給休暇単価");
			csvStringTitle.addItem( "通勤費精算区分");
			csvStringTitle.addItem( "退職年月日");
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
			csvStringRecord.addItem(data.get(i).get( "ShainNO"));
			csvStringRecord.addItem(data.get(i).get( "ShainName"));
			csvStringRecord.addItem(data.get(i).get( "Password"));
			csvStringRecord.addItem(data.get(i).get( "UserKbn"));
			csvStringRecord.addItem(data.get(i).get( "ShainKbn"));
			csvStringRecord.addItem(data.get(i).get( "ShukinboKbn"));
			csvStringRecord.addItem(data.get(i).get( "EigyoshoCode"));
			csvStringRecord.addItem(data.get(i).get( "BushoCode"));
			csvStringRecord.addItem(data.get(i).get( "YukyuKyukaFuyoNissu"));
			csvStringRecord.addItem(data.get(i).get( "JikyuNikkyuKbn"));
			csvStringRecord.addItem(data.get(i).get( "KinmuKaishiJi"));
			csvStringRecord.addItem(data.get(i).get( "KinmuKaishiFun"));
			csvStringRecord.addItem(data.get(i).get( "KinmuShuryoJi"));
			csvStringRecord.addItem(data.get(i).get( "KinmuShuryoFun"));
			csvStringRecord.addItem(data.get(i).get( "KeiyakuJitsudoJikan"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka01"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka02"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka03"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka04"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka05"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka06"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka07"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka08"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka09"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka10"));
			csvStringRecord.addItem(data.get(i).get( "ShinseiTanka11"));
			csvStringRecord.addItem(data.get(i).get( "TsukinHiKbn"));
			csvStringRecord.addItem(data.get(i).get( "TaisyokuDate"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinShainNO"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinDate"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinJikan"));
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes());
		// 名前を付けて保存
		this.setFilename("CsvMstShain_" + formattedDateTime + ".csv");
	}
	
}