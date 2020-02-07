package tr.limonist.classes;

import java.io.Serializable;

public class ProductItem implements Serializable {

	private String id;
	private String title;
	private String image;
	private String price;
	private String video;
	private String favorite;

	public ProductItem(String id, String title, String image,String price, String video, String favorite) {

		this.id = id;
		this.title = title;
		this.image = image;
		this.price = price;
		this.video = video;
		this.favorite = favorite;
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

	public String getPrice() {
		return price;
	}

	public String getVideo() {
		return video;
	}

	public String getFavorite() {
		return favorite;
	}
}