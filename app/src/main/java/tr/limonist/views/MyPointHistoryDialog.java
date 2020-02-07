package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItem;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.PointHistoryItem;
import tr.limonist.extras.AutoScrollViewPager;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;


public class MyPointHistoryDialog extends Dialog {

    private final TransparentProgressDialog pd;
    ListView list;
    Activity m_activity;
    ImageView img_left;
    ArrayList<PointHistoryItem> results;
    private AutoScrollViewPager jazzy_pager;
    private int selected_page = 0;
    private String[] part1;
    private lazy adapter;
    private String part2;
    TextView point;

    public MyPointHistoryDialog(Activity context, String title) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        m_activity = context;
        pd = new TransparentProgressDialog(m_activity, "", true);
        getWindow().setBackgroundDrawableResource(R.color.a_black12);
        setContentView(R.layout.a_my_point_history_dialog);

        MyTextView tv_title = (MyTextView) findViewById(R.id.tv_title);
        tv_title.setText(title);

        img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        list = (ListView) findViewById(R.id.list);

        jazzy_pager = findViewById(R.id.jazzy_pager);

        pageAdapter adapter_pager = new pageAdapter(ViewPagerItems.with(m_activity)
                .add(R.string.s_earned_point, R.layout.c_item_appointments_page)
                .add(R.string.s_used_point, R.layout.c_item_appointments_page).create());

        selected_page = 0;

        jazzy_pager.setAdapter(adapter_pager);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(jazzy_pager);

        jazzy_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                selected_page = position;
                pd.show();
                new Connection().execute();
            }
        });
        results = new ArrayList<>();
        adapter = new lazy();
        list.setAdapter(adapter);

        point = findViewById(R.id.point);

        show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pd.show();
                new Connection().execute();
            }
        }, 250);

    }

    public class pageAdapter extends PagerAdapter {

        private final ViewPagerItems pages;
        private final SparseArrayCompat<WeakReference<View>> holder;
        private final LayoutInflater inflater;

        public pageAdapter(ViewPagerItems pages) {
            this.pages = pages;
            this.holder = new SparseArrayCompat<>(pages.size());
            this.inflater = LayoutInflater.from(pages.getContext());
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getPagerItem(position).initiate(inflater, container);

            container.addView(view);
            holder.put(position, new WeakReference<View>(view));
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            holder.remove(position);
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPagerItem(position).getTitle();
        }

        @Override
        public float getPageWidth(int position) {
            return getPagerItem(position).getWidth();
        }

        public View getPage(int position) {
            final WeakReference<View> weakRefItem = holder.get(position);
            return (weakRefItem != null) ? weakRefItem.get() : null;
        }

        protected ViewPagerItem getPagerItem(int position) {
            return pages.get(position);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
            results = new ArrayList<>();
            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode("" + selected_page)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/promotions/get_point_history_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    String[] part_slide_items = null;

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part2")).split("\\[##\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part1"));
                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            PointHistoryItem ai = new PointHistoryItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : ""
                            );
                            results.add(ai);
                        }

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
                point.setText(part2);
                adapter.notifyDataSetChanged();
            } else {
                APP.show_status(m_activity, 1, m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
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
            return results.size();
        }

        @Override
        public PointHistoryItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            MyTextView value, desc, date;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final PointHistoryItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_my_point_history, null);

                holder.value = (MyTextView) view.findViewById(R.id.value);
                holder.desc = (MyTextView) view.findViewById(R.id.desc);
                holder.date = (MyTextView) view.findViewById(R.id.date);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.value.setText(item.getValue());
            holder.desc.setText(item.getDetail());
            holder.date.setText(item.getDate());

            return view;
        }
    }
}
