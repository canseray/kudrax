package tr.limonist.classes;

import java.io.Serializable;

public class CartItem implements Serializable {

	private String row_id;
	private String name;
	private String price;
	private String count;
	private String image;
	private String desc;
	private String id;
	private String amount;

	public CartItem(String row_id, String name,String price, String count,String image, String desc, String id, String amount) {

		this.row_id = row_id;
		this.name = name;
		this.price = price;
		this.count = count;
		this.image = image;
		this.desc = desc;
		this.id = id;
		this.amount = amount;
	}

	public String getRow_id() {
		return row_id;
	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}

	public String getCount() {
		return count;
	}

	public String getImage() {
		return image;
	}

	public String getDesc() {
		return desc;
	}

	public String getId() {
		return id;
	}

	public String getAmount() {
		return amount;
	}
}