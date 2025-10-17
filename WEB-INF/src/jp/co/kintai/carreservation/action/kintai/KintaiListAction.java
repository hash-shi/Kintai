package jp.co.kintai.carreservation.action.kintai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
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
		
		//対象年月初期値の取得
		String taishoNengetsu = "";
		// 取得
		ArrayList<HashMap<String, String>> mstKanris = PJActionBase.getMstKanris(con, "01");
		// 送信データを減らすため不要なカラムは削って対象年月のみ返す。
		for (HashMap<String, String> hashMap : mstKanris) {
			taishoNengetsu = hashMap.get("GenzaishoriNengetsudo");
		}
		
		req.setAttribute("result", taishoNengetsu);
		this.setView("success");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void kinShukkinBo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		// 画面パラメータの取得
		int count = 0;
		String fromTaishoNengetsu	= req.getParameter("srhTxtTaishoNengetsuF");
		String toTaishoNengetsu		= req.getParameter("srhTxtTaishoNengetsuT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		String joken				= req.getParameter("srhSelJoken");
		
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
		
		// ユーザー情報の取得
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		ArrayList<String> shoriKanoEigyoshoCode = userInformation.getShoriKanoEigyoshoCode();
		
	    sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
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
		sql.append(" 	K0050.KbnCode = '0050'");
		sql.append(" AND ");
		sql.append(" 	K0050.Code = K.KakuteiKbn ");
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
		   sql.append(" AND CAST(E.EigyoshoCode AS int) IN ( ");
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
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			rset.next();
			count = rset.getInt("CNT");
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		if(count == 0) {
	    	this.addContent("result", false);
			this.addContent("message","対象データが存在しません。");
	    } else {
	    	this.addContent("result", true);
	    }
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void chiChinginkeisansho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// DB接続
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		// 画面パラメータの取得
		int count = 0;
		String fromTaishoNengetsu	= req.getParameter("srhTxtTaishoNengetsuF");
		String toTaishoNengetsu		= req.getParameter("srhTxtTaishoNengetsuT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		String joken				= req.getParameter("srhSelJoken");
		
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
		
		// ユーザー情報の取得
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		ArrayList<String> shoriKanoEigyoshoCode = userInformation.getShoriKanoEigyoshoCode();
		
	    sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
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
		   sql.append(" AND CAST(E.EigyoshoCode AS int) IN ( ");
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
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			rset.next();
			count = rset.getInt("CNT");
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		if(count == 0) {
	    	this.addContent("result", false);
			this.addContent("message","対象データが存在しません。");
	    } else {
	    	this.addContent("result", true);
	    }
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void kinYukyuKyukaDaicho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		// 画面パラメータの取得
		int count = 0;
		String fromTaishoNendo		= req.getParameter("srhTxtTaishoNendoF");
		String toTaishoNendo		= req.getParameter("srhTxtTaishoNendoT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		
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
		
		// ユーザー情報の取得
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		ArrayList<String> shoriKanoEigyoshoCode = userInformation.getShoriKanoEigyoshoCode();
		
	    sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
		sql.append(" FROM ");
		sql.append(" 	KIN_YUKYU_KYUKA_DAICHO Y ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_SHUKKINBO_KIHON SK ");
		sql.append(" ON ");
		sql.append(" 	Y.ShainNO = SK.ShainNO ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	CHI_CHINGINKEISANSHO_KIHON CK ");
		sql.append(" ON ");
		sql.append(" 	Y.ShainNO = CK.ShainNO ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_SHAIN S ");
		sql.append(" ON ");
		sql.append(" 	S.ShainNO = Y.ShainNO ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_EIGYOSHO E ");
		sql.append(" ON ");
		sql.append(" 	S.EigyoshoCode = E.EigyoshoCode ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_BUSHO B ");
		sql.append(" ON ");
		sql.append(" 	S.BushoCode = B.BushoCode ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		
		if (StringUtils.isNotBlank(fromTaishoNendo)) {
			sql.append(" AND Y.TaishoNendo >=  ? ");
			sql.append(" OR CASE ");
			sql.append(" 	WHEN RIGHT(SK.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(SK.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(SK.TaishoNenGetsudo, 4) ");
			sql.append(" END >= ?");
			sql.append(" OR CASE ");
			sql.append(" 	WHEN RIGHT(CK.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(CK.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(CK.TaishoNenGetsudo, 4) ");
			sql.append(" END >= ?");
			pstmtf.addValue("String", fromTaishoNendo);
			pstmtf.addValue("String", fromTaishoNendo);
			pstmtf.addValue("String", fromTaishoNendo);
		}
		
		if (StringUtils.isNotBlank(toTaishoNendo)) {
			sql.append(" AND Y.TaishoNendo <=  ? ");			
			sql.append(" OR CASE ");
			sql.append(" 	WHEN RIGHT(SK.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(SK.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(SK.TaishoNenGetsudo, 4) ");
			sql.append(" END <= ?");
			sql.append(" OR CASE ");
			sql.append(" 	WHEN RIGHT(CK.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
			sql.append(" 	THEN CAST(CAST(LEFT(CK.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
			sql.append(" 	ELSE LEFT(CK.TaishoNenGetsudo, 4) ");
			sql.append(" END <= ?");
			pstmtf.addValue("String", toTaishoNendo);
			pstmtf.addValue("String", toTaishoNendo);
			pstmtf.addValue("String", toTaishoNendo);
		}

		if (StringUtils.isNotBlank(fromShainNo)) {
			sql.append(" AND CAST(Y.ShainNO AS int) >=  ? ");
			pstmtf.addValue("String", fromShainNo);
		}
		
		if (StringUtils.isNotBlank(toShainNo)) {
			sql.append(" AND CAST(Y.ShainNO AS int) <=  ? ");
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
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			rset.next();
			count = rset.getInt("CNT");
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		if(count == 0) {
	    	this.addContent("result", false);
			this.addContent("message","対象データが存在しません。");
	    } else {
	    	this.addContent("result", true);
	    }
		
	}
}