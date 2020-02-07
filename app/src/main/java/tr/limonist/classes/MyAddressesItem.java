package tr.limonist.classes;

import java.io.Serializable;

public class MyAddressesItem implements Serializable {
	private String id;
	private String title;
	private String address;
	private String city;
	private String sub_city;

	private String special;
	private String lat;
	private String lon;


	public MyAddressesItem(String id, String title, String address, String city, String sub_city, String special, String lat, String lon) {
		this.title = title;
		this.id = id;
		this.address = address;
		this.city = city;
		this.sub_city = sub_city;

		this.special = special;
		this.lat = lat;
		this.lon = lon;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getSub_city() {
		return sub_city;
	}

	public String getSpecial() {
		return special;
	}

	public String getLat() {
		return lat;
	}

	public String getLon() {
		return lon;
	}
}