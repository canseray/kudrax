package tr.limonist.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.viewpagerindicator.CirclePageIndicator;

import tr.limonist.classes.BannerItem;
import tr.limonist.extras.AutoScrollViewPager;
import tr.limonist.extras.MyTextView;
import tr.limonist.kudra.R;

public class MyAnnouncementDialog extends Dialog {
    AutoScrollViewPager mJazzyBanner;
    Context mContext;
    String[] dialog_images = {};

    public MyAnnouncementDialog(Context context, BannerItem item) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        mContext = context;
        setContentView(R.layout.a_my_announcement_dialog);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(item.getTitle());
        tv_baslik.setTextColor(mContext.getResources().getColor(R.color.a_brown11));

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();
            }

        });

        dialog_images = item.getImage().split("\\[-\\]");
        mJazzyBanner = (AutoScrollViewPager) findViewById(R.id.jazzy_pager);
        pageAdapterDialog slider_adapter = new pageAdapterDialog();
        mJazzyBanner.setAdapter(slider_adapter);
        CirclePageIndicator circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        circleIndicator.setViewPager(mJazzyBanner);

        WebView webView1 = (WebView) findViewById(R.id.webView1);
        webView1.setBackgroundColor(Color.TRANSPARENT);
        webView1.loadDataWithBaseURL("", item.getContent(), "text/html", "UTF-8", "");

        show();

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private class pageAdapterDialog extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi;
            vi = new View(mContext);
            vi = inflater.inflate(R.layout.c_item_banner_dialog, null);

            SimpleDraweeView img = (SimpleDraweeView) vi.findViewById(R.id.img);
            img.setImageURI(dialog_images[position]);
            container.addView(vi);

            return vi;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView((LinearLayout) obj);
        }

        @Override
        public int getCount() {
            return dialog_images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
