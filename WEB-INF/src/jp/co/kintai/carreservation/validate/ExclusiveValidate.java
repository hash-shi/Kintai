package jp.co.kintai.carreservation.validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class ExclusiveValidate extends ValidateBase {

	public ExclusiveValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		/**
		 * 詳細説明
		 * 
		 * テーブル更新の排他制御チェックを行う。
		 * 更新対象のテーブル行の最終更新時間を比較する。
		 */
		
		boolean result					= false;
		
		//=====================================================================
		// DB接続
		//=====================================================================
		Connection con	= this.getConnection("kintai", req);
		PreparedStatement pstmt			= null;
		StringBuffer sql				= new StringBuffer();
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// 対象のテーブル
		String tableName					= this.params.get("tableName").toString();
		// キー項目(カラム名)
		String keyName						= this.params.get("keyName").toString();
		String[] keyNames					= keyName.split(",");
		// キー項目(実際の値)
		String keyValue						= this.params.get("keyValue").toString();
		String[] keyValues					= keyValue.split(",");
		// 最終更新日
		String saishuKoshinDate				= req.getParameter(this.params.get("saishuKoshinDate").toString());
		// 最終更新時間
		String saishuKoshinJikan			= req.getParameter(this.params.get("saishuKoshinJikan").toString());
		
		//=====================================================================
		// 検索結果
		//=====================================================================
		// 最終更新日
		String saishuKoshinDate_			= "";
		// 最終更新時間
		String saishuKoshinJikan_			= "";
		
		//=====================================================================
		// 処理
		//=====================================================================
		
		// SQL文を生成する。
		pstmtf.clear();
		sql.setLength(0);
		sql.append(" SELECT ");
		sql.append("   SaishuKoshinDate ");
		sql.append("  ,SaishuKoshinJikan ");
		sql.append(" FROM ");
		sql.append(" " + tableName + " ");
		sql.append(" WHERE ");
		sql.append(" 1 = 1 ");
		
		for(int i = 0; i < keyNames.length; i++) {
			sql.append(" AND " + keyNames[i] + " = ? ");
			pstmtf.addValue("String", req.getParameter(keyValues[i]));
		}
		
		try {
			// パラメータ付きSQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			
			// レコード数分繰り返す
			while (rset.next()){
				saishuKoshinDate_	= StringUtils.stripToEmpty(rset.getString("SaishuKoshinDate"));
				saishuKoshinJikan_	= StringUtils.stripToEmpty(rset.getString("SaishuKoshinJikan"));
			}
			
			// 比較
			if (saishuKoshinDate.equals(saishuKoshinDate_) && saishuKoshinJikan.equalsIgnoreCase(saishuKoshinJikan_)) {
				result = true;
			}
			
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}

		// 結果返却
		return result;
	}
}