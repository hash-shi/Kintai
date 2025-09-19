package jp.co.kintai.carreservation.download;

import java.awt.Color;
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
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class PdfChiChinginkeisanshoDownload extends DownloadBase {
	
	public PdfChiChinginkeisanshoDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//=====================================================================
		// パラメータ取得
		//=====================================================================
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
		sql.append(" 	 LEFT(K.TaishoNenGetsudo, 4) + '年' + RIGHT(K.TaishoNenGetsudo, 2) + '月分' AS TaishoNenGetsudo ");
		sql.append(" 	,K0050.KbnName AS KakuteiKbn ");
		sql.append(" 	,CONVERT(varchar,GETDATE(),111) AS SakuseiDate ");
		sql.append(" 	,M.ShainNO ");
		sql.append(" 	,S.ShainName ");
		sql.append(" 	,E.EigyoshoName ");
		sql.append(" 	,B.BushoName ");
		
		sql.append(" 	,SUBSTRING(M.TaishoNengappi, 6, 2) AS [Month] ");
		sql.append(" 	,SUBSTRING(M.TaishoNengappi, 9, 2) AS [Day] ");
		sql.append(" 	,M.YobiKbn ");
		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN S.KinmuKaishiJi NOT IN ('') ");
		sql.append(" 		THEN S.KinmuKaishiJi + ':' + S.KinmuKaishiFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KinmuKaishiJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN S.KinmuShuryoJi  NOT IN ('') ");
		sql.append(" 		THEN S.KinmuShuryoJi + ':' + S.KinmuShuryoFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KinmuShuryoJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN S.KinmuKaishiJi NOT IN ('') ");
		sql.append(" 		THEN CAST(S.KeiyakuJitsudoJikan AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KeiyakuJitsudoJikan ");
		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ShusshaJi NOT IN ('') ");
		sql.append(" 		THEN M.ShusshaJi + ':' + M.ShusshaFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS ShusshaJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.TaishaJi  NOT IN ('') ");
		sql.append(" 		THEN M.TaishaJi  + ':' + M.TaishaFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS TaishaJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ShusshaJi NOT IN ('') ");
		sql.append(" 		THEN CAST(M.JitsudoJikan AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS JitsudoJikan ");
		
		sql.append(" 	,COALESCE(K0201A.KbnName, '') AS ChinginShinseiKbn1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ChinginShinseiKbn1 NOT IN ('', '00') ");		
		sql.append(" 		THEN CAST(M.ChinginShinseiJikan1 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS ChinginShinseiJikan1 ");
		
		sql.append(" 	,COALESCE(K0201B.KbnName, '') AS ChinginShinseiKbn2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ChinginShinseiKbn2 NOT IN ('', '00') ");		
		sql.append(" 		THEN CAST(M.ChinginShinseiJikan2 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS ChinginShinseiJikan2 ");
		
		sql.append(" 	,COALESCE(K0201C.KbnName, '') AS ChinginShinseiKbn3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ChinginShinseiKbn3 NOT IN ('', '00') ");		
		sql.append(" 		THEN CAST(M.ChinginShinseiJikan3 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS ChinginShinseiJikan3 ");
		
		sql.append(" 	,SUBSTRING(CONVERT(TEXT, TokkiJiko),   1, 66) AS TokkiJiko1 ");
		sql.append(" 	,SUBSTRING(CONVERT(TEXT, TokkiJiko),  67, 66) AS TokkiJiko2 ");
		sql.append(" 	,SUBSTRING(CONVERT(TEXT, TokkiJiko), 133, 66) AS TokkiJiko3 ");
		sql.append(" 	,SUBSTRING(CONVERT(TEXT, TokkiJiko), 198, 66) AS TokkiJiko4 ");
		
		sql.append(" 	,CAST(CAST(K.ShinseiNissu01 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu01 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu02 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu02 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu03 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu03 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu04 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu04 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu05 + K.ShinseiNissu06 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu05 "); // 半日有給0.5日
		sql.append(" 	,CAST(CAST(K.ShinseiNissu06 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu06 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu07 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu07 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu08 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu08 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu09 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu09 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu10 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu10 ");
		sql.append(" 	,CAST(CAST(K.ShinseiNissu11 AS DECIMAL(4,1)) AS VARCHAR) AS ShinseiNissu11 ");
		
		sql.append(" 	,CAST(K.ShinseiJikan01 AS VARCHAR) AS ShinseiJikan01 ");
		sql.append(" 	,CAST(K.ShinseiJikan02 AS VARCHAR) AS ShinseiJikan02 ");
		sql.append(" 	,CAST(K.ShinseiJikan03 AS VARCHAR) AS ShinseiJikan03 ");
		sql.append(" 	,CAST(K.ShinseiJikan04 AS VARCHAR) AS ShinseiJikan04 ");
		sql.append(" 	,CAST(K.ShinseiJikan05 + K.ShinseiJikan06 AS VARCHAR) AS ShinseiJikan05 "); // 半日有給
		sql.append(" 	,CAST(K.ShinseiJikan06 AS VARCHAR) AS ShinseiJikan06 ");
		sql.append(" 	,CAST(K.ShinseiJikan07 AS VARCHAR) AS ShinseiJikan07 ");
		sql.append(" 	,CAST(K.ShinseiJikan08 AS VARCHAR) AS ShinseiJikan08 ");
		sql.append(" 	,CAST(K.ShinseiJikan09 AS VARCHAR) AS ShinseiJikan09 ");
		sql.append(" 	,CAST(K.ShinseiJikan10 AS VARCHAR) AS ShinseiJikan10 ");
		sql.append(" 	,CAST(K.ShinseiJikan11 AS VARCHAR) AS ShinseiJikan11 ");
		
		sql.append(" 	,CAST(K.ShinseiTanka01 AS VARCHAR) AS ShinseiTanka01 ");
		sql.append(" 	,CAST(K.ShinseiTanka02 AS VARCHAR) AS ShinseiTanka02 ");
		sql.append(" 	,CAST(K.ShinseiTanka03 AS VARCHAR) AS ShinseiTanka03 ");
		sql.append(" 	,CAST(K.ShinseiTanka04 AS VARCHAR) AS ShinseiTanka04 ");
		sql.append(" 	,CAST(K.ShinseiTanka05 AS VARCHAR) AS ShinseiTanka05 ");
		sql.append(" 	,CAST(K.ShinseiTanka06 AS VARCHAR) AS ShinseiTanka06 ");
		sql.append(" 	,CAST(K.ShinseiTanka07 AS VARCHAR) AS ShinseiTanka07 ");
		sql.append(" 	,CAST(K.ShinseiTanka08 AS VARCHAR) AS ShinseiTanka08 ");
		sql.append(" 	,CAST(K.ShinseiTanka09 AS VARCHAR) AS ShinseiTanka09 ");
		sql.append(" 	,CAST(K.ShinseiTanka10 AS VARCHAR) AS ShinseiTanka10 ");
		sql.append(" 	,CAST(K.ShinseiTanka11 AS VARCHAR) AS ShinseiTanka11 ");
		
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei01 AS VARCHAR) AS ShinseiKingakuGoukei01 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei02 AS VARCHAR) AS ShinseiKingakuGoukei02 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei03 AS VARCHAR) AS ShinseiKingakuGoukei03 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei04 AS VARCHAR) AS ShinseiKingakuGoukei04 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei05 + K.ShinseiKingakuGoukei06 AS VARCHAR) AS ShinseiKingakuGoukei05 ");  // 半日有給
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei06 AS VARCHAR) AS ShinseiKingakuGoukei06 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei07 AS VARCHAR) AS ShinseiKingakuGoukei07 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei08 AS VARCHAR) AS ShinseiKingakuGoukei08 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei09 AS VARCHAR) AS ShinseiKingakuGoukei09 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei10 AS VARCHAR) AS ShinseiKingakuGoukei10 ");
		sql.append(" 	,CAST(K.ShinseiKingakuGoukei11 AS VARCHAR) AS ShinseiKingakuGoukei11 ");
		
		// 休日
		sql.append(" 	,CAST( ");
		sql.append(" 		(");
		sql.append(" 			SELECT ");
		sql.append(" 				CAST(COUNT('a') AS DECIMAL) ");
		sql.append(" 			FROM ");
		sql.append(" 				CHI_CHINGINKEISANSHO_MEISAI WITH(NOLOCK) ");
		sql.append(" 			WHERE ");
		sql.append(" 				TaishoNenGetsudo = M.TaishoNenGetsudo ");
		sql.append(" 			AND ");
		sql.append(" 				ShainNO = M.ShainNO ");
		sql.append(" 			AND ");
		sql.append(" 				ShusshaJi = '' ");
		sql.append(" 			AND ");
		sql.append(" 				ShusshaFun = '' ");
		sql.append(" 			AND ");
		sql.append(" 				TaishaJi = '' ");
		sql.append(" 			AND ");
		sql.append(" 				TaishaFun = '' ");
		sql.append(" 			AND ");
		sql.append(" 				JitsudoJikan = 0 ");
		sql.append(" 			AND ");
		sql.append(" 				ChinginShinseiKbn1 IN ('', '00') ");
		sql.append(" 			AND ");
		sql.append(" 				ChinginShinseiJikan1 = 0 ");
		sql.append(" 			AND ");
		sql.append(" 				ChinginShinseiKbn2 IN ('', '00') ");
		sql.append(" 			AND ");
		sql.append(" 				ChinginShinseiJikan2 = 0 ");
		sql.append(" 			AND ");
		sql.append(" 				ChinginShinseiKbn3 IN ('', '00') ");
		sql.append(" 			AND ");
		sql.append(" 				ChinginShinseiJikan3 = 0 ");
		sql.append(" 		) AS DECIMAL(4,1) ");
		sql.append(" 	) AS ShinseiNissuKyujitsu");
		
		// 申請日数合計
		sql.append(" 	,CAST(");
		sql.append(" 		CAST(K.ShinseiNissu01 + K.ShinseiNissu04 + K.ShinseiNissu05 + K.ShinseiNissu06 + ");
		sql.append(" 			( ");
		sql.append(" 				SELECT ");
		sql.append(" 					CAST(COUNT('a') AS DECIMAL) ");
		sql.append(" 				FROM ");
		sql.append(" 					CHI_CHINGINKEISANSHO_MEISAI WITH(NOLOCK) ");
		sql.append(" 				WHERE ");
		sql.append(" 					TaishoNenGetsudo = M.TaishoNenGetsudo ");
		sql.append(" 				AND ");
		sql.append(" 					ShainNO = M.ShainNO ");
		sql.append(" 				AND ");
		sql.append(" 					ShusshaJi = '' ");
		sql.append(" 				AND ");
		sql.append(" 					ShusshaFun = '' ");
		sql.append(" 				AND ");
		sql.append(" 					TaishaJi = '' ");
		sql.append(" 				AND ");
		sql.append(" 					TaishaFun = '' ");
		sql.append(" 				AND ");
		sql.append(" 					JitsudoJikan = 0 ");
		sql.append(" 				AND ");
		sql.append(" 					ChinginShinseiKbn1 IN ('', '00') ");
		sql.append(" 				AND ");
		sql.append(" 					ChinginShinseiJikan1 = 0 ");
		sql.append(" 				AND ");
		sql.append(" 					ChinginShinseiKbn2 IN ('', '00') ");
		sql.append(" 				AND ");
		sql.append(" 					ChinginShinseiJikan2 = 0 ");
		sql.append(" 				AND ");
		sql.append(" 					ChinginShinseiKbn3 IN ('', '00') ");
		sql.append(" 				AND ");
		sql.append(" 					ChinginShinseiJikan3 = 0 ");
		sql.append(" 			) AS DECIMAL(4,1) ");
		sql.append(" 		) AS VARCHAR ");
		sql.append(" 	) AS ShinseiNisuuGoukei ");
		
		// 有給残 WHERE条件は対象年の4月から対象年月までの日数
		sql.append(" 	,CAST ( ");
		sql.append(" 		COALESCE(Y.YukyuKyukaFuyoNissu, S.YukyuKyukaFuyoNissu) - ");
		sql.append(" 		( ");
		sql.append(" 			SELECT ");
		sql.append(" 				SUM(KIHON.ShinseiNissu05 + KIHON.ShinseiNissu06) ");
		sql.append(" 			FROM ");
		sql.append(" 				CHI_CHINGINKEISANSHO_KIHON KIHON ");
		sql.append(" 			WHERE ");
		sql.append(" 				KIHON.ShainNO = K.ShainNO ");
		sql.append(" 			AND ");
		sql.append(" 				CASE ");
		sql.append(" 					WHEN RIGHT(K.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 					THEN CAST(CAST(LEFT(K.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 					ELSE LEFT(K.TaishoNenGetsudo, 4) ");
		sql.append(" 				END + '/04' <= KIHON.TaishoNenGetsudo ");
		sql.append(" 			AND ");
		sql.append(" 				KIHON.TaishoNenGetsudo <= K.TaishoNenGetsudo ");
		sql.append(" 		) ");
		sql.append(" 		+ ");
		sql.append(" 		( ");
		sql.append(" 			SELECT ");
		sql.append(" 				COUNT(MEISAI.KintaiKbn) ");
		sql.append(" 			FROM ");
		sql.append(" 				KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 			WHERE ");
		sql.append(" 				MEISAI.TaishoNenGetsudo = M.TaishoNenGetsudo ");
		sql.append(" 			AND ");
		sql.append(" 				MEISAI.ShainNO = M.ShainNO ");
		sql.append(" 			AND ");
		sql.append(" 				CASE ");
		sql.append(" 					WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 					THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 					ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 				END + '/04' <= MEISAI.TaishoNenGetsudo ");
		sql.append(" 			AND ");
		sql.append(" 				MEISAI.TaishoNenGetsudo <= M.TaishoNenGetsudo ");
		sql.append(" 			AND ");
		sql.append(" 				MEISAI.KintaiKbn = '06' ");
		sql.append(" 		) AS DECIMAL(4,1) ");
		sql.append(" 	) AS YukyuKyukaZan ");
		
		// 申請時間合計
		sql.append(" 	,CAST( ");
		sql.append(" 		K.ShinseiJikan01 + K.ShinseiJikan02 + K.ShinseiJikan03 + K.ShinseiJikan04 + K.ShinseiJikan05 ");
		sql.append(" 			+  K.ShinseiJikan06 + K.ShinseiJikan07 + K.ShinseiJikan08 + K.ShinseiJikan09 + K.ShinseiJikan10 AS VARCHAR ");
		sql.append(" 	) AS ShinseiJikanGoukei");
		
		//　申請金額合計
		sql.append(" 	,CAST( ");
		sql.append(" 		K.ShinseiKingakuGoukei01 + K.ShinseiKingakuGoukei02 + K.ShinseiKingakuGoukei03 + K.ShinseiKingakuGoukei04 ");
		sql.append(" 			+ K.ShinseiKingakuGoukei05 + K.ShinseiKingakuGoukei06 + K.ShinseiKingakuGoukei07 + K.ShinseiKingakuGoukei08 ");
		sql.append(" 			+ K.ShinseiKingakuGoukei09 + K.ShinseiKingakuGoukei10 AS VARCHAR");
		sql.append(" 	) AS ShinseiKingakuGoukeiGoukei");
		
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
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_YUKYU_KYUKA_DAICHO Y ");
		sql.append(" ON ");
		sql.append(" 	S.ShainNO = Y.ShainNO ");
		sql.append(" AND ");
		sql.append(" 	LEFT(K.TaishoNenGetsudo, 4) = Y.TaishoNendo ");
		
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
		
		// テンプレートファイルの場所
		// idを渡すと帳票テンプレートファイルのパスを返却してくれる。
		String templateFile = this.getTemplateFile("chiChinginkeisansho", req);
		// パスのみ
		String templateFilePath = this.getTemplateFilePath(req);
		// ファイル名のみ
		String templateFileName = this.getTemplateFileName("chiChinginkeisansho");
		// 拡張子(xlsx)
		String extensionXlsx = templateFileName.substring(templateFileName.lastIndexOf('.'));
		// 拡張子(pdf)
		String extensionPdf = ".pdf";
		// ファイル名から拡張子を取り除く
		templateFileName = templateFileName.replace(extensionXlsx, "");
		
		// 新しいファイル名に付ける文字列
		SimpleDateFormat sdfNewFileName = new SimpleDateFormat("yyyyMMddHHmms");
		
		// 現在日付
		Date date = new Date();
		
		// ファイル名の作成(元のファイル名にyyyyMMddHHmms.pdf)
		// excel
		String createFileNameXlsx = templateFileName + "_" + sdfNewFileName.format(date) + extensionXlsx;
		String createFileXlsx = templateFilePath + createFileNameXlsx;
		// pdf
		String createFileNamePdf = templateFileName + "_" + sdfNewFileName.format(date) + extensionPdf;
		String createFilePdf = templateFilePath + createFileNamePdf;
		
		// ワークブック
		Workbook workbook = new Workbook();
		
		// PDF変換で使用
		OfficeManager officeManager = null;
		DocumentConverter localConverter = null;
		
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
			
			int rowCnt = 0;
			int pageCnt = 0;
			
			for (int i = 0; i < data.size(); i++) {
				
				// 最初のシートを作成
				if(i == 0) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageCnt++;
				}
				// 社員Noが変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("ShainNO").equals(data.get(i).get("ShainNO")))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageCnt++;
				} 
				// 対象年月が変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("TaishoNenGetsudo").equals(data.get(i).get("TaishoNenGetsudo")))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageCnt++;
				} 
				
				// 編集するワークシートを選択
				Worksheet worksheet = workbook.getWorksheets().get(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
				
				// 単価項目の値を３桁ごとにカンマ区切りにする
				String ShinseiTanka01 = formatAsCurrency(data.get(i).get("ShinseiTanka01"));
				String ShinseiTanka04 = formatAsCurrency(data.get(i).get("ShinseiTanka04"));
				String ShinseiTanka02 = formatAsCurrency(data.get(i).get("ShinseiTanka02"));
				String ShinseiTanka03 = formatAsCurrency(data.get(i).get("ShinseiTanka03"));
				String ShinseiTanka05 = formatAsCurrency(data.get(i).get("ShinseiTanka05"));
				String ShinseiTanka09 = formatAsCurrency(data.get(i).get("ShinseiTanka09"));
				String ShinseiTanka11 = formatAsCurrency(data.get(i).get("ShinseiTanka11"));
//				int ShinseiTanka01_ = Integer.parseInt(data.get(i).get("ShinseiTanka01"));
//				String ShinseiTanka01 = String.format("%,3d", ShinseiTanka01_);
//				
//				int ShinseiTanka04_ = Integer.parseInt(data.get(i).get("ShinseiTanka04"));
//				String ShinseiTanka04 = String.format("%,3d", ShinseiTanka04_);
//				
//				int ShinseiTanka02_ = Integer.parseInt(data.get(i).get("ShinseiTanka02"));
//				String ShinseiTanka02 = String.format("%,3d", ShinseiTanka02_);
//				
//				int ShinseiTanka03_ = Integer.parseInt(data.get(i).get("ShinseiTanka03"));
//				String ShinseiTanka03 = String.format("%,3d", ShinseiTanka03_);
//				
//				int ShinseiTanka05_ = Integer.parseInt(data.get(i).get("ShinseiTanka05"));
//				String ShinseiTanka05 = String.format("%,3d", ShinseiTanka05_);
//				
//				int ShinseiTanka09_ = Integer.parseInt(data.get(i).get("ShinseiTanka09"));
//				String ShinseiTanka09 = String.format("%,3d", ShinseiTanka09_);
//				
//				int ShinseiTanka11_ = Integer.parseInt(data.get(i).get("ShinseiTanka11"));
//				String ShinseiTanka11 = String.format("%,3d", ShinseiTanka11_);
				
				// 金額項目の値を３桁ごとにカンマ区切りにする
				String ShinseiKingakuGoukei01 = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei01"));
				String ShinseiKingakuGoukei04 = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei04"));
				String ShinseiKingakuGoukei02 = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei02"));
				String ShinseiKingakuGoukei03 = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei03"));
				String ShinseiKingakuGoukei05 = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei05"));
				String ShinseiKingakuGoukei09 = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei09"));
				String ShinseiKingakuGoukei11 = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei11"));
				String ShinseiKingakuGoukeiGoukei = formatAsCurrency(data.get(i).get("ShinseiKingakuGoukeiGoukei"));
				
//				int ShinseiKingakuGoukei01_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukei01"));
//				String ShinseiKingakuGoukei01 = String.format("%,3d", ShinseiKingakuGoukei01_);
//				
//				int ShinseiKingakuGoukei04_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukei04"));
//				String ShinseiKingakuGoukei04 = String.format("%,3d", ShinseiKingakuGoukei04_);
//				
//				int ShinseiKingakuGoukei02_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukei02"));
//				String ShinseiKingakuGoukei02 = String.format("%,3d", ShinseiKingakuGoukei02_);
//				
//				int ShinseiKingakuGoukei03_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukei03"));
//				String ShinseiKingakuGoukei03 = String.format("%,3d", ShinseiKingakuGoukei03_);
//				
//				int ShinseiKingakuGoukei05_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukei05"));
//				String ShinseiKingakuGoukei05 = String.format("%,3d", ShinseiKingakuGoukei05_);
//				
//				int ShinseiKingakuGoukei09_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukei09"));
//				String ShinseiKingakuGoukei09 = String.format("%,3d", ShinseiKingakuGoukei09_);
//				
//				int ShinseiKingakuGoukei11_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukei11"));
//				String ShinseiKingakuGoukei11 = String.format("%,3d", ShinseiKingakuGoukei11_);
//				
//				int ShinseiKingakuGoukeiGoukei_ = Integer.parseInt(data.get(i).get("ShinseiKingakuGoukeiGoukei"));
//				String ShinseiKingakuGoukeiGoukei = String.format("%,3d", ShinseiKingakuGoukeiGoukei_);
				
				// 特定のセルを取得し値を設定
				// 新しいシートを作成した場合のみ上部と下部に値を設定
				if(i == 0 
						|| !(data.get(i-1).get("ShainNO").equals(data.get(i).get("ShainNO"))) 
						|| !(data.get(i-1).get("TaishoNenGetsudo").equals(data.get(i).get("TaishoNenGetsudo")))) {
					// 上部：基本情報を設定
					worksheet.getCellRange("AR3").setText(data.get(i).get("SakuseiDate"));
					worksheet.getCellRange("AX3").setText("PAGE:   " + pageCnt);
					worksheet.getCellRange("A4").setText(data.get(i).get("TaishoNenGetsudo"));
					worksheet.getCellRange("AR4").setText(data.get(i).get("KakuteiKbn"));
					worksheet.getCellRange("A5").setText(data.get(i).get("EigyoshoName"));
					worksheet.getCellRange("K5").setText(data.get(i).get("BushoName"));
					worksheet.getCellRange("AN5").setText(data.get(i).get("ShainNO"));
					worksheet.getCellRange("AR5").setText(data.get(i).get("ShainName"));
					worksheet.getCellRange("K7").setText(data.get(i).get("KinmuKaishiJikoku"));
					worksheet.getCellRange("Q7").setText(data.get(i).get("KinmuShuryoJikoku"));
					worksheet.getCellRange("AA7").setText(data.get(i).get("KeiyakuJitsudoJikan"));
					
					// 下部：集計内容を設定
					worksheet.getCellRange("F44").setText(data.get(i).get("ShinseiNissu01"));
					worksheet.getCellRange("I44").setText(data.get(i).get("ShinseiJikan01"));
					worksheet.getCellRange("L44").setText(ShinseiTanka01);
					worksheet.getCellRange("O44").setText(ShinseiKingakuGoukei01);
					worksheet.getCellRange("F45").setText(data.get(i).get("ShinseiNissu04"));
					worksheet.getCellRange("I45").setText(data.get(i).get("ShinseiJikan04"));
					worksheet.getCellRange("L45").setText(ShinseiTanka04);
					worksheet.getCellRange("O45").setText(ShinseiKingakuGoukei04);
					worksheet.getCellRange("F46").setText(data.get(i).get("ShinseiNissu02"));
					worksheet.getCellRange("I46").setText(data.get(i).get("ShinseiJikan02"));
					worksheet.getCellRange("L46").setText(ShinseiTanka02);
					worksheet.getCellRange("O46").setText(ShinseiKingakuGoukei02);
					worksheet.getCellRange("F47").setText(data.get(i).get("ShinseiNissu03"));
					worksheet.getCellRange("I47").setText(data.get(i).get("ShinseiJikan03"));
					worksheet.getCellRange("L47").setText(ShinseiTanka03);
					worksheet.getCellRange("O47").setText(ShinseiKingakuGoukei03);
					worksheet.getCellRange("F48").setText(data.get(i).get("ShinseiNissu05"));
					worksheet.getCellRange("I48").setText(data.get(i).get("ShinseiJikan05"));
					worksheet.getCellRange("L48").setText(ShinseiTanka05);
					worksheet.getCellRange("O48").setText(ShinseiKingakuGoukei05);
					worksheet.getCellRange("F49").setText(data.get(i).get("ShinseiNissu09"));
					worksheet.getCellRange("L49").setText(ShinseiTanka09);
					worksheet.getCellRange("O49").setText(ShinseiKingakuGoukei09);
					worksheet.getCellRange("F50").setText(data.get(i).get("ShinseiNissu11"));
					worksheet.getCellRange("I50").setText(data.get(i).get("ShinseiJikan11"));
					worksheet.getCellRange("L50").setText(ShinseiTanka11);
					worksheet.getCellRange("O50").setText(ShinseiKingakuGoukei11);
					worksheet.getCellRange("F51").setText(data.get(i).get("ShinseiNissuKyujitsu"));
					worksheet.getCellRange("F52").setText(data.get(i).get("ShinseiNisuuGoukei"));
					worksheet.getCellRange("I52").setText(data.get(i).get("ShinseiJikanGoukei"));
					worksheet.getCellRange("O52").setText(ShinseiKingakuGoukeiGoukei);
					
					worksheet.getCellRange("AE44").setText(data.get(i).get("TokkiJiko1") + data.get(i).get("TokkiJiko2") 
														+ data.get(i).get("TokkiJiko3") + data.get(i).get("TokkiJiko4"));
					worksheet.getCellRange("AE52").setText(data.get(i).get("YukyuKyukaZan"));
				}
				
				// 中央部：明細部を設定
				worksheet.getCellRange("A" + (11 + rowCnt)).setText(data.get(i).get("Month"));
				worksheet.getCellRange("B" + (11 + rowCnt)).setText(data.get(i).get("Day"));
				worksheet.getCellRange("C" + (11 + rowCnt)).setText(data.get(i).get("YobiKbn"));
				
				// 勤務時間のデータがない場合は空文字を出力	
				// 「出社時刻　～　退社時刻」の形で表示
				if(data.get(i).get("ShusshaJikoku").isBlank() && data.get(i).get("TaishaJikoku").isBlank()) {
					worksheet.getCellRange("E" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("E" + (11 + rowCnt)).setText(data.get(i).get("ShusshaJikoku") + " ～ " + data.get(i).get("TaishaJikoku"));
				}
				
				// 勤務時間のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(data.get(i).get("JitsudoJikan").isBlank() || data.get(i).get("JitsudoJikan").equals("0.00")) {
					worksheet.getCellRange("L" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("L" + (11 + rowCnt)).setText(data.get(i).get("JitsudoJikan"));
				}
				
				worksheet.getCellRange("O" + (11 + rowCnt)).setText(data.get(i).get("ChinginShinseiKbn1"));
				
				// 申請時間1のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(data.get(i).get("ChinginShinseiJikan1").isBlank() || data.get(i).get("ChinginShinseiJikan1").equals("0.00")) {
					worksheet.getCellRange("S" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("S" + (11 + rowCnt)).setText(data.get(i).get("ChinginShinseiJikan1"));
				}
				
				worksheet.getCellRange("V" + (11 + rowCnt)).setText(data.get(i).get("ChinginShinseiKbn2"));
				
				// 申請時間2のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(data.get(i).get("ChinginShinseiJikan2").isBlank() || data.get(i).get("ChinginShinseiJikan2").equals("0.00")) {
					worksheet.getCellRange("Z" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("Z" + (11 + rowCnt)).setText(data.get(i).get("ChinginShinseiJikan2"));
				}
				
				worksheet.getCellRange("AC" + (11 + rowCnt)).setText(data.get(i).get("ChinginShinseiKbn3"));
				
				// 申請時間3のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(data.get(i).get("ChinginShinseiJikan3").isBlank() || data.get(i).get("ChinginShinseiJikan3").equals("0.00")) {
					worksheet.getCellRange("AG" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("AG" + (11 + rowCnt)).setText(data.get(i).get("ChinginShinseiJikan3"));
				}
				
				// 曜日区分が土の場合は青色、日の場合は赤色
				if(data.get(i).get("YobiKbn").equals("土")) {
					worksheet.getCellRange("C" + (11 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.blue);
				} else if(data.get(i).get("YobiKbn").equals("日")) {
					worksheet.getCellRange("C" + (11 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				}
				
				rowCnt++;
			}
			
			// テンプレートシートを削除する。
			worksheetTmp.remove();
			
			// 保存(templateFile配下に保存される)
			workbook.saveToFile(createFileXlsx);
			
			// PDF変換元ファイル
			File inputFile = new File(createFileXlsx);
			// PDF変換先ファイル
			File outputFile = new File(createFilePdf);
			
			// PDF変換用ライブラリの準備(これの起動に10秒かかる)
			officeManager = LocalOfficeManager.make();
			localConverter = LocalConverter.make(officeManager);
			// 起動
			officeManager.start();
			// PDF変換
			localConverter.convert(inputFile).to(outputFile).execute();
			// 停止
			officeManager.stop();
			
			// PDFファイルをbyte[]に変換
			byte[] pdfBytes = Files.readAllBytes(Paths.get(createFilePdf));
			
			// データの格納
			this.setData(pdfBytes); // ここに編集中のデータをbyte[]で格納
			
			// 名前を付けて保存
			this.setFilename(createFileNamePdf);
			
			// templateFile配下に作成したxlsxとpdfを削除する
			Files.delete(Paths.get(createFileXlsx));
			Files.delete(Paths.get(createFilePdf));
			
			
		} catch (Exception e) {
		    System.out.println("例外発生: " + e.getClass().getName());
		    e.printStackTrace();
		} catch (Throwable t) {  // ← Exception ではなく Throwable に変更
			System.out.println("予期しない例外が発生しました: " + t.getClass().getName());
			t.printStackTrace();
		} finally {
			// 各機能の停止/解放
			if (officeManager != null) { if (officeManager.isRunning()) { officeManager.stop(); } }
			if (workbook != null) { workbook.dispose(); }
		}

		
	}
	private String formatAsCurrency(String value) {
	    if (value == null || value.isEmpty()) {
	        return ""; // 空欄にする
	    }
	    return String.format("%,3d", Integer.parseInt(value));
	}
}