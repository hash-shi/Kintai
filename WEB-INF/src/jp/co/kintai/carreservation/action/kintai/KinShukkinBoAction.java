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
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 検索条件取得
//		String taishoYM			= this.getParameter("srhTxtEigyoshoCode");
//		String taishoShainNo	= this.getParameter("srhTxtEigyoshoName");
		String taishoYM			= "2010/08";
		String taishoShainNo	= "0001";
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		//=====================================================================
		// 結果返却
		//=====================================================================
		this.addContent("result", this.getKinShukkinBo(con, taishoYM, taishoShainNo));
	}
	
	/**
	 * 出勤簿の取得
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> getKinShukkinBo(Connection con, String taishoYM, String taishoShainNo) throws Exception {
		
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
		
		return mstDatas;
		
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