package jp.co.kintai.carreservation.action.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstShainAction extends PJActionBase {
	public MstShainAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		// 区分プルダウン取得
		ArrayList<HashMap<String, String>> mstKubun0500 = PJActionBase.getMstKubuns(con, "0500", "", "");
		req.setAttribute("mstKubun0500", mstKubun0500);
		
		ArrayList<HashMap<String, String>> mstKubun0010 = PJActionBase.getMstKubuns(con, "0010", "", "");
		req.setAttribute("mstKubun0010", mstKubun0010);
		
		ArrayList<HashMap<String, String>> mstKubun0011 = PJActionBase.getMstKubuns(con, "0011", "", "");
		req.setAttribute("mstKubun0011", mstKubun0011);
		
		ArrayList<HashMap<String, String>> mstKubun0012 = PJActionBase.getMstKubuns(con, "0012", "", "");
		req.setAttribute("mstKubun0012", mstKubun0012);
		
		ArrayList<HashMap<String, String>> mstKubun0052 = PJActionBase.getMstKubuns(con, "0052", "", "");
		req.setAttribute("mstKubun0052", mstKubun0052);
		
		ArrayList<HashMap<String, String>> mstKubun0053 = PJActionBase.getMstKubuns(con, "0053", "", "");
		req.setAttribute("mstKubun0053", mstKubun0053);
		
		ArrayList<HashMap<String, String>> mstKubun0013 = PJActionBase.getMstKubuns(con, "0013", "", "");
		req.setAttribute("mstKubun0013", mstKubun0013);
		
		// 画面表示
		this.setView("success");		
		
    }
	
	/**
	 * 処理可能営業所取得
	 */
	public void getShoriKanoEigyoshoList(HttpServletRequest req, HttpServletResponse res) throws Exception {
	    String shainNo = req.getParameter("srhTxtShainNO"); 
	    
	    //debakku
	    System.out.println("shainNo param = [" + shainNo + "]");

	    Connection con = this.getConnection("kintai", req);

	    // 処理可能営業所一覧を取得（仮メソッド）
	    List<HashMap<String, String>> shoriKanoEigyoshos = PJActionBase.getMstShainEigyoshos(con, shainNo);

	    // データがない場合は空行を返す
	    if (shoriKanoEigyoshos == null || shoriKanoEigyoshos.isEmpty()) {
	        HashMap<String, String> empty = new HashMap<>();
	        empty.put("EigyoshoCode", "");
	        empty.put("EigyoshoName", "");
	        shoriKanoEigyoshos = new ArrayList<>();
	        shoriKanoEigyoshos.add(empty);
	    }

	    this.addContent("result", shoriKanoEigyoshos);
	}

	
	
	/**
	 * 社員情報取得処理（処理可能営業所も同時に取得）
	 * 
	 * 指定された社員NOに対応する社員情報を取得し、
	 * その社員が処理可能な営業所リストも同時に取得。
	 * 検索結果が存在しない場合は、新規モード用の空レコードを作成。
	 * 
	 * @param req HttpServletRequest オブジェクト。検索条件（社員NO）が含まれる。
	 * @param res HttpServletResponse オブジェクト。レスポンス出力に使用可能。
	 * @throws Exception DB接続やSQL実行時の例外
	 */
	public void search(HttpServletRequest req, HttpServletResponse res) throws Exception {

	    //=====================================================================
	    // パラメータ取得
	    //=====================================================================
	    String shainNo = req.getParameter("srhTxtShainNO");

	    //=====================================================================
	    // デバッグ出力
	    //=====================================================================
	    System.out.println("DEBUG: shainNo=" + shainNo);

	    //=====================================================================
	    // DB接続
	    //=====================================================================
	    Connection con = this.getConnection("kintai", req);

	    //=====================================================================
	    // 社員情報検索
	    //=====================================================================
	    String isNew = "0";
	    ArrayList<HashMap<String, String>> mstDatas = PJActionBase.getMstShains(
	        con, shainNo, null, null, null, null, null, null, null
	    );

	    // データが0件の場合は新規モード用に空レコードを作成
	    if (mstDatas.size() == 0) {
	        isNew = "1";
	        HashMap<String, String> record = new HashMap<String, String>();
	        record.put("ShainNO", shainNo);
	        record.put("ShainName", "");
	        record.put("Password", "");
	        record.put("UserKbn", "");
	        record.put("ShainKbn", "");
	        record.put("ShukinboKbn", "");
	        record.put("EigyoshoCode", "");
	        record.put("BushoCode", "");
	        record.put("YukyuKyukaFuyoNissu", "0.0");
	        record.put("JikyuNikkyuKbn", "");
	        record.put("KinmuKaishiJi", "");
	        record.put("KinmuKaishiFun", "");
	        record.put("KinmuShuryoJi", "");
	        record.put("KinmuShuryoFun", "");
	        record.put("KeiyakuJitsudoJikan", "0.00");
	        record.put("ShinseiTanka01", "0");
	        record.put("ShinseiTanka02", "0");
	        record.put("ShinseiTanka03", "0");
	        record.put("ShinseiTanka04", "0");
	        record.put("ShinseiTanka05", "0");
	        record.put("ShinseiTanka06", "0");
	        record.put("ShinseiTanka07", "0");
	        record.put("ShinseiTanka08", "0");
	        record.put("ShinseiTanka09", "0");
	        record.put("ShinseiTanka10", "0");
	        record.put("ShinseiTanka11", "0");
	        record.put("TsukinHiKbn", "");
	        record.put("TaisyokuDate", "");
	        record.put("SaishuKoshinShainNO", "");
	        record.put("SaishuKoshinShainName", "");
	        record.put("SaishuKoshinDate", "");
	        record.put("SaishuKoshinJikan", "");
	        mstDatas.add(record);
	    }

	    //=====================================================================
	    // 処理可能営業所リスト取得
	    //=====================================================================
	    List<HashMap<String, String>> shoriKanoEigyoshos = PJActionBase.getMstShainEigyoshos(con, shainNo);
	    if (shoriKanoEigyoshos == null || shoriKanoEigyoshos.isEmpty()) {
	        HashMap<String, String> empty = new HashMap<>();
	        empty.put("EigyoshoCode", "");
	        empty.put("EigyoshoName", "");
	        shoriKanoEigyoshos = new ArrayList<>();
	        shoriKanoEigyoshos.add(empty);
	    }

	    //=====================================================================
	    // レスポンスにセット
	    //=====================================================================
	    this.addContent("isNew", isNew);
	    this.addContent("mstDatas", mstDatas);
	    this.addContent("shoriKanoEigyoshos", shoriKanoEigyoshos);
	}
	
	/**
	 * 社員削除処理
	 * 
	 * 指定された社員番号に対応するデータを以下の順番で削除または更新：
	 * ① MST_SHAIN_EIGYOSHO から処理可能営業所情報を削除
	 * ② MST_SHAIN から社員情報を削除
	 * 
	 * @param req HttpServletRequest オブジェクト。削除対象の社員NO（txtShainNO）を含む。
	 * @param res HttpServletResponse オブジェクト。レスポンス出力用（未使用）。
	 * @throws Exception DB接続・SQL実行時の例外
	 */
	public void delete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 入力値チェック用の受けメソッドなので特に処理はない。
	}
	public void delete_(HttpServletRequest req, HttpServletResponse res) throws Exception {

		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String shainNo	= req.getParameter("txtShainNO");
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con = this.getConnection("kintai", req);		
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		
		//=====================================================================
		// 削除
		//=====================================================================
		try {
	        // ----------------------------
	        // ① MST_SHAIN_EIGYOSHO 削除
	        // ----------------------------
	        pstmtf.clear();
	        sql.setLength(0);
	        sql.append(" DELETE FROM MST_SHAIN_EIGYOSHO ");
	        sql.append(" WHERE ");
	        sql.append("   ShainNO = ? ");
	
	        pstmtf.addValue("String", shainNo);
	        pstmt = con.prepareStatement(sql.toString());
	        pstmtf.setPreparedStatement(pstmt);
	        pstmt.execute();
	        pstmt.close();
	
	        // ----------------------------
	        // ② MST_SHAIN 削除
	        // ----------------------------
	        pstmtf.clear();
	        sql.setLength(0);
	        sql.append(" DELETE FROM MST_SHAIN ");
	        sql.append(" WHERE ");
	        sql.append(" 	ShainNO = ? ");
	
	        pstmtf.addValue("String", shainNo);
	        pstmt = con.prepareStatement(sql.toString());
	        pstmtf.setPreparedStatement(pstmt);
	        pstmt.execute();
	        pstmt.close();
		


		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
	
	/**
	 * 社員新規登録処理
	 * 
	 * 指定された社員情報および処理可能営業所リストをDBに登録。
	 * ① MST_SHAIN に社員情報を登録
	 * ② MST_SHAIN_EIGYOSHO に処理可能営業所情報を複数登録
	 * 
	 * @param req HttpServletRequest オブジェクト。社員情報（txtShainNO など）および処理可能営業所コード（eigyoshoCode[]）を含む。
	 * @param res HttpServletResponse オブジェクト。レスポンス出力用（未使用）。
	 * @throws Exception DB接続・SQL実行時の例外
	 */
	public void insert(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	public void insert_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		
		String shainNo 				= req.getParameter("txtShainNO");
		String shainName 			= req.getParameter("txtShainName");
		String password 			= req.getParameter("txtPassword");
		String userKbn 				= req.getParameter("txtUserKbn");
		String shainKbn 			= req.getParameter("txtShainKbn");
		String shukinboKbn 			= req.getParameter("txtShukinboKbn");
		String eigyoshoCode			= req.getParameter("txtEigyoshoCode");
		String bushoCode			= req.getParameter("txtBushoCode");
		String yukyukyukafuyoNissu 	= req.getParameter("txtYukyuKyukaFuyoNissu");
		String jikyunikkyuKbn 		= req.getParameter("txtJikyuNikkyuKbn");
		String kinmukaishiJi 		= req.getParameter("txtKinmuKaishiJiKbnName");
		String kinmukaishiFun 		= req.getParameter("txtKinmuKaishiFunKbnName");
		String kinmushuryoJi 		= req.getParameter("txtKinmuShuryoJiKbnName");
		String kinmushuryoFun 		= req.getParameter("txtKinmuShuryoFunKbnName");
		String keiyakujitsudoJikan 	= req.getParameter("txtKeiyakuJitsudoJikan");
	    String[] shinseiTanka = new String[11];
		    for (int i = 1; i <= 11; i++) {
		        shinseiTanka[i-1] 	= req.getParameter("txtShinseiTanka" + (i<10?"0":"") + i);
		    }		
		String tsukinhiKbn 			= req.getParameter("txtTsukinHiKbn");
		String taisyokuDate 		= req.getParameter("txtTaisyokuDate");

	    // 処理可能営業所
	    String[] shoriKanoEigyoshoCodes = req.getParameterValues("eigyoshoCode[]");

		// ===== デフォルト値補正 =====
	    
	    // ユーザ区分 → 空なら null 
	    userKbn = (userKbn == null || userKbn.isEmpty()) ? null : userKbn;
		
		// 勤務開始・終了（時分） → 空なら空文字
		kinmukaishiJi  = (kinmukaishiJi  == null || kinmukaishiJi.isEmpty())  ? ""    : kinmukaishiJi;
		kinmukaishiFun = (kinmukaishiFun == null || kinmukaishiFun.isEmpty()) ? ""    : kinmukaishiFun;
		kinmushuryoJi  = (kinmushuryoJi  == null || kinmushuryoJi.isEmpty())  ? ""    : kinmushuryoJi;
		kinmushuryoFun = (kinmushuryoFun == null || kinmushuryoFun.isEmpty()) ? ""    : kinmushuryoFun;
	
		// 時給日給区分 → 空なら空文字
		jikyunikkyuKbn = (jikyunikkyuKbn == null || jikyunikkyuKbn.isEmpty()) ? ""    : jikyunikkyuKbn;
	
		// 契約実働時間 → 空なら 0.00
		keiyakujitsudoJikan = (keiyakujitsudoJikan == null || keiyakujitsudoJikan.isEmpty()) ? "0.00" : keiyakujitsudoJikan;
	
		// 申請単価1～11 → 空なら 0
		for (int i = 0; i < shinseiTanka.length; i++) {
		     shinseiTanka[i] = (shinseiTanka[i] == null || shinseiTanka[i].isEmpty()) ? "0" : shinseiTanka[i];
		}
	
		// 通勤費区分 → 空なら空文字
		tsukinhiKbn = (tsukinhiKbn == null || tsukinhiKbn.isEmpty()) ? "" : tsukinhiKbn;

	    
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
		
		try {
	        //=====================================================================
	        // ① MST_SHAIN 登録
	        //=====================================================================
	        sql.append(" INSERT INTO MST_SHAIN ( ");
	        sql.append(" ShainNO, ShainName, Password, UserKbn, ShainKbn, ShukinboKbn, EigyoshoCode, BushoCode, YukyuKyukaFuyoNissu, ");
	        sql.append(" JikyuNikkyuKbn, KinmuKaishiJi, KinmuKaishiFun, KinmuShuryoJi, KinmuShuryoFun, KeiyakuJitsudoJikan, ");
	        sql.append(" ShinseiTanka01, ShinseiTanka02, ShinseiTanka03, ShinseiTanka04, ShinseiTanka05, ");
	        sql.append(" ShinseiTanka06, ShinseiTanka07, ShinseiTanka08, ShinseiTanka09, ShinseiTanka10, ShinseiTanka11, ");
	        sql.append(" TsukinHiKbn, TaisyokuDate, SaishuKoshinShainNO, SaishuKoshinDate, SaishuKoshinJikan ");
	        sql.append(" ) VALUES ( ");
	        sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ");
	
			
			// パラメータ設定
		    pstmtf.addValue("String", shainNo);
		    pstmtf.addValue("String", shainName);
		    pstmtf.addValue("String", password);
		    pstmtf.addValue("String", userKbn);
		    pstmtf.addValue("String", shainKbn);
		    pstmtf.addValue("String", shukinboKbn);
		    pstmtf.addValue("String", eigyoshoCode);
		    pstmtf.addValue("String", bushoCode);
		    pstmtf.addValue("String", yukyukyukafuyoNissu);
		    pstmtf.addValue("String", jikyunikkyuKbn);
		    pstmtf.addValue("String", kinmukaishiJi);
		    pstmtf.addValue("String", kinmukaishiFun);
		    pstmtf.addValue("String", kinmushuryoJi);
		    pstmtf.addValue("String", kinmushuryoFun);
		    pstmtf.addValue("String", keiyakujitsudoJikan);
	        for (int i = 0; i < 11; i++) {
	            pstmtf.addValue("String", shinseiTanka[i]);
	        }
		    pstmtf.addValue("String", tsukinhiKbn);
		    pstmtf.addValue("String", taisyokuDate);
		    pstmtf.addValue("String", userInformation.getShainNO());
		    pstmtf.addValue("String", PJActionBase.getNowDate());
		    pstmtf.addValue("String", PJActionBase.getNowTime());
		    
			pstmt = con.prepareStatement(sql.toString());
			pstmtf.setPreparedStatement(pstmt);
			pstmt.execute();
			pstmt.close();
		
			
			//デバック
			
			System.out.println("勤務開始・終了時刻 " + kinmukaishiJi + kinmukaishiFun + kinmushuryoJi + kinmushuryoFun);
			
	        //=====================================================================
	        // ② MST_SHAIN_EIGYOSHO 登録（複数）
	        //=====================================================================
			if (shoriKanoEigyoshoCodes != null && shoriKanoEigyoshoCodes.length > 0) {

			    // PreparedStatement 1回だけ作成
			    sql.setLength(0);
			    sql.append("INSERT INTO MST_SHAIN_EIGYOSHO ");
			    sql.append("(ShainNO, EigyoshoCode, SaishuKoshinShainNO, SaishuKoshinDate, SaishuKoshinJikan) ");
			    sql.append("VALUES (?, ?, ?, ?, ?)");
			    
			    pstmt = con.prepareStatement(sql.toString());
			    
			    for (String code : shoriKanoEigyoshoCodes) {
			        if (code == null || code.trim().isEmpty()) continue;
			        
			        pstmt.setString(1, shainNo);
			        pstmt.setString(2, code);
			        pstmt.setString(3, userInformation.getShainNO());
			        pstmt.setString(4, PJActionBase.getNowDate());
			        pstmt.setString(5, PJActionBase.getNowTime());
			        
			        pstmt.addBatch(); // バッチに追加
			    }
			    
			    pstmt.executeBatch(); // まとめて実行
			    pstmt.close();
	        }
		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
	/**
	 * 社員情報更新処理
	 * MST_SHAIN の基本情報を更新し、
	 * MST_SHAIN_EIGYOSHO は差分更新（既存との差分を追加・削除）。
	 *
	 * @param req HttpServletRequest オブジェクト。フォームからの入力値を取得。
	 * @param res HttpServletResponse オブジェクト。レスポンス設定に使用。
	 * @throws Exception DB接続やSQL実行で例外が発生した場合。
	 */
	public void update(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	}
	public void update_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
	    //=====================================================================
	    // パラメータ取得
	    //=====================================================================
	    String shainNo 				= req.getParameter("txtShainNO");
	    String shainName 			= req.getParameter("txtShainName");
	    String password 			= req.getParameter("txtPassword");
	    String userKbn 				= req.getParameter("txtUserKbn");
	    String shainKbn 			= req.getParameter("txtShainKbn");
	    String shukinboKbn 			= req.getParameter("txtShukinboKbn");
	    String eigyoshoCode 		= req.getParameter("txtEigyoshoCode");
	    String bushoCode 			= req.getParameter("txtBushoCode");
	    String yukyukyukafuyoNissu 	= req.getParameter("txtYukyuKyukaFuyoNissu");
	    String jikyunikkyuKbn 		= req.getParameter("txtJikyuNikkyuKbn");
	    String kinmukaishiJi 		= req.getParameter("txtKinmuKaishiJiKbnName");
	    String kinmukaishiFun 		= req.getParameter("txtKinmuKaishiFunKbnName");
	    String kinmushuryoJi 		= req.getParameter("txtKinmuShuryoJiKbnName");
	    String kinmushuryoFun 		= req.getParameter("txtKinmuShuryoFunKbnName");
	    String keiyakujitsudoJikan 	= req.getParameter("txtKeiyakuJitsudoJikan");
	    String[] shinseiTanka = new String[11];
	    for (int i = 1; i <= 11; i++) {
	        shinseiTanka[i-1] 		= req.getParameter("txtShinseiTanka" + (i<10?"0":"") + i);
	    }
	    String tsukinhiKbn 			= req.getParameter("txtTsukinHiKbn");
	    String taisyokuDate 		= req.getParameter("txtTaisyokuDate");

	    // 処理可能営業所
	    String[] shoriKanoEigyoshoCodes = req.getParameterValues("eigyoshoCode[]");

		 // ===== デフォルト値補正 =====
	    
	    // ユーザ区分 → 空なら null 
	    userKbn = (userKbn == null || userKbn.isEmpty()) ? null : userKbn;
	
		// 勤務開始・終了（時分） → 空なら空文字
		kinmukaishiJi  = (kinmukaishiJi  == null || kinmukaishiJi.isEmpty())  ? ""    : kinmukaishiJi;
		kinmukaishiFun = (kinmukaishiFun == null || kinmukaishiFun.isEmpty()) ? ""    : kinmukaishiFun;
		kinmushuryoJi  = (kinmushuryoJi  == null || kinmushuryoJi.isEmpty())  ? ""    : kinmushuryoJi;
		kinmushuryoFun = (kinmushuryoFun == null || kinmushuryoFun.isEmpty()) ? ""    : kinmushuryoFun;
	
		// 時給日給区分 → 空なら空文字
		jikyunikkyuKbn = (jikyunikkyuKbn == null || jikyunikkyuKbn.isEmpty()) ? ""    : jikyunikkyuKbn;
	
		// 契約実働時間 → 空なら 0.00
		keiyakujitsudoJikan = (keiyakujitsudoJikan == null || keiyakujitsudoJikan.isEmpty()) ? "0.00" : keiyakujitsudoJikan;
	
		// 申請単価1～11 → 空なら 0
		for (int i = 0; i < shinseiTanka.length; i++) {
		    shinseiTanka[i] = (shinseiTanka[i] == null || shinseiTanka[i].isEmpty()) ? "0" : shinseiTanka[i];
		}
	
		// 通勤費区分 → 空なら空文字
		tsukinhiKbn = (tsukinhiKbn == null || tsukinhiKbn.isEmpty()) ? "" : tsukinhiKbn;

	    //=====================================================================
	    // ユーザー情報の取得
	    //=====================================================================
	    UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);

	    //=====================================================================
	    // DB接続
	    //=====================================================================
	    Connection con = this.getConnection("kintai", req);
	    PreparedStatement pstmt = null;
	    StringBuffer sql = new StringBuffer();
	    PreparedStatementFactory pstmtf = new PreparedStatementFactory();

	    try {
	        //=====================================================================
	        // ① MST_SHAIN 更新
	        //=====================================================================
	        pstmtf.clear();
	        sql.setLength(0);
	        sql.append(" UPDATE MST_SHAIN SET ");
	        sql.append(" ShainName = ?, Password = ?, UserKbn = ?, ShainKbn = ?, ShukinboKbn = ?, EigyoshoCode = ?, BushoCode = ?, ");
	        sql.append(" YukyuKyukaFuyoNissu = ?, JikyuNikkyuKbn = ?, KinmuKaishiJi = ?, KinmuKaishiFun = ?, KinmuShuryoJi = ?, KinmuShuryoFun = ?, ");
	        sql.append(" KeiyakuJitsudoJikan = ?, ");
	        for (int i = 1; i <= 11; i++) {
	            sql.append("ShinseiTanka" + (i<10?"0":"") + i + " = ?, ");
	        }
	        sql.append(" TsukinHiKbn = ?, TaisyokuDate = ?, ");
	        sql.append(" SaishuKoshinShainNO = ?, SaishuKoshinDate = ?, SaishuKoshinJikan = ? ");
	        sql.append(" WHERE ShainNO = ? ");

	        pstmtf.addValue("String", shainName);
	        pstmtf.addValue("String", password);
	        pstmtf.addValue("String", userKbn);
	        pstmtf.addValue("String", shainKbn);
	        pstmtf.addValue("String", shukinboKbn);
	        pstmtf.addValue("String", eigyoshoCode);
	        pstmtf.addValue("String", bushoCode);
	        pstmtf.addValue("String", yukyukyukafuyoNissu);
	        pstmtf.addValue("String", jikyunikkyuKbn);
	        pstmtf.addValue("String", kinmukaishiJi);
	        pstmtf.addValue("String", kinmukaishiFun);
	        pstmtf.addValue("String", kinmushuryoJi);
	        pstmtf.addValue("String", kinmushuryoFun);
	        pstmtf.addValue("String", keiyakujitsudoJikan);
	        for (int i = 0; i < 11; i++) {
	            pstmtf.addValue("String", shinseiTanka[i]);
	        }
	        pstmtf.addValue("String", tsukinhiKbn);
	        pstmtf.addValue("String", taisyokuDate);
	        pstmtf.addValue("String", userInformation.getShainNO());
	        pstmtf.addValue("String", PJActionBase.getNowDate());
	        pstmtf.addValue("String", PJActionBase.getNowTime());
	        pstmtf.addValue("String", shainNo);

	        pstmt = con.prepareStatement(sql.toString());
	        pstmtf.setPreparedStatement(pstmt);
	        pstmt.execute();
	        pstmt.close();

	     //=====================================================================
	     // ② MST_SHAIN_EIGYOSHO 差分更新（複数）
	     //=====================================================================
	     if (shoriKanoEigyoshoCodes != null) {
	         // 既存の営業所コードを取得
	         List<String> existingCodes = new ArrayList<>();
	         sql.setLength(0);
	         sql.append("SELECT EigyoshoCode FROM MST_SHAIN_EIGYOSHO WHERE ShainNO = ?");
	         pstmtf.clear();
	         pstmtf.addValue("String", shainNo);
	         pstmt = con.prepareStatement(sql.toString());
	         pstmtf.setPreparedStatement(pstmt);
	         ResultSet rs = pstmt.executeQuery();
	         while (rs.next()) {
	             existingCodes.add(rs.getString("EigyoshoCode"));
	         }
	         rs.close();
	         pstmt.close();

	         // 新規追加対象
	         List<String> toInsert = new ArrayList<>();
	         for (String code : shoriKanoEigyoshoCodes) {
	             if (code != null && !code.trim().isEmpty() && !existingCodes.contains(code)) {
	                 toInsert.add(code);
	             }
	         }

	         // 削除対象（既存だが送られてこなかったもの）
	         List<String> toDelete = new ArrayList<>();
	         for (String code : existingCodes) {
	             if (!Arrays.asList(shoriKanoEigyoshoCodes).contains(code)) {
	                 toDelete.add(code);
	             }
	         }

	         // 削除
	         if (!toDelete.isEmpty()) {
	        	 sql.setLength(0);
	             sql.append("DELETE FROM MST_SHAIN_EIGYOSHO WHERE ShainNO = ? AND EigyoshoCode = ?");
	             pstmt = con.prepareStatement(sql.toString());
	             
	             for (String code : toDelete) {
	                 pstmt.setString(1, shainNo);
	                 pstmt.setString(2, code);
	                 pstmt.addBatch();
	             }
	             
	             pstmt.executeBatch();
	             pstmt.close();
	         }

	         // 新規追加
	         if (!toInsert.isEmpty()) {
                 sql.setLength(0);
                 sql.append("INSERT INTO MST_SHAIN_EIGYOSHO (ShainNO, EigyoshoCode, SaishuKoshinShainNO, SaishuKoshinDate, SaishuKoshinJikan) ");
	             sql.append("VALUES (?, ?, ?, ?, ?)");
	             pstmt = con.prepareStatement(sql.toString());

	             for (String code : toInsert) {
	                 pstmt.setString(1, shainNo);
	                 pstmt.setString(2, code);
	                 pstmt.setString(3, userInformation.getShainNO());
	                 pstmt.setString(4, PJActionBase.getNowDate());
	                 pstmt.setString(5, PJActionBase.getNowTime());
	                 pstmt.addBatch();
	             }

	             pstmt.executeBatch();
	             pstmt.close();
	         }
	         
	    }

		} catch (Exception exp){
			exp.printStackTrace();
		} finally {
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
	}
	
	public void copy_(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// やりたい処理自体は登録処理と同一のため、insert_を呼び出すことで実施される。
		insert_(req, res);
	}

	
}