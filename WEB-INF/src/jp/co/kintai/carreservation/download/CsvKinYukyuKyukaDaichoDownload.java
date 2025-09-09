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

public class CsvKinYukyuKyukaDaichoDownload extends DownloadBase {
	
	public CsvKinYukyuKyukaDaichoDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
		String fromTaishoNendo		= req.getParameter("srhTxtTaishoNendoF");
		String toTaishoNendo		= req.getParameter("srhTxtTaishoNendoT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		String order				= req.getParameter("srhRdoOrder");
		
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
		
		sql.append(" WITH ");
		sql.append(" CTE_MAIN AS ");
		sql.append(" (");
		
		sql.append("  SELECT ");
		sql.append(" 	Q2.RowNumber ");
		sql.append(" 	,Q1.TaishoNendo ");
		sql.append(" 	,Q1.ShainNO ");
		sql.append(" 	,Q1.ShainName ");
		sql.append(" 	,Q1.EigyoshoCode ");
		sql.append(" 	,Q1.EigyoshoName ");
		sql.append(" 	,Q1.BushoName ");
		sql.append(" 	,Q1.YukyuKyukaFuyoNissu ");
		sql.append(" 	,ISNULL(Q2.[month], Q1.[month]) AS [month] ");
		sql.append(" 	,ISNULL(Q2.[day], Q1.[day]) AS [day] ");
		sql.append(" 	,ISNULL(Q2.[hankyu], Q1.[hankyu]) AS [hankyu] ");
		sql.append("  FROM ");
		sql.append("  ( ");
		
		// 対象年度FROMからTOまでの年をループ
		for (int nendo = Integer.parseInt(fromTaishoNendo); nendo <= Integer.parseInt(toTaishoNendo); nendo++) {
			// 最初のループでない場合
			if (nendo != Integer.parseInt(fromTaishoNendo)) { sql.append(" UNION ALL "); }
			
			sql.append("  	SELECT DISTINCT ");
			sql.append(" 		CAST(" + nendo + " AS VARCHAR) AS TaishoNendo");
			sql.append(" 		,S.ShainNO ");
			sql.append(" 		,S.ShainName ");
			sql.append(" 		,ISNULL(E.EigyoshoCode, '') AS EigyoshoCode ");
			sql.append(" 		,ISNULL(E.EigyoshoName, '') AS EigyoshoName ");
			sql.append(" 		,ISNULL(B.BushoName, '') AS BushoName ");
			sql.append(" 		,COALESCE(Y.YukyuKyukaFuyoNissu, S.YukyuKyukaFuyoNissu) AS YukyuKyukaFuyoNissu ");
			sql.append(" 		,'' AS [month] ");
			sql.append(" 		,'' AS [day] ");
			sql.append(" 		,'' AS [hankyu] ");
			sql.append("  	FROM ");
			sql.append("  		MST_SHAIN S ");
			sql.append("  	LEFT OUTER JOIN ");
			sql.append("  		MST_EIGYOSHO E ");
			sql.append("  	ON ");
			sql.append("  		S.EigyoshoCode = E.EigyoshoCode ");
			sql.append("  	LEFT OUTER JOIN ");
			sql.append("  		MST_BUSHO B ");
			sql.append("  	ON ");
			sql.append("  		S.BushoCode = B.BushoCode ");
			sql.append("  	LEFT OUTER JOIN ");
			sql.append("  		KIN_YUKYU_KYUKA_DAICHO Y ");
			sql.append("  	ON ");
			sql.append("  		S.ShainNO = Y.ShainNO ");
			sql.append("  	AND ");	
			sql.append("  		CAST(Y.TaishoNendo AS int) = " + nendo );
			sql.append("  	WHERE ");
			sql.append("  		S.TaisyokuDate = '' ");
			
			if (StringUtils.isNotBlank(fromShainNo)) {
				sql.append(" AND CAST(S.ShainNO AS int) >=  ? ");
				pstmtf.addValue("String", fromShainNo);
			}
			
			if (StringUtils.isNotBlank(toShainNo)) {
				sql.append(" AND CAST(S.ShainNO AS int) <=  ? ");
				pstmtf.addValue("String", toShainNo);
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
		}
		
		sql.append("  ) Q1 ");
		
		sql.append("  LEFT OUTER JOIN ");
		sql.append("  ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		ROW_NUMBER() OVER ( ");
		sql.append(" 			PARTITION BY ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 				THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 	 		END ");
		sql.append(" 			,M.ShainNO ");
		sql.append(" 			ORDER BY M.TaishoNengappi ");
		sql.append(" 			) AS RowNumber ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 		THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 		ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 	 END AS TaishoNendo ");
		sql.append(" 	 ,E.EigyoshoCode ");
		sql.append(" 	 ,E.EigyoshoName ");
		sql.append(" 	 ,B.BushoName ");
		sql.append(" 	 ,M.ShainNO ");
		sql.append(" 	 ,S.ShainName ");
		sql.append(" 	 ,COALESCE(Y.YukyuKyukaFuyoNissu, S.YukyuKyukaFuyoNissu) AS YukyuKyukaFuyoNissu ");
		sql.append(" 	 ,SUBSTRING(M.TaishoNengappi, 6, 2) AS [month] ");
		sql.append(" 	 ,SUBSTRING(M.TaishoNengappi, 9, 2) AS [day] ");
		sql.append(" 	 ,CASE ");
		sql.append(" 	 	WHEN M.KintaiKbn = '05' ");
		sql.append(" 	 	THEN '半休'  ");
		sql.append(" 	 	ELSE '' ");
		sql.append(" 	 END AS [hankyu] ");
		sql.append(" 	FROM ");
		sql.append("  		KIN_SHUKKINBO_KIHON K ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		KIN_SHUKKINBO_MEISAI M ");
		sql.append("  	ON ");
		sql.append("  		K.TaishoNenGetsudo = M.TaishoNenGetsudo ");
		sql.append("  	AND ");			
		sql.append("  		K.ShainNO = M.ShainNO ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		MST_SHAIN S ");
		sql.append("  	ON ");
		sql.append("  		S.ShainNO = M.ShainNO ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		MST_EIGYOSHO E ");
		sql.append("  	ON ");
		sql.append("  		S.EigyoshoCode = E.EigyoshoCode ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		MST_BUSHO B ");
		sql.append("  	ON ");
		sql.append("  		S.BushoCode = B.BushoCode ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		KIN_YUKYU_KYUKA_DAICHO Y ");
		sql.append("  	ON ");
		sql.append("  		S.ShainNO = Y.ShainNO ");
		sql.append("  	AND ");
		sql.append("  		CASE ");
		sql.append("  			WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append("  			THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append("  			ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append("  		END = Y.TaishoNendo ");
		sql.append("  	WHERE ");
		sql.append("  		S.TaisyokuDate = '' ");
		sql.append("  		AND M.KintaiKbn IN ('04', '05') ");
		sql.append(" 		AND S.ShainKbn <> '04' ");
		
		if (StringUtils.isNotBlank(fromTaishoNendo)) {
			sql.append(" AND CASE ");
			sql.append(" 	WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(M.TaishoNenGetsudo, 4) ");
			sql.append(" END >= ?");
			pstmtf.addValue("String", fromTaishoNendo);
		}
		
		if (StringUtils.isNotBlank(toTaishoNendo)) {
			sql.append(" AND CASE ");
			sql.append(" 	WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(M.TaishoNenGetsudo, 4) ");
			sql.append(" END <= ?");
			pstmtf.addValue("String", toTaishoNendo);
		}
		
		if (StringUtils.isNotBlank(fromShainNo)) {
			sql.append(" AND CAST(K.ShainNO AS int) >=  ? ");
			pstmtf.addValue("String", fromShainNo);
		}
		
		if (StringUtils.isNotBlank(toShainNo)) {
			sql.append(" AND CAST(K.ShainNO AS int) <=  ? ");
			pstmtf.addValue("String", toShainNo);
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
			
		sql.append(" 	UNION ALL ");
		
		sql.append("  	SELECT ");
		sql.append(" 		ROW_NUMBER() OVER ( ");
		sql.append(" 			PARTITION BY ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 				THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 	 		END ");
		sql.append(" 			,M.ShainNO ");
		sql.append(" 			ORDER BY M.TaishoNengappi ");
		sql.append(" 			) AS RowNumber ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 		THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 		ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 	 END AS TaishoNendo ");
		sql.append(" 	 ,E.EigyoshoCode ");
		sql.append(" 	 ,E.EigyoshoName ");
		sql.append(" 	 ,B.BushoName ");
		sql.append(" 	 ,M.ShainNO ");
		sql.append(" 	 ,S.ShainName ");
		sql.append(" 	 ,COALESCE(Y.YukyuKyukaFuyoNissu, S.YukyuKyukaFuyoNissu) AS YukyuKyukaFuyoNissu ");
		sql.append(" 	 ,SUBSTRING(M.TaishoNengappi, 6, 2) AS [month] ");
		sql.append(" 	 ,SUBSTRING(M.TaishoNengappi, 9, 2) AS [day] ");
		sql.append(" 	 ,CASE ");
		sql.append(" 	 	WHEN M.ChinginKbn = '06' ");
		sql.append(" 	 	THEN '半休'  ");
		sql.append(" 	 	ELSE '' ");
		sql.append(" 	 END AS [hankyu] ");
		sql.append(" 	FROM ");
		sql.append("  		CHI_CHINGINKEISANSHO_KIHON K ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		CHI_CHINGINKEISANSHO_MEISAI M ");
		sql.append("  	ON ");
		sql.append("  		K.TaishoNenGetsudo = M.TaishoNenGetsudo ");
		sql.append("  	AND ");			
		sql.append("  		K.ShainNO = M.ShainNO ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		MST_SHAIN S ");
		sql.append("  	ON ");
		sql.append("  		S.ShainNO = M.ShainNO ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		MST_EIGYOSHO E ");
		sql.append("  	ON ");
		sql.append("  		S.EigyoshoCode = E.EigyoshoCode ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		MST_BUSHO B ");
		sql.append("  	ON ");
		sql.append("  		S.BushoCode = B.BushoCode ");
		sql.append("  	LEFT OUTER JOIN ");
		sql.append("  		KIN_YUKYU_KYUKA_DAICHO Y ");
		sql.append("  	ON ");
		sql.append("  		S.ShainNO = Y.ShainNO ");
		sql.append("  	AND ");
		sql.append("  		CASE ");
		sql.append("  			WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append("  			THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append("  			ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append("  		END = Y.TaishoNendo ");
		sql.append("  	WHERE ");
		sql.append("  		S.TaisyokuDate = '' ");
		sql.append("  		AND M.ChinginKbn IN ('05', '06') ");
		sql.append(" 		AND S.ShainKbn <> '04' ");
		
		if (StringUtils.isNotBlank(fromTaishoNendo)) {
			sql.append(" AND CASE ");
			sql.append(" 	WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(M.TaishoNenGetsudo, 4) ");
			sql.append(" END >= ?");
			pstmtf.addValue("String", fromTaishoNendo);
		}
		
		if (StringUtils.isNotBlank(toTaishoNendo)) {
			sql.append(" AND CASE ");
			sql.append(" 	WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(M.TaishoNenGetsudo, 4) ");
			sql.append(" END <= ?");
			pstmtf.addValue("String", toTaishoNendo);
		}
		
		if (StringUtils.isNotBlank(fromShainNo)) {
			sql.append(" AND CAST(K.ShainNO AS int) >=  ? ");
			pstmtf.addValue("String", fromShainNo);
		}
		
		if (StringUtils.isNotBlank(toShainNo)) {
			sql.append(" AND CAST(K.ShainNO AS int) <=  ? ");
			pstmtf.addValue("String", toShainNo);
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
		
		sql.append("  ) Q2 ");
		
		sql.append(" ON  Q1.TaishoNendo 		= Q2.TaishoNendo ");
		sql.append(" AND Q1.ShainNO 			= Q2.ShainNO ");
		sql.append(" AND Q1.EigyoshoCode 		= Q2.EigyoshoCode ");
		sql.append(" AND Q1.BushoName 			= Q2.BushoName ");
		sql.append(" AND Q1.YukyuKyukaFuyoNissu = Q2.YukyuKyukaFuyoNissu ");
		
		sql.append("  ) ");		
		
		sql.append(" SELECT ");
		sql.append(" 	 C.TaishoNendo");
		sql.append(" 	 ,CONVERT(varchar,GETDATE(),111) AS SakuseiDate ");
		sql.append(" 	 ,C.EigyoshoCode ");
		sql.append(" 	 ,C.EigyoshoName ");
		sql.append(" 	 ,C.BushoName ");
		sql.append(" 	 ,C.ShainNO ");
		sql.append(" 	 ,C.ShainName ");
		sql.append(" 	 ,C.YukyuKyukaFuyoNissu ");
		
		for (int cnt = 1; cnt <= 60; cnt++) {
			sql.append(" 	 ,MAX( ");
			sql.append(" 	 	CASE ");
			sql.append(" 	 		WHEN RowNumber = " + cnt );
			sql.append(" 	 		THEN [month] ");
			sql.append(" 	 		ELSE '' ");
			sql.append(" 	 	END) AS [month" + cnt + "]");
			sql.append(" 	 ,MAX( ");
			sql.append(" 	 	CASE ");
			sql.append(" 	 		WHEN RowNumber = " + cnt );
			sql.append(" 	 		THEN [day] ");
			sql.append(" 	 		ELSE '' ");
			sql.append(" 	 	END) AS [day" + cnt + "]");
			sql.append(" 	 ,MAX( ");
			sql.append(" 	 	CASE ");
			sql.append(" 	 		WHEN RowNumber = " + cnt );
			sql.append(" 	 		THEN [hankyu] ");
			sql.append(" 	 		ELSE '' ");
			sql.append(" 	 	END) AS [hankyu" + cnt + "]");
		}
		
		sql.append(" 	 ,C.YukyuKyukaFuyoNissu - ");
		sql.append(" 	 	SUM( ");
		sql.append(" 	 		CASE ");
		sql.append(" 	 			WHEN C.[day] <> '' ");
		sql.append(" 	 			THEN ( ");
		sql.append(" 	 			 	CASE ");
		sql.append(" 	 			 		WHEN C.[hankyu] <> '' ");
		sql.append(" 	 			 		THEN 0.5 ");
		sql.append(" 	 			 		ELSE 1 ");
		sql.append(" 	 			 	END) ");
		sql.append(" 	 			 ELSE 0 ");
		sql.append(" 	 		END) AS YukyuKyukaZanNissu");
		
		sql.append(" FROM ");
		sql.append(" 	CTE_MAIN C ");
		
		sql.append(" GROUP BY ");
		sql.append(" 	C.TaishoNendo ");
		sql.append(" 	,C.ShainNO ");
		sql.append(" 	,C.ShainName ");
		sql.append(" 	,C.YukyuKyukaFuyoNissu ");
		sql.append(" 	,C.EigyoshoCode ");
		sql.append(" 	,C.EigyoshoName ");
		sql.append(" 	,C.BushoName ");
		
		sql.append(" ORDER BY ");
		sql.append("     C.TaishoNendo ");
		
		if ("02".equals(order)) {
			sql.append("     ,C.EigyoshoCode ");
		}
		
		sql.append("     ,C.ShainNO ");
		
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
		csvStringTitle.addItem("対象年度",true);
		csvStringTitle.addItem("作成日付",true);
		csvStringTitle.addItem("営業所コード",true);
		csvStringTitle.addItem("営業所名",true);
		csvStringTitle.addItem("部署名",true);
		csvStringTitle.addItem("社員NO",true);
		csvStringTitle.addItem("社員名",true);
		csvStringTitle.addItem("有給休暇期首日数",true);
		
		for (int cnt = 1; cnt <= 60; cnt++) {
			csvStringTitle.addItem("有給休暇取得日(月)"+ cnt,true);
			csvStringTitle.addItem("有給休暇取得日(日)"+ cnt,true);
			csvStringTitle.addItem("有給休暇取得日(半休)"+ cnt,true);
		}
		
		csvStringTitle.addItem("有給休暇残日数",true);
		
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		// 明細部の設定
		count = data.size();
		for (int i = 0; i < count; i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			
			// 1行取得
			HashMap<String, String> d = data.get(i);
			
			csvStringRecord.addItem(d.get("TaishoNendo"), PJActionBase.getQuotation(columns, "TaishoNendo",d.get("TaishoNendo")));
			csvStringRecord.addItem(d.get("SakuseiDate"), PJActionBase.getQuotation(columns, "SakuseiDate",d.get("SakuseiDate")));
			csvStringRecord.addItem(d.get("EigyoshoCode"), PJActionBase.getQuotation(columns, "EigyoshoCode",d.get("EigyoshoCode")));
			csvStringRecord.addItem(d.get("EigyoshoName"), PJActionBase.getQuotation(columns, "EigyoshoName",d.get("EigyoshoName")));
			csvStringRecord.addItem(d.get("BushoName"), PJActionBase.getQuotation(columns, "BushoName",d.get("BushoName")));
			csvStringRecord.addItem(d.get("ShainNO"), PJActionBase.getQuotation(columns, "ShainNO",d.get("ShainNO")));
			csvStringRecord.addItem(d.get("ShainName"), PJActionBase.getQuotation(columns, "ShainName",d.get("ShainName")));
			csvStringRecord.addItem(d.get("YukyuKyukaFuyoNissu"), PJActionBase.getQuotation(columns, "YukyuKyukaFuyoNissu",d.get("YukyuKyukaFuyoNissu")));
			
			for (int cnt = 1; cnt <= 60; cnt++) {
				csvStringRecord.addItem(d.get("month" + cnt), PJActionBase.getQuotation(columns, "month" + cnt,d.get("month" + cnt)));
				csvStringRecord.addItem(d.get("day" + cnt), PJActionBase.getQuotation(columns, "day" + cnt,d.get("day" + cnt)));
				csvStringRecord.addItem(d.get("hankyu" + cnt), PJActionBase.getQuotation(columns, "hankyu" + cnt,d.get("hankyu" + cnt)));
			}
			
			csvStringRecord.addItem(d.get("YukyuKyukaZanNissu"), PJActionBase.getQuotation(columns, "YukyuKyukaZanNissu",d.get("YukyuKyukaZanNissu")));
			
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes("Shift_JIS"));
		// 名前を付けて保存
		this.setFilename("csvKinYukyuKyukaDaicho_" + formattedDateTime + ".csv");
	}
}