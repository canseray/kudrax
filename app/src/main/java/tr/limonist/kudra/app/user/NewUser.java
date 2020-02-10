package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.google.gson.Gson;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.USER;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyContractDialog;

public class NewUser extends Activity {

    MyTextView tv_done;
    EditText et_mail, et_pass, et_name, et_surname, et_phone;
    TextView et_skin_type;
    String s_mail, s_pass, s_name, s_surname, s_phone, s_skin_type;
    private TransparentProgressDialog pd;
    private boolean is_check;
    private Activity m_activity;
    private String part1;
    AnimCheckBox acb;
    private String respPart1,respPart2,respPart3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = NewUser.this;
        APP.setWindowsProperties(m_activity,false);
        pd = new TransparentProgressDialog(m_activity, "", true);

        setContentView(R.layout.z_new_user);

        LinearLayout viewstub_parent_ly = findViewById(R.id.viewstub_parent_ly);
        viewstub_parent_ly.setBackgroundColor(getResources().getColor(R.color.a_brown11));


        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(getString(R.string.s_user_information));
        tv_baslik.setBackgroundColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setTextColor(getResources().getColor(R.color.a_white11));

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.b_ic_prew_white);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        et_mail = (EditText) findViewById(R.id.et_mail);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_name = (EditText) findViewById(R.id.et_name);
        et_surname = (EditText) findViewById(R.id.et_surname);
        et_phone = (EditText) findViewById(R.id.et_phone);

        tv_done = (MyTextView) findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {


                s_mail = et_mail.getText().toString();
                s_pass = et_pass.getText().toString();
                s_name = et_name.getText().toString();
                s_surname = et_surname.getText().toString();
                s_phone = et_phone.getText().toString();
               // s_skin_type = et_skin_type.getText().toString();

                if (s_name.length() > 0) {
                    if (s_surname.length() > 0) {
                        if (APP.isValidEmail(s_mail)) {
                            if (s_phone.length() > 0) {
                                if (s_pass.length() > 5 && s_pass.length() < 21) {
                                         pd.show();
                                       // new Connection().execute("");
                                        startActivity(new Intent(m_activity, UserAggrement.class)
                                        .putExtra("s_name",s_name)
                                        .putExtra("s_surname",s_surname)
                                        .putExtra("s_mail",s_mail)
                                        .putExtra("s_phone",s_phone)
                                        .putExtra("s_pass",s_pass));

                                } else {
                                    APP.show_status(m_activity, 2,
                                            getResources().getString(R.string.s_please_enter_a_password));
                                }
                            } else {
                                APP.show_status(m_activity, 2,
                                        getResources().getString(R.string.s_please_enter_x, getString(R.string.s_phone_number)));
                            }
                        } else {
                            APP.show_status(m_activity, 2,
                                    getResources().getString(R.string.s_please_enter_a_valid_email_address));
                        }
                    } else {
                        APP.show_status(m_activity, 2,
                                getResources().getString(R.string.s_please_enter_x, getString(R.string.s_lastname)));
                    }
                } else {
                    APP.show_status(m_activity, 2,
                            getResources().getString(R.string.s_please_enter_x, getString(R.string.s_firstname)));
                }
            }
        });




    }

}
