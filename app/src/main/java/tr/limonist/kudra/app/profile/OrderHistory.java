package tr.limonist.kudra.app.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.OrderHistoryItem;
import tr.limonist.classes.OrderHistoryProductItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.views.MyContractDialog;

public class OrderHistory extends Activity {

    ArrayList<OrderHistoryItem> results;
    Activity m_activity;
    TransparentProgressDialog pd;
    LinearLayout top_left;
    String[] part1,part2;
    MyTextView tv_baslik;
    ImageView img_left;
    RefreshLayout refreshLayout;
    JazzyListView list;
    private lazy adapter;
    private ArrayList<OrderHistoryProductItem> results_products;
    private MyTextView title1,title2,title3;

    @Override
    protected void onResume() {
        super.onResume();
        pd.show();
        new Connection().execute("");
    }

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = OrderHistory.this;
        APP.setWindowsProperties(m_activity, true);
        title = getIntent().getStringExtra("title");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_layout_listview);

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
                new Connection().execute();
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
        list.setDivider(new ColorDrawable(getResources().getColor(R.color.a_brown11)));
        list.setDividerHeight(1);

        LayoutInflater layoutInflater = LayoutInflater.from(m_activity);
        View head = layoutInflater.inflate(R.layout.c_item_head_order_history, null);

        title1 = (MyTextView)head.findViewById(R.id.title1);
        title2 = (MyTextView)head.findViewById(R.id.title2);
        title3 = (MyTextView)head.findViewById(R.id.title3);

       // list.addHeaderView(head);

    }

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            results = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
           // nameValuePairs.add(new Pair<>("param1", APP.base64Encode("0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/get_order_history_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part2")).split("\\[#\\]");

                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            OrderHistoryItem ai = new OrderHistoryItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "",temp.length > 6 ? temp[6] : "",
                                    temp.length > 7 ? temp[7] : "",temp.length > 8 ? temp[8] : "",
                                    temp.length > 9 ? temp[9] : "");
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

                title1.setText(part2.length>0?part2[0]:"");
                title2.setText(part2.length>1?part2[1]:"");
                title3.setText(part2.length>2?part2[2]:"");

                adapter = new lazy(results);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                APP.show_status(m_activity, 1,
                        getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    public class lazy extends BaseAdapter {
        private ArrayList<OrderHistoryItem> data;
        private LayoutInflater inflater = null;

        public lazy(ArrayList<OrderHistoryItem> d) {
            this.data = d;
            inflater = LayoutInflater.from(m_activity);

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public OrderHistoryItem getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final OrderHistoryItem item = data.get(position);

            if (view == null) {
                view = inflater.inflate(R.layout.c_item_order_history_two, null);
                view.setTag("" + position);
            }


            MyTextView date = (MyTextView) view.findViewById(R.id.date);
            MyTextView number = (MyTextView) view.findViewById(R.id.number);
            MyTextView total = (MyTextView) view.findViewById(R.id.total);
            MyTextView status = (MyTextView) view.findViewById(R.id.status);
            MyTextView tv_payment_method = (MyTextView) view.findViewById(R.id.tv_payment_method);

            ImageView img_delivery = (ImageView) view.findViewById(R.id.img_delivery);
            img_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(m_activity,OrderTracking.class).putExtra("item",item));

                }
            });

            if(item.getStatus_id().contentEquals("5"))
                img_delivery.setVisibility(View.VISIBLE);
            else
                img_delivery.setVisibility(View.GONE);

            date.setText(item.getDate());
            number.setText(item.getOrder_id());
            status.setText(item.getStatus());

            //getStatus color red green

            total.setText(item.getTotal());
            tv_payment_method.setText(item.getOrder_payment_method());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MyContractDialog(m_activity, item.getDetail(),getString(R.string.s_detail));

                }
            });

            return view;
        }

    }

}