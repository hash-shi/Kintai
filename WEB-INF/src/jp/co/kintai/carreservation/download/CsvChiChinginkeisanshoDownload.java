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
		String fromTaishoNengetsu	= req.getParameter("srhTxtTaishoNengetsuF");
		String toTaishoNengetsu		= req.getParameter("srhTxtTaishoNengetsuT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		String joken				= req.getParameter("srhSelJoken");
		String output				= req.getParameter("srhRdoOutput");
		
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
		sql.append(" 	,TaishoNengappi AS TaishoNengappi ");
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
		
		if (output == "02") {
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
		csvStringTitle.addItem( "営業所コード");
		csvStringTitle.addItem( "営業所名");
		csvStringTitle.addItem( "部署コード");
		csvStringTitle.addItem( "部署名");
		csvStringTitle.addItem( "社員NO");
		csvStringTitle.addItem( "社員名");
		csvStringTitle.addItem( "勤務開始時刻");
		csvStringTitle.addItem( "勤務終了時刻");
		csvStringTitle.addItem( "対象年月日");
		csvStringTitle.addItem( "曜日区分");
		csvStringTitle.addItem( "出社時刻");
		csvStringTitle.addItem( "退社時刻");
		csvStringTitle.addItem( "実働時刻");
		csvStringTitle.addItem( "賃金申請区分1");
		csvStringTitle.addItem( "賃金申請時間1");
		csvStringTitle.addItem( "賃金申請区分2");
		csvStringTitle.addItem( "賃金申請時間2");
		csvStringTitle.addItem( "通勤日日数");
		csvStringTitle.addItem( "通勤日単価");
		csvStringTitle.addItem( "特記事項");
			
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
			
		// 明細部の設定
		count = data.size();
		for (int i = 0; i < count; i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			csvStringRecord.addItem(data.get(i).get("TaishoNendo"));
			csvStringRecord.addItem(data.get(i).get("TaishoGetsudo"));
			csvStringRecord.addItem(data.get(i).get("SakuseiDate"));
			csvStringRecord.addItem(data.get(i).get("EigyoshoCode"));
			csvStringRecord.addItem(data.get(i).get("EigyoshoName"));
			csvStringRecord.addItem(data.get(i).get("BushoCode"));
			csvStringRecord.addItem(data.get(i).get("BushoName"));
			csvStringRecord.addItem(data.get(i).get("ShainNO"));
			csvStringRecord.addItem(data.get(i).get("ShainName"));
			csvStringRecord.addItem(data.get(i).get("KinmuKaishiJikoku"));
			csvStringRecord.addItem(data.get(i).get("KinmuShuryoJikoku"));
			csvStringRecord.addItem(data.get(i).get("TaishoNengappi"));
			csvStringRecord.addItem(data.get(i).get("YobiKbn"));
			csvStringRecord.addItem(data.get(i).get("ShusshaJikoku"));
			csvStringRecord.addItem(data.get(i).get("TaishaJikoku"));
			csvStringRecord.addItem(data.get(i).get("JitsudoJikan"));
			csvStringRecord.addItem(data.get(i).get("ChinginShinseiKbn1"));
			csvStringRecord.addItem(data.get(i).get("ChinginShinseiJikan1"));
			csvStringRecord.addItem(data.get(i).get("ChinginShinseiKbn2"));
			csvStringRecord.addItem(data.get(i).get("ChinginShinseiJikan2"));
			csvStringRecord.addItem(data.get(i).get("TsukinhiNissu"));
			csvStringRecord.addItem(data.get(i).get("TsukinhiTanka"));
			csvStringRecord.addItem(data.get(i).get("TokkiJiko"));
			
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("csvChiChinginkeisansho_" + formattedDateTime + ".csv");
		
	}
}