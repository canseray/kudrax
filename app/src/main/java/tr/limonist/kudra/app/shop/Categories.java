package tr.limonist.kudra.app.shop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.cart.Cart;
import tr.limonist.classes.MainItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;

public class Categories extends Activity {

    public String[] part1;
    private lazy adapter;
    public static ArrayList<MainItem> results;
    JazzyListView list;
    RefreshLayout refreshLayout;
    private Activity m_activity;
    private TransparentProgressDialog pd;
    MyTextView badge_right;
    private String part2;

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pd.show();
                new Connection().execute();

            }
        },250);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Categories.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);
        String title = getIntent().getStringExtra("title");
        setContentView(R.layout.z_layout_listview);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_img_with_badge_img2);
        stub.inflate();

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(title);

        ImageView img_left = findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);
/*
        ImageView img_right = findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable.b_ic_add_cart_black);

        ImageView img_right2 = findViewById(R.id.img_right2);
        img_right2.setImageResource(R.drawable.b_ic_search_black);

        RelativeLayout top_right = findViewById(R.id.top_right);
        top_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity, Cart.class));
            }
        }); */

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

      /*  LinearLayout top_right2 = findViewById(R.id.top_right2);
        top_right2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity, Search.class));
            }
        }); */

        badge_right = findViewById(R.id.badge_right);

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Connection().execute("");
            }
        });

        list = findViewById(R.id.list);
        list.setEmptyView(findViewById(android.R.id.empty));
        list.setDivider(new ColorDrawable(Color.TRANSPARENT));
        list.setDividerHeight(10);

        results = new ArrayList<>();
        adapter = new lazy();
        list.setAdapter(adapter);
    }

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_category_data_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            MainItem ai = new MainItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "", temp.length > 6 ? temp[6] : "",
                                    temp.length > 7 ? temp[7] : "");
                            results.add(ai);
                        }

                    }

                    return "true";

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
            if (refreshLayout != null)
                refreshLayout.finishRefresh();
            if (result.contentEquals("true")) {

                int count=0;

                try {
                    count=Integer.parseInt(part2);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if(count>0)
                {
                    badge_right.setVisibility(View.VISIBLE);
                    badge_right.setText(part2);
                }
                else
                    badge_right.setVisibility(View.GONE);

                adapter.notifyDataSetChanged();

            } else {
                APP.show_status(m_activity, 1, getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    public class lazy extends BaseAdapter {
        private LayoutInflater inflater = null;

        public lazy() {
            inflater = LayoutInflater.from(m_activity);

        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public MainItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final MainItem item = results.get(position);

            if (view == null) {
                view = inflater.inflate(R.layout.c_item_buy_now, null);
                view.setTag("" + position);
            }

            MyTextView title = (MyTextView) view.findViewById(R.id.title);
            SimpleDraweeView img = (SimpleDraweeView) view.findViewById(R.id.img);

            title.setText(item.getTitle());
            img.setImageURI(item.getImage());

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(m_activity,Products.class).putExtra("selected_main_page",position));
                }
            });

            return view;
        }
    }
}
