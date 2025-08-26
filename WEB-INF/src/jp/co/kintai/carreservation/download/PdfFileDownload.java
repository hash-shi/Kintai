package jp.co.kintai.carreservation.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class PdfFileDownload extends DownloadBase {
	
	public PdfFileDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String textData = this.getParameter("txtData");
		
		// テンプレートファイルの場所
		// idを渡すと帳票テンプレートファイルのパスを返却してくれる。
		String templateFile = this.getTemplateFile("kinShukkinBo", req);
		// ファイル名のみ取得
		String templateFileName = this.getTemplateFileName("kinShukkinBo");
		// 拡張子
		String extension = templateFileName.substring(templateFileName.lastIndexOf('.'));
		// ファイル名から拡張子を取り除く
		templateFileName = templateFileName.replace(extension, "");
		
		// 新しいファイル名に付ける文字列
		SimpleDateFormat sdfNewFileName = new SimpleDateFormat("yyyyMMddHHmms");
		// 現在日付
		Date date = new Date();
		
		// ファイルの読み込み
		FileInputStream fis = null;
		// ワークブック
		Workbook workbook = null;
		// ファイルの出力
		FileOutputStream fos = null;
		
		ByteArrayOutputStream bos = null;
		
		// PDF変換で使用
		OfficeManager officeManager = null;
		
		try {
			
			// テンプレートファイルが存在しているか確認
			File tmp = new File(templateFile);
			if (!tmp.exists()) {
				throw new RuntimeException("Excelファイルが存在しません: " + tmp.getAbsolutePath());
			}
			System.out.println("ファイルパス: " + tmp.getAbsolutePath());
			System.out.println("ファイル存在: " + tmp.exists());
			System.out.println("ファイルサイズ: " + tmp.length());
			
			// テンプレートファイルをバイトストリームで読み込む
			fis = new FileInputStream(templateFile);
			
			// 読み込んだテンプレートをワークブックで開く
			workbook = WorkbookFactory.create(fis);
		    if (workbook == null) {
		    	throw new RuntimeException("WorkbookFactory.create() は null を返しました");
		    }
			
			// 最初のシートを複製
			workbook.cloneSheet(0);
			
			// シート名を設定
			workbook.setSheetName(1, "勤怠");
			
			// 編集するシートを指定
			Sheet outputSheet = workbook.getSheetAt(1);
			
			// 編集する行を取得
			// 行番号はゼロベース
			Row outputRow = outputSheet.getRow(0);

			// 編集する列を取得
			// 列番号はゼロベース
			Cell outputCell = outputRow.getCell(0);
			
			// セルに値を設定
			outputCell.setCellValue(textData);
			
			// シートの削除
			workbook.removeSheetAt(0);
			

//			// excel出力
//			// 編集データをbyte[]に変換
//			bos = new ByteArrayOutputStream();
//			workbook.write(bos);
//			byte[] excelBytes = bos.toByteArray();
//			
//			// データの格納
//			this.setData(excelBytes); // ここに編集中のデータをbyte[]で格納
//			
//			// 名前を付けて保存(別名にすることでテンプレートを使いまわす)
//			this.setFilename(templateFileName + "_" + sdfNewFileName.format(date) + templateFileExten);
			
			// 編集したエクセルデータをメモリに一時保存
			File tempXlsx = File.createTempFile("temp_" + templateFileName, extension);
			fos = new FileOutputStream(tempXlsx);
		    workbook.write(fos);
			
		    // OfficeManagerを作成して起動
			officeManager = LocalOfficeManager.install();
			officeManager.start();
			
			// PDFに変換
			File tempPdf = File.createTempFile("temp_", ".pdf");
			LocalConverter
			    .builder()						// 作成
			    .officeManager(officeManager)
			    .build()						// ビルド
			    .convert(tempXlsx)				// 変換元
			    .to(tempPdf)					// 変換先
			    .execute();						// 実効
			
			// PDFデータをbyte[]に変換
			byte[] pdfBytes = Files.readAllBytes(tempPdf.toPath());

			// データの格納
			this.setData(pdfBytes); // ここに編集中のデータをbyte[]で格納
			
			// 名前を付けて保存(別名にすることでテンプレートを使いまわす)
			this.setFilename(templateFileName + "_" + sdfNewFileName.format(date) + ".pdf");
			
		} catch (Exception e) {
		    System.out.println("例外発生: " + e.getClass().getName());
		    e.printStackTrace();
		} catch (Throwable t) {  // ← Exception ではなく Throwable に変更
			System.out.println("予期しない例外が発生しました: " + t.getClass().getName());
			t.printStackTrace();
		} finally {
			// 各機能の停止/解放
			if (officeManager != null) { if (officeManager.isRunning()) { officeManager.stop(); } }
			if (fos != null) { fos.close(); }
			if (bos != null) { bos.close(); }
			if (workbook != null) { workbook.close(); }
			if (fis != null) { fis.close(); }
		}
	}
}