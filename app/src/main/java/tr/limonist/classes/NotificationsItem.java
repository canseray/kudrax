package tr.limonist.classes;

import java.io.Serializable;

public class NotificationsItem implements Serializable {

	private String id;
	private String title;
	private String text;
	private String date;
	private String image;
	private String color;
	private String prep;

	public NotificationsItem(String id, String title, String text, String date, String image, String color,
							 String prep) {

		this.id = id;
		this.title = title;
		this.text = text;
		this.image = image;
		this.color = color;
		this.date = date;
		this.prep = prep;

	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public String getDate() {
		return date;
	}

	public String getImage() {
		return image;
	}

	public String getColor() {
		return color;
	}

	public String getPrep() {
		return prep;
	}

}