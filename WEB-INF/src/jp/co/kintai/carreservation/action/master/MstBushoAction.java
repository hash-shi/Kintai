package jp.co.kintai.carreservation.action.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstBushoAction extends PJActionBase {
	public MstBushoAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// 結果返却
		//=====================================================================
				
		// 処理選択
		ArrayList<HashMap<String, String>> mstKubun0153 = PJActionBase.getMstKubuns(con, "0153", "", "");
		req.setAttribute("mstKubun0153", mstKubun0153);
		
		// 画面表示
		this.setView("success");
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String bushoCode	= this.getParameter("srhTxtBushoCode");
		// 4桁詰めをする
		bushoCode = String.format("%4s", bushoCode).replace(" ", "0");
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);
		
		//=====================================================================
		// 検索
		//=====================================================================
		
		String isNew = "0";
		ArrayList<HashMap<String, String>> mstDatas = PJActionBase.getMstBushos(con, bushoCode, null, null);
		
		// データが0件 = 新規の時は空の配列を作成する。
		if (mstDatas.size() == 0) {
			// 新規モード
			isNew = "1";
			// 1レコード分の配列を用意
			HashMap<String, String> record = new HashMap<String, String>();
			record.put("BushoCode", bushoCode);
			record.put("BushoName", "");
			record.put("BushoKbn", "");
			record.put("KbnName", "");
			record.put("EigyoshoCode", "");
			record.put("EigyoshoName", "");
			record.put("SaishuKoshinShainNO", "");
			record.put("SaishuKoshinShainName", "");
			record.put("SaishuKoshinDate", "");
			record.put("SaishuKoshinJikan", "");
			// 配列の格納
			mstDatas.add(record);
		}
		
		this.addContent("isNew", isNew);
		this.addContent("mstDatas", mstDatas);
		
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void delete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 入力値チェック用の受けメソッドなので特に処理はない。
	}
	public void delete_(HttpServletRequest req, HttpServletResponse res) throws Exception {

		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String bushoCode	= this.getParameter("srhTxtBushoCode");
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);		
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		
		//=====================================================================
		// 削除
		//=====================================================================
		pstmtf.clear();
		sql.setLength(0);
		sql.append(" DELETE FROM MST_BUSHO ");
		sql.append(" WHERE ");
		sql.append(" 1 = 1 ");
		sql.append(" and BushoCode = ? ");
		
		// パラメータ設定
		pstmtf.addValue("String", bushoCode);
		
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmtf.setPreparedStatement(pstmt);
			pstmt.execute();
		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void insert(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	public void insert_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String bushoCode	= req.getParameter("txtBushoCode");
		String bushoName	= req.getParameter("txtBushoName");
		String bushoKbn		= req.getParameter("txtBushoKbn");
		String eigyoshoCode	= req.getParameter("txtEigyoshoCode");
		
		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);		
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();

		//=====================================================================
		// 登録
		//=====================================================================
		pstmtf.clear();
		sql.setLength(0);
		sql.append(" INSERT INTO MST_BUSHO ( ");
		sql.append("  BushoCode ");
		sql.append("  ,BushoName ");
		sql.append("  ,BushoKbn ");
		sql.append("  ,EigyoshoCode ");
		sql.append("  ,SaishuKoshinShainNO ");
		sql.append("  ,SaishuKoshinDate ");
		sql.append("  ,SaishuKoshinJikan ");
		sql.append(" ) VALUES ( ");
		sql.append("  ? ");
		sql.append("  ,? ");
		sql.append("  ,? ");
		sql.append("  ,? ");
		sql.append("  ,? ");
		sql.append("  ,? ");
		sql.append("  ,? ");
		sql.append(" ) ");
		
		// パラメータ設定
		pstmtf.addValue("String", bushoCode);
		pstmtf.addValue("String", bushoName);
		pstmtf.addValue("String", bushoKbn);
		pstmtf.addValue("String", eigyoshoCode);
		pstmtf.addValue("String", userInformation.getShainNO());
		pstmtf.addValue("String", PJActionBase.getNowDate());
		pstmtf.addValue("String", PJActionBase.getNowTime());
		
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmtf.setPreparedStatement(pstmt);
			pstmt.execute();
		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void update(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	public void update_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String bushoCode	= req.getParameter("txtBushoCode");
		String bushoName	= req.getParameter("txtBushoName");
		String bushoKbn		= req.getParameter("txtBushoKbn");
		String eigyoshoCode	= req.getParameter("txtEigyoshoCode");
				
		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);		
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		
		//=====================================================================
		// 更新
		//=====================================================================
		pstmtf.clear();
		sql.setLength(0);
		sql.append(" UPDATE MST_BUSHO SET ");
		sql.append("  SaishuKoshinShainNO = ? ");
		sql.append("  ,SaishuKoshinDate = ?  ");
		sql.append("  ,SaishuKoshinJikan = ?  ");
		sql.append("  ,BushoName = ? ");
		sql.append("  ,BushoKbn = ? ");
		sql.append("  ,EigyoshoCode = ? ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		sql.append(" AND BushoCode = ? ");
			
		// パラメータ設定
		pstmtf.addValue("String", userInformation.getShainNO());
		pstmtf.addValue("String", PJActionBase.getNowDate());
		pstmtf.addValue("String", PJActionBase.getNowTime());
		pstmtf.addValue("String", bushoName);
		pstmtf.addValue("String", bushoKbn);
		pstmtf.addValue("String", eigyoshoCode);
		pstmtf.addValue("String", bushoCode);
			
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmtf.setPreparedStatement(pstmt);
			pstmt.execute();
		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
}
