package jp.co.kintai.carreservation.action.kintai;

import java.math.BigDecimal;
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

public class ChiChinginkeisanshoAction extends PJActionBase {
	public ChiChinginkeisanshoAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		HashMap<String, Object> Result = new HashMap<>();
		
		//対象年月初期値取得
		String taishoYM = getTaishoYM(req, res);
		Result.put("taishoYM", taishoYM);
		
		req.setAttribute("result", taishoYM);
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
	public String getTaishoYM(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String result = "";

		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		// チェック対象の社員情報の取得
		ArrayList<HashMap<String, String>> mstKanris = PJActionBase.getMstKanris(con, null);

		if (0 < mstKanris.size()) {
			HashMap<String, String> mstKanri = mstKanris.get(0);
			result = mstKanri.get("GenzaishoriNengetsudo");
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		return result;
	}
	
	/**
	 * DDLの内容取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getDDL(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);

		
		// チェック対象の社員NO
		String shainNo			= this.getParameter("srhTxtShainNO");
		String JikyuNikkyuKbn = "";
		
		//=====================================================================
		// 処理
		//=====================================================================
		// チェック対象の社員の存在確認、時給日給区分取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, null, null, null, null, null, null);
		
		//社員が存在すれば時給日給区分取得する
		if (0 < mstShains.size()){
			JikyuNikkyuKbn = mstShains.get(0).get("JikyuNikkyuKbn"); 
		}
		
		
		//申請区分DDL検索 ソート順が独自なので独自実装
		StringBuffer sql					= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset						= null;
		
		sql.append("	SELECT ");
		sql.append("		COALESCE (Code, '') AS Code, ");
		sql.append("		COALESCE (KbnName, '') AS KbnName ");
		sql.append("	FROM ");
		sql.append("		MST_KUBUN ");
		sql.append("	WHERE ");
		sql.append("		KbnCode = '0201' ");
		sql.append("	AND	LEFT(GroupCode2, 2) <> '' ");
		sql.append("	ORDER BY ");
		sql.append("		LEFT(GroupCode2, 2) ");
		
		try {
			// SQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// レコード数分繰り返す
			while (rset.next()){
				//区分名が空の場合スキップ
				if(StringUtils.isEmpty(rset.getString("KbnName"))){
					continue;
				}
				
				//Codeが"01"はスキップ
				if("01".equals(rset.getString("Code"))) {
					continue;
				}
				
				//社員が時給　かつ　Codeが"07","08","09"の場合スキップ
				if(
					"01".equals(JikyuNikkyuKbn) &&
					(
						"07".equals(rset.getString("Code")) ||
						"08".equals(rset.getString("Code")) ||
						"09".equals(rset.getString("Code"))
					)
				) {
					continue;
				}
				// 1レコード分の配列を用意
				HashMap<String, String> returnRecord = new HashMap<String, String>();

				//値を格納
				returnRecord.put("Code", rset.getString("Code"));
				returnRecord.put("KbnName", rset.getString("KbnName"));

				// 配列の格納
				mstDatas.add(returnRecord);
			}
		}
		catch (Exception exp){
			System.out.println(String.valueOf(exp));
		}
		finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
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
		String taishoYM			= this.getParameter("srhTxtTaishoYM");
		String taishoShainNo	= this.getParameter("srhTxtShainNO");
		
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
		sql1.append("		TOP 1 CHI_CHINGINKEISANSHO_KIHON.KakuteiKbn ");
		sql1.append("	FROM ");
		sql1.append("		CHI_CHINGINKEISANSHO_KIHON WITH(NOLOCK) ");
		sql1.append("	INNER JOIN ");
		sql1.append("		MST_SHAIN WITH(NOLOCK) ");
		sql1.append("	ON ");
		sql1.append("		MST_SHAIN.ShainNO = CHI_CHINGINKEISANSHO_KIHON.ShainNO ");
		sql1.append("	WHERE ");
		sql1.append("		CHI_CHINGINKEISANSHO_KIHON.TaishoNenGetsudo = ? ");
		sql1.append("	AND	CHI_CHINGINKEISANSHO_KIHON.KakuteiKbn = '03' ");
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
			if (rset1.next()){
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
		sql2.append("		CHI_CHINGINKEISANSHO_KIHON WITH(NOLOCK) ");
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
			if (rset2.next()){
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
	 * 賃金計算書の取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		HashMap<String, Object> Result = new HashMap<>();
		// 検索条件取得
		String taishoYM			= this.getParameter("srhTxtTaishoYM");
		String taishoShainNo	= this.getParameter("srhTxtShainNO");

		// DB接続
		Connection con		= this.getConnection("kintai", req);

		ArrayList<HashMap<String, String>> meisaiResult = new ArrayList<>();
		meisaiResult = searchMeisaiArea(req, res, con, taishoYM, taishoShainNo);
		Result.put("chinginkeisanshoArea", meisaiResult);

		HashMap<String, String> tokubetsuNyuryokuResult = new HashMap<>();
		tokubetsuNyuryokuResult = searchTokubetsuNyuryokuArea(req, res, con, taishoShainNo);
		Result.put("tokubetsuNyuryokuArea", tokubetsuNyuryokuResult);

		HashMap<String, String> shukeiResult = new HashMap<>();
		shukeiResult = searchShukeiArea(req, res, con, taishoYM, taishoShainNo);
		Result.put("shukeiArea", shukeiResult);
		
		
		this.addContent("result", Result);
		
	}

	/**
	 * 賃金計算書の取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public ArrayList<HashMap<String, String>> searchMeisaiArea(HttpServletRequest req, HttpServletResponse res, Connection con, String taishoYM, String taishoShainNo) throws Exception {
		
		//検索結果0件の時のため、デフォルトのデータを作成
		ArrayList<HashMap<String, String>> ResultDatas = getResultDatas(taishoYM);
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append("         M.TaishoNengappi, ");
		sql.append("         MONTH(CONVERT(DATETIME, M.TaishoNengappi, 111)) AS TaishoGetsu, ");
		sql.append("         DAY(CONVERT(DATETIME, M.TaishoNengappi, 111)) AS TaishoBi, ");
		sql.append("         M.YobiKbn,");
		sql.append("         COALESCE(RTRIM(M.ShusshaJi), '') AS ShusshaJi, ");
		sql.append("         COALESCE(RTRIM(M.ShusshaFun), '') AS ShusshaFun, ");
		sql.append("         COALESCE(RTRIM(M.TaishaJi), '') AS TaishaJi, ");
		sql.append("         COALESCE(RTRIM(M.TaishaFun), '') AS TaishaFun, ");
		sql.append("         COALESCE(M.JitsudoJikan, 0) AS JitsudoJikan, ");
		sql.append("         COALESCE(M.ChinginShinseiKbn1, '00') AS ChinginShinseiKbn1, ");
		sql.append("         COALESCE(M.ChinginShinseiJikan1, 0) AS ChinginShinseiJikan1, ");
		sql.append("         COALESCE(M.ChinginShinseiKbn2, '00') AS ChinginShinseiKbn2, ");
		sql.append("         COALESCE(M.ChinginShinseiJikan2, 0) AS ChinginShinseiJikan2, ");
		sql.append("         COALESCE(M.ChinginShinseiKbn3, '00') AS ChinginShinseiKbn3, ");
		sql.append("         COALESCE(M.ChinginShinseiJikan3, 0) AS ChinginShinseiJikan3, ");
		sql.append("         COALESCE(M.SaishuKoshinDate, '') AS MeisaiSaishuKoshinDate, ");
		sql.append("         COALESCE(M.SaishuKoshinJikan, '') AS MeisaiSaishuKoshinJikan, ");

		sql.append("         COALESCE(K.SaishuKoshinDate, '') AS KihonSaishuKoshinDate, ");
		sql.append("         COALESCE(K.SaishuKoshinJikan, '') AS KihonSaishuKoshinJikan ");
		sql.append(" FROM ");
		sql.append("         CHI_CHINGINKEISANSHO_MEISAI M WITH(NOLOCK) ");
		sql.append(" LEFT OUTER JOIN");
		sql.append("         CHI_CHINGINKEISANSHO_KIHON K WITH(NOLOCK) ");
		sql.append(" ON");
		sql.append("         K.TaishoNenGetsudo = M.TaishoNenGetsudo AND");
		sql.append("         K.ShainNO = M.ShainNO ");

		sql.append(" WHERE");
		sql.append("         M.TaishoNenGetsudo = ? AND");
		sql.append("         M.ShainNO = ? ");
		sql.append(" ORDER BY M.TaishoNengappi");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();

			// レコード数分繰り返す
			while (rset.next()){
				for(int i = 0;i < ResultDatas.size();i++) {
					if(
							ResultDatas.get(i).get("txtTaishoGetsu").equals(StringUtils.stripToEmpty(rset.getString("TaishoGetsu"))) &&
							ResultDatas.get(i).get("txtTaishoBi").equals(StringUtils.stripToEmpty(rset.getString("TaishoBi")))
							) {
						// 1レコード分の配列を用意
						HashMap<String, String> record = new HashMap<String, String>();
						record.put("txtTaishoNengappi",				StringUtils.stripToEmpty(rset.getString("TaishoNengappi")));
						record.put("txtTaishoGetsu",				StringUtils.stripToEmpty(rset.getString("TaishoGetsu")));
						record.put("txtTaishoBi",					StringUtils.stripToEmpty(rset.getString("TaishoBi")));
						record.put("txtYobiKbn",					StringUtils.stripToEmpty(rset.getString("YobiKbn")));
						record.put("numShusshaJi",					StringUtils.stripToEmpty(rset.getString("ShusshaJi")));
						record.put("numShusshaFun",					StringUtils.stripToEmpty(rset.getString("ShusshaFun")));
						record.put("numTaishaJi",					StringUtils.stripToEmpty(rset.getString("TaishaJi")));
						record.put("numTaishaFun",					StringUtils.stripToEmpty(rset.getString("TaishaFun")));
						record.put("numJitsudoJikan",				StringUtils.stripToEmpty(rset.getString("JitsudoJikan")));
						record.put("selChinginShinseiKbn1",			StringUtils.stripToEmpty(rset.getString("ChinginShinseiKbn1")));
						record.put("numChinginShinseiJikan1",		StringUtils.stripToEmpty(rset.getString("ChinginShinseiJikan1")));
						record.put("selChinginShinseiKbn2",			StringUtils.stripToEmpty(rset.getString("ChinginShinseiKbn2")));
						record.put("numChinginShinseiJikan2",		StringUtils.stripToEmpty(rset.getString("ChinginShinseiJikan2")));
						record.put("selChinginShinseiKbn3",			StringUtils.stripToEmpty(rset.getString("ChinginShinseiKbn3")));
						record.put("numChinginShinseiJikan3",		StringUtils.stripToEmpty(rset.getString("ChinginShinseiJikan3")));
						record.put("txtMeisaiSaishuKoshinDate",		StringUtils.stripToEmpty(rset.getString("MeisaiSaishuKoshinDate")));
						record.put("txtMeisaiSaishuKoshinJikan",	StringUtils.stripToEmpty(rset.getString("MeisaiSaishuKoshinJikan")));
						record.put("KihonSaishuKoshinDate",			StringUtils.stripToEmpty(rset.getString("KihonSaishuKoshinDate")));
						record.put("KihonSaishuKoshinJikan",		StringUtils.stripToEmpty(rset.getString("KihonSaishuKoshinJikan")));
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
		return ResultDatas;
		
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
			record.put("txtTaishoNengappi", wkTaishoYMLD.format(dtf));
			record.put("txtTaishoGetsu", String.valueOf(wkTaishoYMLD.getMonthValue()));
			record.put("txtTaishoBi", String.valueOf(wkTaishoYMLD.getDayOfMonth()));
			
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
			record.put("txtYobiKbn", yobi);
			record.put("numShusshaJi","");
			record.put("numShusshaFun","");
			record.put("numTaishaJi","");
			record.put("numTaishaFun","");
			record.put("numJitsudoJikan","0.00");
			record.put("selChinginShinseiKbn1","00");
			record.put("numChinginShinseiJikan1","0.00");
			record.put("selChinginShinseiKbn2","00");
			record.put("numChinginShinseiJikan2","0.00");
			record.put("selChinginShinseiKbn3","00");
			record.put("numChinginShinseiJikan3","0.00");
			
			
			record.put("txtMeisaiSaishuKoshinDate","");
			record.put("txtMeisaiSaishuKoshinJikan","");
			record.put("txtKihonSaishuKoshinDate","");
			record.put("txtKihonSaishuKoshinJikan","");
			// 配列の格納
			ResultDatas.add(record);
			
			wkTaishoYMLD = wkTaishoYMLD.plusDays(1);
		}
		
		return ResultDatas;
	}
	
	/**
	 * 所定(契約)勤務時間、実働時間の取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public HashMap<String, String> searchTokubetsuNyuryokuArea(HttpServletRequest req, HttpServletResponse res, Connection con, String taishoShainNo) throws Exception {

		HashMap<String, String> returnRecord = new HashMap<String, String>();
		
		String kinmuKaishi = "00:00";
		String kinmuShuryo = "00:00";
		String jitsudojikan = "";
		
		// 現在日付の取得
		String nowDate	= PJActionBase.getNowDate();
		
		// チェック対象の社員情報の取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, taishoShainNo, null, null, null, null, null, null, nowDate);

		if (0 < mstShains.size()) {
			HashMap<String, String> mstShain = mstShains.get(0);
			kinmuKaishi = mstShain.get("KinmuKaishiJi") + ":" + mstShain.get("KinmuKaishiFun");
			kinmuShuryo = mstShain.get("KinmuShuryoJi") + ":" + mstShain.get("KinmuShuryoFun");
			jitsudojikan = mstShain.get("KeiyakuJitsudoJikan");
		}
		
		//値を格納
		returnRecord.put("kinmuKaishi", kinmuKaishi);
		returnRecord.put("kinmuShuryo", kinmuShuryo);
		returnRecord.put("jitsudojikan", jitsudojikan);

		//=====================================================================
		// 結果返却
		//=====================================================================
		return returnRecord;
		
	}
	
	/**
	 * 集計エリアの取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public HashMap<String, String> searchShukeiArea(HttpServletRequest req, HttpServletResponse res, Connection con, String taishoYM, String taishoShainNo) throws Exception {
		HashMap<String, String> ResultDatas = new HashMap<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append("     KakuteiKbn, ");
		sql.append("     ShinseiNissu01, ");
		sql.append("     ShinseiNissu02, ");
		sql.append("     ShinseiNissu03, ");
		sql.append("     ShinseiNissu04, ");
		sql.append("     ShinseiNissu05 + ShinseiNissu06 AS ShinseiNissu05, ");
		sql.append("     ShinseiNissu06, ");
		sql.append("     ShinseiNissu07, ");
		sql.append("     ShinseiNissu08, ");
		sql.append("     ShinseiNissu09, ");
		sql.append("     ShinseiNissu10, ");
		sql.append("     ShinseiNissu11, ");
		
		// 休日
		sql.append("     ( ");
		sql.append("         SELECT    ");
		sql.append("             CAST(COUNT('a') AS DECIMAL) ");
		sql.append("         FROM ");
		sql.append("             CHI_CHINGINKEISANSHO_MEISAI WITH(NOLOCK) ");
		sql.append("         WHERE TaishoNenGetsudo = ? ");
		sql.append("             AND ShainNO = ? ");
		sql.append("             AND ShusshaJi = '' ");
		sql.append("             AND ShusshaFun = '' ");
		sql.append("             AND TaishaJi = '' ");
		sql.append("             AND TaishaFun = '' ");
		sql.append("             AND JitsudoJikan = 0 ");
		sql.append("             AND ChinginShinseiKbn1 IN ('', '0', '00') ");
		sql.append("             AND ChinginShinseiJikan1 = 0 ");
		sql.append("             AND ChinginShinseiKbn2 IN ('', '0', '00') ");
		sql.append("             AND ChinginShinseiJikan2 = 0 ");
		sql.append("             AND ChinginShinseiKbn3 IN ('', '0', '00') ");
		sql.append("             AND ChinginShinseiJikan3 = 0 ");
		sql.append("     ) AS ShinseiNissuKyujitsu, ");
		
		sql.append("     ShinseiJikan01, ");
		sql.append("     ShinseiJikan02, ");
		sql.append("     ShinseiJikan03, ");
		sql.append("     ShinseiJikan04, ");
		sql.append("     ShinseiJikan05 + ShinseiJikan06 AS ShinseiJikan05, ");
		sql.append("     ShinseiJikan06, ");
		sql.append("     ShinseiJikan07, ");
		sql.append("     ShinseiJikan08, ");
		sql.append("     ShinseiJikan09, ");
		sql.append("     ShinseiJikan10, ");
		sql.append("     ShinseiJikan11, ");
		sql.append("     ShinseiTanka01, ");
		sql.append("     ShinseiTanka02, ");
		sql.append("     ShinseiTanka03, ");
		sql.append("     ShinseiTanka04, ");
		sql.append("     ShinseiTanka05, ");
		sql.append("     ShinseiTanka06, ");
		sql.append("     ShinseiTanka07, ");
		sql.append("     ShinseiTanka08, ");
		sql.append("     ShinseiTanka09, ");
		sql.append("     ShinseiTanka10, ");
		sql.append("     ShinseiTanka11, ");
		sql.append("     ShinseiKingakuGoukei01, ");
		sql.append("     ShinseiKingakuGoukei02, ");
		sql.append("     ShinseiKingakuGoukei03, ");
		sql.append("     ShinseiKingakuGoukei04, ");
		sql.append("     ShinseiKingakuGoukei05 + ShinseiKingakuGoukei06 AS ShinseiKingakuGoukei05, ");
		sql.append("     ShinseiKingakuGoukei06, ");
		sql.append("     ShinseiKingakuGoukei07, ");
		sql.append("     ShinseiKingakuGoukei08, ");
		sql.append("     ShinseiKingakuGoukei09, ");
		sql.append("     ShinseiKingakuGoukei10, ");
		sql.append("     ShinseiKingakuGoukei11, ");
		sql.append("     TokkiJiko, ");
		
		// 休日
		sql.append("     ( ");
		sql.append("         SELECT    ");
		sql.append("             CAST(COUNT('a') AS DECIMAL) ");
		sql.append("         FROM ");
		sql.append("             CHI_CHINGINKEISANSHO_MEISAI WITH(NOLOCK) ");
		sql.append("         WHERE TaishoNenGetsudo = ? ");
		sql.append("             AND ShainNO = ? ");
		sql.append("             AND ShusshaJi = '' ");
		sql.append("             AND ShusshaFun = '' ");
		sql.append("             AND TaishaJi = '' ");
		sql.append("             AND TaishaFun = '' ");
		sql.append("             AND JitsudoJikan = 0 ");
		sql.append("             AND ChinginShinseiKbn1 IN ('', '0', '00') ");
		sql.append("             AND ChinginShinseiJikan1 = 0 ");
		sql.append("             AND ChinginShinseiKbn2 IN ('', '0', '00') ");
		sql.append("             AND ChinginShinseiJikan2 = 0 ");
		sql.append("             AND ChinginShinseiKbn3 IN ('', '0', '00') ");
		sql.append("             AND ChinginShinseiJikan3 = 0 ");
		sql.append("     ) + ");
		
		sql.append("     ShinseiNissu01 + ");
		sql.append("     ShinseiNissu04 + ShinseiNissu05 + ShinseiNissu06 AS ShinseiNisuuGoukei, ");
		
		sql.append("     ShinseiJikan01 + ");
		sql.append("     ShinseiJikan02 + ");
		sql.append("     ShinseiJikan03 + ");
		sql.append("     ShinseiJikan04 + ");
		sql.append("     ShinseiJikan05 + ");
		sql.append("     ShinseiJikan06 + ");
		sql.append("     ShinseiJikan07 + ");
		sql.append("     ShinseiJikan08 + ");
		sql.append("     ShinseiJikan09 + ");
		sql.append("     ShinseiJikan10 + ");
		sql.append("     ShinseiJikan11 AS ShinseiJikanGoukei, ");
		
		sql.append("     ShinseiKingakuGoukei01 + ");
		sql.append("     ShinseiKingakuGoukei02 + ");
		sql.append("     ShinseiKingakuGoukei03 + ");
		sql.append("     ShinseiKingakuGoukei04 + ");
		sql.append("     ShinseiKingakuGoukei05 + ");
		sql.append("     ShinseiKingakuGoukei06 + ");
		sql.append("     ShinseiKingakuGoukei07 + ");
		sql.append("     ShinseiKingakuGoukei08 + ");
		sql.append("     ShinseiKingakuGoukei09 + ");
		sql.append("     ShinseiKingakuGoukei10 + ");
		sql.append("     ShinseiKingakuGoukei11 AS ShinseiKingakuGoukeiGoukei ");
		sql.append(" FROM ");
		sql.append("     CHI_CHINGINKEISANSHO_KIHON WITH(NOLOCK) ");
		sql.append(" WHERE TaishoNenGetsudo = ? ");
		sql.append("     AND ShainNO = ? ");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
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
			if (rset.next()){
				// カラム名をkeyとして値を格納
				for (int j = 1; j <= colCount; j++) {
					ResultDatas.put(metaData.getColumnLabel(j), StringUtils.stripToEmpty(rset.getString(j)));
				}
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		return ResultDatas;
		
	}
	
	/**
	 * 出勤簿の再計算　登録検索し、ロールバックする
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void recalc(HttpServletRequest req, HttpServletResponse res) throws Exception {
		HashMap<String, Object> Result = new HashMap<>();
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String taishoYM			= this.getParameter("txtTaishoYM");
		String taishoShainNo	= this.getParameter("txtShainNO");
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		//トランザクション開始
		con.setAutoCommit(false);

		//対象社員の営業所・部署コード取得
		String nowDate	= PJActionBase.getNowDate();
		String eigyoshoCode		= "";
		String bushoCode		= "";
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, taishoShainNo, null, null, null, null, null, null, nowDate);
		if (0 < mstShains.size()) {
			HashMap<String, String> mstShain = mstShains.get(0);
			eigyoshoCode = mstShain.get("EigyoshoCode");
			bushoCode = mstShain.get("BushoCode");
		}

		//登録
		updateExecute(req, res, con, taishoYM, taishoShainNo, eigyoshoCode, bushoCode);

		ArrayList<HashMap<String, String>> meisaiResult = new ArrayList<>();
		meisaiResult = searchMeisaiArea(req, res, con, taishoYM, taishoShainNo);
		Result.put("chinginkeisanshoArea", meisaiResult);

		HashMap<String, String> tokubetsuNyuryokuResult = new HashMap<>();
		tokubetsuNyuryokuResult = searchTokubetsuNyuryokuArea(req, res, con, taishoShainNo);
		Result.put("tokubetsuNyuryokuArea", tokubetsuNyuryokuResult);

		HashMap<String, String> shukeiResult = new HashMap<>();
		shukeiResult = searchShukeiArea(req, res, con, taishoYM, taishoShainNo);
		Result.put("shukeiArea", shukeiResult);
		
		//ロールバック
		con.rollback();
		this.addContent("result", Result);
	}
	
	/**
	 * 出勤簿の更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void update(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String taishoYM			= this.getParameter("txtTaishoYM");
		String taishoShainNo	= this.getParameter("txtShainNO");
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		//トランザクション開始
		con.setAutoCommit(false);

		//対象社員の営業所・部署コード取得
		String nowDate	= PJActionBase.getNowDate();
		String eigyoshoCode		= "";
		String bushoCode		= "";
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, taishoShainNo, null, null, null, null, null, null, nowDate);
		if (0 < mstShains.size()) {
			HashMap<String, String> mstShain = mstShains.get(0);
			eigyoshoCode = mstShain.get("EigyoshoCode");
			bushoCode = mstShain.get("BushoCode");
		}

		int returnval = 0;
		returnval = updateExecute(req, res, con, taishoYM, taishoShainNo, eigyoshoCode, bushoCode);
		if(returnval == 0) {
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
	 * 出勤簿の更新
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public int updateExecute(HttpServletRequest req, HttpServletResponse res, Connection con, String taishoYM, String taishoShainNo, String eigyoshoCode, String bushoCode) throws Exception {

		//=====================================================================
		// ユーザー情報の取得
		//=====================================================================
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		String loginShainNo = userInformation.getShainNO();
		
		boolean result = false;
		int returnval = 0;
		//1か月分入力項目があるので1か月分ループ
		for(int i = 0;i < 31;i++){
			StringBuilder taishoNengappiKeySb	= new StringBuilder();
			taishoNengappiKeySb	.append("txtTaishoNengappi")	.append(String.valueOf(i));
			String taishoNengappi		= this.getParameter(taishoNengappiKeySb.toString());
			
			if(StringUtils.isEmpty(taishoNengappi)) {
				//データが終わったので終了
				break;
			}
			
			if(getChinginkeisanshoMeisaiCount(con, taishoYM, taishoShainNo, taishoNengappi) > 0) {
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
			if(getChinginkeisanshoKihonCount(con, taishoYM, taishoShainNo, eigyoshoCode, bushoCode) > 0) {
				returnval = 2;
				result = updateKihonRow(con, taishoYM, taishoShainNo, loginShainNo, eigyoshoCode, bushoCode);
			}
			else {
				returnval = 1;
				result = insertKihonRow(con, taishoYM, taishoShainNo, loginShainNo, eigyoshoCode, bushoCode);
			}
		}
		if(result == false) {
			returnval = 0;
		}
		return returnval;
	}
	
	/**
	 * 出勤簿の更新対象レコードの存在確認
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	private int getChinginkeisanshoKihonCount(Connection con, String taishoYM, String taishoShainNo, String eigyoshoCode, String bushoCode) throws Exception {
		int result = 0;
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
		sql.append(" FROM ");
		sql.append(" 	CHI_CHINGINKEISANSHO_KIHON ");
		
		sql.append(" WHERE ");
		sql.append(" 	TaishoNengetsudo = ? ");
		sql.append(" AND	ShainNO = ? ");
		sql.append(" AND	EigyoshoCode = ? ");
		sql.append(" AND	BushoCode = ? ");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		pstmtf.addValue("String", eigyoshoCode);
		pstmtf.addValue("String", bushoCode);
		
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if (rset.next()){
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
		
		sql.append("     SELECT ");
		sql.append("         '01' AS Kbn, ");
		sql.append("         SUM(Q1.ChinginShinseiNisuu) AS ChinginShinseiNisuu ");
		sql.append("     FROM ");
		sql.append("     ( ");
		sql.append("         SELECT ");
		sql.append("             * ");
		sql.append("          FROM ");
		sql.append("              CHI_CHINGINKEISANSHO_MEISAI MEISAI WITH(NOLOCK) ");
		sql.append("          WHERE ");
		sql.append("             MEISAI.TaishoNenGetsudo = ? ");
		sql.append("             AND MEISAI.ShainNO = ? ");
		
		sql.append("         EXCEPT ");
		
		sql.append("         SELECT ");
		sql.append("             * ");
		sql.append("          FROM ");
		sql.append("              CHI_CHINGINKEISANSHO_MEISAI MEISAI WITH(NOLOCK) ");
		sql.append("          WHERE ");
		sql.append("             MEISAI.TaishoNenGetsudo = ? ");
		sql.append("             AND MEISAI.ShainNO = ? ");
		sql.append("             AND MEISAI.ChinginShinseiKbn1 = '00' ");
		sql.append("             AND MEISAI.ChinginShinseiKbn2 = '00' ");
		sql.append("             AND MEISAI.ChinginShinseiKbn3 = '00' ");
		sql.append("             AND MEISAI.JitsudoJikan = 0 ");
		
		sql.append("      ) Q1 ");
		sql.append("      WHERE Q1.ChinginShinseiKbn1 <> '04' AND Q1.ChinginShinseiKbn2 <> '04' ");
		sql.append("            AND Q1.ChinginKbn NOT IN ('05', '06') "); // 有給、半給は除く
		
		sql.append("     UNION ALL ");

		sql.append("     SELECT ");
		sql.append("         LEFT(M1.GroupCode1, 2) AS Kbn, ");
		sql.append("         SUM(MEISAI.ChinginShinseiNisuu) AS ChinginShinseiNisuu ");
		sql.append("     FROM ");
		sql.append("         CHI_CHINGINKEISANSHO_MEISAI MEISAI WITH(NOLOCK) ");
		sql.append("     LEFT OUTER JOIN MST_KUBUN M1 WITH(NOLOCK) ");
		sql.append("         ON  M1.KbnCode = '0201' ");
		sql.append("         AND M1.Code = MEISAI.ChinginKbn ");
		sql.append("         AND M1.Code <> '01' ");
		sql.append("     WHERE ");
		sql.append("         MEISAI.ShusshaJi <> '00' ");
		sql.append("         AND MEISAI.ShainNO = ? ");
		sql.append("         AND MEISAI.TaishoNenGetsudo = ? ");
		sql.append("         and MEISAI.ChinginShinseiKbn1 <> '02' and MEISAI.ChinginShinseiKbn2 <> '02' and MEISAI.ChinginShinseiKbn3 <> '02' ");
		sql.append("     GROUP BY ");
		sql.append("         LEFT(M1.GroupCode1, 2) ");
		sql.append(" ) ");
		sql.append(" SELECT ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn IN ('01', '06') THEN ChinginShinseiNisuu END), 0) AS Nissu01, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '02' THEN ChinginShinseiNisuu END), 0) AS Nissu02, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '03' THEN ChinginShinseiNisuu END), 0) AS Nissu03, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '04' THEN ChinginShinseiNisuu END), 0) AS Nissu04, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '05' THEN ChinginShinseiNisuu END), 0) AS Nissu05, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '06' THEN ChinginShinseiNisuu END), 0) AS Nissu06, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '07' THEN ChinginShinseiNisuu END), 0) AS Nissu07, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '08' THEN ChinginShinseiNisuu END), 0) AS Nissu08, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn IN ('01', '04') THEN ChinginShinseiNisuu END) + SUM(CASE WHEN Kbn IN ('06') THEN ChinginShinseiNisuu * 2 ELSE 0 END), 0) AS Nissu09, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '10' THEN ChinginShinseiNisuu END), 0) AS Nissu10,  ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '11' THEN ChinginShinseiNisuu END), 0) AS Nissu11  ");
		sql.append(" FROM CteNissu ");
		
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
	
	private HashMap<String, String> getJikan(Connection con, String taishoYM, String taishoShainNo){
		
		HashMap<String, String> result = new HashMap<String, String>();
				
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		

		sql.append(" ;WITH CteJikan AS ");
		sql.append(" ( ");
		sql.append("     SELECT ");
		sql.append("         '01' AS Kbn, ");
		sql.append("         SUM(MEISAI.JitsudoJikan) AS ChinginShinseiJikanKeisan "); // 実働時間
		sql.append("      FROM ");
		sql.append("          CHI_CHINGINKEISANSHO_MEISAI MEISAI WITH(NOLOCK) ");
		sql.append("      WHERE ");
		sql.append("         MEISAI.TaishoNenGetsudo = ? ");
		sql.append("         AND MEISAI.ShainNO = ? ");

		sql.append("     UNION ALL ");

		sql.append("     SELECT ");
		sql.append("         LEFT(M1.GroupCode1, 2) AS Kbn, ");
		sql.append("         SUM(MEISAI.ChinginShinseiJikanKeisan1) AS ChinginShinseiJikanKeisan ");
		sql.append("     FROM ");
		sql.append("         CHI_CHINGINKEISANSHO_MEISAI MEISAI WITH(NOLOCK) ");
		sql.append("     LEFT OUTER JOIN MST_KUBUN M1 WITH(NOLOCK) ");
		sql.append("         ON  M1.KbnCode = '0201' ");
		sql.append("         AND M1.Code = MEISAI.ChinginShinseiKbn1 ");
		sql.append("     WHERE ");
		sql.append("         MEISAI.ChinginShinseiKbn1 <> '00' ");
		sql.append("         AND MEISAI.TaishoNenGetsudo = ? ");
		sql.append("         AND MEISAI.ShainNO = ? ");
		sql.append("         AND M1.Code <> '01' ");
		sql.append("     GROUP BY ");
		sql.append("         LEFT(M1.GroupCode1, 2) ");

		sql.append("     UNION ALL ");

		sql.append("     SELECT ");
		sql.append("         LEFT(M1.GroupCode1, 2) AS Kbn, ");
		sql.append("         SUM(MEISAI.ChinginShinseiJikanKeisan2) AS ChinginShinseiJikanKeisan ");
		sql.append("     FROM ");
		sql.append("         CHI_CHINGINKEISANSHO_MEISAI MEISAI WITH(NOLOCK) ");
		sql.append("     LEFT OUTER JOIN MST_KUBUN M1 WITH(NOLOCK) ");
		sql.append("         ON  M1.KbnCode = '0201' ");
		sql.append("         AND M1.Code = MEISAI.ChinginShinseiKbn2 ");
		sql.append("     WHERE ");
		sql.append("         MEISAI.ChinginShinseiKbn2 <> '00' ");
		sql.append("         AND MEISAI.TaishoNenGetsudo = ? ");
		sql.append("         AND MEISAI.ShainNO = ? ");
		sql.append("         AND M1.Code <> '01' ");
		sql.append("     GROUP BY ");
		sql.append("         LEFT(M1.GroupCode1, 2) ");
		sql.append(" ) ");
		sql.append(" SELECT ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '01' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan01, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '02' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan02, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '03' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan03, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '04' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan04, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '05' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan05, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '06' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan06, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '07' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan07, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '08' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan08, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '09' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan09, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '10' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan10, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn = '11' THEN ChinginShinseiJikanKeisan END), 0) AS Jikan11, ");
		sql.append("     ISNULL(SUM(CASE WHEN Kbn IN ('01', '02', '03', '04' ,'05', '06', '07', '08', '09', '10', '11') THEN ChinginShinseiJikanKeisan END), 0) AS JikanGoukei "); 
		sql.append(" FROM ");
		sql.append("     CteJikan ");
		
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
	private boolean updateKihonRow(Connection con, String taishoYM, String taishoShainNo, String loginShainNo, String eigyoshoCode, String bushoCode) throws Exception {
		boolean result = false;
		HashMap<String, String> nissuRecord = getNissu(con, taishoYM, taishoShainNo);
		HashMap<String, String> jikanRecord = getJikan(con, taishoYM, taishoShainNo);
		
		String tokkijiko =  this.getParameter("tokkijiko");

		// チェック対象の社員情報の取得
		HashMap<String, String> mstShain = new HashMap<>();
		String nowDate	= PJActionBase.getNowDate();
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, taishoShainNo, null, null, null, null, null, null, nowDate);
		if (0 < mstShains.size()) {
			mstShain = mstShains.get(0);
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
		sql.append(" 	CHI_CHINGINKEISANSHO_KIHON ");
		sql.append(" SET ");
		sql.append(" 	TaishoNenGetsudo =			?, ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	ShainNO =					?, ");
		pstmtf.addValue("String", taishoShainNo);
		sql.append(" 	EigyoshoCode =					?, ");
		pstmtf.addValue("String", eigyoshoCode);
		sql.append(" 	BushoCode =					?, ");
		pstmtf.addValue("String", bushoCode);
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
		sql.append(" 	ShinseiJikan11 =			?, ");
		pstmtf.addValue("String", jikanRecord.get("Jikan11"));

		sql.append(" 	ShinseiKihonGokeiJikan =	?, ");
		pstmtf.addValue("String", jikanRecord.get("JikanGoukei"));

		sql.append(" 	ShinseiTanka01 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka01"));
		sql.append(" 	ShinseiTanka02 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka02"));
		sql.append(" 	ShinseiTanka03 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka03"));
		sql.append(" 	ShinseiTanka04 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka04"));
		sql.append(" 	ShinseiTanka05 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka05"));
		sql.append(" 	ShinseiTanka06 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka06"));
		sql.append(" 	ShinseiTanka07 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka07"));
		sql.append(" 	ShinseiTanka08 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka08"));
		sql.append(" 	ShinseiTanka09 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka09"));
		sql.append(" 	ShinseiTanka10 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka10"));
		sql.append(" 	ShinseiTanka11 =			?, ");
		pstmtf.addValue("String", mstShain.get("ShinseiTanka11"));

		//時給日給区分 = "01"(時給)
		if("01".equals(mstShain.get("JikyuNikkyuKbn"))) {
			sql.append(" 	ShinseiKingakuGoukei01 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan01"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka01")))).toString());
			sql.append(" 	ShinseiKingakuGoukei02 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan02"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka02")))).toString());
			sql.append(" 	ShinseiKingakuGoukei03 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan03"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka03")))).toString());
			sql.append(" 	ShinseiKingakuGoukei04 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan04"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka04")))).toString());
			sql.append(" 	ShinseiKingakuGoukei05 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan05"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka05")))).toString());
			sql.append(" 	ShinseiKingakuGoukei06 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan06"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka06")))).toString());
			sql.append(" 	ShinseiKingakuGoukei07 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan07"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka07")))).toString());
			sql.append(" 	ShinseiKingakuGoukei08 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan08"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka08")))).toString());

			sql.append(" 	ShinseiKingakuGoukei10 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan10"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka10")))).toString());
			sql.append(" 	ShinseiKingakuGoukei11 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan11"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka11")))).toString());
		}
		else {
			sql.append(" 	ShinseiKingakuGoukei01 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu01"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka01")))).toString());
			sql.append(" 	ShinseiKingakuGoukei02 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan02"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka02")))).toString());
			sql.append(" 	ShinseiKingakuGoukei03 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan03"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka03")))).toString());
			sql.append(" 	ShinseiKingakuGoukei04 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu04"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka04")))).toString());
			sql.append(" 	ShinseiKingakuGoukei05 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu05"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka05")))).toString());
			sql.append(" 	ShinseiKingakuGoukei06 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu06"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka06")))).toString());
			sql.append(" 	ShinseiKingakuGoukei07 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu07"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka07")))).toString());
			sql.append(" 	ShinseiKingakuGoukei08 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu08"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka08")))).toString());

			sql.append(" 	ShinseiKingakuGoukei10 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu10"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka10")))).toString());
			sql.append(" 	ShinseiKingakuGoukei11 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu11"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka11")))).toString());
		}
		
		//通勤費区分 = "00"(日給)
		if("00".equals(mstShain.get("TsukinHiKbn"))) {
			sql.append(" 	ShinseiKingakuGoukei09 =			?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu09"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka09")))).toString());
		}
		else {
			sql.append(" 	ShinseiKingakuGoukei09 =			?, ");
			pstmtf.addValue("String", mstShain.get("ShinseiTanka09"));
		}
		sql.append(" 	TokkiJiko =			?, ");
		pstmtf.addValue("String", tokkijiko);
		
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
	private boolean insertKihonRow(Connection con, String taishoYM, String taishoShainNo, String loginShainNo, String eigyoshoCode, String bushoCode) throws Exception {
		boolean result = false;
		HashMap<String, String> nissuRecord = getNissu(con, taishoYM, taishoShainNo);
		HashMap<String, String> jikanRecord = getJikan(con, taishoYM, taishoShainNo);
		
		String tokkijiko =  this.getParameter("txtTokkijiko");

		// チェック対象の社員情報の取得
		HashMap<String, String> mstShain = new HashMap<>();
		String nowDate	= PJActionBase.getNowDate();
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, taishoShainNo, null, null, null, null, null, null, nowDate);
		if (0 < mstShains.size()) {
			mstShain = mstShains.get(0);
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
		sql.append(" 	CHI_CHINGINKEISANSHO_KIHON ");
		sql.append(" ( ");
		sql.append(" 	TaishoNenGetsudo, ");
		sql.append(" 	ShainNO, ");
		sql.append(" 	EigyoshoCode, ");
		sql.append(" 	BushoCode, ");
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
		sql.append(" 	ShinseiJikan11, ");
		sql.append(" 	ShinseiKihonGokeiJikan, ");
		
		sql.append(" 	ShinseiTanka01, ");
		sql.append(" 	ShinseiTanka02, ");
		sql.append(" 	ShinseiTanka03, ");
		sql.append(" 	ShinseiTanka04, ");
		sql.append(" 	ShinseiTanka05, ");
		sql.append(" 	ShinseiTanka06, ");
		sql.append(" 	ShinseiTanka07, ");
		sql.append(" 	ShinseiTanka08, ");
		sql.append(" 	ShinseiTanka09, ");
		sql.append(" 	ShinseiTanka10, ");
		sql.append(" 	ShinseiTanka11, ");

		sql.append(" 	ShinseiKingakuGoukei01, ");
		sql.append(" 	ShinseiKingakuGoukei02, ");
		sql.append(" 	ShinseiKingakuGoukei03, ");
		sql.append(" 	ShinseiKingakuGoukei04, ");
		sql.append(" 	ShinseiKingakuGoukei05, ");
		sql.append(" 	ShinseiKingakuGoukei06, ");
		sql.append(" 	ShinseiKingakuGoukei07, ");
		sql.append(" 	ShinseiKingakuGoukei08, ");

		sql.append(" 	ShinseiKingakuGoukei10, ");
		sql.append(" 	ShinseiKingakuGoukei11, ");
		
		sql.append(" 	ShinseiKingakuGoukei09, ");//この申請金額合計のみ通勤費区分で分岐するため、行をずらす

		
		
		sql.append(" 	TokkiJiko, ");
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
		pstmtf.addValue("String", eigyoshoCode);
		sql.append(" 	?, ");
		pstmtf.addValue("String", bushoCode);
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
		pstmtf.addValue("String", jikanRecord.get("Jikan11"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("JikanGoukei"));

		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka01"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka02"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka03"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka04"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka05"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka06"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka07"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka08"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka09"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka10"));
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanRecord.get("ShinseiTanka11"));
		
		//時給日給区分 = "01"(時給)
		if("01".equals(mstShain.get("JikyuNikkyuKbn"))) {
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan01"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka01")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan02"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka02")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan03"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka03")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan04"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka04")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan05"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka05")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan06"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka06")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan07"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka07")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan08"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka08")))).toString());

			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan10"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka10")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan11"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka11")))).toString());
		}
		else {
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu01"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka01")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan02"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka02")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(jikanRecord.get("Jikan03"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka03")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu04"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka04")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu05"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka05")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu06"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka06")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu07"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka07")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu08"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka08")))).toString());

			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu10"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka10")))).toString());
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu11"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka11")))).toString());
		}

		//通勤費区分 = "00"(日給)
		if("00".equals(mstShain.get("TsukinHiKbn"))) {
			sql.append(" 	?, ");
			pstmtf.addValue("String", (new BigDecimal(nissuRecord.get("Nissu09"))).multiply((new BigDecimal(mstShain.get("ShinseiTanka09")))).toString());
		}
		else {
			sql.append(" 	?, ");
			pstmtf.addValue("String", mstShain.get("ShinseiTanka09"));
		}

		sql.append(" 	?, ");
		pstmtf.addValue("String", tokkijiko);
		
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
	private int getChinginkeisanshoMeisaiCount(Connection con, String taishoYM, String taishoShainNo, String taishoNengappi) throws Exception {
		int result = 0;
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
		sql.append(" FROM ");
		sql.append(" 	CHI_CHINGINKEISANSHO_MEISAI ");
		
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
			if (rset.next()){
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

		StringBuilder shusshaJiKeySb			= new StringBuilder();
		StringBuilder shusshaFunKeySb			= new StringBuilder();
		StringBuilder taishaJiKeySb				= new StringBuilder();
		StringBuilder taishaFunKeySb			= new StringBuilder();
		StringBuilder jitsudoJikanKeySb			= new StringBuilder();
		StringBuilder chinginShinseiKbn1KeySb	= new StringBuilder();
		StringBuilder jikan1KeySb				= new StringBuilder();
		StringBuilder chinginShinseiKbn2KeySb	= new StringBuilder();
		StringBuilder jikan2KeySb				= new StringBuilder();
		StringBuilder chinginShinseiKbn3KeySb	= new StringBuilder();
		StringBuilder jikan3KeySb				= new StringBuilder();

		shusshaJiKeySb			.append("numShusshaJi")				.append(String.valueOf(i));
		shusshaFunKeySb			.append("numShusshaFun")				.append(String.valueOf(i));
		taishaJiKeySb			.append("numTaishaJi")					.append(String.valueOf(i));
		taishaFunKeySb			.append("numTaishaFun")				.append(String.valueOf(i));
		jitsudoJikanKeySb		.append("numJitsudoJikan")				.append(String.valueOf(i));
		chinginShinseiKbn1KeySb	.append("selChinginShinseiKbn1")		.append(String.valueOf(i));
		jikan1KeySb				.append("numChinginShinseiJikan1")		.append(String.valueOf(i));
		chinginShinseiKbn2KeySb	.append("selChinginShinseiKbn2")		.append(String.valueOf(i));
		jikan2KeySb				.append("numChinginShinseiJikan2")		.append(String.valueOf(i));
		chinginShinseiKbn3KeySb	.append("selChinginShinseiKbn3")		.append(String.valueOf(i));
		jikan3KeySb				.append("numChinginShinseiJikan3")		.append(String.valueOf(i));
		
		String shusshaJi			= this.getParameter(shusshaJiKeySb.toString());
		String shusshaFun			= this.getParameter(shusshaFunKeySb.toString());
		String taishaJi				= this.getParameter(taishaJiKeySb.toString());
		String taishaFun			= this.getParameter(taishaFunKeySb.toString());
		String jitsudoJikan			= this.getParameter(jitsudoJikanKeySb.toString());
		String chinginShinseiKbn1	= this.getParameter(chinginShinseiKbn1KeySb.toString());
		String jikan1				= this.getParameter(jikan1KeySb.toString());
		String chinginShinseiKbn2	= this.getParameter(chinginShinseiKbn2KeySb.toString());
		String jikan2				= this.getParameter(jikan2KeySb.toString());
		String chinginShinseiKbn3	= this.getParameter(chinginShinseiKbn3KeySb.toString());
		String jikan3				= this.getParameter(jikan3KeySb.toString());

		if(isDouble(shusshaJi) == false) {shusshaJi = "";}
		if(isDouble(shusshaFun) == false) {shusshaFun = "";}
		if(isDouble(taishaJi) == false) {taishaJi = "";}
		if(isDouble(taishaFun) == false) {taishaFun = "";}

		if(StringUtils.isEmpty(jitsudoJikan) || (isDouble(jitsudoJikan) == false)) {jitsudoJikan = "0";}
		if(StringUtils.isEmpty(jikan1) || (isDouble(jikan1) == false)) {jikan1 = "0";}
		if(StringUtils.isEmpty(jikan2) || (isDouble(jikan2) == false)) {jikan2 = "0";}
		if(StringUtils.isEmpty(jikan3) || (isDouble(jikan3) == false)) {jikan3 = "0";}

		HashMap<String, String> shinseiPatternRecord = getShinseiPattern(con, taishoShainNo, chinginShinseiKbn1, chinginShinseiKbn2, chinginShinseiKbn3);
		String jikanKeisan1 = "0";
		String jikanKeisan2 = "0";
		String jikanKeisan3 = "0";
		String nissu = "0";
		String kihonJikan = "0";
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
		sql.append(" 	CHI_CHINGINKEISANSHO_MEISAI ");
		sql.append(" SET ");
		sql.append(" 	TaishoNenGetsudo			=	?, ");
		pstmtf.addValue("String", taishoYM);
		sql.append(" 	ShainNO						=	?, ");
		pstmtf.addValue("String", taishoShainNo);
		sql.append(" 	TaishoNengappi				=	?, ");
		pstmtf.addValue("String", taishoNengappi);
		sql.append(" 	YobiKbn						=	?, ");
		pstmtf.addValue("String", yobi);
		sql.append(" 	ChinginKbn				=	?, ");
		pstmtf.addValue("String", shinseiPatternRecord.get("KintaiKbn"));
		sql.append(" 	ShusshaJi					=	?, ");
		if(StringUtils.isEmpty(shusshaJi) == false) {
			shusshaJi = StringUtils.leftPad(shusshaJi, 2, '0');
		}
		pstmtf.addValue("String", shusshaJi);
		sql.append(" 	ShusshaFun					=	?, ");
		if(StringUtils.isEmpty(shusshaFun) == false) {
			shusshaFun = StringUtils.leftPad(shusshaFun, 2, '0');
		}
		pstmtf.addValue("String", shusshaFun);
		sql.append(" 	TaishaJi					=	?, ");
		if(StringUtils.isEmpty(taishaJi) == false) {
			taishaJi = StringUtils.leftPad(taishaJi, 2, '0');
		}
		pstmtf.addValue("String", taishaJi);
		sql.append(" 	TaishaFun					=	?, ");
		if(StringUtils.isEmpty(taishaFun) == false) {
			taishaFun = StringUtils.leftPad(taishaFun, 2, '0');
		}
		pstmtf.addValue("String", taishaFun);
		sql.append(" 	JitsudoJikan				=	?, ");
		pstmtf.addValue("String", jitsudoJikan);
		
		sql.append(" 	ChinginShinseiKbn1			=	?, ");
		pstmtf.addValue("String", chinginShinseiKbn1);
		sql.append(" 	ChinginShinseiJikan1		=	?, ");
		pstmtf.addValue("String", jikan1);
		sql.append(" 	ChinginShinseiJikanKeisan1	=	?, ");
		pstmtf.addValue("String", jikanKeisan1);

		sql.append(" 	ChinginShinseiKbn2			=	?, ");
		pstmtf.addValue("String", chinginShinseiKbn2);
		sql.append(" 	ChinginShinseiJikan2		=	?, ");
		pstmtf.addValue("String", jikan2);
		sql.append(" 	ChinginShinseiJikanKeisan2	=	?, ");
		pstmtf.addValue("String", jikanKeisan2);

		sql.append(" 	ChinginShinseiKbn3			=	?, ");
		pstmtf.addValue("String", chinginShinseiKbn3);
		sql.append(" 	ChinginShinseiJikan3		=	?, ");
		pstmtf.addValue("String", jikan3);
		sql.append(" 	ChinginShinseiJikanKeisan3	=	?, ");
		pstmtf.addValue("String", jikanKeisan3);

		sql.append(" 	ChinginShinseiNisuu			=	?, ");
		pstmtf.addValue("String", nissu);
		sql.append(" 	ChinginShinseiKihonJikan		=	?, ");
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

		StringBuilder shusshaJiKeySb			= new StringBuilder();
		StringBuilder shusshaFunKeySb			= new StringBuilder();
		StringBuilder taishaJiKeySb				= new StringBuilder();
		StringBuilder taishaFunKeySb			= new StringBuilder();
		StringBuilder jitsudoJikanKeySb			= new StringBuilder();
		StringBuilder chinginShinseiKbn1KeySb	= new StringBuilder();
		StringBuilder jikan1KeySb				= new StringBuilder();
		StringBuilder chinginShinseiKbn2KeySb	= new StringBuilder();
		StringBuilder jikan2KeySb				= new StringBuilder();
		StringBuilder chinginShinseiKbn3KeySb	= new StringBuilder();
		StringBuilder jikan3KeySb				= new StringBuilder();

		shusshaJiKeySb			.append("numShusshaJi")				.append(String.valueOf(i));
		shusshaFunKeySb			.append("numShusshaFun")				.append(String.valueOf(i));
		taishaJiKeySb			.append("numTaishaJi")					.append(String.valueOf(i));
		taishaFunKeySb			.append("numTaishaFun")				.append(String.valueOf(i));
		jitsudoJikanKeySb		.append("numJitsudoJikan")				.append(String.valueOf(i));
		chinginShinseiKbn1KeySb	.append("selChinginShinseiKbn1")		.append(String.valueOf(i));
		jikan1KeySb				.append("numChinginShinseiJikan1")		.append(String.valueOf(i));
		chinginShinseiKbn2KeySb	.append("selChinginShinseiKbn2")		.append(String.valueOf(i));
		jikan2KeySb				.append("numChinginShinseiJikan2")		.append(String.valueOf(i));
		chinginShinseiKbn3KeySb	.append("selChinginShinseiKbn3")		.append(String.valueOf(i));
		jikan3KeySb				.append("numChinginShinseiJikan3")		.append(String.valueOf(i));
		
		String shusshaJi			= this.getParameter(shusshaJiKeySb.toString());
		String shusshaFun			= this.getParameter(shusshaFunKeySb.toString());
		String taishaJi				= this.getParameter(taishaJiKeySb.toString());
		String taishaFun			= this.getParameter(taishaFunKeySb.toString());
		String jitsudoJikan			= this.getParameter(jitsudoJikanKeySb.toString());
		String chinginShinseiKbn1	= this.getParameter(chinginShinseiKbn1KeySb.toString());
		String jikan1				= this.getParameter(jikan1KeySb.toString());
		String chinginShinseiKbn2	= this.getParameter(chinginShinseiKbn2KeySb.toString());
		String jikan2				= this.getParameter(jikan2KeySb.toString());
		String chinginShinseiKbn3	= this.getParameter(chinginShinseiKbn3KeySb.toString());
		String jikan3				= this.getParameter(jikan3KeySb.toString());

		if(isDouble(shusshaJi) == false) {shusshaJi = "";}
		if(isDouble(shusshaFun) == false) {shusshaFun = "";}
		if(isDouble(taishaJi) == false) {taishaJi = "";}
		if(isDouble(taishaFun) == false) {taishaFun = "";}

		if(StringUtils.isEmpty(jitsudoJikan) || (isDouble(jitsudoJikan) == false)) {jitsudoJikan = "0";}
		if(StringUtils.isEmpty(jikan1) || (isDouble(jikan1) == false)) {jikan1 = "0";}
		if(StringUtils.isEmpty(jikan2) || (isDouble(jikan2) == false)) {jikan2 = "0";}
		if(StringUtils.isEmpty(jikan3) || (isDouble(jikan3) == false)) {jikan3 = "0";}

		HashMap<String, String> shinseiPatternRecord = getShinseiPattern(con, taishoShainNo, chinginShinseiKbn1, chinginShinseiKbn2, chinginShinseiKbn3);
		String jikanKeisan1 = "0";
		String jikanKeisan2 = "0";
		String jikanKeisan3 = "0";
		String nissu = "0";
		String kihonJikan = "0";
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
		sql.append(" 	CHI_CHINGINKEISANSHO_MEISAI ");
		sql.append(" ( ");
		sql.append(" 	TaishoNenGetsudo, ");
		sql.append(" 	ShainNO, ");
		sql.append(" 	TaishoNengappi, ");
		sql.append(" 	YobiKbn, ");
		sql.append(" 	ChinginKbn, ");

		sql.append(" 	ShusshaJi, ");
		sql.append(" 	ShusshaFun, ");
		sql.append(" 	TaishaJi, ");
		sql.append(" 	TaishaFun, ");
		sql.append(" 	JitsudoJikan, ");

		sql.append(" 	ChinginShinseiKbn1, ");
		sql.append(" 	ChinginShinseiJikan1, ");
		sql.append(" 	ChinginShinseiJikanKeisan1, ");

		sql.append(" 	ChinginShinseiKbn2, ");
		sql.append(" 	ChinginShinseiJikan2, ");
		sql.append(" 	ChinginShinseiJikanKeisan2, ");

		sql.append(" 	ChinginShinseiKbn3, ");
		sql.append(" 	ChinginShinseiJikan3, ");
		sql.append(" 	ChinginShinseiJikanKeisan3, ");

		sql.append(" 	ChinginShinseiNisuu, ");
		sql.append(" 	ChinginShinseiKihonJikan, ");

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
		pstmtf.addValue("String", shinseiPatternRecord.get("KintaiKbn"));

		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shusshaJi) == false) {
			shusshaJi = StringUtils.leftPad(shusshaJi, 2, '0');
		}
		pstmtf.addValue("String", shusshaJi);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(shusshaFun) == false) {
			shusshaFun = StringUtils.leftPad(shusshaFun, 2, '0');
		}
		pstmtf.addValue("String", shusshaFun);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(taishaJi) == false) {
			taishaJi = StringUtils.leftPad(taishaJi, 2, '0');
		}
		pstmtf.addValue("String", taishaJi);
		sql.append(" 	?, ");
		if(StringUtils.isEmpty(taishaFun) == false) {
			taishaFun = StringUtils.leftPad(taishaFun, 2, '0');
		}
		pstmtf.addValue("String", taishaFun);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jitsudoJikan);

		sql.append(" 	?, ");
		pstmtf.addValue("String", chinginShinseiKbn1);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikan1);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanKeisan1);

		sql.append(" 	?, ");
		pstmtf.addValue("String", chinginShinseiKbn2);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikan2);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanKeisan2);

		sql.append(" 	?, ");
		pstmtf.addValue("String", chinginShinseiKbn3);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikan3);
		sql.append(" 	?, ");
		pstmtf.addValue("String", jikanKeisan3);

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
	public HashMap<String, String> getShinseiPattern(Connection con, String taishoShainNo, String chinginShinseiKbn1, String chinginShinseiKbn2, String chinginShinseiKbn3) throws Exception {
		
		HashMap<String, String> result = new HashMap<String, String>();
				
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		

		String JikyuNikkyuKbn = "";
		// チェック対象の社員の存在確認、時給日給区分取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, taishoShainNo, null, null, null, null, null, null, null);
		//社員が存在すれば時給日給区分を取得する
		if (0 < mstShains.size()){
			JikyuNikkyuKbn = mstShains.get(0).get("JikyuNikkyuKbn"); 
		}
		
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
		sql.append(" 	SyukinboNyuryokuKbn = '02' ");
		sql.append(" AND ShinseiKbn1 = ? ");
		sql.append(" AND ShinseiKbn2 = ? ");
		sql.append(" AND ShinseiKbn3 = ? ");
		//社員が日給の場合、一部除外
		if("02".equals(JikyuNikkyuKbn)) {
			sql.append(" AND KintaiKbn NOT IN ('07', '08') ");
		}

		pstmtf.addValue("String", chinginShinseiKbn1);
		pstmtf.addValue("String", chinginShinseiKbn2);
		pstmtf.addValue("String", chinginShinseiKbn3);

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
			if (rset.next()){
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
		boolean result = false;
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String taishoYM			= this.getParameter("txtTaishoYM");
		String taishoShainNo	= this.getParameter("txtShainNO");

		// DB接続
		Connection con		= this.getConnection("kintai", req);

		//トランザクション開始
		con.setAutoCommit(false);

		//対象社員の営業所・部署コード取得
		String nowDate	= PJActionBase.getNowDate();
		String eigyoshoCode		= "";
		String bushoCode		= "";
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, taishoShainNo, null, null, null, null, null, null, nowDate);
		if (0 < mstShains.size()) {
			HashMap<String, String> mstShain = mstShains.get(0);
			eigyoshoCode = mstShain.get("EigyoshoCode");
			bushoCode = mstShain.get("BushoCode");
		}

		//出勤簿明細の削除
		result = deleteMeisaiRow(con, taishoYM, taishoShainNo);

		if(result) {
			//出勤簿基本の削除
			result = deleteKihonRow(con, taishoYM, taishoShainNo, eigyoshoCode, bushoCode);
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
		sql.append(" 	CHI_CHINGINKEISANSHO_MEISAI ");
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
	private boolean deleteKihonRow(Connection con, String taishoYM, String taishoShainNo, String eigyoshoCode, String bushoCode) throws Exception {
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
		sql.append(" AND	EigyoshoCode = ? ");
		sql.append(" AND	BushoCode = ? ");
		
		pstmtf.addValue("String", taishoYM);
		pstmtf.addValue("String", taishoShainNo);
		pstmtf.addValue("String", eigyoshoCode);
		pstmtf.addValue("String", bushoCode);

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
	
	private boolean isDouble(String str) {
		//判定処理
		boolean result = false;
		try {
			Double.parseDouble(str);
			result = true;
		}
		catch (NumberFormatException e) {
		}
		return result;
	}
	
}