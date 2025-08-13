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
import jp.co.kintai.carreservation.define.Define;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstChohyoListAction extends PJActionBase {
	public MstChohyoListAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 検索条件取得
		String kbnCode	= "0501";
				
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		ResultSet rset					= null;
		
		//営業所初期値取得
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		sql.append(" SELECT ");
		sql.append(" 	 MIN(EigyoshoCode) AS Saisho ");
		sql.append(" 	,MAX(EigyoshoCode) AS Saidai ");
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
			
			// レコード数分繰り返す
			while (rset.next()){
				// 1レコード分の配列を用意
				HashMap<String, String> record = new HashMap<String, String>();
				// カラム名をkeyとして値を格納
				for (int i = 1; i <= colCount; i++) {
					record.put(metaData.getColumnLabel(i), StringUtils.stripToEmpty(rset.getString(i)));
				}
				// 配列の格納
				mstDatas.add(record);
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		// 処理選択取得
		ArrayList<HashMap<String, String>> shoriSentaku = PJActionBase.getMstKubuns(con, kbnCode, "", "");
				
		//=====================================================================
		// 結果返却
		//=====================================================================
		req.setAttribute("shoriSentaku", shoriSentaku);
		req.setAttribute("eigyosho", mstDatas);
		//this.addContent("name", kbnName);
		// 画面表示
		this.setView("success");
	}
	
	public void check(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		// 画面パラメータの取得
		String shorisentaku	= req.getParameter("numSrhShorisentaku");
		String fromSaishuKoshinDate	= req.getParameter("txtSrhSaishuKoshinDateF");
		String toSaishuKoshinDate	= req.getParameter("txtSrhSaishuKoshinDateT");
		int count = 0;
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		if(shorisentaku.equals("01")||shorisentaku.equals("02")||shorisentaku.equals("03")) {
			String fromEigyoshoCode	= req.getParameter("txtSrhEigyoshoCodeF");
			String toEigyoshoCode	= req.getParameter("txtSrhEigyoshoCodeT");
		  if(shorisentaku.equals("02")) {
			String fromBushoCode	= req.getParameter("txtSrhBushoCodeF");
			String toBushoCode	= req.getParameter("txtSrhBushoCodeT");
			mstDatas = bushoMst(con,fromEigyoshoCode,toEigyoshoCode,fromBushoCode,toBushoCode,fromSaishuKoshinDate,toSaishuKoshinDate);
		  } else if(shorisentaku.equals("03")) {
			String fromShainNO	= req.getParameter("txtSrhShainNOF");
		    String toShainNO	= req.getParameter("txtSrhShainNOT");
		    mstDatas = shainMst(con,fromEigyoshoCode,toEigyoshoCode,fromShainNO,toShainNO,fromSaishuKoshinDate,toSaishuKoshinDate);
		  } else {
			mstDatas = eigyoshoMst(con,fromEigyoshoCode,toEigyoshoCode,fromSaishuKoshinDate,toSaishuKoshinDate);
		  }
		} else {
			String fromKbnCode	= req.getParameter("txtSrhKbnCodeF");
			String toKbnCode	= req.getParameter("txtSrhKbnCodeT");
			mstDatas = kbnMst(con,fromKbnCode,toKbnCode,fromSaishuKoshinDate,toSaishuKoshinDate);
		}
		
		count = mstDatas.size();
		System.out.println("マスタデータ数:" + count);
		if(count == 0) {
	    	this.addContent("result", false);
			this.addContent("message","対象データが存在しません。");
	    } else {
	    	this.addContent("result", true);
	    	req.getSession().setAttribute(Define. SESSION_ID_CSV, mstDatas);
	    }
	}
	
	private static ArrayList<HashMap<String, String>> eigyoshoMst(Connection con, String fromEigyoshoCode, String toEigyoshoCode, String fromSaishuKoshinDate, String toSaishuKoshinDate) throws Exception {
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_EIGYOSHO ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
		 sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
	     pstmtf.addValue("String", fromEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
	     sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
		 pstmtf.addValue("String", toEigyoshoCode);
		}
		
		if(StringUtils.isNotBlank(fromSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate >= ?");
		 pstmtf.addValue("String", fromSaishuKoshinDate);
		}
		
		if(StringUtils.isNotBlank(toSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate <= ?");
		 pstmtf.addValue("String", toSaishuKoshinDate);
		}
		
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
				mstDatas.add(record);
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return mstDatas;
	}

	private static ArrayList<HashMap<String, String>> bushoMst(Connection con, String fromEigyoshoCode, String toEigyoshoCode, String fromBushoCode,String toBushoCode,String fromSaishuKoshinDate, String toSaishuKoshinDate) throws Exception {
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_BUSHO ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
		 sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
	     pstmtf.addValue("String", fromEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
	     sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
		 pstmtf.addValue("String", toEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(fromBushoCode)) {
	     sql.append(" AND CAST(BushoCode AS int) >= ? ");
		 pstmtf.addValue("String", fromBushoCode);
		}
			
		if (StringUtils.isNotBlank(toBushoCode)) {
		 sql.append(" AND CAST(BushoCode AS int) <= ? ");
		 pstmtf.addValue("String", toBushoCode);
		}
		
		if(StringUtils.isNotBlank(fromSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate >= ?");
		 pstmtf.addValue("String", fromSaishuKoshinDate);
		}
		
		if(StringUtils.isNotBlank(toSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate <= ?");
		 pstmtf.addValue("String", toSaishuKoshinDate);
		}
		
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
				mstDatas.add(record);
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return mstDatas;
	}
	
	private static ArrayList<HashMap<String, String>> shainMst(Connection con, String fromEigyoshoCode, String toEigyoshoCode, String fromShainNO,String toShainNO,String fromSaishuKoshinDate, String toSaishuKoshinDate) throws Exception {
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_SHAIN ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
		 sql.append(" AND CAST(EigyoshoCode AS int) >= ? ");
	     pstmtf.addValue("String", fromEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
	     sql.append(" AND CAST(EigyoshoCode AS int) <= ? ");
		 pstmtf.addValue("String", toEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(fromShainNO)) {
	     sql.append(" AND CAST(ShainNO AS int) >= ? ");
		 pstmtf.addValue("String", fromShainNO);
		}
			
		if (StringUtils.isNotBlank(toShainNO)) {
		 sql.append(" AND CAST(ShainNO AS int) <= ? ");
		 pstmtf.addValue("String", toShainNO);
		}
		
		if(StringUtils.isNotBlank(fromSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate >= ?");
		 pstmtf.addValue("String", fromSaishuKoshinDate);
		}
		
		if(StringUtils.isNotBlank(toSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate <= ?");
		 pstmtf.addValue("String", toSaishuKoshinDate);
		}
		
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
				mstDatas.add(record);
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return mstDatas;
	}
	
	private static ArrayList<HashMap<String, String>> kbnMst(Connection con, String fromKbnCode, String toKbnCode, String fromSaishuKoshinDate, String toSaishuKoshinDate) throws Exception {
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	MST_KUBUN ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(fromKbnCode)) {
		 sql.append(" AND CAST(KbnCode AS int) >= ? ");
	     pstmtf.addValue("String", fromKbnCode);
		}
		
		if (StringUtils.isNotBlank(toKbnCode)) {
	     sql.append(" AND CAST(KbnCode AS int) <= ? ");
		 pstmtf.addValue("String", toKbnCode);
		}
		
		if(StringUtils.isNotBlank(fromSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate >= ?");
		 pstmtf.addValue("String", fromSaishuKoshinDate);
		}
		
		if(StringUtils.isNotBlank(toSaishuKoshinDate)) {
		 sql.append(" AND SaishuKoshinDate <= ?");
		 pstmtf.addValue("String", toSaishuKoshinDate);
		}
		
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
				mstDatas.add(record);
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return mstDatas;
	}
	
}