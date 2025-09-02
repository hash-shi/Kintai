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
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CsvKyuyokeisanDataDownload extends DownloadBase {
	
	public CsvKyuyokeisanDataDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		String taishoNengetsuF	= req.getParameter("srhTxtTaishoNengetsuF");
		String taishoNengetsuT	= req.getParameter("srhTxtTaishoNengetsuT");
		
		// 現在日時を取得
		LocalDateTime now = LocalDateTime.now();
		
		// フォーマットを指定
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		
		DateTimeFormatter filenameformat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		
		// フォーマットに従って日時を文字列に変換
		String formattedDateTime = now.format(formatter);
		
		String filenameformattedDateTime = now.format(filenameformat);
		
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
		
		String[] targetCols = {"01", "03", "04", "06", "07", "08", "09", "10", "11", "12"};
		
		sql.append("SELECT ");
		sql.append("	skihon.*, ");
		sql.append("	shain.ShainName, ");
		sql.append("	shain.EigyoshoCode, ");
		sql.append("	( ");
		for (int idx = 0; idx < targetCols.length; idx++) {
		    if (idx > 0) {
		        sql.append(" + ");
		    }
		    sql.append("COALESCE(skihon.ShinseiNissu").append(targetCols[idx]).append(", 0)");
		}
		sql.append("	) AS GoukeiNissu ");
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
			csvStringTitle.addItem( "対象年度");
			csvStringTitle.addItem( "対象月度");
			csvStringTitle.addItem( "作成日付");
			csvStringTitle.addItem( "社員NO");
			csvStringTitle.addItem( "社員名");
			csvStringTitle.addItem( "営業所コード");
			
			csvStringTitle.addItem( "出勤日数");
			csvStringTitle.addItem( "出張日数");
			csvStringTitle.addItem( "欠勤日数");
			csvStringTitle.addItem( "有給日数");
			csvStringTitle.addItem( "半給日数");
			csvStringTitle.addItem( "積休日数");
			csvStringTitle.addItem( "特休日数");
			csvStringTitle.addItem( "休日日数");
			csvStringTitle.addItem( "代休日数");
			csvStringTitle.addItem( "振休日数");
			csvStringTitle.addItem( "休出日数");
			csvStringTitle.addItem( "代出日数");
			csvStringTitle.addItem( "合計日数");
			
			csvStringTitle.addItem( "残業時間");
			csvStringTitle.addItem( "深夜時間");
			csvStringTitle.addItem( "休出時間");
			csvStringTitle.addItem( "遅早時間");
			csvStringTitle.addItem( "時間外計");
			csvStringTitle.addItem( "特別作業金額");
			csvStringTitle.addItem( "その他");
			
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		for (int i = 0; i < data.size(); i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			
			// TaishoNenGetsudo の分割処理
			String nenGetsudo = data.get(i).get("TaishoNenGetsudo");
			String nendo = "";
			String getsudo = "";

			if (StringUtils.isNotBlank(nenGetsudo) && nenGetsudo.contains("/")) {
				String[] parts = nenGetsudo.split("/");
				if (parts.length == 2) {
					nendo = parts[0];     // 年度
					getsudo = parts[1];   // 月度
				}
			}
			
			// 年度・月度・作成日時など基本項目
			csvStringRecord.addItem(nendo);
			csvStringRecord.addItem(getsudo);
			csvStringRecord.addItem(formattedDateTime);
			csvStringRecord.addItem(data.get(i).get( "ShainNO"));
			csvStringRecord.addItem(data.get(i).get( "ShainName"));
			csvStringRecord.addItem(data.get(i).get( "EigyoshoCode"));
			
			
			// 日数出力
			String[] nissuKeys = {
					"ShinseiNissu01", "ShinseiNissu02", "ShinseiNissu03",
					"ShinseiNissu04", "ShinseiNissu05", "ShinseiNissu06",
					"ShinseiNissu07", "ShinseiNissu08", "ShinseiNissu09",
					"ShinseiNissu10", "ShinseiNissu11", "ShinseiNissu12",
					"GoukeiNissu"
				};

			for (String key : nissuKeys) {
			    String val = data.get(i).get(key);
			    if (StringUtils.isNotBlank(val)) {
			        try {
			            csvStringRecord.addItem(String.format("%.2f", new BigDecimal(val)));
			        } catch (NumberFormatException e) {
			            csvStringRecord.addItem("0.00");
			        }
			    } else {
			        csvStringRecord.addItem("0.00");
			    }
			}
			
			// 時間処理 + 出力
				
			// それぞれ小数点第2位までの表示に変換してCSVに追加
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiJikan01"), "0"))));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiJikan02"), "0"))));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiJikan03"), "0"))));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiJikan04"), "0"))));

			// 01 と 03 を足して合計を計算、表示
			BigDecimal jikan01 = new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiJikan01"), "0"));
			BigDecimal jikan03 = new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiJikan03"), "0"));
			csvStringRecord.addItem(String.format("%.2f", jikan01.add(jikan03)));
			
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiKingaku01"), "0"))));
			csvStringRecord.addItem(String.format("%.2f", new BigDecimal(StringUtils.defaultIfBlank(data.get(i).get("ShinseiKingaku02"), "0"))));
			
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("CsvKyuyokeisanData_" + filenameformattedDateTime + ".csv");
	}
	
}