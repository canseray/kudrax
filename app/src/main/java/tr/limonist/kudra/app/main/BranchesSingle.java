package tr.limonist.kudra.app.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.CirclePageIndicator;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.BranchesItem;
import tr.limonist.extras.AutoScrollViewPager;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyCallDialog;
import tr.limonist.views.MyZoomImageDialog;

public class BranchesSingle extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TransparentProgressDialog pd;
    public String part3;
    public String[] part2,part1;
    private Activity m_activity;
    private AutoScrollViewPager pager;
    private CirclePageIndicator indicator;
    String selected_branch_id ="";
    BranchesItem main_item;
    private TextView title,adres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = BranchesSingle.this;
        APP.setWindowsProperties(m_activity, true);
        selected_branch_id = getIntent().getStringExtra("branch_id");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_contact_single);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        pager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(getString(R.string.s_contact));

        ImageView img_left = findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = findViewById(R.id.title);
        adres = findViewById(R.id.adres);

        ImageView img_path = (ImageView) findViewById(R.id.img_path);
        img_path.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String uri = String.format(Locale.ENGLISH, part3,
                        getString(R.string.app_name));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(m_activity, "Google Haritalar yüklü değil...", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });

        ImageView img_tel = (ImageView) findViewById(R.id.img_tel);
        img_tel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new MyCallDialog(m_activity,main_item.getPhone());
            }
        });

        ImageView img_mail = (ImageView) findViewById(R.id.img_mail);
        img_mail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] emails = new String[1];
                emails[0] = main_item.getEmail();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, emails);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Mobile");
                startActivity(Intent.createChooser(intent,getString(R.string.s_select)));
            }
        });

        pd.show();
        new Connection().execute("IMAGE_IDS");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        LatLng dest = new LatLng(Double.parseDouble(main_item.getLat()), Double.parseDouble(main_item.getLng()));
        Marker marker = mMap.addMarker(new MarkerOptions().position(dest).title(main_item.getName()).snippet(main_item.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.holder_map)));


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 12.0f));

    }

    private void component() {

        if (pager != null) {
            pager.startAutoScroll(5000);
            pager.setScrollDurationFactor(5);
            pager.setInterval(5000);
            pager.setAdapter(new MainAdapter());

            indicator.setViewPager(pager);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        title.setText(main_item.getName());
        adres.setText(main_item.getAddress());

    }

    private class MainAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi;
            vi = new FrameLayout(m_activity);
            vi = inflater.inflate(R.layout.c_item_slide, null);
            SimpleDraweeView iv = vi.findViewById(R.id.img);
            iv.setImageURI(part2[position]);

            container.addView(vi);

            vi.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    MyZoomImageDialog alert = new MyZoomImageDialog(m_activity, main_item.getName(), part2[position], null, true);
                    alert.show();

                }
            });
            return vi;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView((FrameLayout) obj);
        }

        @Override
        public int getCount() {
            return part2.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_branch_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_contact_information_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[#\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part2")).split("\\[#\\]");
                        part3 = APP.base64Decode(APP.getElement(parse, "part3"));
                    }

                    if (!part1[0].contentEquals("")) {
                        main_item = new BranchesItem(
                                part1.length > 0 ? part1[0] : "", part1.length > 1 ? part1[1] : "",
                                part1.length > 2 ? part1[2] : "", part1.length > 3 ? part1[3] : "",
                                part1.length > 4 ? part1[4] : "", part1.length > 5 ? part1[5] : "",
                                part1.length > 6 ? part1[6] : "", part1.length > 7 ? part1[7] : ""
                        );
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
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {
                component();
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    class MyInfoWindowAdapter implements InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.z_info_window_layout, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = myContentsView.findViewById(R.id.tv_name);
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = myContentsView.findViewById(R.id.tv_info);
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
