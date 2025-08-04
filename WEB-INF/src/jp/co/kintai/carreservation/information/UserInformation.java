package jp.co.kintai.carreservation.information;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class UserInformation implements Serializable {
	
	private String shainNO;
	private String shainName;
	private String password;
	private String userKbn;
	private String shainKbn;
	private String eigyoshoCode;
	private String eigyoshoName;
	private String bushoCode;
	private String bushoName;
	private String bushoKbn;
	private String taisyokuDate;
	private ArrayList<String> shoriKanoEigyoshoCode;
	private String loginDate;
	private String saishuKoshinShainNO;
	private String saishuKoshinDate;
	private String saishuKoshinJikan;
	
	public String getShainNO() { 
		return shainNO;
	}
	public void setShainNO(String shainNO) {
		this.shainNO = shainNO;
	}
	
	public String getShainName() { 
		return shainName;
	}
	public void setShainName(String shainName) {
		this.shainName = shainName;
	}

	public String getPassword() { 
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserKbn() {
		return userKbn;
	}
	public void setUserKbn(String userKbn) {
		this.userKbn = userKbn;
	}
	
	public String getShainKbn() {
		return shainKbn;
	}
	public void setShainKbn(String shainKbn) {
		this.shainKbn = shainKbn;
	}

	public String getEigyoshoCode() {
		return eigyoshoCode;
	}
	public void setEigyoshoCode(String eigyoshoCode) {
		this.eigyoshoCode = eigyoshoCode;
	}

	public String getEigyoshoName() {
		return eigyoshoName;
	}
	public void setEigyoshoName(String eigyoshoName) {
		this.eigyoshoName = eigyoshoName;
	}
	
	public String getBushoCode() {
		return bushoCode;
	}
	public void setBushoCode(String bushoCode) {
		this.bushoCode = bushoCode;
	}

	public String getBushoName() {
		return bushoName;
	}
	public void setBushoName(String bushoName) {
		this.bushoName = bushoName;
	}

	public String getBushoKbn() {
		return bushoKbn;
	}
	public void setBushoKbn(String bushoKbn) {
		this.bushoKbn = bushoKbn;
	}
	
	public String getTaisyokuDate() {
		return taisyokuDate;
	}
	public void setTaisyokuDate(String taisyokuDate) {
		this.taisyokuDate = taisyokuDate;
	}

	public ArrayList<String> getShoriKanoEigyoshoCode() {
		return shoriKanoEigyoshoCode;
	}
	public void setShoriKanoEigyoshoCode(ArrayList<String> shoriKanoEigyoshoCode) {
		this.shoriKanoEigyoshoCode = shoriKanoEigyoshoCode;
	}

	public String getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}
	
	public String getSaishuKoshinShainNO() {
		return saishuKoshinShainNO;
	}
	public void setSaishuKoshinShainNO(String saishuKoshinShainNO) {
		this.saishuKoshinShainNO = saishuKoshinShainNO;
	}
	
	public String getSaishuKoshinDate() {
		return saishuKoshinDate;
	}
	public void setSaishuKoshinDate(String saishuKoshinDate) {
		this.saishuKoshinDate = saishuKoshinDate;
	}
	
	public String getSaishuKoshinJikan() {
		return saishuKoshinJikan;
	}
	public void setSaishuKoshinJikan(String saishuKoshinJikan) {
		this.saishuKoshinJikan = saishuKoshinJikan;
	}
}
