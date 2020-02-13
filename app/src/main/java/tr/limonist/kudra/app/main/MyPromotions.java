package tr.limonist.kudra.app.main;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import tr.limonist.classes.PromotionItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.views.MyPromotionDialog;

public class MyPromotions extends AppCompatActivity {
    Activity m_activity;
    private TransparentProgressDialog pd;
    JazzyGridView list;
    RefreshLayout refreshLayout;
    LinearLayout top_left,lay_active_promotions,lay_used_promotions;
    MyTextView tv_baslik,tv_active_promotions, tv_used_promotions;
    View view_active_promotions, view_used_promotions;
    ImageView img_left;
    ArrayList<PromotionItem> results;
    String[] part_promotions_list;
    private String selected_product_id="0";
    private int selected_page = 0, selected_promotion_status = 0;
    private lazy adapter;
    boolean is_usable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = MyPromotions.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_my_promotions);


        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText("PROMOSYONLAR");
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));

        img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);


        top_left = (LinearLayout) findViewById(R.id.top_left);
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
             //   new Connection().execute();
            }
        });

        list = (JazzyGridView) findViewById(R.id.list);

        lay_active_promotions = findViewById(R.id.lay_active_promotions);
        tv_active_promotions = findViewById(R.id.tv_active_promotions);
        view_active_promotions = findViewById(R.id.view_active_promotions);
        lay_active_promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_promotion_status = 0;
                tv_used_promotions.setTextColor(getResources().getColor(R.color.a_brown14));
                view_used_promotions.setVisibility(View.INVISIBLE);

                tv_active_promotions.setTextColor(getResources().getColor(R.color.a_brown11));
                view_active_promotions.setVisibility(View.VISIBLE);

                tv_used_promotions.setTextColor(getResources().getColor(R.color.a_brown14));
                view_used_promotions.setVisibility(View.INVISIBLE);

                pd.show();
                new Connection().execute();

            }
        });

        lay_used_promotions = findViewById(R.id.lay_used_promotions);
        tv_used_promotions = findViewById(R.id.tv_used_promotions);
        tv_used_promotions.setTextColor(getResources().getColor(R.color.a_brown14));
        view_used_promotions = findViewById(R.id.view_used_promotions);
        view_used_promotions.setVisibility(View.INVISIBLE);
        lay_used_promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_promotion_status = 1;

                tv_used_promotions.setTextColor(getResources().getColor(R.color.a_brown11));
                view_used_promotions.setVisibility(View.VISIBLE);

                tv_active_promotions.setTextColor(getResources().getColor(R.color.a_brown14));
                view_active_promotions.setVisibility(View.INVISIBLE);

                pd.show();
                new Connection().execute();
            }
        });


        pd.show();
        new Connection().execute();

    }

    private class Connection extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... args) {
            results = new ArrayList<>();
            List<Pair<String, String>> nameValuePairs = new ArrayList<>();


            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(String.valueOf(selected_promotion_status)))); //request_status
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param7", APP.base64Encode(APP.language_id)));

            String xml = APP.post1(nameValuePairs, APP.path + "/promotions/get_promotions_data_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part_promotions_list = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                    }

                    if (!part_promotions_list[0].contentEquals("")) {

                        for (int i = 0; i < part_promotions_list.length; i++) {
                            String[] temp = part_promotions_list[i].split("\\[#\\]");
                            PromotionItem ai = new PromotionItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "", temp.length > 6 ? temp[6] : "",
                                    temp.length > 7 ? temp[7] : "", temp.length > 8 ? temp[8] : "",
                                    temp.length > 9 ? temp[9] : ""
                            );
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
            if (result.contentEquals("true")) {

                adapter = new lazy();
                list.setAdapter(adapter);
                 //adapter.notifyDataSetChanged();
            } else {
                APP.show_status(m_activity, 1, m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
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
        public PromotionItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            MyTextView promotion_product_name, show_code, promotion_count, date;
            SimpleDraweeView qr_code_img;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final PromotionItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_promotions, null);

                holder.promotion_product_name = (MyTextView) view.findViewById(R.id.promotion_product_name);
                holder.show_code = (MyTextView) view.findViewById(R.id.show_code);
                holder.promotion_count = (MyTextView) view.findViewById(R.id.promotion_count);
                holder.date = (MyTextView) view.findViewById(R.id.date);
                holder.qr_code_img = (SimpleDraweeView) view.findViewById(R.id.qr_code_img);


                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();            }

            holder.promotion_product_name.setText(item.getTitle());
            //holder.promotion_count.setText(item.getDetail());
            holder.date.setText(item.getDate());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String put_code_id = results.get(position).getId();
                    startActivity(new Intent(m_activity, MyPromotionCodeId.class).putExtra("code_id",put_code_id));
                }
            });

            holder.show_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String put_code =results.get(position).getCode();
                    startActivity(new Intent(m_activity, MyPromotionQrCode.class).putExtra("code",put_code));
                }
            });

            if (selected_promotion_status == 0 )
                holder.show_code.setVisibility(View.VISIBLE);
            else
                holder.show_code.setVisibility(View.GONE);

            return view;
        }
    }

}
