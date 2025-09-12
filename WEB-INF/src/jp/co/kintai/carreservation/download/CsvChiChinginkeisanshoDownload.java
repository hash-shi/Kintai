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
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CsvChiChinginkeisanshoDownload extends DownloadBase {
	
	public CsvChiChinginkeisanshoDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		int count = 0;
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		HashMap<String, String> columns = new HashMap<String, String>();
		String fromTaishoNengetsu	= req.getParameter("srhTxtTaishoNengetsuF");
		String toTaishoNengetsu		= req.getParameter("srhTxtTaishoNengetsuT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		String joken				= req.getParameter("srhSelJoken");
		String order				= req.getParameter("srhRdoOrder");
		
		// パラメータを取得した場合は、0詰め処理を行う
		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
			// 数値に変換
			int fromEigyoshoCode_ = Integer.parseInt(fromEigyoshoCode);
			// 3桁0詰めに変換
			fromEigyoshoCode = String.format("%03d", fromEigyoshoCode_);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
			// 数値に変換
			int toEigyoshoCode_ = Integer.parseInt(toEigyoshoCode);
			// 3桁0詰めに変換
			toEigyoshoCode = String.format("%03d", toEigyoshoCode_);
		}
				
		if (StringUtils.isNotBlank(fromBushoCode)) {
			// 数値に変換
			int fromBushoCode_ = Integer.parseInt(fromBushoCode);
			// 4桁0詰めに変換
			fromBushoCode = String.format("%04d", fromBushoCode_);
		}
		
		if (StringUtils.isNotBlank(toBushoCode)) {
			// 数値に変換
			int toBushoCode_ = Integer.parseInt(toBushoCode);
			// 4桁0詰めに変換
			toBushoCode = String.format("%04d", toBushoCode_);
		}
		
		if (StringUtils.isNotBlank(fromShainNo)) {
			// 数値に変換
			int fromShainNo_ = Integer.parseInt(fromShainNo);
			// 4桁0詰めに変換
			fromShainNo = String.format("%04d", fromShainNo_);
		}
		
		if (StringUtils.isNotBlank(toShainNo)) {
			// 数値に変換
			int toShainNo_ = Integer.parseInt(toShainNo);
			// 4桁0詰めに変換
			toShainNo = String.format("%04d", toShainNo_);
		}
		
		// ログインユーザが処理可能な営業所コードの取得
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		ArrayList<String> shoriKanoEigyoshoCode = userInformation.getShoriKanoEigyoshoCode();
		
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
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// データ取得
		//=====================================================================						
		
		sql.append(" SELECT ");
		sql.append(" 	 LEFT(K.TaishoNenGetsudo, 4) AS TaishoNendo ");
		sql.append(" 	,RIGHT(K.TaishoNenGetsudo, 2) AS TaishoGetsudo ");
		sql.append(" 	,CONVERT(varchar,GETDATE(),111) AS SakuseiDate ");
		sql.append(" 	,E.EigyoshoCode AS EigyoshoCode ");
		sql.append(" 	,E.EigyoshoName AS EigyoshoName ");
		sql.append(" 	,B.BushoCode AS BushoCode ");
		sql.append(" 	,B.BushoName AS BushoName ");
		sql.append(" 	,M.ShainNO AS ShainNO ");
		sql.append(" 	,S.ShainName AS ShainName ");
		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN S.KinmuKaishiJi NOT IN ('', '00') ");
		sql.append(" 		THEN S.KinmuKaishiJi + ':' + S.KinmuKaishiFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KinmuKaishiJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN S.KinmuShuryoJi NOT IN ('', '00') ");		
		sql.append(" 		THEN S.KinmuShuryoJi + ':' + S.KinmuShuryoFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KinmuShuryoJikoku ");
		sql.append(" 	,S.KeiyakuJitsudoJikan AS KeiyakuJitsudoJikan ");
		sql.append(" 	,M.TaishoNengappi AS TaishoNengappi ");
		sql.append(" 	,M.YobiKbn AS YobiKbn ");
		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ShusshaJi NOT IN ('', '00') ");
		sql.append(" 		THEN M.ShusshaJi + ':' + M.ShusshaFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS ShusshaJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.TaishaJi  NOT IN ('', '00') ");
		sql.append(" 		THEN M.TaishaJi  + ':' + M.TaishaFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS TaishaJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ShusshaJi NOT IN ('', '00') ");
		sql.append(" 		THEN M.JitsudoJikan ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS JitsudoJikan ");
		
		sql.append(" 	,COALESCE(K0201A.KbnName, '') AS ChinginShinseiKbn1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ChinginShinseiKbn1 NOT IN ('', '00') ");		
		sql.append(" 		THEN M.ChinginShinseiJikan1 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS ChinginShinseiJikan1 ");
		
		sql.append(" 	,COALESCE(K0201B.KbnName, '') AS ChinginShinseiKbn2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ChinginShinseiKbn2 NOT IN ('', '00') ");		
		sql.append(" 		THEN M.ChinginShinseiJikan2 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS ChinginShinseiJikan2 ");
		
		sql.append(" 	,COALESCE(K0201C.KbnName, '') AS ChinginShinseiKbn3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ChinginShinseiKbn3 NOT IN ('', '00') ");		
		sql.append(" 		THEN M.ChinginShinseiJikan3 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS ChinginShinseiJikan3 ");
		
		sql.append(" 	,CAST(K.ShinseiNissu09 AS VARCHAR) AS TsukinhiNissu ");
		sql.append(" 	,CAST(K.ShinseiTanka09 AS VARCHAR) AS TsukinhiTanka ");
		
		sql.append(" 	,COALESCE(K.TokkiJiko, '') AS TokkiJiko ");
				
		sql.append(" FROM ");
		sql.append(" 	CHI_CHINGINKEISANSHO_KIHON K ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	CHI_CHINGINKEISANSHO_MEISAI M ");
		sql.append(" ON ");
		sql.append(" 	K.TaishoNenGetsudo = M.TaishoNenGetsudo ");
		sql.append(" AND ");
		sql.append(" 	K.ShainNO = M.ShainNO ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_SHAIN S ");
		sql.append(" ON ");
		sql.append(" 	S.ShainNO = M.ShainNO");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_EIGYOSHO E ");
		sql.append(" ON ");
		sql.append(" 	S.EigyoshoCode = E.EigyoshoCode ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_BUSHO B ");
		sql.append(" ON ");
		sql.append(" 	S.BushoCode = B.BushoCode ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0050 ");
		sql.append(" ON ");
		sql.append(" 	K0050.KbnCode = '0050' ");
		sql.append(" AND ");
		sql.append(" 	K0050.Code = K.KakuteiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0201A ");
		sql.append(" ON ");
		sql.append(" 	K0201A.KbnCode = '0201' ");
		sql.append(" AND ");
		sql.append(" 	K0201A.Code = M.ChinginShinseiKbn1 ");
		sql.append(" AND ");
		sql.append(" 	K0201A.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0201B ");
		sql.append(" ON ");
		sql.append(" 	K0201B.KbnCode = '0201' ");
		sql.append(" AND ");
		sql.append(" 	K0201B.Code = M.ChinginShinseiKbn2 ");
		sql.append(" AND ");
		sql.append(" 	K0201B.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0201C ");
		sql.append(" ON ");
		sql.append(" 	K0201C.KbnCode = '0201' ");
		sql.append(" AND ");
		sql.append(" 	K0201C.Code = M.ChinginShinseiKbn3 ");
		sql.append(" AND ");
		sql.append(" 	K0201C.Code <> '00' ");
		
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		
		if (StringUtils.isNotBlank(fromTaishoNengetsu)) {
			sql.append(" AND K.TaishoNenGetsudo >=  ? ");
			pstmtf.addValue("String", fromTaishoNengetsu);
		}
		
		if (StringUtils.isNotBlank(toTaishoNengetsu)) {
			sql.append(" AND K.TaishoNenGetsudo <=  ? ");
			pstmtf.addValue("String", toTaishoNengetsu);
		}
		
		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
			sql.append(" AND CAST(E.EigyoshoCode AS int) >=  ? ");
			pstmtf.addValue("String", fromEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
			sql.append(" AND CAST(E.EigyoshoCode AS int) <=  ? ");
			pstmtf.addValue("String", toEigyoshoCode);
		}
		
		// 処理可能営業所コードがあるか判定
		if (0 < shoriKanoEigyoshoCode.size()) {
		   sql.append(" AND CAST(E.EigyoshoCode AS int) in ( ");
		   // 処理可能営業所コード分繰り返す
		   for (int i = 0; i < shoriKanoEigyoshoCode.size(); i++) {
		     // 最初の1回目のみ,がいらない。
		     if (i == 0) { sql.append(" ? "); } else { sql.append(" , ? "); }
		     // パラメータセット
		     pstmtf.addValue("String", shoriKanoEigyoshoCode.get(i));
		   }
		   sql.append(" ) ");
		}
		
		if (StringUtils.isNotBlank(fromBushoCode)) {
			sql.append(" AND CAST(B.BushoCode AS int) >=  ? ");
			pstmtf.addValue("String", fromBushoCode);
		}
		
		if (StringUtils.isNotBlank(toBushoCode)) {
			sql.append(" AND CAST(B.BushoCode AS int) <=  ? ");
			pstmtf.addValue("String", toBushoCode);
		}
		
		if (StringUtils.isNotBlank(fromShainNo)) {
			sql.append(" AND CAST(K.ShainNO AS int) >=  ? ");
			pstmtf.addValue("String", fromShainNo);
		}
		
		if (StringUtils.isNotBlank(toShainNo)) {
			sql.append(" AND CAST(K.ShainNO AS int) <=  ? ");
			pstmtf.addValue("String", toShainNo);
		}
		
		if (StringUtils.isNotBlank(joken)) {
			sql.append(" AND CAST(K.KakuteiKbn AS int) =  ? ");
			pstmtf.addValue("String", joken);
		}
		
		sql.append(" ORDER BY ");
		sql.append("     K.TaishoNenGetsudo ");
		
		if ("02".equals(order)) {
			sql.append("     ,E.EigyoshoCode ");
		}
		
		sql.append("     ,K.ShainNO ");
		sql.append("     ,B.BushoCode ");
		sql.append("     ,M.TaishoNengappi ");
		
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
		csvStringTitle.addItem( "営業所コード",true);
		csvStringTitle.addItem( "営業所名",true);
		csvStringTitle.addItem( "部署コード",true);
		csvStringTitle.addItem( "部署名",true);
		csvStringTitle.addItem( "社員NO",true);
		csvStringTitle.addItem( "社員名",true);
		csvStringTitle.addItem( "勤務開始時刻",true);
		csvStringTitle.addItem( "勤務終了時刻",true);
		csvStringTitle.addItem( "勤務実働時間",true);
		csvStringTitle.addItem( "年月日",true);
		csvStringTitle.addItem( "曜日",true);
		csvStringTitle.addItem( "出社時間",true);
		csvStringTitle.addItem( "退社時間",true);
		csvStringTitle.addItem( "実働時間",true);
		csvStringTitle.addItem( "賃金申請区分1",true);
		csvStringTitle.addItem( "賃金申請時間1",true);
		csvStringTitle.addItem( "賃金申請区分2",true);
		csvStringTitle.addItem( "賃金申請時間2",true);
		csvStringTitle.addItem( "賃金申請区分3",true);
		csvStringTitle.addItem( "賃金申請時間3",true);
		csvStringTitle.addItem( "通勤費日数",true);
		csvStringTitle.addItem( "通勤費単価",true);
		csvStringTitle.addItem( "特記事項",true);
			
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
			
		// 明細部の設定
		count = data.size();
		for (int i = 0; i < count; i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			
			// 1行取得
			HashMap<String, String> d = data.get(i);
			
			csvStringRecord.addItem(d.get("TaishoNendo"), PJActionBase.getQuotation(columns, "TaishoNendo", d.get("TaishoNendo")));
			csvStringRecord.addItem(d.get("TaishoGetsudo"), PJActionBase.getQuotation(columns, "TaishoGetsudo", d.get("TaishoGetsudo")));
			csvStringRecord.addItem(d.get("SakuseiDate"), PJActionBase.getQuotation(columns, "SakuseiDate", d.get("SakuseiDate")));
			csvStringRecord.addItem(d.get("EigyoshoCode"), PJActionBase.getQuotation(columns, "EigyoshoCode", d.get("EigyoshoCode")));
			csvStringRecord.addItem(d.get("EigyoshoName"), PJActionBase.getQuotation(columns, "EigyoshoName", d.get("EigyoshoName")));
			csvStringRecord.addItem(d.get("BushoCode"), PJActionBase.getQuotation(columns, "BushoCode", d.get("BushoCode")));
			csvStringRecord.addItem(d.get("BushoName"), PJActionBase.getQuotation(columns, "BushoName", d.get("BushoName")));
			csvStringRecord.addItem(d.get("ShainNO"), PJActionBase.getQuotation(columns, "ShainNO", d.get("ShainNO")));
			csvStringRecord.addItem(d.get("ShainName"), PJActionBase.getQuotation(columns, "ShainName", d.get("ShainName")));
			csvStringRecord.addItem(d.get("KinmuKaishiJikoku"), PJActionBase.getQuotation(columns, "KinmuKaishiJikoku", d.get("KinmuKaishiJikoku")));
			csvStringRecord.addItem(d.get("KinmuShuryoJikoku"), PJActionBase.getQuotation(columns, "KinmuShuryoJikoku", d.get("KinmuShuryoJikoku")));
			csvStringRecord.addItem(d.get("KeiyakuJitsudoJikan"), PJActionBase.getQuotation(columns, "KeiyakuJitsudoJikan", d.get("KeiyakuJitsudoJikan")));
			csvStringRecord.addItem(d.get("TaishoNengappi"), PJActionBase.getQuotation(columns, "TaishoNengappi", d.get("TaishoNengappi")));
			csvStringRecord.addItem(d.get("YobiKbn"), PJActionBase.getQuotation(columns, "YobiKbn", d.get("YobiKbn")));
			csvStringRecord.addItem(d.get("ShusshaJikoku"), PJActionBase.getQuotation(columns, "ShusshaJikoku", d.get("ShusshaJikoku")));
			csvStringRecord.addItem(d.get("TaishaJikoku"), PJActionBase.getQuotation(columns, "TaishaJikoku", d.get("TaishaJikoku")));
			csvStringRecord.addItem(d.get("JitsudoJikan"), PJActionBase.getQuotation(columns, "JitsudoJikan", d.get("JitsudoJikan")));
			csvStringRecord.addItem(d.get("ChinginShinseiKbn1"), PJActionBase.getQuotation(columns, "ChinginShinseiKbn1", d.get("ChinginShinseiKbn1")));
			csvStringRecord.addItem(d.get("ChinginShinseiJikan1"), PJActionBase.getQuotation(columns, "ChinginShinseiJikan1", d.get("ChinginShinseiJikan1")));
			csvStringRecord.addItem(d.get("ChinginShinseiKbn2"), PJActionBase.getQuotation(columns, "ChinginShinseiKbn2", d.get("ChinginShinseiKbn2")));
			csvStringRecord.addItem(d.get("ChinginShinseiJikan2"), PJActionBase.getQuotation(columns, "ChinginShinseiJikan2", d.get("ChinginShinseiJikan2")));
			csvStringRecord.addItem(d.get("ChinginShinseiKbn3"), PJActionBase.getQuotation(columns, "ChinginShinseiKbn3", d.get("ChinginShinseiKbn3")));
			csvStringRecord.addItem(d.get("ChinginShinseiJikan3"), PJActionBase.getQuotation(columns, "ChinginShinseiJikan3", d.get("ChinginShinseiJikan3")));
			csvStringRecord.addItem(d.get("TsukinhiNissu"), PJActionBase.getQuotation(columns, "TsukinhiNissu", d.get("TsukinhiNissu")));
			csvStringRecord.addItem(d.get("TsukinhiTanka"), PJActionBase.getQuotation(columns, "TsukinhiTanka", d.get("TsukinhiTanka")));
			csvStringRecord.addItem(d.get("TokkiJiko"), PJActionBase.getQuotation(columns, "TokkiJiko", d.get("TokkiJiko")));
			
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("csvChiChinginkeisansho_" + formattedDateTime + ".csv");
		
	}
}