package tr.limonist.classes;

import java.io.Serializable;

public class BranchesItem implements Serializable {

	private String id;
	private String name;
	private String address;
	private String image;
	private String phone;
	private String email;
	private String lat;
	private String lng;
	private String country;

	public BranchesItem(String id, String name, String address, String image, String phone, String email, String lat, String lng, String country) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.image = image;
		this.phone = phone;
		this.email = email;
		this.lat = lat;
		this.lng = lng;
		this.country = country;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}