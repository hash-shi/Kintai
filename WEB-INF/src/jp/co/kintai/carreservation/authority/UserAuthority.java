package jp.co.kintai.carreservation.authority;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.kintai.carreservation.define.Define;
import jp.co.tjs_net.java.framework.base.AuthorityBase;
import jp.co.tjs_net.java.framework.information.IndexInformation;

public class UserAuthority extends AuthorityBase {
	
	public UserAuthority(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}

	@Override
	public void doRun(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		if (req.getSession().getAttribute(Define.SESSION_ID) == null){
			setAuthorityResult(false);
			return;
		}
		
		try {
			
			res.setContentType("text/html");
			String clientIp = req.getRemoteAddr();
			
			setAuthorityResult(true);
			return;
		} catch (Exception exp){
			setAuthorityResult(false);
			return;
		}
	}
	
}