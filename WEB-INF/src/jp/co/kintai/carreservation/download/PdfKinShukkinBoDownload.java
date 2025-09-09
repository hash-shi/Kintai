package jp.co.kintai.carreservation.download;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import jp.co.kintai.carreservation.define.Define;
import jp.co.kintai.carreservation.information.UserInformation;
import jp.co.tjs_net.java.framework.base.DownloadBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class PdfKinShukkinBoDownload extends DownloadBase {
	
	public PdfKinShukkinBoDownload(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		ArrayList<HashMap<String, String>> data = new ArrayList<>();
		String fromTaishoNengetsu	= req.getParameter("srhTxtTaishoNengetsuF");
		String toTaishoNengetsu		= req.getParameter("srhTxtTaishoNengetsuT");
		String fromEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeF");
		String toEigyoshoCode		= req.getParameter("srhTxtEigyoshoCodeT");
		String fromBushoCode		= req.getParameter("srhTxtBushoCodeF");
		String toBushoCode			= req.getParameter("srhTxtBushoCodeT");
		String fromShainNo			= req.getParameter("srhTxtShainNoF");
		String toShainNo			= req.getParameter("srhTxtShainNoT");
		String joken				= req.getParameter("srhSelJoken");
		String order				= req.getParameter("srhRdoOrder");
		
		// ログインユーザが処理可能な営業所コードの取得
		UserInformation userInformation = (UserInformation)req.getSession().getAttribute(Define.SESSION_ID);
		ArrayList<String> shoriKanoEigyoshoCode = userInformation.getShoriKanoEigyoshoCode();
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con		= this.getConnection("kintai", req);
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// データ取得
		//=====================================================================						
		sql.append(" SELECT ");
		sql.append(" 	LEFT(K.TaishoNenGetsudo, 4) + '年' + RIGHT(K.TaishoNenGetsudo, 2) + '月分' AS TaishoNenGetsudo ");
		sql.append(" 	,CONVERT(NVARCHAR, CURRENT_TIMESTAMP ,111) AS SakuseiDate ");
		sql.append(" 	,K0050.KbnName AS KakuteiKbn ");
		sql.append(" 	,M.ShainNO ");
		sql.append(" 	,S.ShainName ");
		sql.append(" 	,E.EigyoshoName ");
		sql.append(" 	,B.BushoName ");
		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN S.KinmuKaishiJi NOT IN ('') ");
		sql.append(" 		THEN S.KinmuKaishiJi + ':' + S.KinmuKaishiFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS RodoJikanFrom ");		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN S.KinmuShuryoJi NOT IN ('') ");		
		sql.append(" 		THEN S.KinmuShuryoJi + ':' + S.KinmuShuryoFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS RodoJikanTo ");
		
		sql.append(" 	,SUBSTRING(M.TaishoNengappi, 6, 2) AS [Month] ");
		sql.append(" 	,SUBSTRING(M.TaishoNengappi, 9, 2) AS [Day] ");
		sql.append(" 	,M.YobiKbn ");
		sql.append(" 	,COALESCE(K0051.KbnName, '') AS ShukkinYoteiKbn ");
		sql.append(" 	,COALESCE(M0100.KbnName, '') AS KintaiKbn ");
		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.ShusshaJi NOT IN ('') ");
		sql.append(" 		THEN M.ShusshaJi + ':' + M.ShusshaFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS ShusshaJikoku ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.TaishaJi NOT IN ('') ");		
		sql.append(" 		THEN M.TaishaJi + ':' + M.TaishaFun ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS TaishaJikoku ");
		sql.append(" 	 ,M.JitsudoJikan ");
		
		sql.append(" 	,COALESCE(K0101A.KbnName, '') AS KintaiShinseiKbn1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi1 NOT IN ('') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi1 + ':' + M.KintaiShinseiKaishiFun1 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku1 ");		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi1 NOT IN ('') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi1 + ':' + M.KintaiShinseiShuryoFun1 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn1 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiJikan1 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiJikan1 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn1 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiKyukeiJikan1 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan1 ");
		
		sql.append(" 	,COALESCE(K0101B.KbnName, '') AS KintaiShinseiKbn2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi2 NOT IN ('') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi2 + ':' + M.KintaiShinseiKaishiFun2 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku2 ");		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi2 NOT IN ('') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi2 + ':' + M.KintaiShinseiShuryoFun2 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn2 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiJikan2 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiJikan2 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn2 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiKyukeiJikan2 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan2 ");
		
		sql.append(" 	,COALESCE(K0101C.KbnName, '') AS KintaiShinseiKbn3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKaishiJi3 NOT IN ('') ");
		sql.append(" 		THEN M.KintaiShinseiKaishiJi3 + ':' + M.KintaiShinseiKaishiFun3 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKaishiJikoku3 ");		
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiShuryoJi3 NOT IN ('') ");		
		sql.append(" 		THEN M.KintaiShinseiShuryoJi3 + ':' + M.KintaiShinseiShuryoFun3 ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiShuryoJikoku3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn3 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiJikan3 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiJikan3 ");
		sql.append(" 	,CASE ");
		sql.append(" 		WHEN M.KintaiShinseiKbn3 NOT IN ('', '00') ");
		sql.append(" 		THEN CAST(M.KintaiShinseiKyukeiJikan3 AS VARCHAR) ");
		sql.append(" 		ELSE '' ");
		sql.append(" 	 END AS KintaiShinseiKyukeiJikan3 ");
		
		sql.append(" 	, COALESCE(M.KintaiShinseiBiko, '') AS KintaiShinseiBiko");
		
		sql.append(" 	,ShinseiNissu01 ");
		sql.append(" 	,ShinseiNissu03 ");
		sql.append(" 	,ShinseiNissu04 ");
		sql.append(" 	,ShinseiNissu07 ");
		sql.append(" 	,( ");
		sql.append(" 		SELECT ");
		sql.append(" 			COUNT(MEISAI.KintaiKbn) ");
		sql.append(" 		FROM ");
		sql.append(" 			KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 		WHERE ");
		sql.append(" 			MEISAI.ShainNO = M.ShainNO AND ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 	 			THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 			END + '/04' <= MEISAI.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.TaishoNenGetsudo <= M.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.KintaiKbn = '06' ");
		sql.append(" 	 ) AS ShinseiNissu06 ");
		sql.append(" 	,ShinseiNissu08 ");
		sql.append(" 	,ShinseiNissu09 ");
		sql.append(" 	,ShinseiNissu10 ");
		sql.append(" 	,ShinseiNissu11 + ShinseiNissu12 AS ShinseiNissu11 ");		
		sql.append(" 	,ShinseiNissu01 + ShinseiNissu03 + ShinseiNissu04 + ShinseiNissu07 + ShinseiNissu08 ");
		sql.append(" 		+ ShinseiNissu09 + ShinseiNissu10 + ShinseiNissu11 + ShinseiNissu12 AS ShiseiNisuuGoukei ");
		
		sql.append(" 	,ShinseiJikan01 ");
		sql.append(" 	,ShinseiJikan02 ");
		sql.append(" 	,ShinseiJikan03 ");
		sql.append(" 	,ShinseiJikan04 ");
		sql.append(" 	,ShinseiJikan01 + ShinseiJikan03 AS ShinseiJikangaiKei ");
		
		sql.append(" 	,ShinseiKingaku01 ");
		sql.append(" 	,ShinseiKingaku02 ");
		
		sql.append(" 	,COALESCE(Y.YukyuKyukaFuyoNissu, S.YukyuKyukaFuyoNissu) ");
		
		sql.append(" 	,( ");
		sql.append(" 		SELECT ");
		sql.append(" 			SUM(KIHON.ShinseiNissu04 + KIHON.ShinseiNissu05 * 0.5) ");
		sql.append(" 		FROM ");
		sql.append(" 			KIN_SHUKKINBO_KIHON KIHON ");
		sql.append(" 		WHERE ");
		sql.append(" 			KIHON.ShainNO = K.ShainNO AND ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(K.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 				THEN CAST(CAST(LEFT(K.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(K.TaishoNenGetsudo, 4) ");
		sql.append(" 			END + '/04' <= KIHON.TaishoNenGetsudo AND ");
		sql.append(" 			KIHON.TaishoNenGetsudo <= K.TaishoNenGetsudo ");
		sql.append(" 	 ) ");
		sql.append(" 	+ ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			COUNT(MEISAI.KintaiKbn) ");
		sql.append(" 		FROM ");
		sql.append(" 			KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 		WHERE ");
		sql.append(" 			MEISAI.ShainNO = M.ShainNO AND ");
		sql.append(" 			CASE ");
		sql.append(" 				WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 				THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 				ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 			END + '/04' <= MEISAI.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.TaishoNenGetsudo <= M.TaishoNenGetsudo AND ");
		sql.append(" 			MEISAI.KintaiKbn = '06' ");
		sql.append(" 	) AS YukyuKyukaZan ");
		
		sql.append(" FROM ");
		sql.append(" 	KIN_SHUKKINBO_MEISAI M ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_SHUKKINBO_KIHON K ");
		sql.append(" ON ");
		sql.append(" 	K.TaishoNenGetsudo = M.TaishoNenGetsudo AND ");
		sql.append(" 	K.ShainNO = M.ShainNO ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_SHAIN S ");
		sql.append(" ON ");
		sql.append(" 	S.ShainNO = M.ShainNO");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_EIGYOSHO E ");
		sql.append(" ON ");
		sql.append(" 	S.EigyoshoCode = E.EigyoshoCode ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_BUSHO B ");
		sql.append(" ON ");
		sql.append(" 	S.BushoCode = B.BushoCode ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0050 ");
		sql.append(" ON ");
		sql.append(" 	K0050.KbnCode = '0050' AND ");
		sql.append(" 	K0050.Code = K.KakuteiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0051 ");
		sql.append(" ON ");
		sql.append(" 	K0051.KbnCode = '0051' AND ");
		sql.append(" 	K0051.Code = M.ShukkinYoteiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN M0100 ");
		sql.append(" ON ");
		sql.append(" 	M0100.KbnCode = '0100' AND");
		sql.append(" 	M0100.Code = M.KintaiKbn ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0101A ");
		sql.append(" ON ");
		sql.append(" 	K0101A.KbnCode = '0101' AND ");
		sql.append(" 	K0101A.Code = M.KintaiShinseiKbn1 AND ");
		sql.append(" 	K0101A.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0101B ");
		sql.append(" ON ");
		sql.append(" 	K0101B.KbnCode = '0101' AND ");
		sql.append(" 	K0101B.Code = M.KintaiShinseiKbn2 AND ");
		sql.append(" 	K0101B.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	MST_KUBUN K0101C ");
		sql.append(" ON ");
		sql.append(" 	K0101C.KbnCode = '0101' AND ");
		sql.append(" 	K0101C.Code = M.KintaiShinseiKbn3 AND ");
		sql.append(" 	K0101C.Code <> '00' ");
		sql.append(" LEFT OUTER JOIN ");
		sql.append(" 	KIN_YUKYU_KYUKA_DAICHO Y");
		sql.append(" ON ");
		sql.append(" 	S.ShainNO = Y.ShainNO AND ");
		sql.append(" 	CASE ");
		sql.append(" 		WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 		THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 		ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 	END = Y.TaishoNendo");
		
		sql.append(" WHERE ");
		sql.append(" 	1 = 1 ");
				
		if (StringUtils.isNotBlank(fromTaishoNengetsu)) {
			sql.append(" AND K.TaishoNenGetsudo >=  ? ");
			pstmtf.addValue("String", fromTaishoNengetsu);
		}
		
		if (StringUtils.isNotBlank(toTaishoNengetsu)) {
			sql.append(" AND K.TaishoNenGetsudo <=  ? ");
			pstmtf.addValue("String", toTaishoNengetsu);
		}
		
		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
			sql.append(" AND E.EigyoshoCode >=  ? ");
			pstmtf.addValue("String", fromEigyoshoCode);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
			sql.append(" AND E.EigyoshoCode <=  ? ");
			pstmtf.addValue("String", toEigyoshoCode);
		}
		
		// 処理可能営業所コードがあるか判定
		if (0 < shoriKanoEigyoshoCode.size()) {
		   sql.append(" AND CAST(E.EigyoshoCode AS int) in ( ");
		   // 処理可能営業所コード分繰り返す
		   for (int i = 0; i < shoriKanoEigyoshoCode.size(); i++) {
		     // 最初の1回目のみ,がいらない。
		     if (i == 0) { sql.append(" ? "); } else { sql.append(" , ? "); }
		     // パラメータセット
		     pstmtf.addValue("String", shoriKanoEigyoshoCode.get(i));
		   }
		   sql.append(" ) ");
		}
		
		if (StringUtils.isNotBlank(fromBushoCode)) {
			sql.append(" AND B.BushoCode >=  ? ");
			pstmtf.addValue("String", fromBushoCode);
		}
		
		if (StringUtils.isNotBlank(toBushoCode)) {
			sql.append(" AND B.BushoCode <=  ? ");
			pstmtf.addValue("String", toBushoCode);
		}
		
		if (StringUtils.isNotBlank(fromShainNo)) {
			sql.append(" AND K.ShainNO >=  ? ");
			pstmtf.addValue("String", fromShainNo);
		}
		
		if (StringUtils.isNotBlank(toShainNo)) {
			sql.append(" AND K.ShainNO <=  ? ");
			pstmtf.addValue("String", toShainNo);
		}
		
		if (StringUtils.isNotBlank(joken)) {
			sql.append(" AND K.KakuteiKbn =  ? ");
			pstmtf.addValue("String", joken);
		}
		
		sql.append(" 	AND S.ShainKbn <> '04' ");
		
		sql.append(" ORDER BY ");
		sql.append("     K.TaishoNenGetsudo ");
		
		if ("02".equals(order)) {
			sql.append("     ,E.EigyoshoCode ");
		}
		
		sql.append("     ,K.ShainNO ");
		sql.append("     ,M.TaishoNengappi ");
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
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
				data.add(record);
			}
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		// テンプレートファイルの場所
		// idを渡すと帳票テンプレートファイルのパスを返却してくれる。
		String templateFile = this.getTemplateFile("kinShukkinBo", req);
		// パスのみ
		String templateFilePath = this.getTemplateFilePath(req);
		// ファイル名のみ
		String templateFileName = this.getTemplateFileName("kinShukkinBo");
		// 拡張子(xlsx)
		String extensionXlsx = templateFileName.substring(templateFileName.lastIndexOf('.'));
		// 拡張子(pdf)
		String extensionPdf = ".pdf";
		// ファイル名から拡張子を取り除く
		templateFileName = templateFileName.replace(extensionXlsx, "");
		
		// 新しいファイル名に付ける文字列
		SimpleDateFormat sdfNewFileName = new SimpleDateFormat("yyyyMMddHHmms");
		
		// 現在日付
		Date date = new Date();
		
		// ファイル名の作成(元のファイル名にyyyyMMddHHmms.pdf)
		// excel
		String createFileNameXlsx = templateFileName + "_" + sdfNewFileName.format(date) + extensionXlsx;
		String createFileXlsx = templateFilePath + createFileNameXlsx;
		// pdf
		String createFileNamePdf = templateFileName + "_" + sdfNewFileName.format(date) + extensionPdf;
		String createFilePdf = templateFilePath + createFileNamePdf;
		
		// ワークブック
		Workbook workbook = new Workbook();
		
		// PDF変換で使用
		OfficeManager officeManager = null;
		DocumentConverter localConverter = null;
		
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
			
			int rowCnt = 0;
			int pageCnt = 0;
			
			for (int i = 0; i < data.size(); i++) {
				
				// 最初のシートを作成
				if(i == 0) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageCnt++;
				}
				// 社員Noが変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("ShainNO").equals(data.get(i).get("ShainNO")))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageCnt++;
				} 
				// 対象年月が変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("TaishoNenGetsudo").equals(data.get(i).get("TaishoNenGetsudo")))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageCnt++;
				} 
				
				// 編集するワークシートを選択
				Worksheet worksheet = workbook.getWorksheets().get(data.get(i).get("TaishoNenGetsudo") + "_" + data.get(i).get("ShainNO"));
				
				// 特定のセルを取得し値を設定
				// 新しいシートを作成した場合のみ上部と下部に値を設定
				if(i == 0 
						|| !(data.get(i-1).get("ShainNO").equals(data.get(i).get("ShainNO"))) 
						|| !(data.get(i-1).get("TaishoNenGetsudo").equals(data.get(i).get("TaishoNenGetsudo")))) {
					// 上部：基本情報を設定
					worksheet.getCellRange("A3").setText(data.get(i).get("TaishoNenGetsudo"));
					worksheet.getCellRange("AT3").setText(data.get(i).get("SakuseiDate"));
					worksheet.getCellRange("AZ3").setText("PAGE:   " + pageCnt);
					worksheet.getCellRange("A4").setText(data.get(i).get("EigyoshoName"));
					worksheet.getCellRange("J4").setText(data.get(i).get("BushoName"));
					worksheet.getCellRange("AT4").setText(data.get(i).get("KakuteiKbn"));
					worksheet.getCellRange("J5").setText(data.get(i).get("RodoJikanFrom") + "～" + data.get(i).get("RodoJikanTo"));
					worksheet.getCellRange("AP5").setText(data.get(i).get("ShainNO"));
					worksheet.getCellRange("AT5").setText(data.get(i).get("ShainName"));
					
					// 下部：集計内容を設定
					worksheet.getCellRange("A43").setText(data.get(i).get("ShinseiNissu01"));
					worksheet.getCellRange("D43").setText(data.get(i).get("ShinseiNissu03"));
					worksheet.getCellRange("G43").setText(data.get(i).get("ShinseiNissu04"));
					worksheet.getCellRange("J43").setText(data.get(i).get("ShinseiNissu07"));
					worksheet.getCellRange("M43").setText(data.get(i).get("ShinseiNissu06"));
					worksheet.getCellRange("P43").setText(data.get(i).get("ShinseiNissu08"));
					worksheet.getCellRange("S43").setText(data.get(i).get("ShinseiNissu09"));
					worksheet.getCellRange("V43").setText(data.get(i).get("ShinseiNissu10"));
					worksheet.getCellRange("Y43").setText(data.get(i).get("ShinseiNissu11"));
					worksheet.getCellRange("AB43").setText(data.get(i).get("ShiseiNisuuGoukei"));
					worksheet.getCellRange("AE43").setText(data.get(i).get("ShinseiJikan01"));
					worksheet.getCellRange("AI43").setText(data.get(i).get("ShinseiJikan02"));
					worksheet.getCellRange("AL43").setText(data.get(i).get("ShinseiJikan03"));
					worksheet.getCellRange("AO43").setText(data.get(i).get("ShinseiJikan04"));
					worksheet.getCellRange("AR43").setText(data.get(i).get("ShinseiJikangaiKei"));
					worksheet.getCellRange("AV43").setText(data.get(i).get("ShinseiKingaku01"));
					worksheet.getCellRange("BA43").setText(data.get(i).get("ShinseiKingaku02"));
					worksheet.getCellRange("A46").setText(data.get(i).get("YukyuKyukaZan"));
				}
				
				// 中央部：明細部を設定
				worksheet.getCellRange("A" 	+ (9 + rowCnt)).setText(data.get(i).get("Month"));
				worksheet.getCellRange("B" 	+ (9 + rowCnt)).setText(data.get(i).get("Day"));
				worksheet.getCellRange("C" 	+ (9 + rowCnt)).setText(data.get(i).get("YobiKbn"));
				worksheet.getCellRange("E" 	+ (9 + rowCnt)).setText(data.get(i).get("ShukkinYoteiKbn"));
				worksheet.getCellRange("G" 	+ (9 + rowCnt)).setText(data.get(i).get("KintaiKbn"));
				worksheet.getCellRange("J" 	+ (9 + rowCnt)).setText(data.get(i).get("ShusshaJikoku") + "～" + data.get(i).get("TaishaJikoku"));
				worksheet.getCellRange("O" 	+ (9 + rowCnt)).setText(data.get(i).get("JitsudoJikan"));
				worksheet.getCellRange("Q" 	+ (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiBiko"));
				worksheet.getCellRange("V" 	+ (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiKbn1"));
				worksheet.getCellRange("Y" 	+ (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiKaishiJikoku1"));
				worksheet.getCellRange("AB" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiShuryoJikoku1"));
				worksheet.getCellRange("AE" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiJikan1"));
				worksheet.getCellRange("AH" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiKbn2"));
				worksheet.getCellRange("AK" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiKaishiJikoku2"));
				worksheet.getCellRange("AN" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiShuryoJikoku2"));
				worksheet.getCellRange("AQ" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiJikan2"));
				worksheet.getCellRange("AT" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiKbn3"));
				worksheet.getCellRange("AW" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiKaishiJikoku3"));
				worksheet.getCellRange("AZ" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiShuryoJikoku3"));
				worksheet.getCellRange("BC" + (9 + rowCnt)).setText(data.get(i).get("KintaiShinseiJikan3"));
				
				// 曜日区分が土の場合は青色、日の場合は赤色
				if(data.get(i).get("YobiKbn").equals("土")) {
					worksheet.getCellRange("C" 	+ (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.blue);
				} else if(data.get(i).get("YobiKbn").equals("日")) {
					worksheet.getCellRange("C" 	+ (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				}
				
				// 出勤予定が休・有休の場合は赤色
				if(data.get(i).get("ShukkinYoteiKbn").equals("休")) {
					worksheet.getCellRange("E" 	+ (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				} else if(data.get(i).get("ShukkinYoteiKbn").equals("有休")) {
					worksheet.getCellRange("E" 	+ (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				}
				
				String green = "#006400";
				
				// 勤怠区分が有給休暇・半日休暇・休日・振替休日の場合は赤色、欠勤の場合は緑色
				if(data.get(i).get("KintaiKbn").equals("有給休暇")
						|| data.get(i).get("KintaiKbn").equals("半日有給")
						|| data.get(i).get("KintaiKbn").equals("休日")
						|| data.get(i).get("KintaiKbn").equals("振替休日")) {
					worksheet.getCellRange("G" 	+ (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				} else if(data.get(i).get("KintaiKbn").equals("欠勤")) {
					worksheet.getCellRange("G" 	+ (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.decode(green));
				}
				
				rowCnt++;
			}
			
			// テンプレートシートを削除する。
			worksheetTmp.remove();
			
			// 保存(templateFile配下に保存される)
			workbook.saveToFile(createFileXlsx);
			
			// PDF変換元ファイル
			File inputFile = new File(createFileXlsx);
			// PDF変換先ファイル
			File outputFile = new File(createFilePdf);
			
			// PDF変換用ライブラリの準備(これの起動に10秒かかる)
			officeManager = LocalOfficeManager.make();
			localConverter = LocalConverter.make(officeManager);
			// 起動
			officeManager.start();
			// PDF変換
			localConverter.convert(inputFile).to(outputFile).execute();
			// 停止
			officeManager.stop();
			
			// PDFファイルをbyte[]に変換
			byte[] pdfBytes = Files.readAllBytes(Paths.get(createFilePdf));
			
			// データの格納
			this.setData(pdfBytes); // ここに編集中のデータをbyte[]で格納
			
			// 名前を付けて保存
			this.setFilename(createFileNamePdf);
			
//			// templateFile配下に作成したxlsxとpdfを削除する
//			Files.delete(Paths.get(createFileXlsx));
//			Files.delete(Paths.get(createFilePdf));
			
			
		} catch (Exception e) {
		    System.out.println("例外発生: " + e.getClass().getName());
		    e.printStackTrace();
		} catch (Throwable t) {  // ← Exception ではなく Throwable に変更
			System.out.println("予期しない例外が発生しました: " + t.getClass().getName());
			t.printStackTrace();
		} finally {
			// 各機能の停止/解放
			if (officeManager != null) { if (officeManager.isRunning()) { officeManager.stop(); } }
			if (workbook != null) { workbook.dispose(); }
		}
		
	}
}