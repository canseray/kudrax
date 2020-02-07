package tr.limonist.classes;

import java.io.Serializable;

public class SlideMenuItem implements Serializable {
	private String id;
	private String title;
	private String image;
	private String prep;
	private String badge;

	public SlideMenuItem(String id, String title, String image, String prep, String badge) {
		this.title = title;
		this.id = id;
		this.image = image;
		this.prep = prep;
		this.badge = badge;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getImage() {
		return image;
	}

	public String getPrep() {
		return prep;
	}

	public String getBadge() {
		return badge;
	}

}