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

import com.facebook.drawee.view.SimpleDraweeView;
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
import tr.limonist.classes.PromotionItem;
import tr.limonist.extras.AutoScrollViewPager;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;


public class MyPromotionDialog extends Dialog {

    private final TransparentProgressDialog pd;
    ListView list;
    Activity m_activity;
    ImageView img_left;
    ArrayList<PromotionItem> results;
    boolean is_usable = false;
    private AutoScrollViewPager jazzy_pager;
    private int selected_page = 0;
    private String[] part1;
    private lazy adapter;
    private String sendPart1, sendPart2;
    private String selected_item_id;
    private String part2;
    TextView point;
    String cart_amount = "0";

    public MyPromotionDialog(Activity context, String title, boolean usable, String total_cart_amount) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        m_activity = context;
        is_usable = usable;
        cart_amount = total_cart_amount;
        pd = new TransparentProgressDialog(m_activity, "", true);
        getWindow().setBackgroundDrawableResource(R.color.a_black12);
        setContentView(R.layout.a_my_promotions_dialog);

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
                .add(R.string.s_active_prom, R.layout.c_item_appointments_page)
                .add(R.string.s_used_prom, R.layout.c_item_appointments_page).create());

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
        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyPointHistoryDialog(m_activity, (m_activity.getString(R.string.app_name) + " " + m_activity.getString(R.string.s_point_history)));
            }
        });

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
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode("0"))); //request_status
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.device_id)));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param7", APP.base64Encode(APP.language_id)));

            String xml = APP.post1(nameValuePairs, APP.path + "/promotions/get_promotions_data_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                    }

                    if (!part1[0].contentEquals("")) {

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");
                            PromotionItem ai = new PromotionItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "", temp.length > 6 ? temp[6] : "",
                                    temp.length > 7 ? temp[7] : "", temp.length > 8 ? temp[8] : "",
                                    temp.length > 9 ? temp[9] : ""
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

              //  point.setText(part2);
                adapter.notifyDataSetChanged();
            } else {
                APP.show_status(m_activity, 1, m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {
            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_item_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(cart_amount)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/promotions/send_include_promotion_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        sendPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        sendPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }

                    if (sendPart1.contentEquals("OK")) {
                        return "true";
                    } else if (sendPart1.contentEquals("FAIL")) {
                        return "error";
                    } else {
                        return "hata";
                    }

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
                dismiss();
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, sendPart2);
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
        public PromotionItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            MyTextView title, desc, date, action;
            SimpleDraweeView img;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final PromotionItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_my_promotions, null);

                holder.title = (MyTextView) view.findViewById(R.id.title);
                holder.desc = (MyTextView) view.findViewById(R.id.desc);
                holder.date = (MyTextView) view.findViewById(R.id.date);
                holder.action = (MyTextView) view.findViewById(R.id.action);

                holder.img = (SimpleDraweeView) view.findViewById(R.id.img);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.title.setText(item.getTitle());
            holder.desc.setText(item.getDetail());
            holder.date.setText(item.getDate());

            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selected_item_id = item.getId();
                    pd.show();
                    new Connection2().execute();

                }
            });

            if (is_usable && selected_page == 0 && item.getActivation_status().contentEquals("2"))
                holder.action.setVisibility(View.VISIBLE);
            else
                holder.action.setVisibility(View.GONE);

            holder.img.setImageURI(item.getImage());

            return view;
        }
    }
}
