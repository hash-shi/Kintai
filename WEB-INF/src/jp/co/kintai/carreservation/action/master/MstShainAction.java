package jp.co.kintai.carreservation.action.master;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.base.PJActionBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstShainAction extends PJActionBase {
	public MstShainAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 画面表示
		this.setView("success");		
		
    }
	
	/**
	 * 区分プルダウン取得
	 */
	
	public void getKubunList(HttpServletRequest req, HttpServletResponse res) throws Exception {
	    String kbnCode = this.getParameter("kbnCode");

	    Connection con = this.getConnection("kintai", req);

	    // MSTから該当区分リストを取得（仮：DAOで取得）
	    List<HashMap<String,String>> kubuns = PJActionBase.getMstKubuns(con, kbnCode, null, null);

	    // 結果返却
	    this.addContent("result", kubuns);
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
	 * 社員取得（処理可能営業所も同時に取得）
	 * 
	 * @param req
	 * @param res
	 * @throws Exception
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
	        record.put("YukyuKyukaFuyoNissu", "");
	        record.put("JikyuNikkyuKbn", "");
	        record.put("KinmuKaishiJi", "");
	        record.put("KinmuKaishiFun", "");
	        record.put("KinmuShuryoJi", "");
	        record.put("KinmuShuryoFun", "");
	        record.put("KeiyakuJitsudoJikan", "");
	        record.put("ShinseiTanka01", "");
	        record.put("ShinseiTanka02", "");
	        record.put("ShinseiTanka03", "");
	        record.put("ShinseiTanka04", "");
	        record.put("ShinseiTanka05", "");
	        record.put("ShinseiTanka06", "");
	        record.put("ShinseiTanka07", "");
	        record.put("ShinseiTanka08", "");
	        record.put("ShinseiTanka09", "");
	        record.put("ShinseiTanka10", "");
	        record.put("ShinseiTanka11", "");
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

	
}