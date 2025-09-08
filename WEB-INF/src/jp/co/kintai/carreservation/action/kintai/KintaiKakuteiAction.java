package jp.co.kintai.carreservation.action.kintai;

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
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KintaiKakuteiAction extends PJActionBase {
	public KintaiKakuteiAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		this.setView("success");
	}

	/**
	 * 対象年月の初期値の取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getTaishoYM(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String result = "";
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		ResultSet rset					= null;
		
		sql.append(" SELECT TOP 1 GenzaishoriNengetsudo FROM MST_KANRI");
		
		try {
			// SQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if(rset.next()) {
				result = StringUtils.stripToEmpty(rset.getString("GenzaishoriNengetsudo"));
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		this.addContent("result", result);
	}
	
	/**
	 * 勤怠確定待ち一覧の取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void searchKintaiKakutei(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 検索条件取得
		String taishoYM			= this.getParameter("txtSearchedTaishoYM");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append("    SELECT ");
		sql.append("         Q1.EigyoshoCode, ");
		sql.append("         Q1.EigyoshoName, ");
		sql.append("         MAX(Q1.KakuteiKbn01) AS KakuteiKbn01, ");
		sql.append("         MAX(ISNULL(M1.KbnName, '')) AS KakuteiKbnName01, ");
		sql.append("         MAX(Q1.KakuteiKbn02) AS KakuteiKbn02, ");
		sql.append("         MAX(ISNULL(M2.KbnName, '')) AS KakuteiKbnName02 ");
		sql.append("     FROM ");
		sql.append("     ( ");
		sql.append("         SELECT ");
		sql.append("             COALESCE (M1.EigyoshoCode ,'') AS EigyoshoCode, ");
		sql.append("             COALESCE (M2.EigyoshoName, '') AS EigyoshoName, ");
		sql.append("             COALESCE (C1.KakuteiKbn, '')   AS KakuteiKbn01, ");
		sql.append("             '' AS KakuteiKbn02 ");
		sql.append("         FROM ");
		sql.append("             KIN_SHUKKINBO_KIHON C1 ");
		sql.append("         LEFT OUTER JOIN MST_SHAIN M1 ");
		sql.append("             ON  C1.ShainNO = M1.ShainNO ");
		sql.append("         LEFT OUTER JOIN MST_EIGYOSHO M2 ");
		sql.append("             ON  M1.EigyoshoCode = M2.EigyoshoCode ");
		sql.append("         LEFT OUTER JOIN MST_BUSHO M3 ");
		sql.append("             ON  M1.BushoCode = M3.BushoCode ");
		sql.append("         WHERE ");
		sql.append("             C1.TaishoNenGetsudo = ? ");
		sql.append("         GROUP BY ");
		sql.append("             M1.EigyoshoCode, ");
		sql.append("             M2.EigyoshoName, ");
		sql.append("             C1.KakuteiKbn ");
		sql.append("         UNION ALL ");
		sql.append("         SELECT ");
		sql.append("             COALESCE (C1.EigyoshoCode ,'') AS EigyoshoCode, ");
		sql.append("             COALESCE (M2.EigyoshoName, '') AS EigyoshoName, ");
		sql.append("             '' AS KakuteiKbn01, ");
		sql.append("             COALESCE (C1.KakuteiKbn, '')   AS KakuteiKbn02 ");
		sql.append("         FROM ");
		sql.append("             CHI_CHINGINKEISANSHO_KIHON C1 ");
		sql.append("         LEFT OUTER JOIN MST_SHAIN M1 ");
		sql.append("             ON  C1.ShainNO = M1.ShainNO ");
		sql.append("         LEFT OUTER JOIN MST_EIGYOSHO M2 ");
		sql.append("             ON  M1.EigyoshoCode = M2.EigyoshoCode ");
		sql.append("         LEFT OUTER JOIN MST_BUSHO M3 ");
		sql.append("             ON  M1.BushoCode = M3.BushoCode ");
		sql.append("         WHERE ");
		sql.append("             C1.TaishoNenGetsudo = ? ");
		sql.append("         GROUP BY ");
		sql.append("             C1.EigyoshoCode, ");
		sql.append("             M2.EigyoshoName, ");
		sql.append("             C1.KakuteiKbn ");
		sql.append("     ) Q1 ");
		sql.append("     LEFT OUTER JOIN MST_KUBUN M1 ");
		sql.append("         ON  M1.KbnCode = '0050' ");
		sql.append("         AND M1.Code    = Q1.KakuteiKbn01 ");
		sql.append("     LEFT OUTER JOIN MST_KUBUN M2 ");
		sql.append("         ON  M2.KbnCode = '0050' ");
		sql.append("         AND M2.Code    = Q1.KakuteiKbn02 ");
		sql.append("     GROUP BY ");
		sql.append("         Q1.EigyoshoCode, ");
		sql.append("         Q1.EigyoshoName ");
		sql.append("     ORDER BY ");
		sql.append("         Q1.EigyoshoCode, ");
		sql.append("         Q1.EigyoshoName ");
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoYM);
		
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
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		this.addContent("result", mstDatas);
		
	}
	
	public void kaijo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	
	public void kaijo_(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		String loginShainNo = userInformation.getShainNO();
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// 明細件数
		String count				= this.getParameter("kakuteiCount");
		int cnt						= Integer.parseInt(count);
		//対象年月
		String taishoYM			= this.getParameter("txtSearchedTaishoYM");
		//最終更新日
		String saishuKoshinDate	= PJActionBase.getNowDate();
		//最終更新時刻
		String saishuKoshinJikan	= PJActionBase.getNowTime();
				
		boolean result = false;
		//トランザクション開始
		con.setAutoCommit(false);
		
		for(int i = 0;i < cnt;i++){
			// 選択状態取得
			StringBuilder checkSb	= new StringBuilder();
			checkSb		.append("cbxKakutei")	.append(String.valueOf(i));
			String check				= this.getParameter(checkSb.toString());
			if(!check.equals("01")) {
			//選択されていない場合、その行の更新をしない
				continue;
			}
			// 月給制取得
			StringBuilder eigyoshoCodeSb	= new StringBuilder();
			eigyoshoCodeSb		.append("hdnTxtEigyoshoCode")	.append(String.valueOf(i));
			String eigyoshoCode				= this.getParameter(eigyoshoCodeSb.toString());
			String kakuteiKbn				= "02";

			//給休暇台帳(月給)の削除
			result = deleteGekkyu(con, taishoYM, eigyoshoCode);

			if(result) {
				//有給休暇台帳(時給日給)の削除
				result = deleteNikkyu(con, taishoYM, eigyoshoCode);
			}
		
			if(result) {
				//出勤簿基本の更新
				result = updateShukkinbo(con, kakuteiKbn, loginShainNo, saishuKoshinDate, saishuKoshinJikan, taishoYM, eigyoshoCode);
			}
		
			if(result) {
				//賃金計算書基本の更新
				result = updateChingin(con, kakuteiKbn, loginShainNo, saishuKoshinDate, saishuKoshinJikan, taishoYM, eigyoshoCode);
			}
		}
		
		if(result == false) {
			//ロールバック
			con.rollback();
		}
		
		else {
			//コミット
			con.commit();
		}
		
		this.addContent("result", result);
	}
	
	public void kakutei(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	
	public void kakutei_(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		String loginShainNo = userInformation.getShainNO();
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// 明細件数
		String count				= this.getParameter("kakuteiCount");
		int cnt						= Integer.parseInt(count);
		//対象年月
		String taishoYM			= this.getParameter("txtSearchedTaishoYM");
		//最終更新日
		String saishuKoshinDate	= PJActionBase.getNowDate();
		//最終更新時刻
		String saishuKoshinJikan	= PJActionBase.getNowTime();
				
		boolean result = false;
		//トランザクション開始
		con.setAutoCommit(false);
		
		for(int i = 0;i < cnt;i++){
			// 選択状態取得
			StringBuilder checkSb	= new StringBuilder();
			checkSb		.append("cbxKakutei")	.append(String.valueOf(i));
			String check				= this.getParameter(checkSb.toString());
			if(!check.equals("01")) {
			//選択されていない場合、その行の更新をしない
				continue;
			}
			// 月給制取得
			StringBuilder eigyoshoCodeSb	= new StringBuilder();
			eigyoshoCodeSb		.append("hdnTxtEigyoshoCode")	.append(String.valueOf(i));
			String eigyoshoCode				= this.getParameter(eigyoshoCodeSb.toString());
			String kakuteiKbn				= "03";
			//有給休暇台帳(月給)の削除
			result = deleteGekkyu(con, taishoYM, eigyoshoCode);
		
			if(result) {
				//有給休暇台帳(月給)の追加
				result = insertGekkyu(con, loginShainNo, saishuKoshinDate, saishuKoshinJikan, taishoYM, eigyoshoCode);
			}

			if(result) {
				//有給休暇台帳(時給日給)の削除
				result = deleteNikkyu(con, taishoYM, eigyoshoCode);
			}

			if(result) {
				//有給休暇台帳(時給日給)の追加
				result = insertNikkyu(con, loginShainNo, saishuKoshinDate, saishuKoshinJikan, taishoYM, eigyoshoCode);
			}
		
			if(result) {
				//出勤簿基本の更新
				result = updateShukkinbo(con, kakuteiKbn, loginShainNo, saishuKoshinDate, saishuKoshinJikan, taishoYM, eigyoshoCode);
			}
		
			if(result) {
				//賃金計算書基本の更新
				result = updateChingin(con, kakuteiKbn, loginShainNo, saishuKoshinDate, saishuKoshinJikan, taishoYM, eigyoshoCode);
			}
		}
		
		if(result == false) {
			//ロールバック
			con.rollback();
		}
	
		else {
			//コミット
			con.commit();
		}
		this.addContent("result", result);
	}

	/**
	 * 有給休暇台帳(月給)を削除
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean deleteGekkyu(Connection con, String taishoYM, String eigyoshoCode) throws Exception {
		boolean result = false;
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// 削除
		//=====================================================================
		pstmtf.clear();
		sql.setLength(0);
		sql.append(" DELETE FROM KIN_YUKYU_KYUKA_DAICHO");
		sql.append(" FROM  KIN_SHUKKINBO_KIHON C1  ");
		sql.append(" INNER JOIN MST_SHAIN M1  ");
		sql.append("     ON  C1.ShainNO = M1.ShainNO  ");
		sql.append(" INNER JOIN KIN_YUKYU_KYUKA_DAICHO  ");
		sql.append("     ON  C1.ShainNO = KIN_YUKYU_KYUKA_DAICHO.ShainNO  ");
		sql.append("     AND LEFT(C1.TaishoNenGetsudo, 4) = KIN_YUKYU_KYUKA_DAICHO.TaishoNendo  ");
		sql.append(" LEFT OUTER JOIN MST_EIGYOSHO M2  ");
		sql.append("     ON  M1.EigyoshoCode = M2.EigyoshoCode  ");
		sql.append(" LEFT OUTER JOIN MST_BUSHO M3  ");
		sql.append("     ON  M1.BushoCode = M3.BushoCode  ");
		sql.append(" LEFT OUTER JOIN MST_KUBUN M4  ");
		sql.append("     ON  M4.KbnCode = '0050'  ");
		sql.append("     AND M4.Code    = C1.KakuteiKbn  ");
		sql.append(" WHERE  ");
		sql.append("     C1.TaishoNenGetsudo = ?  ");
		sql.append("     AND M2.EigyoshoCode = ?  ");
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", eigyoshoCode);

		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			pstmt.execute();
			result = true;
		} 
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		}
		finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;

	}
	
	/**
	 * 有給休暇台帳(時給日給)を削除
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean deleteNikkyu(Connection con, String taishoYM, String eigyoshoCode) throws Exception {
		boolean result = false;
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// 削除
		//=====================================================================
		pstmtf.clear();
		sql.append(" DELETE FROM KIN_YUKYU_KYUKA_DAICHO");
		sql.append(" FROM  CHI_CHINGINKEISANSHO_KIHON C1  ");
		sql.append(" INNER JOIN MST_SHAIN M1  ");
		sql.append("     ON  C1.ShainNO = M1.ShainNO  ");
		sql.append(" INNER JOIN KIN_YUKYU_KYUKA_DAICHO  ");
		sql.append("     ON  C1.ShainNO = KIN_YUKYU_KYUKA_DAICHO.ShainNO  ");
		sql.append("     AND LEFT(C1.TaishoNenGetsudo, 4) = KIN_YUKYU_KYUKA_DAICHO.TaishoNendo  ");
		sql.append(" WHERE  ");
		sql.append("     C1.TaishoNenGetsudo = ?  ");
		sql.append("     AND C1.EigyoshoCode = ?  ");
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", eigyoshoCode);

		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			pstmt.execute();
			result = true;
		} 
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		}
		finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;

	}
	
	/**
	 * 出勤簿基本を更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean updateShukkinbo(Connection con, String kakuteiKbn, String loginShainNo, String saishuKoshinDate, String saishuKoshinJikan, String taishoYM, String eigyoshoCode) throws Exception {
		boolean result = false;
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// 画面.対象年月と画面.選択行の営業所コードに対応する社員NOの取得
		//=====================================================================
		ArrayList<HashMap<String, String>> shainNo = getShainNo(con, taishoYM, eigyoshoCode);
		
		//=====================================================================
		// 更新
		//=====================================================================
		pstmtf.clear();
		sql.append(" UPDATE  ");
		sql.append("  　 KIN_SHUKKINBO_KIHON ");
		sql.append(" SET ");
		sql.append(" 　　KakuteiKbn = ? ,");
		sql.append(" 　　SaishuKoshinShainNO = ?, ");
		sql.append(" 　　SaishuKoshinDate = ?, ");
		sql.append(" 　　SaishuKoshinJikan = ? ");
		sql.append(" WHERE ");
		sql.append("  	1 <> 1");
		pstmtf.addValue("String", kakuteiKbn);
		pstmtf.addValue("String", loginShainNo);
		pstmtf.addValue("String", saishuKoshinDate);
		pstmtf.addValue("String", saishuKoshinJikan);
		
		if(shainNo.size() != 0) {
			for (int i = 0; i < shainNo.size(); i++) {
				// 1レコード分の配列を用意
				HashMap<String, String> taishoData= shainNo.get(i);
				String taiShoNenGetsudo = taishoData.get("TaishoNenGetsudo");
				String shainNO = taishoData.get("ShainNO");
				sql.append("OR (TaishoNenGetsudo = ?");
				sql.append(" AND ShainNO = ?)");
				pstmtf.addValue("String", taiShoNenGetsudo);
				pstmtf.addValue("String", shainNO);
			}
		}
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			pstmt.execute();
			result = true;
		} 
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		}
		finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;

	}
	
	/**
	 * 賃金計算書基本を更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean updateChingin(Connection con, String kakuteiKbn, String loginShainNo, String saishuKoshinDate, String saishuKoshinJikan, String taishoYM, String eigyoshoCode) throws Exception {
		boolean result = false;
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// 更新
		//=====================================================================
		pstmtf.clear();
		sql.append(" UPDATE  ");
		sql.append("  　 CHI_CHINGINKEISANSHO_KIHON ");
		sql.append(" SET ");
		sql.append(" 　　KakuteiKbn = ?, ");
		sql.append(" 　　SaishuKoshinShainNO = ?, ");
		sql.append(" 　　SaishuKoshinDate = ?, ");
		sql.append(" 　　SaishuKoshinJikan = ? ");
		sql.append(" WHERE ");
		sql.append(" 　　TaishoNenGetsudo = ?  ");
		sql.append(" 　　 AND EigyoshoCode = ? ");
		pstmtf.addValue("String", kakuteiKbn);
		pstmtf.addValue("String", loginShainNo);
		pstmtf.addValue("String", saishuKoshinDate);
		pstmtf.addValue("String", saishuKoshinJikan);
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", eigyoshoCode);

		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			pstmt.execute();
			result = true;
		} 
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		}
		finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;

	}
	
	/**
	 * 有給休暇台帳(月給)を追加
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean insertGekkyu(Connection con, String loginShainNo, String saishuKoshinDate, String saishuKoshinJikan, String taishoYM, String eigyoshoCode) throws Exception {
		boolean result = false;
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// 更新
		//=====================================================================
		pstmtf.clear();
		sql.setLength(0);
		sql.append(" INSERT INTO KIN_YUKYU_KYUKA_DAICHO ");
		sql.append(" ( ");
		sql.append(" ShainNO, ");
		sql.append(" 　TaishoNendo, ");
		sql.append(" 　KakuteiKbn, ");
		sql.append(" 　YukyuKyukaFuyoNissu, ");
		sql.append(" 　SaishuKoshinShainNO, ");
		sql.append(" 　SaishuKoshinDate, ");
		sql.append(" 　SaishuKoshinJikan ");
		sql.append(" ) ");
		sql.append(" SELECT ");
		sql.append("      C1.ShainNO, ");
		sql.append("      LEFT(C1.TaishoNenGetsudo, 4), ");
		sql.append("      ?, ");
		sql.append("      M1.YukyuKyukaFuyoNissu, ");
		sql.append("      ?, ");
		sql.append("      ?, ");
		sql.append("      ? ");
		sql.append(" FROM ");
		sql.append("     KIN_SHUKKINBO_KIHON C1  ");
		sql.append(" INNER JOIN MST_SHAIN M1  ");
		sql.append("     ON  C1.ShainNO = M1.ShainNO  ");
		sql.append(" LEFT OUTER JOIN MST_EIGYOSHO M2  ");
		sql.append("     ON  M1.EigyoshoCode = M2.EigyoshoCode  ");
		sql.append(" LEFT OUTER JOIN MST_BUSHO M3  ");
		sql.append("     ON  M1.BushoCode = M3.BushoCode  ");
		sql.append(" LEFT OUTER JOIN MST_KUBUN M4  ");
		sql.append("     ON  M4.KbnCode = '0050'  ");
		sql.append("     AND M4.Code    = C1.KakuteiKbn  ");
		sql.append(" WHERE  ");
		sql.append("     C1.TaishoNenGetsudo = ?  ");
		sql.append("     AND M2.EigyoshoCode = ?  ");
		sql.append(" GROUP BY  ");
		sql.append("     C1.ShainNO,  ");
		sql.append("     LEFT(C1.TaishoNenGetsudo, 4),  ");
		sql.append("     M1.YukyuKyukaFuyoNissu  ");
		pstmtf.addValue("String", "03");
		pstmtf.addValue("String", loginShainNo);
		pstmtf.addValue("String", saishuKoshinDate);
		pstmtf.addValue("String", saishuKoshinJikan);
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", eigyoshoCode);
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			pstmt.execute();
			result = true;
		} 
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		}
		finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;
	}

	/**
	 * 有給休暇台帳(時給日給)を追加
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean insertNikkyu(Connection con, String loginShainNo, String saishuKoshinDate, String saishuKoshinJikan, String taishoYM, String eigyoshoCode) throws Exception {
		boolean result = false;
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// 更新
		//=====================================================================
		pstmtf.clear();
		sql.append(" INSERT INTO KIN_YUKYU_KYUKA_DAICHO ");
		sql.append(" ( ");
		sql.append("   ShainNO, ");
		sql.append(" 　TaishoNendo, ");
		sql.append(" 　KakuteiKbn, ");
		sql.append(" 　YukyuKyukaFuyoNissu, ");
		sql.append(" 　SaishuKoshinShainNO, ");
		sql.append(" 　SaishuKoshinDate, ");
		sql.append(" 　SaishuKoshinJikan ");
		sql.append(" ) ");
		sql.append(" SELECT ");
		sql.append("      C1.ShainNO, ");
		sql.append("      LEFT(C1.TaishoNenGetsudo, 4), ");
		sql.append("      ?, ");
		sql.append("      M1.YukyuKyukaFuyoNissu , ");
		sql.append("      ?, ");
		sql.append("      ?, ");
		sql.append("      ? ");
		sql.append(" FROM ");
		sql.append("     CHI_CHINGINKEISANSHO_KIHON C1  ");
		sql.append(" LEFT OUTER JOIN MST_SHAIN M1  ");
		sql.append("     ON  C1.ShainNO = M1.ShainNO  ");
		sql.append(" WHERE  ");
		sql.append("     C1.TaishoNenGetsudo = ?  ");
		sql.append("     AND C1.EigyoshoCode = ?  ");
		sql.append(" GROUP BY  ");
		sql.append("     C1.ShainNO,  ");
		sql.append("     LEFT(C1.TaishoNenGetsudo, 4),  ");
		sql.append("     M1.YukyuKyukaFuyoNissu  ");
		pstmtf.addValue("String", "03");
		pstmtf.addValue("String", loginShainNo);
		pstmtf.addValue("String", saishuKoshinDate);
		pstmtf.addValue("String", saishuKoshinJikan);
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", eigyoshoCode);

		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			pstmt.execute();
			result = true;
		} 
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		}
		finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;
	}

	/**
	 * 社員NO取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public ArrayList<HashMap<String, String>> getShainNo(Connection con, String taishoYM, String eigyoshoCode){
		
		ArrayList<HashMap<String, String>> result = new ArrayList<>();
				
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT  ");
		sql.append("     C1.TaishoNenGetsudo,   ");
		sql.append("     C1.ShainNO  ");
		sql.append(" FROM  ");
		sql.append("     KIN_SHUKKINBO_KIHON C1  ");
		sql.append(" LEFT OUTER JOIN MST_SHAIN M1  ");
		sql.append("     ON  C1.ShainNO = M1.ShainNO  ");
		sql.append(" LEFT OUTER JOIN MST_EIGYOSHO M2  ");
		sql.append("     ON  M1.EigyoshoCode = M2.EigyoshoCode  ");
		sql.append(" LEFT OUTER JOIN MST_BUSHO M3  ");
		sql.append("     ON  M1.BushoCode = M3.BushoCode  ");
		sql.append(" LEFT OUTER JOIN MST_KUBUN M4  ");
		sql.append("     ON  M4.KbnCode = '0050'  ");
		sql.append("     AND M4.Code    = C1.KakuteiKbn  ");
		sql.append(" WHERE  ");
		sql.append("     C1.TaishoNenGetsudo = ? ");
		sql.append("     AND M2.EigyoshoCode = ? ");
		sql.append(" GROUP BY  ");
		sql.append("     C1.TaishoNenGetsudo,   ");
		sql.append("     C1.ShainNO  ");
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", eigyoshoCode);
		
		try {
			// SQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			ResultSetMetaData metaData = rset.getMetaData(); 
			
			// カラム数(列数)の取得
			int colCount = metaData.getColumnCount(); 
			
			while (rset.next()){
				// 1レコード分の配列を用意
				HashMap<String, String> record = new HashMap<String, String>();
				// カラム名をkeyとして値を格納
				for (int i = 1; i <= colCount; i++) {
					record.put(metaData.getColumnLabel(i), StringUtils.stripToEmpty(rset.getString(i)));
				}
				// 配列の格納
				result.add(record);
			}
		}
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		// 結果返却
		return result;
		
	}
}