package jp.co.kintai.carreservation.validate;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class ShainTaisyokuValidate extends ValidateBase {

	public ShainTaisyokuValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		/**
		 * 詳細説明
		 * 
		 * 対象社員の退職日チェック
		 */
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// チェック対象の社員NO
		String shainNo					= this.getParameter("txtSearchedShainNO");
		// チェック対象の日付
		String taishoYM					= this.getParameter("txtSearchedTaishoYM");
		
		//=====================================================================
		// 処理
		//=====================================================================
		
		// チェック対象の日付がYYYY/mmフォーマットか確認
		if (taishoYM.trim().equals("")){ return true; }
		if(this.IsDate(taishoYM, "yyyy/MM") == false){ return true; }
		
		
		// チェック対象の社員の存在確認、退職日取得
		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, null, null, null, null, null, null);
		
		//社員が存在しなければ処理終了
		if (0 >= mstShains.size()){ return true; }
		
		HashMap<String, String> mstShain = mstShains.get(0);

		//社員の退職日が未登録なら処理終了
		String taisyokuDateStr = mstShain.get("TaisyokuDate");
		if(StringUtils.isBlank(taisyokuDateStr)){ return true; }
		
		//退職日、チェック日をLocalDateTimeに変換
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate taisyokuDateLD = LocalDate.parse(taisyokuDateStr, dtf);
		LocalDate taishoYMLD = LocalDate.parse(taishoYM + "/15", dtf);
		
		//退職日が15日以前の場合、その月の15日を取得
		//退職日が16日以降の場合、その月の末日を取得
		if(taisyokuDateLD.getDayOfMonth() <= 15){
			taisyokuDateLD = taisyokuDateLD.withDayOfMonth(15);
		}
		else{
			taisyokuDateLD = taisyokuDateLD.withDayOfMonth(taisyokuDateLD.lengthOfMonth());
		}
		
		//対象年月の15日　＞　退職日の場合エラー
		if(
			taishoYMLD.isAfter(taisyokuDateLD)
		){
			return false;
		}
		
		// 結果返却
		return true;
	}
}