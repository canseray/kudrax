package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;


public class MyPromotionUsageDialog extends Dialog {

    private final TransparentProgressDialog pd;
    Activity m_activity;
    ImageView img_left;
    private String part1;
    TextView point,total_cart;
    String cart_amount = "0";
    EditText et_point;
    MyTextView tv_done,tv_history;
    public String used_point="0";

    public MyPromotionUsageDialog(Activity context, String title, String total_cart_amount) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        m_activity = context;
        cart_amount = total_cart_amount;
        pd = new TransparentProgressDialog(m_activity, "", true);
        getWindow().setBackgroundDrawableResource(R.color.a_black12);
        setContentView(R.layout.a_my_promotions_usage_dialog);

        MyTextView tv_title = (MyTextView) findViewById(R.id.tv_title);
        tv_title.setText(title);

        img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        et_point = findViewById(R.id.et_point);

        total_cart = findViewById(R.id.total_cart);
        total_cart.setText(m_activity.getString(R.string.s_total_cart_amount_x,cart_amount));
        point = findViewById(R.id.point);

        tv_history = findViewById(R.id.tv_history);
        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyPointHistoryDialog(m_activity, (m_activity.getString(R.string.app_name) + " " + m_activity.getString(R.string.s_point_history)));
            }
        });

        tv_done = findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double temp_cart_amount = 0;

                try {
                    temp_cart_amount = Double.parseDouble(cart_amount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                String temp_point = et_point.getText().toString();

                double temp_used_point = 0;

                try {
                    temp_used_point = Double.parseDouble(temp_point);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if(temp_cart_amount == temp_used_point)
                {
                    used_point=temp_point;
                    dismiss();
                }
                else
                {
                    APP.show_status(m_activity,2, m_activity.getString(R.string.s_must_enter_as_many_point_as_your_cart_amount));
                }

            }
        });
    }

    @Override
    public void show() {
        super.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pd.show();
                new Connection().execute();
            }
        }, 250);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/promotions/get_total_usable_point.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1"));
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
                et_point.setHint(cart_amount);
                point.setText(part1);
            } else {
                APP.show_status(m_activity, 1, m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }
}
