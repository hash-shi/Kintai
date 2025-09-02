package jp.co.kintai.carreservation.download;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.spire.xls.CellRange;
import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class PdfFileDownload extends DownloadBase {
	
	public PdfFileDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// テンプレートファイルの場所
		// idを渡すと帳票テンプレートファイルのパスを返却してくれる。
		String templateFile = this.getTemplateFile("kinShukkinBo", req);
		// パスのみ
		String templateFilePath = this.getTemplateFilePath(req);
		// ファイル名のみ
		String templateFileName = this.getTemplateFileName("kinShukkinBo");
		// 拡張子
		String extension = templateFileName.substring(templateFileName.lastIndexOf('.'));
		// ファイル名から拡張子を取り除く
		templateFileName = templateFileName.replace(extension, "");
		
		// 新しいファイル名に付ける文字列
		SimpleDateFormat sdfNewFileName = new SimpleDateFormat("yyyyMMddHHmms");
		
		// 現在日付
		Date date = new Date();
		
		// ファイル名の作成(元のファイル名にyyyyMMddHHmms.pdf)
		String createFile = templateFilePath + "\\" + templateFileName + "_" + sdfNewFileName.format(date) + ".pdf";
		String createFileName = templateFileName + "_" + sdfNewFileName.format(date) + ".pdf";
		
		// ワークブック
		Workbook workbook = new Workbook();
		
		try {
			
			// テンプレートファイルが存在しているか確認
			File tmp = new File(templateFile);
			if (!tmp.exists()) {
				throw new RuntimeException("Excelファイルが存在しません: " + tmp.getAbsolutePath());
			}
			
			// テンプレートファイルを開く
			workbook.loadFromFile(templateFile);
			
			// 最初のシートを取得
			Worksheet worksheetTmp = workbook.getWorksheets().get(0);
			// 新しいシートを作成
			Worksheet worksheetNew = workbook.getWorksheets().add("kintai");
			//最初のシートを2番目のシートに複製する
			worksheetNew.copyFrom(worksheetTmp);
			
			// 編集するワークシートを選択
			Worksheet worksheet = workbook.getWorksheets().get("kintai");
			
			// 特定のセルを取得
			CellRange cell = worksheet.getCellRange("A1");
//			// セルの値を取得
//			String text = cell.getValue();
			// セルに値を設定
			cell.setValue("勤怠システム");
			
			// テンプレートシートを削除する。
			worksheetTmp.remove();
			
			// PDFに変換して保存(templateFile配下に保存される)
			workbook.saveToFile(createFile ,FileFormat.PDF);
			
			// PDFファイルをbyte[]に変換
			byte[] pdfBytes = Files.readAllBytes(Paths.get(createFile));
			
			// データの格納
			this.setData(pdfBytes); // ここに編集中のデータをbyte[]で格納
			
			// 名前を付けて保存
			this.setFilename(createFileName);
			
			// templateFile配下に保存されたPDFファイルを削除する。
			Files.delete(Paths.get(createFile));
			
			
		} catch (Exception e) {
		    System.out.println("例外発生: " + e.getClass().getName());
		    e.printStackTrace();
		} catch (Throwable t) {  // ← Exception ではなく Throwable に変更
			System.out.println("予期しない例外が発生しました: " + t.getClass().getName());
			t.printStackTrace();
		} finally {
			// 各機能の停止/解放
			if (workbook != null) { workbook.dispose(); }
		}
	}
}