package tr.limonist.classes;

import java.io.Serializable;

public class ProductItem implements Serializable {

	private String id;
	private String name;
	private String desc;
	private String price;
	private String old_price;
	private String image;
	private String quantities;
	private String order_max_quantities;
	private String order_min_quantities;

	public ProductItem(String id,
					   String name,
					   String desc,
					   String price,
					   String old_price,
					   String image,
					   String quantities,
					   String order_max_quantities,
					   String order_min_quantities) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.price = price;
		this.old_price = old_price;
		this.image = image;
		this.quantities = quantities;
		this.order_max_quantities = order_max_quantities;
		this.order_min_quantities = order_min_quantities;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getOld_price() {
		return old_price;
	}

	public void setOld_price(String old_price) {
		this.old_price = old_price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getQuantities() {
		return quantities;
	}

	public void setQuantities(String quantities) {
		this.quantities = quantities;
	}

	public String getOrder_max_quantities() {
		return order_max_quantities;
	}

	public void setOrder_max_quantities(String order_max_quantities) {
		this.order_max_quantities = order_max_quantities;
	}

	public String getOrder_min_quantities() {
		return order_min_quantities;
	}

	public void setOrder_min_quantities(String order_min_quantities) {
		this.order_min_quantities = order_min_quantities;
	}
}