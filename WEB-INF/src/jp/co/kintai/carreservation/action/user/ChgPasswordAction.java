package jp.co.kintai.carreservation.action.user;

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

public class ChgPasswordAction extends PJActionBase {
	public ChgPasswordAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
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
				userInformation.setSaishuKoshinShainNO(mstShain.get("SaishuKoshinShainNO"));
				userInformation.setSaishuKoshinDate(mstShain.get("SaishuKoshinDate"));
				userInformation.setSaishuKoshinJikan(mstShain.get("SaishuKoshinJikan"));
				// セッションに格納
				req.getSession().setAttribute(Define.SESSION_ID, userInformation);
				
				// 返却値
				this.addContent("password", mstShain.get("Password"));
				this.addContent("saishuKoshinDate", mstShain.get("SaishuKoshinDate"));
				this.addContent("saishuKoshinJikan", mstShain.get("SaishuKoshinJikan"));
				// 結果
				this.addContent("result", true);
			}
			
		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
}