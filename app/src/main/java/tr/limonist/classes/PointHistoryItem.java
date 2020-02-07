package tr.limonist.classes;

import java.io.Serializable;

public class PointHistoryItem implements Serializable {

	private String id;
	private String value;
	private String detail;
	private String date;

	public PointHistoryItem(String id, String value, String detail, String date) {

		this.id = id;
		this.value = value;
		this.detail = detail;
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public String getDetail() {
		return detail;
	}

	public String getDate() {
		return date;
	}

	public String getValue() {
		return value;
	}
}