package tr.limonist.classes;

import java.io.Serializable;

public class FavoriteItem implements Serializable {

	private String product;
	private String name;
	private String image;
	private String price;
	private String old;

	public FavoriteItem(String product, String name, String image, String price,String old) {

		this.product = product;
		this.name = name;
		this.price = price;
		this.image = image;
		this.old = old;
	}

	public String getProduct() {
		return product;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String getPrice() {
		return price;
	}

	public String getOld() {
		return old;
	}
}