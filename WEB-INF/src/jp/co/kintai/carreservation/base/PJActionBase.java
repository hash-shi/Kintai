package jp.co.kintai.carreservation.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ActionBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public abstract class PJActionBase extends ActionBase {
	
	static SimpleDateFormat sdfDate_yyyyMMdd = new SimpleDateFormat("yyyy/MM/dd");
	static SimpleDateFormat sdfDate_HHmmss = new SimpleDateFormat("HH:mm:ss");
	
	public PJActionBase(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	/**
	 * セッション情報のクリア
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void sessionInvalidate(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// セッション情報のクリア
		req.getSession().invalidate();
	}
	
	/**
	 * 現在日付の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static String getNowDate() throws Exception {
		// 現在日付
		Date date = new Date();
		// フォーマットの形式の文字列にして返却
		return sdfDate_yyyyMMdd.format(date);
	}
	
	/**
	 * 現在時刻の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static String getNowTime() throws Exception {
		// 現在日付
		Date date = new Date();
		// フォーマットの形式の文字列にして返却
		return sdfDate_HHmmss.format(date);
	}
	
	/**
	 * 営業所名取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getEigyoshoName(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//
		String eigyoshoName = "";
		
		// 検索条件取得
		String eigyoshoCode	= this.getParameter("eigyoshoCode");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		// 取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstEigyoshos(con, eigyoshoCode, null);
		// 送信データを減らすため不要なカラムは削って名称のみ返す。
		for (HashMap<String, String> hashMap : mstShains) {
			eigyoshoName = hashMap.get("EigyoshoName");
		}
		this.addContent("result", eigyoshoName);
	}
	
	/**
	 * 部署名取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getBushoName(HttpServletRequest req, HttpServletResponse res) throws Exception {
			
		//
		String bushoName = "";
			
		// 検索条件取得
		String bushoCode	= this.getParameter("bushoCode");
		String eigyoshoCode	= this.getParameter("eigyoshoCode");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		// 取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstBushos(con, bushoCode, null, eigyoshoCode);
		// 送信データを減らすため不要なカラムは削って名称のみ返す。
		for (HashMap<String, String> hashMap : mstShains) {
				bushoName = hashMap.get("BushoName");
		}
		this.addContent("result", bushoName);
	}
	
	/**
	 * 社員名取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getShainName(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//
		String shainName = "";
		
		// 検索条件取得
		String shainNo	= this.getParameter("shainNo");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		// 取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, null, null, null, null,  null, null);
		// 送信データを減らすため不要なカラムは削って名称のみ返す。
		for (HashMap<String, String> hashMap : mstShains) {
			shainName = hashMap.get("ShainName");
		}
		this.addContent("result", shainName);
	}
	
	/**
	 * 区分名取得
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void getKubunName(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//
		String kbnName = "";
		
		// 検索条件取得
		String kbnCode	= this.getParameter("kbnCode");
		String code		= this.getParameter("code");
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		// 取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstKubuns(con, kbnCode, code, null);
		// 送信データを減らすため不要なカラムは削って名称のみ返す。
		for (HashMap<String, String> hashMap : mstShains) {
			kbnName = hashMap.get("KbnName");
		}
		this.addContent("result", kbnName);
	}
	
	/**
	 * 営業所の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getMstEigyoshos(Connection con, String eigyoshoCode, String eigyoshoName) throws Exception {
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	E.EigyoshoCode ");
		sql.append(" 	,E.EigyoshoName ");
		sql.append(" 	,E.SaishuKoshinShainNO ");
		sql.append(" 	,U.ShainName SaishuKoshinShainName ");
		sql.append(" 	,E.SaishuKoshinDate ");
		sql.append(" 	,E.SaishuKoshinJikan ");
		sql.append(" FROM ");
		sql.append(" 	MST_EIGYOSHO E ");
		sql.append(" LEFT JOIN MST_SHAIN U ");
		sql.append(" 	ON E.SaishuKoshinShainNO = U.ShainNO ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(eigyoshoCode)) {
			sql.append(" AND CAST(E.EigyoshoCode AS int) = ? ");
			pstmtf.addValue("String", eigyoshoCode);
		}

		if (StringUtils.isNotBlank(eigyoshoName)) {
			sql.append(" AND E.EigyoshoName like ? ");
			pstmtf.addValue("String", "%" + eigyoshoName + "%");
		}
		
		sql.append(" ORDER BY ");
		sql.append("     E.EigyoshoCode ");
		
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
	 * 部署の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getMstBushos(Connection con, String bushoCode, String bushoName, String eigyoshoCode) throws Exception {
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	B.BushoCode ");
		sql.append(" 	,B.BushoName ");
		sql.append(" 	,B.BushoKbn ");
		sql.append(" 	,K0153.KbnName BushoKbnName ");
		sql.append(" 	,B.EigyoshoCode ");
		sql.append(" 	,E.EigyoshoName ");
		sql.append(" 	,B.SaishuKoshinShainNO ");
		sql.append(" 	,U.ShainName SaishuKoshinShainName ");
		sql.append(" 	,B.SaishuKoshinDate ");
		sql.append(" 	,B.SaishuKoshinJikan ");
		sql.append(" FROM ");
		sql.append(" 	MST_BUSHO B ");
		sql.append(" LEFT JOIN MST_KUBUN K0153 ");
		sql.append(" 	ON K0153.KbnCode = '0153' ");
		sql.append(" 	AND B.BushoKbn = K0153.Code ");
		sql.append(" LEFT JOIN MST_EIGYOSHO E ");
		sql.append(" 	ON B.EigyoshoCode = E.EigyoshoCode ");
		sql.append(" LEFT JOIN MST_SHAIN U ");
		sql.append(" 	ON B.SaishuKoshinShainNO = U.ShainNO ");
		
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(bushoCode)) {
			sql.append(" AND CAST(B.BushoCode AS int) = ? ");
			pstmtf.addValue("String", bushoCode);
		}
		
		if (StringUtils.isNotBlank(bushoName)) {
			sql.append(" AND B.BushoName like ? ");
			pstmtf.addValue("String", "%" + bushoName + "%");
		}
		
		if (StringUtils.isNotBlank(eigyoshoCode)) {
			sql.append(" AND CAST(B.EigyoshoCode AS int) = ? ");
			pstmtf.addValue("String", eigyoshoCode);
		}

		sql.append(" ORDER BY ");
		sql.append("     B.BushoCode ");
		
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
	 * 社員の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getMstShains(Connection con, String shainNo, String shainName,String password, String shainKbn, String userKbn, String eigyoshoCode, String bushoCode, String taisyokuDate) throws Exception {
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	S.ShainNO ");
		sql.append(" 	,S.ShainName ");
		sql.append(" 	,S.Password ");
		sql.append(" 	,S.ShainKbn ");
		sql.append(" 	,S.UserKbn ");
		sql.append(" 	,S.ShukinboKbn ");
		sql.append(" 	,S.EigyoshoCode ");
		sql.append(" 	,E.EigyoshoName ");
		sql.append(" 	,S.BushoCode ");
		sql.append(" 	,B.BushoName ");
		sql.append(" 	,B.BushoKbn ");
		sql.append(" 	,S.YukyuKyukaFuyoNissu ");
		sql.append(" 	,S.JikyuNikkyuKbn ");
		sql.append(" 	,S.KinmuKaishiJi ");
		sql.append(" 	,S.KinmuKaishiFun ");
		sql.append(" 	,S.KinmuShuryoJi ");
		sql.append(" 	,S.KinmuShuryoFun ");
		sql.append(" 	,S.KeiyakuJitsudoJikan ");
		sql.append(" 	,S.ShinseiTanka01 ");
		sql.append(" 	,S.ShinseiTanka02 ");
		sql.append(" 	,S.ShinseiTanka03 ");
		sql.append(" 	,S.ShinseiTanka04 ");
		sql.append(" 	,S.ShinseiTanka05 ");
		sql.append(" 	,S.ShinseiTanka06 ");
		sql.append(" 	,S.ShinseiTanka07 ");
		sql.append(" 	,S.ShinseiTanka08 ");
		sql.append(" 	,S.ShinseiTanka09 ");
		sql.append(" 	,S.ShinseiTanka10 ");
		sql.append(" 	,S.ShinseiTanka11 ");
		sql.append(" 	,S.TsukinHiKbn ");
		sql.append(" 	,S.TaisyokuDate ");
		sql.append(" 	,S.SaishuKoshinShainNO ");
		sql.append(" 	,U.ShainName SaishuKoshinShainName ");
		sql.append(" 	,S.SaishuKoshinDate ");
		sql.append(" 	,S.SaishuKoshinJikan ");
		sql.append(" FROM ");
		sql.append(" 	MST_SHAIN S ");
		sql.append(" LEFT JOIN MST_EIGYOSHO E ");
		sql.append(" 	ON S.EigyoshoCode = E.EigyoshoCode ");
		sql.append(" LEFT JOIN MST_BUSHO B ");
		sql.append(" 	ON S.BushoCode = B.BushoCode ");
		sql.append(" LEFT JOIN MST_SHAIN U ");
		sql.append(" 	ON S.SaishuKoshinShainNO = U.ShainNO ");
		
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		
		if (StringUtils.isNotBlank(shainNo)) {
			sql.append(" AND CAST(S.ShainNO AS int) = ? ");
			pstmtf.addValue("String", shainNo);
		}

		if (StringUtils.isNotBlank(shainName)) {
			sql.append(" AND S.ShainName LIKE ? ");
			pstmtf.addValue("String", "%" + shainName + "%");
		}
		
		if (StringUtils.isNotBlank(password)) {
			sql.append(" AND S.Password = ? ");
			pstmtf.addValue("String", password);
		}
		
		if (StringUtils.isNotBlank(shainKbn)) {
			sql.append(" AND CAST(S.ShainKbn AS int) = ? ");
			pstmtf.addValue("String", shainKbn);
		}
		
		if (StringUtils.isNotBlank(userKbn)) {
			sql.append(" AND CAST(S.UserKbn AS int) = ? ");
			pstmtf.addValue("String", userKbn);
		}
		
		if (StringUtils.isNotBlank(eigyoshoCode)) {
			sql.append(" AND CAST(S.EigyoshoCode AS int) = ? ");
			pstmtf.addValue("String", eigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(bushoCode)) {
			sql.append(" AND CAST(S.BushoCode AS int) = ? ");
			pstmtf.addValue("String", bushoCode);
		}

		if (StringUtils.isNotBlank(taisyokuDate)) {
			sql.append(" AND ( ");
			sql.append(" 	S.TaisyokuDate is null ");
			sql.append(" 	OR ");
			sql.append(" 	S.TaisyokuDate = '' ");
			sql.append(" 	OR ");
			sql.append(" 	S.TaisyokuDate >= ? ");
			sql.append(" ) ");
			pstmtf.addValue("String", taisyokuDate);
		}
		
		sql.append(" ORDER BY ");
		sql.append("     S.ShainNO ");
		
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
	 * 社員処理可能営業所の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getMstShainEigyoshos(Connection con, String shainNo) throws Exception {
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	ShainNO ");
		sql.append(" 	,EigyoshoCode ");
		sql.append(" 	,SaishuKoshinShainNO ");
		sql.append(" 	,SaishuKoshinDate ");
		sql.append(" 	,SaishuKoshinJikan ");
		sql.append(" FROM ");
		sql.append(" 	MST_SHAIN_EIGYOSHO ");
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
		
		if (StringUtils.isNotBlank(shainNo)) {
			sql.append(" AND CAST(ShainNO AS int) = ? ");
			pstmtf.addValue("String", shainNo);
		}
		
		sql.append(" ORDER BY ");
		sql.append("     ShainNO ");
		
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
	 * 区分の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getMstKubuns(Connection con, String kbnCode, String code, String kbnName) throws Exception {

		ArrayList<HashMap<String, String>> mstUserIds = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	K.KbnCode ");
		sql.append(" 	,K.Code ");
		sql.append(" 	,K.KbnName ");
		sql.append(" 	,K.KbnRyaku ");
		sql.append(" 	,K.GroupCode1 ");
		sql.append(" 	,K.GroupCode2 ");
		sql.append(" 	,K.SaishuKoshinShainNO ");
		sql.append(" 	,U.ShainName SaishuKoshinShainName ");
		sql.append(" 	,K.SaishuKoshinDate ");
		sql.append(" 	,K.SaishuKoshinJikan ");
		sql.append(" FROM ");
		sql.append(" 	MST_KUBUN K ");
		sql.append(" LEFT JOIN MST_SHAIN U ");
		sql.append(" 	ON K.SaishuKoshinShainNO = U.ShainNO ");

		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");

		if (StringUtils.isNotBlank(kbnCode)) {
			sql.append(" AND CAST(K.KbnCode AS int) = ? ");
			pstmtf.addValue("String", kbnCode);
		}
		
		if (StringUtils.isNotBlank(code)) {
			sql.append(" AND CAST(K.Code AS int) = ? ");
			pstmtf.addValue("String", code);
		}
		
		if (StringUtils.isNotBlank(kbnName)) {
			sql.append(" AND K.KbnName like ? ");
			pstmtf.addValue("String", "%" + kbnName + "%");
		}

		sql.append(" ORDER BY ");
		sql.append(" 	K.KbnCode ");
		sql.append(" 	,K.Code ");
		
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
				mstUserIds.add(record);
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		return mstUserIds;
		
	}
	
}
