package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class UserAggrement extends AppCompatActivity {
    Activity m_activity;
    private TransparentProgressDialog pd;
    AnimCheckBox acb;
    String contract;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = UserAggrement.this;
        APP.setWindowsProperties(m_activity,false);
        setContentView(R.layout.z_user_aggrement);

        contract = getIntent().getStringExtra("part1");
    }
}
