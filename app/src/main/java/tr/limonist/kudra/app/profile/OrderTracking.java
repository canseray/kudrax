package tr.limonist.kudra.app.profile;

import android.app.Activity;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.OrderHistoryItem;
import tr.limonist.extras.DirectionsJSONParser;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyCallDialog;

public class OrderTracking extends FragmentActivity {

    private GoogleMap googleHarita;
    Marker marker;
    public double dlat;
    public double dlon;
    LatLng finish_position;
    TransparentProgressDialog pd;
    private Activity m_activity;
    OrderHistoryItem item;
    private String[] part1, part4;
    String part2, part3, part5;
    MyTextView tv_time, tv_distance,driver,detail;
    private String qurierPart1,qurierPart2;
    double driverLat,driverLon;
    private LatLng first_position,last_position;
    private MarkerOptions options3;
    private Marker marker3;
    private SimpleDraweeView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = OrderTracking.this;
        APP.setWindowsProperties(m_activity, true);
        item = (OrderHistoryItem) getIntent().getSerializableExtra("item");
        setContentView(R.layout.z_order_tracking);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        pd = new TransparentProgressDialog(m_activity, "", true);
        pd.show();

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.b_ic_close_black);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(getString(R.string.s_your_delivery));

        ImageView img_call = findViewById(R.id.img_call);
        img_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyCallDialog(m_activity,part1[3]);
            }
        });

        tv_time = (MyTextView) findViewById(R.id.tv_time);
        tv_distance = (MyTextView) findViewById(R.id.tv_distance);

        img = (SimpleDraweeView) findViewById(R.id.img);
        driver =(MyTextView)findViewById(R.id.driver);
        detail =(MyTextView)findViewById(R.id.detail);

        pd.show();
        new Connection().execute();

    }

    private void component() {

        img.setImageURI(part1[2]);
        driver.setText(part1[0]);
        detail.setText(part1[1]);

        dlat = Double.parseDouble(part4[0]);
        dlon = Double.parseDouble(part4[1]);
        marker = null;

        if (googleHarita == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.haritafragment1))
                    .getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {
                            googleHarita = googleMap;
                            if (googleHarita != null) {
                                googleHarita.getUiSettings().setZoomControlsEnabled(false);
                                finish_position = new LatLng(dlat, dlon);
                                marker = googleHarita.addMarker(new MarkerOptions().position(finish_position).title(getString(R.string.s_your_address)).icon(BitmapDescriptorFactory.fromResource(R.drawable.holder_map)));
                            }
                        }
                    });

        }

        pd.dismiss();

        if (!part5.contentEquals(""))
            APP.show_status(m_activity, 2, part5);

        first_time = true;

        new Connection2().execute();

    }
    int REFRESH_INTERVAL=5;

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(item.getId())));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(item.getDriver_id())));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(item.getAddress_id())));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/check_order_following_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[#\\]",-1);
                        part4 = APP.base64Decode(APP.getElement(parse, "part4")).split("\\[#\\]",-1);

                        part2 = APP.base64Decode(APP.getElement(parse, "part2"));
                        part3 = APP.base64Decode(APP.getElement(parse, "part3"));
                        part5 = APP.base64Decode(APP.getElement(parse, "part5"));

                    }

                    if (!part1[0].contentEquals("")) {

                        try {
                            REFRESH_INTERVAL = Integer.parseInt(part2);
                        } catch (Exception e) {}

                        return "true";
                    } else
                        return "error";

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }

        }

        protected void onPostExecute(String result) {
            if (result.contentEquals("true")) {
                component();
            } else {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(item.getDriver_id())));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(item.getId())));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/get_order_driver_current_location.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        qurierPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        qurierPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }
                    try {
                        driverLat = Double.parseDouble(qurierPart1);
                        driverLon = Double.parseDouble(qurierPart2);
                        return "true";
                    } catch (Exception e) {
                        return "false";
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
            if (result.contentEquals("true")) {

                try {
                    if (first_time)
                        setMain();
                    else
                        setMain2();

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Connection2().execute();
                }
            },REFRESH_INTERVAL*1000);
        }
    }

    boolean first_time = true;

    private void setMain() {

        first_time = false;
        first_position = new LatLng(driverLat,driverLon);
        last_position = new LatLng(driverLat,driverLon);

        options3 = new MarkerOptions();
        options3.position(first_position);
        options3.icon(BitmapDescriptorFactory.fromResource(R.drawable.holder_map_driver));
        marker3 = googleHarita.addMarker(options3);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(finish_position);
                builder.include(first_position);

                LatLngBounds bounds = builder.build();
                int padding = 150; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleHarita.animateCamera(cu);
            }
        }, 1000);

    }

    private void setMain2() {

        first_position = last_position;
        last_position = new LatLng(driverLat, driverLon);

        String url = part3.replace("PLC1",(""+last_position.latitude+","+last_position.longitude));
        url = url.replace("PLC2",(""+finish_position.latitude+","+finish_position.longitude));
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    public String dist;
    public String time;

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null)
                pd.dismiss();

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject = new JSONObject(result.toString());
                JSONArray array = jsonObject.getJSONArray("routes");
                JSONObject routes = array.getJSONObject(0);
                JSONArray legs = routes.getJSONArray("legs");
                JSONObject steps = legs.getJSONObject(0);
                JSONObject distance = steps.getJSONObject("distance");
                JSONObject duration = steps.getJSONObject("duration");

                dist = distance.getString("text");
                time = duration.getString("text");
                if (duration.getString("text").contains("mins"))
                    time = time.replace("mins", "dakika");
                time = time.replace("min", "dakika");
                if (duration.getString("text").contains("hours"))
                    time = time.replace("hours", "saat");
                time = time.replace("hour", "saat");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ParserTask parserTask = new ParserTask();

            if (lineOptions != null)
                lineOptions.visible(false);

            parserTask.execute(result);

        }
    }

    PolylineOptions lineOptions;

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception while url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(first_position);
            builder.include(finish_position);

            final LatLngBounds bounds = builder.build();
            final int padding = 200;
            animateMarker(marker3, last_position, false);
            final Handler handler = new Handler();
            handler.postDelayed( new Runnable() {
                public void run() {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleHarita.animateCamera(cu);

                    tv_time.setText(time);
                    tv_distance.setText(dist);

                }
            }, 250);

        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleHarita.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
