package jp.co.kintai.carreservation.action.master;

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
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class MstChohyoListAction extends PJActionBase {
	public MstChohyoListAction(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 検索条件取得
		String kbnCode	= "0501";
				
		// DB接続
		Connection con		= this.getConnection("kintai", req);
				
		//=====================================================================
		// 結果返却
		//=====================================================================
		// 取得
		ArrayList<HashMap<String, String>> shoriSentaku = PJActionBase.getMstKubuns(con, kbnCode, "", "");
		System.out.println("配列: " + shoriSentaku);
		req.setAttribute("shoriSentaku", shoriSentaku);
		//this.addContent("name", kbnName);
		// 画面表示
		this.setView("success");
	}
	
	public void change(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		ResultSet rset					= null;
		
		//営業所初期値取得
		sql.append(" SELECT ");
		sql.append(" 	 MIN(EigyoshoCode) AS Saisho ");
		sql.append(" 	,MAX(EigyoshoCode) AS Saidai ");
		sql.append(" FROM");
		sql.append("   MST_EIGYOSHO ");
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
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
		
		// ドロップダウンの値を取得
        String selectedValue = req.getParameter("rdoShoriSentaku");
        // 処理選択名称を取得
        String kbnName = "";
        String kbnCode = "0501";
        ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstKubuns(con, kbnCode, selectedValue, null);
		// 送信データを減らすため不要なカラムは削って名称のみ返す。
		for (HashMap<String, String> hashMap : mstShains) {
			kbnName = hashMap.get("KbnName");
		}
		// 現在日付の取得
		String nowDate	= PJActionBase.getNowDate();
		
		//取得値の格納
		this.addContent("result",selectedValue);
		this.addContent("kbnName", kbnName);
		this.addContent("date",nowDate);
		this.addContent("eigyosho",mstDatas);
	}
	
}