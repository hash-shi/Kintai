package jp.co.kintai.carreservation.validate;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.base.ValidateBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;
import jp.co.tjs_net.java.framework.validate.IsHalf;
import jp.co.tjs_net.java.framework.validate.IsNumber;
import jp.co.tjs_net.java.framework.validate.IsRequired;

public class MstShainEigyoshoValidate extends ValidateBase {

	public MstShainEigyoshoValidate(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		/**
		 * 詳細説明
		 * 
		 * 社員マスタメンテの処理可能営業所関係のチェックを行う。
		 * 処理可能営業所は行数が可変のため、xmlで静的な指定ができず動的に指定する。
		 * 必須,半角,数字,コード存在をチェックする。
		 * 重複は考慮しないモノとする。(登録,更新処理時に対応する)
		 * 
		 */
		
		boolean result					= false;
		
		//=====================================================================
		// クラス
		//=====================================================================
		// 必須
		IsRequired isRequired = new IsRequired(req, res, info);
		// 全角半角
		IsHalf isHalf = new IsHalf(req, res, info);
		// 数値
		// カンマ設定のパラメータを設定しないとシステムエラーになる。
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isReplaceComma", "false");
		IsNumber isNumber = new IsNumber(req, res, info);
		isNumber.setParams(map);
		
		//=====================================================================
		// パラメータ取得
		//=====================================================================
		// 処理可能営業所コード(id)
		String shoriKanoEigyoshoCode	= this.params.get("code").toString();
		// 処理可能営業所名(id)
		String shoriKanoEigyoshoName	= this.params.get("name").toString();
		
		// まずループしながら全体を取得する
		
		// 全体を格納する配列
		ArrayList<HashMap<String, String>> eigyoshos = new ArrayList<>();
		
		for (int i = 0; 0 <= i; i++) {
			
			// 処理可能営業所項目がない場合はループを抜ける
			// ※keyが存在して値がない場合は空文字が返却される
			// 　keyそのものが存在しない場合はnullが返却される
			if (req.getParameter(shoriKanoEigyoshoCode + "_" + i) == null) { break; }
			
			// 配列の準備
			HashMap<String, String> record = new HashMap<String, String>();

			// パラメータ取得
			String eigyoshoCode	= req.getParameter(shoriKanoEigyoshoCode + "_" + i);
			String eigyoshoName	= req.getParameter(shoriKanoEigyoshoName + "_" + i);
			
			// 値を格納
			record.put("columnName", "処理可能営業所コード" + "_" + (i + 1));
			record.put("eigyoshoCode", eigyoshoCode);
			record.put("eigyoshoName", eigyoshoName);
			
			// 配列の格納
			eigyoshos.add(record);
		}
		
		// 配列を回しながらチェックを行う。
		for (HashMap<String, String> eigyosho : eigyoshos) {
			// 必須
			if (!isRequired.doValidate(req, res, eigyosho.get("eigyoshoCode"), info)) { 
				this.addValidateMessage(eigyosho.get("columnName") + "が入力されていません。");
				return false;
			}
			// 全角半角
			if (!isHalf.doValidate(req, res, eigyosho.get("eigyoshoCode"), info)) { 
				this.addValidateMessage(eigyosho.get("columnName") + "には全角文字は入力できません。");
				return false;
			}
			// 数字
			if (!isNumber.doValidate(req, res, eigyosho.get("eigyoshoCode"), info)) { 
				this.addValidateMessage(eigyosho.get("columnName") + "には数字を入力してください。");
				return false;
			}
			// コード存在(名称がない = 存在しないコード)
			if (!isRequired.doValidate(req, res, eigyosho.get("eigyoshoName"), info)) { 
				this.addValidateMessage("該当する" + eigyosho.get("columnName") + "が存在しません。");
				return false;
			}
		}
		
		result = true;
		
		// 結果返却
		return result;
	}
}