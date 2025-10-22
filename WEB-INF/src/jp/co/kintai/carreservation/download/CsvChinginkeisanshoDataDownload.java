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

public class CsvChinginkeisanshoDataDownload extends DownloadBase {
	
	public CsvChinginkeisanshoDataDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
		Connection con 					= this.getConnection("kintai", req);
		PreparedStatement pstmt 		= null;
		StringBuffer sql 				= new StringBuffer();
		PreparedStatementFactory pstmtf = new PreparedStatementFactory();
		ResultSet rset 					= null;
		
		//=====================================================================
		// データ取得
		//=====================================================================
		sql.append("SELECT ");
		sql.append("	FORMAT(GETDATE(), 'yyyy/MM/dd') AS SystemDate, ");
		sql.append("	kihon.*, ");
		sql.append("		shain.ShainName, ");
		sql.append("		shain.EigyoshoCode, ");
		sql.append("		LEFT(kihon.TaishoNenGetsudo, 4) AS Nendo, ");
		sql.append("		RIGHT(kihon.TaishoNenGetsudo, 2) AS Getsudo, ");
		// 休日日数
		sql.append("	( ");
		sql.append("		SELECT COUNT(*) ");
		sql.append("		FROM CHI_CHINGINKEISANSHO_MEISAI meisai ");
		sql.append("		WHERE ");
		sql.append("			meisai.TaishoNenGetsudo = kihon.TaishoNenGetsudo ");
		sql.append("		AND meisai.ShainNO = kihon.ShainNO ");
		sql.append("		AND (meisai.ShusshaJi = '' OR meisai.ShusshaJi IS NULL) ");
		sql.append("		AND (meisai.ShusshaFun = '' OR meisai.ShusshaFun IS NULL) ");
		sql.append("		AND (meisai.TaishaJi = '' OR meisai.TaishaJi IS NULL) ");
		sql.append("		AND (meisai.TaishaFun = '' OR meisai.TaishaFun IS NULL) ");
		sql.append("		AND meisai.JitsudoJikan = 0 ");
		sql.append("		AND (meisai.ChinginShinseiKbn1 = '' OR meisai.ChinginShinseiKbn1 = '00') ");
		sql.append("		AND meisai.ChinginShinseiJikan1 = 0 ");
		sql.append("		AND (meisai.ChinginShinseiKbn2 = '' OR meisai.ChinginShinseiKbn2 = '00') ");
		sql.append("		AND meisai.ChinginShinseiJikan2 = 0 ");
		sql.append("	) AS KyujitsuNissu, ");
		// 合計日数
		sql.append("	( ");
		sql.append("		COALESCE(kihon.ShinseiNissu01,0) + ");
		sql.append("		COALESCE(kihon.ShinseiNissu04,0) + ");
		sql.append("		COALESCE(kihon.ShinseiNissu02,0) + ");
		sql.append("		COALESCE(kihon.ShinseiNissu03,0) + ");
		sql.append("		COALESCE(kihon.ShinseiNissu05,0) + ");
		sql.append("		COALESCE(kihon.ShinseiNissu11,0) + ");
		sql.append("		COALESCE(kihon.ShinseiNissu06,0) + ");
		sql.append("		COALESCE(kihon.ShinseiNissu07,0) + ");
		sql.append("		COALESCE(( ");
		sql.append("		SELECT COUNT(*) ");
		sql.append("		FROM CHI_CHINGINKEISANSHO_MEISAI meisai ");
		sql.append("		WHERE meisai.TaishoNenGetsudo = kihon.TaishoNenGetsudo ");
		sql.append("		AND meisai.ShainNO = kihon.ShainNO ");
		sql.append("			AND (meisai.ShusshaJi = '' OR meisai.ShusshaJi IS NULL) ");
		sql.append("			AND (meisai.ShusshaFun = '' OR meisai.ShusshaFun IS NULL) ");
		sql.append("			AND (meisai.TaishaJi = '' OR meisai.TaishaJi IS NULL) ");
		sql.append("			AND (meisai.TaishaFun = '' OR meisai.TaishaFun IS NULL) ");
		sql.append("			AND meisai.JitsudoJikan = 0 ");
		sql.append("			AND (meisai.ChinginShinseiKbn1 = '' OR meisai.ChinginShinseiKbn1 = '00') ");
		sql.append("			AND meisai.ChinginShinseiJikan1 = 0 ");
		sql.append("			AND (meisai.ChinginShinseiKbn2 = '' OR meisai.ChinginShinseiKbn2 = '00') ");
		sql.append("			AND meisai.ChinginShinseiJikan2 = 0 ");
		sql.append("		),0) ");
		sql.append("	) AS GoukeiNissu, ");
		// 合計時間
		sql.append("	( ");
		sql.append("		COALESCE(kihon.ShinseiJikan01,0) + ");
		sql.append("		COALESCE(kihon.ShinseiJikan04,0) + ");
		sql.append("		COALESCE(kihon.ShinseiJikan02,0) + ");
		sql.append("		COALESCE(kihon.ShinseiJikan03,0) + ");
		sql.append("		COALESCE(kihon.ShinseiJikan05,0) + ");
		sql.append("		COALESCE(kihon.ShinseiJikan11,0) + ");
		sql.append("		COALESCE(kihon.ShinseiJikan06,0) + ");
		sql.append("		COALESCE(kihon.ShinseiJikan07,0) ");
		sql.append("	) AS GoukeiJikan, ");
		// 合計金額
		sql.append("	( ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei01,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei04,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei02,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei03,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei05,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei11,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei06,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei09,0) + ");
		sql.append("		COALESCE(kihon.ShinseiKingakuGoukei07,0) ");
		sql.append("	) AS GoukeiKingaku ");
		
		sql.append("FROM CHI_CHINGINKEISANSHO_KIHON kihon ");
		sql.append("LEFT JOIN MST_SHAIN shain ON kihon.ShainNO = shain.ShainNO ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(taishoNengetsuF)) {
			sql.append(" AND kihon.TaishoNenGetsudo >= ? ");
			pstmtf.addValue("String", taishoNengetsuF);
		}

		if (StringUtils.isNotBlank(taishoNengetsuT)) {
			sql.append(" AND kihon.TaishoNenGetsudo <= ? ");
			pstmtf.addValue("String", taishoNengetsuT);
		}
		
		sql.append(" AND kihon.KakuteiKbn = '03' ");
		sql.append(" ORDER BY kihon.TaishoNenGetsudo, shain.ShainNO ");
		
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
			csvStringTitle.addItem( "対象年度", true);
			csvStringTitle.addItem( "対象月度",true);
			csvStringTitle.addItem( "作成日付",true);
			csvStringTitle.addItem( "社員NO",true);
			csvStringTitle.addItem( "社員名",true);
			csvStringTitle.addItem( "営業所コード",true);
			
			csvStringTitle.addItem( "通常勤務日数",true);
			csvStringTitle.addItem( "休日勤務日数",true);
			csvStringTitle.addItem( "時間外勤務日数",true);
			csvStringTitle.addItem( "深夜勤務日数",true);
			csvStringTitle.addItem( "有給休暇勤務日数",true);
			csvStringTitle.addItem( "特別有給休暇勤務日数",true);
			csvStringTitle.addItem( "半日有給日数",true);
			csvStringTitle.addItem( "控除日数",true);
			csvStringTitle.addItem( "休日日数",true);
			csvStringTitle.addItem( "合計日数",true);
			
			csvStringTitle.addItem( "通常勤務時間",true);
			csvStringTitle.addItem( "休日勤務時間",true);
			csvStringTitle.addItem( "時間外勤務時間",true);
			csvStringTitle.addItem( "深夜勤務時間",true);
			csvStringTitle.addItem( "有給休暇勤務時間",true);
			csvStringTitle.addItem( "特別有給休暇勤務時間",true);
			csvStringTitle.addItem( "半日有給時間",true);
			csvStringTitle.addItem( "控除時間",true);
			csvStringTitle.addItem( "合計時間",true);
			
			csvStringTitle.addItem( "通常勤務単価",true);
			csvStringTitle.addItem( "休日勤務単価",true);
			csvStringTitle.addItem( "時間外勤務単価",true);
			csvStringTitle.addItem( "深夜勤務単価",true);
			csvStringTitle.addItem( "有給休暇勤務単価",true);
			csvStringTitle.addItem( "特別有給休暇勤務単価",true);
			csvStringTitle.addItem( "半日有給単価",true);
			csvStringTitle.addItem( "通勤費単価",true);
			csvStringTitle.addItem( "控除単価",true);
			
			csvStringTitle.addItem( "通常勤務金額",true);
			csvStringTitle.addItem( "休日勤務金額",true);
			csvStringTitle.addItem( "時間外勤務金額",true);
			csvStringTitle.addItem( "深夜勤務金額",true);
			csvStringTitle.addItem( "有給休暇勤務金額",true);
			csvStringTitle.addItem( "特別有給休暇勤務金額",true);
			csvStringTitle.addItem( "半日有給金額",true);
			csvStringTitle.addItem( "通勤費金額",true);
			csvStringTitle.addItem( "控除金額",true);
			
			csvStringTitle.addItem( "合計金額",true);
			
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

			csvStringRecord.addItem(d.get("ShinseiNissu01"), PJActionBase.getQuotation(columns, "ShinseiNissu01", d.get("ShinseiNissu01")));
			csvStringRecord.addItem(d.get("ShinseiNissu04"), PJActionBase.getQuotation(columns, "ShinseiNissu04", d.get("ShinseiNissu04")));
			csvStringRecord.addItem(d.get("ShinseiNissu02"), PJActionBase.getQuotation(columns, "ShinseiNissu02", d.get("ShinseiNissu02")));
			csvStringRecord.addItem(d.get("ShinseiNissu03"), PJActionBase.getQuotation(columns, "ShinseiNissu03", d.get("ShinseiNissu03")));
			csvStringRecord.addItem(d.get("ShinseiNissu05"), PJActionBase.getQuotation(columns, "ShinseiNissu05", d.get("ShinseiNissu05")));
			csvStringRecord.addItem(d.get("ShinseiNissu11"), PJActionBase.getQuotation(columns, "ShinseiNissu11", d.get("ShinseiNissu11")));
			csvStringRecord.addItem(d.get("ShinseiNissu06"), PJActionBase.getQuotation(columns, "ShinseiNissu06", d.get("ShinseiNissu06")));
			csvStringRecord.addItem(d.get("ShinseiNissu07"), PJActionBase.getQuotation(columns, "ShinseiNissu07", d.get("ShinseiNissu07")));
			csvStringRecord.addItem(d.get("KyujitsuNissu"), PJActionBase.getQuotation(columns, "KyujitsuNissu", d.get("KyujitsuNissu")));
			csvStringRecord.addItem(d.get("GoukeiNissu"), PJActionBase.getQuotation(columns, "GoukeiNissu", d.get("GoukeiNissu")));

			csvStringRecord.addItem(d.get("ShinseiJikan01"), PJActionBase.getQuotation(columns, "ShinseiJikan01", d.get("ShinseiJikan01")));
			csvStringRecord.addItem(d.get("ShinseiJikan04"), PJActionBase.getQuotation(columns, "ShinseiJikan04", d.get("ShinseiJikan04")));
			csvStringRecord.addItem(d.get("ShinseiJikan02"), PJActionBase.getQuotation(columns, "ShinseiJikan02", d.get("ShinseiJikan02")));
			csvStringRecord.addItem(d.get("ShinseiJikan03"), PJActionBase.getQuotation(columns, "ShinseiJikan03", d.get("ShinseiJikan03")));
			csvStringRecord.addItem(d.get("ShinseiJikan05"), PJActionBase.getQuotation(columns, "ShinseiJikan05", d.get("ShinseiJikan05")));
			csvStringRecord.addItem(d.get("ShinseiJikan11"), PJActionBase.getQuotation(columns, "ShinseiJikan11", d.get("ShinseiJikan11")));
			csvStringRecord.addItem(d.get("ShinseiJikan06"), PJActionBase.getQuotation(columns, "ShinseiJikan06", d.get("ShinseiJikan06")));
			csvStringRecord.addItem(d.get("ShinseiJikan07"), PJActionBase.getQuotation(columns, "ShinseiJikan07", d.get("ShinseiJikan07")));
			csvStringRecord.addItem(d.get("GoukeiJikan"), PJActionBase.getQuotation(columns, "GoukeiJikan", d.get("GoukeiJikan")));

			csvStringRecord.addItem(d.get("ShinseiTanka01"), PJActionBase.getQuotation(columns, "ShinseiTanka01", d.get("ShinseiTanka01")));
			csvStringRecord.addItem(d.get("ShinseiTanka04"), PJActionBase.getQuotation(columns, "ShinseiTanka04", d.get("ShinseiTanka04")));
			csvStringRecord.addItem(d.get("ShinseiTanka02"), PJActionBase.getQuotation(columns, "ShinseiTanka02", d.get("ShinseiTanka02")));
			csvStringRecord.addItem(d.get("ShinseiTanka03"), PJActionBase.getQuotation(columns, "ShinseiTanka03", d.get("ShinseiTanka03")));
			csvStringRecord.addItem(d.get("ShinseiTanka05"), PJActionBase.getQuotation(columns, "ShinseiTanka05", d.get("ShinseiTanka05")));
			csvStringRecord.addItem(d.get("ShinseiTanka11"), PJActionBase.getQuotation(columns, "ShinseiTanka11", d.get("ShinseiTanka11")));
			csvStringRecord.addItem(d.get("ShinseiTanka06"), PJActionBase.getQuotation(columns, "ShinseiTanka06", d.get("ShinseiTanka06")));
			csvStringRecord.addItem(d.get("ShinseiTanka09"), PJActionBase.getQuotation(columns, "ShinseiTanka09", d.get("ShinseiTanka09")));
			csvStringRecord.addItem(d.get("ShinseiTanka07"), PJActionBase.getQuotation(columns, "ShinseiTanka07", d.get("ShinseiTanka07")));

			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei01"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei01", d.get("ShinseiKingakuGoukei01")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei04"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei04", d.get("ShinseiKingakuGoukei04")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei02"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei02", d.get("ShinseiKingakuGoukei02")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei03"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei03", d.get("ShinseiKingakuGoukei03")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei05"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei05", d.get("ShinseiKingakuGoukei05")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei11"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei11", d.get("ShinseiKingakuGoukei11")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei06"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei06", d.get("ShinseiKingakuGoukei06")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei09"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei09", d.get("ShinseiKingakuGoukei09")));
			csvStringRecord.addItem(d.get("ShinseiKingakuGoukei07"), PJActionBase.getQuotation(columns, "ShinseiKingakuGoukei07", d.get("ShinseiKingakuGoukei07")));
			csvStringRecord.addItem(d.get("GoukeiKingaku"), PJActionBase.getQuotation(columns, "GoukeiKingaku", d.get("GoukeiKingaku")));
			
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("CsvChinginkeisanshoData_" + filenameformattedDateTime + ".csv");
	}
	
}