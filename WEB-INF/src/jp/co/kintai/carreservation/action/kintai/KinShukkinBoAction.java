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

public class KinShukkinBoAction extends PJActionBase {
	public KinShukkinBoAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 画面表示
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

		// 検索条件取得
		String taishoYM			= this.getParameter("txtTaishoYM");
		String taishoShainNo	= this.getParameter("txtShainNO");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT TOP 1 GenzaishoriNengetsudo FROM MST_KANRI");
		
		try {
			// SQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			ResultSetMetaData metaData = rset.getMetaData(); 
			rset.next();
			result = StringUtils.stripToEmpty(rset.getString(1));
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
	 * DDLの内容取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getDDL(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		//予定DDL検索
		ArrayList<HashMap<String, String>> mstYoteiKubun = PJActionBase.getMstKubuns(con, "0051", null, null);

		// 送信データを減らすため不要なカラムは削る
		// レコード数分繰り返す
		for (HashMap<String, String> searchRecord: mstYoteiKubun){
			//区分名が空の場合スキップ
			if(StringUtils.isEmpty(searchRecord.get("KbnName"))){
				continue;
			}
			// 1レコード分の配列を用意
			HashMap<String, String> returnRecord = new HashMap<String, String>();

			//値を格納
			returnRecord.put("DDLName", "yotei");
			returnRecord.put("Code", searchRecord.get("Code"));
			returnRecord.put("KbnName", searchRecord.get("KbnName"));

			// 配列の格納
			mstDatas.add(returnRecord);
		}
		
		//勤怠区分DDL検索
		ArrayList<HashMap<String, String>> mstKintaiKubun = PJActionBase.getMstKubuns(con, "0100", null, null);

		// 送信データを減らすため不要なカラムは削る
		// レコード数分繰り返す
		for (HashMap<String, String> searchRecord: mstKintaiKubun){
			//区分名が空の場合スキップ
			if(StringUtils.isEmpty(searchRecord.get("KbnName"))){
				continue;
			}
			// 1レコード分の配列を用意
			HashMap<String, String> returnRecord = new HashMap<String, String>();

			//値を格納
			returnRecord.put("DDLName", "kintai");
			returnRecord.put("Code", searchRecord.get("Code"));
			returnRecord.put("KbnName", searchRecord.get("KbnName"));

			// 配列の格納
			mstDatas.add(returnRecord);
		}
		
		//申請区分DDL検索
		ArrayList<HashMap<String, String>> mstShinseiKubun = PJActionBase.getMstKubuns(con, "0101", null, null);

		// 送信データを減らすため不要なカラムは削る
		// レコード数分繰り返す
		for (HashMap<String, String> searchRecord: mstShinseiKubun){
			//区分名が空の場合スキップ
			if(StringUtils.isEmpty(searchRecord.get("KbnName"))){
				continue;
			}
			// 1レコード分の配列を用意
			HashMap<String, String> returnRecord = new HashMap<String, String>();

			//値を格納
			returnRecord.put("DDLName", "shinsei");
			returnRecord.put("Code", searchRecord.get("Code"));
			returnRecord.put("KbnName", searchRecord.get("KbnName"));

			// 配列の格納
			mstDatas.add(returnRecord);
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		this.addContent("result", mstDatas);
	}
	
	/**
	 * 本社確定済みの確認
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void honshaKakuteizumiCheck(HttpServletRequest req, HttpServletResponse res) throws Exception {
		System.out.println("honshaKakuteizumiCheck開始");
		
		// 検索条件取得
		String taishoYM			= this.getParameter("txtTaishoYM");
		String taishoShainNo	= this.getParameter("txtShainNO");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		// DB接続
		StringBuffer sql1					= new StringBuffer();
		PreparedStatement pstmt1			= null;
		PreparedStatementFactory pstmtf1	= new PreparedStatementFactory();
		ResultSet rset1						= null;
		StringBuffer sql2					= new StringBuffer();
		PreparedStatement pstmt2			= null;
		PreparedStatementFactory pstmtf2	= new PreparedStatementFactory();
		ResultSet rset2						= null;
		
		int sql1result = 0;
		int sql2result = 0;

		sql1.append("	SELECT ");
		sql1.append("		TOP 1 KIN_SHUKKINBO_KIHON.KakuteiKbn ");
		sql1.append("	FROM ");
		sql1.append("		KIN_SHUKKINBO_KIHON WITH(NOLOCK) ");
		sql1.append("	INNER JOIN ");
		sql1.append("		MST_SHAIN WITH(NOLOCK) ");
		sql1.append("	ON ");
		sql1.append("		MST_SHAIN.ShainNO = KIN_SHUKKINBO_KIHON.ShainNO ");
		sql1.append("	WHERE ");
		sql1.append("		KIN_SHUKKINBO_KIHON.TaishoNenGetsudo = ? ");
		sql1.append("	AND	KIN_SHUKKINBO_KIHON.KakuteiKbn = '03' ");
		sql1.append("	AND	MST_SHAIN.EigyoshoCode = ( ");
		sql1.append("		SELECT TOP 1 ");
		sql1.append("			EigyoshoCode ");
		sql1.append("		FROM ");
		sql1.append("			MST_SHAIN ");
		sql1.append("		WHERE ");
		sql1.append("			ShainNO = ? ");
		sql1.append("	) ");
		
		pstmtf1.addValue("String", taishoYM);
		pstmtf1.addValue("String", taishoShainNo);
		
		try {
			// SQL文の生成
			pstmt1 = con.prepareStatement(sql1.toString());
			// パラメータの設定
			pstmtf1.setPreparedStatement(pstmt1);
			// 実行
			rset1 = pstmt1.executeQuery();
			// 結果件数取得
			while (rset1.next()){
				sql1result++;
			}
		}
		catch (Exception exp){
			System.out.println(String.valueOf(exp));
		}
		finally {
			if (rset1 != null){ try { rset1.close(); } catch (Exception exp){}}
			if (pstmt1 != null){ try { pstmt1.close(); } catch (Exception exp){}}
		}

		
		sql2.append("	SELECT ");
		sql2.append("		TOP 1 KakuteiKbn ");
		sql2.append("	FROM ");
		sql2.append("		KIN_SHUKKINBO_KIHON WITH(NOLOCK) ");
		sql2.append("	WHERE ");
		sql2.append("		TaishoNenGetsudo = ? ");
		sql2.append("	AND	ShainNO = ? ");
		
		pstmtf2.addValue("String", taishoYM);
		pstmtf2.addValue("String", taishoShainNo);
		
		try {
			// SQL文の生成
			pstmt2 = con.prepareStatement(sql2.toString());
			// パラメータの設定
			pstmtf2.setPreparedStatement(pstmt2);
			// 実行
			rset2 = pstmt2.executeQuery();
			// 結果件数取得
			while (rset2.next()){
				sql2result++;
			}
		}
		catch (Exception exp){
			System.out.println(String.valueOf(exp));
		}
		finally {
			if (rset2 != null){ try { rset2.close(); } catch (Exception exp){}}
			if (pstmt2 != null){ try { pstmt2.close(); } catch (Exception exp){}}
		}
		
		
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		String result = "0";
		System.out.println("sql1result : " + String.valueOf(sql1result));
		System.out.println("sql2result : " + String.valueOf(sql2result));
		if(sql1result > 0 && sql2result == 0){
			result = "1";
		}
		this.addContent("result", result);
	
	
	}
	
	/**
	 * 出勤簿の取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 検索条件取得
//		String taishoYM			= "2010/08";
//		String taishoShainNo	= "0001";
		String taishoYM			= this.getParameter("txtTaishoYM");
		String taishoShainNo	= this.getParameter("txtShainNO");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	MONTH(CONVERT(DATETIME, M.TaishoNengappi, 111)) AS TaishoGetsu ");
		sql.append(" 	,DAY(CONVERT(DATETIME, M.TaishoNengappi, 111)) AS TaishoBi ");
		sql.append(" 	,M.YobiKbn ");
		sql.append(" 	,M.ShukkinYoteiKbn ");
		sql.append(" 	,M.KintaiKbn ");
		sql.append(" 	,M.KintaiShinseiBiko ");
		sql.append(" 	,M.KintaiShinseiKbn1 ");
		sql.append(" 	,M.KintaiShinseiKaishiJi1 ");
		sql.append(" 	,M.KintaiShinseiKaishiFun1 ");
		sql.append(" 	,M.KintaiShinseiShuryoJi1 ");
		sql.append(" 	,M.KintaiShinseiShuryoFun1 ");
		sql.append(" 	,M.KintaiShinseiJikan1 ");
		sql.append(" 	,M.KintaiShinseiKbn2 ");
		sql.append(" 	,M.KintaiShinseiKaishiJi2 ");
		sql.append(" 	,M.KintaiShinseiKaishiFun2 ");
		sql.append(" 	,M.KintaiShinseiShuryoJi2 ");
		sql.append(" 	,M.KintaiShinseiShuryoFun2 ");
		sql.append(" 	,M.KintaiShinseiJikan2 ");
		sql.append(" 	,'' AS KintaiShinseiKbn3 ");	//TODO
		sql.append(" 	,'' AS KintaiShinseiKaishiJi3 ");
		sql.append(" 	,'' AS KintaiShinseiKaishiFun3 ");
		sql.append(" 	,'' AS KintaiShinseiShuryoJi3 ");
		sql.append(" 	,'' AS KintaiShinseiShuryoFun3 ");
		sql.append(" 	,'' AS KintaiShinseiJikan3 ");
		sql.append(" 	,K.ShinseiKingaku01 ");
		sql.append(" 	,K.ShinseiKingaku02 ");
		sql.append(" 	,K.KakuteiKbn ");
		sql.append(" FROM ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI M ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_SHUKKINBO_KIHON K ");
		sql.append(" ON ");
		sql.append(" 	K.TaishoNenGetsudo = M.TaishoNenGetsudo ");
		sql.append(" AND	K.ShainNO = M.ShainNO ");
		sql.append(" WHERE ");
		sql.append(" 	M.TaishoNenGetsudo = ? ");
		sql.append(" AND	M.ShainNO = ? ");
		sql.append(" ORDER BY ");
		sql.append(" 	M.TaishoNengappi ");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		
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
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void update(HttpServletRequest req, HttpServletResponse res) throws Exception {

		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String shainNo	= req.getParameter("hdnShainNO");
		String password	= req.getParameter("txtPasswordNew");
		
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
		sql.append(" UPDATE MST_SHAIN SET ");
		sql.append("  SaishuKoshinShainNO = ? ");
		sql.append(" ,SaishuKoshinDate = ? ");
		sql.append(" ,SaishuKoshinJikan = ? ");
		sql.append(" ,Password = ? ");
		sql.append(" WHERE ");
		sql.append(" 1 = 1 ");
		sql.append(" and ShainNO = ? ");
		
		// パラメータ設定
		pstmtf.addValue("String", userInformation.getShainNO());
		pstmtf.addValue("String", PJActionBase.getNowDate());
		pstmtf.addValue("String", PJActionBase.getNowTime());
		pstmtf.addValue("String", password);
		pstmtf.addValue("String", shainNo);
		
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmtf.setPreparedStatement(pstmt);
			pstmt.execute();
			
			// ユーザ情報を再取得、セッションの再設定
			// 現在日付の取得
			String nowDate	= PJActionBase.getNowDate();
			
			ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, password, null, null, null, null, nowDate);
			if (0 < mstShains.size()) {
				HashMap<String, String> mstShain = mstShains.get(0);
				
				// 処理可能営業所コードの配列
				ArrayList<String> shoriKanoEigyoshoCode = new ArrayList<>();
				shoriKanoEigyoshoCode.add(mstShain.get("EigyoshoCode"));
				
				// 処理可能営業所コードを取得
				ArrayList<HashMap<String, String>> mstShainEigyoshos = PJActionBase.getMstShainEigyoshos(con, shainNo);
				for(HashMap<String, String> mstShainEigyosho : mstShainEigyoshos) {
					shoriKanoEigyoshoCode.add(mstShainEigyosho.get("EigyoshoCode"));
				}

				//// ユーザ情報の呼び出し
				//UserInformation userInformation	= new UserInformation();
				
				// マスタから取得したデータを設定
				userInformation.setShainNO(mstShain.get("ShainNO"));
				userInformation.setShainName(mstShain.get("ShainName"));
				userInformation.setPassword(mstShain.get("Password"));
				userInformation.setUserKbn(mstShain.get("UserKbn"));
				userInformation.setShainKbn(mstShain.get("ShainKbn"));
				userInformation.setEigyoshoCode(mstShain.get("EigyoshoCode"));
				userInformation.setEigyoshoName(mstShain.get("EigyoshoName"));
				userInformation.setBushoCode(mstShain.get("BushoCode"));
				userInformation.setBushoName(mstShain.get("BushoName"));
				userInformation.setBushoKbn(mstShain.get("BushoKbn"));
				userInformation.setTaisyokuDate(mstShain.get("TaisyokuDate"));
				userInformation.setShoriKanoEigyoshoCode(shoriKanoEigyoshoCode);
				userInformation.setLoginDate(nowDate);
				
				// セッションに格納
				req.getSession().setAttribute(Define.SESSION_ID, userInformation);
				this.addContent("result", true);
			}
			
		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
}