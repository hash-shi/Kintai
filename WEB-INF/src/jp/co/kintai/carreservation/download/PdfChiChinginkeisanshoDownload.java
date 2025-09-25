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
		sql.append(" 	) AS ShinseiNissuGoukei ");
		
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
			int pageIndex = 0;
			
			for (int i = 0; i < data.size(); i++) {
				String nengetsudo	= data.get(i).get("TaishoNenGetsudo");
				String shainNo		= data.get(i).get("ShainNO");
				
				String key = nengetsudo + "_" + shainNo;
				
				// 最初のシートを作成
				if(i == 0) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(key);
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageIndex++;
				}
				// 社員Noが変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("ShainNO").equals(shainNo))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(key);
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageIndex++;
				} 
				// 対象年月が変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("TaishoNenGetsudo").equals(nengetsudo))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(key);
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageIndex++;
				} 
				
				// 編集するワークシートを選択
				Worksheet worksheet = workbook.getWorksheets().get(key);
				
				String sakuseiDate 			= data.get(i).get("SakuseiDate");
				String kakuteiKbn 			= data.get(i).get("KakuteiKbn");
				String eigyoshoName 		= data.get(i).get("EigyoshoName");
				String bushoName 			= data.get(i).get("BushoName");
				String shainName			= data.get(i).get("ShainName");
				String kinmuKaishiJikoku	= data.get(i).get("KinmuKaishiJikoku");
				String kinmuShuryoJikoku	= data.get(i).get("KinmuShuryoJikoku");
				String KeiyakuJitsudoJikan 	= data.get(i).get("KeiyakuJitsudoJikan");
				
				String shinseiNissu01 			= data.get(i).get("ShinseiNissu01");
				String shinseiNissu04 			= data.get(i).get("ShinseiNissu04");
				String shinseiNissu02 			= data.get(i).get("ShinseiNissu02");
				String shinseiNissu03 			= data.get(i).get("ShinseiNissu03");
				String shinseiNissu05 			= data.get(i).get("ShinseiNissu05");
				String shinseiNissu09 			= data.get(i).get("ShinseiNissu09");
				String shinseiNissu11 			= data.get(i).get("ShinseiNissu11");
				String shinseiNissuKyujitsu		= data.get(i).get("ShinseiNissuKyujitsu");
				String shinseiNisuuGoukei		= data.get(i).get("ShinseiNissuGoukei");
				
				String shinseiJikan01 		= data.get(i).get("ShinseiJikan01");
				String shinseiJikan04 		= data.get(i).get("ShinseiJikan04");
				String shinseiJikan02 		= data.get(i).get("ShinseiJikan02");
				String shinseiJikan03 		= data.get(i).get("ShinseiJikan03");
				String shinseiJikan05		= data.get(i).get("ShinseiJikan05");
				String shinseiJikan11 		= data.get(i).get("ShinseiJikan11");
				String shinseiJikanGoukei 	= data.get(i).get("ShinseiJikanGoukei");
				
				// 単価項目の値を３桁ごとにカンマ区切りにする
				String shinseiTanka01 = formatAsCurrency(data.get(i).get("ShinseiTanka01"));
				String shinseiTanka04 = formatAsCurrency(data.get(i).get("ShinseiTanka04"));
				String shinseiTanka02 = formatAsCurrency(data.get(i).get("ShinseiTanka02"));
				String shinseiTanka03 = formatAsCurrency(data.get(i).get("ShinseiTanka03"));
				String shinseiTanka05 = formatAsCurrency(data.get(i).get("ShinseiTanka05"));
				String shinseiTanka09 = formatAsCurrency(data.get(i).get("ShinseiTanka09"));
				String shinseiTanka11 = formatAsCurrency(data.get(i).get("ShinseiTanka11"));
				
				// 金額項目の値を３桁ごとにカンマ区切りにする
				String shinseiKingakuGoukei01		= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei01"));
				String shinseiKingakuGoukei04		= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei04"));
				String shinseiKingakuGoukei02		= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei02"));
				String shinseiKingakuGoukei03		= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei03"));
				String shinseiKingakuGoukei05		= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei05"));
				String shinseiKingakuGoukei09		= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei09"));
				String shinseiKingakuGoukei11		= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukei11"));
				String shinseiKingakuGoukeiGoukei	= formatAsCurrency(data.get(i).get("ShinseiKingakuGoukeiGoukei"));
				
				String tokkiJiko = data.get(i).get("TokkiJiko1") + data.get(i).get("TokkiJiko2") + data.get(i).get("TokkiJiko3") + data.get(i).get("TokkiJiko4");
				String yukyuKyukaZan = data.get(i).get("YukyuKyukaZan");
				
				// 特定のセルを取得し値を設定
				// 新しいシートを作成した場合のみ上部と下部に値を設定
				if(i == 0 
						|| !(data.get(i-1).get("ShainNO").equals(shainNo)) 
						|| !(data.get(i-1).get("TaishoNenGetsudo").equals(nengetsudo))) {
					// 上部：基本情報を設定
					worksheet.getCellRange("AR3").setText(sakuseiDate);
					worksheet.getCellRange("AX3").setText("PAGE:   " + pageIndex);
					worksheet.getCellRange("A4").setText(nengetsudo);
					worksheet.getCellRange("AR4").setText(kakuteiKbn);
					worksheet.getCellRange("A5").setText(eigyoshoName);
					worksheet.getCellRange("K5").setText(bushoName);
					worksheet.getCellRange("AN5").setText(shainNo);
					worksheet.getCellRange("AR5").setText(shainName);
					worksheet.getCellRange("K7").setText(kinmuKaishiJikoku);
					worksheet.getCellRange("Q7").setText(kinmuShuryoJikoku);
					worksheet.getCellRange("AA7").setText(KeiyakuJitsudoJikan);
					
					// 下部：集計内容を設定
					worksheet.getCellRange("F44").setText(shinseiNissu01);
					worksheet.getCellRange("F45").setText(shinseiNissu04);
					worksheet.getCellRange("F46").setText(shinseiNissu02);
					worksheet.getCellRange("F47").setText(shinseiNissu03);
					worksheet.getCellRange("F48").setText(shinseiNissu05);
					worksheet.getCellRange("F49").setText(shinseiNissu09);
					worksheet.getCellRange("F50").setText(shinseiNissu11);
					worksheet.getCellRange("F51").setText(shinseiNissuKyujitsu);
					worksheet.getCellRange("F52").setText(shinseiNisuuGoukei);
					worksheet.getCellRange("I44").setText(shinseiJikan01);
					worksheet.getCellRange("I45").setText(shinseiJikan04);
					worksheet.getCellRange("I46").setText(shinseiJikan02);
					worksheet.getCellRange("I47").setText(shinseiJikan03);
					worksheet.getCellRange("I48").setText(shinseiJikan05);
					worksheet.getCellRange("I50").setText(shinseiJikan11);
					worksheet.getCellRange("I52").setText(shinseiJikanGoukei);
					worksheet.getCellRange("L44").setText(shinseiTanka01);
					worksheet.getCellRange("L45").setText(shinseiTanka04);
					worksheet.getCellRange("L46").setText(shinseiTanka02);
					worksheet.getCellRange("L47").setText(shinseiTanka03);
					worksheet.getCellRange("L48").setText(shinseiTanka05);
					worksheet.getCellRange("L49").setText(shinseiTanka09);
					worksheet.getCellRange("L50").setText(shinseiTanka11);
					worksheet.getCellRange("O44").setText(shinseiKingakuGoukei01);
					worksheet.getCellRange("O45").setText(shinseiKingakuGoukei04);
					worksheet.getCellRange("O46").setText(shinseiKingakuGoukei02);
					worksheet.getCellRange("O47").setText(shinseiKingakuGoukei03);
					worksheet.getCellRange("O48").setText(shinseiKingakuGoukei05);
					worksheet.getCellRange("O49").setText(shinseiKingakuGoukei09);
					worksheet.getCellRange("O50").setText(shinseiKingakuGoukei11);
					worksheet.getCellRange("O52").setText(shinseiKingakuGoukeiGoukei);
					
					worksheet.getCellRange("AE44").setText(tokkiJiko);
					worksheet.getCellRange("AE52").setText(yukyuKyukaZan);
				}
				
				String month				= data.get(i).get("Month");
				String day					= data.get(i).get("Day");
				String yobiKbn				= data.get(i).get("YobiKbn");
				String shusshaJikoku		= data.get(i).get("ShusshaJikoku");
				String taishaJikoku			= data.get(i).get("TaishaJikoku");
				String jitsudoJikan			= data.get(i).get("JitsudoJikan");
				String chinginShinseiKbn1	= data.get(i).get("ChinginShinseiKbn1");
				String chinginShinseiJikan1	= data.get(i).get("ChinginShinseiJikan1");
				String chinginShinseiKbn2	= data.get(i).get("ChinginShinseiKbn2");
				String chinginShinseiJikan2	= data.get(i).get("ChinginShinseiJikan2");
				String chinginShinseiKbn3	= data.get(i).get("ChinginShinseiKbn3");
				String chinginShinseiJikan3	= data.get(i).get("ChinginShinseiJikan3");
				
				// 中央部：明細部を設定
				worksheet.getCellRange("A" + (11 + rowCnt)).setText(month);
				worksheet.getCellRange("B" + (11 + rowCnt)).setText(day);
				worksheet.getCellRange("C" + (11 + rowCnt)).setText(yobiKbn);
				
				// 勤務時間のデータがない場合は空文字を出力	
				// 「出社時刻　～　退社時刻」の形で表示
				if(shusshaJikoku.isBlank() && taishaJikoku.isBlank()) {
					worksheet.getCellRange("E" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("E" + (11 + rowCnt)).setText(shusshaJikoku + " ～ " + taishaJikoku);
				}
				
				// 勤務時間のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(jitsudoJikan.isBlank() || jitsudoJikan.equals("0.00")) {
					worksheet.getCellRange("L" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("L" + (11 + rowCnt)).setText(jitsudoJikan);
				}
				
				worksheet.getCellRange("O" + (11 + rowCnt)).setText(chinginShinseiKbn1);
				
				// 申請時間1のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(chinginShinseiJikan1.isBlank() || chinginShinseiJikan1.equals("0.00")) {
					worksheet.getCellRange("S" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("S" + (11 + rowCnt)).setText(chinginShinseiJikan1);
				}
				
				worksheet.getCellRange("V" + (11 + rowCnt)).setText(chinginShinseiKbn2);
				
				// 申請時間2のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(chinginShinseiJikan2.isBlank() || chinginShinseiJikan2.equals("0.00")) {
					worksheet.getCellRange("Z" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("Z" + (11 + rowCnt)).setText(chinginShinseiJikan2);
				}
				
				worksheet.getCellRange("AC" + (11 + rowCnt)).setText(chinginShinseiKbn3);
				
				// 申請時間3のデータがない場合は空文字を出力
				// 未入力項目に0.00が出力されることを防ぐ
				if(chinginShinseiJikan3.isBlank() || chinginShinseiJikan3.equals("0.00")) {
					worksheet.getCellRange("AG" + (11 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("AG" + (11 + rowCnt)).setText(chinginShinseiJikan3);
				}
				
				// 曜日区分が土の場合は青色、日の場合は赤色
				if(yobiKbn.equals("土")) {
					worksheet.getCellRange("C" + (11 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.blue);
				} else if(yobiKbn.equals("日")) {
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
	
	// データがない場合は0を入れる
	private String formatAsCurrency(String value) {
	    if (value == null || value.isEmpty()) {
	        return "0";
	    }
	    return String.format("%,3d", Integer.parseInt(value));
	}
}