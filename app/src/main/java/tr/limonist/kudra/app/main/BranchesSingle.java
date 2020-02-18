package tr.limonist.kudra.app.main;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.BranchesItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;

public class BranchesSingle extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TransparentProgressDialog pd;
    public String part3;
    public String[] part2,part1;
    private Activity m_activity;
    BranchesItem store_item;
    private TextView title,adres, select_store, select_another_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = BranchesSingle.this;
        APP.setWindowsProperties(m_activity, true);
        store_item = (BranchesItem) getIntent().getSerializableExtra("store_item");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_contact_single);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(getString(R.string.s_another_store));

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
        select_store = findViewById(R.id.select_store);
        select_another_store = findViewById(R.id.select_another_store);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        title.setText(store_item.getName());
        adres.setText(store_item.getAddress());


        select_store.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity,BranchesDetail.class).putExtra("store_item_detail", store_item));
            }
        });


        select_another_store.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        LatLng dest = new LatLng(Double.parseDouble(store_item.getLat()), Double.parseDouble(store_item.getLng()));
        Marker marker = mMap.addMarker(new MarkerOptions().position(dest).title(store_item.getName()).snippet(store_item.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.holder_map)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 12.0f));

    }

}