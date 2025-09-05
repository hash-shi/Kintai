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
		DateTimeFormatter formatter 		= DateTimeFormatter.ofPattern("yyyy/MM/dd");
		DateTimeFormatter filenameformat 	= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		
		// フォーマットに従って日時を文字列に変換
		String formattedDateTime 			= now.format(formatter);
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
		sql.append("	kihon.*, ");
		sql.append("		shain.ShainName, ");
		sql.append("		shain.EigyoshoCode, ");
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
		sql.append("	) AS KyujitsuNissu ");
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
			
			BigDecimal goukeiNissu = BigDecimal.ZERO;
			BigDecimal goukeiJikan = BigDecimal.ZERO;
			BigDecimal goukeiKingaku = BigDecimal.ZERO;
			
			// TaishoNenGetsudo の分割処理
			String nenGetsudo = d.get("TaishoNenGetsudo");
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
			csvStringRecord.addItem(nendo,true);
			csvStringRecord.addItem(getsudo,true);
			csvStringRecord.addItem(formattedDateTime,true);
			csvStringRecord.addItem(d.get( "ShainNO"), PJActionBase.getQuotation(columns, "ShainNO"));
			csvStringRecord.addItem(d.get( "ShainName"), PJActionBase.getQuotation(columns, "ShainName"));
			csvStringRecord.addItem(d.get( "EigyoshoCode"), PJActionBase.getQuotation(columns, "EigyoshoCode"));
			
			
			// 合計日数処理 + 出力
			String[] nissuKeys = {
				    "ShinseiNissu01", "ShinseiNissu04", "ShinseiNissu02",
				    "ShinseiNissu03", "ShinseiNissu05", "ShinseiNissu11",
				    "ShinseiNissu06", "ShinseiNissu07", "kyujitsuNissu"
				};

			for (String key : nissuKeys) {
				String val = data.get(i).get(key);
				if (StringUtils.isNotBlank(val)) {
					try {
						goukeiNissu = goukeiNissu.add(new BigDecimal(val));
					} catch (NumberFormatException e) {
					}
				}
		        csvStringRecord.addItem(
		                StringUtils.isNotBlank(val) ?
		                    String.format("%.2f",new BigDecimal(val)) :
		                    "0.00"
		            );
			}
			
			csvStringRecord.addItem(String.format("%.2f", goukeiNissu));
			
			// 合計時間処理 + 出力
			String[] jikanKeys = {
					"ShinseiJikan01", "ShinseiJikan04", "ShinseiJikan02",
					"ShinseiJikan03", "ShinseiJikan05", "ShinseiJikan11",
					"ShinseiJikan06", "ShinseiJikan07"
				};

			for (String key : jikanKeys) {
				String val = data.get(i).get(key);
				if (StringUtils.isNotBlank(val)) {
					try {
						goukeiJikan = goukeiJikan.add(new BigDecimal(val));
					} catch (NumberFormatException e) {
					}
				}
		        csvStringRecord.addItem(
		                StringUtils.isNotBlank(val) ?
		                	String.format("%.2f",new BigDecimal(val)) :
		                	"0.00"
		            );
			}
			
			csvStringRecord.addItem(String.format("%.2f", goukeiJikan));

			csvStringRecord.addItem(d.get( "ShinseiTanka01"),PJActionBase.getQuotation(columns, "ShinseiTanka01"));
			csvStringRecord.addItem(d.get( "ShinseiTanka04"),PJActionBase.getQuotation(columns, "ShinseiTanka04"));
			csvStringRecord.addItem(d.get( "ShinseiTanka02"),PJActionBase.getQuotation(columns, "ShinseiTanka02"));
			csvStringRecord.addItem(d.get( "ShinseiTanka03"),PJActionBase.getQuotation(columns, "ShinseiTanka03"));
			csvStringRecord.addItem(d.get( "ShinseiTanka05"),PJActionBase.getQuotation(columns, "ShinseiTanka05"));
			csvStringRecord.addItem(d.get( "ShinseiTanka11"),PJActionBase.getQuotation(columns, "ShinseiTanka11"));
			csvStringRecord.addItem(d.get( "ShinseiTanka06"),PJActionBase.getQuotation(columns, "ShinseiTanka06"));
			csvStringRecord.addItem(d.get( "ShinseiTanka09"),PJActionBase.getQuotation(columns, "ShinseiTanka09"));
			csvStringRecord.addItem(d.get( "ShinseiTanka07"),PJActionBase.getQuotation(columns, "ShinseiTanka07"));

			// 合計金額計算
			String[] kingakuKeys = {
					"ShinseiKingakuGoukei01", "ShinseiKingakuGoukei04", "ShinseiKingakuGoukei02",
					"ShinseiKingakuGoukei03", "ShinseiKingakuGoukei05", "ShinseiKingakuGoukei11",
					"ShinseiKingakuGoukei06", "ShinseiKingakuGoukei09", "ShinseiKingakuGoukei07"
				};

			for (String key : kingakuKeys) {
				String val = data.get(i).get(key);
				if (StringUtils.isNotBlank(val)) {
					try {
						goukeiKingaku = goukeiKingaku.add(new BigDecimal(val));
					} catch (NumberFormatException e) {
					}
				}
				csvStringRecord.addItem(val);
			}
			
			csvStringRecord.addItem(goukeiKingaku.toPlainString());
			
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("CsvChinginkeisanshoData_" + filenameformattedDateTime + ".csv");
	}
	
}