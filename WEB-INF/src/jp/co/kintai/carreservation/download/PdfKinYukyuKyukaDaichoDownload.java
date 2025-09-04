package jp.co.kintai.carreservation.download;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.spire.xls.CellRange;
import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class PdfKinYukyuKyukaDaichoDownload extends DownloadBase {
	
	public PdfKinYukyuKyukaDaichoDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		int count = 0;
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		String fromTaishoNendo		= req.getParameter("srhTxtTaishoNendoF");
		String toTaishoNendo		= req.getParameter("srhTxtTaishoNendoT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		String output				= req.getParameter("srhRdoOutput");
		
		// ログインユーザが処理可能な営業所コードの取得
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		ArrayList<String> shoriKanoEigyoshoCode = userInformation.getShoriKanoEigyoshoCode();
		
		// テンプレートファイルの場所
		// idを渡すと帳票テンプレートファイルのパスを返却してくれる。
		String templateFile = this.getTemplateFile("kinYukyuKyukaDaicho", req);
		// パスのみ
		String templateFilePath = this.getTemplateFilePath(req);
		// ファイル名のみ
		String templateFileName = this.getTemplateFileName("kinYukyuKyukaDaicho");
		// 拡張子
		String extension = templateFileName.substring(templateFileName.lastIndexOf('.'));
		// ファイル名から拡張子を取り除く
		templateFileName = templateFileName.replace(extension, "");
		
		// 新しいファイル名に付ける文字列
		SimpleDateFormat sdfNewFileName = new SimpleDateFormat("yyyyMMddHHmms");
		
		// 現在日付
		Date date = new Date();
		
		// ファイル名の作成(元のファイル名にyyyyMMddHHmms.pdf)
		String createFile = templateFilePath + "\\" + templateFileName + "_" + sdfNewFileName.format(date) + ".pdf";
		String createFileName = templateFileName + "_" + sdfNewFileName.format(date) + ".pdf";
		
		// ワークブック
		Workbook workbook = new Workbook();
		
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
			sql.append(" 		" + nendo + " AS TaishoNendo ");
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
		
		if (output == "02") {
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
		
		try {
			
			// テンプレートファイルが存在しているか確認
			File tmp = new File(templateFile);
			if (!tmp.exists()) {
				throw new RuntimeException("Excelファイルが存在しません: " + tmp.getAbsolutePath());
			}
			
			// テンプレートファイルを開く
			workbook.loadFromFile(templateFile);
			
			// 最初のシートを取得
			Worksheet worksheetTmp = workbook.getWorksheets().get(0);
//			// 新しいシートを作成
//			// 社員NOの数分シートを作成する
//			Worksheet worksheetNew = workbook.getWorksheets().add("kintai");
//			//最初のシートを2番目のシートに複製する
//			worksheetNew.copyFrom(worksheetTmp);
//			
//			// 編集するワークシートを選択
//			Worksheet worksheet = workbook.getWorksheets().get("kintai");
//			
//			
//			
//			String cellShainNO = rset.getString("ShainNO");
//			// 特定のセルを取得
//			CellRange cellshainNo = worksheet.getCellRange("AI5");// 社員NO入れたい
////			// セルの値を取得
////			String text = cell.getValue();
//			// セルに値を設定
//			cellshainNo.setText(cellShainNO);
			
			SimpleDateFormat sakuseiformat = new SimpleDateFormat("yyyy/MM/dd");
			
			// dataリストから1件ずつ社員NOを取得して処理
			for (int i = 0; i < data.size(); i++) {

				HashMap<String, String> record = data.get(i);
				String shainNo = record.get("ShainNO");
				
				String shainName = record.get("ShainName");
				String eigyosho = record.get("EigyoshoName");
				String busho = record.get("BushoName");
				String yukyukyukahuyoNissu = record.get("YukyuKyukaFuyoNissu");
				String yukyukyukazanNissu = record.get("YukyuKyukaZanNissu");

				// シート名を作成
				String sheetName = "Shain_" + shainNo;

				// 新しいシートをテンプレートからコピーして作成
				Worksheet newSheet = workbook.getWorksheets().add(sheetName);
				newSheet.copyFrom(worksheetTmp);
				    
				// セルに作成年月日を書き込み
				CellRange cellsakuseidate = newSheet.getCellRange("AM3");
				String sakuseidate = sakuseiformat.format(date);
				cellsakuseidate.setText(sakuseidate);
				    
				// セルにページ数を書き込み
				CellRange cellpagecnt = newSheet.getCellRange("AS3");
				cellpagecnt.setText("PAGE: " + (i+1));

				// セルに社員NOを書き込み
				CellRange cellshainNo = newSheet.getCellRange("AI5");
				cellshainNo.setText(shainNo);
				    
				// セルに社員名を書き込み
				CellRange cellshainName = newSheet.getCellRange("AM5");
				cellshainName.setText(shainName);
				    
				// セルに営業所名を書き込み
				CellRange celleigyosho = newSheet.getCellRange("A5");
				celleigyosho.setText(eigyosho);
				    
				// セルに部署名を書き込み
				CellRange cellbusho = newSheet.getCellRange("K5");
				cellbusho.setText(busho);
				
				// セルに有給休暇期首日数を書き込み
				CellRange cellyukyukyukahuyoNissu = newSheet.getCellRange("J7");
				cellyukyukyukahuyoNissu.setText(yukyukyukahuyoNissu);
				
				// セルに有給休暇残日数を書き込み
				CellRange cellyukyukyukazanNissu = newSheet.getCellRange("J32");
				cellyukyukyukazanNissu.setText(yukyukyukazanNissu);
				    
			
			}
			
			
			// テンプレートシートを削除する。
			worksheetTmp.remove();
			
			// PDFに変換して保存(templateFile配下に保存される)
			workbook.saveToFile(createFile ,FileFormat.PDF);
			
			// PDFファイルをbyte[]に変換
			byte[] pdfBytes = Files.readAllBytes(Paths.get(createFile));
			
			// データの格納
			this.setData(pdfBytes); // ここに編集中のデータをbyte[]で格納
			
			// 名前を付けて保存
			this.setFilename(createFileName);
			
			// templateFile配下に保存されたPDFファイルを削除する。
			Files.delete(Paths.get(createFile));
			
			
		} catch (Exception e) {
		    System.out.println("例外発生: " + e.getClass().getName());
		    e.printStackTrace();
		} catch (Throwable t) {  // ← Exception ではなく Throwable に変更
			System.out.println("予期しない例外が発生しました: " + t.getClass().getName());
			t.printStackTrace();
		} finally {
			// 各機能の停止/解放
			if (workbook != null) { workbook.dispose(); }
		}
	}
}