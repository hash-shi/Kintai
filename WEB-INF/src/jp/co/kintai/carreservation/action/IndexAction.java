package jp.co.kintai.carreservation.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class IndexAction extends PJActionBase {
	public IndexAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// セッション情報のクリア
		req.getSession().invalidate();
		
		// 表示画面の制御
		String returnView				= "success";
		
		this.setView(returnView);
	}
	
	/**
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void login(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// セッション情報のクリア
		req.getSession().invalidate();
		
		// ログインIDとパスワードのチェックを行う
		// DB接続
		Connection con = this.getConnection("kintai", req);
		
		// 画面パラメータの取得
		String shainNo	= req.getParameter("txtShainNO");
		String password	= req.getParameter("txtPassword");
		
		// 現在日付の取得
		String nowDate	= PJActionBase.getNowDate();
		
		if (!"".equals(shainNo) && !"".equals(password)){
		
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

				// ユーザ情報の呼び出し
				UserInformation userInformation	= new UserInformation();
				
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
				this.addContent("result", true);
				
			} else {
				this.addContent("result", false);
				this.addContent("message", "指定されたアカウントの社員NO、またはパスワードが違っているか指定されたアカウントが存在しないため、認証できませんでした。");
			}
			
		} else {
			this.addContent("result", false);
			this.addContent("message", "指定されたアカウントの社員NO、またはパスワードが違っているか指定されたアカウントが存在しないため、認証できませんでした。");
		}
	}
}
