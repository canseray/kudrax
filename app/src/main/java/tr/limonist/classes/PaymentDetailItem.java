package tr.limonist.classes;

import java.io.Serializable;

public class PaymentDetailItem implements Serializable {

	private String title;
	private String desc;

	public PaymentDetailItem(String title, String desc) {

		this.title = title;
		this.desc = desc;

	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}
}