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
		
		// パラメータを取得した場合は、0詰め処理を行う
		if (StringUtils.isNotBlank(fromEigyoshoCode)) {
			// 数値に変換
			int fromEigyoshoCode_ = Integer.parseInt(fromEigyoshoCode);
			// 3桁0詰めに変換
			fromEigyoshoCode = String.format("%03d", fromEigyoshoCode_);
		}
		
		if (StringUtils.isNotBlank(toEigyoshoCode)) {
			// 数値に変換
			int toEigyoshoCode_ = Integer.parseInt(toEigyoshoCode);
			// 3桁0詰めに変換
			toEigyoshoCode = String.format("%03d", toEigyoshoCode_);
		}
				
		if (StringUtils.isNotBlank(fromBushoCode)) {
			// 数値に変換
			int fromBushoCode_ = Integer.parseInt(fromBushoCode);
			// 4桁0詰めに変換
			fromBushoCode = String.format("%04d", fromBushoCode_);
		}
		
		if (StringUtils.isNotBlank(toBushoCode)) {
			// 数値に変換
			int toBushoCode_ = Integer.parseInt(toBushoCode);
			// 4桁0詰めに変換
			toBushoCode = String.format("%04d", toBushoCode_);
		}
		
		if (StringUtils.isNotBlank(fromShainNo)) {
			// 数値に変換
			int fromShainNo_ = Integer.parseInt(fromShainNo);
			// 4桁0詰めに変換
			fromShainNo = String.format("%04d", fromShainNo_);
		}
		
		if (StringUtils.isNotBlank(toShainNo)) {
			// 数値に変換
			int toShainNo_ = Integer.parseInt(toShainNo);
			// 4桁0詰めに変換
			toShainNo = String.format("%04d", toShainNo_);
		}
		
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
		sql.append(" 	,CAST( ");
		sql.append(" 		(");
		sql.append(" 			SELECT ");
		sql.append(" 				COUNT(MEISAI.KintaiKbn) ");
		sql.append(" 			FROM ");
		sql.append(" 				KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 			WHERE ");
		sql.append(" 				MEISAI.ShainNO = M.ShainNO AND ");
		sql.append(" 				CASE ");
		sql.append(" 					WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 	 				THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 					ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 				END + '/04' <= MEISAI.TaishoNenGetsudo ");
		sql.append(" 			AND ");
		sql.append(" 				MEISAI.TaishoNenGetsudo <= M.TaishoNenGetsudo ");
		sql.append(" 			AND ");
		sql.append(" 				MEISAI.KintaiKbn = '06' ");
		sql.append(" 	 	) AS DECIMAL(4,1) ");
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
		
		sql.append(" 	,CAST ( ");
		sql.append(" 		( ");
		sql.append(" 			SELECT ");
		sql.append(" 				SUM(KIHON.ShinseiNissu04 + KIHON.ShinseiNissu05 * 0.5) ");
		sql.append(" 			FROM ");
		sql.append(" 				KIN_SHUKKINBO_KIHON KIHON ");
		sql.append(" 			WHERE ");
		sql.append(" 				KIHON.ShainNO = K.ShainNO AND ");
		sql.append(" 				CASE ");
		sql.append(" 					WHEN RIGHT(K.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 					THEN CAST(CAST(LEFT(K.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 					ELSE LEFT(K.TaishoNenGetsudo, 4) ");
		sql.append(" 				END + '/04' <= KIHON.TaishoNenGetsudo AND ");
		sql.append(" 				KIHON.TaishoNenGetsudo <= K.TaishoNenGetsudo ");
		sql.append(" 	 	) ");
		sql.append(" 		+ ");
		sql.append(" 		( ");
		sql.append(" 			SELECT ");
		sql.append(" 				COUNT(MEISAI.KintaiKbn) ");
		sql.append(" 			FROM ");
		sql.append(" 				KIN_SHUKKINBO_MEISAI MEISAI ");
		sql.append(" 			WHERE ");
		sql.append(" 				MEISAI.ShainNO = M.ShainNO AND ");
		sql.append(" 				CASE ");
		sql.append(" 					WHEN RIGHT(M.TaishoNenGetsudo, 2) IN ('01', '02', '03') ");
		sql.append(" 					THEN CAST(CAST(LEFT(M.TaishoNenGetsudo, 4) AS INT) - 1 AS VARCHAR) ");
		sql.append(" 					ELSE LEFT(M.TaishoNenGetsudo, 4) ");
		sql.append(" 				END + '/04' <= MEISAI.TaishoNenGetsudo AND ");
		sql.append(" 				MEISAI.TaishoNenGetsudo <= M.TaishoNenGetsudo AND ");
		sql.append(" 				MEISAI.KintaiKbn = '06' ");
		sql.append(" 		) AS DECIMAL(4,1) ");
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
			int pageIndex = 0;
			
			for (int i = 0; i < data.size(); i++) {
				String nengetsudo	= data.get(i).get("TaishoNenGetsudo");
				String shainNo		= data.get(i).get("ShainNO");
				
				String key = nengetsudo + "_" + shainNo;
				
				// 最初のシートを作成
				if(i == 0) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(key);
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageIndex++;
				}
				// 社員Noが変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("ShainNO").equals(shainNo))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(key);
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageIndex++;
				} 
				// 対象年月が変わる場合はシートを新しく作成
				else if(!(data.get(i-1).get("TaishoNenGetsudo").equals(nengetsudo))) {
					// 新しいシートを作成
					// シート名が重複しないように別名にする
					Worksheet worksheetNew = workbook.getWorksheets().add(key);
					//最初のシートを2番目のシートに複製する
					worksheetNew.copyFrom(worksheetTmp);
					// 行数カウントをリセットする
					rowCnt = 0;
					// ページ数を数える
					pageIndex++;
				} 
				
				// 編集するワークシートを選択
				Worksheet worksheet = workbook.getWorksheets().get(key);
				
				String sakuseiDate 		= data.get(i).get("SakuseiDate");
				String eigyoshoName 	= data.get(i).get("EigyoshoName");
				String bushoName 		= data.get(i).get("BushoName");
				String kakuteiKbn 		= data.get(i).get("KakuteiKbn");
				String rodoJikanFrom	= data.get(i).get("RodoJikanFrom");
				String rodoJikanTo 		= data.get(i).get("RodoJikanTo");
				String shainName		= data.get(i).get("ShainName");
				
				String shinseiNissu01 		= data.get(i).get("ShinseiNissu01");
				String shinseiNissu03 		= data.get(i).get("ShinseiNissu03");
				String shinseiNissu04 		= data.get(i).get("ShinseiNissu04");
				String shinseiNissu07 		= data.get(i).get("ShinseiNissu07");
				String shinseiNissu06 		= data.get(i).get("ShinseiNissu06");
				String shinseiNissu08 		= data.get(i).get("ShinseiNissu08");
				String shinseiNissu09 		= data.get(i).get("ShinseiNissu09");
				String shinseiNissu10 		= data.get(i).get("ShinseiNissu10");
				String shinseiNissu11 		= data.get(i).get("ShinseiNissu11");
				String shinseiNissuGoukei 	= data.get(i).get("ShiseiNisuuGoukei");
				String shinseiJikan01 		= data.get(i).get("ShinseiJikan01");
				String shinseiJikan02 		= data.get(i).get("ShinseiJikan02");
				String shinseiJikan03 		= data.get(i).get("ShinseiJikan03");
				String shinseiJikan04 		= data.get(i).get("ShinseiJikan04");
				String shinseiJikangaiKei 	= data.get(i).get("ShinseiJikangaiKei");
				String shinseiKingaku01 	= data.get(i).get("ShinseiKingaku01");
				String shinseiKingaku02 	= data.get(i).get("ShinseiKingaku02");
				String yukyuKyukaZan		= data.get(i).get("YukyuKyukaZan");
				
				// 特定のセルを取得し値を設定
				// 新しいシートを作成した場合のみ上部と下部に値を設定
				if(i == 0 
						|| !(data.get(i-1).get("ShainNO").equals(shainNo)) 
						|| !(data.get(i-1).get("TaishoNenGetsudo").equals(nengetsudo))) {
					// 上部：基本情報を設定
					worksheet.getCellRange("A3").setText(nengetsudo);
					worksheet.getCellRange("AT3").setText(sakuseiDate);
					worksheet.getCellRange("AZ3").setText("PAGE:   " + pageIndex);
					worksheet.getCellRange("A4").setText(eigyoshoName);
					worksheet.getCellRange("J4").setText(bushoName);
					worksheet.getCellRange("AT4").setText(kakuteiKbn);
					worksheet.getCellRange("J5").setText(rodoJikanFrom + "　～　" + rodoJikanTo);
					worksheet.getCellRange("AP5").setText(shainNo);
					worksheet.getCellRange("AT5").setText(shainName);
					
					// 下部：集計内容を設定
					worksheet.getCellRange("A43").setText(shinseiNissu01);
					worksheet.getCellRange("D43").setText(shinseiNissu03);
					worksheet.getCellRange("G43").setText(shinseiNissu04);
					worksheet.getCellRange("J43").setText(shinseiNissu07);
					worksheet.getCellRange("M43").setText(shinseiNissu06);
					worksheet.getCellRange("P43").setText(shinseiNissu08);
					worksheet.getCellRange("S43").setText(shinseiNissu09);
					worksheet.getCellRange("V43").setText(shinseiNissu10);
					worksheet.getCellRange("Y43").setText(shinseiNissu11);
					worksheet.getCellRange("AB43").setText(shinseiNissuGoukei);
					worksheet.getCellRange("AE43").setText(shinseiJikan01);
					worksheet.getCellRange("AI43").setText(shinseiJikan02);
					worksheet.getCellRange("AL43").setText(shinseiJikan03);
					worksheet.getCellRange("AO43").setText(shinseiJikan04);
					worksheet.getCellRange("AR43").setText(shinseiJikangaiKei);
					worksheet.getCellRange("AV43").setText(shinseiKingaku01);
					worksheet.getCellRange("BA43").setText(shinseiKingaku02);
					worksheet.getCellRange("A46").setText(yukyuKyukaZan);
				}
				
				String month						= data.get(i).get("Month");
				String day							= data.get(i).get("Day");
				String yobiKbn						= data.get(i).get("YobiKbn");
				String shukkinYoteiKbn				= data.get(i).get("ShukkinYoteiKbn");
				String kintaiKbn					= data.get(i).get("KintaiKbn");
				String shusshaJikoku				= data.get(i).get("ShusshaJikoku");
				String taishaJikoku					= data.get(i).get("TaishaJikoku");
				String jitsudoJikan					= data.get(i).get("JitsudoJikan");
				String kintaiShinseiBiko			= data.get(i).get("KintaiShinseiBiko");
				String kintaiShinseiKbn1			= data.get(i).get("KintaiShinseiKbn1");
				String kintaiShinseiKaishiJikoku1	= data.get(i).get("KintaiShinseiKaishiJikoku1");
				String kintaiShinseiShuryoJikoku1	= data.get(i).get("KintaiShinseiShuryoJikoku1");
				String kintaiShinseiJikan1			= data.get(i).get("KintaiShinseiJikan1");
				String kintaiShinseiKbn2			= data.get(i).get("KintaiShinseiKbn2");
				String kintaiShinseiKaishiJikoku2	= data.get(i).get("KintaiShinseiKaishiJikoku2");
				String kintaiShinseiShuryoJikoku2	= data.get(i).get("KintaiShinseiShuryoJikoku2");
				String kintaiShinseiJikan2			= data.get(i).get("KintaiShinseiJikan2");
				String kintaiShinseiKbn3			= data.get(i).get("KintaiShinseiKbn3");
				String kintaiShinseiKaishiJikoku3	= data.get(i).get("KintaiShinseiKaishiJikoku3");
				String kintaiShinseiShuryoJikoku3	= data.get(i).get("KintaiShinseiShuryoJikoku3");
				String kintaiShinseiJikan3			= data.get(i).get("KintaiShinseiJikan3");
				
				// 中央部：明細部を設定
				worksheet.getCellRange("A" + (9 + rowCnt)).setText(month);
				worksheet.getCellRange("B" + (9 + rowCnt)).setText(day);
				worksheet.getCellRange("C" + (9 + rowCnt)).setText(yobiKbn);
				worksheet.getCellRange("E" + (9 + rowCnt)).setText(shukkinYoteiKbn);
				worksheet.getCellRange("G" + (9 + rowCnt)).setText(kintaiKbn);

				// 勤務時間のデータがない場合は空文字を出力	
				// 「出社時刻　～　退社時刻」の形で表示
				if(shusshaJikoku.isBlank() && taishaJikoku.isBlank()) {
					worksheet.getCellRange("J" + (9 + rowCnt)).setText("");
				} else {
					worksheet.getCellRange("J" + (9 + rowCnt)).setText(shusshaJikoku + " ～ " + taishaJikoku);
				}
				
				worksheet.getCellRange("O"  + (9 + rowCnt)).setText(formatTimeForCell(jitsudoJikan));
				worksheet.getCellRange("Q" 	+ (9 + rowCnt)).setText(kintaiShinseiBiko);
				worksheet.getCellRange("V" 	+ (9 + rowCnt)).setText(kintaiShinseiKbn1);
				worksheet.getCellRange("Y" 	+ (9 + rowCnt)).setText(kintaiShinseiKaishiJikoku1);
				worksheet.getCellRange("AB" + (9 + rowCnt)).setText(kintaiShinseiShuryoJikoku1);
				worksheet.getCellRange("AE" + (9 + rowCnt)).setText(formatTimeForCell(kintaiShinseiJikan1));
				worksheet.getCellRange("AH" + (9 + rowCnt)).setText(kintaiShinseiKbn2);
				worksheet.getCellRange("AK" + (9 + rowCnt)).setText(kintaiShinseiKaishiJikoku2);
				worksheet.getCellRange("AN" + (9 + rowCnt)).setText(kintaiShinseiShuryoJikoku2);
				worksheet.getCellRange("AE" + (9 + rowCnt)).setText(formatTimeForCell(kintaiShinseiJikan2));
				worksheet.getCellRange("AT" + (9 + rowCnt)).setText(kintaiShinseiKbn3);
				worksheet.getCellRange("AW" + (9 + rowCnt)).setText(kintaiShinseiKaishiJikoku3);
				worksheet.getCellRange("AZ" + (9 + rowCnt)).setText(kintaiShinseiShuryoJikoku3);
				worksheet.getCellRange("AE" + (9 + rowCnt)).setText(formatTimeForCell(kintaiShinseiJikan3));
				
				// 曜日区分が土の場合は青色、日の場合は赤色
				if(yobiKbn.equals("土")) {
					worksheet.getCellRange("C" + (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.blue);
				} else if(yobiKbn.equals("日")) {
					worksheet.getCellRange("C" + (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				}
				
				// 出勤予定が休・有休の場合は赤色
				if(shukkinYoteiKbn.equals("休")
						|| shukkinYoteiKbn.equals("有休")) {
					worksheet.getCellRange("E" + (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				}
				
				String green = "#006400";
				
				// 勤怠区分が有給休暇・半日休暇・休日・振替休日の場合は赤色、欠勤の場合は緑色
				if(kintaiKbn.equals("有給休暇")
						|| kintaiKbn.equals("半日有給")
						|| kintaiKbn.equals("休日")
						|| kintaiKbn.equals("振替休日")) {
					worksheet.getCellRange("G" + (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.red);
				} else if(kintaiKbn.equals("欠勤")) {
					worksheet.getCellRange("G" + (9 + rowCnt)).getCellStyle().getExcelFont().setColor(Color.decode(green));
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
			
			// templateFile配下に作成したxlsxとpdfを削除する
			Files.delete(Paths.get(createFileXlsx));
			Files.delete(Paths.get(createFilePdf));
			
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
	
	// 勤務時間・申請時間のデータがない場合は空文字を出力
	// 未入力項目に0.00が出力されることを防ぐ
	public static String formatTimeForCell(String timeStr) {
	    if (timeStr == null || timeStr.isBlank() || timeStr.equals("0.00")) {
	        return "";
	    }
	    return timeStr;
	}
	
}