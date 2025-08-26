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

public class MstKanriAction extends PJActionBase {
	public MstKanriAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
				ArrayList<HashMap<String, String>> mstKubun0505 = PJActionBase.getMstKubuns(con, "0505", "", "");
				req.setAttribute("mstKubun0505", mstKubun0505);
				
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
		String kanriCode	= req.getParameter("srhKanriCode");
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);
		
		//=====================================================================
		// 検索
		//=====================================================================

			ArrayList<HashMap<String, String>> mstDatas = PJActionBase.getMstKanris(con, kanriCode);
			

			for (HashMap<String, String> rec : mstDatas) {
			    System.out.println("レコード: " + rec);
			}
			
		this.addContent("mstDatas", mstDatas != null ? mstDatas : new ArrayList<>());

	}
	
	public void update(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	public void update_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String nendoKakuteiStatus	= req.getParameter("txtNendoKakuteiStatus");
		String genzaishoriNengetsudo	= req.getParameter("txtGenzaishoriNengetsudo");
		String kintaiKishuGetsudo	= req.getParameter("txtKintaiKishuGetsudo");
		String kintaiGetsudoShimebi		= req.getParameter("txtKintaiGetsudoShimebi");
		String kintaiKihonSagyoJikan	= req.getParameter("txtKintaiKihonSagyoJikan");
		String kanriCode = req.getParameter("txtKanriCode");
				
		
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
		sql.append(" UPDATE MST_KANRI SET ");
		sql.append("  NendoKakuteiStatus = ?  ");
		sql.append("  ,GenzaishoriNengetsudo = ?  ");
		sql.append("  ,KintaiKishuGetsudo = ? ");
		sql.append("  ,KintaiGetsudoShimebi = ? ");
		sql.append("  ,KintaiKihonSagyoJikan = ? ");
		sql.append("  ,SaishuKoshinShainNO = ? ");
		sql.append("  ,SaishuKoshinDate = ? ");
		sql.append("  ,SaishuKoshinJikan = ? ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		sql.append(" AND CAST(KanriCode AS int) = ? ");
			
		// パラメータ設定		
		pstmtf.addValue("String", nendoKakuteiStatus);
		pstmtf.addValue("String", genzaishoriNengetsudo);
		pstmtf.addValue("String", kintaiKishuGetsudo);
		pstmtf.addValue("String", kintaiGetsudoShimebi);
		pstmtf.addValue("String", kintaiKihonSagyoJikan);
		pstmtf.addValue("String", userInformation.getShainNO());
		pstmtf.addValue("String", PJActionBase.getNowDate());
		pstmtf.addValue("String", PJActionBase.getNowTime());
		pstmtf.addValue("String", kanriCode);
		
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