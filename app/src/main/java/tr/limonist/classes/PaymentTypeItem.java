package tr.limonist.classes;

import java.io.Serializable;

public class PaymentTypeItem implements Serializable {

	private String type;
	private String name;
	private String active;

	public PaymentTypeItem(String type, String name, String active) {

		this.type = type;
		this.name = name;
		this.active = active;

	}


	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getActive() {
		return active;
	}
}