package tr.limonist.classes;

import java.io.Serializable;

public class OrderItem implements Serializable {
	private String id;
	private String name;
	private String image;
	private String count;
	private String price;
	private String quantity;
	private String amount;

	public OrderItem(String id, String name, String image, String price,String count, String quantity, String amount) {
		this.name = name;
		this.id = id;
		this.image = image;
		this.count = count;
		this.price = price;
		this.quantity = quantity;
		this.amount = amount;
	}

	public String getAmount() {
		return amount;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String getCount() {
		return count;
	}

	public String getPrice() {
		return price;
	}

	public String getQuantity() {
		return quantity;
	}
}