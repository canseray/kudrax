package tr.limonist.kudra.app.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import tr.limonist.classes.WelcomeItem;
import tr.limonist.extras.AutoScrollViewPager;
import tr.limonist.extras.MyTextView;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class WelcomeActivity extends AppCompatActivity {

    AutoScrollViewPager mJazzy;
    CirclePageIndicator circleIndicator;
    MyTextView tv_skip;
    ArrayList<WelcomeItem> results;
    Activity m_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = WelcomeActivity.this;
        APP.setWindowsProperties(m_activity,false);
        results= (ArrayList<WelcomeItem>) getIntent().getSerializableExtra("results");

        setContentView(R.layout.z_my_welcome_dialog);

        tv_skip = (MyTextView) findViewById(R.id.tv_skip);
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(m_activity, NewUser.class), 1);
            }
        });
        mJazzy = (AutoScrollViewPager) findViewById(R.id.jazzy_pager);
        mJazzy.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                if (arg0 == results.size() - 1)
                    tv_skip.setVisibility(View.VISIBLE);
                else
                    tv_skip.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mJazzy.setAdapter(new pageAdapter());
        circleIndicator.setViewPager(mJazzy);

    }

    private class pageAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi;
            vi = new View(m_activity);
            vi = inflater.inflate(R.layout.c_item_welcome_dialog_row, null);

            SimpleDraweeView img = (SimpleDraweeView ) vi.findViewById(R.id.img);

            img.setImageURI(Uri.parse(results.get(position).getImage()));

            container.addView(vi);

            return vi;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView((LinearLayout) obj);
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result.contentEquals("OK")) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "OK");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }
}
