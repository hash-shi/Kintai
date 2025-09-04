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

public class CsvKinShukkinBoDownload extends DownloadBase {
	
	public CsvKinShukkinBoDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
		sql.append(" 	,CONVERT(NVARCHAR, CURRENT_TIMESTAMP ,111) AS SakuseiDate ");
		sql.append(" 	,M.ShainNO AS ShainNO ");
		sql.append(" 	,S.ShainName AS ShainName ");
		sql.append(" 	,RIGHT(M.TaishoNengappi, 5) AS TsukiHi ");
		
		sql.append(" 	,COALESCE(K0051.KbnName, '') AS ShukkinYoteiKbn ");
		sql.append(" 	,COALESCE(M0100.KbnName, '') AS KintaiKbn ");
		
		sql.append(" 	,COALESCE(K0101A.KbnName, '') AS KintaiShinseiKbn1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi1 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi1 + ':' + M.KintaiShinseiKaishiFun1 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi1 NOT IN ('', '00') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi1 + ':' + M.KintaiShinseiShuryoFun1 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn1 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiJikan1 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS KintaiShinseiJikan1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn1 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiKyukeiJikan1 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan1 ");
		
		sql.append(" 	,COALESCE(K0101B.KbnName, '') AS KintaiShinseiKbn2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi2 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi2 + ':' + M.KintaiShinseiKaishiFun2 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi2 NOT IN ('', '00') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi2 + ':' + M.KintaiShinseiShuryoFun2 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn2 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiJikan2 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS KintaiShinseiJikan2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn2 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiKyukeiJikan2 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan2 ");
		
		sql.append(" 	,COALESCE(K0101B.KbnName, '') AS KintaiShinseiKbn3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi3 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi3 + ':' + M.KintaiShinseiKaishiFun3 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi3 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiShuryoJi3 + ':' + M.KintaiShinseiShuryoFun3 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn3 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiJikan3 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS KintaiShinseiJikan3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn3 NOT IN ('', '00') ");
		sql.append(" 		THEN M.KintaiShinseiKyukeiJikan3 ");
		sql.append(" 		ELSE 0 ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan3 ");
		
		sql.append(" 	,COALESCE(M.KintaiShinseiBiko, '') AS KintaiShinseiBiko");
		
		sql.append(" FROM ");
		sql.append(" 	KIN_SHUKKINBO_KIHON K ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI M ");
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
		sql.append(" 	MST_KUBUN K0051 ");
		sql.append(" ON ");
		sql.append(" 	K0051.KbnCode = '0051' ");
		sql.append(" AND ");
		sql.append(" 	K0051.Code = M.ShukkinYoteiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN M0100 ");
		sql.append(" ON ");
		sql.append(" 	M0100.KbnCode = '0100' ");
		sql.append(" AND ");
		sql.append(" 	M0100.Code = M.KintaiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0101A ");
		sql.append(" ON ");
		sql.append(" 	K0101A.KbnCode = '0101' ");
		sql.append(" AND ");
		sql.append(" 	K0101A.Code = M.KintaiShinseiKbn1 ");
		sql.append(" AND ");
		sql.append(" 	K0101A.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0101B ");
		sql.append(" ON ");
		sql.append(" 	K0101B.KbnCode = '0101' ");
		sql.append(" AND ");
		sql.append(" 	K0101B.Code = M.KintaiShinseiKbn2 ");
		sql.append(" AND ");
		sql.append(" 	K0101B.Code <> '00' ");
		
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
		csvStringTitle.addItem( "社員NO");
		csvStringTitle.addItem( "社員名");
		csvStringTitle.addItem( "月日");
		csvStringTitle.addItem( "出勤予定区分");
		csvStringTitle.addItem( "勤怠区分");
		csvStringTitle.addItem( "勤怠申請区分1");
		csvStringTitle.addItem( "勤怠申請開始時間1");
		csvStringTitle.addItem( "勤怠申請終了時間1");
		csvStringTitle.addItem( "勤怠申請申請時間1");
		csvStringTitle.addItem( "勤怠申請休憩時間1");
		csvStringTitle.addItem( "勤怠申請区分2");
		csvStringTitle.addItem( "勤怠申請開始時間2");
		csvStringTitle.addItem( "勤怠申請終了時間2");
		csvStringTitle.addItem( "勤怠申請申請時間2");
		csvStringTitle.addItem( "勤怠申請休憩時間2");
		csvStringTitle.addItem( "勤怠申請区分3");
		csvStringTitle.addItem( "勤怠申請開始時間3");
		csvStringTitle.addItem( "勤怠申請終了時間3");
		csvStringTitle.addItem( "勤怠申請申請時間3");
		csvStringTitle.addItem( "勤怠申請休憩時間3");
		csvStringTitle.addItem( "勤怠申請備考");
			
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
			csvStringRecord.addItem(data.get(i).get("ShainNO"));
			csvStringRecord.addItem(data.get(i).get("ShainName"));
			csvStringRecord.addItem(data.get(i).get("TsukiHi"));
			csvStringRecord.addItem(data.get(i).get("ShukkinYoteiKbn"));
			csvStringRecord.addItem(data.get(i).get("KintaiKbn"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKbn1"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKaishiJikoku1"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiShuryoJikoku1"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiJikan1"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKyukeiJikan1"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKbn2"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKaishiJikoku2"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiShuryoJikoku2"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiJikan2"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKyukeiJikan2"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKbn3"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKaishiJikoku3"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiShuryoJikoku3"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiJikan3"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiKyukeiJikan3"));
			csvStringRecord.addItem(data.get(i).get("KintaiShinseiBiko"));
			
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("CsvKinShukkinBo_" + formattedDateTime + ".csv");
		
	}
	
}