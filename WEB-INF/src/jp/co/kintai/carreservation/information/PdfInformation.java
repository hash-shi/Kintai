package jp.co.kintai.carreservation.information;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PdfInformation implements Serializable {
	
	private String textData;

	public String getTextData() {
		return textData;
	}
	public void setTextData(String textData) {
		this.textData = textData;
	}
}
