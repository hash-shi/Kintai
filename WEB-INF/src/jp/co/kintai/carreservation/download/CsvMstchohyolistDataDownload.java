package jp.co.kintai.carreservation.download;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.wakhok.tomoharu.csv.CSVLine;
import jp.co.kintai.carreservation.define.Define;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CsvMstchohyolistDataDownload extends DownloadBase {
	
	public CsvMstchohyolistDataDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		String sentaku	= req.getParameter("numSrhShorisentaku");
		ArrayList<HashMap<String, String>> data			= (ArrayList<HashMap<String, String>>)req.getSession().getAttribute(Define.SESSION_ID_CSV);
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
//		// PJActionBaseからデータ検索も可能
//		ArrayList<HashMap<String, String>> mstShains = PJActionBase.getMstShains(con, shainNo, null, null, null, null, null, null, null);
				
		// 改行コード
		String newLine = "\r\n";
		
		// CSVデータ
		StringBuffer csvString = new StringBuffer();
		
		// CSVデータヘッダ
		CSVLine csvStringTitle = new CSVLine();
		  if(sentaku.equals("01")) {
			csvStringTitle.addItem( "営業所コード");
			csvStringTitle.addItem( "営業所名");
		  } else if(sentaku.equals("02")) {
			csvStringTitle.addItem( "部署コード");
		    csvStringTitle.addItem( "部署名");
		    csvStringTitle.addItem( "部署区分");
		    csvStringTitle.addItem( "営業所コード");
		  } else if(sentaku.equals("03")) {
			csvStringTitle.addItem( "社員NO");
		    csvStringTitle.addItem( "社員名");
		    csvStringTitle.addItem( "パスワード");
		    csvStringTitle.addItem( "ユーザ区分");
			csvStringTitle.addItem( "社員区分");
			csvStringTitle.addItem( "出勤簿入力区分");
			csvStringTitle.addItem( "営業所コード");
			csvStringTitle.addItem( "部署コード");
			csvStringTitle.addItem( "有給休暇付与日数");
			csvStringTitle.addItem( "時給日給区分");
			csvStringTitle.addItem( "勤務開始時刻（時）");
			csvStringTitle.addItem( "勤務開始時刻（分）");
			csvStringTitle.addItem( "勤務終了時刻（時）");
			csvStringTitle.addItem( "勤務終了時刻（分）");
			csvStringTitle.addItem( "勤務実働時間");
			csvStringTitle.addItem( "01勤務時間単価");
			csvStringTitle.addItem( "02時間外勤務単価");
			csvStringTitle.addItem( "03深夜勤務単価");
			csvStringTitle.addItem( "04休日勤務単価");
			csvStringTitle.addItem( "05有給休暇単価");
			csvStringTitle.addItem( "06半日有給単価");
			csvStringTitle.addItem( "07控除単価");
			csvStringTitle.addItem( "08単価");
			csvStringTitle.addItem( "09通勤費単価/月給");
			csvStringTitle.addItem( "10時間外勤務単価");
			csvStringTitle.addItem( "11特別有給休暇単価");
			csvStringTitle.addItem( "通勤費精算区分");
			csvStringTitle.addItem( "退職年月日");
		  } else {
			csvStringTitle.addItem( "区分コード");
			csvStringTitle.addItem( "コード");
			csvStringTitle.addItem( "区分名称");
			csvStringTitle.addItem( "区分略称");
		    csvStringTitle.addItem( "グループコード1");
			csvStringTitle.addItem( "グループコード2");

		  }
			csvStringTitle.addItem( "最終更新社員NO");
			csvStringTitle.addItem( "最終更新日");
			csvStringTitle.addItem( "最終更新時刻");

		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		// 明細部の設定
		int count = data.size();
		for (int i = 0; i < count; i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			if(sentaku.equals("01")) {
				csvStringRecord.addItem(data.get(i).get("EigyoshoCode"));
				csvStringRecord.addItem(data.get(i).get("EigyoshoName"));
			} else if(sentaku.equals("02")) {
				csvStringRecord.addItem(data.get(i).get("BushoCode"));
				csvStringRecord.addItem(data.get(i).get("BushoName"));
				csvStringRecord.addItem(data.get(i).get("BushoKbn"));
				csvStringRecord.addItem(data.get(i).get("EigyoshoCode"));
			} else if(sentaku.equals("03")) {
				csvStringRecord.addItem(data.get(i).get( "ShainNO"));
				csvStringRecord.addItem(data.get(i).get( "ShainName"));
				csvStringRecord.addItem(data.get(i).get( "Password"));
				csvStringRecord.addItem(data.get(i).get( "UserKbn"));
				csvStringRecord.addItem(data.get(i).get( "ShainKbn"));
				csvStringRecord.addItem(data.get(i).get( "ShukinboKbn"));
				csvStringRecord.addItem(data.get(i).get( "EigyoshoCode"));
				csvStringRecord.addItem(data.get(i).get( "BushoCode"));
				csvStringRecord.addItem(data.get(i).get( "YukyuKyukaFuyoNissu"));
				csvStringRecord.addItem(data.get(i).get( "JikyuNikkyuKbn"));
				csvStringRecord.addItem(data.get(i).get( "KinmuKaishiJi"));
				csvStringRecord.addItem(data.get(i).get( "KinmuKaishiFun"));
				csvStringRecord.addItem(data.get(i).get( "KinmuShuryoJi"));
				csvStringRecord.addItem(data.get(i).get( "KinmuShuryoFun"));
				csvStringRecord.addItem(data.get(i).get( "KeiyakuJitsudoJikan"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka01"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka02"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka03"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka04"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka05"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka06"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka07"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka08"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka09"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka10"));
				csvStringRecord.addItem(data.get(i).get( "ShinseiTanka11"));
				csvStringRecord.addItem(data.get(i).get( "TsukinHiKbn"));
				csvStringRecord.addItem(data.get(i).get( "TaisyokuDate"));
			} else {
				csvStringRecord.addItem(data.get(i).get( "KbnCode"));
				csvStringRecord.addItem(data.get(i).get( "Code"));
				csvStringRecord.addItem(data.get(i).get( "KbnName"));
				csvStringRecord.addItem(data.get(i).get( "KbnRyaku"));
				csvStringRecord.addItem(data.get(i).get( "GroupCode1"));
				csvStringRecord.addItem(data.get(i).get( "GroupCode2"));
			}
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinShainNO"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinDate"));
			csvStringRecord.addItem(data.get(i).get("SaishuKoshinJikan"));
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes());
		// 名前を付けて保存
		this.setFilename("csvFileDownload.csv");
	}
	
}