package jp.co.kintai.carreservation.validate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class ShainAuthorityValidate extends ValidateBase {

	public ShainAuthorityValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		/**
		 * 詳細説明
		 * 
		 * 出勤簿入力、賃金計算書入力はログイン社員NOのデータも編集可能。
		 * ログイン中の社員NOが他社員NOのデータを編集可能な権限を有しているかをチェックする。
		 * 自分自身は制御可能
		 * ユーザ区分:個人   = 自分自身のみ制御可能
		 * ユーザ区分:部署   = 同じ部署なら制御可能
		 * ユーザ区分:営業所 = 同じ営業所なら制御可能 ※処理可能営業所も含む
		 * ユーザ区分:本社   = 部署区分:本社なら全ユーザ制御可能、本社以外はユーザ区分:営業所と同じ
		 */
		
		boolean result					= false;
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// ログイン社員情報
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		String shainNo = userInformation.getShainNO();
		String userKbn = userInformation.getUserKbn();
		String bushoKbn = userInformation.getBushoKbn();
		String bushoCode = userInformation.getBushoCode();
		String eigyoshoCode = userInformation.getEigyoshoCode();
		ArrayList<String> shoriKanoEigyoshoCode = userInformation.getShoriKanoEigyoshoCode();
		
		// チェック対象の社員NO
		String _shainNo					= this.getParameter("shainNo");
		
		
		//=====================================================================
		// 処理
		//=====================================================================
		
		// 現在日付の取得
		String nowDate	= PJActionBase.getNowDate();
		
		// チェック対象の社員情報の取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, _shainNo, null, null, null, null, null, null, nowDate);
		
		if (0 < mstShains.size()) {
			HashMap<String, String> mstShain = mstShains.get(0);
			
			// 自分自身は制御可能
			if (shainNo.equals(mstShain.get("ShainNO"))) {
				result = true;
			}
			
			// 他者の場合はチェックが必要
			
			// 個人
			if ("04".equals(userKbn)) {
				// 既にチェック済み
			}
			
			// 部署
			if ("03".equals(userKbn)) {
				// 同部署ならOK
				if (bushoCode.contains(mstShain.get("BushoCode"))) {
					result = true;
				}
			}
			
			// 営業所
			if ("02".equals(userKbn)) {
				// 同営業所ならOK
				// 処理可能営業所も含む
				if (eigyoshoCode.equals(mstShain.get("EigyoshoCode")) || shoriKanoEigyoshoCode.contains(mstShain.get("EigyoshoCode"))) {
					result = true;
				}
			}
			
			// 本社
			if ("01".equals(userKbn)) {
				// 部署区分が本社
				if ("00".equals(bushoKbn)) {
					result = true;
				}
				
				// 部署区分が本社以外
				if (!"00".equals(bushoKbn)) {
					// 同営業所ならOK
					// 処理可能営業所も含む
					if (eigyoshoCode.equals(mstShain.get("EigyoshoCode")) || shoriKanoEigyoshoCode.contains(mstShain.get("EigyoshoCode"))) {
						result = true;
					}
				}
			}
		}
		
		// 結果返却
		return result;
	}
}