package jp.co.kintai.carreservation.validate;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class KinShukkinBoValidate extends ValidateBase {

	public KinShukkinBoValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	/**
	 * 詳細説明
	 * 
	 * 出勤簿更新時の入力チェック
	 */
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		
		//1か月分入力項目があるので1か月分ループ
		for(int i = 0;i < 31;i++){
			StringBuilder taishoNengappiKeySb	= new StringBuilder();
			taishoNengappiKeySb	.append("TaishoNengappi")	.append(String.valueOf(i));
			String taishoNengappi		= this.getParameter(taishoNengappiKeySb.toString());
			
			if(StringUtils.isEmpty(taishoNengappi)) {
				System.out.println("データが終わったので終了");
				break;
			}

			StringBuilder shusshaJiKeySb	= new StringBuilder();
			StringBuilder shusshaFunKeySb	= new StringBuilder();
			StringBuilder taishaJiKeySb		= new StringBuilder();
			StringBuilder taishaFunKeySb	= new StringBuilder();
			StringBuilder jitsudoJikanKeySb	= new StringBuilder();
			shusshaJiKeySb		.append("ShusshaJi")	.append(String.valueOf(i));
			shusshaFunKeySb		.append("ShusshaFun")	.append(String.valueOf(i));
			taishaJiKeySb		.append("TaishaJi")		.append(String.valueOf(i));
			taishaFunKeySb		.append("TaishaFun")	.append(String.valueOf(i));
			jitsudoJikanKeySb	.append("JitsudoJikan")	.append(String.valueOf(i));
			
			String shusshaJi		= this.getParameter(shusshaJiKeySb.toString());
			String shusshaFun		= this.getParameter(shusshaFunKeySb.toString());
			String taishaJi			= this.getParameter(taishaJiKeySb.toString());
			String taishaFun		= this.getParameter(taishaFunKeySb.toString());
			String jitsudoJikan		= this.getParameter(jitsudoJikanKeySb.toString());
			
			int intShusshaJi		= 0;
			int intShusshaFun		= 0;
			int intTaishaJi			= 0;
			int intTaishaFun		= 0;
			BigDecimal dcmJitsudoJikan	= BigDecimal.ZERO;
			
			try {
				if("".equals(shusshaJi) == false){
					intShusshaJi = Integer.parseInt(shusshaJi);
				}
			} catch (Exception e) {
				this.addValidateMessage("出社（時）には数値を入力してください。");
				return false;
			}
			if(intShusshaJi > 100){
				this.addValidateMessage("出社（時）の桁数が不正です。");
				return false;
			}
			if(intShusshaJi < 0){
				this.addValidateMessage("出社（時）にはマイナスは設定できません。");
				return false;
			}
			if(intShusshaJi > 23){
				this.addValidateMessage("時間は00～23の値で入力してください。");
				return false;
			}

			try {
				if("".equals(shusshaFun) == false){
					intShusshaFun = Integer.parseInt(shusshaFun);
				}
			} catch (Exception e) {
				this.addValidateMessage("出社（分）には数値を入力してください。");
				return false;
			}
			if(intShusshaFun > 100){
				this.addValidateMessage("出社（分）の桁数が不正です。");
				return false;
			}
			if(intShusshaFun < 0){
				this.addValidateMessage("出社（分）にはマイナスは設定できません。");
				return false;
			}
			if(intShusshaFun > 59){
				this.addValidateMessage("分は00～59の値で入力してください。");
				return false;
			}

			try {
				if("".equals(taishaJi) == false){
					intTaishaJi = Integer.parseInt(taishaJi);
				}
			} catch (Exception e) {
				this.addValidateMessage("退社（時）には数値を入力してください。");
				return false;
			}
			if(intTaishaJi > 100){
				this.addValidateMessage("退社（時）の桁数が不正です。");
				return false;
			}
			if(intTaishaJi < 0){
				this.addValidateMessage("退社（時）にはマイナスは設定できません。");
				return false;
			}
			if(intTaishaJi > 23){
				this.addValidateMessage("時間は00～23の値で入力してください。");
				return false;
			}

			try {
				if("".equals(taishaFun) == false){
					intTaishaFun = Integer.parseInt(taishaFun);
				}
			} catch (Exception e) {
				this.addValidateMessage("退社（分）には数値を入力してください。");
				return false;
			}
			if(intTaishaFun > 100){
				this.addValidateMessage("退社（分）の桁数が不正です。");
				return false;
			}
			if(intTaishaFun < 0){
				this.addValidateMessage("退社（分）にはマイナスは設定できません。");
				return false;
			}
			if(intTaishaFun > 59){
				this.addValidateMessage("分は00～59の値で入力してください。");
				return false;
			}

			if(
				("".equals(shusshaJi) == false) &&
				("".equals(shusshaFun) == false) &&
				("".equals(taishaJi) == false) &&
				("".equals(taishaFun) == false) &&
				((intShusshaJi * 60 + intShusshaFun) > (intTaishaJi * 60 + intTaishaFun))
			){
				this.addValidateMessage("開始時刻が終了時刻以降になっています。");
				return false;
			}

			try {
				if("".equals(jitsudoJikan) == false){
					dcmJitsudoJikan = new BigDecimal(jitsudoJikan);
				}
			} catch (Exception e) {
				this.addValidateMessage("実働時間には数値を入力してください。");
				return false;
			}
			if(
				(dcmJitsudoJikan.precision() - dcmJitsudoJikan.scale() > 2) ||
				(dcmJitsudoJikan.scale() > 2)
			){
				this.addValidateMessage("実働時間の桁数が不正です。");
				return false;
			}
			if(dcmJitsudoJikan.compareTo(BigDecimal.ZERO) < 0){
				this.addValidateMessage("実働時間にはマイナスは設定できません。");
				return false;
			}
			
			for(int j = 1;j <= 3;j++){
				StringBuilder kaishiJiKeySb		= new StringBuilder();
				StringBuilder kaishiFunKeySb	= new StringBuilder();
				StringBuilder shuryoJiKeySb		= new StringBuilder();
				StringBuilder shuryoFunKeySb	= new StringBuilder();
				StringBuilder jikanKeySb		= new StringBuilder();
				kaishiJiKeySb	.append("KintaiShinseiKaishiJi")	.append(String.valueOf(j)).append(String.valueOf(i));
				kaishiFunKeySb	.append("KintaiShinseiKaishiFun")	.append(String.valueOf(j)).append(String.valueOf(i));
				shuryoJiKeySb	.append("KintaiShinseiShuryoJi")	.append(String.valueOf(j)).append(String.valueOf(i));
				shuryoFunKeySb	.append("KintaiShinseiShuryoFun")	.append(String.valueOf(j)).append(String.valueOf(i));
				jikanKeySb		.append("KintaiShinseiJikan")		.append(String.valueOf(j)).append(String.valueOf(i));
				
				String kaishiJi			= this.getParameter(kaishiJiKeySb.toString());
				String kaishiFun		= this.getParameter(kaishiFunKeySb.toString());
				String shuryoJi			= this.getParameter(shuryoJiKeySb.toString());
				String shuryoFun		= this.getParameter(shuryoFunKeySb.toString());
				String jikan			= this.getParameter(jikanKeySb.toString());
				
				int intKaishiJi		= 0;
				int intKaishiFun	= 0;
				int intShuryoJi		= 0;
				int intShuryoFun	= 0;
				BigDecimal dcmJikan	= BigDecimal.ZERO;
				
				try {
					if("".equals(kaishiJi) == false){
						intKaishiJi = Integer.parseInt(kaishiJi);
					}
				} catch (Exception e) {
					this.addValidateMessage("勤怠申請区分開始（時）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}
				if(intKaishiJi > 100){
					this.addValidateMessage("勤怠申請区分開始（時）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}
				if(intKaishiJi < 0){
					this.addValidateMessage("勤怠申請区分開始（時）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}
				if(intKaishiJi > 23){
					this.addValidateMessage("時間は00～23の値で入力してください。");
					return false;
				}

				try {
					if("".equals(kaishiFun) == false){
						intKaishiFun = Integer.parseInt(kaishiFun);
					}
				} catch (Exception e) {
					this.addValidateMessage("勤怠申請区分開始（分）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}
				if(intKaishiFun > 100){
					this.addValidateMessage("勤怠申請区分開始（分）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}
				if(intKaishiFun < 0){
					this.addValidateMessage("勤怠申請区分開始（分）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}
				if(intKaishiFun > 59){
					this.addValidateMessage("分は00～59の値で入力してください。");
					return false;
				}

				try {
					if("".equals(shuryoJi) == false){
						intShuryoJi = Integer.parseInt(shuryoJi);
					}
				} catch (Exception e) {
					this.addValidateMessage("勤怠申請区分終了（時）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}
				if(intShuryoJi > 100){
					this.addValidateMessage("勤怠申請区分終了（時）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}
				if(intShuryoJi < 0){
					this.addValidateMessage("勤怠申請区分終了（時）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}
				if(intShuryoJi > 23){
					this.addValidateMessage("時間は00～23の値で入力してください。");
					return false;
				}

				try {
					if("".equals(shuryoFun) == false){
						intShuryoFun = Integer.parseInt(shuryoFun);
					}
				} catch (Exception e) {
					this.addValidateMessage("勤怠申請区分終了（分）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}
				if(intShuryoFun > 100){
					this.addValidateMessage("勤怠申請区分終了（分）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}
				if(intShuryoFun < 0){
					this.addValidateMessage("勤怠申請区分終了（分）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}
				if(intShuryoFun > 59){
					this.addValidateMessage("分は00～59の値で入力してください。");
					return false;
				}

				if(
					("".equals(kaishiJi) == false) &&
					("".equals(kaishiFun) == false) &&
					("".equals(shuryoJi) == false) &&
					("".equals(shuryoFun) == false) &&
					((intKaishiJi * 60 + intKaishiFun) > (intShuryoJi * 60 + intShuryoFun))
				){
					this.addValidateMessage("開始時刻が終了時刻以降になっています。");
					return false;
				}

				try {
					if("".equals(jikan) == false){
						dcmJikan = new BigDecimal(jikan);
					}
				} catch (Exception e) {
					this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}
				if(
					(dcmJikan.precision() - dcmJikan.scale() > 2) ||
					(dcmJikan.scale() > 2)
				){
					this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}
				if(dcmJikan.compareTo(BigDecimal.ZERO) < 0){
					this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}
				
			}
			
			
			// 賃金申請書入力区分("01"固定)、勤怠区分、勤怠申請区分1、勤怠申請区分2、勤怠申請区分3の組み合わせが、申請パターンマスタ(MST_SHINSEI_PATTERN)に登録されていない場合
			StringBuilder kintaiKbnKeySb	= new StringBuilder();
			StringBuilder kintaiShinseiKbn1KeySb	= new StringBuilder();
			StringBuilder kintaiShinseiKbn2KeySb	= new StringBuilder();
			StringBuilder kintaiShinseiKbn3KeySb	= new StringBuilder();
			kintaiKbnKeySb	.append("KintaiKbn")	.append(String.valueOf(i));
			kintaiShinseiKbn1KeySb	.append("KintaiShinseiKbn1")	.append(String.valueOf(i));
			kintaiShinseiKbn2KeySb	.append("KintaiShinseiKbn2")	.append(String.valueOf(i));
			kintaiShinseiKbn3KeySb	.append("KintaiShinseiKbn3")	.append(String.valueOf(i));
			String kintaiKbn		= this.getParameter(kintaiKbnKeySb.toString());
			String kintaiShinseiKbn1		= this.getParameter(kintaiShinseiKbn1KeySb.toString());
			String kintaiShinseiKbn2		= this.getParameter(kintaiShinseiKbn2KeySb.toString());
			String kintaiShinseiKbn3		= this.getParameter(kintaiShinseiKbn3KeySb.toString());

			if(shinseiPatternCheck(kintaiKbn,kintaiShinseiKbn1,kintaiShinseiKbn2,kintaiShinseiKbn3) == false){
				this.addValidateMessage("申請区分の組み合わせが正しくありません。");
				return false;
			}

			//半角1Byte、全角2Byteで換算するため、sjisで判定する
			Charset charsetMS932 = Charset.forName("MS932");

			StringBuilder bikoKeySb	= new StringBuilder();
			bikoKeySb	.append("KintaiShinseiBiko")	.append(String.valueOf(i));
			String biko		= this.getParameter(bikoKeySb.toString());

			if(biko.getBytes(charsetMS932).length > 40){
				this.addValidateMessage("備考が40バイトを超えています。");
				return false;
			}

		}

		
		String shinseiKingaku01 = this.getParameter("txtShinseiKingaku01");
		if(StringUtils.isEmpty(shinseiKingaku01)) {
			shinseiKingaku01 = "0";
		}
		BigDecimal dcmDhinseiKingaku01 = BigDecimal.ZERO;
		try {
			dcmDhinseiKingaku01 = new BigDecimal(shinseiKingaku01);
		} catch (Exception e) {
			this.addValidateMessage("特別作業金額には数値を入力してください。");
			return false;
		}
		if(
			(dcmDhinseiKingaku01.precision() - dcmDhinseiKingaku01.scale() > 7) ||
			(dcmDhinseiKingaku01.scale() > 0)
		){
			this.addValidateMessage("特別作業金額の桁数が不正です。");
			return false;
		}
		if(dcmDhinseiKingaku01.compareTo(BigDecimal.ZERO) < 0){
			this.addValidateMessage("特別作業金額にはマイナスは設定できません。");
			return false;
		}

		String shinseiKingaku02 = this.getParameter("txtShinseiKingaku02");
		if(StringUtils.isEmpty(shinseiKingaku02)) {
			shinseiKingaku02 = "0";
		}
		BigDecimal dcmDhinseiKingaku02 = BigDecimal.ZERO;
		try {
			dcmDhinseiKingaku02 = new BigDecimal(shinseiKingaku02);
		} catch (Exception e) {
			this.addValidateMessage("営業日当手当には数値を入力してください。");
			return false;
		}
		if(
			(dcmDhinseiKingaku02.precision() - dcmDhinseiKingaku02.scale() > 7) ||
			(dcmDhinseiKingaku02.scale() > 0)
		){
			this.addValidateMessage("営業日当手当の桁数が不正です。");
			return false;
		}
		if(dcmDhinseiKingaku02.compareTo(BigDecimal.ZERO) < 0){
			this.addValidateMessage("営業日当手当にはマイナスは設定できません。");
			return false;
		}

		return true;
	}

	/**
	 * 詳細説明
	 * 
	 * 申請パターンマスタ確認
	 */
	private boolean shinseiPatternCheck(String kintaiKbn, String shinseiKbn1, String shinseiKbn2, String shinseiKbn3) throws Exception {
		
		boolean result = false;
		
		// DB接続
		Connection con		= this.getConnection("kintai", req);
		
		ArrayList<HashMap<String, String>> mstDatas = new ArrayList<>();
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	KintaiKbn, ");
		sql.append(" 	ShinseiKbn1, ");
		sql.append(" 	ShinseiKbn2, ");
		sql.append(" 	Nissuu, ");
		sql.append(" 	CASE WHEN KihonSagyoJikanKbn = '01' THEN ");
		sql.append(" 		(SELECT KintaiKihonSagyoJikan FROM MST_KANRI) ");
		sql.append(" 		ELSE 0 END AS KintaiKihonSagyoJikan, ");
		sql.append(" 	KihonSagyoJikanKbn, ");
		sql.append(" 	KyukeiJikanKbn, ");
		sql.append(" 	KaGenZanKbn1, ");
		sql.append(" 	KaGenZanKbn2 ");
		sql.append(" FROM ");
		sql.append(" 	MST_SHINSEI_PATTERN ");
		sql.append(" WHERE ");
		sql.append(" 	SyukinboNyuryokuKbn = '01' ");
		sql.append(" AND KintaiKbn = ? ");
		sql.append(" AND ShinseiKbn1 = ? ");
		sql.append(" AND ShinseiKbn2 = ? ");
//		sql.append(" AND ShinseiKbn3 = ? ");	//TODO

		pstmtf.addValue("String", kintaiKbn);
		pstmtf.addValue("String", shinseiKbn1);
		pstmtf.addValue("String", shinseiKbn2);
//		pstmtf.addValue("String", shinseiKbn3);

		try {
			// SQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			ResultSetMetaData metaData = rset.getMetaData(); 
			
			if(rset.next()){
				result = true;
			}
		}
		catch(Exception e) {
			System.out.println(String.valueOf(e));
		} finally {
			if (rset != null){ try { rset.close(); } catch (Exception exp){}}
			if (pstmt != null){ try { pstmt.close(); } catch (Exception exp){}}
		}
		
		// 結果返却
		return result;
	}
}