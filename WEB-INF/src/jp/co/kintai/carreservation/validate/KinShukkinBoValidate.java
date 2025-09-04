package jp.co.kintai.carreservation.validate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.database.PreparedStatementFactory;
import jp.co.tjs_net.java.framework.information.IndexInformation;
import jp.co.tjs_net.java.framework.validate.IsNumber;
import jp.co.tjs_net.java.framework.validate.IsRequired;
import jp.co.tjs_net.java.framework.validate.MaxLength;
import jp.co.tjs_net.java.framework.validate.MaxNumberLimit;
import jp.co.tjs_net.java.framework.validate.MinNumberLimit;
import jp.co.tjs_net.java.framework.validate.NumberLimit;

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
		// DB接続
		Connection con		= this.getConnection("kintai", req);

		IsNumber isNumberValidate = new IsNumber(req, res, info);
		MaxLength lengthValidate = new MaxLength(req, res, info);
		MinNumberLimit minNumberLimitValidate = new MinNumberLimit(req, res, info);
		MaxNumberLimit maxNumberLimitValidate = new MaxNumberLimit(req, res, info);
		IsRequired isRequiredValidate = new IsRequired(req, res, info);
		NumberLimit numberLimitValidate = new NumberLimit(req, res, info);
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		
		//1か月分入力項目があるので1か月分ループ
		for(int i = 0;i < 31;i++){
			StringBuilder taishoNengappiKeySb	= new StringBuilder();
			taishoNengappiKeySb	.append("TaishoNengappi")	.append(String.valueOf(i));
			String taishoNengappi		= this.getParameter(taishoNengappiKeySb.toString());
			
			if(StringUtils.isEmpty(taishoNengappi)) {
				//データが終わったので終了
				break;
			}

			//各日ごとに入力チェックを呼び出す
			StringBuilder shukkinYoteiKbnKeySb	= new StringBuilder();
			shukkinYoteiKbnKeySb	.append("ShukkinYoteiKbn")	.append(String.valueOf(i));
			String shukkinYoteiKbn		= this.getParameter(shukkinYoteiKbnKeySb.toString());

			StringBuilder kintaiKbnKeySb	= new StringBuilder();
			kintaiKbnKeySb	.append("KintaiKbn")	.append(String.valueOf(i));
			String kintaiKbn		= this.getParameter(kintaiKbnKeySb.toString());
			
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
			

			if((StringUtils.isEmpty(kintaiKbn) || "00".equals(kintaiKbn)) == false) {
				//勤怠区分が空でない場合のみ、出勤予定区分の未入力チェックを行う
				if(StringUtils.isEmpty(shukkinYoteiKbn) || "00".equals(shukkinYoteiKbn)) {
					this.addValidateMessage("出勤予定区分が入力されていません。");
					return false;
				}
			}

			double wkJitsudoJikan = 0;
			try {
				wkJitsudoJikan = Double.parseDouble(jitsudoJikan);
			}
			catch(Exception e) {}
			if(
					("".equals(shusshaJi) == false) ||
					("".equals(shusshaFun) == false) ||
					("".equals(taishaJi) == false) ||
					("".equals(taishaFun) == false) ||
					(("".equals(jitsudoJikan) || wkJitsudoJikan <= 0)== false)
				){
				//時間が入力されているとき、勤怠区分が空だとエラー
				if(StringUtils.isEmpty(kintaiKbn) || "00".equals(kintaiKbn)) {
					this.addValidateMessage("勤怠区分が入力されていません。");
					return false;
				}
			}

			if(
					(
							StringUtils.isEmpty(kintaiKbn) || 
							"00".equals(kintaiKbn) ||	//未設定
							"03".equals(kintaiKbn) ||	//欠勤
							"04".equals(kintaiKbn) ||	//有休
							"06".equals(kintaiKbn) ||	//積立有休
							"07".equals(kintaiKbn) ||	//特別休暇
							"08".equals(kintaiKbn) ||	//休日
							"09".equals(kintaiKbn) ||	//代休
							"10".equals(kintaiKbn)		//振替休日
							) == false
					) {
				//(勤怠区分が空または休日)でない場合のみ、未入力や0のチェックを行う
				isRequiredValidate.setParams(this.params);
				if(isRequiredValidate.doValidate(req, res, shusshaJi, info) == false) {
					this.addValidateMessage("出社（時）が入力されていません。");
					return false;
				}
			}

			isNumberValidate.setParams(this.params);
			if(isNumberValidate.doValidate(req, res, shusshaJi, info) == false) {
				this.addValidateMessage("出社（時）には数値を入力してください。");
				return false;
			}

			this.params.put("type", "half");
			this.params.put("length", "2");
			lengthValidate.setParams(this.params);
			if(lengthValidate.doValidate(req, res, shusshaJi, info) == false) {
				this.addValidateMessage("出社（時）の桁数が不正です。");
				return false;
			}

			this.params.put("length", "0");
			minNumberLimitValidate.setParams(this.params);
			if(minNumberLimitValidate.doValidate(req, res, shusshaJi, info) == false) {
				this.addValidateMessage("出社（時）にはマイナスは設定できません。");
				return false;
			}

			this.params.put("length", "23");
			maxNumberLimitValidate.setParams(this.params);
			if(maxNumberLimitValidate.doValidate(req, res, shusshaJi, info) == false) {
				this.addValidateMessage("時間は00～23の値で入力してください。");
				return false;
			}
			
			if(
					(
							StringUtils.isEmpty(kintaiKbn) || 
							"00".equals(kintaiKbn) ||	//未設定
							"03".equals(kintaiKbn) ||	//欠勤
							"04".equals(kintaiKbn) ||	//有休
							"06".equals(kintaiKbn) ||	//積立有休
							"07".equals(kintaiKbn) ||	//特別休暇
							"08".equals(kintaiKbn) ||	//休日
							"09".equals(kintaiKbn) ||	//代休
							"10".equals(kintaiKbn)		//振替休日
							) == false
					) {
				//(勤怠区分が空または休日)でない場合のみ、未入力や0のチェックを行う
				isRequiredValidate.setParams(this.params);
				if(isRequiredValidate.doValidate(req, res, shusshaFun, info) == false) {
					this.addValidateMessage("出社（分）が入力されていません。");
					return false;
				}
			}

			isNumberValidate.setParams(this.params);
			if(isNumberValidate.doValidate(req, res, shusshaFun, info) == false) {
				this.addValidateMessage("出社（分）には数値を入力してください。");
				return false;
			}

			this.params.put("type", "half");
			this.params.put("length", "2");
			lengthValidate.setParams(this.params);
			if(lengthValidate.doValidate(req, res, shusshaFun, info) == false) {
				this.addValidateMessage("出社（分）の桁数が不正です。");
				return false;
			}

			this.params.put("length", "0");
			minNumberLimitValidate.setParams(this.params);
			if(minNumberLimitValidate.doValidate(req, res, shusshaFun, info) == false) {
				this.addValidateMessage("出社（分）にはマイナスは設定できません。");
				return false;
			}

			this.params.put("length", "59");
			maxNumberLimitValidate.setParams(this.params);
			if(maxNumberLimitValidate.doValidate(req, res, shusshaFun, info) == false) {
				this.addValidateMessage("分は00～59の値で入力してください。");
				return false;
			}

			if(
					(
							StringUtils.isEmpty(kintaiKbn) || 
							"00".equals(kintaiKbn) ||	//未設定
							"03".equals(kintaiKbn) ||	//欠勤
							"04".equals(kintaiKbn) ||	//有休
							"06".equals(kintaiKbn) ||	//積立有休
							"07".equals(kintaiKbn) ||	//特別休暇
							"08".equals(kintaiKbn) ||	//休日
							"09".equals(kintaiKbn) ||	//代休
							"10".equals(kintaiKbn)		//振替休日
							) == false
					) {
				//(勤怠区分が空または休日)でない場合のみ、未入力や0のチェックを行う
				isRequiredValidate.setParams(this.params);
				if(isRequiredValidate.doValidate(req, res, taishaJi, info) == false) {
					this.addValidateMessage("退社（時）が入力されていません。");
					return false;
				}
			}

			isNumberValidate.setParams(this.params);
			if(isNumberValidate.doValidate(req, res, taishaJi, info) == false) {
				this.addValidateMessage("退社（時）には数値を入力してください。");
				return false;
			}

			this.params.put("type", "half");
			this.params.put("length", "2");
			lengthValidate.setParams(this.params);
			if(lengthValidate.doValidate(req, res, taishaJi, info) == false) {
				this.addValidateMessage("退社（時）の桁数が不正です。");
				return false;
			}

			this.params.put("length", "0");
			minNumberLimitValidate.setParams(this.params);
			if(minNumberLimitValidate.doValidate(req, res, taishaJi, info) == false) {
				this.addValidateMessage("退社（時）にはマイナスは設定できません。");
				return false;
			}

			this.params.put("length", "23");
			maxNumberLimitValidate.setParams(this.params);
			if(maxNumberLimitValidate.doValidate(req, res, taishaJi, info) == false) {
				this.addValidateMessage("時間は00～23の値で入力してください。");
				return false;
			}
			
			if(
					(
							StringUtils.isEmpty(kintaiKbn) || 
							"00".equals(kintaiKbn) ||	//未設定
							"03".equals(kintaiKbn) ||	//欠勤
							"04".equals(kintaiKbn) ||	//有休
							"06".equals(kintaiKbn) ||	//積立有休
							"07".equals(kintaiKbn) ||	//特別休暇
							"08".equals(kintaiKbn) ||	//休日
							"09".equals(kintaiKbn) ||	//代休
							"10".equals(kintaiKbn)		//振替休日
							) == false
					) {
				//(勤怠区分が空または休日)でない場合のみ、未入力や0のチェックを行う
				isRequiredValidate.setParams(this.params);
				if(isRequiredValidate.doValidate(req, res, taishaFun, info) == false) {
					this.addValidateMessage("退社（分）が入力されていません。");
					return false;
				}
			}

			isNumberValidate.setParams(this.params);
			if(isNumberValidate.doValidate(req, res, taishaFun, info) == false) {
				this.addValidateMessage("退社（分）には数値を入力してください。");
				return false;
			}

			this.params.put("type", "half");
			this.params.put("length", "2");
			lengthValidate.setParams(this.params);
			if(lengthValidate.doValidate(req, res, taishaFun, info) == false) {
				this.addValidateMessage("退社（分）の桁数が不正です。");
				return false;
			}

			this.params.put("length", "0");
			minNumberLimitValidate.setParams(this.params);
			if(minNumberLimitValidate.doValidate(req, res, taishaFun, info) == false) {
				this.addValidateMessage("退社（分）にはマイナスは設定できません。");
				return false;
			}

			this.params.put("length", "59");
			maxNumberLimitValidate.setParams(this.params);
			if(maxNumberLimitValidate.doValidate(req, res, taishaFun, info) == false) {
				this.addValidateMessage("分は00～59の値で入力してください。");
				return false;
			}
			
			
			try {
				if("".equals(shusshaJi) == false){
					intShusshaJi = Integer.parseInt(shusshaJi);
				}
				if("".equals(shusshaFun) == false){
					intShusshaFun = Integer.parseInt(shusshaFun);
				}
				if("".equals(taishaJi) == false){
					intTaishaJi = Integer.parseInt(taishaJi);
				}
				if("".equals(taishaFun) == false){
					intTaishaFun = Integer.parseInt(taishaFun);
				}
			} catch (Exception e) {
				return false;
			}

			if(
				("".equals(shusshaJi) == false) &&
				("".equals(shusshaFun) == false) &&
				("".equals(taishaJi) == false) &&
				("".equals(taishaFun) == false) &&
				((intShusshaJi * 60 + intShusshaFun) > (intTaishaJi * 60 + intTaishaFun))
			){
				this.addValidateMessage("出社時刻が退社時刻以降になっています。");
				return false;
			}

			if(
					(
							StringUtils.isEmpty(kintaiKbn) || 
							"00".equals(kintaiKbn) ||	//未設定
							"03".equals(kintaiKbn) ||	//欠勤
							"04".equals(kintaiKbn) ||	//有休
							"06".equals(kintaiKbn) ||	//積立有休
							"07".equals(kintaiKbn) ||	//特別休暇
							"08".equals(kintaiKbn) ||	//休日
							"09".equals(kintaiKbn) ||	//代休
							"10".equals(kintaiKbn)		//振替休日
							) == false
					) {
				//(勤怠区分が空または休日)でない場合のみ、未入力や0のチェックを行う
				isRequiredValidate.setParams(this.params);
				if(isRequiredValidate.doValidate(req, res, jitsudoJikan, info) == false) {
					this.addValidateMessage("実働時間が入力されていません。");
					return false;
				}
			}
			
			isNumberValidate.setParams(this.params);
			if(isNumberValidate.doValidate(req, res, jitsudoJikan, info) == false) {
				this.addValidateMessage("実働時間には数値を入力してください。");
				return false;
			}
			this.params.put("length", "0");
			minNumberLimitValidate.setParams(this.params);
			if(minNumberLimitValidate.doValidate(req, res, jitsudoJikan, info) == false) {
				this.addValidateMessage("実働時間にはマイナスは設定できません。");
				return false;
			}

			if(
					(
							StringUtils.isEmpty(kintaiKbn) || 
							"00".equals(kintaiKbn) ||	//未設定
							"03".equals(kintaiKbn) ||	//欠勤
							"04".equals(kintaiKbn) ||	//有休
							"06".equals(kintaiKbn) ||	//積立有休
							"07".equals(kintaiKbn) ||	//特別休暇
							"08".equals(kintaiKbn) ||	//休日
							"09".equals(kintaiKbn) ||	//代休
							"10".equals(kintaiKbn)		//振替休日
							) == false
					) {
				//(勤怠区分が空または休日)でない場合のみ、未入力や0のチェックを行う
				this.params.put("length", "0");
				this.params.put("comparisonoperator", "<");
				numberLimitValidate.setParams(this.params);
				if(numberLimitValidate.doValidate(req, res, jitsudoJikan, info) == false) {
					this.addValidateMessage("実働時間が入力されていません。");
					return false;
				}
			}

			try {
				if("".equals(jitsudoJikan) == false){
					dcmJitsudoJikan = new BigDecimal(jitsudoJikan);
				}
				if((dcmJitsudoJikan.precision() - dcmJitsudoJikan.scale() > 2) || (dcmJitsudoJikan.scale() > 2)){
						this.addValidateMessage("実働時間の桁数が不正です。");
						return false;
					}
			} catch (Exception e) {
				return false;
			}
			
			ArrayList<String> kintaiShinseiKbnList = new ArrayList<String>(Arrays.asList("", "", ""));

			for(int j = 1;j <= 3;j++){
				StringBuilder kaishiJiKeySb		= new StringBuilder();
				StringBuilder kaishiFunKeySb	= new StringBuilder();
				StringBuilder shuryoJiKeySb		= new StringBuilder();
				StringBuilder shuryoFunKeySb	= new StringBuilder();
				StringBuilder jikanKeySb		= new StringBuilder();
				StringBuilder kintaiShinseiKbnKeySb	= new StringBuilder();
				kaishiJiKeySb	.append("KintaiShinseiKaishiJi")	.append(String.valueOf(j)).append(String.valueOf(i));
				kaishiFunKeySb	.append("KintaiShinseiKaishiFun")	.append(String.valueOf(j)).append(String.valueOf(i));
				shuryoJiKeySb	.append("KintaiShinseiShuryoJi")	.append(String.valueOf(j)).append(String.valueOf(i));
				shuryoFunKeySb	.append("KintaiShinseiShuryoFun")	.append(String.valueOf(j)).append(String.valueOf(i));
				jikanKeySb		.append("KintaiShinseiJikan")		.append(String.valueOf(j)).append(String.valueOf(i));
				kintaiShinseiKbnKeySb	.append("KintaiShinseiKbn")	.append(String.valueOf(j)).append(String.valueOf(i));
				
				String kaishiJi			= this.getParameter(kaishiJiKeySb.toString());
				String kaishiFun		= this.getParameter(kaishiFunKeySb.toString());
				String shuryoJi			= this.getParameter(shuryoJiKeySb.toString());
				String shuryoFun		= this.getParameter(shuryoFunKeySb.toString());
				String jikan			= this.getParameter(jikanKeySb.toString());
				String kintaiShinseiKbn		= this.getParameter(kintaiShinseiKbnKeySb.toString());
				kintaiShinseiKbnList.set(j-1, kintaiShinseiKbn);
				
				int intKaishiJi		= 0;
				int intKaishiFun	= 0;
				int intShuryoJi		= 0;
				int intShuryoFun	= 0;
				BigDecimal dcmJikan	= BigDecimal.ZERO;
				

				
				double wkJikan = 0;
				try {
					wkJikan = Double.parseDouble(jikan);
				}
				catch(Exception e) {}
				if(
						("".equals(kaishiJi) == false) ||
						("".equals(kaishiFun) == false) ||
						("".equals(shuryoJi) == false) ||
						("".equals(shuryoFun) == false) ||
						(("".equals(jikan) || wkJikan <= 0)== false)
					){
					//時間が入力されているとき、申請区分が空だとエラー
					if(StringUtils.isEmpty(kintaiShinseiKbn) || "00".equals(kintaiShinseiKbn)) {
						this.addValidateMessage("申請区分" + String.valueOf(j) + "が入力されていません。");
						return false;
					}
				}

				if((StringUtils.isEmpty(kintaiShinseiKbn) || "00".equals(kintaiShinseiKbn)) == false) {
					//申請区分が空でない場合のみ、未入力や0のチェックを行う
					isRequiredValidate.setParams(this.params);
					if(isRequiredValidate.doValidate(req, res, kaishiJi, info) == false) {
						this.addValidateMessage("勤怠申請区分開始（時）" + String.valueOf(j) + "が入力されていません。");
						return false;
					}
				}

				isNumberValidate.setParams(this.params);
				if(isNumberValidate.doValidate(req, res, kaishiJi, info) == false) {
					this.addValidateMessage("勤怠申請区分開始（時）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}

				this.params.put("type", "half");
				this.params.put("length", "2");
				lengthValidate.setParams(this.params);
				if(lengthValidate.doValidate(req, res, kaishiJi, info) == false) {
					this.addValidateMessage("勤怠申請区分開始（時）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}

				this.params.put("length", "0");
				minNumberLimitValidate.setParams(this.params);
				if(minNumberLimitValidate.doValidate(req, res, kaishiJi, info) == false) {
					this.addValidateMessage("勤怠申請区分開始（時）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}

				this.params.put("length", "23");
				maxNumberLimitValidate.setParams(this.params);
				if(maxNumberLimitValidate.doValidate(req, res, kaishiJi, info) == false) {
					this.addValidateMessage("時間は00～23の値で入力してください。");
					return false;
				}
				
				if((StringUtils.isEmpty(kintaiShinseiKbn) || "00".equals(kintaiShinseiKbn)) == false) {
					//申請区分が空でない場合のみ、未入力や0のチェックを行う
					isRequiredValidate.setParams(this.params);
					if(isRequiredValidate.doValidate(req, res, kaishiFun, info) == false) {
						this.addValidateMessage("勤怠申請区分開始（分）" + String.valueOf(j) + "が入力されていません。");
						return false;
					}
				}

				isNumberValidate.setParams(this.params);
				if(isNumberValidate.doValidate(req, res, kaishiFun, info) == false) {
					this.addValidateMessage("勤怠申請区分開始（分）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}

				this.params.put("type", "half");
				this.params.put("length", "2");
				lengthValidate.setParams(this.params);
				if(lengthValidate.doValidate(req, res, kaishiFun, info) == false) {
					this.addValidateMessage("勤怠申請区分開始（分）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}

				this.params.put("length", "0");
				minNumberLimitValidate.setParams(this.params);
				if(minNumberLimitValidate.doValidate(req, res, kaishiFun, info) == false) {
					this.addValidateMessage("勤怠申請区分開始（分）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}

				this.params.put("length", "59");
				maxNumberLimitValidate.setParams(this.params);
				if(maxNumberLimitValidate.doValidate(req, res, kaishiFun, info) == false) {
					this.addValidateMessage("分は00～59の値で入力してください。");
					return false;
				}

				if((StringUtils.isEmpty(kintaiShinseiKbn) || "00".equals(kintaiShinseiKbn)) == false) {
					//申請区分が空でない場合のみ、未入力や0のチェックを行う
					isRequiredValidate.setParams(this.params);
					if(isRequiredValidate.doValidate(req, res, shuryoJi, info) == false) {
						this.addValidateMessage("勤怠申請区分終了（時）" + String.valueOf(j) + "が入力されていません。");
						return false;
					}
				}

				isNumberValidate.setParams(this.params);
				if(isNumberValidate.doValidate(req, res, shuryoJi, info) == false) {
					this.addValidateMessage("勤怠申請区分終了（時）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}

				this.params.put("type", "half");
				this.params.put("length", "2");
				lengthValidate.setParams(this.params);
				if(lengthValidate.doValidate(req, res, shuryoJi, info) == false) {
					this.addValidateMessage("勤怠申請区分終了（時）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}

				this.params.put("length", "0");
				minNumberLimitValidate.setParams(this.params);
				if(minNumberLimitValidate.doValidate(req, res, shuryoJi, info) == false) {
					this.addValidateMessage("勤怠申請区分終了（時）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}

				this.params.put("length", "23");
				maxNumberLimitValidate.setParams(this.params);
				if(maxNumberLimitValidate.doValidate(req, res, shuryoJi, info) == false) {
					this.addValidateMessage("時間は00～23の値で入力してください。");
					return false;
				}
				
				if((StringUtils.isEmpty(kintaiShinseiKbn) || "00".equals(kintaiShinseiKbn)) == false) {
					//申請区分が空でない場合のみ、未入力や0のチェックを行う
					isRequiredValidate.setParams(this.params);
					if(isRequiredValidate.doValidate(req, res, shuryoFun, info) == false) {
						this.addValidateMessage("勤怠申請区分終了（分）" + String.valueOf(j) + "が入力されていません。");
						return false;
					}
				}

				isNumberValidate.setParams(this.params);
				if(isNumberValidate.doValidate(req, res, shuryoFun, info) == false) {
					this.addValidateMessage("勤怠申請区分終了（分）" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}

				this.params.put("type", "half");
				this.params.put("length", "2");
				lengthValidate.setParams(this.params);
				if(lengthValidate.doValidate(req, res, shuryoFun, info) == false) {
					this.addValidateMessage("勤怠申請区分終了（分）" + String.valueOf(j) + "の桁数が不正です。");
					return false;
				}

				this.params.put("length", "0");
				minNumberLimitValidate.setParams(this.params);
				if(minNumberLimitValidate.doValidate(req, res, shuryoFun, info) == false) {
					this.addValidateMessage("勤怠申請区分終了（分）" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}

				this.params.put("length", "59");
				maxNumberLimitValidate.setParams(this.params);
				if(maxNumberLimitValidate.doValidate(req, res, shuryoFun, info) == false) {
					this.addValidateMessage("分は00～59の値で入力してください。");
					return false;
				}
				
				try {
					if("".equals(kaishiJi) == false){
						intKaishiJi = Integer.parseInt(kaishiJi);
					}
					if("".equals(kaishiFun) == false){
						intKaishiFun = Integer.parseInt(kaishiFun);
					}
					if("".equals(shuryoJi) == false){
						intShuryoJi = Integer.parseInt(shuryoJi);
					}
					if("".equals(shuryoFun) == false){
						intShuryoFun = Integer.parseInt(shuryoFun);
					}
				} catch (Exception e) {
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

				if((StringUtils.isEmpty(kintaiShinseiKbn) || "00".equals(kintaiShinseiKbn)) == false) {
					//申請区分が空でない場合のみ、未入力や0のチェックを行う
					isRequiredValidate.setParams(this.params);
					if(isRequiredValidate.doValidate(req, res, jitsudoJikan, info) == false) {
						this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "が入力されていません。");
						return false;
					}
				}

				isNumberValidate.setParams(this.params);
				if(isNumberValidate.doValidate(req, res, jikan, info) == false) {
					this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "には数値を入力してください。");
					return false;
				}
				this.params.put("length", "0");
				minNumberLimitValidate.setParams(this.params);
				if(minNumberLimitValidate.doValidate(req, res, jikan, info) == false) {
					this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "にはマイナスは設定できません。");
					return false;
				}

				if((StringUtils.isEmpty(kintaiShinseiKbn) || "00".equals(kintaiShinseiKbn)) == false) {
					//申請区分が空でない場合のみ、未入力や0のチェックを行う
					this.params.put("length", "0");
					this.params.put("comparisonoperator", "<");
					numberLimitValidate.setParams(this.params);
					if(numberLimitValidate.doValidate(req, res, jitsudoJikan, info) == false) {
						this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "が入力されていません。");
						return false;
					}
				}
				
				try {
					if("".equals(jikan) == false){
						dcmJikan = new BigDecimal(jikan);
					}
					if((dcmJikan.precision() - dcmJikan.scale() > 2) || (dcmJikan.scale() > 2)){
						this.addValidateMessage("勤怠申請時間" + String.valueOf(j) + "の桁数が不正です。");
							return false;
						}
				} catch (Exception e) {
					return false;
				}
				
			}
			
			
			if((StringUtils.isEmpty(kintaiKbn) || "00".equals(kintaiKbn)) == false) {
				//勤怠区分が空でない場合のみ、申請パターンのチェックを行う
				// 賃金申請書入力区分("01"固定)、勤怠区分、勤怠申請区分1,2,3の組み合わせが、申請パターンマスタ(MST_SHINSEI_PATTERN)に登録されていない場合
				if(shinseiPatternCheck(con,kintaiKbn, kintaiShinseiKbnList.get(0), kintaiShinseiKbnList.get(1), kintaiShinseiKbnList.get(2)) == false){
					this.addValidateMessage("申請区分の組み合わせが正しくありません。");
					return false;
				}
			}

			StringBuilder bikoKeySb	= new StringBuilder();
			bikoKeySb	.append("KintaiShinseiBiko")	.append(String.valueOf(i));
			String biko		= this.getParameter(bikoKeySb.toString());

			this.params.put("type", "half");
			this.params.put("length", "40");
			lengthValidate.setParams(this.params);
			if(lengthValidate.doValidate(req, res, biko, info) == false) {
				this.addValidateMessage("備考が40バイトを超えています。(40バイト = 全角40/2文字, 半角40文字)");
				return false;
			}

		}

		return true;
	}

	/**
	 * 詳細説明
	 * 
	 * 申請パターンマスタ確認
	 */
	private boolean shinseiPatternCheck(Connection con, String kintaiKbn, String shinseiKbn1, String shinseiKbn2, String shinseiKbn3) throws Exception {
		
		boolean result = false;
		
		// DB接続
		StringBuffer sql				= new StringBuffer();
		PreparedStatement pstmt			= null;
		PreparedStatementFactory pstmtf	= new PreparedStatementFactory();
		ResultSet rset					= null;
		
		sql.append(" SELECT ");
		sql.append(" 	COUNT(*) AS CNT ");
		sql.append(" FROM ");
		sql.append(" 	MST_SHINSEI_PATTERN ");
		sql.append(" WHERE ");
		sql.append(" 	SyukinboNyuryokuKbn = '01' ");
		sql.append(" AND KintaiKbn = ? ");
		sql.append(" AND ShinseiKbn1 = ? ");
		sql.append(" AND ShinseiKbn2 = ? ");
		sql.append(" AND ShinseiKbn3 = ? ");

		pstmtf.addValue("String", kintaiKbn);
		pstmtf.addValue("String", shinseiKbn1);
		pstmtf.addValue("String", shinseiKbn2);
		pstmtf.addValue("String", shinseiKbn3);

		try {
			// SQL文の生成
			pstmt = con.prepareStatement(sql.toString());
			// パラメータの設定
			pstmtf.setPreparedStatement(pstmt);
			// 実行
			rset = pstmt.executeQuery();
			// 結果取得
			if(rset.next()){
				if(rset.getInt("CNT") > 0) {
					result = true;
				}
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