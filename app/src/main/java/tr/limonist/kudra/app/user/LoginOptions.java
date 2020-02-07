package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class LoginOptions extends AppCompatActivity {
    private TransparentProgressDialog pd;
    private Activity m_activity;
    MyTextView tv_login, tv_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = LoginOptions.this;
        APP.setWindowsProperties(m_activity,false);
        setContentView(R.layout.z_login_options);

        tv_login = findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity, LoginMain.class));
            }
        });

        tv_new = findViewById(R.id.tv_new);
        tv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity, NewUser.class));
            }
        });
    }
}
