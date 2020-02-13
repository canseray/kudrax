package tr.limonist.kudra.app.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import tr.limonist.classes.HelpChatItem;
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
    public String[] part1;
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
                new MyQrCodeDialog(m_activity);
            }
        });
    }
}
