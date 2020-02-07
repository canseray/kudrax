package tr.limonist.kudra.app.cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.MyAddressesItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyAlertDialog;

public class SelectLocation extends FragmentActivity {

    private TransparentProgressDialog pd;
    private Activity m_activity;
    private GoogleMap googleHarita;
    LatLng loc = null;
    MyAddressesItem coming_item = null;
    private EditText et_title, et_address, et_city, et_special, et_subcity;
    private String s_address = "", s_city = "", s_subcity = "", s_title = "", s_special = "", s_lat = "", s_lon = "";
    String s_address_detail = "";
    private String respPart1, respPart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = SelectLocation.this;
        APP.setWindowsProperties(m_activity, true);
        if (getIntent().hasExtra("coming_item"))
            coming_item = (MyAddressesItem) getIntent().getSerializableExtra("coming_item");

        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_select_location);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_txt_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(getString(R.string.s_select_location));

        MyTextView tv_left = (MyTextView) findViewById(R.id.tv_left);
        tv_left.setText(getString(R.string.s_done));
        tv_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        MyTextView tv_done = (MyTextView) findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                s_title = et_title.getText().toString();
                s_address = et_address.getText().toString();
                s_city = et_city.getText().toString();
                s_subcity = et_subcity.getText().toString();
                s_special = et_special.getText().toString();

                if (loc != null) {

                    s_lat = "" + loc.latitude;
                    s_lon = "" + loc.longitude;

                    if (s_title.length() > 0) {
                        if (s_address.length() > 0) {
                            if (s_city.length() > 0) {
                                if (s_subcity.length() > 0) {

                                    s_address_detail = s_title + "[#]" + s_address + "[#]" + s_city + "[#]" + s_subcity + "[#]" + s_special;

                                    pd.show();
                                    new Connection2().execute();

                                } else
                                    APP.show_status(m_activity, 2, getString(R.string.s_please_enter_x, getString(R.string.s_subcity)));
                            } else
                                APP.show_status(m_activity, 2, getString(R.string.s_please_enter_x, getString(R.string.s_city)));
                        } else
                            APP.show_status(m_activity, 2, getString(R.string.s_please_enter_x, getString(R.string.s_address)));
                    } else
                        APP.show_status(m_activity, 2, getString(R.string.s_please_enter_x, getString(R.string.s_title_home_work)));
                } else
                    APP.show_status(m_activity, 2, getString(R.string.s_please_select_location));


            }
        });

        if (googleHarita == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.haritafragment1))
                    .getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {
                            googleHarita = googleMap;
                            if (googleHarita != null) {

                                new Handler().postDelayed(new Runnable() {

                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void run() {
                                        if (APP.mLocationManager != null) {

                                            double lat = APP.lat;
                                            double lon = APP.lon;
                                            LatLng myLocation = new LatLng((lat), (lon));

                                            if (googleHarita != null) {
                                                googleHarita.setMyLocationEnabled(true);
                                                googleHarita.getUiSettings().setZoomControlsEnabled(false);
                                                googleHarita.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14.0f));
                                            }

                                        } else {
                                            pd.dismiss();
                                            settingsAlert();

                                        }

                                        googleHarita.setOnCameraChangeListener(new OnCameraChangeListener() {

                                            @Override
                                            public void onCameraChange(CameraPosition position) {

                                                loc = position.target;

                                                new ReverseGeocodingTask().execute(position.target);


                                            }
                                        });

                                    }
                                }, 1000);

                            }
                        }
                    });

        }

        et_title = (EditText) findViewById(R.id.et_title);
        et_address = (EditText) findViewById(R.id.et_address);
        et_city = (EditText) findViewById(R.id.et_city);
        et_subcity = (EditText) findViewById(R.id.et_subcity);
        et_special = (EditText) findViewById(R.id.et_special);

        if (coming_item != null) {

            s_title = coming_item.getTitle();
            s_address = coming_item.getAddress();
            s_city = coming_item.getCity();
            s_subcity = coming_item.getSub_city();
            s_special = coming_item.getSpecial();

            et_title.setText(s_title);
            et_address.setText(s_address);
            et_city.setText(s_city);
            et_subcity.setText(s_subcity);
            et_special.setText(s_special);
        }

    }

    private void settingsAlert() {

        final MyAlertDialog alert = new MyAlertDialog(m_activity, R.drawable.ic_nearby,
                getString(R.string.s_select_location), getString(R.string.s_select_location_exp),
                getString(R.string.s_go_to_settings), getString(R.string.s_cancel), true);

        alert.setNegativeClicl(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss();

            }
        });
        alert.setPositiveClicl(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                m_activity.startActivity(intent);
            }
        });
        alert.show();
    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, Address> {
        double _latitude, _longitude;

        @Override
        protected Address doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(m_activity);
            _latitude = params[0].latitude;
            _longitude = params[0].longitude;

            List<Address> addresses = null;
            Address returnedAddress = null;

            try {
                addresses = geocoder.getFromLocation(_latitude, _longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                returnedAddress = addresses.get(0);

            } else {
                returnedAddress = null;
            }

            return returnedAddress;
        }

        @Override
        protected void onPostExecute(final Address returnedAddress) {
            m_activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (returnedAddress != null) {
                        s_address = "";
                        s_city = "";
                        s_subcity = "";

                        StringBuilder strReturnedAddress = new StringBuilder("");
                        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex() + 1; i++) {
                            if (returnedAddress.getMaxAddressLineIndex() == (i - 1)) {
                                strReturnedAddress.append(returnedAddress.getAddressLine(i));
                            } else {
                                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                            }
                            s_city = returnedAddress.getAdminArea();
                            s_subcity = returnedAddress.getSubAdminArea();
                        }
                        s_address = strReturnedAddress.toString();

                        et_city.setText(s_city);
                        et_subcity.setText(s_subcity);
                        et_address.setText(s_address);
                    }
                }

            });

        }
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            String xml = "";

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));

            if (coming_item != null) {

                nameValuePairs.add(new Pair<>("param2", APP.base64Encode(coming_item.getId())));
                nameValuePairs.add(new Pair<>("param3", APP.base64Encode(s_address_detail)));
                nameValuePairs.add(new Pair<>("param4", APP.base64Encode(s_lat)));
                nameValuePairs.add(new Pair<>("param5", APP.base64Encode(s_lon)));
                nameValuePairs.add(new Pair<>("param6", APP.base64Encode(APP.language_id)));
                nameValuePairs.add(new Pair<>("param7", APP.base64Encode("A")));

                xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/send_update_address_request.php");
            } else {

                nameValuePairs.add(new Pair<>("param2", APP.base64Encode(s_address_detail)));
                nameValuePairs.add(new Pair<>("param3", APP.base64Encode(s_lat)));
                nameValuePairs.add(new Pair<>("param4", APP.base64Encode(s_lon)));
                nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.language_id)));
                nameValuePairs.add(new Pair<>("param6", APP.base64Encode("A")));

                xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/send_add_address_request.php");
            }

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        respPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        respPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (respPart1.contentEquals("OK")) {
                        return "true";
                    } else if (respPart1.contentEquals("FAIL")) {
                        return "error";
                    } else {
                        return "hata";
                    }

                } catch (Exception e) {
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
            if (result.contentEquals("true")) {
                APP.show_status(m_activity, 0, respPart2);
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, respPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }
}
