package tr.limonist.kudra.app.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.cart.Cart;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyZoomImageDialog;

public class ProductDetail extends Activity {

    Activity m_activity;
    TransparentProgressDialog pd;
    String[] part1;
    String product_id = "0",product_title="";
    MyTextView badge_right;
    SimpleDraweeView img;
    private SimpleDraweeView spark;
    private MyTextView tv_title;
    private TextView tv_count;
    private MyTextView tv_total;
    private LinearLayout lay_minus;
    private LinearLayout lay_plus;
    private LinearLayout lay_cart;
    private String sendPart1,sendPart2;
    private String favPart1,favPart2;

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

        img = findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyZoomImageDialog(m_activity, product_title,part_product_image, null, true).show();
            }
        });

        spark = (SimpleDraweeView) findViewById(R.id.spark);

        tv_title = (MyTextView) findViewById(R.id.tv_title);

        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_total = (MyTextView) findViewById(R.id.tv_total);

        lay_minus = (LinearLayout) findViewById(R.id.lay_minus);
        lay_minus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (product_cart_count > 1) {
                    --product_cart_count;
                    fillPrice();
                }
            }

        });

        lay_plus = (LinearLayout) findViewById(R.id.lay_plus);
        lay_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ++product_cart_count;
                fillPrice();
            }

        });

        lay_cart = (LinearLayout) findViewById(R.id.lay_cart);
        lay_cart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                pd.show();
                new Connection2().execute();
            }

        });

    }

    private String part_product_image, part_product_favorite = "0", part_product_currency, part_product_detail, part_cart_item_count;
    private String part_product_price, part_product_cart_amount, part_product_video;

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(product_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_selected_product_details.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part_product_cart_amount = APP.base64Decode(APP.getElement(parse, "part1"));
                        part_product_price = APP.base64Decode(APP.getElement(parse, "part2"));
                        part_cart_item_count = APP.base64Decode(APP.getElement(parse, "part3"));
                        part_product_detail = APP.base64Decode(APP.getElement(parse, "part4"));
                        part_product_currency = APP.base64Decode(APP.getElement(parse, "part5"));
                        part_product_favorite = APP.base64Decode(APP.getElement(parse, "part6"));
                        part_product_video = APP.base64Decode(APP.getElement(parse, "part7"));
                        part_product_image = APP.base64Decode(APP.getElement(parse, "part8"));


                    }

                    if (!part_product_price.contentEquals("")) {
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
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private void fillComponents() {

        tv_title.setText(product_title);
        img.setImageURI(part_product_image);

        int cart_count = 0;

        try {
            cart_count = Integer.parseInt(part_cart_item_count);
        } catch (Exception e) {
        }

        if (cart_count > 0) {
            badge_right.setVisibility(View.VISIBLE);
            badge_right.setText(part_cart_item_count);
        } else badge_right.setVisibility(View.GONE);

        if (part_product_favorite.contentEquals("1")) {
            //spark.setChecked(true);
            //spark.playAnimation();
        }

      /*  spark.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                pd.show();
                new Connection3().execute();
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        }); */

      spark.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              //share
          }
      });

        single_price = Double.parseDouble(part_product_price);
        product_cart_count = 1;
        try {
            product_cart_count = Double.parseDouble(part_product_cart_amount);
        } catch (Exception e) {
        }

        fillPrice();

    }

    double product_cart_count = 1;
    double single_price = 0;
    double total_price = 0;

    void fillPrice() {
        tv_count.setText(APP.formatFigureOnePlaces(product_cart_count));
        total_price = product_cart_count * single_price;
        tv_total.setText(APP.formatFigureTwoPlaces(total_price) + " " + part_product_currency);
    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(product_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("" + product_cart_count)));
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

                startActivity(new Intent(m_activity, Cart.class));

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

                    if (favPart1.contentEquals("OK"))
                        return "true";
                    else if (favPart1.contentEquals("FAIL"))
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
                if (favPart2.contentEquals("1")){

                }
                    //spark.setChecked(true);
                else{
                    // spark.setChecked(false);

                }

            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, favPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }
}
