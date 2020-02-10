package tr.limonist.kudra.app.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import tr.limonist.kudra.R;
import tr.limonist.classes.FavoriteItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;

public class Favorites extends Activity {

    ArrayList<FavoriteItem> results;
    Activity m_activity;
    TransparentProgressDialog pd;
    RefreshLayout refreshLayout;
    String[] part1;
    JazzyGridView list;
    lazy adapter;
    private String selected_product_id = "";
    private String favPart1, favPart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Favorites.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);
        String title = getIntent().getStringExtra("title");

        setContentView(R.layout.z_layout_gridview);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setAllCaps(true);
        tv_baslik.setText(title);

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Connection().execute();
            }
        });

        list = (JazzyGridView) findViewById(R.id.list);
        list.setNumColumns(2);

        results = new ArrayList<>();
        adapter = new lazy();
        list.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pd.show();
        new Connection().execute();
    }

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_favorites_data_list.php");

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
                            FavoriteItem pi = new FavoriteItem(
                                    temp.length > 0 ? temp[0] : "", temp.length > 1 ? temp[1] : "",
                                    temp.length > 2 ? temp[2] : "", temp.length > 3 ? temp[3] : "",
                                    temp.length > 4 ? temp[4] : ""
                            );
                            results.add(pi);
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
                adapter.notifyDataSetChanged();
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
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
        public FavoriteItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            SimpleDraweeView img;
            ImageView img_play, img_favorite;
            MyTextView title, price;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final FavoriteItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_favorites, null);

                holder.img_favorite = view.findViewById(R.id.img_favorite);
                holder.img = view.findViewById(R.id.img);
                holder.img_play = view.findViewById(R.id.img_play);
                holder.title = view.findViewById(R.id.title);
                holder.price = view.findViewById(R.id.price);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.img.setImageURI(item.getImage());
            holder.title.setText(item.getName());
            holder.price.setText(item.getPrice());


            holder.img_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.img_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_product_id = item.getProduct();
                    pd.show();
                    new Connection2().execute();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(m_activity,ProductDetail.class)
                            .putExtra("product_id",item.getProduct())
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

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/send_add_to_favorite_request.php");

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
               new Connection().execute();
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