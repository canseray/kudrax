package tr.limonist.kudra.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.USER;
import tr.limonist.classes.WelcomeItem;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.main.Main;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.app.user.LoginMain;
import tr.limonist.kudra.app.user.LoginOptions;
import tr.limonist.kudra.app.user.WelcomeActivity;

@SuppressLint("NewApi")
public class StartMain extends ActivityManagePermission {

	private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1000;
	public TransparentProgressDialog pd;
	public PackageInfo pInfo;
	public String part1;
	private Activity m_activity;
	String call_type = "0";
	ArrayList<WelcomeItem> results_welcome;
	public String[] welcome_part1;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = StartMain.this;
		APP.setWindowsProperties(m_activity,true);
		if (getIntent().hasExtra("call_type"))
			call_type = getIntent().getStringExtra("call_type");

		pd = new TransparentProgressDialog(m_activity, "", true);

		askCompactPermission(PermissionUtils.Manifest_ACCESS_FINE_LOCATION, new PermissionResult() {
			@Override
			public void permissionGranted() {
				//permission granted
				MainStart();
			}

			@Override
			public void permissionDenied() {
				//permission denied
				MainStart();
			}
			@Override
			public void permissionForeverDenied() {
				// contains not ask again
				MainStart();
			}
		});

	}

	private void MainStart() {

		APP.android_id = APP.getDeviceId(m_activity);

		pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		APP.version = pInfo.versionName;

		boolean isPermissionGranted = isPermissionGranted(m_activity, PermissionUtils.Manifest_ACCESS_FINE_LOCATION);
		if(isPermissionGranted) {
			APP.mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		}

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				APP.lat = location.getLatitude();
				APP.lon = location.getLongitude();
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};
		if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

			ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
					MY_PERMISSION_ACCESS_COURSE_LOCATION );
		}
		if(isPermissionGranted) {
			APP.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}

		if (APP.main_user != null && APP.main_user.email.length() > 0) {
			if (APP.main_user != null && APP.main_user.pass.length() > 0) {
				if (APP.isValidEmail(APP.main_user.email)) {
					pd.show();
					new Connection2().execute("IMAGE_IDS");

				} else {
					start();
				}
			} else {
				start();
			}
		} else {
			start();
		}
	}

	public String respPart1;
	public String respPart2;

	public void start() {

		APP.android_id = APP.getDeviceId(m_activity);
		boolean welcome = (boolean) APP.myPrefs.getBoolean("welcome",true);
		if (welcome){
			pd.show();
			new Connection3().execute("");

		} else {

		startActivity(new Intent(m_activity, LoginOptions.class).putExtra("call_type",call_type));
		finish();
		}
	}

	private class Connection2 extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... args) {

			APP.device_id = APP.myPrefs.getString("regId", "");

			List<Pair<String, String>> nameValuePairs = new ArrayList<>();

			String device_model = "";
			String os_version = "";
			try {
				device_model = Build.MANUFACTURER + " " + Build.MODEL;
				os_version = Build.VERSION.RELEASE;
			} catch (Exception e1) {
				e1.printStackTrace();

			}

			nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.email)));
			nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.main_user.pass)));
			nameValuePairs.add(new Pair<>("param7", APP.base64Encode(APP.device_id)));
			nameValuePairs.add(new Pair<>("param8", APP.base64Encode("A")));
			nameValuePairs.add(new Pair<>("param9", APP.base64Encode(APP.android_id)));
			nameValuePairs.add(new Pair<>("param10", APP.base64Encode(device_model)));
			nameValuePairs.add(new Pair<>("param12", APP.base64Encode(os_version)));
			nameValuePairs.add(new Pair<>("param13", APP.base64Encode(APP.android_id)));
			nameValuePairs.add(new Pair<>("param14", APP.base64Encode(APP.version != null ? APP.version : "")));
			nameValuePairs.add(new Pair<>("param15", APP.base64Encode(APP.language_id)));

			String xml = APP.post1(nameValuePairs, APP.path + "/account_panel/login_in.php");

			if (xml != null && !xml.contentEquals("fail")) {
				try {

					DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
					List<String> dataList = new ArrayList<String>();

					for (int i=0;i< parse.getElementsByTagName("row").getLength();i++){

						respPart1 = APP.base64Decode(APP.getElement(parse,"part1"));
						respPart2 = APP.base64Decode(APP.getElement(parse,"part2"));

					}

					if (respPart1.contentEquals("OK")) {
						String[] customerInfo = respPart2.split("\\[#\\]");

						USER user = new USER(
								(customerInfo.length > 0 ? customerInfo[0] : ""),
								(customerInfo.length > 1 ? customerInfo[1] : ""),
								(customerInfo.length > 2 ? customerInfo[2] : ""),
								(customerInfo.length > 3 ? customerInfo[3] : ""),
								(customerInfo.length > 4 ? customerInfo[4] : ""),
								(customerInfo.length > 5 ? customerInfo[5] : ""),
								APP.main_user.pass);

						APP.main_user = user;
						Gson gson = new Gson();
						String json = gson.toJson(user);
						APP.e.putString("USER", json);
						APP.e.commit();

						return "true";
					}

					else {
						APP.main_user = null;
						APP.e.putString("USER", null);
						APP.e.commit();
						return "error";
					}

				} catch(Exception e){
					e.printStackTrace();
					return "false";
				}

			} else {
				return "false";
			}

		}

		protected void onPostExecute(String result) {
			if (pd != null)
				pd.dismiss();
			if (result.contentEquals("true"))
				StartMenu();
			else
				start();
		}
	}

	public void StartMenu() {
		Intent intent = new Intent(m_activity, Main.class);
		intent.putExtra("call_type", call_type);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
		finish();

	}

	private class Connection3 extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			List<Pair<String, String>> nameValuePairs = new ArrayList<>();

			results_welcome = new ArrayList<>();

			nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.android_id)));
			nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
			nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));
			String xml = APP.post1(nameValuePairs, APP.path + "/get_intro_screen_items.php");

			if (xml != null && !xml.contentEquals("fail")) {

				try {

					DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

					for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

						welcome_part1 = APP.base64Decode(APP.getElement(parse,"part1")).split("\\[##\\]");

					}

					if (!welcome_part1[0].contentEquals("")) {

						for (int i = 0; i < welcome_part1.length; i++) {
							String[] temp = welcome_part1[i].split("\\[#\\]");
							WelcomeItem wi = new WelcomeItem(temp.length > 0 ? temp[0] : "",
									temp.length > 1 ? temp[1] : "",
									temp.length > 2 ? temp[2] : "",
									temp.length > 3 ? temp[3] : "");
							results_welcome.add(wi);
						}

						return "true";
					} else
						return "false";

				} catch (Exception e) {
					e.printStackTrace();
					return "false";
				}

			} else {
				return "false";
			}

		}

		@Override
		protected void onPostExecute(String result) {

			if (pd != null)
				pd.dismiss();

			if (result.contentEquals("true")) {
				startActivityForResult(new Intent(m_activity, WelcomeActivity.class).putExtra("results",results_welcome), 1);
			} else {
				APP.show_status(m_activity, 1,
						getResources().getString(R.string.s_unexpected_connection_error_has_occured));
			}

		}

	}

}
