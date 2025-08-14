package jp.co.kintai.carreservation.validate;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;
public class KinShukkinBoExclusiveValidate extends ValidateBase {

	public KinShukkinBoExclusiveValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	/**
	 * 詳細説明
	 * 
	 * 出勤簿更新時の入力チェック
	 */
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		//1か月分入力項目があるので1か月分ループ
		for(int i = 0;i < 31;i++){
			StringBuilder taishoNengappiKeySb	= new StringBuilder();
			taishoNengappiKeySb	.append("TaishoNengappi")	.append(String.valueOf(i));
			String taishoNengappi		= this.getParameter(taishoNengappiKeySb.toString());
			
			if(StringUtils.isEmpty(taishoNengappi)) {
				//データが終わったので終了
				break;
			}

			//各日ごとに排他チェックを呼び出す
			StringBuilder meisaiSaishuKoshinDateKeySb	= new StringBuilder();
			meisaiSaishuKoshinDateKeySb	.append("MeisaiSaishuKoshinDate")	.append(String.valueOf(i));
			StringBuilder meisaiSaishuKoshinJikanKeySb	= new StringBuilder();
			meisaiSaishuKoshinJikanKeySb	.append("MeisaiSaishuKoshinJikan")	.append(String.valueOf(i));
			
			this.params.put("tableName", "KIN_SHUKKINBO_MEISAI");
			this.params.put("keyName", "TaishoNenGetsudo,ShainNO,TaishoNengappi");
			this.params.put("keyValue", "txtSearchedTaishoYM,txtSearchedShainNO," + taishoNengappiKeySb.toString());
			this.params.put("saishuKoshinDate", meisaiSaishuKoshinDateKeySb.toString());
			this.params.put("saishuKoshinJikan", meisaiSaishuKoshinJikanKeySb.toString());
			
			ExclusiveValidate exclusiveValidate = new ExclusiveValidate(req, res, info);
			exclusiveValidate.setParams(this.params);
			boolean exclusiveResult = exclusiveValidate.doValidate(req, res, value, info);
			if(exclusiveResult == false) {
				return false;
			}
			
		}
		
		return true;
	}
}