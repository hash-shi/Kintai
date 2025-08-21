package jp.co.kintai.carreservation.action.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class MstKubunAction extends PJActionBase {
	public MstKubunAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
		String KbnCode	= this.getParameter("srhTxtKbnCode");
		String Code	= this.getParameter("srhTxtCode");
		
		// 4桁0詰めに統一するための処理
		// 数値に変換
		int KbnCode_ = Integer.parseInt(KbnCode);
		// 4桁0詰めに変換
		KbnCode = String.format("%04d", KbnCode_);
		
		// 2桁0詰めに統一するための処理
		// 数値に変換
		int Code_ = Integer.parseInt(Code);
		// 2桁0詰めに変換
		Code = String.format("%02d", Code_);
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);
		
		//=====================================================================
		// 検索
		//=====================================================================
		
		String isNew = "0";
		ArrayList<HashMap<String, String>> mstDatas = PJActionBase.getMstKubuns(con, KbnCode, Code, null);
		
		// データが0件 = 新規の時は空の配列を作成する。
		if (mstDatas.size() == 0) {
			// 新規モード
			isNew = "1";
			// 1レコード分の配列を用意
			HashMap<String, String> record = new HashMap<String, String>();
			record.put("KbnCode", KbnCode);
			record.put("Code", Code);
			record.put("KbnName", "");
			record.put("KbnRyaku", "");
			record.put("GroupCode1", "");
			record.put("GroupCode2", "");
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
		String KbnCode	= this.getParameter("txtKbnCode");
		String Code	= this.getParameter("txtCode");
		
		// 4桁0詰めに統一するための処理
		// 数値に変換
		int KbnCode_ = Integer.parseInt(KbnCode);
		// 4桁0詰めに変換
		KbnCode = String.format("%04d", KbnCode_);	

		// 2桁0詰めに統一するための処理
		// 数値に変換
		int Code_ = Integer.parseInt(Code);
		// 2桁0詰めに変換
		Code = String.format("%02d", Code_);
		
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
		sql.append(" DELETE FROM MST_KUBUN ");
		sql.append(" WHERE ");
		sql.append(" 1 = 1 ");
		sql.append(" AND CAST(KbnCode AS int) = ? ");
        sql.append(" AND CAST(Code AS int) = ? ");
		
		// パラメータ設定
		pstmtf.addValue("String", KbnCode);
		pstmtf.addValue("String", Code);
		
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
		String KbnCode	= req.getParameter("txtKbnCode");
		String Code	= req.getParameter("txtCode");
		String KbnName	= req.getParameter("txtKbnName");
		String KbnRyaku	= req.getParameter("txtKbnRyaku");
		String GroupCode1	= req.getParameter("txtGroupCode1");
		String GroupCode2	= req.getParameter("txtGroupCode2");
		
		// 4桁0詰めに統一するための処理
		// 数値に変換
		int KbnCode_ = Integer.parseInt(KbnCode);
		// 4桁0詰めに変換
		KbnCode = String.format("%04d", KbnCode_);

		// 2桁0詰めに統一するための処理
		// 数値に変換
		int Code_ = Integer.parseInt(Code);
		// 2桁0詰めに変換
		Code = String.format("%02d", Code_);

		// 4桁0詰めに統一するための処理
		// 数値に変換
		// 4桁0詰めに変換
		if(StringUtils.isNotBlank(GroupCode1)) {
			int GroupCode1_ = Integer.parseInt(GroupCode1);
			GroupCode1 = String.format("%04d", GroupCode1_);
		} 
		
		if(StringUtils.isNotBlank(GroupCode2)) {
			int GroupCode2_ = Integer.parseInt(GroupCode2);
			GroupCode2 = String.format("%04d", GroupCode2_);
		} 
		
		
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
		sql.append(" INSERT INTO MST_KUBUN ( ");
		sql.append("  KbnCode ");
		sql.append("  ,Code ");
		sql.append("  ,KbnName ");
		sql.append("  ,KbnRyaku ");
		sql.append("  ,GroupCode1 ");
		sql.append("  ,GroupCode2 ");
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
		sql.append("  ,? ");
		sql.append("  ,? ");
		sql.append(" ) ");
		
		// パラメータ設定
		pstmtf.addValue("String", KbnCode);
		pstmtf.addValue("String", Code);
		pstmtf.addValue("String", KbnName);
		pstmtf.addValue("String", KbnRyaku);
		pstmtf.addValue("String", GroupCode1);
		pstmtf.addValue("String", GroupCode2);
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
		String KbnCode	= req.getParameter("txtKbnCode");
		String Code	= req.getParameter("txtCode");
		String KbnName	= req.getParameter("txtKbnName");
		String KbnRyaku	= req.getParameter("txtKbnRyaku");
		String GroupCode1	= req.getParameter("txtGroupCode1");
		String GroupCode2	= req.getParameter("txtGroupCode2");
		
		// 4桁0詰めに統一するための処理
		// 数値に変換
		int KbnCode_ = Integer.parseInt(KbnCode);
		// 4桁0詰めに変換
		KbnCode = String.format("%04d", KbnCode_);

		// 2桁0詰めに統一するための処理
		// 数値に変換
		int Code_ = Integer.parseInt(Code);
		// 2桁0詰めに変換
		Code = String.format("%02d", Code_);

		// 4桁0詰めに統一するための処理
		// 数値に変換
		// 4桁0詰めに変換
		if(StringUtils.isNotBlank(GroupCode1)) {
			int GroupCode1_ = Integer.parseInt(GroupCode1);
			GroupCode1 = String.format("%04d", GroupCode1_);
		} 
		
		if(StringUtils.isNotBlank(GroupCode2)) {
			int GroupCode2_ = Integer.parseInt(GroupCode2);
			GroupCode2 = String.format("%04d", GroupCode2_);
		}
		
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
		sql.append(" UPDATE MST_KUBUN SET ");
		sql.append("  SaishuKoshinShainNO = ? ");
		sql.append("  ,SaishuKoshinDate = ?  ");
		sql.append("  ,SaishuKoshinJikan = ?  ");
		sql.append("  ,KbnName = ? ");
		sql.append("  ,KbnRyaku= ? ");
		sql.append("  ,GroupCode1 = ? ");
		sql.append("  ,GroupCode2 = ? ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		sql.append(" AND CAST(KbnCode AS int) = ? ");
		sql.append(" AND CAST(Code AS int) = ? ");
			
		// パラメータ設定
		pstmtf.addValue("String", userInformation.getShainNO());
		pstmtf.addValue("String", PJActionBase.getNowDate());
		pstmtf.addValue("String", PJActionBase.getNowTime());
		pstmtf.addValue("String", KbnName);
		pstmtf.addValue("String", KbnRyaku);
		pstmtf.addValue("String", GroupCode1);
	    pstmtf.addValue("String", GroupCode2);
		pstmtf.addValue("String", KbnCode);
		pstmtf.addValue("String", Code);
			
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
	
	public void copy_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// やりたい処理自体は登録処理と同一のため、insert_を呼び出すことで実施される。
		insert_(req, res);
	}
}
