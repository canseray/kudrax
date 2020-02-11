package tr.limonist.kudra.app.profile;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class MyInvoices extends AppCompatActivity {
    Activity m_activity;
    TransparentProgressDialog pd;
    LinearLayout top_left;
    MyTextView tv_baslik;
    ImageView img_left;
    RefreshLayout refreshLayout;
    JazzyListView list;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = MyInvoices.this;
        APP.setWindowsProperties(m_activity, true);
        title = getIntent().getStringExtra("title");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_my_invoices);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(title);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));

        img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                // Connection().execute();
            }
        });

        top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        list = (JazzyListView) findViewById(R.id.list);
        list.setTransitionEffect(JazzyHelper.TILT);
        list.setEmptyView(findViewById(android.R.id.empty));
        list.setDivider(new ColorDrawable(Color.BLACK));
        list.setDividerHeight(0);
    }
}
