package tr.limonist.kudra.app.cart;

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
import android.widget.TextView;

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
import tr.limonist.classes.MyAddressesItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyAlertDialog;

public class MyAddresses extends Activity {

    public String[] part1;
    private lazy adapter;
    ArrayList<MyAddressesItem> results;
    JazzyListView list;
    RefreshLayout refreshLayout;
    private Activity m_activity;
    private TransparentProgressDialog pd;
    private String selected_item_id = "";
    private String removePart1, removePart2;

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
        m_activity = MyAddresses.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);
        String title = getIntent().getStringExtra("title");
        setContentView(R.layout.z_layout_listview);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_black11));
        tv_baslik.setText(title);

        ImageView img_left = findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.b_ic_prew_black);

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        LayoutInflater layoutInflater = LayoutInflater.from(m_activity);
        View footer = layoutInflater.inflate(R.layout.c_item_footer_my_addresses, null);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity,SelectLocation.class));
            }
        });

        list.addFooterView(footer);

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

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/get_address_data_list.php");

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
                            MyAddressesItem ai = new MyAddressesItem(temp.length > 0 ? temp[0] : "",
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
        public MyAddressesItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final MyAddressesItem item = results.get(position);

            if (view == null) {
                view = inflater.inflate(R.layout.c_item_my_address, null);
                view.setTag("" + position);
            }

            MyTextView title = (MyTextView) view.findViewById(R.id.title);
            TextView address = (TextView) view.findViewById(R.id.address);

            ImageView img_delete = (ImageView) view.findViewById(R.id.img_delete);
            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final MyAlertDialog alert = new MyAlertDialog(m_activity, R.drawable.b_ic_add_address,
                            getString(R.string.s_address_information), getString(R.string.s_address_remove_exp),
                            getString(R.string.s_confirm), getString(R.string.s_cancel), true);

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
                            selected_item_id = item.getId();
                            pd.show();
                            new Connection2().execute("");
                        }
                    });
                    alert.show();

                }
            });

            title.setText(item.getTitle());
            address.setText(item.getAddress());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(m_activity,SelectLocation.class).putExtra("coming_item",item));
                }
            });

            return view;
        }
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_item_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/send_remove_address_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        removePart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        removePart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (removePart1.contentEquals("OK"))
                        return "true";
                    else if (removePart1.contentEquals("FAIL"))
                        return "error";
                    else
                        return "hata";

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
            } else if (result.contentEquals("true")) {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 2, removePart2);
            } else {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 1, getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}
