package tr.limonist.kudra.app.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.MyVideoDialog;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.SelfAd;
import tr.limonist.kudra.app.profile.Help;
import tr.limonist.kudra.app.profile.MyInvoices;
import tr.limonist.kudra.app.profile.MyNotifications;
import tr.limonist.kudra.app.profile.OrderHistory;
import tr.limonist.kudra.app.profile.PraticalSolves;
import tr.limonist.kudra.app.shop.Categories;
import tr.limonist.kudra.app.shop.Favorites;
import tr.limonist.kudra.app.user.LoginMain;
import tr.limonist.kudra.app.user.ProfileSettings;
import tr.limonist.classes.BannerItem;
import tr.limonist.classes.MainItem;
import tr.limonist.classes.SlideMenuItem;
import tr.limonist.extras.AutoScrollViewPager;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyAlertDialog;
import tr.limonist.views.MyAnnouncementDialog;
import tr.limonist.views.MyContractDialog;
import tr.limonist.views.MyNotificationDialog;
import tr.limonist.views.MyPromotionDialog;
import tr.limonist.views.MyQrCodeDialog;
import tr.limonist.views.MySlideMenuDialog;
import tr.limonist.views.MyWebviewDialog;

public class Main extends AppCompatActivity {
    ArrayList<SlideMenuItem> results_slide;
    Activity m_activity;
    RefreshLayout refreshLayout;
    private TransparentProgressDialog pd;
    public String part_intertiate_ads;
    public String[] part_social = {""};
    private String[] part_banner, part_status;
    private String[] part_category_items;
    private String[] part_side_menu;
    ArrayList<BannerItem> results_banner;
    ArrayList<MainItem> results, results_temp;
    ArrayList<SlideMenuItem> results_slide_menu;
    AutoScrollViewPager mJazzy;
    private String result_code = "";
    private String qrPart1, qrPart2;
    private pageAdapter slider_adapter;
    private int width = 0;
    private int height = 0;
    private lazy adapter;
    private String[] part_user_informaiton;
    private String aboutPart1, aboutPart2;
    private String contPart1, contPart2;
    int scanner_type = 0;
    LinearLayout lay_main;
    RelativeLayout lay_circle;
    CirclePageIndicator circleIndicator;


    int[] rl_resources = {R.id.rl1,R.id.rl2,R.id.rl3,R.id.rl4,R.id.rl5,R.id.rl6};
    int[] img_resources = {R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5,R.id.img6};
    int[] title_resources = {R.id.title1,R.id.title2,R.id.title3,R.id.title4,R.id.title5,R.id.title6};
    int[] badge_resources = {R.id.badge1,R.id.badge2,R.id.badge3,R.id.badge4,R.id.badge5,R.id.badge6};

    @Override
    protected void onResume() {
        super.onResume();
        new Connection2().execute("");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Main.this;
        APP.setWindowsProperties(m_activity, false);
        pd = new TransparentProgressDialog(m_activity, "", true);

        setContentView(R.layout.z_main);

        LinearLayout viewstub_parent_ly = findViewById(R.id.viewstub_parent_ly);
        viewstub_parent_ly.setBackgroundColor(getResources().getColor(R.color.a_brown14));

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_img_img);
        stub.inflate();

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (APP.main_user != null) {
                    final MySlideMenuDialog smd = new MySlideMenuDialog(m_activity);
                    smd.setData(results_slide_menu);
                    smd.setItemClick(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            if (position > 0) {
                                smd.close();
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (position > 0) {
                                            SlideMenuItem smi = results_slide_menu.get(position - 1);
                                            if (smi.getPrep().contentEquals("PRESENTMODALFORMYINVOICESVIEW")) {
                                                startActivity(new Intent(m_activity, MyInvoices.class).putExtra("title", smi.getTitle()));

                                            } else if (smi.getPrep().contentEquals("PRESENTMODALFORORDERHISTORYVIEW")) {
                                                startActivity(new Intent(m_activity, OrderHistory.class).putExtra("title", smi.getTitle()));


                                            } else if (smi.getPrep().contentEquals("PRESENTMODALFORQRCODEVIEW")) {
                                                new MyQrCodeDialog(m_activity);


                                            } else if (smi.getPrep().contentEquals("SEGUEFORHELPVIEW")) {
                                                startActivity(new Intent(m_activity, PraticalSolves.class).putExtra("title", smi.getTitle()));


                                            } else if (smi.getPrep().contentEquals("SEGUEFORNOTIFICATIONSETTINGSVIEW")) {
                                                startActivity(new Intent(m_activity, MyNotifications.class).putExtra("title", smi.getTitle()));

                                            } else if (smi.getPrep().contentEquals("PRESENTFORCONTRACTVIEW")) {
                                                pd.show();
                                                new Connection5().execute(smi.getPrep());

                                            } else if (smi.getPrep().contentEquals("PRESENTSENDMAILVIEW")) {

                                                startActivity(new Intent(m_activity, Help.class).putExtra("title", smi.getTitle()));

                                               /* String[] emails = new String[1];
                                                emails[0] = part_user_informaiton[2];
                                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                intent.setData(Uri.parse("mailto:"));
                                                intent.putExtra(Intent.EXTRA_EMAIL, emails);
                                                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Mobile");
                                                startActivity(Intent.createChooser(intent, getString(R.string.s_select))); */



                                            } else if (smi.getPrep().contentEquals("PRESENTMODALFORAPPSETTINGS")) {

                                                try {
                                                    Intent intent = new Intent(
                                                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.parse("package:" + APP.package_name));
                                                    startActivity(intent);

                                                } catch (ActivityNotFoundException e) {
                                                    Intent intent = new Intent(
                                                            android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                                    startActivity(intent);

                                                }

                                            } else if (smi.getPrep().contentEquals("LOGOUTAPPACTION")) {



                                                final MyAlertDialog alert = new MyAlertDialog(m_activity,
                                                        R.drawable.logout_k , "",
                                                        getString(R.string.s_log_out_exp),
                                                        getString(R.string.s_log_out), getString(R.string.s_cancel),
                                                        true);

                                                alert.setNegativeClicl(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        alert.dismiss();

                                                    }
                                                });
                                                alert.setPositiveClicl(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        alert.dismiss();
                                                        APP.main_user = null;
                                                        APP.e.putString("USER", null);
                                                        APP.e.commit();
                                                        startActivity(new Intent(m_activity, LoginMain.class)
                                                                .putExtra("call_type", "0"));
                                                        finish();
                                                    }
                                                });
                                                alert.show();

                                            }

                                        }
                                    }

                                }, 250);
                            }

                        }
                    });
                    smd.show();
                } else {
                    startActivity(new Intent(m_activity, LoginMain.class).putExtra("call_type", "0"));
                }

            }
        });

        LinearLayout top_right = findViewById(R.id.top_right);
        top_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (APP.main_user != null)
                    qrOptionDialog();
                else
                    startActivity(new Intent(m_activity, LoginMain.class));

            }
        });

        ImageView img_left = findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.hidden_menu_icon_k);

        ImageView img_right = findViewById(R.id.img_right);
        img_right.setImageResource(R.drawable .qr_code_k);

        ImageView img_center = findViewById(R.id.img_center);
        img_center.setImageResource(R.drawable.header_text_k);

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Connection().execute();
            }
        });

        ImageView img_face = findViewById(R.id.img_face);
        img_face.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                MyWebviewDialog alert = new MyWebviewDialog(m_activity, part_social[0]);
                alert.show();

            }
        });
        ImageView img_twit = findViewById(R.id.img_twit);
        img_twit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyWebviewDialog alert = new MyWebviewDialog(m_activity, part_social[1]);
                alert.show();
            }
        });
        ImageView img_inst = findViewById(R.id.img_inst);
        img_inst.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyWebviewDialog alert = new MyWebviewDialog(m_activity, part_social[2]);
                alert.show();
            }
        });

        ImageView img_order = findViewById(R.id.img_order);
        img_order.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                startActivity(new Intent(m_activity, Cargo.class).putExtra("title", getString(R.string.s_order_history)));
            }
        });

        circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mJazzy = findViewById(R.id.jazzy_pager);

        lay_main = findViewById(R.id.lay_main);
        lay_circle = findViewById(R.id.lay_circle);

        /*list = findViewById(R.id.list);
        list.setNumColumns(3);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


            }
        });*/

        ViewTreeObserver vto = lay_main.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lay_main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = lay_main.getMeasuredWidth();
                height = lay_main.getMeasuredHeight();

                float temp_width = (float) width * (0.95f);
                float temp_height = (float) height * (0.98f);

                width = (int) temp_width;
                height = (int) temp_height;

                int new_value = Math.min(width, height);
;
                ViewGroup.LayoutParams params = lay_circle.getLayoutParams();
                params.height = new_value;
                params.width = new_value;
                lay_circle.setLayoutParams(params);

            }
        });

        pd.show();
        new Connection().execute("");
        new Connection6().execute("");

        if (getIntent().hasExtra("call_type"))
            if (getIntent().getStringExtra("call_type").contentEquals("notification"))
                new MyNotificationDialog(m_activity);

    }

    private void qrOptionDialog() {

        final Dialog deliveryDialog = new Dialog(m_activity, android.R.style.Theme_Black_NoTitleBar);
        deliveryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        deliveryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deliveryDialog.setContentView(R.layout.z_dialog_qr_option);
        deliveryDialog.setCancelable(true);
        deliveryDialog.setCanceledOnTouchOutside(true);

        final ImageView img_close = deliveryDialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                deliveryDialog.dismiss();

            }

        });

        final LinearLayout main = deliveryDialog.findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                deliveryDialog.dismiss();

            }

        });

        final LinearLayout lay_scan = deliveryDialog.findViewById(R.id.lay_scan);
        lay_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                deliveryDialog.dismiss();
                scanner_type = 0;
                new IntentIntegrator(m_activity).setCaptureActivity(ScannerActivity.class).setCameraId(camera_type).initiateScan();
            }

        });

        final LinearLayout lay_qr = deliveryDialog.findViewById(R.id.lay_qr);
        lay_qr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                deliveryDialog.dismiss();
                new MyQrCodeDialog(m_activity);

            }

        });

        deliveryDialog.show();

    }

    //sidemenu
    private class Connection6 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results_slide_menu = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));


            String xml = APP.post1(nameValuePairs, APP.path + "/get_side_menu_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part_side_menu = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");

                    }

                    if (!part_side_menu[0].contentEquals("")) {

                        for (int i = 0; i < part_side_menu.length; i++) {
                            String[] temp = part_side_menu[i].split("\\[#\\]");
                             SlideMenuItem ai = new SlideMenuItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "",
                                    temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "",
                                    temp.length > 4 ? temp[4] : "");
                            results_slide_menu.add(ai);
                        }




                        return "true";
                    } else
                        return "false";

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
            if (refreshLayout != null)
                refreshLayout.finishRefresh();
            if (result.contentEquals("true")) {
                fillCompoments();
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }


    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results_banner = new ArrayList<>();
            results = new ArrayList<>();
            results_slide = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/init_scr.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    String[] part_slide_items = null;

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part_category_items = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        part_intertiate_ads = APP.base64Decode(APP.getElement(parse, "part2"));
                        part_user_informaiton = APP.base64Decode(APP.getElement(parse, "part4")).split("\\[#\\]", -1);
                        part_status = APP.base64Decode(APP.getElement(parse, "part3")).split("\\[#\\]", -1);
                       // part_slide_items = APP.base64Decode(APP.getElement(parse, "part9")).split("\\[##\\]");
                        part_banner = APP.base64Decode(APP.getElement(parse, "part5")).split("\\[##\\]");
                        part_social = APP.base64Decode(APP.getElement(parse, "part6")).split("\\[#\\]");

                    }

                    if (!part_category_items[0].contentEquals("")) {

                        for (int i = 0; i < part_category_items.length; i++) {
                            String[] temp = part_category_items[i].split("\\[#\\]");
                            MainItem ai = new MainItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "",
                                    temp.length > 2 ? temp[2] : "",
                                    temp.length > 3 ? temp[3] : "",
                                    temp.length > 4 ? temp[4] : "",
                                    temp.length > 5 ? temp[5] : "");
                            results.add(ai);
                        }

                        if (!part_banner[0].contentEquals("")) {

                            for (int i = 0; i < part_banner.length; i++) {
                                String[] temp = part_banner[i].split("\\[#\\]");
                                BannerItem ai = new BannerItem(temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "",
                                        temp.length > 2 ? temp[2] : "",
                                        temp.length > 3 ? temp[3] : "");
                                results_banner.add(ai);
                            }

                        }

                       /* if (!part_slide_items[0].contentEquals("")) {

                            for (int i = 0; i < part_slide_items.length; i++) {
                                String[] temp = part_slide_items[i].split("\\[#\\]");
                                SlideMenuItem ai = new SlideMenuItem(temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
                                        temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "");
                                results_slide.add(ai);
                            }

                        } */

                        return "true";
                    } else
                        return "false";

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
            if (refreshLayout != null)
                refreshLayout.finishRefresh();
            if (result.contentEquals("true")) {
                fillCompoments();
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private void fillCompoments() {

        fillMainItems();

        if (mJazzy != null) {
            mJazzy.startAutoScroll(3000);
            mJazzy.setScrollDurationFactor(2);
            mJazzy.setInterval(3000);

            slider_adapter = new pageAdapter();
            mJazzy.setAdapter(slider_adapter);
            circleIndicator.setViewPager(mJazzy);


        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (!part_intertiate_ads.contentEquals("")) {
                 //   startActivity(new Intent(m_activity, SelfAd.class).putExtra("onemli", part_intertiate_ads));
                }
            }
        }, 500);
    }

    private void fillCompoments2() {

        results = results_temp;
        fillMainItems();
    }
    private void fillMainItems() {

        if(results.size()>0)
        {
            for(int i=0;i<results.size();i++)
            {
                MainItem item = results.get(i);

                SimpleDraweeView img = findViewById(img_resources[i]);
                img.setImageURI(item.getImage());

                MyTextView title = findViewById(title_resources[i]);
                title.setText(item.getTitle());

                MyTextView badge = findViewById(badge_resources[i]);

                int count =0;

                try {
                    count = Integer.parseInt(item.getBadge());
                } catch (NumberFormatException e) {}

                if(count>0)
                {
                    badge.setText(item.getBadge());
                    badge.setVisibility(View.VISIBLE);
                }
                else
                    badge.setVisibility(View.GONE);

                final int k= i;

                RelativeLayout rl = findViewById(rl_resources[i]);

                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menu_item_click(k);
                    }
                });

            }
        }
    }

    private void menu_item_click(int i) {

        MainItem smi = results.get(i);
        if (smi.getPrep().contentEquals("PRESENTFORNOTIFICATIONSVIEW")) {

            if (APP.main_user != null) {
               // new MyNotificationDialog(m_activity);
                startActivity(new Intent(m_activity,Notifications.class).putExtra("title",smi.getTitle()));

            } else
                startActivity(new Intent(m_activity, LoginMain.class));

        } else if (smi.getPrep().contentEquals("PRESENTFORFEEDBACKVIEW")) {

            if (APP.main_user != null) {
                startActivity(new Intent(m_activity, Feedback.class).putExtra("title", smi.getTitle()));
            } else
                startActivity(new Intent(m_activity, LoginMain.class));

        } else if (smi.getPrep().contentEquals("SEGUEFORBRANCHESVIEW")) {

            startActivity(new Intent(m_activity, Branches.class).putExtra("title", smi.getTitle()));

        }  else if (smi.getPrep().contentEquals("PRESENTFORPROMOTIONSVIEW")) {

            if (APP.main_user != null) {
                //new MyPromotionDialog(m_activity,smi.getTitle(),false,"0");
                startActivity(new Intent(m_activity,Promotions.class));
            } else
                startActivity(new Intent(m_activity, LoginMain.class));
        } else if (smi.getPrep().contentEquals("PRESENTFORFAVORITIES")) {

            if (APP.main_user != null) {
                startActivity(new Intent(m_activity, Favorites.class).putExtra("title", smi.getTitle()));
            } else
                startActivity(new Intent(m_activity, LoginMain.class));


        } else if (smi.getPrep().contentEquals("PRESENTFORBUYITEMS")) {

            if (APP.main_user != null) {
                startActivity(new Intent(m_activity, Categories.class).putExtra("title", smi.getTitle()));
            } else
                startActivity(new Intent(m_activity, LoginMain.class));
        }

    }

    int REQUEST_CODE = 0x0000c0de;
    int camera_type = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_FIRST_USER) {
            camera_type = camera_type == 0 ? 1 : 0;
            new IntentIntegrator(m_activity).setCaptureActivity(ScannerActivity.class).setCameraId(camera_type).initiateScan();
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    result_code = result.getContents();
                    pd.show();
                    new Connection3().execute("");
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
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
        public MainItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            MyTextView title, badge;
            SimpleDraweeView img;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final MainItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_main, null);

                holder.title = view.findViewById(R.id.title);
                holder.badge = view.findViewById(R.id.badge);
                holder.img = view.findViewById(R.id.img);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            view.setLayoutParams(new AbsListView.LayoutParams(width / 3, height / 3));

            holder.title.setText(item.getTitle());
            holder.img.setImageURI(item.getImage());

            int count = 0;

            try {
                count = Integer.parseInt(item.getBadge());
            } catch (Exception e) {
                // TODO: handle exception
            }

            if (count > 0) {
                holder.badge.setVisibility(View.VISIBLE);
                holder.badge.setText(item.getBadge());
            } else
                holder.badge.setVisibility(View.GONE);

            return view;
        }
    }

    private class Connection3 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(result_code)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("" + scanner_type)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/check_scanned_qrcode.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
                        qrPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        qrPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                    }
                    if (qrPart1.contentEquals("OK")) {
                        return "true";
                    } else if (qrPart1.contentEquals("VIDEO")) {
                        return "video";
                    } else if (qrPart1.contentEquals("FAIL")) {
                        return "error";
                    } else
                        return "false";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }
            } else {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {
                APP.show_status(m_activity, 3, qrPart2);
            } else if (result.contentEquals("video")) {
                startActivity(new Intent(m_activity, MyVideoDialog.class).putExtra("url", qrPart2));
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 1, qrPart2);
            } else {
                APP.show_status(m_activity, 1, getString(R.string.s_unexpected_connection_error_has_occured));
            }

        }

    }

    private class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results_temp = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.android_id)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/init_scr.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part_category_items = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");

                    }

                    if (!part_category_items[0].contentEquals("")) {

                            for (int i = 0; i < part_category_items.length; i++) {
                                String[] temp = part_category_items[i].split("\\[#\\]");
                                MainItem ai = new MainItem(temp.length > 0 ? temp[0] : "",
                                        temp.length > 1 ? temp[1] : "",
                                        temp.length > 2 ? temp[2] : "",
                                        temp.length > 3 ? temp[3] : "",
                                        temp.length > 4 ? temp[4] : "",
                                        temp.length > 5 ? temp[5] : "");
                                results_temp.add(ai);
                            }



                        return "true";
                    } else
                        return "false";

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }
        }

        protected void onPostExecute(String result) {
            if (result.contentEquals("true")) {
                fillCompoments2();
            }
        }
    }

    private class pageAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi;
            vi = new View(m_activity);
            vi = inflater.inflate(R.layout.c_item_banner, null);

            TextView title = vi.findViewById(R.id.title);
            TextView content = vi.findViewById(R.id.content);

            SimpleDraweeView img = vi.findViewById(R.id.img);

            img.setImageURI(results_banner.get(position).getImage());
            title.setText(results_banner.get(position).getTitle());
            content.setText(results_banner.get(position).getContent());

            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MyAnnouncementDialog(m_activity, results_banner.get(position));

                }
            });

            container.addView(vi);

            return vi;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView((LinearLayout) obj);
        }

        @Override
        public int getCount() {
            return results_banner.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class Connection4 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));
            String xml = "";
            if (params[0].contentEquals("PRESENTFORABOUTUSVIEW"))
                xml = APP.post1(nameValuePairs, APP.path + "/get_about_us.php");
            else if (params[0].contentEquals("PRESENTFORFAQVIEW"))
                xml = APP.post1(nameValuePairs, APP.path + "/get_faq.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        aboutPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        aboutPart2 = APP.base64Decode(APP.getElement(parse, "part2"));

                    }
                    if (!aboutPart1.contentEquals("")) {
                        return "true";
                    } else {
                        return "false";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {
                new MyContractDialog(m_activity, aboutPart2, aboutPart1);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private class Connection5 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode("0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode("1")));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_contract_data.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        contPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        contPart2 = APP.base64Decode(APP.getElement(parse, "part2"));

                    }
                    if (!contPart1.contentEquals("")) {
                        return "true";
                    } else {
                        return "false";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {
                new MyContractDialog(m_activity, contPart1, getString(R.string.s_contract));


                //activity
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}
