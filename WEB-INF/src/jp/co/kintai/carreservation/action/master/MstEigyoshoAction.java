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

public class MstEigyoshoAction extends PJActionBase {
	public MstEigyoshoAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
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
		String eigyoshoCode	= req.getParameter("srhTxtEigyoshoCode");
		// 3桁0詰めをする
		eigyoshoCode = String.format("%3s", eigyoshoCode).replace(" ", "0");
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);
		
		//=====================================================================
		// 検索
		//=====================================================================

		String isNew = "0";
		ArrayList<HashMap<String, String>> mstDatas = PJActionBase.getMstEigyoshos(con, eigyoshoCode, null);
		
		// データが0件 = 新規の時は空の配列を作成する。
		if (mstDatas.size() == 0) {
			// 新規モード
			isNew = "1";
			// 1レコード分の配列を用意
			HashMap<String, String> record = new HashMap<String, String>();
			record.put("EigyoshoCode", eigyoshoCode);
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
		String eigyoshoCode	= req.getParameter("txtEigyoshoCode");
		
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
		sql.append(" DELETE FROM MST_EIGYOSHO ");
		sql.append(" WHERE ");
		sql.append(" 1 = 1 ");
		sql.append(" and EigyoshoCode = ? ");
		
		// パラメータ設定
		pstmtf.addValue("String", eigyoshoCode);
		
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
		String isNew		= req.getParameter("hdnIsNew");
		String eigyoshoCode	= req.getParameter("txtEigyoshoCode");
		String eigyoshoName	= req.getParameter("txtEigyoshoName");
		
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
		
		if ("1".equals(isNew)) {
			
			//=====================================================================
			// 登録
			//=====================================================================
			pstmtf.clear();
			sql.setLength(0);
			sql.append(" INSERT INTO MST_EIGYOSHO ( ");
			sql.append("  EigyoshoCode ");
			sql.append("  ,EigyoshoName ");
			sql.append("  ,SaishuKoshinShainNO ");
			sql.append("  ,SaishuKoshinDate ");
			sql.append("  ,SaishuKoshinJikan ");
			sql.append(" ) VALUES ( ");
			sql.append("  ? ");
			sql.append("  ,? ");
			sql.append("  ,? ");
			sql.append("  ,? ");
			sql.append("  ,? ");
			sql.append(" ) ");
			
			// パラメータ設定
			pstmtf.addValue("String", eigyoshoCode);
			pstmtf.addValue("String", eigyoshoName);
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
			
		} else {
			
			//=====================================================================
			// 更新
			//=====================================================================
			pstmtf.clear();
			sql.setLength(0);
			sql.append(" UPDATE MST_EIGYOSHO SET ");
			sql.append("  SaishuKoshinShainNO = ? ");
			sql.append("  ,SaishuKoshinDate = ?  ");
			sql.append("  ,SaishuKoshinJikan = ?  ");
			sql.append("  ,EigyoshoName = ? ");
			sql.append(" WHERE ");
			sql.append(" 	1 = 1 ");
			sql.append(" AND EigyoshoCode = ? ");
			
			// パラメータ設定
			pstmtf.addValue("String", userInformation.getShainNO());
			pstmtf.addValue("String", PJActionBase.getNowDate());
			pstmtf.addValue("String", PJActionBase.getNowTime());
			pstmtf.addValue("String", eigyoshoName);
			pstmtf.addValue("String", eigyoshoCode);
			
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
}