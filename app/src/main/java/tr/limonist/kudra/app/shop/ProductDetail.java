package tr.limonist.kudra.app.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.twotoasters.jazzylistview.JazzyListView;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.BannerItem;
import tr.limonist.classes.MainItem;
import tr.limonist.classes.NotificationsItem;
import tr.limonist.classes.ProductAllPricaData;
import tr.limonist.classes.ProductCommentData;
import tr.limonist.classes.ProductTempList;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.cart.Cart;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.app.main.Main;
import tr.limonist.views.MyImageDialog;
import tr.limonist.views.MyNotificationDialog;
import tr.limonist.views.MyZoomImageDialog;

public class ProductDetail extends Activity {

    Activity m_activity;
    TransparentProgressDialog pd;
    String[] part1;
    String product_id = "0",product_title="";
    TextView badge_right;
    SimpleDraweeView img;
    private SimpleDraweeView spark;
    private TextView tv_title,tv_total_bottom;
    private TextView tv_count;
    private TextView tv_total;
    private LinearLayout lay_minus;
    private LinearLayout lay_plus;
    private LinearLayout lay_cart, lay_add_comment,hl_sub;
    private String sendPart1,sendPart2;
    private String favPart1,favPart2;
    private String comPart1,comPart2;
    private String[] temp_list, all_price_data, all_comment_data;
    ArrayList<ProductTempList> result_temp_list;
    ArrayList<ProductAllPricaData> result_all_price_data;
    ArrayList<ProductCommentData> result_comment_data;
    TextView gramaj_one, gramaj_two, price_one, price_two, tv_count_top, tv_desc, tv_content;
    TextView tab_desc, tab_content, tab_comment, add_comment_title;
    int product_count = 0;
    double total_price;
    AnimCheckBox checkbox_one, checkBox_two;
    JazzyListView list_comment;
    private lazy adapter;
    ImageView add_comment;
    String comment_text = "";
    EditText comment_layout;
    private String part_cart_item_count = "10";


    //spark btn yerine share

    @Override
    protected void onResume() {
        super.onResume();
        pd.show();
        new Connection().execute("");
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = ProductDetail.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);
        if (getIntent().hasExtra("product_id"))
            product_id = getIntent().getStringExtra("product_id");
        if (getIntent().hasExtra("product_title"))
            product_title = getIntent().getStringExtra("product_title");

        setContentView(R.layout.z_layout_product_detail);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_img_with_badge);
        stub.inflate();

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(getString(R.string.s_product_detail));

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        ImageView img_left = findViewById(R.id.img_left);
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

        badge_right = findViewById(R.id.badge_right);
        badge_right.setBackgroundResource(R.drawable.but_circle_black1);
        badge_right.setTextColor(getResources().getColor(R.drawable.text_white));

        gramaj_one = findViewById(R.id.gramaj_one);
        gramaj_two = findViewById(R.id.gramaj_two);
        price_one = findViewById(R.id.price_one);
        price_two = findViewById(R.id.price_two);
        tv_count_top = findViewById(R.id.tv_count_top);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_total = (TextView) findViewById(R.id.tv_total);
        spark = (SimpleDraweeView) findViewById(R.id.spark);
        tv_total_bottom = findViewById(R.id.tv_total_bottom);
        tv_title = (TextView) findViewById(R.id.tv_title);
        checkbox_one = findViewById(R.id.checkbox_one);
        checkBox_two = findViewById(R.id.checkbox_two);
        tv_desc = findViewById(R.id.tv_desc);
        tv_content = findViewById(R.id.tv_content);
        list_comment = findViewById(R.id.list_comment);
        tab_desc = findViewById(R.id.tab_desc);
        tab_content = findViewById(R.id.tab_content);
        tab_comment = findViewById(R.id.tab_comment);
        add_comment = findViewById(R.id.add_comment);
        comment_layout = findViewById(R.id.comment_layout);
        lay_add_comment = findViewById(R.id.lay_add_comment);
        hl_sub = findViewById(R.id.hl_sub);
        add_comment_title = findViewById(R.id.add_comment_title);

        add_comment_title.setVisibility(View.GONE);
        tv_count_top.setText("");

        img = findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MyZoomImageDialog(m_activity, product_title,result_temp_list.get(0).getProduct_image(), null, true).show();
            }
        });

        lay_minus = (LinearLayout) findViewById(R.id.lay_minus);
        lay_minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!checkbox_one.isChecked() && !checkBox_two.isChecked()){

                    final MyImageDialog dia = new MyImageDialog(m_activity,"",
                            "Lütfen ilk olarak satın almak istediğiniz ürün gramajını seçiniz.","Tamam",true);

                    dia.setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dia.dismiss();

                        }
                    });

                    dia.show();

                } else if (checkbox_one.isChecked() && checkBox_two.isChecked()){

                    final MyImageDialog dia = new MyImageDialog(m_activity,"",
                            "Lütfen tek bir ürün gramajı seçiniz.","Tamam",true);

                    dia.setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dia.dismiss();
                        }
                    });

                    dia.show();

                } else {
                    tv_count_top.setBackground(null);
                    if (product_count > 1){
                        --product_count;
                        tv_count.setText(String.valueOf(product_count));
                        tv_count_top.setText(String.valueOf(product_count));
                        fillPrice();
                    }
                }


            }

        });

        lay_plus = (LinearLayout) findViewById(R.id.lay_plus);
        lay_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (!checkbox_one.isChecked() && !checkBox_two.isChecked()){

                    final MyImageDialog dia = new MyImageDialog(m_activity,"",
                            "Lütfen ilk olarak satın almak istediğiniz ürün gramajını seçiniz.","Tamam",true);

                    dia.setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dia.dismiss();
                        }
                    });

                    dia.show();

                } else if (checkbox_one.isChecked() && checkBox_two.isChecked()){

                    final MyImageDialog dia = new MyImageDialog(m_activity,"",
                            "Lütfen tek bir ürün gramajı seçiniz.","Tamam",true);

                    dia.setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dia.dismiss();
                        }
                    });

                    dia.show();

                } else {

                    tv_count_top.setBackground(null);
                    ++product_count;
                    tv_count.setText(String.valueOf(product_count));
                    tv_count_top.setText(String.valueOf(product_count));
                    fillPrice();
                }

            }

        });

        lay_cart = (LinearLayout) findViewById(R.id.lay_cart);
        lay_cart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (product_count < 1){

                    final MyImageDialog dia = new MyImageDialog(m_activity,"",
                            "Lütfen ürün seçiniz.","Tamam",true);

                    dia.setOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dia.dismiss();
                        }
                    });

                    dia.show();

                }  else {

                    pd.show();
                    new Connection2().execute();
                }

            }

        });

        lay_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    comment_text = comment_layout.getText().toString();
                    new Connection3().execute();




            }
        });


    }



    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            result_temp_list = new ArrayList<>();
            result_all_price_data = new ArrayList<>();
            result_comment_data = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(product_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_product_detail.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        temp_list = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        all_price_data = APP.base64Decode(APP.getElement(parse, "part2")).split("\\[##\\]");
                        all_comment_data = APP.base64Decode(APP.getElement(parse, "part3")).split("\\[##\\]");

                    }

                    if (!temp_list[0].contentEquals("")) {

                        for (int i = 0; i < temp_list.length; i++){
                            String[] temp = temp_list[i].split("\\[#\\]");
                            ProductTempList ai = new ProductTempList(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "",
                                    temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "",
                                    temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "",
                                    temp.length > 6 ? temp[6] : "",
                                    temp.length > 7 ? temp[7] : "");
                            result_temp_list.add(ai);
                        }

                        if (!all_price_data[0].contentEquals("")) {

                            for (int i = 0; i < all_price_data.length; i++) {
                                String[] temp = all_price_data[i].split("\\[#\\]");
                                ProductAllPricaData ai = new ProductAllPricaData(temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "",
                                        temp.length > 2 ? temp[2] : "",
                                        temp.length > 3 ? temp[3] : "");
                                result_all_price_data.add(ai);
                            }

                        }

                        if (!all_comment_data[0].contentEquals("")) {

                            for (int i = 0; i < all_comment_data.length; i++) {
                                String[] temp = all_comment_data[i].split("\\[#\\]");
                                ProductCommentData ai = new ProductCommentData(temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "",
                                        temp.length > 2 ? temp[2] : "");
                                result_comment_data.add(ai);
                            }

                        }

                        return "true";
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
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {
                fillComponents();
                fillDescription();
                bottomMenu();
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
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
            return result_comment_data.size();
        }

        @Override
        public ProductCommentData getItem(int position) {
            return result_comment_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            TextView comment_name, comment_date, comment;


        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ProductCommentData item = result_comment_data.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_product_comment, null);

                holder.comment_name = (TextView) view.findViewById(R.id.comment_name);
                holder.comment_date = (TextView) view.findViewById(R.id.comment_date);
                holder.comment = (TextView) view.findViewById(R.id.comment);


                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.comment_name.setText(item.getAuthor());
            holder.comment_date.setText(item.getDate());
            holder.comment.setText(item.getText());

            return view;
        }

    }



    private void fillComponents() {

        int cart_count = 0;

        try {
            cart_count = Integer.parseInt(part_cart_item_count);
        } catch (Exception e) {
        }

        if (cart_count > 0) {
            badge_right.setVisibility(View.VISIBLE);
            badge_right.setText(part_cart_item_count);
        } else badge_right.setVisibility(View.GONE);


        tv_title.setText(result_temp_list.get(0).getProduct_name());
        img.setImageURI(Uri.parse(result_temp_list.get(0).getProduct_image()));
        gramaj_one.setText(result_all_price_data.get(0).getGramaj());
        gramaj_two.setText(result_all_price_data.get(1).getGramaj());
        price_one.setText(result_all_price_data.get(0).getOld_price());
        price_two.setText(result_all_price_data.get(1).getOld_price());

    }


    void fillPrice() {
        if (checkbox_one.isChecked()){
            total_price = product_count * Double.parseDouble(result_all_price_data.get(0).getOld_price());
            tv_total.setText(result_all_price_data.get(0).getOld_price());
            tv_total_bottom.setText(APP.formatFigureTwoPlaces(total_price));

        } else if (checkBox_two.isChecked()){
            total_price = product_count * Double.parseDouble(result_all_price_data.get(1).getOld_price());
            tv_total.setText(result_all_price_data.get(1).getOld_price());
            tv_total_bottom.setText(APP.formatFigureTwoPlaces(total_price));
        }
    }

    private void fillDescription(){
        tv_desc.setText(result_temp_list.get(0).getProduct_desc());
    }

    private void fillContent(){
        tv_content.setText(result_temp_list.get(0).getProduct_content());
    }

    private void fillComment(){
        adapter = new lazy();
        list_comment.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    private void bottomMenu(){
        tab_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillDescription();

                tab_desc.setTextColor(getResources().getColor(R.color.a_brown11));
                tab_content.setTextColor(getResources().getColor(R.color.a_brown12));
                tab_comment.setTextColor(getResources().getColor(R.color.a_brown12));

                comment_layout.setVisibility(View.GONE);
                tv_desc.setVisibility(View.VISIBLE);
                tv_content.setVisibility(View.GONE);
                list_comment.setVisibility(View.GONE);

                lay_add_comment.setVisibility(View.GONE);
                lay_cart.setVisibility(View.VISIBLE);

                add_comment_title.setVisibility(View.GONE);
                hl_sub.setVisibility(View.VISIBLE);
            }
        });

        tab_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillContent();

                tab_desc.setTextColor(getResources().getColor(R.color.a_brown12));
                tab_content.setTextColor(getResources().getColor(R.color.a_brown11));
                tab_comment.setTextColor(getResources().getColor(R.color.a_brown12));

                comment_layout.setVisibility(View.GONE);
                tv_desc.setVisibility(View.GONE);
                tv_content.setVisibility(View.VISIBLE);
                list_comment.setVisibility(View.GONE);

                lay_add_comment.setVisibility(View.GONE);
                lay_cart.setVisibility(View.VISIBLE);

                add_comment_title.setVisibility(View.GONE);
                hl_sub.setVisibility(View.VISIBLE);
            }
        });

        tab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillComment();

                tab_desc.setTextColor(getResources().getColor(R.color.a_brown12));
                tab_content.setTextColor(getResources().getColor(R.color.a_brown12));
                tab_comment.setTextColor(getResources().getColor(R.color.a_brown11));

                comment_layout.setVisibility(View.GONE);
                tv_desc.setVisibility(View.GONE);
                tv_content.setVisibility(View.GONE);
                list_comment.setVisibility(View.VISIBLE);

                lay_add_comment.setVisibility(View.GONE);
                lay_cart.setVisibility(View.VISIBLE);

                add_comment_title.setVisibility(View.GONE);
                hl_sub.setVisibility(View.VISIBLE);
            }
        });

        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_layout.setVisibility(View.VISIBLE);
                tv_desc.setVisibility(View.GONE);
                tv_content.setVisibility(View.GONE);
                list_comment.setVisibility(View.GONE);

                lay_add_comment.setVisibility(View.VISIBLE);
                lay_cart.setVisibility(View.GONE);

                add_comment_title.setVisibility(View.VISIBLE);
                hl_sub.setVisibility(View.GONE);
            }
        });
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(product_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(""))); //size id
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("" )));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/add_or_update_item_in_cart.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        sendPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        sendPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (sendPart1.contentEquals("OK"))
                        return "true";
                    else if (sendPart1.contentEquals("FAIL"))
                        return "error";
                    else
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
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {

                int badge = 0;

                try {
                    badge = Integer.parseInt(sendPart2);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (badge > 0) {
                    badge_right.setText(sendPart2);
                    badge_right.setVisibility(View.VISIBLE);
                } else
                    badge_right.setVisibility(View.GONE);

               // startActivity(new Intent(m_activity, Cart.class));

            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, sendPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    class Connection3 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(product_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(comment_text)));

            String xml = APP.post1(nameValuePairs, APP.path + "/send_product_review_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        comPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        comPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (comPart1.contentEquals("OK"))
                        return "true";
                    else if (comPart1.contentEquals("FAIL"))
                        return "error";
                    else
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
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {

                final MyImageDialog dia = new MyImageDialog(m_activity,"",
                        "Yorumunuz başarılı bir şekilde iletildi!","Tamam",true);

                dia.setOkClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dia.dismiss();
                    }
                });

                dia.show();

            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, favPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }
}


