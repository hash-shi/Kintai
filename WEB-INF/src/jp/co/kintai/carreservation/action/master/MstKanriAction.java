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

public class MstKanriAction extends PJActionBase {
	public MstKanriAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 画面表示
		this.setView("success");
	}
	
	
	public static ArrayList<HashMap<String, String>> getMstKanris(Connection con, String kanriCode) throws Exception {
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append("	KanriCode");
		sql.append("	,NendoKakuteiStatus");
		sql.append("	,GenzaishoriNengetsudo");
		sql.append("	,KintaiKishuGetsudo");
		sql.append("	,KintaiGetsudoShimebi");
		sql.append("	,KintaiKihonSagyoJikan");
		sql.append("	,SaishuKoshinShainNO");
		sql.append("	,SaishuKoshinDate");
		sql.append("	,SaishuKoshinJikan");
		sql.append(" FROM ");
		sql.append("	MST_KANRI");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		
		if (StringUtils.isNotBlank(kanriCode)) {
			sql.append(" AND CAST(KanriCode AS int) = ? ");
			pstmtf.addValue("String", kanriCode);
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

		//String isNew = "0";
			ArrayList<HashMap<String, String>> mstDatas = MstKanriAction.getMstKanris(con, kanriCode);
			
			//debakku
			System.out.println("MSTデータ取得件数: " + mstDatas.size());
			for (HashMap<String, String> rec : mstDatas) {
			    System.out.println("レコード: " + rec);
			}
			
			
//			HashMap<String, String> record = new HashMap<String, String>();
//
//					record.put("KanriCode",kanriCode);
//					record.put("NendoKakuteiStatus", "");
//					record.put("GenzaishoriNengetsudo", "");
//					record.put("KintaiKishuGetsudo", "");
//					record.put("KintaiGetsudoShimebi", "");
//					record.put("KintaiKihonSagyoJikan", "");
//					record.put("SaishuKoshinShainNO", "");
//					record.put("SaishuKoshinDate", "");
//					record.put("SaishuKoshinJikan", "");
//					mstDatas.add(record);

				    this.addContent("mstDatas", mstDatas != null ? mstDatas : new ArrayList<>());
				    
				    
				    //debakku
				    System.out.println("返却するmstDatas: " + mstDatas);
	}
}