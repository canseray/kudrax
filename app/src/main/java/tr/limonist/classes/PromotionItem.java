package tr.limonist.classes;

import java.io.Serializable;

public class PromotionItem implements Serializable {

	private String id;
	private String title;
	private String detail;
	private String date;
	private String image;
	private String status;
	private String activation_status;
	private String code;
	private String promotion_date_title;
	private String date_title;


	public PromotionItem(String id,
						 String title,
						 String detail,
						 String date,
						 String image,
						 String status,
						 String activation_status,
						 String code,
						 String promotion_date_title,
						 String date_title) {
		this.id = id;
		this.title = title;
		this.detail = detail;
		this.date = date;
		this.image = image;
		this.status = status;
		this.activation_status = activation_status;
		this.code = code;
		this.promotion_date_title = promotion_date_title;
		this.date_title = date_title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getActivation_status() {
		return activation_status;
	}

	public void setActivation_status(String activation_status) {
		this.activation_status = activation_status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPromotion_date_title() {
		return promotion_date_title;
	}

	public void setPromotion_date_title(String promotion_date_title) {
		this.promotion_date_title = promotion_date_title;
	}

	public String getDate_title() {
		return date_title;
	}

	public void setDate_title(String date_title) {
		this.date_title = date_title;
	}
}