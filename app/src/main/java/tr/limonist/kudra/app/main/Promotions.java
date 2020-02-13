package tr.limonist.kudra.app.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.classes.HelpChatItem;
import tr.limonist.classes.PromotionItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.profile.Help;
import tr.limonist.views.MyQrCodeDialog;

public class Promotions extends AppCompatActivity {

    Activity m_activity;
    private ListView list;
    private TransparentProgressDialog pd;
    public Help.lazy adapter;
    TextView tv_desc;
    LinearLayout lay_compose, lay_scan_qr_code, lay_my_promotions;
    private EditText et_message;
    private ImageView img_send;
    String message, part2, part3;
    public String sendPart1;
    public String sendPart2;
    MyTextView tv_call_number;
    ArrayList<HelpChatItem> results;
    LinearLayout top_left;
    MyTextView tv_baslik;
    ImageView img_left;
    SimpleDraweeView img;
    public String part1_user_status_image, part2_winning_status, part3_winning_message, part4_cong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Promotions.this;
        APP.setWindowsProperties(m_activity,false);
        setContentView(R.layout.z_promotions);

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

        lay_my_promotions = findViewById(R.id.lay_my_promotions);
        lay_my_promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity,MyPromotions.class));
            }
        });



        lay_scan_qr_code = findViewById(R.id.lay_scan_qr_code);
        lay_scan_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity,ScannerActivity.class));
            }
        });

        img = findViewById(R.id.img);

        new Connection().execute();

    }

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
            results = new ArrayList<>();
            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.device_id))); //request_status
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode(APP.language_id)));

            String xml = APP.post1(nameValuePairs, APP.path + "/promotions/get_promotion_count.php");

            if (xml != null && !xml.contentEquals("fail")) {
                try {
                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1_user_status_image = APP.base64Decode(APP.getElement(parse, "part1"));
                        part2_winning_status = APP.base64Decode(APP.getElement(parse, "part2"));
                        part3_winning_message = APP.base64Decode(APP.getElement(parse, "part3"));
                        part4_cong = APP.base64Decode(APP.getElement(parse, "part4"));

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

                if (part2_winning_status.equals("YES")){

                    //new dialog set part3 part4
                }

                img.setImageURI(Uri.parse(part1_user_status_image));

            } else {
                APP.show_status(m_activity, 1, m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}
