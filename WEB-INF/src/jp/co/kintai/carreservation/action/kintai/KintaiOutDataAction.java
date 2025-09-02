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
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiOutDataAction extends PJActionBase {
	public KintaiOutDataAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
				
		//=====================================================================
		// 結果返却
		//=====================================================================
		// 取得
		ArrayList<HashMap<String, String>> mstKubun = PJActionBase.getMstKubuns(con, "0503", "", "");
		req.setAttribute("mstKubun", mstKubun);
		
		this.setView("success");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void kyuyokeisanData(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		int count = 0;
		String taishoNengetsuF	= req.getParameter("srhTxtTaishoNengetsuF");
		String taishoNengetsuT	= req.getParameter("srhTxtTaishoNengetsuT");
		
		sql.append("SELECT COUNT(*) AS CNT ");
		sql.append("FROM KIN_SHUKKINBO_KIHON skihon ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(taishoNengetsuF)) {
			sql.append(" AND skihon.TaishoNenGetsudo >= ? ");
			pstmtf.addValue("String", taishoNengetsuF);
		}
		if (StringUtils.isNotBlank(taishoNengetsuT)) {
			sql.append(" AND skihon.TaishoNenGetsudo <= ? ");
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
			rset.next();
			count = rset.getInt("CNT");
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
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
	public void chinginkeisanshoData(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		int count = 0;
		String taishoNengetsuF	= req.getParameter("srhTxtTaishoNengetsuF");
		String taishoNengetsuT	= req.getParameter("srhTxtTaishoNengetsuT");
		
		sql.append("SELECT COUNT(*) AS CNT ");
		sql.append("FROM CHI_CHINGINKEISANSHO_KIHON ckihon ");
		sql.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(taishoNengetsuF)) {
			sql.append(" AND ckihon.TaishoNenGetsudo >= ? ");
			pstmtf.addValue("String", taishoNengetsuF);
		}
		if (StringUtils.isNotBlank(taishoNengetsuT)) {
			sql.append(" AND ckihon.TaishoNenGetsudo <= ? ");
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
			rset.next();
			count = rset.getInt("CNT");
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		if(count == 0) {
	    	this.addContent("result", false);
			this.addContent("message","対象データが存在しません。");
	    } else {
	    	this.addContent("result", true);
	    }
	}
}