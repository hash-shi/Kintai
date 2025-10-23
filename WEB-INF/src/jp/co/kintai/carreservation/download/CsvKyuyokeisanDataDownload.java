package jp.co.kintai.carreservation.download;

import java.math.BigDecimal;
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

public class CsvKyuyokeisanDataDownload extends DownloadBase {
	
	public CsvKyuyokeisanDataDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		HashMap<String, String> columns = new HashMap<String, String>();
		String taishoNengetsuF	= req.getParameter("srhTxtTaishoNengetsuF");
		String taishoNengetsuT	= req.getParameter("srhTxtTaishoNengetsuT");
		
		// 現在日時を取得
		LocalDateTime now 					= LocalDateTime.now();
		
		// フォーマットを指定
		DateTimeFormatter filenameformat 	= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		
		// フォーマットに従って日時を文字列に変換
		String filenameformattedDateTime 	= now.format(filenameformat);
		
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
		
		sql.append("SELECT ");
		sql.append("	FORMAT(GETDATE(), 'yyyy/MM/dd') AS SystemDate, ");
		sql.append("	skihon.*, ");
		sql.append("	shain.ShainName, ");
		sql.append("	shain.EigyoshoCode, ");
		sql.append("		LEFT(skihon.TaishoNenGetsudo, 4) AS Nendo, ");
		sql.append("		RIGHT(skihon.TaishoNenGetsudo, 2) AS Getsudo, ");
		
		sql.append("	COALESCE(skihon.ShinseiNissu01, 0) AS ShinseiNissu01, ");		
		
		// 出張
		sql.append("	(SELECT ISNULL(SUM(CASE WHEN Q1.KintaiKbn = '02' THEN Q1.KintaiShinseiNisuu END), 0) ");
		sql.append("		FROM ( ");
		sql.append("			SELECT smeisai.KintaiKbn, LEFT(M1.GroupCode1, 2) AS Kbn, smeisai.KintaiShinseiNisuu ");
		sql.append("			FROM KIN_SHUKKINBO_MEISAI smeisai ");
		sql.append("			LEFT OUTER JOIN MST_KUBUN M1 ");
		sql.append("			ON smeisai.KintaiKbn = M1.Code AND M1.KbnCode = '0100' ");
		sql.append("			WHERE smeisai.KintaiKbn <> '00' ");
		sql.append("				AND smeisai.TaishoNenGetsudo = skihon.TaishoNenGetsudo ");
		sql.append("				AND smeisai.ShainNO = skihon.ShainNO ");
		sql.append("		) Q1) AS ShinseiNissu02, ");
		
		sql.append("	COALESCE(skihon.ShinseiNissu03, 0) AS ShinseiNissu03, ");
		
		// 有給
		sql.append("	(SELECT ISNULL(SUM(CASE WHEN Q1.KintaiKbn = '04' THEN Q1.KintaiShinseiNisuu END), 0) ");
		sql.append("		FROM ( ");
		sql.append("			SELECT smeisai.KintaiKbn, LEFT(M1.GroupCode1, 2) AS Kbn, smeisai.KintaiShinseiNisuu ");
		sql.append("			FROM KIN_SHUKKINBO_MEISAI smeisai ");
		sql.append("			LEFT OUTER JOIN MST_KUBUN M1 ");
		sql.append("			ON smeisai.KintaiKbn = M1.Code AND M1.KbnCode = '0100' ");
		sql.append("			WHERE smeisai.KintaiKbn <> '00' ");
		sql.append("				AND smeisai.TaishoNenGetsudo = skihon.TaishoNenGetsudo ");
		sql.append("				AND smeisai.ShainNO = skihon.ShainNO ");
		sql.append("		) Q1) AS ShinseiNissu04, ");
		
		// 半給
		sql.append("	(SELECT ISNULL(SUM(CASE WHEN Q1.KintaiKbn = '05' THEN Q1.KintaiShinseiNisuu END), 0) ");
		sql.append("		FROM ( ");
		sql.append("			SELECT smeisai.KintaiKbn, LEFT(M1.GroupCode1, 2) AS Kbn, smeisai.KintaiShinseiNisuu ");
		sql.append("			FROM KIN_SHUKKINBO_MEISAI smeisai ");
		sql.append("			LEFT OUTER JOIN MST_KUBUN M1 ");
		sql.append("			ON smeisai.KintaiKbn = M1.Code AND M1.KbnCode = '0100' ");
		sql.append("			WHERE smeisai.KintaiKbn <> '00' ");
		sql.append("				AND smeisai.TaishoNenGetsudo = skihon.TaishoNenGetsudo ");
		sql.append("				AND smeisai.ShainNO = skihon.ShainNO ");
		sql.append("		) Q1) AS ShinseiNissu05, ");
		
		// 積休
		sql.append("	(SELECT ISNULL(SUM(CASE WHEN Q1.KintaiKbn = '06' THEN Q1.KintaiShinseiNisuu END), 0) ");
		sql.append("		FROM ( ");
		sql.append("			SELECT smeisai.KintaiKbn, LEFT(M1.GroupCode1, 2) AS Kbn, smeisai.KintaiShinseiNisuu ");
		sql.append("			FROM KIN_SHUKKINBO_MEISAI smeisai ");
		sql.append("			LEFT OUTER JOIN MST_KUBUN M1 ");
		sql.append("			ON smeisai.KintaiKbn = M1.Code AND M1.KbnCode = '0100' ");
		sql.append("			WHERE smeisai.KintaiKbn <> '00' ");
		sql.append("				AND smeisai.TaishoNenGetsudo = skihon.TaishoNenGetsudo ");
		sql.append("				AND smeisai.ShainNO = skihon.ShainNO ");
		sql.append("		) Q1) AS ShinseiNissu06, ");
		
		sql.append("	COALESCE(skihon.ShinseiNissu07, 0) AS ShinseiNissu07, ");
		sql.append("	COALESCE(skihon.ShinseiNissu08, 0) AS ShinseiNissu08, ");
		sql.append("	COALESCE(skihon.ShinseiNissu09, 0) AS ShinseiNissu09, ");
		sql.append("	COALESCE(skihon.ShinseiNissu10, 0) AS ShinseiNissu10, ");
		sql.append("	COALESCE(skihon.ShinseiNissu11, 0) AS ShinseiNissu11, ");
		sql.append("	COALESCE(skihon.ShinseiNissu12, 0) AS ShinseiNissu12, ");
		
		// 合計日数
		sql.append("	(COALESCE(skihon.ShinseiNissu01, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu03, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu04, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu06, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu07, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu08, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu09, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu10, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu11, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiNissu12, 0) ");
		sql.append("	) AS GoukeiNissu, ");
		
		// 時間外計
		sql.append("	(COALESCE(skihon.ShinseiJikan01, 0) ");
		sql.append("	+ COALESCE(skihon.ShinseiJikan03, 0) ");
		sql.append("	) AS JikangaiKei ");
		
		
		sql.append("FROM KIN_SHUKKINBO_KIHON skihon ");
		sql.append("LEFT JOIN MST_SHAIN shain ON skihon.ShainNO = shain.ShainNO ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(taishoNengetsuF)) {
			sql.append(" AND skihon.TaishoNenGetsudo >= ? ");
			pstmtf.addValue("String", taishoNengetsuF);
		}

		if (StringUtils.isNotBlank(taishoNengetsuT)) {
			sql.append(" AND skihon.TaishoNenGetsudo <= ? ");
			pstmtf.addValue("String", taishoNengetsuT);
		}
		
		sql.append(" AND skihon.KakuteiKbn = '03' ");
		sql.append(" ORDER BY skihon.TaishoNenGetsudo, skihon.ShainNO ");
		
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
			csvStringTitle.addItem( "対象年度",true);
			csvStringTitle.addItem( "対象月度",true);
			csvStringTitle.addItem( "作成日付",true);
			csvStringTitle.addItem( "社員NO",true);
			csvStringTitle.addItem( "社員名",true);
			csvStringTitle.addItem( "営業所コード",true);
			
			csvStringTitle.addItem( "出勤日数",true);
			csvStringTitle.addItem( "出張日数",true);
			csvStringTitle.addItem( "欠勤日数",true);
			csvStringTitle.addItem( "有給日数",true);
			csvStringTitle.addItem( "半給日数",true);
			csvStringTitle.addItem( "積休日数",true);
			csvStringTitle.addItem( "特休日数",true);
			csvStringTitle.addItem( "休日日数",true);
			csvStringTitle.addItem( "代休日数",true);
			csvStringTitle.addItem( "振休日数",true);
			csvStringTitle.addItem( "休出日数",true);
			csvStringTitle.addItem( "代出日数",true);
			csvStringTitle.addItem( "合計日数",true);
			
			csvStringTitle.addItem( "残業時間",true);
			csvStringTitle.addItem( "深夜時間",true);
			csvStringTitle.addItem( "休出時間",true);
			csvStringTitle.addItem( "遅早時間",true);
			csvStringTitle.addItem( "時間外計",true);
			csvStringTitle.addItem( "特別作業金額",true);
			csvStringTitle.addItem( "その他",true);
			
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		for (int i = 0; i < data.size(); i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			
			// 1行取得
			HashMap<String, String> d = data.get(i);
			
			csvStringRecord.addItem(d.get("Nendo"), PJActionBase.getQuotation(columns, "Nendo", d.get("Nendo")));
			csvStringRecord.addItem(d.get("Getsudo"), PJActionBase.getQuotation(columns, "Getsudo", d.get("Getsudo")));
			csvStringRecord.addItem(d.get("SystemDate"), PJActionBase.getQuotation(columns, "SystemDate", d.get("SystemDate")));
			csvStringRecord.addItem(d.get("ShainNO"), PJActionBase.getQuotation(columns, "ShainNO", d.get("ShainNO")));
			csvStringRecord.addItem(d.get("ShainName"), PJActionBase.getQuotation(columns, "ShainName", d.get("ShainName")));
			csvStringRecord.addItem(d.get("EigyoshoCode"), PJActionBase.getQuotation(columns, "EigyoshoCode", d.get("EigyoshoCode")));

			// 小数点第2位までの表示に変換してCSVに追加
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu01"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu01", d.get("ShinseiNissu01")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu02"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu02", d.get("ShinseiNissu02")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu03"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu03", d.get("ShinseiNissu03")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu04"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu04", d.get("ShinseiNissu04")));

			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu05"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu05", d.get("ShinseiNissu05")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu06"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu06", d.get("ShinseiNissu06")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu07"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu07", d.get("ShinseiNissu07")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu08"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu08", d.get("ShinseiNissu08")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu09"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu09", d.get("ShinseiNissu09")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu10"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu10", d.get("ShinseiNissu10")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu11"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu11", d.get("ShinseiNissu11")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiNissu12"), "0"))), PJActionBase.getQuotation(columns, "ShinseiNissu12", d.get("ShinseiNissu12")));

			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("GoukeiNissu"), "0"))), PJActionBase.getQuotation(columns, "GoukeiNissu", d.get("GoukeiNissu")));

			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiJikan01"), "0"))), PJActionBase.getQuotation(columns, "ShinseiJikan01", d.get("ShinseiJikan01")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiJikan02"), "0"))), PJActionBase.getQuotation(columns, "ShinseiJikan02", d.get("ShinseiJikan02")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiJikan03"), "0"))), PJActionBase.getQuotation(columns, "ShinseiJikan03", d.get("ShinseiJikan03")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiJikan04"), "0"))), PJActionBase.getQuotation(columns, "ShinseiJikan04", d.get("ShinseiJikan04")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("JikangaiKei"), "0"))), PJActionBase.getQuotation(columns, "JikangaiKei", d.get("JikangaiKei")));

			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiKingaku01"), "0"))), PJActionBase.getQuotation(columns, "ShinseiKingaku01", d.get("ShinseiKingaku01")));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(d.get("ShinseiKingaku02"), "0"))), PJActionBase.getQuotation(columns, "ShinseiKingaku02", d.get("ShinseiKingaku02")));
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("CsvKyuyokeisanData_" + filenameformattedDateTime + ".csv");
	}
	
}