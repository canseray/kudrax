package tr.limonist.classes;

import java.io.Serializable;

public class BranchesItem implements Serializable {

	private String id;
	private String name;
	private String city;
	private String address;

	private String phone;
	private String email;
	private String lat;
	private String lng;

	public BranchesItem(String id, String name, String city, String address, String phone, String email, String lat, String lng) {

		this.id = id;
		this.name = name;
		this.city = city;
		this.address = address;

		this.phone = phone;
		this.email = email;
		this.lat = lat;
		this.lng = lng;

	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getLat() {
		return lat;
	}

	public String getLng() {
		return lng;
	}
}