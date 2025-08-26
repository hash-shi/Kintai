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
		
		// 2桁0詰めに統一するための処理
		// 数値に変換
		int kanriCode_ = Integer.parseInt(kanriCode);
		// 2桁0詰めに変換
		kanriCode = String.format("%02d", kanriCode_);
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);
		
		//=====================================================================
		// 検索
		//=====================================================================
		
		String isNew = "0";
		ArrayList<HashMap<String, String>> mstDatas = PJActionBase.getMstKanris(con, kanriCode);
		
		// データが0件 = 新規の時は空の配列を作成する。
		if (mstDatas.size() == 0) {
			// 新規モード
			isNew = "1";
			// 1レコード分の配列を用意
			HashMap<String, String> record = new HashMap<String, String>();
			record.put("KanriCode", kanriCode);
			record.put("NendoKakuteiStatus", "");
			record.put("GenzaishoriNengetsudo", "");
			record.put("KintaiKishuGetsudo", "");
			record.put("KintaiGetsudoShimebi", "");
			record.put("KintaiKihonSagyoJikan", "");
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
	
	public void update(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	public void update_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String kanriCode 				= req.getParameter("txtKanriCode");
		String nendoKakuteiStatus		= req.getParameter("txtNendoKakuteiStatus");
		String genzaishoriNengetsudo	= req.getParameter("txtGenzaishoriNengetsudo");
		String kintaiKishuGetsudo		= req.getParameter("txtKintaiKishuGetsudo");
		String kintaiGetsudoShimebi		= req.getParameter("txtKintaiGetsudoShimebi");
		String kintaiKihonSagyoJikan	= req.getParameter("txtKintaiKihonSagyoJikan");
		
		// 2桁0詰めに統一するための処理
		// 数値に変換
		int kanriCode_ = Integer.parseInt(kanriCode);
		// 2桁0詰めに変換
		kanriCode = String.format("%02d", kanriCode_);
		
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
		sql.append("  SaishuKoshinShainNO = ? ");
		sql.append("  ,SaishuKoshinDate = ? ");
		sql.append("  ,SaishuKoshinJikan = ? ");
		sql.append("  ,NendoKakuteiStatus = ?  ");
		sql.append("  ,GenzaishoriNengetsudo = ?  ");
		sql.append("  ,KintaiKishuGetsudo = ? ");
		sql.append("  ,KintaiGetsudoShimebi = ? ");
		sql.append("  ,KintaiKihonSagyoJikan = ? ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		sql.append(" AND CAST(KanriCode AS int) = ? ");
			
		// パラメータ設定
		pstmtf.addValue("String", userInformation.getShainNO());
		pstmtf.addValue("String", PJActionBase.getNowDate());
		pstmtf.addValue("String", PJActionBase.getNowTime());
		pstmtf.addValue("String", nendoKakuteiStatus);
		pstmtf.addValue("String", genzaishoriNengetsudo);
		pstmtf.addValue("String", kintaiKishuGetsudo);
		pstmtf.addValue("String", kintaiGetsudoShimebi);
		pstmtf.addValue("String", kintaiKihonSagyoJikan);
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