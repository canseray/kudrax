package tr.limonist.classes;

import java.io.Serializable;

public class FavoriteItem implements Serializable {

	private String product_id;
	private String name;
	private String image;
	private String new_price;
	private String old_price;
	private String video_url;

	public FavoriteItem(String product_id, String name, String image, String new_price, String old_price, String video_url) {
		this.product_id = product_id;
		this.name = name;
		this.image = image;
		this.new_price = new_price;
		this.old_price = old_price;
		this.video_url = video_url;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNew_price() {
		return new_price;
	}

	public void setNew_price(String new_price) {
		this.new_price = new_price;
	}

	public String getOld_price() {
		return old_price;
	}

	public void setOld_price(String old_price) {
		this.old_price = old_price;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
}