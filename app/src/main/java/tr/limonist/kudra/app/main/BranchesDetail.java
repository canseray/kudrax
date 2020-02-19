package tr.limonist.kudra.app.main;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import tr.limonist.classes.BranchesItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class BranchesDetail extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TransparentProgressDialog pd;
    public String part3;
    public String[] part2,part1;
    private Activity m_activity;
    BranchesItem store_item_detail;
    private TextView title,adres, phone, email;
    SimpleDraweeView store_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = BranchesDetail.this;
        APP.setWindowsProperties(m_activity, true);
        store_item_detail = (BranchesItem) getIntent().getSerializableExtra("store_item_detail");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_branches_detail);

        title = findViewById(R.id.title);
        adres = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        store_img = findViewById(R.id.store_img);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(getString(R.string.s_another_store));

        ImageView img_left = findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        title.setText(store_item_detail.getName());
        adres.setText(store_item_detail.getAddress());
        phone.setText(store_item_detail.getPhone());
        email.setText(store_item_detail.getEmail());
        store_img.setImageURI(Uri.parse(store_item_detail.getImage()));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        LatLng dest = new LatLng(Double.parseDouble(store_item_detail.getLat()), Double.parseDouble(store_item_detail.getLng()));
        Marker marker = mMap.addMarker(new MarkerOptions().position(dest).title(store_item_detail.getName()).snippet(store_item_detail.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.holder_map)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 12.0f));
    }
}
