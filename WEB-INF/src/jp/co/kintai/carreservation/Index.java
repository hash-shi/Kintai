package jp.co.kintai.carreservation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;

@SuppressWarnings("serial")
public class Index extends jp.co.tjs_net.java.framework.core.Index {

	@Override
	public Class<?> getClass(String className) {
		Class<?> result		= null;
		try	{ result = Class.forName(className); }
		catch (ClassNotFoundException e){ result = null; }
		return result;
	}

	@Override
	public boolean init(HttpServletRequest req, HttpServletResponse res, IndexInformation info) throws Exception {
		// 結果返却
		return true;
	}

	@Override
	public void exception(IndexInformation info, Exception exception) throws Exception {
	}
}
