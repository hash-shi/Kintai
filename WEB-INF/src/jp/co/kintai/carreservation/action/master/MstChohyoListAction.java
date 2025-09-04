package jp.co.kintai.carreservation.action.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstChohyoListAction extends PJActionBase {
	public MstChohyoListAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		ResultSet rset					= null;
		
		//営業所初期値取得
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		sql.append(" SELECT ");
		sql.append(" 	 MIN(EigyoshoCode) AS eigyoshoCodeF ");
		sql.append(" 	,MAX(EigyoshoCode) AS eigyoshoCodeT ");
		sql.append(" FROM");
		sql.append("   MST_EIGYOSHO");
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			ResultSetMetaData metaData = rset.getMetaData(); 
			
			// カラム数(列数)の取得
			int colCount = metaData.getColumnCount(); 
			
			rset.next();
			
			// 1レコード分の配列を用意
			HashMap<String, String> record = new HashMap<String, String>();
			// カラム名をkeyとして値を格納
			for (int i = 1; i <= colCount; i++) {
				record.put(metaData.getColumnLabel(i), StringUtils.stripToEmpty(rset.getString(i)));
			}
			// 配列の格納
			mstDatas.add(record);
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		// 処理選択取得
		ArrayList<HashMap<String, String>> mstKubun0501 = PJActionBase.getMstKubuns(con, "0501", "", "");
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		req.setAttribute("mstDatas", mstDatas);
		req.setAttribute("mstKubun0501", mstKubun0501);
		
		// 画面表示
		this.setView("success");
	}
	
	public void eigyosho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		// 画面パラメータの取得
		String eigyoshoCodeF		= req.getParameter("srhTxtEigyoshoCodeF");
		String eigyoshoCodeT		= req.getParameter("srhTxtEigyoshoCodeT");
		String saishuKoshinDateF	= req.getParameter("srhTxtSaishuKoshinDateF");
		String saishuKoshinDateT	= req.getParameter("srhTxtSaishuKoshinDateT");
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_EIGYOSHO ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(eigyoshoCodeF)) {
			sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
			pstmtf.addValue("String", eigyoshoCodeF);
		}
		
		if (StringUtils.isNotBlank(eigyoshoCodeT)) {
			sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
			pstmtf.addValue("String", eigyoshoCodeT);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateF)) {
			sql.append(" AND SaishuKoshinDate >= ?");
			pstmtf.addValue("String", saishuKoshinDateF);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateT)) {
			sql.append(" AND SaishuKoshinDate <= ?");
			pstmtf.addValue("String", saishuKoshinDateT);
		}
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if (rset.next()) {
				this.addContent("result", true);
			} else {
		    	this.addContent("result", false);
				this.addContent("message","対象データが存在しません。");
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
	public void busho(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		// 画面パラメータの取得
		String eigyoshoCodeF		= req.getParameter("srhTxtEigyoshoCodeF");
		String eigyoshoCodeT		= req.getParameter("srhTxtEigyoshoCodeT");
		String bushoCodeF			= req.getParameter("srhTxtBushoCodeF");
		String bushoCodeT			= req.getParameter("srhTxtBushoCodeT");
		String saishuKoshinDateF	= req.getParameter("srhTxtSaishuKoshinDateF");
		String saishuKoshinDateT	= req.getParameter("srhTxtSaishuKoshinDateT");
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_BUSHO ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(eigyoshoCodeF)) {
			sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
			pstmtf.addValue("String", eigyoshoCodeF);
		}
		
		if (StringUtils.isNotBlank(eigyoshoCodeT)) {
			sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
			pstmtf.addValue("String", eigyoshoCodeT);
		}
		
		if (StringUtils.isNotBlank(bushoCodeF)) {
			sql.append(" AND CAST(BushoCode AS int) >= ? ");
			pstmtf.addValue("String", bushoCodeF);
		}
			
		if (StringUtils.isNotBlank(bushoCodeT)) {
			sql.append(" AND CAST(BushoCode AS int) <= ? ");
			pstmtf.addValue("String", bushoCodeT);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateF)) {
			sql.append(" AND SaishuKoshinDate >= ?");
			pstmtf.addValue("String", saishuKoshinDateF);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateT)) {
			sql.append(" AND SaishuKoshinDate <= ?");
			pstmtf.addValue("String", saishuKoshinDateT);
		}
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if (rset.next()) {
				this.addContent("result", true);
			} else {
		    	this.addContent("result", false);
				this.addContent("message","対象データが存在しません。");
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
	public void shain(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		// 画面パラメータの取得
		String eigyoshoCodeF		= req.getParameter("srhTxtEigyoshoCodeF");
		String eigyoshoCodeT		= req.getParameter("srhTxtEigyoshoCodeT");
		String shainNoF				= req.getParameter("srhTxtShainNOF");
		String shainNoT				= req.getParameter("srhTxtShainNOT");
		String saishuKoshinDateF	= req.getParameter("srhTxtSaishuKoshinDateF");
		String saishuKoshinDateT	= req.getParameter("srhTxtSaishuKoshinDateT");
		
	    sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_SHAIN ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(eigyoshoCodeF)) {
			sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
			pstmtf.addValue("String", eigyoshoCodeF);
		}
		
		if (StringUtils.isNotBlank(eigyoshoCodeT)) {
			sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
			pstmtf.addValue("String", eigyoshoCodeT);
		}
		
		if (StringUtils.isNotBlank(shainNoF)) {
			sql.append(" AND CAST(ShainNO AS int) >= ? ");
			pstmtf.addValue("String", shainNoF);
		}
			
		if (StringUtils.isNotBlank(shainNoT)) {
			sql.append(" AND CAST(ShainNO AS int) <= ? ");
			pstmtf.addValue("String", shainNoT);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateF)) {
			sql.append(" AND SaishuKoshinDate >= ?");
			pstmtf.addValue("String", saishuKoshinDateF);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateT)) {
			sql.append(" AND SaishuKoshinDate <= ?");
			pstmtf.addValue("String", saishuKoshinDateT);
		}
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if (rset.next()) {
				this.addContent("result", true);
			} else {
		    	this.addContent("result", false);
				this.addContent("message","対象データが存在しません。");
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
	public void kbn(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con					= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		// 画面パラメータの取得
		String kbnCodeF				= req.getParameter("srhTxtKbnCodeF");
		String kbnCodeT				= req.getParameter("srhTxtKbnCodeT");
		String saishuKoshinDateF	= req.getParameter("srhTxtSaishuKoshinDateF");
		String saishuKoshinDateT	= req.getParameter("srhTxtSaishuKoshinDateT");
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_KUBUN ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(kbnCodeF)) {
			sql.append(" AND CAST(KbnCode AS int) >= ? ");
			pstmtf.addValue("String", kbnCodeF);
		}
		
		if (StringUtils.isNotBlank(kbnCodeT)) {
			sql.append(" AND CAST(KbnCode AS int) <= ? ");
			pstmtf.addValue("String", kbnCodeT);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateF)) {
			sql.append(" AND SaishuKoshinDate >= ?");
			pstmtf.addValue("String", saishuKoshinDateF);
		}
		
		if(StringUtils.isNotBlank(saishuKoshinDateT)) {
			sql.append(" AND SaishuKoshinDate <= ?");
			pstmtf.addValue("String", saishuKoshinDateT);
		}
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if (rset.next()) {
				this.addContent("result", true);
			} else {
		    	this.addContent("result", false);
				this.addContent("message","対象データが存在しません。");
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
}