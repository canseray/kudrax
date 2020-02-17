package tr.limonist.kudra.app.shop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyGridView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.MyVideoDialog;
import tr.limonist.kudra.R;
import tr.limonist.classes.ProductItem;
import tr.limonist.classes.MainItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.app.cart.Cart;
import tr.limonist.views.MyNotificationDialog;

public class Products extends FragmentActivity {

    ArrayList<MainItem> results_categories;
    ArrayList<ProductItem> results;
    RefreshLayout refreshLayout;
    JazzyGridView list;
    Activity m_activity;
    MyTextView badge_right;
    private TransparentProgressDialog pd;
    RecyclerView hl_main,hl_sub;
    private int selected_page = 0, selected_main_page = 0, selected_category_id;
    private String[] part1,part2;
    private String selected_sub_category_id="0";
    private String selected_product_id="0";
    private String favPart1,favPart2;
    private lazy adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Products.this;
        APP.setWindowsProperties(m_activity, true);
        //selected_main_page = getIntent().getIntExtra("selected_main_page",0);
       // selected_category_id = getIntent().getIntExtra("selected_category_id",0);

        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_layout_gridview_products);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_img_with_badge);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(getString(R.string.s_products));

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        RelativeLayout top_right = findViewById(R.id.top_right);
        top_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(m_activity, Cart.class));
            }
        });

        ImageView img_right = findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable.box_k);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        badge_right = findViewById(R.id.badge_right);
        badge_right.setBackgroundResource(R.drawable.but_circle_black1);
        badge_right.setTextColor(getResources().getColor(R.color.a_white11));

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new ConnectionSub().execute();
            }
        });

        list = (JazzyGridView) findViewById(R.id.list);
        list.setNumColumns(2);


        selected_page = 0;
        selected_sub_category_id = "0";

        results = new ArrayList<>();
        results_categories = new ArrayList<>();

        results = new ArrayList<>();
        adapter = new lazy();
        list.setAdapter(adapter);

        pd.show();
        new ConnectionSub().execute();

    }

    class ConnectionSub extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            //nameValuePairs.add(new Pair<>("param2", APP.base64Encode(String.valueOf(selected_category_id))));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode("80")));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));


            String xml = APP.post1(nameValuePairs, APP.path + "/get_product_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                    }


                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            ProductItem ai = new ProductItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "", temp.length > 6 ? temp[6] : "",
                                    temp.length > 7 ? temp[7] : "", temp.length > 8 ? temp[8] : "");
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

                adapter = new lazy();
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                APP.show_status(m_activity, 1, getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    public class lazy extends BaseAdapter {
        LayoutInflater inflater = null;

        public lazy() {
            inflater = LayoutInflater.from(m_activity);
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public ProductItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            SimpleDraweeView img;
            SimpleDraweeView img_play, img_favorite;
            MyTextView title;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ProductItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_products, null);

                holder.img_favorite = view.findViewById(R.id.img_favorite);
                holder.img = view.findViewById(R.id.img);
                holder.img_play = view.findViewById(R.id.img_play);
                holder.title = view.findViewById(R.id.title);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.img.setImageURI(item.getImage());
            holder.title.setText(item.getName());

          /*  if(!item.getVideo().contentEquals(""))
                holder.img_play.setImageResource(R.drawable.play_k);
            else
                holder.img_play.setImageResource(R.drawable.play_k); */

           /* holder.img_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!item.getVideo().contentEquals(""))
                    {
                        startActivity(new Intent(m_activity, MyVideoDialog.class).putExtra("url",item.getVideo()));
                    }
                }
            }); */

           /* if(!item.getFavorite().contentEquals("0"))
                holder.img_favorite.setImageResource(R.drawable.fav_flower_k);
            else
                holder.img_favorite.setImageResource(R.drawable.fav_flower_k); */

            holder.img_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_product_id = item.getId();
                    pd.show();
                    new Connection2().execute();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(m_activity,ProductDetail.class)
                            .putExtra("product_id",item.getId())
                            .putExtra("product_title",item.getName())
                    );
                }
            });

            return view;
        }
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_product_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/favorities/send_add_to_favorite_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        favPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        favPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (favPart1.contentEquals("OK")) {
                        return "true";
                    } else if (favPart1.contentEquals("FAIL")) {
                        return "error";
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

        protected void onPostExecute(String result) {

            if (result.contentEquals("true")) {

            } else if (result.contentEquals("error")) {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 2, favPart2);
            } else {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }
}
