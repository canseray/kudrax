package tr.limonist.kudra.app.cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.OrderItem;
import tr.limonist.classes.PaymentDetailItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.views.MyPaymentDetailDialog;
import tr.limonist.views.MyPromotionDialog;

public class Cart extends Activity {

    public String[] part1;
    private lazy adapter;
    ArrayList<OrderItem> results;
    ArrayList<PaymentDetailItem> results_payment;
    JazzyListView list;
    RefreshLayout refreshLayout;
    private Activity m_activity;
    private TransparentProgressDialog pd;
    boolean editing = false;
    private MyTextView tv_total;
    public static String part2;
    public String part3,part6,part5;
    private String[] part4;
    private String sendPart1,sendPart2;
    private String selected_count;
    View head,footer;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Cart.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);

        setContentView(R.layout.z_cart);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_img);
        stub.inflate();

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_black11));
        tv_baslik.setText(getString(R.string.s_your_cart));

        ImageView img_left = findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.b_ic_prew_black);

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        ImageView img_right = findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable.b_ic_delete_black);

        LinearLayout top_right = findViewById(R.id.top_right);
        top_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                editing = !editing;
                adapter.notifyDataSetChanged();
            }

        });

        ImageView img_detail = (ImageView) findViewById(R.id.img_detail);
        img_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MyPaymentDetailDialog(m_activity,results_payment);
            }
        });

        MyTextView tv_done = (MyTextView) findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!part3.contentEquals("0"))
                    startActivity(new Intent(m_activity,CheckOut.class).putExtra("total",part2).putExtra("currency",part5).putExtra("item_count",part3));

            }
        });

        list = (JazzyListView) findViewById(R.id.list);

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Connection().execute();
            }
        });

        tv_total = (MyTextView) findViewById(R.id.tv_total);

        LayoutInflater layoutInflater = LayoutInflater.from(m_activity);
        head = layoutInflater.inflate(R.layout.c_item_head_cart, null);

        TextView date = (TextView) head.findViewById(R.id.date);
        TextView title = (TextView) head.findViewById(R.id.title);

        title.setText(getString(R.string.s_products));

        String date_n = new SimpleDateFormat("dd MMMM EEEE", Locale.getDefault()).format(new Date());
        date.setText(date_n);

        footer = layoutInflater.inflate(R.layout.c_item_footer_cart, null);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyPromotionDialog(m_activity,getString(R.string.s_promotions),true,part2);
            }
        });

        results = new ArrayList<>();
        results_payment = new ArrayList<>();

        adapter = new lazy();
        list.setAdapter(adapter);

    }

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

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();
            results_payment = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/get_cart_product_data_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        part4 = APP.base64Decode(APP.getElement(parse, "part4")).split("\\[##\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part2"));
                        part3 = APP.base64Decode(APP.getElement(parse, "part3"));
                        part5 = APP.base64Decode(APP.getElement(parse, "part5"));
                        part6 = APP.base64Decode(APP.getElement(parse, "part6"));

                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            OrderItem ai = new OrderItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "",temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "", temp.length > 6 ? temp[6] : "");
                            results.add(ai);
                        }

                    }

                    if (!part4[0].contentEquals("")) {

                        for (int i = 0; i < part4.length; i++) {
                            String[] temp = part4[i].split("\\[#\\]");
                            PaymentDetailItem ai = new PaymentDetailItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "");
                            results_payment.add(ai);
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

                fillComponents();

            } else {
                APP.show_status(m_activity, 1, getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private void fillComponents() {

        if(head!=null)
        {
            list.removeHeaderView(head);
            list.removeFooterView(footer);

            list.addHeaderView(head);
            list.addFooterView(footer);
        }

        tv_total.setText(part2+" "+part5);

        adapter.notifyDataSetChanged();
        if(!part6.contentEquals(""))
            APP.show_status(m_activity,2,part6);

    }

    protected String selected_item_id;
    public String respPart1;
    public String respPart2;

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
        public OrderItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            MyTextView title, count, price;
            ImageView img_remove;
            SimpleDraweeView img;
            LinearLayout lay_minus,lay_plus;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final OrderItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_order, null);

                holder.title = (MyTextView) view.findViewById(R.id.title);
                holder.count = (MyTextView) view.findViewById(R.id.count);
                holder.price = (MyTextView) view.findViewById(R.id.price);

                holder.img_remove = (ImageView) view.findViewById(R.id.img_remove);
                holder.img = (SimpleDraweeView) view.findViewById(R.id.img);

                holder.lay_minus = (LinearLayout) view.findViewById(R.id.lay_minus);
                holder.lay_plus = (LinearLayout) view.findViewById(R.id.lay_plus);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.title.setText(item.getName());
            holder.count.setText(item.getCount());
            holder.price.setText(item.getPrice());

            if (editing)
                holder.img_remove.setVisibility(View.VISIBLE);
            else
                holder.img_remove.setVisibility(View.GONE);

            holder.img.setImageURI(item.getImage());

            holder.img_remove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    selected_item_id = item.getId();
                    pd.show();
                    new Connection3().execute("");

                }
            });

            holder.lay_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double count = 1;

                    try {
                        count = Double.parseDouble(item.getAmount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (count >1) {

                        selected_item_id = item.getId();
                        selected_count =""+(count-1);
                        pd.show();
                        new Connection2().execute("");

                    }

                }
            });

            holder.lay_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double max_count = 100;
                    double count = 1;
                    try {
                        max_count = Double.parseDouble(item.getQuantity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        count = Double.parseDouble(item.getAmount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (count < max_count) {

                        selected_item_id = item.getId();
                        selected_count =""+(count+1);
                        pd.show();
                        new Connection2().execute("");

                    }

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
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(selected_count)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/add_or_update_item_in_cart.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        sendPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        sendPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (sendPart1.contentEquals("OK")) {
                        return "true";
                    } else if (sendPart1.contentEquals("FAIL")) {
                        return "error";
                    } else {
                        return "hata";
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
                new Connection().execute();
            } else if (result.contentEquals("error")) {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 2, sendPart2);
            } else {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    class Connection3 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_item_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/delete_selected_product_in_cart.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        respPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        respPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (respPart1.contentEquals("OK")) {
                        return "true";
                    } else if (respPart1.contentEquals("FAIL")) {
                        return "error";
                    } else {
                        return "hata";
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
                new Connection().execute("");
            } else if (result.contentEquals("error")) {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 2, respPart2);
            } else {
                if (pd != null)
                    pd.dismiss();
                APP.show_status(m_activity, 1,
                        getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}