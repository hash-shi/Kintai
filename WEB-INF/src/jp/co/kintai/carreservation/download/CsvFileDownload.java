package jp.co.kintai.carreservation.download;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.ac.wakhok.tomoharu.csv.CSVLine;
import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.CsvInformation;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class CsvFileDownload extends DownloadBase {
	
	public CsvFileDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// セッションに格納したパラメータを取得
		CsvInformation cavInformation	= (CsvInformation)req.getSession().getAttribute(Define.SESSION_ID_CSV);
		
		String textData = cavInformation.getTextData();
		
		// 改行コード
		String newLine = "\r\n";
		
		// CSVデータ
		StringBuffer csvString = new StringBuffer();
		
		// CSVデータヘッダ
		CSVLine csvStringTitle = new CSVLine();
		for (int i = 0; i < 10; i++) {
			csvStringTitle.addItem( i + "列目");
		}
		// データ格納
		csvString.append(csvStringTitle.getLine() + newLine);
		
		// 明細部の設定
		for (int i = 0; i < 10; i++) {
			// CSVデータ1レコード分
			CSVLine csvStringRecord = new CSVLine();
			for (int v = 0; v < 10; v++) {
				csvStringRecord.addItem(textData);
			}
			// データ格納
			csvString.append(csvStringRecord.getLine() + newLine);
		}
		
		// CSVデータの格納
		this.setData(csvString.toString().getBytes());
		// 名前を付けて保存
		this.setFilename("csvFileDownload.csv");
	}
	
}