package jp.co.kintai.carreservation.action.kintai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	 * 社員NOの初期値の取得　ログインユーザーの社員NOがMST_SHAINに存在すれば、それを初期値とする
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getShainNO(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String result = "";

		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		String loginShainNo = userInformation.getShainNO();
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ShainNO AS code, ShainName AS code_name FROM MST_SHAIN WHERE ShukinboKbn = '00' AND ShainNO = ? ORDER BY ShainNO");
		pstmtf.addValue("String", loginShainNo);
		
		try {
			// SQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if(rset.next()) {
				result = StringUtils.stripToEmpty(rset.getString("code"));
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
		// 検索条件取得
		String taishoYM			= this.getParameter("txtSearchedTaishoYM");
		String taishoShainNo	= this.getParameter("txtSearchedShainNO");
		
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
		String taishoYM			= this.getParameter("txtSearchedTaishoYM");
		String taishoShainNo	= this.getParameter("txtSearchedShainNO");

		//検索結果0件の時のため、デフォルトのデータを作成
		ArrayList<HashMap<String, String>> ResultDatas = getResultDatas(taishoYM);
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	M.TaishoNengappi ");
		sql.append(" 	,MONTH(CONVERT(DATETIME, M.TaishoNengappi, 111)) AS TaishoGetsu ");
		sql.append(" 	,DAY(CONVERT(DATETIME, M.TaishoNengappi, 111)) AS TaishoBi ");
		sql.append(" 	,M.YobiKbn ");
		sql.append(" 	,M.ShukkinYoteiKbn ");
		sql.append(" 	,M.KintaiKbn ");

		sql.append(" 	,M.ShusshaJi ");
		sql.append(" 	,M.ShusshaFun ");
		sql.append(" 	,M.TaishaJi ");
		sql.append(" 	,M.TaishaFun ");
		sql.append(" 	,M.JitsudoJikan ");

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
		sql.append(" 	,M.KintaiShinseiKbn3 ");
		sql.append(" 	,M.KintaiShinseiKaishiJi3 ");
		sql.append(" 	,M.KintaiShinseiKaishiFun3 ");
		sql.append(" 	,M.KintaiShinseiShuryoJi3 ");
		sql.append(" 	,M.KintaiShinseiShuryoFun3 ");
		sql.append(" 	,M.KintaiShinseiJikan3 ");
		sql.append(" 	,M.SaishuKoshinDate AS MeisaiSaishuKoshinDate ");
		sql.append(" 	,M.SaishuKoshinJikan AS MeisaiSaishuKoshinJikan ");
		sql.append(" 	,K.ShinseiKingaku01 ");
		sql.append(" 	,K.ShinseiKingaku02 ");
		sql.append(" 	,K.KakuteiKbn ");
		sql.append(" 	,K.SaishuKoshinDate AS KihonSaishuKoshinDate ");
		sql.append(" 	,K.SaishuKoshinJikan AS KihonSaishuKoshinJikan ");
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
				for(int i = 0;i < ResultDatas.size();i++) {
					if(
							ResultDatas.get(i).get("TaishoGetsu").equals(StringUtils.stripToEmpty(rset.getString("TaishoGetsu"))) &&
							ResultDatas.get(i).get("TaishoBi").equals(StringUtils.stripToEmpty(rset.getString("TaishoBi")))
							) {
						// 1レコード分の配列を用意
						HashMap<String, String> record = new HashMap<String, String>();
						// カラム名をkeyとして値を格納
						for (int j = 1; j <= colCount; j++) {
							record.put(metaData.getColumnLabel(j), StringUtils.stripToEmpty(rset.getString(j)));
						}
						// 配列の格納
						ResultDatas.set(i, record);
					}
				}
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		this.addContent("result", ResultDatas);
		
	}
	
	/**
	 * 出勤簿のデフォルトデータ取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private ArrayList<HashMap<String, String>> getResultDatas(String taishoYM){
		ArrayList<HashMap<String, String>> ResultDatas = new ArrayList<>();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate endTaishoYMLD = LocalDate.parse(taishoYM + "/15", dtf);
		LocalDate startTaishoYMLD = endTaishoYMLD.minusMonths(1).plusDays(1);
		LocalDate wkTaishoYMLD = startTaishoYMLD;
		while(endTaishoYMLD.compareTo(wkTaishoYMLD) >= 0) {
			
			// 1レコード分の配列を用意
			HashMap<String, String> record = new HashMap<String, String>();
			// カラム名をkeyとして値を格納
			record.put("TaishoNengappi", wkTaishoYMLD.format(dtf));
			record.put("TaishoGetsu", String.valueOf(wkTaishoYMLD.getMonthValue()));
			record.put("TaishoBi", String.valueOf(wkTaishoYMLD.getDayOfMonth()));
			
			String yobi = "";
			switch(wkTaishoYMLD.getDayOfWeek().getValue()) {
			case 1:
				yobi = "月";
				break;
			case 2:
				yobi = "火";
				break;
			case 3:
				yobi = "水";
				break;
			case 4:
				yobi = "木";
				break;
			case 5:
				yobi = "金";
				break;
			case 6:
				yobi = "土";
				break;
			case 7:
				yobi = "日";
				break;
			}
			record.put("YobiKbn", yobi);
			record.put("ShukkinYoteiKbn","00");
			record.put("KintaiKbn","00");
			record.put("ShusshaJi","");
			record.put("ShusshaFun","");
			record.put("TaishaJi","");
			record.put("TaishaFun","");
			record.put("JitsudoJikan","");
			record.put("KintaiShinseiBiko","");
			record.put("KintaiShinseiKbn1","00");
			record.put("KintaiShinseiKaishiJi1","");
			record.put("KintaiShinseiKaishiFun1","");
			record.put("KintaiShinseiShuryoJi1","");
			record.put("KintaiShinseiShuryoFun1","");
			record.put("KintaiShinseiJikan1","");
			record.put("KintaiShinseiKbn2","00");
			record.put("KintaiShinseiKaishiJi2","");
			record.put("KintaiShinseiKaishiFun2","");
			record.put("KintaiShinseiShuryoJi2","");
			record.put("KintaiShinseiShuryoFun2","");
			record.put("KintaiShinseiJikan2","");
			record.put("KintaiShinseiKbn3","00");
			record.put("KintaiShinseiKaishiJi3","");
			record.put("KintaiShinseiKaishiFun3","");
			record.put("KintaiShinseiShuryoJi3","");
			record.put("KintaiShinseiShuryoFun3","");
			record.put("KintaiShinseiJikan3","");
			record.put("ShinseiKingaku01","");
			record.put("ShinseiKingaku02","");
			record.put("KakuteiKbn","");
			
			// 配列の格納
			ResultDatas.add(record);
			
			wkTaishoYMLD = wkTaishoYMLD.plusDays(1);
		}
		
		return ResultDatas;
	}
	
	/**
	 * 出勤簿の更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void update(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// DB接続
		Connection con		= this.getConnection("kintai", req);

		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String taishoYM			= this.getParameter("txtSearchedTaishoYM");
		String taishoShainNo	= this.getParameter("txtSearchedShainNO");
		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		String loginShainNo = userInformation.getShainNO();
		
		boolean result = false;
		int returnval = 0;
		//トランザクション開始
		con.setAutoCommit(false);
		//1か月分入力項目があるので1か月分ループ
		for(int i = 0;i < 31;i++){
			StringBuilder taishoNengappiKeySb	= new StringBuilder();
			taishoNengappiKeySb	.append("TaishoNengappi")	.append(String.valueOf(i));
			String taishoNengappi		= this.getParameter(taishoNengappiKeySb.toString());
			
			if(StringUtils.isEmpty(taishoNengappi)) {
				//データが終わったので終了
				break;
			}
			
			if(getShukkinboMeisaiCount(con, taishoYM, taishoShainNo, taishoNengappi) > 0) {
				result = updateMeisaiRow(con, taishoYM, taishoShainNo, taishoNengappi, loginShainNo, i);
			}
			else {
				result = insertMeisaiRow(con, taishoYM, taishoShainNo, taishoNengappi, loginShainNo, i);
			}
			if(result == false) {
				break;
			}
		}
		if(result) {
			//出勤簿基本の更新
			if(getShukkinboKihonCount(con, taishoYM, taishoShainNo) > 0) {
				returnval = 2;
				result = updateKihonRow(con, taishoYM, taishoShainNo, loginShainNo);
			}
			else {
				returnval = 1;
				result = insertKihonRow(con, taishoYM, taishoShainNo, loginShainNo);
			}
		}
		if(result == false) {
			returnval = 0;
			//ロールバック
			con.rollback();
		}
		else {
			//コミット
			con.commit();
		}
		this.addContent("result", returnval);
	}
	
	/**
	 * 出勤簿の更新対象レコードの存在確認
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private int getShukkinboKihonCount(Connection con, String taishoYM, String taishoShainNo) throws Exception {
		int result = 0;
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
		sql.append(" FROM ");
		sql.append(" 	KIN_SHUKKINBO_KIHON ");
		
		sql.append(" WHERE ");
		sql.append(" 	TaishoNengetsudo = ? ");
		sql.append(" AND	ShainNO = ? ");
		
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
			while (rset.next()){
				result = rset.getInt("CNT");
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;
		
	}
	
	private HashMap<String, String> getNissu(Connection con, String taishoYM, String taishoShainNo){
		
		HashMap<String, String> result = new HashMap<String, String>();
				
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" WITH CteNissu AS ");
		sql.append(" ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		MEISAI.KintaiKbn, ");
		sql.append(" 	LEFT(M1.GroupCode1, 2) AS Kbn, ");
		sql.append(" 		MEISAI.KintaiShinseiNisuu ");
		sql.append(" 	FROM ");
		sql.append(" 		KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 	LEFT OUTER JOIN ");
		sql.append(" 		MST_KUBUN M1 ");
		sql.append(" 	ON ");
		sql.append(" 		MEISAI.KintaiKbn = M1.Code AND ");
		sql.append(" 		M1.KbnCode = '0100' ");
		sql.append(" 	WHERE ");
		sql.append(" 		MEISAI.KintaiKbn <> '00' AND ");
		sql.append(" 		MEISAI.TaishoNenGetsudo = ? AND ");
		sql.append(" 		MEISAI.ShainNO = ? ");
		sql.append(" ) ");
		sql.append(" SELECT ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '01' OR KintaiKbn = '05' THEN KintaiShinseiNisuu END), 0) AS Nissu01, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '02' THEN KintaiShinseiNisuu END), 0) AS Nissu02, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '03' THEN KintaiShinseiNisuu END), 0) AS Nissu03, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '04' THEN KintaiShinseiNisuu END), 0) AS Nissu04, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '05' THEN KintaiShinseiNisuu END), 0) AS Nissu05, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '06' THEN KintaiShinseiNisuu END), 0) AS Nissu06, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '07' THEN KintaiShinseiNisuu END), 0) AS Nissu07, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '08' THEN KintaiShinseiNisuu END), 0) AS Nissu08, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '09' THEN KintaiShinseiNisuu END), 0) AS Nissu09, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '10' THEN KintaiShinseiNisuu END), 0) AS Nissu10, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '11' THEN KintaiShinseiNisuu END), 0) AS Nissu11, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '12' THEN KintaiShinseiNisuu END), 0) AS Nissu12, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '13' THEN KintaiShinseiNisuu END), 0) AS Nissu13, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '14' THEN KintaiShinseiNisuu END), 0) AS Nissu14, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '15' THEN KintaiShinseiNisuu END), 0) AS Nissu15 ");
		sql.append(" FROM ");
		sql.append(" 	CteNissu ");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);

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
				// カラム名をkeyとして値を格納
				for (int j = 1; j <= colCount; j++) {
					result.put(metaData.getColumnLabel(j), StringUtils.stripToEmpty(rset.getString(j)));
				}
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
	
	private HashMap<String, String> getJikan(Connection con, String taishoYM, String taishoShainNo){
		
		HashMap<String, String> result = new HashMap<String, String>();
				
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" WITH CteJikan AS ");
		sql.append(" ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		LEFT(M1.GroupCode1, 2) AS Kbn, ");
		sql.append(" 		MEISAI.KintaiShinseiJikanKeisan1 AS KintaiShinseiJikanKeisan ");
		sql.append(" 	FROM ");
		sql.append(" 		KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 	LEFT OUTER JOIN ");
		sql.append(" 		MST_KUBUN M1 ");
		sql.append(" 	ON ");
		sql.append(" 		MEISAI.KintaiShinseiKbn1 = M1.Code AND ");
		sql.append(" 		M1.KbnCode = '0101' ");
		sql.append(" 	WHERE ");
		sql.append(" 		MEISAI.KintaiShinseiKbn1 <> '00' AND ");
		sql.append(" 		MEISAI.TaishoNenGetsudo = ? AND ");
		sql.append(" 		MEISAI.ShainNO = ? ");

		sql.append(" 	UNION ALL ");

		sql.append(" 	SELECT ");
		sql.append(" 		LEFT(M1.GroupCode1, 2) AS Kbn, ");
		sql.append(" 		MEISAI.KintaiShinseiJikanKeisan2 AS KintaiShinseiJikanKeisan ");
		sql.append(" 	FROM ");
		sql.append(" 		KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 	LEFT OUTER JOIN ");
		sql.append(" 		MST_KUBUN M1 ");
		sql.append(" 	ON ");
		sql.append(" 		MEISAI.KintaiShinseiKbn2 = M1.Code AND ");
		sql.append(" 		M1.KbnCode = '0101' ");
		sql.append(" 	WHERE ");
		sql.append(" 		MEISAI.KintaiShinseiKbn2 <> '00' AND ");
		sql.append(" 		MEISAI.TaishoNenGetsudo = ? AND ");
		sql.append(" 		MEISAI.ShainNO = ? ");

		sql.append(" 	UNION ALL ");

		sql.append(" 	SELECT ");
		sql.append(" 		LEFT(M1.GroupCode1, 2) AS Kbn, ");
		sql.append(" 		MEISAI.KintaiShinseiJikanKeisan3 AS KintaiShinseiJikanKeisan ");
		sql.append(" 	FROM ");
		sql.append(" 		KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 	LEFT OUTER JOIN ");
		sql.append(" 		MST_KUBUN M1 ");
		sql.append(" 	ON ");
		sql.append(" 		MEISAI.KintaiShinseiKbn3 = M1.Code AND ");
		sql.append(" 		M1.KbnCode = '0101' ");
		sql.append(" 	WHERE ");
		sql.append(" 		MEISAI.KintaiShinseiKbn3 <> '00' AND ");
		sql.append(" 		MEISAI.TaishoNenGetsudo = ? AND ");
		sql.append(" 		MEISAI.ShainNO = ? ");
		sql.append(" ) ");
		sql.append(" SELECT ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '01' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan01, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '02' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan02, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '03' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan03, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn IN ('04', '05') THEN KintaiShinseiJikanKeisan END), 0) AS Jikan04, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '05' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan05, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '06' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan06, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '07' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan07, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '08' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan08, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '09' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan09, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn = '10' THEN KintaiShinseiJikanKeisan END), 0) AS Jikan10, ");
		sql.append(" 	ISNULL(SUM(CASE WHEN Kbn IN ('01', '02', '03', '04' ,'05', '06', '07', '08', '09', '10') THEN KintaiShinseiJikanKeisan END), 0) AS JikanGoukei ");
		sql.append(" FROM ");
		sql.append(" 	CteJikan ");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);

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
				// カラム名をkeyとして値を格納
				for (int j = 1; j <= colCount; j++) {
					result.put(metaData.getColumnLabel(j), StringUtils.stripToEmpty(rset.getString(j)));
				}
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

	/**
	 * 出勤簿のレコード更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean updateKihonRow(Connection con, String taishoYM, String taishoShainNo, String loginShainNo) throws Exception {
		boolean result = false;
		HashMap<String, String> nissuRecord = getNissu(con, taishoYM, taishoShainNo);
		HashMap<String, String> jikanRecord = getJikan(con, taishoYM, taishoShainNo);
		
		String shinseiKingaku01 =  this.getParameter("txtShinseiKingaku01");
		if(StringUtils.isEmpty(shinseiKingaku01)) {
			shinseiKingaku01 = "0";
		}
		String shinseiKingaku02 =  this.getParameter("txtShinseiKingaku02");
		if(StringUtils.isEmpty(shinseiKingaku02)) {
			shinseiKingaku02 = "0";
		}

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
		sql.append(" UPDATE ");
		sql.append(" 	KIN_SHUKKINBO_KIHON ");
		sql.append(" SET ");
		sql.append(" 	TaishoNenGetsudo =			?, ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	ShainNO =					?, ");
		pstmtf.addValue("String", taishoShainNo);
		sql.append(" 	KakuteiKbn =				'02', ");
		sql.append(" 	ShinseiNissu01 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu01"));
		sql.append(" 	ShinseiNissu02 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu02"));
		sql.append(" 	ShinseiNissu03 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu03"));
		sql.append(" 	ShinseiNissu04 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu04"));
		sql.append(" 	ShinseiNissu05 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu05"));
		sql.append(" 	ShinseiNissu06 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu06"));
		sql.append(" 	ShinseiNissu07 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu07"));
		sql.append(" 	ShinseiNissu08 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu08"));
		sql.append(" 	ShinseiNissu09 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu09"));
		sql.append(" 	ShinseiNissu10 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu10"));
		sql.append(" 	ShinseiNissu11 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu11"));
		sql.append(" 	ShinseiNissu12 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu12"));
		sql.append(" 	ShinseiNissu13 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu13"));
		sql.append(" 	ShinseiNissu14 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu14"));
		sql.append(" 	ShinseiNissu15 =			?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu15"));
		sql.append(" 	ShinseiJikan01 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan01"));
		sql.append(" 	ShinseiJikan02 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan02"));
		sql.append(" 	ShinseiJikan03 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan03"));
		sql.append(" 	ShinseiJikan04 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan04"));
		sql.append(" 	ShinseiJikan05 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan05"));
		sql.append(" 	ShinseiJikan06 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan06"));
		sql.append(" 	ShinseiJikan07 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan07"));
		sql.append(" 	ShinseiJikan08 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan08"));
		sql.append(" 	ShinseiJikan09 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan09"));
		sql.append(" 	ShinseiJikan10 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan10"));
		sql.append(" 	ShinseiKingaku01 =			?, ");
		pstmtf.addValue("String", shinseiKingaku01);
		sql.append(" 	ShinseiKingaku02 =			?, ");
		pstmtf.addValue("String", shinseiKingaku02);
		sql.append(" 	ShinseiKingaku03 =			0, ");
		sql.append(" 	ShinseiKingaku04 =			0, ");
		sql.append(" 	ShinseiKingaku05 =			0, ");
		sql.append(" 	ShinseiKihonGokeiJikan =	?, ");
		pstmtf.addValue("String", jikanRecord.get("JikanGoukei"));
		sql.append(" 	SaishuKoshinShainNO =		?, ");
		pstmtf.addValue("String", loginShainNo);
		sql.append(" 	SaishuKoshinDate =			?, ");
		pstmtf.addValue("String", PJActionBase.getNowDate());
		sql.append(" 	SaishuKoshinJikan =			? ");
		pstmtf.addValue("String", PJActionBase.getNowTime());
		sql.append(" WHERE ");
		sql.append(" 	TaishoNenGetsudo =			? AND ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	ShainNO =					? ");
		pstmtf.addValue("String", taishoShainNo);

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
	 * 出勤簿のレコード更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean insertKihonRow(Connection con, String taishoYM, String taishoShainNo, String loginShainNo) throws Exception {
		boolean result = false;
		HashMap<String, String> nissuRecord = getNissu(con, taishoYM, taishoShainNo);
		HashMap<String, String> jikanRecord = getJikan(con, taishoYM, taishoShainNo);
		
		String shinseiKingaku01 =  this.getParameter("txtShinseiKingaku01");
		if(StringUtils.isEmpty(shinseiKingaku01)) {
			shinseiKingaku01 = "0";
		}
		String shinseiKingaku02 =  this.getParameter("txtShinseiKingaku02");
		if(StringUtils.isEmpty(shinseiKingaku02)) {
			shinseiKingaku02 = "0";
		}

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
		sql.append(" INSERT INTO ");
		sql.append(" 	KIN_SHUKKINBO_KIHON ");
		sql.append(" ( ");
		sql.append(" 	TaishoNenGetsudo, ");
		sql.append(" 	ShainNO, ");
		sql.append(" 	KakuteiKbn, ");
		sql.append(" 	ShinseiNissu01, ");
		sql.append(" 	ShinseiNissu02, ");
		sql.append(" 	ShinseiNissu03, ");
		sql.append(" 	ShinseiNissu04, ");
		sql.append(" 	ShinseiNissu05, ");
		sql.append(" 	ShinseiNissu06, ");
		sql.append(" 	ShinseiNissu07, ");
		sql.append(" 	ShinseiNissu08, ");
		sql.append(" 	ShinseiNissu09, ");
		sql.append(" 	ShinseiNissu10, ");
		sql.append(" 	ShinseiNissu11, ");
		sql.append(" 	ShinseiNissu12, ");
		sql.append(" 	ShinseiNissu13, ");
		sql.append(" 	ShinseiNissu14, ");
		sql.append(" 	ShinseiNissu15, ");
		sql.append(" 	ShinseiJikan01, ");
		sql.append(" 	ShinseiJikan02, ");
		sql.append(" 	ShinseiJikan03, ");
		sql.append(" 	ShinseiJikan04, ");
		sql.append(" 	ShinseiJikan05, ");
		sql.append(" 	ShinseiJikan06, ");
		sql.append(" 	ShinseiJikan07, ");
		sql.append(" 	ShinseiJikan08, ");
		sql.append(" 	ShinseiJikan09, ");
		sql.append(" 	ShinseiJikan10, ");
		sql.append(" 	ShinseiKingaku01, ");
		sql.append(" 	ShinseiKingaku02, ");
		sql.append(" 	ShinseiKingaku03, ");
		sql.append(" 	ShinseiKingaku04, ");
		sql.append(" 	ShinseiKingaku05, ");
		sql.append(" 	ShinseiKihonGokeiJikan, ");
		sql.append(" 	SaishuKoshinShainNO, ");
		sql.append(" 	SaishuKoshinDate, ");
		sql.append(" 	SaishuKoshinJikan ");
		sql.append(" ) ");
		sql.append(" VALUES ");
		sql.append(" ( ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	?, ");
		pstmtf.addValue("String", taishoShainNo);
		sql.append(" 	'02', ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu01"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu02"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu03"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu04"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu05"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu06"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu07"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu08"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu09"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu10"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu11"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu12"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu13"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu14"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissuRecord.get("Nissu15"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan01"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan02"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan03"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan04"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan05"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan06"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan07"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan08"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan09"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan10"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", shinseiKingaku01);
		sql.append(" 	?, ");
		pstmtf.addValue("String", shinseiKingaku02);
		sql.append(" 	0, ");
		sql.append(" 	0, ");
		sql.append(" 	0, ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("JikanGoukei"));
//		sql.append(" 	'', ");
//		sql.append(" 	'', ");
//		sql.append(" 	'', ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", loginShainNo);
		sql.append(" 	?, ");
		pstmtf.addValue("String", PJActionBase.getNowDate());
		sql.append(" 	? ");
		pstmtf.addValue("String", PJActionBase.getNowTime());
		sql.append(" ) ");

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
	 * 出勤簿の更新対象レコードの存在確認
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private int getShukkinboMeisaiCount(Connection con, String taishoYM, String taishoShainNo, String taishoNengappi) throws Exception {
		int result = 0;
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
		sql.append(" FROM ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI ");
		
		sql.append(" WHERE ");
		sql.append(" 	TaishoNengetsudo = ? ");
		sql.append(" AND	ShainNO = ? ");
		sql.append(" AND	TaishoNengappi = ? ");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		pstmtf.addValue("String", taishoNengappi);
		
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			while (rset.next()){
				result = rset.getInt("CNT");
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return result;
		
	}
	
	/**
	 * 出勤簿のレコード更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean updateMeisaiRow(Connection con, String taishoYM, String taishoShainNo, String taishoNengappi, String loginShainNo, int i) throws Exception {
		boolean result = false;

		//対象年月日の曜日を取得
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate taishoNengappiLD = LocalDate.parse(taishoNengappi, dtf);
		
		String yobi = "";
		switch(taishoNengappiLD.getDayOfWeek().getValue()) {
		case 1:
			yobi = "月";
			break;
		case 2:
			yobi = "火";
			break;
		case 3:
			yobi = "水";
			break;
		case 4:
			yobi = "木";
			break;
		case 5:
			yobi = "金";
			break;
		case 6:
			yobi = "土";
			break;
		case 7:
			yobi = "日";
			break;
		}

		StringBuilder shukkinYoteiKbnKeySb		= new StringBuilder();
		StringBuilder kintaiKbnKeySb			= new StringBuilder();
		StringBuilder shusshaJiKeySb			= new StringBuilder();
		StringBuilder shusshaFunKeySb			= new StringBuilder();
		StringBuilder taishaJiKeySb				= new StringBuilder();
		StringBuilder taishaFunKeySb			= new StringBuilder();
		StringBuilder jitsudoJikanKeySb			= new StringBuilder();
		StringBuilder bikoKeySb					= new StringBuilder();
		StringBuilder kintaiShinseiKbn1KeySb	= new StringBuilder();
		StringBuilder kaishiJi1KeySb			= new StringBuilder();
		StringBuilder kaishiFun1KeySb			= new StringBuilder();
		StringBuilder shuryoJi1KeySb			= new StringBuilder();
		StringBuilder shuryoFun1KeySb			= new StringBuilder();
		StringBuilder jikan1KeySb				= new StringBuilder();
		StringBuilder kintaiShinseiKbn2KeySb	= new StringBuilder();
		StringBuilder kaishiJi2KeySb			= new StringBuilder();
		StringBuilder kaishiFun2KeySb			= new StringBuilder();
		StringBuilder shuryoJi2KeySb			= new StringBuilder();
		StringBuilder shuryoFun2KeySb			= new StringBuilder();
		StringBuilder jikan2KeySb				= new StringBuilder();
		StringBuilder kintaiShinseiKbn3KeySb	= new StringBuilder();
		StringBuilder kaishiJi3KeySb			= new StringBuilder();
		StringBuilder kaishiFun3KeySb			= new StringBuilder();
		StringBuilder shuryoJi3KeySb			= new StringBuilder();
		StringBuilder shuryoFun3KeySb			= new StringBuilder();
		StringBuilder jikan3KeySb				= new StringBuilder();

		shukkinYoteiKbnKeySb	.append("ShukkinYoteiKbn")			.append(String.valueOf(i));
		kintaiKbnKeySb			.append("KintaiKbn")				.append(String.valueOf(i));
		shusshaJiKeySb			.append("ShusshaJi")				.append(String.valueOf(i));
		shusshaFunKeySb			.append("ShusshaFun")				.append(String.valueOf(i));
		taishaJiKeySb			.append("TaishaJi")					.append(String.valueOf(i));
		taishaFunKeySb			.append("TaishaFun")				.append(String.valueOf(i));
		jitsudoJikanKeySb		.append("JitsudoJikan")				.append(String.valueOf(i));
		bikoKeySb				.append("KintaiShinseiBiko")		.append(String.valueOf(i));
		kintaiShinseiKbn1KeySb	.append("KintaiShinseiKbn1")		.append(String.valueOf(i));
		kaishiJi1KeySb			.append("KintaiShinseiKaishiJi1")	.append(String.valueOf(i));
		kaishiFun1KeySb			.append("KintaiShinseiKaishiFun1")	.append(String.valueOf(i));
		shuryoJi1KeySb			.append("KintaiShinseiShuryoJi1")	.append(String.valueOf(i));
		shuryoFun1KeySb			.append("KintaiShinseiShuryoFun1")	.append(String.valueOf(i));
		jikan1KeySb				.append("KintaiShinseiJikan1")		.append(String.valueOf(i));
		kintaiShinseiKbn2KeySb	.append("KintaiShinseiKbn2")		.append(String.valueOf(i));
		kaishiJi2KeySb			.append("KintaiShinseiKaishiJi2")	.append(String.valueOf(i));
		kaishiFun2KeySb			.append("KintaiShinseiKaishiFun2")	.append(String.valueOf(i));
		shuryoJi2KeySb			.append("KintaiShinseiShuryoJi2")	.append(String.valueOf(i));
		shuryoFun2KeySb			.append("KintaiShinseiShuryoFun2")	.append(String.valueOf(i));
		jikan2KeySb				.append("KintaiShinseiJikan2")		.append(String.valueOf(i));
		kintaiShinseiKbn3KeySb	.append("KintaiShinseiKbn3")		.append(String.valueOf(i));
		kaishiJi3KeySb			.append("KintaiShinseiKaishiJi3")	.append(String.valueOf(i));
		kaishiFun3KeySb			.append("KintaiShinseiKaishiFun3")	.append(String.valueOf(i));
		shuryoJi3KeySb			.append("KintaiShinseiShuryoJi3")	.append(String.valueOf(i));
		shuryoFun3KeySb			.append("KintaiShinseiShuryoFun3")	.append(String.valueOf(i));
		jikan3KeySb				.append("KintaiShinseiJikan3")		.append(String.valueOf(i));
		
		String shukkinYoteiKbn		= this.getParameter(shukkinYoteiKbnKeySb.toString());
		String kintaiKbn			= this.getParameter(kintaiKbnKeySb.toString());
		String shusshaJi			= this.getParameter(shusshaJiKeySb.toString());
		String shusshaFun			= this.getParameter(shusshaFunKeySb.toString());
		String taishaJi				= this.getParameter(taishaJiKeySb.toString());
		String taishaFun			= this.getParameter(taishaFunKeySb.toString());
		String jitsudoJikan			= this.getParameter(jitsudoJikanKeySb.toString());
		String biko					= this.getParameter(bikoKeySb.toString());
		String kintaiShinseiKbn1	= this.getParameter(kintaiShinseiKbn1KeySb.toString());
		String kaishiJi1			= this.getParameter(kaishiJi1KeySb.toString());
		String kaishiFun1			= this.getParameter(kaishiFun1KeySb.toString());
		String shuryoJi1			= this.getParameter(shuryoJi1KeySb.toString());
		String shuryoFun1			= this.getParameter(shuryoFun1KeySb.toString());
		String jikan1				= this.getParameter(jikan1KeySb.toString());
		String kintaiShinseiKbn2	= this.getParameter(kintaiShinseiKbn2KeySb.toString());
		String kaishiJi2			= this.getParameter(kaishiJi2KeySb.toString());
		String kaishiFun2			= this.getParameter(kaishiFun2KeySb.toString());
		String shuryoJi2			= this.getParameter(shuryoJi2KeySb.toString());
		String shuryoFun2			= this.getParameter(shuryoFun2KeySb.toString());
		String jikan2				= this.getParameter(jikan2KeySb.toString());
		String kintaiShinseiKbn3	= this.getParameter(kintaiShinseiKbn3KeySb.toString());
		String kaishiJi3			= this.getParameter(kaishiJi3KeySb.toString());
		String kaishiFun3			= this.getParameter(kaishiFun3KeySb.toString());
		String shuryoJi3			= this.getParameter(shuryoJi3KeySb.toString());
		String shuryoFun3			= this.getParameter(shuryoFun3KeySb.toString());
		String jikan3				= this.getParameter(jikan3KeySb.toString());

		if(StringUtils.isEmpty(jikan1)) {
			jikan1 = "0";
		}
		if(StringUtils.isEmpty(jikan2)) {
			jikan2 = "0";
		}
		if(StringUtils.isEmpty(jikan3)) {
			jikan3 = "0";
		}
		if(StringUtils.isEmpty(jitsudoJikan)) {
			jitsudoJikan = "0";
		}

		HashMap<String, String> shinseiPatternRecord = getShinseiPattern(con, kintaiKbn, kintaiShinseiKbn1, kintaiShinseiKbn2, kintaiShinseiKbn3);
		String jikanKeisan1 = "0";
		String jikanKeisan2 = "0";
		String jikanKeisan3 = "0";
		String nissu = "0";
		String kihonJikan = "0";
		if("00".equals(kintaiKbn) == false) {
			if(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn1")) == false) {
				if("01".equals(shinseiPatternRecord.get("KaGenZanKbn1"))) {
					jikanKeisan1 = jikan1;
				}
				else if("02".equals(shinseiPatternRecord.get("KaGenZanKbn1"))) {
					jikanKeisan1 = "-" + jikan1;
				}
			}
			if(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn2")) == false) {
				if("01".equals(shinseiPatternRecord.get("KaGenZanKbn2"))) {
					jikanKeisan2 = jikan2;
				}
				else if("02".equals(shinseiPatternRecord.get("KaGenZanKbn2"))) {
					jikanKeisan2 = "-" + jikan2;
				}
			}
			if(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn3")) == false) {
				if("01".equals(shinseiPatternRecord.get("KaGenZanKbn3"))) {
					jikanKeisan3 = jikan3;
				}
				else if("02".equals(shinseiPatternRecord.get("KaGenZanKbn3"))) {
					jikanKeisan3 = "-" + jikan3;
				}
			}
			
			if(
					(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn1")) == false) &&
					(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn2")) == false) &&
					(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn3")) == false)
					) {
				nissu = shinseiPatternRecord.get("Nissuu");
				kihonJikan = shinseiPatternRecord.get("KintaiKihonSagyoJikan");
			}
		}


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
		sql.append(" UPDATE ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI ");
		sql.append(" SET ");
		sql.append(" 	TaishoNenGetsudo			=	?, ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	ShainNO						=	?, ");
		pstmtf.addValue("String", taishoShainNo);
		sql.append(" 	TaishoNengappi				=	?, ");
		pstmtf.addValue("String", taishoNengappi);
		sql.append(" 	YobiKbn						=	?, ");
		pstmtf.addValue("String", yobi);
		sql.append(" 	ShukkinYoteiKbn				=	?, ");
		if("00".equals(shukkinYoteiKbn)) {
			shukkinYoteiKbn = "";
		}
		pstmtf.addValue("String", shukkinYoteiKbn);
		sql.append(" 	KintaiKbn					=	?, ");
		if("00".equals(kintaiKbn)) {
			kintaiKbn = "";
		}
		pstmtf.addValue("String", kintaiKbn);

		sql.append(" 	ShusshaJi					=	?, ");
		if(StringUtils.isEmpty(shusshaJi) == false) {
			shusshaJi = StringUtils.leftPad(shusshaJi, 2, '0');
			if("00".equals(kintaiKbn)) {
				shusshaJi = "";
			}
		}
		pstmtf.addValue("String", shusshaJi);
		sql.append(" 	ShusshaFun					=	?, ");
		if(StringUtils.isEmpty(shusshaFun) == false) {
			shusshaFun = StringUtils.leftPad(shusshaFun, 2, '0');
			if("00".equals(kintaiKbn)) {
				shusshaFun = "";
			}
		}
		pstmtf.addValue("String", shusshaFun);
		sql.append(" 	TaishaJi					=	?, ");
		if(StringUtils.isEmpty(taishaJi) == false) {
			taishaJi = StringUtils.leftPad(taishaJi, 2, '0');
			if("00".equals(kintaiKbn)) {
				taishaJi = "";
			}
		}
		pstmtf.addValue("String", taishaJi);
		sql.append(" 	TaishaFun					=	?, ");
		if(StringUtils.isEmpty(taishaFun) == false) {
			taishaFun = StringUtils.leftPad(taishaFun, 2, '0');
			if("00".equals(kintaiKbn)) {
				taishaFun = "";
			}
		}
		pstmtf.addValue("String", taishaFun);
		sql.append(" 	JitsudoJikan				=	?, ");
		pstmtf.addValue("String", jitsudoJikan);
		
		sql.append(" 	KintaiShinseiKbn1			=	?, ");
		pstmtf.addValue("String", kintaiShinseiKbn1);
		sql.append(" 	KintaiShinseiKaishiJi1		=	?, ");
		if(StringUtils.isEmpty(kaishiJi1) == false) {
			kaishiJi1 = StringUtils.leftPad(kaishiJi1, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiJi1 = "";
			}
		}
		pstmtf.addValue("String", kaishiJi1);
		sql.append(" 	KintaiShinseiKaishiFun1		=	?, ");
		if(StringUtils.isEmpty(kaishiFun1) == false) {
			kaishiFun1 = StringUtils.leftPad(kaishiFun1, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiFun1 = "";
			}
		}
		pstmtf.addValue("String", kaishiFun1);
		sql.append(" 	KintaiShinseiShuryoJi1		=	?, ");
		if(StringUtils.isEmpty(shuryoJi1) == false) {
			shuryoJi1 = StringUtils.leftPad(shuryoJi1, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoJi1 = "";
			}
		}
		pstmtf.addValue("String", shuryoJi1);
		sql.append(" 	KintaiShinseiShuryoFun1		=	?, ");
		if(StringUtils.isEmpty(shuryoFun1) == false) {
			shuryoFun1 = StringUtils.leftPad(shuryoFun1, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoFun1 = "";
			}
		}
		pstmtf.addValue("String", shuryoFun1);
		sql.append(" 	KintaiShinseiJikan1			=	?, ");
		pstmtf.addValue("String", jikan1);
		sql.append(" 	KintaiShinseiKyukeiJikan1	=	0, ");
		sql.append(" 	KintaiShinseiJikanKeisan1	=	?, ");
		pstmtf.addValue("String", jikanKeisan1);

		sql.append(" 	KintaiShinseiKbn2			=	?, ");
		pstmtf.addValue("String", kintaiShinseiKbn2);
		sql.append(" 	KintaiShinseiKaishiJi2		=	?, ");
		if(StringUtils.isEmpty(kaishiJi2) == false) {
			kaishiJi2 = StringUtils.leftPad(kaishiJi2, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiJi2 = "";
			}
		}
		pstmtf.addValue("String", kaishiJi2);
		sql.append(" 	KintaiShinseiKaishiFun2		=	?, ");
		if(StringUtils.isEmpty(kaishiFun2) == false) {
			kaishiFun2 = StringUtils.leftPad(kaishiFun2, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiFun2 = "";
			}
		}
		pstmtf.addValue("String", kaishiFun2);
		sql.append(" 	KintaiShinseiShuryoJi2		=	?, ");
		if(StringUtils.isEmpty(shuryoJi2) == false) {
			shuryoJi2 = StringUtils.leftPad(shuryoJi2, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoJi2 = "";
			}
		}
		pstmtf.addValue("String", shuryoJi2);
		sql.append(" 	KintaiShinseiShuryoFun2		=	?, ");
		if(StringUtils.isEmpty(shuryoFun2) == false) {
			shuryoFun2 = StringUtils.leftPad(shuryoFun2, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoFun2 = "";
			}
		}
		pstmtf.addValue("String", shuryoFun2);
		sql.append(" 	KintaiShinseiJikan2			=	?, ");
		pstmtf.addValue("String", jikan2);
		sql.append(" 	KintaiShinseiKyukeiJikan2	=	0, ");
		sql.append(" 	KintaiShinseiJikanKeisan2	=	?, ");
		pstmtf.addValue("String", jikanKeisan2);

		sql.append(" 	KintaiShinseiKbn3			=	?, ");
		pstmtf.addValue("String", kintaiShinseiKbn3);
		sql.append(" 	KintaiShinseiKaishiJi3		=	?, ");
		if(StringUtils.isEmpty(kaishiJi3) == false) {
			kaishiJi3 = StringUtils.leftPad(kaishiJi3, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiJi3 = "";
			}
		}
		pstmtf.addValue("String", kaishiJi3);
		sql.append(" 	KintaiShinseiKaishiFun3		=	?, ");
		if(StringUtils.isEmpty(kaishiFun3) == false) {
			kaishiFun3 = StringUtils.leftPad(kaishiFun3, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiFun3 = "";
			}
		}
		pstmtf.addValue("String", kaishiFun3);
		sql.append(" 	KintaiShinseiShuryoJi3		=	?, ");
		if(StringUtils.isEmpty(shuryoJi3) == false) {
			shuryoJi3 = StringUtils.leftPad(shuryoJi3, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoJi3 = "";
			}
		}
		pstmtf.addValue("String", shuryoJi3);
		sql.append(" 	KintaiShinseiShuryoFun3		=	?, ");
		if(StringUtils.isEmpty(shuryoFun3) == false) {
			shuryoFun3 = StringUtils.leftPad(shuryoFun3, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoFun3 = "";
			}
		}
		pstmtf.addValue("String", shuryoFun3);
		sql.append(" 	KintaiShinseiJikan3			=	?, ");
		pstmtf.addValue("String", jikan3);
		sql.append(" 	KintaiShinseiKyukeiJikan3	=	0, ");
		sql.append(" 	KintaiShinseiJikanKeisan3	=	?, ");
		pstmtf.addValue("String", jikanKeisan3);

		sql.append(" 	KintaiShinseiBiko			=	?, ");
		pstmtf.addValue("String", biko);
		sql.append(" 	KintaiShinseiNisuu			=	?, ");
		pstmtf.addValue("String", nissu);
		sql.append(" 	KintaiShinseiKihonJikan		=	?, ");
		pstmtf.addValue("String", kihonJikan);

		sql.append(" 	SaishuKoshinShainNO			=	?, ");
		pstmtf.addValue("String", loginShainNo);
		sql.append(" 	SaishuKoshinDate			=	?, ");
		pstmtf.addValue("String", PJActionBase.getNowDate());
		sql.append(" 	SaishuKoshinJikan			=	? ");
		pstmtf.addValue("String", PJActionBase.getNowTime());

		sql.append(" WHERE ");
		sql.append(" 	TaishoNenGetsudo			=	?	AND ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	ShainNO						=	?	AND ");
		pstmtf.addValue("String", taishoShainNo);
		sql.append(" 	TaishoNengappi				=	?	");
		pstmtf.addValue("String", taishoNengappi);
		
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
	 * 出勤簿のレコード登録
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean insertMeisaiRow(Connection con, String taishoYM, String taishoShainNo, String taishoNengappi, String loginShainNo, int i) throws Exception {
		boolean result = false;

		//対象年月日の曜日を取得
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate taishoNengappiLD = LocalDate.parse(taishoNengappi, dtf);
		
		String yobi = "";
		switch(taishoNengappiLD.getDayOfWeek().getValue()) {
		case 1:
			yobi = "月";
			break;
		case 2:
			yobi = "火";
			break;
		case 3:
			yobi = "水";
			break;
		case 4:
			yobi = "木";
			break;
		case 5:
			yobi = "金";
			break;
		case 6:
			yobi = "土";
			break;
		case 7:
			yobi = "日";
			break;
		}

		StringBuilder shukkinYoteiKbnKeySb		= new StringBuilder();
		StringBuilder kintaiKbnKeySb			= new StringBuilder();
		StringBuilder shusshaJiKeySb			= new StringBuilder();
		StringBuilder shusshaFunKeySb			= new StringBuilder();
		StringBuilder taishaJiKeySb				= new StringBuilder();
		StringBuilder taishaFunKeySb			= new StringBuilder();
		StringBuilder jitsudoJikanKeySb			= new StringBuilder();
		StringBuilder bikoKeySb					= new StringBuilder();
		StringBuilder kintaiShinseiKbn1KeySb	= new StringBuilder();
		StringBuilder kaishiJi1KeySb			= new StringBuilder();
		StringBuilder kaishiFun1KeySb			= new StringBuilder();
		StringBuilder shuryoJi1KeySb			= new StringBuilder();
		StringBuilder shuryoFun1KeySb			= new StringBuilder();
		StringBuilder jikan1KeySb				= new StringBuilder();
		StringBuilder kintaiShinseiKbn2KeySb	= new StringBuilder();
		StringBuilder kaishiJi2KeySb			= new StringBuilder();
		StringBuilder kaishiFun2KeySb			= new StringBuilder();
		StringBuilder shuryoJi2KeySb			= new StringBuilder();
		StringBuilder shuryoFun2KeySb			= new StringBuilder();
		StringBuilder jikan2KeySb				= new StringBuilder();
		StringBuilder kintaiShinseiKbn3KeySb	= new StringBuilder();
		StringBuilder kaishiJi3KeySb			= new StringBuilder();
		StringBuilder kaishiFun3KeySb			= new StringBuilder();
		StringBuilder shuryoJi3KeySb			= new StringBuilder();
		StringBuilder shuryoFun3KeySb			= new StringBuilder();
		StringBuilder jikan3KeySb				= new StringBuilder();

		shukkinYoteiKbnKeySb	.append("ShukkinYoteiKbn")			.append(String.valueOf(i));
		kintaiKbnKeySb			.append("KintaiKbn")				.append(String.valueOf(i));
		shusshaJiKeySb			.append("ShusshaJi")				.append(String.valueOf(i));
		shusshaFunKeySb			.append("ShusshaFun")				.append(String.valueOf(i));
		taishaJiKeySb			.append("TaishaJi")					.append(String.valueOf(i));
		taishaFunKeySb			.append("TaishaFun")				.append(String.valueOf(i));
		jitsudoJikanKeySb		.append("JitsudoJikan")				.append(String.valueOf(i));
		bikoKeySb				.append("KintaiShinseiBiko")		.append(String.valueOf(i));
		kintaiShinseiKbn1KeySb	.append("KintaiShinseiKbn1")		.append(String.valueOf(i));
		kaishiJi1KeySb			.append("KintaiShinseiKaishiJi1")	.append(String.valueOf(i));
		kaishiFun1KeySb			.append("KintaiShinseiKaishiFun1")	.append(String.valueOf(i));
		shuryoJi1KeySb			.append("KintaiShinseiShuryoJi1")	.append(String.valueOf(i));
		shuryoFun1KeySb			.append("KintaiShinseiShuryoFun1")	.append(String.valueOf(i));
		jikan1KeySb				.append("KintaiShinseiJikan1")		.append(String.valueOf(i));
		kintaiShinseiKbn2KeySb	.append("KintaiShinseiKbn2")		.append(String.valueOf(i));
		kaishiJi2KeySb			.append("KintaiShinseiKaishiJi2")	.append(String.valueOf(i));
		kaishiFun2KeySb			.append("KintaiShinseiKaishiFun2")	.append(String.valueOf(i));
		shuryoJi2KeySb			.append("KintaiShinseiShuryoJi2")	.append(String.valueOf(i));
		shuryoFun2KeySb			.append("KintaiShinseiShuryoFun2")	.append(String.valueOf(i));
		jikan2KeySb				.append("KintaiShinseiJikan2")		.append(String.valueOf(i));
		kintaiShinseiKbn3KeySb	.append("KintaiShinseiKbn3")		.append(String.valueOf(i));
		kaishiJi3KeySb			.append("KintaiShinseiKaishiJi3")	.append(String.valueOf(i));
		kaishiFun3KeySb			.append("KintaiShinseiKaishiFun3")	.append(String.valueOf(i));
		shuryoJi3KeySb			.append("KintaiShinseiShuryoJi3")	.append(String.valueOf(i));
		shuryoFun3KeySb			.append("KintaiShinseiShuryoFun3")	.append(String.valueOf(i));
		jikan3KeySb				.append("KintaiShinseiJikan3")		.append(String.valueOf(i));
		
		String shukkinYoteiKbn		= this.getParameter(shukkinYoteiKbnKeySb.toString());
		String kintaiKbn			= this.getParameter(kintaiKbnKeySb.toString());
		String shusshaJi			= this.getParameter(shusshaJiKeySb.toString());
		String shusshaFun			= this.getParameter(shusshaFunKeySb.toString());
		String taishaJi				= this.getParameter(taishaJiKeySb.toString());
		String taishaFun			= this.getParameter(taishaFunKeySb.toString());
		String jitsudoJikan			= this.getParameter(jitsudoJikanKeySb.toString());
		String biko					= this.getParameter(bikoKeySb.toString());
		String kintaiShinseiKbn1	= this.getParameter(kintaiShinseiKbn1KeySb.toString());
		String kaishiJi1			= this.getParameter(kaishiJi1KeySb.toString());
		String kaishiFun1			= this.getParameter(kaishiFun1KeySb.toString());
		String shuryoJi1			= this.getParameter(shuryoJi1KeySb.toString());
		String shuryoFun1			= this.getParameter(shuryoFun1KeySb.toString());
		String jikan1				= this.getParameter(jikan1KeySb.toString());
		String kintaiShinseiKbn2	= this.getParameter(kintaiShinseiKbn2KeySb.toString());
		String kaishiJi2			= this.getParameter(kaishiJi2KeySb.toString());
		String kaishiFun2			= this.getParameter(kaishiFun2KeySb.toString());
		String shuryoJi2			= this.getParameter(shuryoJi2KeySb.toString());
		String shuryoFun2			= this.getParameter(shuryoFun2KeySb.toString());
		String jikan2				= this.getParameter(jikan2KeySb.toString());
		String kintaiShinseiKbn3	= this.getParameter(kintaiShinseiKbn3KeySb.toString());
		String kaishiJi3			= this.getParameter(kaishiJi3KeySb.toString());
		String kaishiFun3			= this.getParameter(kaishiFun3KeySb.toString());
		String shuryoJi3			= this.getParameter(shuryoJi3KeySb.toString());
		String shuryoFun3			= this.getParameter(shuryoFun3KeySb.toString());
		String jikan3				= this.getParameter(jikan3KeySb.toString());

		if(StringUtils.isEmpty(jikan1)) {
			jikan1 = "0";
		}
		if(StringUtils.isEmpty(jikan2)) {
			jikan2 = "0";
		}
		if(StringUtils.isEmpty(jikan3)) {
			jikan3 = "0";
		}
		if(StringUtils.isEmpty(jitsudoJikan)) {
			jitsudoJikan = "0";
		}

		HashMap<String, String> shinseiPatternRecord = getShinseiPattern(con, kintaiKbn, kintaiShinseiKbn1, kintaiShinseiKbn2, kintaiShinseiKbn3);
		String jikanKeisan1 = "0";
		String jikanKeisan2 = "0";
		String jikanKeisan3 = "0";
		String nissu = "0";
		String kihonJikan = "0";
		if("00".equals(kintaiKbn) == false) {
			if(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn1")) == false) {
				if("01".equals(shinseiPatternRecord.get("KaGenZanKbn1"))) {
					jikanKeisan1 = jikan1;
				}
				else if("02".equals(shinseiPatternRecord.get("KaGenZanKbn1"))) {
					jikanKeisan1 = "-" + jikan1;
				}
			}
			if(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn2")) == false) {
				if("01".equals(shinseiPatternRecord.get("KaGenZanKbn2"))) {
					jikanKeisan2 = jikan2;
				}
				else if("02".equals(shinseiPatternRecord.get("KaGenZanKbn2"))) {
					jikanKeisan2 = "-" + jikan2;
				}
			}
			if(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn3")) == false) {
				if("01".equals(shinseiPatternRecord.get("KaGenZanKbn3"))) {
					jikanKeisan3 = jikan3;
				}
				else if("02".equals(shinseiPatternRecord.get("KaGenZanKbn3"))) {
					jikanKeisan3 = "-" + jikan3;
				}
			}
			
			if(
					(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn1")) == false) &&
					(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn2")) == false) &&
					(StringUtils.isEmpty(shinseiPatternRecord.get("ShinseiKbn3")) == false)
					) {
				nissu = shinseiPatternRecord.get("Nissuu");
				kihonJikan = shinseiPatternRecord.get("KintaiKihonSagyoJikan");
			}
		}


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
		sql.append(" INSERT INTO ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI ");
		sql.append(" ( ");
		sql.append(" 	TaishoNenGetsudo, ");
		sql.append(" 	ShainNO, ");
		sql.append(" 	TaishoNengappi, ");
		sql.append(" 	YobiKbn, ");
		sql.append(" 	ShukkinYoteiKbn, ");
		sql.append(" 	KintaiKbn, ");

		sql.append(" 	ShusshaJi, ");
		sql.append(" 	ShusshaFun, ");
		sql.append(" 	TaishaJi, ");
		sql.append(" 	TaishaFun, ");
		sql.append(" 	JitsudoJikan, ");

		sql.append(" 	KintaiShinseiKbn1, ");
		sql.append(" 	KintaiShinseiKaishiJi1, ");
		sql.append(" 	KintaiShinseiKaishiFun1, ");
		sql.append(" 	KintaiShinseiShuryoJi1, ");
		sql.append(" 	KintaiShinseiShuryoFun1, ");
		sql.append(" 	KintaiShinseiJikan1, ");
		sql.append(" 	KintaiShinseiKyukeiJikan1, ");
		sql.append(" 	KintaiShinseiJikanKeisan1, ");

		sql.append(" 	KintaiShinseiKbn2, ");
		sql.append(" 	KintaiShinseiKaishiJi2, ");
		sql.append(" 	KintaiShinseiKaishiFun2, ");
		sql.append(" 	KintaiShinseiShuryoJi2, ");
		sql.append(" 	KintaiShinseiShuryoFun2, ");
		sql.append(" 	KintaiShinseiJikan2, ");
		sql.append(" 	KintaiShinseiKyukeiJikan2, ");
		sql.append(" 	KintaiShinseiJikanKeisan2, ");

		sql.append(" 	KintaiShinseiKbn3, ");
		sql.append(" 	KintaiShinseiKaishiJi3, ");
		sql.append(" 	KintaiShinseiKaishiFun3, ");
		sql.append(" 	KintaiShinseiShuryoJi3, ");
		sql.append(" 	KintaiShinseiShuryoFun3, ");
		sql.append(" 	KintaiShinseiJikan3, ");
		sql.append(" 	KintaiShinseiKyukeiJikan3, ");
		sql.append(" 	KintaiShinseiJikanKeisan3, ");

		sql.append(" 	KintaiShinseiBiko, ");
		sql.append(" 	KintaiShinseiNisuu, ");
		sql.append(" 	KintaiShinseiKihonJikan, ");

		sql.append(" 	SaishuKoshinShainNO, ");
		sql.append(" 	SaishuKoshinDate, ");
		sql.append(" 	SaishuKoshinJikan ");
		sql.append(" ) ");
		sql.append(" VALUES ");
		sql.append(" ( ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	?, ");
		pstmtf.addValue("String", taishoShainNo);
		sql.append(" 	?, ");
		pstmtf.addValue("String", taishoNengappi);
		sql.append(" 	?, ");
		pstmtf.addValue("String", yobi);
		sql.append(" 	?, ");
		if("00".equals(shukkinYoteiKbn)) {
			shukkinYoteiKbn = "";
		}
		pstmtf.addValue("String", shukkinYoteiKbn);
		sql.append(" 	?, ");
		if("00".equals(kintaiKbn)) {
			kintaiKbn = "";
		}
		pstmtf.addValue("String", kintaiKbn);

		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shusshaJi) == false) {
			shusshaJi = StringUtils.leftPad(shusshaJi, 2, '0');
			if("00".equals(kintaiKbn)) {
				shusshaJi = "";
			}
		}
		pstmtf.addValue("String", shusshaJi);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shusshaFun) == false) {
			shusshaFun = StringUtils.leftPad(shusshaFun, 2, '0');
			if("00".equals(kintaiKbn)) {
				shusshaFun = "";
			}
		}
		pstmtf.addValue("String", shusshaFun);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(taishaJi) == false) {
			taishaJi = StringUtils.leftPad(taishaJi, 2, '0');
			if("00".equals(kintaiKbn)) {
				taishaJi = "";
			}
		}
		pstmtf.addValue("String", taishaJi);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(taishaFun) == false) {
			taishaFun = StringUtils.leftPad(taishaFun, 2, '0');
			if("00".equals(kintaiKbn)) {
				taishaFun = "";
			}
		}
		pstmtf.addValue("String", taishaFun);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jitsudoJikan);

		sql.append(" 	?, ");
		pstmtf.addValue("String", kintaiShinseiKbn1);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(kaishiJi1) == false) {
			kaishiJi1 = StringUtils.leftPad(kaishiJi1, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiJi1 = "";
			}
		}
		pstmtf.addValue("String", kaishiJi1);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(kaishiFun1) == false) {
			kaishiFun1 = StringUtils.leftPad(kaishiFun1, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiFun1 = "";
			}
		}
		pstmtf.addValue("String", kaishiFun1);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shuryoJi1) == false) {
			shuryoJi1 = StringUtils.leftPad(shuryoJi1, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoJi1 = "";
			}
		}
		pstmtf.addValue("String", shuryoJi1);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shuryoFun1) == false) {
			shuryoFun1 = StringUtils.leftPad(shuryoFun1, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoFun1 = "";
			}
		}
		pstmtf.addValue("String", shuryoFun1);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikan1);
		sql.append(" 	0, ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanKeisan1);

		sql.append(" 	?, ");
		pstmtf.addValue("String", kintaiShinseiKbn2);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(kaishiJi2) == false) {
			kaishiJi2 = StringUtils.leftPad(kaishiJi2, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiJi2 = "";
			}
		}
		pstmtf.addValue("String", kaishiJi2);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(kaishiFun2) == false) {
			kaishiFun2 = StringUtils.leftPad(kaishiFun2, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiFun2 = "";
			}
		}
		pstmtf.addValue("String", kaishiFun2);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shuryoJi2) == false) {
			shuryoJi2 = StringUtils.leftPad(shuryoJi2, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoJi2 = "";
			}
		}
		pstmtf.addValue("String", shuryoJi2);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shuryoFun2) == false) {
			shuryoFun2 = StringUtils.leftPad(shuryoFun2, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoFun2 = "";
			}
		}
		pstmtf.addValue("String", shuryoFun2);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikan2);
		sql.append(" 	0, ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanKeisan2);

		sql.append(" 	?, ");
		pstmtf.addValue("String", kintaiShinseiKbn3);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(kaishiJi3) == false) {
			kaishiJi3 = StringUtils.leftPad(kaishiJi3, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiJi3 = "";
			}
		}
		pstmtf.addValue("String", kaishiJi3);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(kaishiFun3) == false) {
			kaishiFun3 = StringUtils.leftPad(kaishiFun3, 2, '0');
			if("00".equals(kintaiKbn)) {
				kaishiFun3 = "";
			}
		}
		pstmtf.addValue("String", kaishiFun3);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shuryoJi3) == false) {
			shuryoJi3 = StringUtils.leftPad(shuryoJi3, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoJi3 = "";
			}
		}
		pstmtf.addValue("String", shuryoJi3);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shuryoFun3) == false) {
			shuryoFun3 = StringUtils.leftPad(shuryoFun3, 2, '0');
			if("00".equals(kintaiKbn)) {
				shuryoFun3 = "";
			}
		}
		pstmtf.addValue("String", shuryoFun3);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikan3);
		sql.append(" 	0, ");
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanKeisan3);

		sql.append(" 	?, ");
		pstmtf.addValue("String", biko);
		sql.append(" 	?, ");
		pstmtf.addValue("String", nissu);
		sql.append(" 	?, ");
		pstmtf.addValue("String", kihonJikan);

		sql.append(" 	?, ");
		pstmtf.addValue("String", loginShainNo);
		sql.append(" 	?, ");
		pstmtf.addValue("String", PJActionBase.getNowDate());
		sql.append(" 	? ");
		pstmtf.addValue("String", PJActionBase.getNowTime());
		sql.append(" ) ");
		
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
	 * 申請パターン取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private HashMap<String, String> getShinseiPattern(Connection con, String kintaiKbn, String kintaiShinseiKbn1, String kintaiShinseiKbn2, String kintaiShinseiKbn3){
		
		HashMap<String, String> result = new HashMap<String, String>();
				
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	KintaiKbn, ");
		sql.append(" 	ShinseiKbn1, ");
		sql.append(" 	ShinseiKbn2, ");
		sql.append(" 	ShinseiKbn3, ");
		sql.append(" 	Nissuu, ");
		sql.append(" 	CASE WHEN KihonSagyoJikanKbn = '01' THEN ");
		sql.append(" 		(SELECT KintaiKihonSagyoJikan FROM MST_KANRI) ");
		sql.append(" 		ELSE 0 END AS KintaiKihonSagyoJikan, ");
		sql.append(" 	KihonSagyoJikanKbn, ");
		sql.append(" 	KyukeiJikanKbn, ");
		sql.append(" 	KaGenZanKbn1, ");
		sql.append(" 	KaGenZanKbn2, ");
		sql.append(" 	KaGenZanKbn3 ");
		sql.append(" FROM ");
		sql.append(" 	MST_SHINSEI_PATTERN ");
		sql.append(" WHERE ");
		sql.append(" 	SyukinboNyuryokuKbn = '01' ");
		sql.append(" AND KintaiKbn = ? ");
		sql.append(" AND ShinseiKbn1 = ? ");
		sql.append(" AND ShinseiKbn2 = ? ");
		sql.append(" AND ShinseiKbn3 = ? ");

		pstmtf.addValue("String", kintaiKbn);
		pstmtf.addValue("String", kintaiShinseiKbn1);
		pstmtf.addValue("String", kintaiShinseiKbn2);
		pstmtf.addValue("String", kintaiShinseiKbn3);

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
				// カラム名をkeyとして値を格納
				for (int j = 1; j <= colCount; j++) {
					result.put(metaData.getColumnLabel(j), StringUtils.stripToEmpty(rset.getString(j)));
				}
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

	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void delete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// DB接続
		Connection con		= this.getConnection("kintai", req);

		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String taishoYM			= this.getParameter("txtSearchedTaishoYM");
		String taishoShainNo	= this.getParameter("txtSearchedShainNO");
		
		boolean result = false;
		//トランザクション開始
		con.setAutoCommit(false);

		//出勤簿明細の削除
		result = deleteMeisaiRow(con, taishoYM, taishoShainNo);

		if(result) {
			//出勤簿基本の削除
			result = deleteKihonRow(con, taishoYM, taishoShainNo);
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
	 * 出勤簿明細のレコード削除
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean deleteMeisaiRow(Connection con, String taishoYM, String taishoShainNo) throws Exception {
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
		sql.append(" DELETE FROM ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI ");
		sql.append(" WHERE ");
		sql.append(" 	TaishoNenGetsudo = ? AND ");
		sql.append(" 	ShainNO = ? ");
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);

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
	 * 出勤簿明細のレコード削除
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private boolean deleteKihonRow(Connection con, String taishoYM, String taishoShainNo) throws Exception {
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
		sql.append(" DELETE FROM ");
		sql.append(" 	KIN_SHUKKINBO_KIHON ");
		sql.append(" WHERE ");
		sql.append(" 	TaishoNenGetsudo = ? AND ");
		sql.append(" 	ShainNO = ? ");
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);

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
	
}