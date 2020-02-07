package tr.limonist.classes;

import java.io.Serializable;

public class PromotionItem implements Serializable {

	private String id;
	private String title;
	private String detail;
	private String date;
	private String image;
	private String status;
	private String type;

	public PromotionItem(String id, String title, String detail, String date, String image, String status,
			String type) {

		this.id = id;
		this.title = title;
		this.detail = detail;
		this.date = date;
		this.image = image;
		this.status = status;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDetail() {
		return detail;
	}

	public String getDate() {
		return date;
	}

	public String getImage() {
		return image;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}
}