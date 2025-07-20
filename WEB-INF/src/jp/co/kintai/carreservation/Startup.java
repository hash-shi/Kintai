package jp.co.kintai.carreservation;

/**
 * @author toshiyuki
 *
 */
public class Startup extends jp.co.tjs_net.java.framework.startup.Startup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see lips.fw.fsapp.core.Index#getClass(java.lang.String)
	 */
	@Override
	public Class<?> getClass(String className) {
		Class<?> result		= null;
		try	{ result = Class.forName(className); }
		catch (ClassNotFoundException e){ result = null; }
		return result;
	}
}
