package jp.co.kintai.carreservation.action.kintai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiListAction extends PJActionBase {
	public KintaiListAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
				
		//=====================================================================
		// 結果返却
		//=====================================================================
		
		// 処理選択
		ArrayList<HashMap<String, String>> mstKubun0504 = PJActionBase.getMstKubuns(con, "0504", "", "");
		req.setAttribute("mstKubun0504", mstKubun0504);
		
		// 出力条件
		ArrayList<HashMap<String, String>> mstKubun0050 = PJActionBase.getMstKubuns(con, "0050", "", "");
		req.setAttribute("mstKubun0050", mstKubun0050);
		
		this.setView("success");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void kinShukkinBo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;

		String loginUser			= req.getParameter("hdnShainNo");
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

		List<HashMap<String, String>> shoriKanoEigyoshos = PJActionBase.getMstShainEigyoshos(con, loginUser);

		sql.append(" SELECT ");
		sql.append(" 	LEFT(K.TaishoNenGetsudo, 4) + '年' + RIGHT(K.TaishoNenGetsudo, 2) + '月分' AS TaishoNenGetsudo ");
		sql.append(" 	,K0050.KbnName AS KakuteiKbn ");
		sql.append(" 	,M.ShainNO ");
		sql.append(" 	,S.ShainName ");
		sql.append(" 	,E.EigyoshoName ");
		sql.append(" 	,B.BushoName ");

		sql.append(" 	,SUBSTRING(M.TaishoNengappi, 6, 2) AS [Month] ");
		sql.append(" 	,SUBSTRING(M.TaishoNengappi, 9, 2) AS [Day] ");
		sql.append(" 	,M.YobiKbn ");
		sql.append(" 	,COALESCE(K0051.KbnName, '') AS ShukkinYoteiKbn ");
		sql.append(" 	,COALESCE(M0100.KbnName, '') AS KintaiKbn ");

		sql.append(" 	,COALESCE(K0101A.KbnName, '') AS KintaiShinseiKbn1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi1 NOT IN ('') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi1 + ':' + M.KintaiShinseiKaishiFun1 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku1 ");		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi1 NOT IN ('') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi1 + ':' + M.KintaiShinseiShuryoFun1 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn1 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiJikan1 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiJikan1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn1 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiKyukeiJikan1 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan1 ");

		sql.append(" 	,COALESCE(K0101B.KbnName, '') AS KintaiShinseiKbn2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi2 NOT IN ('') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi2 + ':' + M.KintaiShinseiKaishiFun2 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku2 ");		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi2 NOT IN ('') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi2 + ':' + M.KintaiShinseiShuryoFun2 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn2 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiJikan2 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiJikan2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn2 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiKyukeiJikan2 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan2 ");

		sql.append(" 	,COALESCE(K0101B.KbnName, '') AS KintaiShinseiKbn3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi3 NOT IN ('') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi3 + ':' + M.KintaiShinseiKaishiFun3 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku3 ");		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi3 NOT IN ('') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi3 + ':' + M.KintaiShinseiShuryoFun3 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn3 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiJikan3 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiJikan3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn3 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiKyukeiJikan3 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan3 ");

		sql.append(" 	, COALESCE(M.KintaiShinseiBiko, '') AS KintaiShinseiBiko");

		sql.append(" 	,ShinseiNissu01 ");
		sql.append(" 	,ShinseiNissu03 ");
		sql.append(" 	,ShinseiNissu04 ");
		sql.append(" 	,ShinseiNissu07 ");
		sql.append(" 	,( ");
		sql.append(" 		SELECT ");
		sql.append(" 			COUNT(MEISAI.KintaiKbn) ");
		sql.append(" 		FROM ");
		sql.append(" 			KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 		WHERE ");
		sql.append(" 			MEISAI.ShainNO = M.ShainNO AND ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 	 			THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 			END + '/04' <= MEISAI.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.TaishoNenGetsudo <= M.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.KintaiKbn = '06' ");
		sql.append(" 	 ) ");
		sql.append(" 	,ShinseiNissu08 ");
		sql.append(" 	,ShinseiNissu09 ");
		sql.append(" 	,ShinseiNissu10 ");
		sql.append(" 	,ShinseiNissu11 + ShinseiNissu12 AS ShinseiNissu11 ");		
		sql.append(" 	,ShinseiNissu01 + ShinseiNissu03 + ShinseiNissu04 + ShinseiNissu07 + ShinseiNissu08 ");
		sql.append(" 		+ ShinseiNissu09 + ShinseiNissu10 + ShinseiNissu11 + ShinseiNissu12 AS ShiseiNisuuGoukei ");

		sql.append(" 	,ShinseiJikan01 ");
		sql.append(" 	,ShinseiJikan02 ");
		sql.append(" 	,ShinseiJikan03 ");
		sql.append(" 	,ShinseiJikan04 ");
		sql.append(" 	,ShinseiJikan01 + ShinseiJikan03 AS ShinseiJikangaiKei ");

		sql.append(" 	,ShinseiKingaku01 ");
		sql.append(" 	,ShinseiKingaku02 ");

		sql.append(" 	,COALESCE(Y.YukyuKyukaFuyoNissu, S.YukyuKyukaFuyoNissu) ");

		sql.append(" 	,( ");
		sql.append(" 		SELECT ");
		sql.append(" 			SUM(KIHON.ShinseiNissu04 + KIHON.ShinseiNissu05 * 0.5) ");
		sql.append(" 		FROM ");
		sql.append(" 			KIN_SHUKKINBO_KIHON KIHON ");
		sql.append(" 		WHERE ");
		sql.append(" 			KIHON.ShainNO = K.ShainNO AND ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(K.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 				THEN CAST(CAST(LEFT(K.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(K.TaishoNenGetsudo, 4) ");
		sql.append(" 			END + '/04' <= KIHON.TaishoNenGetsudo AND ");
		sql.append(" 			KIHON.TaishoNenGetsudo <= K.TaishoNenGetsudo ");
		sql.append(" 	 ) ");
		sql.append(" 	+ ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			COUNT(MEISAI.KintaiKbn) ");
		sql.append(" 		FROM ");
		sql.append(" 			KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 		WHERE ");
		sql.append(" 			MEISAI.ShainNO = M.ShainNO AND ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 				THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 			END + '/04' <= MEISAI.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.TaishoNenGetsudo <= M.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.KintaiKbn = '06' ");
		sql.append(" 	) AS YukyuKyukaZan ");

		sql.append(" FROM ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI M ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_SHUKKINBO_KIHON K ");
		sql.append(" ON ");
		sql.append(" 	K.TaishoNenGetsudo = M.TaishoNenGetsudo AND ");
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
		sql.append(" 	K0050.KbnCode = '0050' AND ");
		sql.append(" 	K0050.Code = K.KakuteiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0051 ");
		sql.append(" ON ");
		sql.append(" 	K0051.KbnCode = '0051' AND ");
		sql.append(" 	K0051.Code = M.ShukkinYoteiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN M0100 ");
		sql.append(" ON ");
		sql.append(" 	M0100.KbnCode = '0100' AND");
		sql.append(" 	M0100.Code = M.KintaiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0101A ");
		sql.append(" ON ");
		sql.append(" 	K0101A.KbnCode = '0101' AND ");
		sql.append(" 	K0101A.Code = M.KintaiShinseiKbn1 AND ");
		sql.append(" 	K0101A.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0101B ");
		sql.append(" ON ");
		sql.append(" 	K0101B.KbnCode = '0101' AND ");
		sql.append(" 	K0101B.Code = M.KintaiShinseiKbn2 AND ");
		sql.append(" 	K0101B.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_YUKYU_KYUKA_DAICHO Y");
		sql.append(" ON ");
		sql.append(" 	S.ShainNO = Y.ShainNO AND ");
		sql.append(" 	CASE ");
		sql.append(" 		WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 		THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 		ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 	END = Y.TaishoNendo");
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
			sql.append(" AND E.EigyoshoCode >=  ? ");
			pstmtf.addValue("String", fromEigyoshoCode);
		}

		if (StringUtils.isNotBlank(toEigyoshoCode)) {
			sql.append(" AND E.EigyoshoCode <=  ? ");
			pstmtf.addValue("String", toEigyoshoCode);
		}

		if (shoriKanoEigyoshos != null) {
			sql.append(" AND E.EigyoshoCode IN  ? ");
			pstmtf.addValue("String", shoriKanoEigyoshos);
		}

		if (StringUtils.isNotBlank(fromBushoCode)) {
			sql.append(" AND B.BushoCode >=  ? ");
			pstmtf.addValue("String", fromBushoCode);
		}

		if (StringUtils.isNotBlank(toBushoCode)) {
			sql.append(" AND B.BushoCode <=  ? ");
			pstmtf.addValue("String", toBushoCode);
		}

		if (StringUtils.isNotBlank(fromShainNo)) {
			sql.append(" AND K.ShainNO >=  ? ");
			pstmtf.addValue("String", fromShainNo);
		}

		if (StringUtils.isNotBlank(toShainNo)) {
			sql.append(" AND K.ShainNO <=  ? ");
			pstmtf.addValue("String", toShainNo);
		}

		if (StringUtils.isNotBlank(joken)) {
			sql.append(" AND K.KakuteiKbn =  ? ");
			pstmtf.addValue("String", joken);
		}

		sql.append(" 	AND S.ShainKbn <> '04' ");

		sql.append(" ORDER BY ");
		sql.append("     K.TaishoNenGetsudo ");

		if (output == "02") {
			sql.append("     ,E.EigyoshoCode ");
		}

		sql.append("     ,K.ShainNO ");
		sql.append("     ,M.TaishoNengappi ");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void chiChinginkeisansho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void kinYukyuKyukaDaicho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
}